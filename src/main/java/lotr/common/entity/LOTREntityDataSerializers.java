package lotr.common.entity;

import java.util.Optional;

import lotr.common.fac.FactionPointer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.IDataSerializer;

public class LOTREntityDataSerializers {
	public static final IDataSerializer FACTION_POINTER = new IDataSerializer() {
		@Override
		public Object copy(Object p_192717_1_) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FactionPointer read(PacketBuffer buf) {
			return FactionPointer.of(buf.readResourceLocation());
		}

		@Override
		public void write(PacketBuffer p_187160_1_, Object p_187160_2_) {
			// TODO Auto-generated method stub

		}
	};
	public static final IDataSerializer OPTIONAL_FACTION_POINTER = new IDataSerializer() {
		@Override
		public Object copy(Object p_192717_1_) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional read(PacketBuffer buf) {
			return !buf.readBoolean() ? Optional.empty() : Optional.of(FactionPointer.of(buf.readResourceLocation()));
		}

		@Override
		public void write(PacketBuffer p_187160_1_, Object p_187160_2_) {
			// TODO Auto-generated method stub

		}
	};

	public static void register() {
		DataSerializers.registerSerializer(FACTION_POINTER);
		DataSerializers.registerSerializer(OPTIONAL_FACTION_POINTER);
	}
}
