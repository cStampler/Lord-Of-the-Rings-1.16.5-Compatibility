package lotr.common.command.arguments;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import lotr.common.data.PlayerMessageType;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

public class PlayerMessageTypeArgument implements ArgumentType {
	private static final Collection EXAMPLES;
	public static final DynamicCommandExceptionType MESSAGE_BAD_ID;

	static {
		EXAMPLES = Arrays.asList(PlayerMessageType.ALIGN_DRAIN.getSaveName(), PlayerMessageType.FRIENDLY_FIRE.getSaveName());
		MESSAGE_BAD_ID = new DynamicCommandExceptionType(arg -> new TranslationTextComponent("argument.lotr.playerMessage.id.invalid", arg));
	}

	@Override
	public Collection getExamples() {
		return EXAMPLES;
	}

	@Override
	public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
		return ISuggestionProvider.suggest(PlayerMessageType.getAllPresetNamesForCommand(), builder);
	}

	@Override
	public PlayerMessageType parse(StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		String name = reader.readUnquotedString();
		PlayerMessageType message = PlayerMessageType.forSaveName(name);
		if (message != null && message != PlayerMessageType.CUSTOM) {
			return message;
		}
		reader.setCursor(cursor);
		throw MESSAGE_BAD_ID.createWithContext(reader, name.toString());
	}

	public static PlayerMessageType getMessageType(CommandContext context, String name) {
		return (PlayerMessageType) context.getArgument(name, PlayerMessageType.class);
	}

	public static PlayerMessageTypeArgument messageType() {
		return new PlayerMessageTypeArgument();
	}
}
