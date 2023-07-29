package lotr.client.align;

import java.util.List;
import java.util.stream.Collectors;

import lotr.common.LOTRLog;
import lotr.common.fac.Faction;
import lotr.common.network.SPacketNotifyAlignRequirement;
import lotr.common.util.LOTRUtil;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.*;

public class NotifyAlignmentRequirement {
	public static void displayMessage(ClientPlayerEntity player, SPacketNotifyAlignRequirement packet) {
		String alignString = AlignmentFormatter.formatAlignForDisplay(packet.getAlignmentRequired());
		IFormattableTextComponent componentAlignReq = new StringTextComponent(alignString);
		componentAlignReq.withStyle(TextFormatting.YELLOW);
		List<String> factionNames = (List<String>) packet.getAnyOfFactions().stream().map(hummel -> ((Faction) hummel).getColoredDisplayName()).collect(Collectors.toList());
		if (!factionNames.isEmpty()) {
			TranslationTextComponent fullMessage;
			if (factionNames.size() == 1) {
				fullMessage = new TranslationTextComponent("chat.lotr.align.insufficient", componentAlignReq, factionNames.get(0));
			} else if (factionNames.size() == 2) {
				fullMessage = new TranslationTextComponent("chat.lotr.align.insufficient.2", componentAlignReq, factionNames.get(0), factionNames.get(1));
			} else if (factionNames.size() == 3) {
				fullMessage = new TranslationTextComponent("chat.lotr.align.insufficient.3", componentAlignReq, factionNames.get(0), factionNames.get(1), factionNames.get(2));
			} else {
				fullMessage = new TranslationTextComponent("chat.lotr.align.insufficient.more", componentAlignReq, factionNames.get(0), factionNames.get(1), factionNames.get(2), factionNames.size() - 3);
			}

			LOTRUtil.sendMessage(player, fullMessage);
		} else {
			LOTRLog.error("Received notify alignment requirement packet from server with an empty list of factions - this shouldn't happen!");
		}

	}
}
