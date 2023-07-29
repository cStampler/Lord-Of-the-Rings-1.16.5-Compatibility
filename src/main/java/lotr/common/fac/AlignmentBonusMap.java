package lotr.common.fac;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import lotr.common.LOTRLog;
import lotr.common.data.DataUtil;
import net.minecraft.network.PacketBuffer;

public class AlignmentBonusMap extends HashMap {
	public Set getChangedFactions() {
		return (Set) keySet().stream().filter(f -> ((Float) get(f) != 0.0F)).collect(Collectors.toSet());
	}

	public void write(PacketBuffer buf) {
		DataUtil.writeMapToBuffer(buf, this, (fac, align) -> {
			buf.writeVarInt(((Faction) fac).getAssignedId());
			buf.writeFloat((float) align);
		});
	}

	public static AlignmentBonusMap read(PacketBuffer buf, FactionSettings currentLoadedFactions) {
		return (AlignmentBonusMap) DataUtil.readNewMapFromBuffer(buf, AlignmentBonusMap::new, () -> {
			int facId = buf.readVarInt();
			float align = buf.readFloat();
			Faction fac = currentLoadedFactions.getFactionByID(facId);
			if (fac != null) {
				return Pair.of(fac, align);
			}
			LOTRLog.warn("Received nonexistent faction ID %d in alignment bonus packet", facId);
			return null;
		});
	}
}
