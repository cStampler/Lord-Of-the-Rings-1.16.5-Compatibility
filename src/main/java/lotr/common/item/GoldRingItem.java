package lotr.common.item;

import lotr.common.data.LOTRLevelData;
import lotr.common.entity.item.RingPortalEntity;
import lotr.common.init.*;
import lotr.common.stat.LOTRStats;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class GoldRingItem extends Item {
	public GoldRingItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity ring) {
		World world = ring.level;
		if (!world.isClientSide) {
			Entity thrower = ((ServerWorld) world).getEntity(ring.getThrower());
			if (ring.isOnFire()) {
				LOTRLevelData levelData = LOTRLevelData.serverInstance();
				if (LOTRDimensions.isDimension(world, World.OVERWORLD) && !levelData.madePortal()) {
					BlockPos portalPos = ring.blockPosition();
					levelData.setMadePortal(true);
					levelData.markOverworldPortalLocation(portalPos);
					BlockPos abovePos = portalPos.above(3);
					ring.remove();
					world.explode(thrower, abovePos.getX(), abovePos.getY(), abovePos.getZ(), 3.0F, Mode.DESTROY);
					RingPortalEntity portal = (RingPortalEntity) ((EntityType) LOTREntities.RING_PORTAL.get()).create(world);
					portal.moveTo(abovePos, 0.0F, 0.0F);
					world.addFreshEntity(portal);
					if (thrower instanceof PlayerEntity) {
						((PlayerEntity) thrower).awardStat(LOTRStats.RING_INTO_FIRE);
					}
				}
			}
		}

		return false;
	}
}
