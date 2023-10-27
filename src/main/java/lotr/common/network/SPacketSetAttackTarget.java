package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketSetAttackTarget {
	private final int entityId;
	private final boolean hasTarget;
	private final int targetEntityId;

	private SPacketSetAttackTarget(int entityId, boolean hasTarget, int targetEntityId) {
		this.entityId = entityId;
		this.hasTarget = hasTarget;
		this.targetEntityId = targetEntityId;
	}

	public SPacketSetAttackTarget(MobEntity entity) {
		entityId = entity.getId();
		LivingEntity target = entity.getTarget();
		hasTarget = target != null;
		targetEntityId = target != null ? target.getId() : 0;
	}

	public int getEntityId() {
		return entityId;
	}

	public boolean getHasTarget() {
		return hasTarget;
	}

	public int getTargetEntityId() {
		return targetEntityId;
	}

	public static SPacketSetAttackTarget decode(PacketBuffer buf) {
		int entityId = buf.readVarInt();
		boolean hasTarget = buf.readBoolean();
		int targetEntityId = hasTarget ? buf.readVarInt() : 0;
		return new SPacketSetAttackTarget(entityId, hasTarget, targetEntityId);
	}

	public static void encode(SPacketSetAttackTarget packet, PacketBuffer buf) {
		buf.writeVarInt(packet.entityId);
		buf.writeBoolean(packet.hasTarget);
		if (packet.hasTarget) {
			buf.writeVarInt(packet.targetEntityId);
		}

	}

	public static void handle(SPacketSetAttackTarget packet, Supplier context) {
		LOTRMod.PROXY.receiveClientAttackTarget(packet);
		((Context) context.get()).setPacketHandled(true);
	}
}
