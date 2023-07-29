package lotr.common.entity.npc.ai;

import java.util.function.Function;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.entity.npc.inv.NPCItemsInventory;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class StandardAttackModeUpdaters {
	private static void helperSetMainhandItem(NPCEntity npc, Function itemFetcher) {
		npc.setItemInHand(Hand.MAIN_HAND, (ItemStack) itemFetcher.apply(npc.getNPCItemsInv()));
	}

	public static final AttackModeUpdater meleeOnly() {
		return (npc, mode, mounted) -> {
			helperSetMainhandItem(npc, mode == AttackMode.IDLE ? hummel -> ((NPCItemsInventory) hummel).getIdleItem() : hummel -> ((NPCItemsInventory) hummel).getMeleeWeapon());
		};
	}

	public static final AttackModeUpdater meleeOnlyOrcWithBomb() {
		return (npc, mode, mounted) -> {
			if (!npc.getNPCItemsInv().getBomb().isEmpty()) {
				helperSetMainhandItem(npc, hummel -> ((NPCItemsInventory) hummel).getBombingItem());
			} else {
				helperSetMainhandItem(npc, mode == AttackMode.IDLE ? hummel -> ((NPCItemsInventory) hummel).getIdleItem() : hummel -> ((NPCItemsInventory) hummel).getMeleeWeapon());
			}

		};
	}

	public static final AttackModeUpdater meleeRangedSwitching() {
		return (npc, mode, mounted) -> {
			Goal meleeGoal = npc.getAttackGoalsHolder().getNonNullMeleeAttackGoal();
			Goal rangedGoal = npc.getAttackGoalsHolder().getNonNullRangedAttackGoal();
			npc.goalSelector.removeGoal(meleeGoal);
			npc.goalSelector.removeGoal(rangedGoal);
			if (mode == AttackMode.IDLE) {
				helperSetMainhandItem(npc, hummel -> ((NPCItemsInventory) hummel).getIdleItem());
			} else {
				int goalIndex = npc.getAttackGoalIndex();
				if (goalIndex < 0) {
					throw new IllegalStateException("Tried to run melee-range switching for an NPC " + npc.getName().getString() + " without a defined attack goal index - this is a development error!");
				}

				if (mode == AttackMode.MELEE) {
					npc.goalSelector.addGoal(goalIndex, meleeGoal);
					helperSetMainhandItem(npc, hummel -> ((NPCItemsInventory) hummel).getMeleeWeapon());
				} else if (mode == AttackMode.RANGED) {
					npc.goalSelector.addGoal(goalIndex, rangedGoal);
					helperSetMainhandItem(npc, hummel -> ((NPCItemsInventory) hummel).getRangedWeapon());
				}
			}

		};
	}

	public static final AttackModeUpdater mountableMeleeOnly() {
		return (npc, mode, mounted) -> {
			if (mounted) {
				helperSetMainhandItem(npc, mode == AttackMode.IDLE ? hummel -> ((NPCItemsInventory) hummel).getIdleItemMounted() : hummel -> ((NPCItemsInventory) hummel).getMeleeWeaponMounted());
			} else {
				helperSetMainhandItem(npc, mode == AttackMode.IDLE ? hummel -> ((NPCItemsInventory) hummel).getIdleItem() : hummel -> ((NPCItemsInventory) hummel).getMeleeWeapon());
			}

		};
	}

	public static final AttackModeUpdater rangedOnly() {
		return (npc, mode, mounted) -> {
			helperSetMainhandItem(npc, mode == AttackMode.IDLE ? hummel -> ((NPCItemsInventory) hummel).getIdleItem() : hummel -> ((NPCItemsInventory) hummel).getRangedWeapon());
		};
	}
}
