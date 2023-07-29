package lotr.curuquesta.util;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;

public class StringSerializer {
	public static String read(ByteBuf buf) {
		int length = buf.readInt();
		ByteBuf bytes = buf.readBytes(length);
		return bytes.toString(StandardCharsets.UTF_8);
	}

	public static void write(String value, ByteBuf buf) {
		buf.writeInt(value.length());
		buf.writeBytes(value.getBytes(StandardCharsets.UTF_8));
	}
}
