package lotr.common.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.arguments.TimeArgument;

public class LOTRTimeArgument extends TimeArgument {
	@Override
	public Integer parse(StringReader reader) throws CommandSyntaxException {
		StringReader copyReader = new StringReader(reader);
		copyReader.readFloat();
		String type = copyReader.readUnquotedString();
		int i = super.parse(reader);
		if ("d".equals(type)) {
			i *= 2;
		}

		return i;
	}

	public static LOTRTimeArgument create() {
		return new LOTRTimeArgument();
	}
}
