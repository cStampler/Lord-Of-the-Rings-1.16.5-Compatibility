package lotr.common.command.arguments;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import lotr.common.command.LOTRArgumentTypes;
import lotr.common.world.map.*;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class WaypointRegionArgument implements ArgumentType {
	private static final Collection EXAMPLES = Arrays.asList("lotr:shire", "lotr:gondor");
	public static final DynamicCommandExceptionType REGION_BAD_ID = new DynamicCommandExceptionType(arg -> new TranslationTextComponent("argument.lotr.waypointRegion.id.invalid", arg));

	@Override
	public Collection getExamples() {
		return EXAMPLES;
	}

	@Override
	public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
		return ISuggestionProvider.suggestResource(LOTRArgumentTypes.getCurrentSidedMapSettings().getWaypointRegionNames(), builder);
	}

	@Override
	public WaypointRegion parse(StringReader reader) throws CommandSyntaxException {
		MapSettings currentMap = LOTRArgumentTypes.getCurrentSidedMapSettings();
		if (currentMap == null) {
			return null;
		}
		int cursor = reader.getCursor();
		ResourceLocation name = ResourceLocation.read(reader);
		WaypointRegion region = currentMap.getWaypointRegionByName(name);
		if (region != null) {
			return region;
		}
		reader.setCursor(cursor);
		throw REGION_BAD_ID.createWithContext(reader, name.toString());
	}

	public static WaypointRegion getRegion(CommandContext context, String name) {
		return (WaypointRegion) context.getArgument(name, WaypointRegion.class);
	}

	public static WaypointRegionArgument waypointRegion() {
		return new WaypointRegionArgument();
	}
}
