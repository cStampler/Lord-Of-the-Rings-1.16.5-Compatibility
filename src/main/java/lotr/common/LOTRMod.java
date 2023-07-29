package lotr.common;

import java.io.*;
import java.util.NoSuchElementException;

import lotr.client.LOTRClientProxy;
import lotr.common.command.LOTRArgumentTypes;
import lotr.common.config.LOTRConfig;
import lotr.common.datafix.LOTRItemRemappings;
import lotr.common.datagen.NPCLootTableGenerator;
import lotr.common.entity.LOTREntityDataSerializers;
import lotr.common.entity.npc.data.NPCEntitySettingsManager;
import lotr.common.entity.npc.data.name.NameBankManager;
import lotr.common.event.*;
import lotr.common.fac.FactionSettingsManager;
import lotr.common.init.*;
import lotr.common.loot.functions.LOTRLootFunctions;
import lotr.common.loot.modifiers.LOTRLootModifiers;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.recipe.LOTRRecipes;
import lotr.common.resources.PostServerLoadedValidator;
import lotr.common.stat.LOTRStats;
import lotr.common.world.biome.util.LOTRBiomeJsonGenerator;
import lotr.common.world.gen.feature.LOTRFeatures;
import lotr.common.world.map.MapSettingsManager;
import lotr.common.world.spawning.NPCSpawnSettingsManager;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.packs.*;

@Mod("lotr")
public class LOTRMod {
	public static final String MOD_ID = "lotr";
	public static final LOTRProxy PROXY = DistExecutor.safeRunForDist(() -> LOTRClientProxy::new, () -> LOTRServerProxy::new);
	private static LOTREventHandler eventHandler;
	public static LOTRTickHandlerServer serverTickHandler;

	public LOTRMod() {
		LOTRLog.find();
		IEventBus fmlBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		fmlBus.register(this);
		forgeBus.register(this);
		forgeBus.addListener(this::onRegisterCommands);
		forgeBus.addListener(this::onAddDatapackListeners);
		forgeBus.addListener(this::onServerStarted);
		fmlBus.register(PROXY);
		forgeBus.register(PROXY);
		LOTRConfig.register(fmlBus);
		LOTRBlocks.register();
		LOTRItems.register();
		LOTRAttributes.register();
		LOTREntityDataSerializers.register();
		LOTREntities.register();
		LOTRTileEntities.register();
		LOTRParticles.register();
		LOTRContainers.register();
		LOTRRecipes.register();
		LOTRBiomes.register();
		LOTRFeatures.register();
		LOTRDimensions.registerAssociated();
		LOTRWorldTypes.register();
		LOTRArgumentTypes.registerTypes();
		LOTRPacketHandler.register();
		LOTRStats.setup();
		LOTRGameRules.registerAll();
		LOTRSoundEvents.register();
		LOTRLootFunctions.register();
		LOTRLootModifiers.register();
		eventHandler = new LOTREventHandler();
		serverTickHandler = new LOTRTickHandlerServer();
	}

	@SubscribeEvent
	public void missingMappings(MissingMappings event) {
		LOTRItemRemappings.handle(event);
	}

	private void onAddDatapackListeners(AddReloadListenerEvent event) {
		event.addListener(MapSettingsManager.serverInstance());
		event.addListener(FactionSettingsManager.serverInstance());
		event.addListener(NPCEntitySettingsManager.serverInstance());
		event.addListener(NPCSpawnSettingsManager.INSTANCE);
		event.addListener(NameBankManager.INSTANCE);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		LOTRCapabilities.register();
		if ((Boolean) LOTRConfig.COMMON.generateBiomeJsons.get()) {
			LOTRBiomeJsonGenerator.generateBiomeJsons();
		}

	}

	@SubscribeEvent
	public void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
		LOTREntities.registerEntityTypeAttributes(event);
	}

	@SubscribeEvent
	public void onGatherData(GatherDataEvent event) {
		if (event.includeServer()) {
			DataGenerator generator = event.getGenerator();
			generator.addProvider(new NPCLootTableGenerator(generator));
		}

	}

	private void onRegisterCommands(RegisterCommandsEvent event) {
		LOTRCommands.registerCommands(event.getDispatcher());
	}

	private void onServerStarted(FMLServerStartedEvent event) {
		ServerWorld mainWorld = event.getServer().overworld();
		PostServerLoadedValidator.validators.forEach(v -> {
			((PostServerLoadedValidator) v).performPostServerLoadValidation(mainWorld);
		});
	}

	public static InputStream getDefaultModResourceStream(ResourcePackType type, ResourceLocation res) {
		try {
			ModFileResourcePack lotrAsPack = ResourcePackLoader.getResourcePackFor("lotr").get();
			return lotrAsPack.getResource(type, res);
		} catch (NoSuchElementException var3) {
			LOTRLog.error("Error loading mod resource - resource does not exist!");
			var3.printStackTrace();
			return null;
		} catch (IOException var4) {
			LOTRLog.error("IOException loading mod resource");
			var4.printStackTrace();
			return null;
		}
	}

	public static ModContainer getModContainer() {
		return ModList.get().getModContainerById("lotr").get();
	}
}
