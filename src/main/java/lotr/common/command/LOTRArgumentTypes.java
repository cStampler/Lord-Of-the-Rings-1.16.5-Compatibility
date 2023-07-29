package lotr.common.command;

import lotr.common.LOTRMod;
import lotr.common.command.arguments.*;
import lotr.common.fac.*;
import lotr.common.world.map.*;
import net.minecraft.command.arguments.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class LOTRArgumentTypes {
	public static FactionSettings getCurrentSidedFactionSettings() {
		return FactionSettingsManager.sidedInstance(getSafeSideToUse()).getCurrentLoadedFactions();
	}

	public static MapSettings getCurrentSidedMapSettings() {
		return MapSettingsManager.sidedInstance(getSafeSideToUse()).getCurrentLoadedMap();
	}

	protected static LogicalSide getSafeSideToUse() {
		return ServerLifecycleHooks.getCurrentServer() == null && LOTRMod.PROXY.isClient() ? LogicalSide.CLIENT : LogicalSide.SERVER;
	}

	private static void register(String name, Class cls, IArgumentSerializer serializer) {
		String namespacedName = new ResourceLocation("lotr", name).toString();
		ArgumentTypes.register(namespacedName, cls, serializer);
	}

	public static void registerTypes() {
		register("lotr_time", LOTRTimeArgument.class, new ArgumentSerializer(LOTRTimeArgument::create));
		register("waypoint_region", WaypointRegionArgument.class, new ArgumentSerializer(WaypointRegionArgument::waypointRegion));
		register("faction", FactionArgument.class, new ArgumentSerializer(FactionArgument::faction));
		register("player_message", PlayerMessageTypeArgument.class, new ArgumentSerializer(PlayerMessageTypeArgument::messageType));
	}
}
