package lotr.common.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import lotr.common.LOTRLog;
import lotr.common.fac.Faction;
import lotr.common.network.SPacketFactionStats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class FactionStatsDataModule extends PlayerDataModule {
	private final Map factionStatsMap = new HashMap();

	protected FactionStatsDataModule(LOTRPlayerData pd) {
		super(pd);
	}

	public FactionStats getFactionStats(Faction faction) {
		return (FactionStats) factionStatsMap.computeIfAbsent(faction, f -> new FactionStats(this, (Faction) f));
	}

	@Override
	public void load(CompoundNBT playerNBT) {
		DataUtil.loadMapFromListNBT(factionStatsMap, playerNBT.getList("FactionStats", 10), nbt -> {
			ResourceLocation facName = new ResourceLocation(((CompoundNBT) nbt).getString("Faction"));
			Faction fac = currentFactionSettings().getFactionByName(facName);
			if (fac != null) {
				FactionStats stats = new FactionStats(this, fac);
				stats.load((CompoundNBT) nbt);
				return Pair.of(fac, stats);
			}
			playerData.logPlayerError("Loaded faction stats for nonexistent faction %s", facName);
			return null;
		});
	}

	@Override
	protected void receiveLoginData(PacketBuffer buf) {
		DataUtil.fillMapFromBuffer(buf, factionStatsMap, () -> {
			int factionId = buf.readVarInt();
			Faction faction = currentFactionSettings().getFactionByID(factionId);
			if (faction == null) {
				LOTRLog.warn("Received faction stats for nonexistent faction ID %d from server", factionId);
				return null;
			}
			FactionStats stats = new FactionStats(this, faction);
			stats.read(buf);
			return Pair.of(faction, stats);
		});
	}

	@Override
	public void save(CompoundNBT playerNBT) {
		playerNBT.put("FactionStats", DataUtil.saveMapAsListNBT(factionStatsMap, (nbt, fac, stats) -> {
			((CompoundNBT) nbt).putString("Faction", ((Faction) fac).getName().toString());
			((FactionStats) stats).save((CompoundNBT) nbt);
		}));
	}

	@Override
	protected void sendLoginData(PacketBuffer buf) {
		DataUtil.writeMapToBuffer(buf, factionStatsMap, (faction, stats) -> {
			buf.writeVarInt(((Faction) faction).getAssignedId());
			((FactionStats) stats).write(buf);
		});
	}

	protected void updateFactionData(Faction faction) {
		markDirty();
		sendPacketToClient(new SPacketFactionStats(faction, getFactionStats(faction)));
	}
}
