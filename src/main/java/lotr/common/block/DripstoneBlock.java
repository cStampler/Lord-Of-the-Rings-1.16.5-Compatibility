package lotr.common.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import lotr.common.init.LOTRDamageSources;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DripstoneBlock extends Block implements IWaterLoggable {
	public static final Map BLOCK_TO_DRIPSTONE = new HashMap();
	public static final EnumProperty DRIPSTONE_TYPE;
	public static final BooleanProperty WATERLOGGED;
	private static final VoxelShape STALACTITE_SHAPE;
	private static final VoxelShape STALAGMITE_SHAPE;
	private static final VoxelShape STALACTITE_DOUBLE_BASE_SHAPE;
	private static final VoxelShape STALACTITE_DOUBLE_POINT_SHAPE;
	private static final VoxelShape STALAGMITE_DOUBLE_BASE_SHAPE;
	private static final VoxelShape STALAGMITE_DOUBLE_POINT_SHAPE;
	static {
		DRIPSTONE_TYPE = LOTRBlockStates.DRIPSTONE_TYPE;
		WATERLOGGED = BlockStateProperties.WATERLOGGED;
		STALACTITE_SHAPE = VoxelShapes.or(Block.box(4.5D, 12.0D, 4.5D, 11.5D, 16.0D, 11.5D), Block.box(5.5D, 8.0D, 5.5D, 10.5D, 12.0D, 10.5D), Block.box(6.5D, 4.0D, 6.5D, 9.5D, 8.0D, 9.5D), Block.box(7.5D, 0.0D, 7.5D, 8.5D, 4.0D, 8.5D));
		STALAGMITE_SHAPE = VoxelShapes.or(Block.box(4.5D, 0.0D, 4.5D, 11.5D, 4.0D, 11.5D), Block.box(5.5D, 4.0D, 5.5D, 10.5D, 8.0D, 10.5D), Block.box(6.5D, 8.0D, 6.5D, 9.5D, 12.0D, 9.5D), Block.box(7.5D, 12.0D, 7.5D, 8.5D, 16.0D, 8.5D));
		STALACTITE_DOUBLE_BASE_SHAPE = VoxelShapes.or(Block.box(4.0D, 8.0D, 4.0D, 12.0D, 16.0D, 12.0D), Block.box(5.0D, 0.0D, 5.0D, 11.0D, 8.0D, 11.0D));
		STALACTITE_DOUBLE_POINT_SHAPE = VoxelShapes.or(Block.box(6.0D, 8.0D, 6.0D, 10.0D, 16.0D, 10.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D));
		STALAGMITE_DOUBLE_BASE_SHAPE = VoxelShapes.or(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D), Block.box(5.0D, 8.0D, 5.0D, 11.0D, 16.0D, 11.0D));
		STALAGMITE_DOUBLE_POINT_SHAPE = VoxelShapes.or(Block.box(6.0D, 0.0D, 6.0D, 10.0D, 8.0D, 10.0D), Block.box(7.0D, 8.0D, 7.0D, 9.0D, 16.0D, 9.0D));
	}
	private final Block modelBlock;
	private final IParticleData particleType;

	public final boolean isWaterloggable;

	public DripstoneBlock(Block block) {
		this(block, true);
	}

	public DripstoneBlock(Block block, boolean waterlog) {
		this(block, ParticleTypes.DRIPPING_WATER, waterlog);
	}

	public DripstoneBlock(Block block, IParticleData particle) {
		this(block, particle, true);
	}

	public DripstoneBlock(Block block, IParticleData particle, boolean waterlog) {
		super(Properties.copy(block).noOcclusion());
		modelBlock = block;
		particleType = particle;
		isWaterloggable = waterlog;
		registerDefaultState(defaultBlockState().setValue(DRIPSTONE_TYPE, DripstoneBlock.Type.STALACTITE).setValue(WATERLOGGED, false));
		BLOCK_TO_DRIPSTONE.put(block, this);
	}

	public DripstoneBlock(Supplier blockSup) {
		this((Block) blockSup.get());
	}

	public DripstoneBlock(Supplier blockSup, IParticleData particle) {
		this((Block) blockSup.get(), particle);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		modelBlock.animateTick(state, world, pos, rand);
		DripstoneBlock.Type type = (DripstoneBlock.Type) state.getValue(DRIPSTONE_TYPE);
		if ((type == DripstoneBlock.Type.STALACTITE || type == DripstoneBlock.Type.STALACTITE_DOUBLE_POINT) && rand.nextInt(50) == 0) {
			BlockPos abovePos = pos.above();
			BlockState above = world.getBlockState(abovePos);
			if (above.isSolidRender(world, abovePos) && above.getMaterial() == Material.STONE) {
				BlockPos belowPos = pos.below();
				BlockState below = world.getBlockState(belowPos);
				if (!below.canOcclude() || !below.isFaceSturdy(world, belowPos, Direction.UP)) {
					world.addParticle(particleType, pos.getX() + 0.5D, pos.getY() - 0.05D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
				}
			}
		}

	}

	@Override
	public boolean canBeReplaced(BlockState state, Fluid fluid) {
		return !isWaterloggable ? true : super.canBeReplaced(state, fluid);
	}

	@Override
	@Deprecated
	public boolean canPlaceLiquid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
		return false;//return isWaterloggable ? canPlaceLiquid(world, pos, state, fluid) : false;
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		boolean isPresent = world.getBlockState(pos).getBlock() == this;
		DripstoneBlock.Type type = (DripstoneBlock.Type) state.getValue(DRIPSTONE_TYPE);
		if (type == DripstoneBlock.Type.STALACTITE) {
			return checkSolidSide(world, pos.above(), Direction.DOWN);
		}
		if (type == DripstoneBlock.Type.STALAGMITE) {
			return checkSolidSide(world, pos.below(), Direction.UP);
		}
		if (type == DripstoneBlock.Type.STALACTITE_DOUBLE_BASE) {
			return checkSolidSide(world, pos.above(), Direction.DOWN) && matchType(world, pos.below(), DripstoneBlock.Type.STALACTITE_DOUBLE_POINT);
		}
		if (type == DripstoneBlock.Type.STALACTITE_DOUBLE_POINT) {
			return isPresent ? matchType(world, pos.above(), DripstoneBlock.Type.STALACTITE_DOUBLE_BASE) : matchType(world, pos.above(), DripstoneBlock.Type.STALACTITE);
		}
		if (type == DripstoneBlock.Type.STALAGMITE_DOUBLE_BASE) {
			return checkSolidSide(world, pos.below(), Direction.UP) && matchType(world, pos.above(), DripstoneBlock.Type.STALAGMITE_DOUBLE_POINT);
		}
		if (type == DripstoneBlock.Type.STALAGMITE_DOUBLE_POINT) {
			return isPresent ? matchType(world, pos.below(), DripstoneBlock.Type.STALAGMITE_DOUBLE_BASE) : matchType(world, pos.below(), DripstoneBlock.Type.STALAGMITE);
		}
		return true;
	}

	private boolean checkSolidSide(IWorldReader world, BlockPos pos, Direction dir) {
		BlockState state = world.getBlockState(pos);
		return LOTRUtil.hasSolidSide(world, pos, dir) || dir.getAxis().isVertical() && state.getBlock() instanceof WallBlock;
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(DRIPSTONE_TYPE, WATERLOGGED);
	}

	@Override
	public void fallOn(World world, BlockPos pos, Entity entity, float fallDistance) {
		if (entity instanceof LivingEntity) {
			DripstoneBlock.Type type = (DripstoneBlock.Type) world.getBlockState(pos).getValue(DRIPSTONE_TYPE);
			if (type == DripstoneBlock.Type.STALAGMITE || type == DripstoneBlock.Type.STALAGMITE_DOUBLE_POINT) {
				float damage = fallDistance * 2.0F + 1.0F;
				entity.hurt(LOTRDamageSources.STALAGMITE, damage);
			}
		}

	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return isWaterloggable && state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		switch ((DripstoneBlock.Type) state.getValue(DRIPSTONE_TYPE)) {
		case STALACTITE:
			return STALACTITE_SHAPE;
		case STALAGMITE:
		default:
			return STALAGMITE_SHAPE;
		case STALACTITE_DOUBLE_BASE:
			return STALACTITE_DOUBLE_BASE_SHAPE;
		case STALACTITE_DOUBLE_POINT:
			return STALACTITE_DOUBLE_POINT_SHAPE;
		case STALAGMITE_DOUBLE_BASE:
			return STALAGMITE_DOUBLE_BASE_SHAPE;
		case STALAGMITE_DOUBLE_POINT:
			return STALAGMITE_DOUBLE_POINT_SHAPE;
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		context.getLevel().getBlockState(pos);
		BlockState placeState = defaultBlockState();
		if (isWaterloggable) {
			FluidState fluid = context.getLevel().getFluidState(pos);
			placeState = placeState.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
		}

		Direction hitFace = context.getClickedFace();
		boolean pointingDown = hitFace != Direction.DOWN && (hitFace == Direction.UP || context.getClickLocation().y - pos.getY() <= 0.5D);
		BlockState stalagmiteDouble = placeState.setValue(DRIPSTONE_TYPE, DripstoneBlock.Type.STALAGMITE_DOUBLE_POINT);
		BlockState stalactiteDouble = placeState.setValue(DRIPSTONE_TYPE, DripstoneBlock.Type.STALACTITE_DOUBLE_POINT);
		boolean stalagmiteDoubleValid = stalagmiteDouble.canSurvive(world, pos);
		boolean stalactiteDoubleValid = stalactiteDouble.canSurvive(world, pos);
		if (stalagmiteDoubleValid && stalactiteDoubleValid) {
			return pointingDown ? stalagmiteDouble : stalactiteDouble;
		}
		if (stalagmiteDoubleValid && pointingDown) {
			return stalagmiteDouble;
		}
		if (stalactiteDoubleValid && !pointingDown) {
			return stalactiteDouble;
		}
		BlockState stalagmite = placeState.setValue(DRIPSTONE_TYPE, DripstoneBlock.Type.STALAGMITE);
		BlockState stalactite = placeState.setValue(DRIPSTONE_TYPE, DripstoneBlock.Type.STALACTITE);
		boolean stalagmiteValid = stalagmite.canSurvive(world, pos);
		boolean stalactiteValid = stalactite.canSurvive(world, pos);
		if (stalagmiteValid && stalactiteValid) {
			return pointingDown ? stalagmite : stalactite;
		}
		if (stalagmiteValid) {
			return stalagmite;
		}
		return stalactiteValid ? stalactite : null;
	}

	private boolean matchType(IWorldReader world, BlockPos pos, DripstoneBlock.Type type) {
		BlockState state = world.getBlockState(pos);
		return state.getBlock() == this && state.getValue(DRIPSTONE_TYPE) == type;
	}

	@Override
	public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluid) {
		return isWaterloggable ? placeLiquid(world, pos, state, fluid) : false;
	}

	@Override
	public Fluid takeLiquid(IWorld world, BlockPos pos, BlockState state) {
		return isWaterloggable ? takeLiquid(world, pos, state) : Fluids.EMPTY;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (isWaterloggable && state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		boolean check = false;
		DripstoneBlock.Type type = (DripstoneBlock.Type) state.getValue(DRIPSTONE_TYPE);
		if (type == DripstoneBlock.Type.STALACTITE && facing == Direction.UP || type == DripstoneBlock.Type.STALAGMITE && facing == Direction.DOWN) {
			check = true;
		} else if ((type == DripstoneBlock.Type.STALACTITE_DOUBLE_BASE || type == DripstoneBlock.Type.STALACTITE_DOUBLE_POINT || type == DripstoneBlock.Type.STALAGMITE_DOUBLE_BASE || type == DripstoneBlock.Type.STALAGMITE_DOUBLE_POINT) && (facing == Direction.DOWN || facing == Direction.UP)) {
			check = true;
		}

		if (check && !state.canSurvive(world, currentPos)) {
			BlockState singleState;
			if (type == DripstoneBlock.Type.STALACTITE_DOUBLE_BASE) {
				singleState = state.setValue(DRIPSTONE_TYPE, DripstoneBlock.Type.STALACTITE);
				if (singleState.canSurvive(world, currentPos)) {
					return singleState;
				}
			} else if (type == DripstoneBlock.Type.STALAGMITE_DOUBLE_BASE) {
				singleState = state.setValue(DRIPSTONE_TYPE, DripstoneBlock.Type.STALAGMITE);
				if (singleState.canSurvive(world, currentPos)) {
					return singleState;
				}
			}

			return Blocks.AIR.defaultBlockState();
		}
		if (type == DripstoneBlock.Type.STALAGMITE && facing == Direction.UP && matchType(world, currentPos.above(), DripstoneBlock.Type.STALAGMITE_DOUBLE_POINT)) {
			return state.setValue(DRIPSTONE_TYPE, DripstoneBlock.Type.STALAGMITE_DOUBLE_BASE);
		}
		return type == DripstoneBlock.Type.STALACTITE && facing == Direction.DOWN && matchType(world, currentPos.below(), DripstoneBlock.Type.STALACTITE_DOUBLE_POINT) ? (BlockState) state.setValue(DRIPSTONE_TYPE, DripstoneBlock.Type.STALACTITE_DOUBLE_BASE) : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		ItemStack heldItem = player.getMainHandItem();
		if (heldItem.getItem() == asItem()) {
			BlockItemUseContext useContext = new BlockItemUseContext(new ItemUseContext(player, hand, trace));
			DripstoneBlock.Type type = (DripstoneBlock.Type) state.getValue(DRIPSTONE_TYPE);
			BlockPos placePos = null;
			BlockState placeState = null;
			if (type == DripstoneBlock.Type.STALACTITE) {
				placePos = pos.below();
				placeState = defaultBlockState().setValue(DRIPSTONE_TYPE, DripstoneBlock.Type.STALACTITE_DOUBLE_POINT);
			} else if (type == DripstoneBlock.Type.STALAGMITE) {
				placePos = pos.above();
				placeState = defaultBlockState().setValue(DRIPSTONE_TYPE, DripstoneBlock.Type.STALAGMITE_DOUBLE_POINT);
			}

			if (placePos != null && placeState != null) {
				boolean canDouble = world.getBlockState(placePos).canBeReplaced(useContext);
				if (canDouble) {
					BlockState placeStateFull = placeState;
					if (isWaterloggable) {
						placeStateFull = placeState.setValue(WATERLOGGED, world.getFluidState(placePos).getType() == Fluids.WATER);
					}

					world.setBlock(placePos, placeStateFull, 3);
					SoundType sound = this.getSoundType(placeStateFull, world, placePos, player);
					world.playSound(player, placePos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
					if (!player.abilities.instabuild) {
						heldItem.shrink(1);
					}

					return ActionResultType.SUCCESS;
				}
			}
		}

		return super.use(state, world, pos, player, hand, trace);
	}

	public enum Type implements IStringSerializable {
		STALACTITE("stalactite"), STALAGMITE("stalagmite"), STALACTITE_DOUBLE_BASE("stalactite_double_base"), STALACTITE_DOUBLE_POINT("stalactite_double_point"), STALAGMITE_DOUBLE_BASE("stalagmite_double_base"), STALAGMITE_DOUBLE_POINT("stalagmite_double_point");

		private final String typeName;

		Type(String s) {
			typeName = s;
		}

		@Override
		public String getSerializedName() {
			return typeName;
		}
	}
}
