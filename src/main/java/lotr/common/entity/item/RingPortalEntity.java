package lotr.common.entity.item;

import java.util.List;

import lotr.common.LOTRMod;
import lotr.common.init.LOTRDimensions;
import lotr.common.init.LOTREntities;
import lotr.common.init.LOTRItems;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class RingPortalEntity extends Entity {
	private static final DataParameter PORTAL_AGE;
	public static final int MAX_PORTAL_AGE = 120;
	static {
		PORTAL_AGE = EntityDataManager.defineId(RingPortalEntity.class, DataSerializers.INT);
	}
	private float prevPortalRotation;
	private float portalRotation;
	private float portalSpinSpeed;
	private int recentUseTick;
	private int clientPortalAge;

	private int clientPrevPortalAge;

	public RingPortalEntity(EntityType type, World w) {
		super(type, w);
		portalSpinSpeed = 4.0F;
		setInvulnerable(true);
	}

	public RingPortalEntity(World w) {
		this((EntityType) LOTREntities.RING_PORTAL.get(), w);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		compound.putInt("PortalAge", getPortalAge());
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(PORTAL_AGE, 0);
	}

	@Override
	public IPacket getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack((IItemProvider) LOTRItems.GOLD_RING.get());
	}

	public int getPortalAge() {
		return (Integer) entityData.get(PORTAL_AGE);
	}

	public float getPortalRotation(float f) {
		return prevPortalRotation + (portalRotation - prevPortalRotation) * f;
	}

	public float getPortalScale(float f) {
		return (clientPrevPortalAge + (clientPortalAge - clientPrevPortalAge) * f) / 120.0F;
	}

	private float getRecentUseProportion() {
		return recentUseTick / 120.0F;
	}

	public float getScriptBrightness(float f) {
		float freq = MathHelper.lerp(getRecentUseProportion(), 0.01F, 0.1F);
		return 0.75F + MathHelper.cos((tickCount + f) * freq) * 0.25F;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleEntityEvent(byte b) {
		if (b == 16) {
			ItemStack breakingItem = new ItemStack((IItemProvider) LOTRItems.GOLD_RING.get());

			for (int l = 0; l < 16; ++l) {
				level.addParticle(new ItemParticleData(ParticleTypes.ITEM, breakingItem), this.getX() + (random.nextDouble() - 0.5D) * getBbWidth(), this.getY() + random.nextDouble() * getBbHeight(), this.getZ() + (random.nextDouble() - 0.5D) * getBbWidth(), 0.0D, 0.0D, 0.0D);
			}
		} else if (b == 17) {
			recentUseTick = 120;
		} else {
			super.handleEntityEvent(b);
		}

	}

	@Override
	protected void handleNetherPortal() {
	}

	@Override
	public boolean hurt(DamageSource source, float f) {
		Entity entity = source.getEntity();
		if (!(entity instanceof PlayerEntity) || entity != source.getDirectEntity() || !((PlayerEntity) entity).abilities.instabuild) {
			return false;
		}
		if (!level.isClientSide) {
			SoundType sound = SoundType.GLASS;
			playSound(sound.getBreakSound(), (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
			level.broadcastEntityEvent(this, (byte) 16);
			this.remove();
		}

		return true;
	}

	@Override
	public boolean isIgnoringBlockTriggers() {
		return true;
	}

	@Override
	protected boolean isMovementNoisy() {
		return false;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	public void onTransferEntity() {
		level.broadcastEntityEvent(this, (byte) 17);
	}

	@Override
	public void push(Entity entity) {
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		setPortalAge(compound.getInt("PortalAge"));
	}

	private void setPortalAge(int i) {
		entityData.set(PORTAL_AGE, i);
	}

	@Override
	public boolean skipAttackInteraction(Entity entity) {
		return entity instanceof PlayerEntity ? hurt(DamageSource.playerAttack((PlayerEntity) entity), 0.0F) : false;
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide && !LOTRDimensions.isDimension(level, World.OVERWORLD) && !LOTRDimensions.isDimension(level, LOTRDimensions.MIDDLE_EARTH_WORLD_KEY)) {
			this.remove();
		}

		if (!level.isClientSide) {
			if (getPortalAge() < 120) {
				setPortalAge(getPortalAge() + 1);
			}
		} else {
			if (recentUseTick > 0) {
				portalSpinSpeed = MathHelper.lerp(getRecentUseProportion(), 4.0F, 12.0F);
				--recentUseTick;
			} else {
				portalSpinSpeed = 4.0F;
			}

			prevPortalRotation = portalRotation;

			for (portalRotation += portalSpinSpeed; portalRotation - prevPortalRotation < -180.0F; prevPortalRotation -= 360.0F) {
			}

			while (portalRotation - prevPortalRotation >= 180.0F) {
				prevPortalRotation += 360.0F;
			}

			clientPrevPortalAge = clientPortalAge;
			clientPortalAge = getPortalAge();
			if (random.nextFloat() < getPortalScale(1.0F)) {
				for (int i = 0; i < 1; ++i) {
					float w = getBbWidth();
					float h = getBbHeight();
					double x = this.getX();
					double y = this.getY() + h / 2.0F;
					double z = this.getZ();
					double d = x + MathHelper.nextFloat(random, -w, w);
					double d1 = y + MathHelper.nextFloat(random, -h * 0.5F, h * 0.5F);
					double d2 = z + MathHelper.nextFloat(random, -w, w);
					double d3 = (x - d) / 8.0D;
					double d4 = (y - d1) / 8.0D;
					double d5 = (z - d2) / 8.0D;
					double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
					double d7 = 1.0D - d6;
					double d8 = 0.0D;
					double d9 = 0.0D;
					double d10 = 0.0D;
					if (d7 > 0.0D) {
						d7 *= d7;
						d8 += d3 / d6 * d7 * 0.2D;
						d9 += d4 / d6 * d7 * 0.2D;
						d10 += d5 / d6 * d7 * 0.2D;
					}

					level.addParticle(ParticleTypes.FLAME, d, d1, d2, d8, d9, d10);
				}
			}
		}

		if (getPortalAge() >= 120) {
			double searchRange = 8.0D;
			List entities = level.getEntitiesOfClass(Entity.class, getBoundingBox().expandTowards(searchRange, searchRange, searchRange), entity -> {
				if (entity != this && !(entity instanceof RingPortalEntity)) {
					return entity.getBoundingBox().intersects(getBoundingBox()) && (entity instanceof PlayerEntity || !entity.isOnPortalCooldown());
				}
				return false;
			});
			if (!entities.isEmpty()) {
				entities.forEach(e -> {
					LOTRMod.PROXY.setInRingPortal((Entity) e, this);
				});
			}

			if (random.nextInt(50) == 0) {
				playSound(SoundEvents.PORTAL_AMBIENT, 0.5F, random.nextFloat() * 0.4F + 0.8F);
			}
		}

	}
}
