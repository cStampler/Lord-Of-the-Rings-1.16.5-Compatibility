package lotr.common.entity.item;

import java.util.Optional;

import lotr.common.block.TreasurePileBlock;
import lotr.common.init.*;
import net.minecraft.block.*;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.fml.network.NetworkHooks;

public class FallingTreasureBlockEntity extends Entity {
	private static final DataParameter ORIGIN;
	private static final DataParameter FALL_TILE;
	static {
		ORIGIN = EntityDataManager.defineId(FallingTreasureBlockEntity.class, DataSerializers.BLOCK_POS);
		FALL_TILE = EntityDataManager.defineId(FallingTreasureBlockEntity.class, DataSerializers.BLOCK_STATE);
	}
	private int fallTime;
	private boolean shouldDropItem;

	private boolean dontSetBlock;

	public FallingTreasureBlockEntity(EntityType type, World world) {
		super(type, world);
		shouldDropItem = true;
	}

	public FallingTreasureBlockEntity(World world, double x, double y, double z, BlockState fallingBlockState) {
		this((EntityType) LOTREntities.FALLING_TREASURE_BLOCK.get(), world);
		blocksBuilding = true;
		setPos(x, y + (1.0F - getBbHeight()) / 2.0F, z);
		this.setDeltaMovement(Vector3d.ZERO);
		xo = x;
		yo = y;
		zo = z;
		setOrigin(blockPosition());
		setFallTile(fallingBlockState);
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt) {
		nbt.put("BlockState", NBTUtil.writeBlockState(getFallTile()));
		nbt.putInt("Time", fallTime);
		nbt.putBoolean("DropItem", shouldDropItem);
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(ORIGIN, BlockPos.ZERO);
		entityData.define(FALL_TILE, Optional.empty());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean displayFireAnimation() {
		return false;
	}

	private void dropTreasureItems() {
		Block.dropResources(getFallTile(), level, blockPosition());
	}

	@Override
	public void fillCrashReportCategory(CrashReportCategory category) {
		super.fillCrashReportCategory(category);
		category.setDetail("Immitating BlockState", getFallTile().toString());
	}

	@Override
	public IPacket getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public BlockState getFallTile() {
		return (BlockState) ((Optional) entityData.get(FALL_TILE)).orElse(Blocks.AIR.defaultBlockState());
	}

	public BlockPos getOrigin() {
		return (BlockPos) entityData.get(ORIGIN);
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	protected boolean isMovementNoisy() {
		return false;
	}

	@Override
	public boolean isPickable() {
		return isAlive();
	}

	private boolean isValidFallTile() {
		BlockState fallTile = getFallTile();
		return !fallTile.isAir() && fallTile.getBlock() instanceof TreasurePileBlock;
	}

	@Override
	public boolean onlyOpCanSetNbt() {
		return true;
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT nbt) {
		setFallTile(NBTUtil.readBlockState(nbt.getCompound("BlockState")));
		fallTime = nbt.getInt("Time");
		if (nbt.contains("DropItem", 99)) {
			shouldDropItem = nbt.getBoolean("DropItem");
		}

		if (!isValidFallTile()) {
			setFallTile(((Block) LOTRBlocks.GOLD_TREASURE_PILE.get()).defaultBlockState());
		}

	}

	public void setFallTile(BlockState state) {
		entityData.set(FALL_TILE, Optional.of(state));
	}

	public void setOrigin(BlockPos pos) {
		entityData.set(ORIGIN, pos);
	}

	@Override
	public void tick() {
		if (!isValidFallTile()) {
			this.remove();
		} else {
			BlockState fallTile = getFallTile();
			Block block = fallTile.getBlock();
			BlockPos thisPos;
			if (fallTime++ == 0) {
				thisPos = blockPosition();
				if (level.getBlockState(thisPos).getBlock() == block) {
					level.removeBlock(thisPos, false);
				} else if (!level.isClientSide) {
					this.remove();
					return;
				}
			}

			if (!isNoGravity()) {
				this.setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
			}

			move(MoverType.SELF, getDeltaMovement());
			if (!level.isClientSide) {
				thisPos = blockPosition();
				boolean flag1 = false;
				if (!onGround && !flag1) {
					if (!level.isClientSide && (fallTime > 100 && (thisPos.getY() < 1 || thisPos.getY() > 256) || fallTime > 600)) {
						if (shouldDropItem && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
							dropTreasureItems();
						}

						this.remove();
					}
				} else {
					BlockState stateAtPos = level.getBlockState(thisPos);
					this.setDeltaMovement(getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
					if (stateAtPos.getBlock() != Blocks.MOVING_PISTON) {
						this.remove();
						if (dontSetBlock) {
							if (block instanceof FallingBlock) {
							}
						} else {
							SoundType treasureSoundType = fallTile.getSoundType(level, thisPos, this);
							boolean placedAnyTreasure = false;
							boolean placedAllTreasure = false;
							if (stateAtPos.getBlock() == fallTile.getBlock()) {
								int belowPileLevel = stateAtPos.getValue(TreasurePileBlock.PILE_LEVEL);
								if (belowPileLevel < 8) {
									int fallingPileLevel;
									for (fallingPileLevel = fallTile.getValue(TreasurePileBlock.PILE_LEVEL); fallingPileLevel > 0 && belowPileLevel < 8; ++belowPileLevel) {
										--fallingPileLevel;
									}

									level.setBlock(thisPos, stateAtPos.setValue(TreasurePileBlock.PILE_LEVEL, belowPileLevel), 3);
									thisPos = thisPos.above();
									stateAtPos = level.getBlockState(thisPos);
									placedAnyTreasure = true;
									if (fallingPileLevel <= 0) {
										placedAllTreasure = true;
										fallTile = Blocks.AIR.defaultBlockState();
									} else {
										fallTile = fallTile.setValue(TreasurePileBlock.PILE_LEVEL, fallingPileLevel);
									}

									setFallTile(fallTile);
								}
							}

							if (!placedAllTreasure) {
								boolean replaceable = stateAtPos.canBeReplaced(new DirectionalPlaceContext(level, thisPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
								boolean canFallThrough = FallingBlock.isFree(level.getBlockState(thisPos.below()));
								boolean placeAt = fallTile.canSurvive(level, thisPos) && !canFallThrough;
								if (replaceable && placeAt) {
									if (fallTile.hasProperty(BlockStateProperties.WATERLOGGED) && level.getFluidState(thisPos).getType() == Fluids.WATER) {
										fallTile = fallTile.setValue(BlockStateProperties.WATERLOGGED, true);
									}

									if (level.setBlock(thisPos, fallTile, 3)) {
										if (block instanceof TreasurePileBlock) {
											((TreasurePileBlock) block).onEndFallingTreasure(level, thisPos, fallTile, stateAtPos);
										}

										placedAnyTreasure = true;
									} else if (shouldDropItem && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
										dropTreasureItems();
									}
								} else if (shouldDropItem && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
									dropTreasureItems();
								}
							}

							if (placedAnyTreasure) {
								level.playSound((PlayerEntity) null, thisPos, treasureSoundType.getPlaceSound(), SoundCategory.BLOCKS, (treasureSoundType.getVolume() + 1.0F) / 2.0F, treasureSoundType.getPitch() * 0.8F);
							}
						}
					}
				}
			}

			this.setDeltaMovement(getDeltaMovement().scale(0.98D));
		}

	}
}
