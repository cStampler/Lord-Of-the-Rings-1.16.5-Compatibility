package lotr.common.entity.npc;

import lotr.common.init.LOTRItems;
import lotr.common.init.LOTRMaterial;
import lotr.common.item.ManFleshItem;
import lotr.common.util.GameRuleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.World;

public abstract class AbstractMannishEntity extends NPCEntity {
	public AbstractMannishEntity(EntityType type, World w) {
		super(type, w);
		spawnRequiresSurfaceBlock = true;
	}

	@Override
	public void die(DamageSource damagesource) {
		super.die(damagesource);
		if (!level.isClientSide && GameRuleUtil.canDropLoot(level) && random.nextInt(5) == 0) {
			Entity killer = damagesource.getEntity();
			if (killer instanceof LivingEntity) {
				LivingEntity livingKiller = (LivingEntity) killer;
				if (ManFleshItem.isManFleshAligned(livingKiller)) {
					ItemStack itemstack = livingKiller.getMainHandItem();
					if (!itemstack.isEmpty()) {
						Item item = itemstack.getItem();
						IItemTier material = null;
						if (item instanceof TieredItem) {
							material = ((TieredItem) item).getTier();
						}

						if (material != null) {
							boolean canHarvest = (Boolean) LOTRMaterial.ifLOTRToolMaterial(material).map(hummel -> ((LOTRMaterial.AsTool) hummel).canHarvestManFlesh()).orElse(false);
							if (canHarvest) {
								ItemStack flesh = new ItemStack((IItemProvider) LOTRItems.MAN_FLESH.get(), 1 + random.nextInt(2));
								this.spawnAtLocation(flesh, 0.0F);
							}
						}
					}
				}
			}
		}

	}

	public static MutableAttribute regAttrs() {
		return NPCEntity.registerBaseNPCAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.2D);
	}
}
