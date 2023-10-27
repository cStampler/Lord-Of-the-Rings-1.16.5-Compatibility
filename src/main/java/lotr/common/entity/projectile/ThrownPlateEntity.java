package lotr.common.entity.projectile;

import lotr.common.init.LOTRBlocks;
import lotr.common.init.LOTREntities;
import lotr.common.init.LOTRItems;
import lotr.common.item.PlateItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ThrownPlateEntity extends ProjectileItemEntity {
	private static final DataParameter THROWN_RETROGRADE;
	static {
		THROWN_RETROGRADE = EntityDataManager.defineId(ThrownPlateEntity.class, DataSerializers.BOOLEAN);
	}
	private int plateSpin;

	private int prevPlateSpin;

	public ThrownPlateEntity(EntityType type, World w) {
		super(type, w);
	}

	public ThrownPlateEntity(World w, ItemStack stack, double x, double y, double z) {
		super((EntityType) LOTREntities.THROWN_PLATE.get(), x, y, z, w);
		setItem(stack);
	}

	public ThrownPlateEntity(World w, ItemStack stack, LivingEntity e) {
		super((EntityType) LOTREntities.THROWN_PLATE.get(), e, w);
		setItem(stack);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putBoolean("ThrownRetrograde", getThrownRetrograde());
	}

	private boolean breakGlass(BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.getMaterial() == Material.GLASS) {
			boolean bannerProtection = false;
			if (!bannerProtection) {
				level.levelEvent((PlayerEntity) null, 2001, pos, Block.getId(state));
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				return true;
			}
		}

		return false;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(THROWN_RETROGRADE, false);
	}

	@Override
	public IPacket getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected Item getDefaultItem() {
		return (Item) LOTRItems.FINE_PLATE.get();
	}

	@Override
	protected float getGravity() {
		return 0.02F;
	}

	public BlockState getPlateBlockState() {
		ItemStack plateItemStack = getItemRaw();
		if (!plateItemStack.isEmpty()) {
			Item plateItem = plateItemStack.getItem();
			Block block = plateItem instanceof PlateItem ? ((PlateItem) plateItem).getBlock() : Block.byItem(plateItem);
			return block.defaultBlockState();
		}
		return ((Block) LOTRBlocks.FINE_PLATE.get()).defaultBlockState();
	}

	public float getPlateSpin(float f) {
		float spinLerp = prevPlateSpin + (plateSpin - prevPlateSpin) * f;
		int spinTicks = 12;
		float deg = spinLerp % spinTicks / spinTicks * 360.0F;
		if (!getThrownRetrograde()) {
			deg *= -1.0F;
		}

		return deg;
	}

	public boolean getThrownRetrograde() {
		return (Boolean) entityData.get(THROWN_RETROGRADE);
	}

	@Override
	protected void onHit(RayTraceResult target) {
		if (target.getType() == Type.ENTITY) {
			Entity hitEntity = ((EntityRayTraceResult) target).getEntity();
			if (hitEntity == getOwner()) {
				return;
			}

			hitEntity.hurt(DamageSource.thrown(this, getOwner()), 1.0F);
		} else if (target.getType() == Type.BLOCK) {
			BlockPos pos = ((BlockRayTraceResult) target).getBlockPos();
			if (!level.isClientSide && breakGlass(pos)) {
				Mutable movingPos = new Mutable();
				int range = 2;

				for (int x = -range; x <= range; ++x) {
					for (int y = -range; y <= range; ++y) {
						for (int z = -range; z <= range; ++z) {
							if (random.nextInt(4) != 0) {
								movingPos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
								breakGlass(movingPos);
							}
						}
					}
				}
			}
		}

		BlockState plateState = getPlateBlockState();

		for (int i = 0; i < 8; ++i) {
			float range = 0.25F;
			double x = this.getX() + MathHelper.nextFloat(random, -range, range);
			double y = this.getY() + MathHelper.nextFloat(random, -range, range);
			double z = this.getZ() + MathHelper.nextFloat(random, -range, range);
			level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, plateState), x, y, z, 0.0D, 0.0D, 0.0D);
		}

		if (!level.isClientSide) {
			playSound(plateState.getSoundType().getBreakSound(), 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
			this.remove();
		}

	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		setThrownRetrograde(nbt.getBoolean("ThrownRetrograde"));
	}

	public void setThrownRetrograde(boolean flag) {
		entityData.set(THROWN_RETROGRADE, flag);
	}

	@Override
	public void tick() {
		super.tick();
		prevPlateSpin = plateSpin++;
		double velX = getDeltaMovement().x();
		double velY = getDeltaMovement().y();
		double velZ = getDeltaMovement().z();
		float xzSpeed = MathHelper.sqrt(velX * velX + velZ * velZ);
		if (xzSpeed > 0.1F && velY < 0.0D && isInWater()) {
			float factor = MathHelper.nextFloat(random, 0.6F, 0.8F);
			float addY = factor * 0.75F;
			velX *= factor;
			velZ *= factor;
			velY += addY;
			this.setDeltaMovement(velX, velY, velZ);
		}

	}
}
