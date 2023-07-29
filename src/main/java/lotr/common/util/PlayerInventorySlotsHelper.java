package lotr.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class PlayerInventorySlotsHelper {
	public static int getHandHeldItemIndex(PlayerEntity player, Hand hand) {
		return hand == Hand.MAIN_HAND ? player.inventory.selected : 40;
	}
}
