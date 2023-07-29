package lotr.common.entity.npc.data;

import java.util.*;
import java.util.function.BiConsumer;

import lotr.common.data.DataUtil;
import lotr.common.entity.npc.NPCEntity;
import lotr.common.network.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class NPCPersonalInfo {
	private static final UUID DRUNK_ATTACK_BOOST_ID = UUID.fromString("ad7395ad-3452-449f-b2e0-fe35d55692a9");
	private static final AttributeModifier DRUNK_ATTACK_BOOST;
	static {
		DRUNK_ATTACK_BOOST = new AttributeModifier(DRUNK_ATTACK_BOOST_ID, "Drunk melee attack boost", 4.0D, Operation.ADDITION);
	}
	private final NPCEntity theEntity;
	private boolean doneFirstUpdate = false;
	private boolean resendData = true;
	private int age;
	private boolean isMale;
	private String name;
	private boolean prevWasDrunk = false;
	private boolean clientIsDrunk;

	private PersonalityTraits personalityTraits;

	public NPCPersonalInfo(NPCEntity npc) {
		theEntity = npc;
	}

	public void assumeRandomPersonalityTraits(Random rand) {
		if (personalityTraits != null) {
			throw new IllegalStateException("Personality traits already set!");
		}
		EnumSet traits = EnumSet.noneOf(PersonalityTrait.class);
		PersonalityTrait[] var3 = PersonalityTrait.values();
		int var4 = var3.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			PersonalityTrait trait = var3[var5];
			if (rand.nextBoolean()) {
				traits.add(trait);
			}
		}

		personalityTraits = PersonalityTraits.of(traits);
		markDirty();
	}

	public int getAge() {
		return age;
	}

	public String getName() {
		return name;
	}

	public PersonalityTraits getPersonalityTraits() {
		return personalityTraits;
	}

	public boolean isChild() {
		return age < 0;
	}

	public boolean isDrunk() {
		return !theEntity.level.isClientSide ? theEntity.hasEffect(Effects.CONFUSION) : clientIsDrunk;
	}

	public boolean isFemale() {
		return !isMale;
	}

	public boolean isMale() {
		return isMale;
	}

	private void markDirty() {
		if (!theEntity.level.isClientSide) {
			if (theEntity.tickCount > 0) {
				resendData = true;
			} else {
				sendDataToAllWatchers();
			}
		}

	}

	public void read(CompoundNBT nbt) {
		setAge(nbt.getInt("NPCAge"));
		if (nbt.contains("NPCMale")) {
			setMale(nbt.getBoolean("NPCMale"));
		}

		if (nbt.contains("NPCName")) {
			setName(nbt.getString("NPCName"));
		}

		if (nbt.contains("NPCPersonality")) {
			personalityTraits = PersonalityTraits.load(nbt.getCompound("NPCPersonality"));
		}

	}

	public void sendData(ServerPlayerEntity player) {
		SPacketNPCPersonalInfo packet = new SPacketNPCPersonalInfo(this);
		LOTRPacketHandler.sendTo(packet, player);
	}

	private void sendDataToAllWatchers() {
		SPacketNPCPersonalInfo packet = new SPacketNPCPersonalInfo(this);
		LOTRPacketHandler.sendToAllTrackingEntity(packet, theEntity);
	}

	public void setAge(int i) {
		age = i;
		markDirty();
	}

	public void setMale(boolean flag) {
		isMale = flag;
		markDirty();
	}

	public void setName(String s) {
		name = s;
		markDirty();
	}

	public void tick() {
		if (!theEntity.level.isClientSide) {
			if (!doneFirstUpdate) {
				doneFirstUpdate = true;
			}

			if (resendData) {
				sendDataToAllWatchers();
				resendData = false;
			}

			if (getAge() < 0) {
				setAge(getAge() + 1);
			} else if (getAge() > 0) {
				setAge(getAge() - 1);
			}

			updateDrunkEffects();
			if (isDrunk()) {
			}
		}

	}

	private void updateDrunkEffects() {
		boolean isDrunk = isDrunk();
		if (isDrunk != prevWasDrunk) {
			ModifiableAttributeInstance attrib = theEntity.getAttribute(Attributes.ATTACK_DAMAGE);
			attrib.removeModifier(DRUNK_ATTACK_BOOST_ID);
			if (isDrunk) {
				attrib.addTransientModifier(DRUNK_ATTACK_BOOST);
			}

			markDirty();
		}

		prevWasDrunk = isDrunk;
	}

	public void write(CompoundNBT nbt) {
		nbt.putInt("NPCAge", getAge());
		nbt.putBoolean("NPCMale", isMale());
		if (getName() != null) {
			nbt.putString("NPCName", getName());
		}

		if (personalityTraits != null) {
			CompoundNBT personalityNbt = new CompoundNBT();
			personalityTraits.save(personalityNbt);
			nbt.put("NPCPersonality", personalityNbt);
		}

	}

	public void write(PacketBuffer buf) {
		buf.writeVarInt(theEntity.getId());
		buf.writeVarInt(age);
		buf.writeBoolean(isMale);
		boolean hasName = name != null;
		buf.writeBoolean(hasName);
		if (hasName) {
			buf.writeUtf(name);
		}

		buf.writeBoolean(isDrunk());
		DataUtil.writeNullableToBuffer(buf, personalityTraits, (BiConsumer) (hummel, hummel2) -> ((PersonalityTraits) hummel).write((PacketBuffer) hummel2));
	}

	public static void read(PacketBuffer buf, World world) {
		int entityId = buf.readVarInt();
		Entity entity = world.getEntity(entityId);
		if (entity instanceof NPCEntity) {
			NPCEntity npc = (NPCEntity) entity;
			NPCPersonalInfo personalInfo = npc.getPersonalInfo();
			personalInfo.setAge(buf.readVarInt());
			personalInfo.setMale(buf.readBoolean());
			boolean hasName = buf.readBoolean();
			personalInfo.setName(hasName ? buf.readUtf() : null);
			personalInfo.clientIsDrunk = buf.readBoolean();
			personalInfo.personalityTraits = (PersonalityTraits) DataUtil.readNullableFromBuffer(buf, () -> PersonalityTraits.read(buf));
		}

	}
}
