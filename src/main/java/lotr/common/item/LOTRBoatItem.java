package lotr.common.item;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import lotr.common.dispenser.DispenseLOTRBoat;
import lotr.common.entity.item.LOTRBoatEntity;
import lotr.common.init.LOTRItemGroups;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class LOTRBoatItem extends Item {
	private static final Predicate entitySelector;
	static {
		entitySelector = EntityPredicates.NO_SPECTATORS.and(Entity::isPickable);
	}

	private final LOTRBoatEntity.ModBoatType boatType;

	public LOTRBoatItem(LOTRBoatEntity.ModBoatType type) {
		super(new Properties().stacksTo(1).tab(LOTRItemGroups.MISC));
		boatType = type;
		DispenserBlock.registerBehavior(this, new DispenseLOTRBoat(boatType));
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		ItemStack heldItem = player.getItemInHand(hand);
		RayTraceResult target = getPlayerPOVHitResult(world, player, FluidMode.ANY);
		if (target.getType() == Type.MISS) {
			return ActionResult.pass(heldItem);
		}
		Vector3d look = player.getViewVector(1.0F);
		double reach = 5.0D;
		List collidedEntities = world.getEntities(player, player.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0D), entitySelector);
		if (!collidedEntities.isEmpty()) {
			Vector3d eyePos = player.getEyePosition(1.0F);
			Iterator var11 = collidedEntities.iterator();

			while (var11.hasNext()) {
				Entity entity = (Entity) var11.next();
				AxisAlignedBB bb = entity.getBoundingBox().inflate(entity.getPickRadius());
				if (bb.contains(eyePos)) {
					return ActionResult.pass(heldItem);
				}
			}
		}

		if (target.getType() != Type.BLOCK) {
			return ActionResult.pass(heldItem);
		}
		LOTRBoatEntity boat = new LOTRBoatEntity(world, target.getLocation().x, target.getLocation().y, target.getLocation().z);
		boat.setModBoatType(boatType);
		boat.yRot = player.yRot;
		if (!world.noCollision(boat, boat.getBoundingBox().inflate(-0.1D))) {
			return ActionResult.fail(heldItem);
		}
		if (!world.isClientSide) {
			world.addFreshEntity(boat);
			if (!player.abilities.instabuild) {
				heldItem.shrink(1);
			}
		}

		player.awardStat(Stats.ITEM_USED.get(this));
		return ActionResult.success(heldItem);
	}
}
