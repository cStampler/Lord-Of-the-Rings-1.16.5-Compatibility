package lotr.common.entity.npc.data;

import java.util.*;
import java.util.stream.*;

import lotr.common.LOTRLog;
import lotr.common.data.DataUtil;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketBuffer;

public class PersonalityTraits {
	private final Set traits;

	private PersonalityTraits(Set traits) {
		this.traits = traits;
	}

	public boolean hasOppositeTrait(PersonalityTrait trait) {
		return !hasTrait(trait);
	}

	public boolean hasTrait(PersonalityTrait trait) {
		return traits.contains(trait);
	}

	public void save(CompoundNBT nbt) {
		nbt.put("Traits", DataUtil.saveCollectionAsPrimitiveListNBT(traits, trait -> StringNBT.valueOf(((PersonalityTrait) trait).getMainName())));
	}

	@Override
	public String toString() {
		return String.format("PersonalityTraits[%s]", Stream.of(PersonalityTrait.values()).sorted().map(trait -> (hasTrait(trait) ? trait.getMainName() : trait.getOppositeName())).collect(Collectors.joining(", ")));
	}

	public void write(PacketBuffer buf) {
		DataUtil.writeCollectionToBuffer(buf, traits, trait -> {
			buf.writeVarInt(((PersonalityTrait) trait).getNetworkID());
		});
	}

	public static PersonalityTraits load(CompoundNBT nbt) {
		EnumSet traits = EnumSet.noneOf(PersonalityTrait.class);
		DataUtil.loadCollectionFromPrimitiveListNBT(traits, nbt.getList("Traits", 8), (hummel, hummel2) -> ((ListNBT) hummel).getString((int) hummel2), name -> {
			PersonalityTrait trait = PersonalityTrait.fromMainName((String) name);
			if (trait == null) {
				LOTRLog.warn("Loaded nonexistent personality trait %s", name);
			}

			return trait;
		});
		return of(traits);
	}

	public static PersonalityTraits of(Set traits) {
		return new PersonalityTraits(traits);
	}

	public static PersonalityTraits read(PacketBuffer buf) {
		EnumSet traits = EnumSet.noneOf(PersonalityTrait.class);
		DataUtil.fillCollectionFromBuffer(buf, traits, () -> {
			int id = buf.readVarInt();
			PersonalityTrait trait = PersonalityTrait.fromNetworkID(id);
			if (trait == null) {
				LOTRLog.warn("Received nonexistent personality trait ID %d from server", id);
			}

			return trait;
		});
		return of(traits);
	}
}
