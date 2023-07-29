package lotr.common.entity.projectile;

import lotr.common.init.LOTREntities;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SmokeRingEntity extends ThrowableEntity {
	private static final DataParameter SMOKE_COLOR;
	private static final DataParameter SMOKE_MAGIC;
	private static final DataParameter SMOKE_AGE;
	private static final DataParameter SMOKE_MAX_AGE;
	private static final DataParameter SMOKE_SCALE;
	static {
		SMOKE_COLOR = EntityDataManager.defineId(SmokeRingEntity.class, DataSerializers.BYTE);
		SMOKE_MAGIC = EntityDataManager.defineId(SmokeRingEntity.class, DataSerializers.BOOLEAN);
		SMOKE_AGE = EntityDataManager.defineId(SmokeRingEntity.class, DataSerializers.INT);
		SMOKE_MAX_AGE = EntityDataManager.defineId(SmokeRingEntity.class, DataSerializers.INT);
		SMOKE_SCALE = EntityDataManager.defineId(SmokeRingEntity.class, DataSerializers.FLOAT);
	}
	private int prevRenderSmokeAge = -1;

	private int renderSmokeAge = -1;

	public SmokeRingEntity(EntityType type, World w) {
		super(type, w);
	}

	public SmokeRingEntity(World w, double x, double y, double z) {
		super((EntityType) LOTREntities.SMOKE_RING.get(), x, y, z, w);
	}

	public SmokeRingEntity(World w, LivingEntity e) {
		super((EntityType) LOTREntities.SMOKE_RING.get(), e, w);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putByte("SmokeColor", (byte) getSmokeColor().getId());
		nbt.putBoolean("SmokeMagic", isMagicSmoke());
		nbt.putInt("SmokeAge", getSmokeAge());
		nbt.putInt("SmokeMaxAge", getSmokeMaxAge());
		nbt.putFloat("SmokeScale", getSmokeScale());
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(SMOKE_COLOR, (byte) DyeColor.WHITE.getId());
		entityData.define(SMOKE_MAGIC, false);
		entityData.define(SMOKE_AGE, 0);
		entityData.define(SMOKE_MAX_AGE, 300);
		entityData.define(SMOKE_SCALE, 1.0F);
	}

	@Override
	public IPacket getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected float getGravity() {
		return 0.0F;
	}

	public float getRenderSmokeAge(float f) {
		float smokeAge = prevRenderSmokeAge + (renderSmokeAge - prevRenderSmokeAge) * f;
		return smokeAge / getSmokeMaxAge();
	}

	private int getSmokeAge() {
		return (Integer) entityData.get(SMOKE_AGE);
	}

	public DyeColor getSmokeColor() {
		return DyeColor.byId((Byte) entityData.get(SMOKE_COLOR));
	}

	private int getSmokeMaxAge() {
		return (Integer) entityData.get(SMOKE_MAX_AGE);
	}

	public float getSmokeScale() {
		return (Float) entityData.get(SMOKE_SCALE);
	}

	public boolean isMagicSmoke() {
		return (Boolean) entityData.get(SMOKE_MAGIC);
	}

	@Override
	protected void onHit(RayTraceResult target) {
		if (target.getType() == Type.ENTITY) {
			Entity hitEntity = ((EntityRayTraceResult) target).getEntity();
			if (hitEntity == getOwner() || hitEntity instanceof SmokeRingEntity) {
				return;
			}
		}

		if (!level.isClientSide) {
			this.remove();
		}

	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		setSmokeColor(DyeColor.byId(nbt.getByte("SmokeColor")));
		setMagicSmoke(nbt.getBoolean("SmokeMagic"));
		setSmokeAge(nbt.getInt("SmokeAge"));
		if (nbt.contains("SmokeMaxAge")) {
			setSmokeMaxAge(nbt.getInt("SmokeMaxAge"));
		}

		if (nbt.contains("SmokeScale")) {
			setSmokeScale(nbt.getFloat("SmokeScale"));
		}

	}

	public void setMagicSmoke(boolean flag) {
		entityData.set(SMOKE_MAGIC, flag);
	}

	private void setSmokeAge(int age) {
		entityData.set(SMOKE_AGE, age);
	}

	public void setSmokeColor(DyeColor color) {
		entityData.set(SMOKE_COLOR, (byte) color.getId());
	}

	private void setSmokeMaxAge(int maxAge) {
		entityData.set(SMOKE_MAX_AGE, maxAge);
	}

	public void setSmokeScale(float scale) {
		entityData.set(SMOKE_SCALE, scale);
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide) {
			int smokeAge = getSmokeAge();
			int maxAge = getSmokeMaxAge();
			if (smokeAge >= maxAge) {
				this.remove();
			} else {
				if (isMagicSmoke()) {
					int spawnInterval = 20;
					int div = smokeAge / spawnInterval;
					if (smokeAge % spawnInterval == 0 && div > 0 && div <= 5) {
						SmokeRingEntity trailingRing = new SmokeRingEntity(level, this.getX(), this.getY(), this.getZ());
						trailingRing.setOwner(getOwner());
						double slow = 0.5D;
						trailingRing.setDeltaMovement(getDeltaMovement().multiply(slow, slow, slow));
						trailingRing.setSmokeColor(getSmokeColor());
						trailingRing.setSmokeScale(getSmokeScale() * 0.35F);
						trailingRing.setSmokeMaxAge(maxAge / 2);
						level.addFreshEntity(trailingRing);
					}
				}

				setSmokeAge(smokeAge + 1);
			}

			if (isInWater()) {
				this.remove();
			}
		} else {
			if (renderSmokeAge == -1) {
				prevRenderSmokeAge = renderSmokeAge = getSmokeAge();
			} else {
				prevRenderSmokeAge = renderSmokeAge;
			}

			renderSmokeAge = getSmokeAge();
		}

	}
}
