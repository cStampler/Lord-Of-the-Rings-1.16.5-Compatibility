package lotr.common.data;

import lotr.common.fac.Faction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class FactionStats {
	private final FactionStatsDataModule dataModule;
	private final Faction faction;
	private int membersKilled;
	private int enemiesKilled;
	private int tradeCount;
	private int hireCount;
	private int miniQuestsCompleted;
	private float conquestEarned;
	private boolean hasConquestHorn;

	public FactionStats(FactionStatsDataModule data, Faction fac) {
		dataModule = data;
		faction = fac;
	}

	public void addConquest(float f) {
		conquestEarned += f;
		updateFactionData();
	}

	public void addEnemyKill() {
		++enemiesKilled;
		updateFactionData();
	}

	public void addHire() {
		++hireCount;
		updateFactionData();
	}

	public void addMemberKill() {
		++membersKilled;
		updateFactionData();
	}

	public void addTrade() {
		++tradeCount;
		updateFactionData();
	}

	public void completeMiniQuest() {
		++miniQuestsCompleted;
		updateFactionData();
	}

	public float getConquestEarned() {
		return conquestEarned;
	}

	public int getEnemiesKilled() {
		return enemiesKilled;
	}

	public int getHireCount() {
		return hireCount;
	}

	public int getMembersKilled() {
		return membersKilled;
	}

	public int getMiniQuestsCompleted() {
		return miniQuestsCompleted;
	}

	public int getTradeCount() {
		return tradeCount;
	}

	public boolean hasConquestHorn() {
		return hasConquestHorn;
	}

	public void load(CompoundNBT nbt) {
		membersKilled = nbt.getInt("MemberKill");
		enemiesKilled = nbt.getInt("EnemyKill");
		tradeCount = nbt.getInt("Trades");
		hireCount = nbt.getInt("Hired");
		miniQuestsCompleted = nbt.getInt("MiniQuests");
		conquestEarned = nbt.getFloat("Conquest");
		hasConquestHorn = nbt.getBoolean("ConquestHorn");
	}

	public void read(PacketBuffer buf) {
		membersKilled = buf.readVarInt();
		enemiesKilled = buf.readVarInt();
		tradeCount = buf.readVarInt();
		hireCount = buf.readVarInt();
		miniQuestsCompleted = buf.readVarInt();
		conquestEarned = buf.readFloat();
		hasConquestHorn = buf.readBoolean();
	}

	public void save(CompoundNBT nbt) {
		nbt.putInt("MemberKill", membersKilled);
		nbt.putInt("EnemyKill", enemiesKilled);
		nbt.putInt("Trades", tradeCount);
		nbt.putInt("Hired", hireCount);
		nbt.putInt("MiniQuests", miniQuestsCompleted);
		if (conquestEarned != 0.0F) {
			nbt.putFloat("Conquest", conquestEarned);
		}

		nbt.putBoolean("ConquestHorn", hasConquestHorn);
	}

	public void takeConquestHorn() {
		hasConquestHorn = true;
		updateFactionData();
	}

	private void updateFactionData() {
		dataModule.updateFactionData(faction);
	}

	public void write(PacketBuffer buf) {
		buf.writeVarInt(membersKilled);
		buf.writeVarInt(enemiesKilled);
		buf.writeVarInt(tradeCount);
		buf.writeVarInt(hireCount);
		buf.writeVarInt(miniQuestsCompleted);
		buf.writeFloat(conquestEarned);
		buf.writeBoolean(hasConquestHorn);
	}
}
