package lotr.common.command.arguments;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import lotr.common.command.LOTRArgumentTypes;
import lotr.common.fac.*;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class FactionArgument implements ArgumentType {
	private static final Collection EXAMPLES;
	public static final DynamicCommandExceptionType FACTION_BAD_ID;

	static {
		EXAMPLES = Arrays.asList(FactionPointers.ROHAN.getNameString(), FactionPointers.ISENGARD.getNameString());
		FACTION_BAD_ID = new DynamicCommandExceptionType(arg -> new TranslationTextComponent("argument.lotr.faction.id.invalid", arg));
	}

	@Override
	public Collection getExamples() {
		return EXAMPLES;
	}

	@Override
	public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
		return ISuggestionProvider.suggestResource(LOTRArgumentTypes.getCurrentSidedFactionSettings().getPlayableFactionNames(), builder);
	}

	@Override
	public FactionPointer parse(StringReader reader) throws CommandSyntaxException {
		FactionSettings currentFactions = LOTRArgumentTypes.getCurrentSidedFactionSettings();
		if (currentFactions == null) {
			return FactionPointers.UNALIGNED;
		}
		int cursor = reader.getCursor();
		ResourceLocation name = ResourceLocation.read(reader);
		FactionPointer pointer = FactionPointer.of(name);
		if (pointer.resolveFaction(currentFactions).filter(hummel -> ((Faction) hummel).isPlayableAlignmentFaction()).isPresent()) {
			return pointer;
		}
		reader.setCursor(cursor);
		throw FACTION_BAD_ID.createWithContext(reader, name.toString());
	}

	public static FactionArgument faction() {
		return new FactionArgument();
	}

	public static Faction getFaction(CommandContext context, String name) {
		return (Faction) getFactionPointer(context, name).resolveFaction(LOTRArgumentTypes.getCurrentSidedFactionSettings()).orElse((Object) null);
	}

	public static FactionPointer getFactionPointer(CommandContext context, String name) {
		return (FactionPointer) context.getArgument(name, FactionPointer.class);
	}
}
