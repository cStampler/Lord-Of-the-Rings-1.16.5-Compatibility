package lotr.client;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import it.unimi.dsi.fastutil.objects.*;
import lotr.client.align.NotifyAlignmentRequirement;
import lotr.client.event.*;
import lotr.client.gui.*;
import lotr.client.gui.inv.ContainerScreenHelper;
import lotr.client.gui.map.*;
import lotr.client.render.*;
import lotr.client.render.entity.LOTREntityRenderers;
import lotr.client.render.model.*;
import lotr.client.render.model.connectedtex.ConnectedTextureUnbakedModel;
import lotr.client.render.model.scatter.ScatterUnbakedModel;
import lotr.client.render.model.vessel.VesselDrinkUnbakedModel;
import lotr.client.render.player.LOTRPlayerRendering;
import lotr.client.speech.*;
import lotr.client.text.QuoteListLoader;
import lotr.common.LOTRServerProxy;
import lotr.common.block.LOTRSignTypes;
import lotr.common.data.PlayerMessageType;
import lotr.common.entity.item.RingPortalEntity;
import lotr.common.entity.misc.AlignmentBonusEntity;
import lotr.common.init.LOTRDimensions;
import lotr.common.inv.*;
import lotr.common.item.LOTRItemProperties;
import lotr.common.network.*;
import lotr.common.util.LOTRUtil;
import lotr.common.world.map.*;
import net.minecraft.block.WoodType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.world.*;
import net.minecraft.entity.*;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.ColorHandlerEvent.*;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class LOTRClientProxy extends LOTRServerProxy {
	private static final Minecraft MC = Minecraft.getInstance();
	public static final int MAX_LIGHTMAP = LightTexture.pack(15, 15);
	private static QuoteListLoader QUOTE_LIST_LOADER;
	private static SpeechbankResourceManager SPEECHBANK_RESOURCE_MANAGER;
	public static Object2ObjectMap<ResourceLocation, DimensionRenderInfo> EFFECTS = Util.make(new Object2ObjectArrayMap<>(), p_239214_0_ -> {
		DimensionRenderInfo.Overworld dimensionrenderinfo$overworld = new DimensionRenderInfo.Overworld();
		p_239214_0_.defaultReturnValue(dimensionrenderinfo$overworld);
		p_239214_0_.put(DimensionType.OVERWORLD_EFFECTS, dimensionrenderinfo$overworld);
		p_239214_0_.put(DimensionType.NETHER_EFFECTS, new DimensionRenderInfo.Nether());
		p_239214_0_.put(DimensionType.END_EFFECTS, new DimensionRenderInfo.End());
	});
	private LOTRTickHandlerClient clientTickHandler;
	private LOTRGuiHandler guiHandler;
	private LOTRKeyHandler keyHandler;

	private LOTRPlayerRendering specialPlayerRendering;

	@Override
	public void displayAdoptCustomWaypointScreen(CustomWaypoint waypoint, String createdPlayerName) {
		MC.setScreen(new AdoptCustomWaypointScreen(waypoint, createdPlayerName));
	}

	@Override
	public void displayAlignmentDrain(int numFactions) {
		clientTickHandler.displayAlignmentDrain(numFactions);
	}

	@Override
	public void displayFastTravelScreen(Waypoint waypoint, int startX, int startZ) {
		MC.setScreen(new FastTravelScreen(waypoint, startX, startZ));
	}

	@Override
	public void displayMessageType(PlayerMessageType messageType, boolean isCommandSent, String customText) {
		MC.setScreen(new PlayerMessageScreen(messageType, isCommandSent, customText));
	}

	@Override
	public void displayNewDate() {
		clientTickHandler.displayNewDate();
	}

	@Override
	public void displayPacketOpenScreen(SPacketOpenScreen.Type type) {
		if (type == SPacketOpenScreen.Type.CREATE_CUSTOM_WAYPOINT) {
			MC.setScreen(new CreateCustomWaypointScreen());
		}

	}

	@Override
	public void displayUpdateCustomWaypointScreen(CustomWaypoint waypoint) {
		MC.setScreen(new UpdateCustomWaypointScreen(waypoint));
	}

	@Override
	public void displayViewAdoptedCustomWaypointScreen(AdoptedCustomWaypoint waypoint, String createdPlayerName) {
		MC.setScreen(new ViewAdoptedCustomWaypointScreen(waypoint, createdPlayerName));
	}

	@Override
	public ClientPlayerEntity getClientPlayer() {
		return MC.player;
	}

	@Override
	public World getClientWorld() {
		return MC.level;
	}

	@Override
	public float getCurrentSandstormFogStrength() {
		return clientTickHandler.getCurrentSandstormFogStrength();
	}

	@Override
	public File getGameRootDirectory() {
		return MC.gameDirectory;
	}

	@Override
	public Optional getSidedAttackTarget(MobEntity entity) {
		return !entity.level.isClientSide ? super.getSidedAttackTarget(entity) : ClientsideAttackTargetCache.getAttackTarget(entity);
	}

	@Override
	public boolean isClient() {
		return EffectiveSide.get() == LogicalSide.CLIENT;
	}

	@Override
	public boolean isSingleplayer() {
		return MC.hasSingleplayerServer();
	}

	@Override
	public void mapHandleIsOp(boolean isOp) {
		Screen screen = MC.screen;
		if (screen instanceof MiddleEarthMapScreen) {
			((MiddleEarthMapScreen) screen).receiveIsOp(isOp);
		}

	}

	@Override
	public void mapHandlePlayerLocations(List playerLocations) {
		MapPlayerLocationHolder.refreshPlayerLocations(playerLocations);
	}

	@SubscribeEvent
	public void onBlockColors(Block event) {
		BlockRenderHelper.setupBlockColors(event);
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		clientTickHandler = new LOTRTickHandlerClient(MC);
		guiHandler = new LOTRGuiHandler(MC);
		keyHandler = new LOTRKeyHandler();
		specialPlayerRendering = new LOTRPlayerRendering(MC);
		QUOTE_LIST_LOADER = new QuoteListLoader(MC);
		SPEECHBANK_RESOURCE_MANAGER = new SpeechbankResourceManager(MC);
		BlockRenderHelper.setupBlocks();
		LOTRSignTypes.forEach(hummel -> Atlases.addWoodType((WoodType) hummel));
		LOTRItemProperties.registerProperties();
		try {
			Field effects = ObfuscationReflectionHelper.findField(DimensionRenderInfo.class, "EFFECTS");
			LOTRUtil.unlockFinalField(effects);
			effects.set(LOTRDimensions.MIDDLE_EARTH_ID, EFFECTS);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		ContainerScreenHelper.registerScreens();
		LOTREntityRenderers.registerEntityRenderers();
		LOTREntityRenderers.registerTileEntityRenderers();
	}

	@SubscribeEvent
	public void onItemColors(Item event) {
		BlockRenderHelper.setupItemColors(event);
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		HandheldItemModels.INSTANCE.onModelBake(event);
	}

	@SubscribeEvent
	public void onModelRegistry(ModelRegistryEvent event) {
		HandheldItemModels.INSTANCE.setupAndDetectModels(MC);
		PlateFoodModels.INSTANCE.setupAndLoadModels(MC);
		ModelLoaderRegistry.registerLoader(new ResourceLocation("lotr", "connected_textures"), ConnectedTextureUnbakedModel.Loader.INSTANCE);
		ModelLoaderRegistry.registerLoader(new ResourceLocation("lotr", "vessel_drink"), VesselDrinkUnbakedModel.Loader.INSTANCE);
		ModelLoaderRegistry.registerLoader(new ResourceLocation("lotr", "scatter"), ScatterUnbakedModel.Loader.INSTANCE);
	}

	@SubscribeEvent
	public void onParticleRegistry(ParticleFactoryRegisterEvent event) {
		LOTRClientParticles.register(event);
	}

	@SubscribeEvent
	public void onTextureStitchedPre(Pre event) {
		AtlasTexture atlas = event.getMap();
		if (atlas.location() == PlayerContainer.BLOCK_ATLAS) {
			event.addSprite(KegSlot.EMPTY_BUCKET_TEXTURE);
			event.addSprite(KegResultSlot.EMPTY_MUG_TEXTURE);
		}
	}

	@Override
	public void receiveClientAttackTarget(SPacketSetAttackTarget packet) {
		ClientsideAttackTargetCache.receivePacket(packet);
	}

	@Override
	public void receiveNotifyAlignRequirementPacket(SPacketNotifyAlignRequirement packet) {
		NotifyAlignmentRequirement.displayMessage(getClientPlayer(), packet);
	}

	@Override
	public void receiveSpeechbankPacket(SPacketSpeechbank packet) {
		NPCSpeechReceiver.receiveSpeech(getClientWorld(), getClientPlayer(), packet);
	}

	@Override
	public void setInRingPortal(Entity entity, RingPortalEntity portal) {
		if (!entity.level.isClientSide) {
			super.setInRingPortal(entity, portal);
		} else {
			clientTickHandler.setInRingPortal(entity);
		}

	}

	@Override
	public void spawnAlignmentBonus(SPacketAlignmentBonus packet) {
		ClientWorld world = MC.level;
		AlignmentBonusEntity entity = AlignmentBonusEntity.createBonusEntityForClientSpawn(world, packet.entityId, packet.source, packet.mainFaction, packet.prevMainAlignment, packet.factionBonusMap, packet.conquestBonus, packet.pos);
		world.putNonPlayerEntity(entity.getId(), entity);
	}

	public static QuoteListLoader getQuoteListLoader() {
		return QUOTE_LIST_LOADER;
	}

	public static SpeechbankResourceManager getSpeechbankResourceManager() {
		return SPEECHBANK_RESOURCE_MANAGER;
	}
}
