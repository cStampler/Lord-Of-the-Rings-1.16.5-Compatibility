package lotr.common.network;

import java.util.function.Consumer;
import java.util.function.Function;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;

public abstract class ByteArrayPacket {
	private final byte[] byteData;

	protected ByteArrayPacket(byte[] data) {
		byteData = data;
	}

	protected ByteArrayPacket(Consumer dataWriter) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		dataWriter.accept(buf);
		byteData = new byte[buf.readableBytes()];
		buf.getBytes(0, byteData);
	}

	protected final PacketBuffer getBufferedByteData() {
		return new PacketBuffer(Unpooled.copiedBuffer(byteData));
	}

	protected static ByteArrayPacket decodeByteData(PacketBuffer buf, Function packetConstructor) {
		byte[] byteData = buf.readByteArray();
		return (ByteArrayPacket) packetConstructor.apply(byteData);
	}

	protected static void encodeByteData(ByteArrayPacket packet, PacketBuffer buf) {
		buf.writeByteArray(packet.byteData);
	}
}
