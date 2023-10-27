package lotr.common.inv;

import java.util.HashMap;
import java.util.Map;

import lotr.common.LOTRLog;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ShulkerBoxContainer;
import net.minecraft.inventory.container.ShulkerBoxSlot;
import net.minecraft.inventory.container.Slot;

public class ShulkerBoxContainerFix {
	public static void fixContainerSlots(ShulkerBoxContainer container, PlayerEntity player) {
		Map replacedSlots = new HashMap();

		for (int i = 0; i < container.slots.size(); ++i) {
			Slot slot = container.slots.get(i);
			if (slot.getClass() == ShulkerBoxSlot.class) {
				Slot replacedSlot = new FixedShulkerBoxSlot(slot.container, slot.getSlotIndex(), slot.x, slot.y);
				replacedSlot.index = slot.index;
				replacedSlots.put(i, replacedSlot);
			}
		}

		if (replacedSlots.isEmpty()) {
			LOTRLog.warn("Didn't replace any slots in ShulkerBoxContainer opened by player %s! (isRemote = %s) Expected to replace them. Call hierarchy is:", player, player.level.isClientSide);
			Thread.dumpStack();
		} else {
			replacedSlots.forEach((index, slotx) -> {
				container.slots.set((int) index, (Slot) slotx);
			});
		}

	}
}
