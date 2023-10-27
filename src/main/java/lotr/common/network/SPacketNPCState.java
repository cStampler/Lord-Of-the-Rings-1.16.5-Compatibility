package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketNPCState {
	private final int entityId;
	private final SPacketNPCState.Type type;
	private final boolean state;

	private SPacketNPCState(int entityId, SPacketNPCState.Type type, boolean state) {
		this.entityId = entityId;
		this.type = type;
		this.state = state;
	}

	public SPacketNPCState(NPCEntity entity, SPacketNPCState.Type type, boolean state) {
		this(entity.getId(), type, state);
	}

	public static SPacketNPCState decode(PacketBuffer buf) {
		int entityId = buf.readInt();
		SPacketNPCState.Type type = SPacketNPCState.Type.forId(buf.readByte());
		boolean state = buf.readBoolean();
		return new SPacketNPCState(entityId, type, state);
	}

	public static void encode(SPacketNPCState packet, PacketBuffer buf) {
		buf.writeInt(packet.entityId);
		buf.writeByte(packet.type.ordinal());
		buf.writeBoolean(packet.state);
	}

	public static void handle(SPacketNPCState packet, Supplier context) {
		World world = LOTRMod.PROXY.getClientWorld();
		SPacketNPCState.Type type = packet.type;
		int entityId = packet.entityId;
		Entity entity = world.getEntity(entityId);
		if (entity instanceof NPCEntity) {
			NPCEntity npc = (NPCEntity) entity;
			boolean state = packet.state;
			if (type == SPacketNPCState.Type.IS_EATING) {
				npc.getNPCItemsInv().receiveClientIsEating(state);
			} else if (type == SPacketNPCState.Type.COMBAT_STANCE) {
				npc.getNPCCombatUpdater().receiveClientCombatStance(state);
			} else {
				LOTRLog.error("Received SPacketNPCState with unsupported type %s!", type.name());
			}
		} else {
			LOTRLog.warn("Received SPacketNPCState (type %s) for client-side entity ID %d, but entity was %s (not an NPC)", type.name(), entityId, entity);
		}

		((Context) context.get()).setPacketHandled(true);
	}

	public enum Type {
		IS_EATING, COMBAT_STANCE;

		public static SPacketNPCState.Type forId(int id) {
			return values()[MathHelper.clamp(id, 0, values().length - 1)];
		}
	}
}
