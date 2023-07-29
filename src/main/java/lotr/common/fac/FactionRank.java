package lotr.common.fac;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class FactionRank implements Comparable {
	private final Faction faction;
	private final String name;
	private final int assignedId;
	private final boolean isDummyRank;
	private final float alignment;
	private final boolean isPledgeRank;

	public FactionRank(Faction faction, String name, int assignedId, boolean isDummyRank, float alignment, boolean isPledgeRank) {
		this.faction = faction;
		this.name = name;
		this.assignedId = assignedId;
		this.isDummyRank = isDummyRank;
		this.alignment = alignment;
		this.isPledgeRank = isPledgeRank;
		if (!isDummyRank && alignment <= 0.0F) {
			throw new IllegalArgumentException(String.format("Faction rank %s.%s is invalid - alignment must be greater than 0 for a non-dummy rank", faction.getName(), name));
		}
	}

	@Override
	public int compareTo(Object other) {
		if (faction != ((FactionRank) other).faction) {
			throw new IllegalArgumentException(String.format("Cannot compare two ranks from different factions! %s, %s", getTranslationNameKey(), ((FactionRank) other).getTranslationNameKey()));
		}
		float align1 = alignment;
		float align2 = ((FactionRank) other).alignment;
		if (align1 == align2 && this != other) {
			throw new IllegalArgumentException(String.format("Two ranks cannot have the same alignment value! %s (= %f), %s (= %f)", getTranslationNameKey(), align1, ((FactionRank) other).getTranslationNameKey(), align2));
		}
		return Float.compare(align1, align2);
	}

	public float getAlignment() {
		return alignment;
	}

	public int getAssignedId() {
		return assignedId;
	}

	public String getBaseName() {
		return name;
	}

	public String getDisplayFullName(RankGender gender) {
		return FactionRankNameDecomposer.actOn(getTranslatedName()).getFullName(gender);
	}

	public String getDisplayShortName(RankGender gender) {
		return FactionRankNameDecomposer.actOn(getTranslatedName()).getShortName(gender);
	}

	public Faction getFaction() {
		return faction;
	}

	private String getTranslatedName() {
		return new TranslationTextComponent(getTranslationNameKey()).getString();
	}

	public String getTranslationNameKey() {
		return String.format("faction.%s", toString());
	}

	public boolean isAbovePledgeRank() {
		return alignment > faction.getPledgeAlignment();
	}

	public boolean isDummyRank() {
		return isDummyRank;
	}

	public boolean isNameEqual(String rankName) {
		return getBaseName().equals(rankName);
	}

	public boolean isPledgeRank() {
		return isPledgeRank;
	}

	@Override
	public String toString() {
		if (isDummyRank) {
			return String.format("%s.rank.%s", "lotr", getBaseName());
		}
		ResourceLocation facName = faction.getName();
		return String.format("%s.%s.rank.%s", facName.getNamespace(), facName.getPath(), getBaseName());
	}

	public void write(PacketBuffer buf) {
		buf.writeUtf(name);
		buf.writeVarInt(assignedId);
		buf.writeBoolean(isDummyRank);
		buf.writeFloat(alignment);
		buf.writeBoolean(isPledgeRank);
	}

	public static FactionRank read(Faction faction, JsonObject json, int assignedId) {
		String name = json.get("name").getAsString();
		float alignment = json.get("alignment").getAsFloat();
		boolean isPledgeRank = json.has("is_pledge_rank") && json.get("is_pledge_rank").getAsBoolean();
		return new FactionRank(faction, name, assignedId, false, alignment, isPledgeRank);
	}

	public static FactionRank read(Faction faction, PacketBuffer buf) {
		String name = buf.readUtf();
		int assignedId = buf.readVarInt();
		boolean isDummyRank = buf.readBoolean();
		float alignment = buf.readFloat();
		boolean isPledgeRank = buf.readBoolean();
		return new FactionRank(faction, name, assignedId, isDummyRank, alignment, isPledgeRank);
	}
}
