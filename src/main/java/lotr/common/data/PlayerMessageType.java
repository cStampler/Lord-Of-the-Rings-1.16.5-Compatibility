package lotr.common.data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lotr.common.network.LOTRPacketHandler;
import lotr.common.network.SPacketPlayerMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum PlayerMessageType {
	FRIENDLY_FIRE("friendly_fire"), ALIGN_DRAIN("align_drain"), CUSTOM("custom");

	private final String messageName;
	public final int networkID;

	PlayerMessageType(String s) {
		messageName = s;
		networkID = ordinal();
	}

	public void displayTo(ServerPlayerEntity player, boolean isCommandSent) {
		LOTRPacketHandler.sendTo(new SPacketPlayerMessage(this, isCommandSent, (String) null), player);
	}

	public ITextComponent getDisplayMessage() {
		return new TranslationTextComponent("gui.lotr.message." + messageName);
	}

	public String getSaveName() {
		return messageName;
	}

	public static void displayCustomMessageTo(ServerPlayerEntity player, boolean isCommandSent, String customText) {
		LOTRPacketHandler.sendTo(new SPacketPlayerMessage(CUSTOM, isCommandSent, customText), player);
	}

	public static PlayerMessageType forNetworkID(int id) {
		PlayerMessageType[] var1 = values();
		int var2 = var1.length;

		for (int var3 = 0; var3 < var2; ++var3) {
			PlayerMessageType message = var1[var3];
			if (message.networkID == id) {
				return message;
			}
		}

		return null;
	}

	public static PlayerMessageType forSaveName(String name) {
		PlayerMessageType[] var1 = values();
		int var2 = var1.length;

		for (int var3 = 0; var3 < var2; ++var3) {
			PlayerMessageType message = var1[var3];
			if (message.getSaveName().equals(name)) {
				return message;
			}
		}

		return null;
	}

	public static List getAllPresetNamesForCommand() {
		return Stream.of(values()).filter(type -> (type != CUSTOM)).map(PlayerMessageType::getSaveName).collect(Collectors.toList());
	}
}
