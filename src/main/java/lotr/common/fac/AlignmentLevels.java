package lotr.common.fac;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import lotr.common.LOTRLog;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.network.SPacketNotifyAlignRequirement;
import net.minecraft.entity.player.ServerPlayerEntity;

public class AlignmentLevels {
	public static final float USE_FACTION_TABLE = 1.0F;
	public static final float MORGUL_FLOWER_SAFE = 250.0F;
	public static final float MAGGOTY_BREAD_SAFE = 250.0F;
	public static final float MAN_FLESH_SAFE = 250.0F;
	public static final float ORC_DRAUGHT_FULLY_EFFECTIVE = 100.0F;

	public static void notifyAlignmentNotHighEnough(ServerPlayerEntity player, float alignmentRequired, Faction faction) {
		notifyAlignmentNotHighEnough(player, alignmentRequired, ImmutableList.of(faction));
	}

	public static void notifyAlignmentNotHighEnough(ServerPlayerEntity player, float alignmentRequired, FactionPointer facPointer) {
		Optional<Faction> optFaction = facPointer.resolveFaction(player.level);
		if (optFaction.isPresent()) {
			notifyAlignmentNotHighEnough(player, alignmentRequired, (Faction) optFaction.get());
		} else {
			LOTRLog.warn("Tried to inform player %s of needing %f required alignment with faction %s, but no such faction exists in the current datapacks!", player.getName().getString(), alignmentRequired, facPointer.getName());
		}

	}

	public static void notifyAlignmentNotHighEnough(ServerPlayerEntity player, float alignmentRequired, List<Faction> factions) {
		SPacketNotifyAlignRequirement packet = new SPacketNotifyAlignRequirement(factions, alignmentRequired);
		LOTRPacketHandler.sendTo(packet, player);
	}
}
