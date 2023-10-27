package lotr.client.event;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.ClientsideAttackTargetCache;
import lotr.client.LOTRKeyHandler;
import lotr.client.gui.map.MapPlayerLocationHolder;
import lotr.client.gui.util.AlignmentRenderer;
import lotr.client.gui.util.AlignmentTextRenderer;
import lotr.client.render.LOTRGameRenderer;
import lotr.client.render.OnScreenCompassRenderer;
import lotr.client.render.speech.ImmersiveSpeechRenderer;
import lotr.client.render.world.GeographicalWaterColors;
import lotr.client.render.world.LOTRDimensionRenderInfo;
import lotr.client.render.world.MiddleEarthCloudRenderer;
import lotr.client.render.world.MiddleEarthWorldRenderer;
import lotr.client.render.world.NorthernLightsRenderer;
import lotr.client.sound.LOTRAmbience;
import lotr.client.speech.ImmersiveSpeech;
import lotr.common.LOTRLog;
import lotr.common.config.LOTRConfig;
import lotr.common.data.LOTRLevelData;
import lotr.common.entity.capabilities.PlateFallingData;
import lotr.common.entity.capabilities.PlateFallingDataProvider;
import lotr.common.event.BeeAdjustments;
import lotr.common.event.LOTRTickHandlerServer;
import lotr.common.init.LOTRDimensions;
import lotr.common.init.LOTRParticles;
import lotr.common.time.LOTRDate;
import lotr.common.time.LOTRTime;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FogRenderer.FogType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.model.BeeModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.client.ICloudRenderHandler;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LOTRTickHandlerClient {
	private static final ResourceLocation PORTAL_OVERLAY = new ResourceLocation("lotr", "textures/overlay/portal.png");
	private static final ResourceLocation MIST_OVERLAY = new ResourceLocation("lotr", "textures/overlay/mist.png");
	public static float renderPartialTick;
	private final Minecraft mc;
	private Screen lastScreenOpen;
	private boolean inRingPortal = false;
	private int ringPortalTick;
	private final AdvancedDrunkEffect drunkEffect = new AdvancedDrunkEffect();
	private final DateDisplay dateDisplay = new DateDisplay();
	private final LOTRAmbience ambienceTicker = new LOTRAmbience();
	private final SunGlare sunGlare = new SunGlare();
	private final RainMist rainMist = new RainMist();
	private final MistyMountainsMist mountainsMist = new MistyMountainsMist();
	private final SandstormFog sandstormFog = new SandstormFog();
	private final AshfallFog ashfallFog = new AshfallFog();
	private final WorldTypeHelpDisplay worldTypeHelpDisplay = new WorldTypeHelpDisplay();
	private final NorthernLightsRenderer northernLights = new NorthernLightsRenderer();
	private final AlignmentRenderer alignmentRenderer = new AlignmentRenderer(AlignmentTextRenderer.newGUIRenderer());
	private final OnScreenCompassRenderer compassRenderer = new OnScreenCompassRenderer();

	public LOTRTickHandlerClient(Minecraft mc) {
		this.mc = mc;
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void displayAlignmentDrain(int numFactions) {
		alignmentRenderer.displayAlignmentDrain(numFactions);
	}

	public void displayNewDate() {
		dateDisplay.displayNewDate();
	}

	public float getCurrentSandstormFogStrength() {
		return sandstormFog.getWeatherFogStrength(renderPartialTick);
	}

	@SubscribeEvent
	public void getItemTooltip(ItemTooltipEvent event) {
		ItemTooltipFeatures.handleTooltipEvent(event);
	}

	private boolean isOutOfWorldMenuScreen(Screen screen) {
		return screen instanceof MainMenuScreen || screen instanceof MultiplayerScreen;
	}

	@SubscribeEvent
	public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (entity.level.isClientSide && entity instanceof LivingEntity) {
			event.addCapability(PlateFallingDataProvider.KEY, new PlateFallingDataProvider());
		}
	}

	@SubscribeEvent
	public void onCameraSetup(CameraSetup event) {
		drunkEffect.handle(event);
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		Phase phase = event.phase;
		ClientPlayerEntity player = mc.player;
		Entity viewer = mc.cameraEntity;
		if (phase == Phase.START) {
			replaceGameRendererIfNotReplaced();
			replaceWorldRendererIfNotReplaced();
			if (player != null) {
				GeographicalWaterColors.updateGeographicalWaterColorInBiomes(mc);
			}
		}

		if (phase == Phase.END) {
			if (player != null) {
				ClientWorld world = (ClientWorld) player.level;
				alignmentRenderer.updateHUD(mc, player);
				LOTRKeyHandler.updateAlignmentChange();
				if (!mc.isPaused()) {
					if (LOTRDimensions.isModDimension(world)) {
						LOTRTime.updateTime(world);
						ICloudRenderHandler clouds = DimensionRenderInfo.forType(world.dimensionType()).getCloudRenderHandler();
						if (clouds instanceof MiddleEarthCloudRenderer) {
							((MiddleEarthCloudRenderer) clouds).updateClouds(world);
						}
					}

					if ((Boolean) LOTRConfig.CLIENT.northernLights.get()) {
						northernLights.update(viewer);
					}

					if ((Boolean) LOTRConfig.CLIENT.immersiveSpeech.get()) {
						ImmersiveSpeech.update();
					}

					if (viewer instanceof LivingEntity) {
						drunkEffect.update((LivingEntity) viewer);
					}

					dateDisplay.update();
					ambienceTicker.updateAmbience(mc, world, player);
					sunGlare.update(world, viewer);
					rainMist.update(world, viewer);
					mountainsMist.update(world, viewer);
					sandstormFog.update(world, viewer);
					ashfallFog.update(world, viewer);
					this.spawnExtraEnvironmentParticles(world, viewer);
					if (inRingPortal) {
						boolean stillInPortal = LOTRTickHandlerServer.checkInRingPortal(player);
						if (stillInPortal) {
							++ringPortalTick;
						} else {
							inRingPortal = false;
							ringPortalTick = 0;
						}
					}
				}
			}

			Screen screen = mc.screen;
			if (screen != null) {
				if (isOutOfWorldMenuScreen(screen) && !isOutOfWorldMenuScreen(lastScreenOpen)) {
					LOTRLevelData.serverInstance().resetNeedsLoad();
					LOTRLevelData.clientInstance().resetNeedsLoad();
					LOTRTime.resetNeedsLoad();
					LOTRDate.resetWorldTimeInMenu();
					MapPlayerLocationHolder.clearPlayerLocations();
					ClientsideAttackTargetCache.clearAll();
					GeographicalWaterColors.resetInMenu();
					ImmersiveSpeech.clearAll();
					drunkEffect.reset();
					dateDisplay.reset();
					sunGlare.reset();
					rainMist.reset();
					mountainsMist.reset();
					sandstormFog.reset();
					ashfallFog.reset();
					alignmentRenderer.resetInMenu();
				}

				lastScreenOpen = screen;
			}
		}

	}

	@SubscribeEvent
	public void onFogColors(FogColors event) {
		sandstormFog.modifyFogColors(event, renderPartialTick);
		ashfallFog.modifyFogColors(event, renderPartialTick);
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World world = entity.level;
		if (world.isClientSide) {
			entity.getCapability(PlateFallingDataProvider.CAPABILITY).ifPresent(plateFalling -> {
				if (!((PlateFallingData) plateFalling).isEntitySet()) {
					((PlateFallingData) plateFalling).setEntity(entity);
				}
				((PlateFallingData) plateFalling).update();
			});
		}

	}

	@SubscribeEvent
	public void onPreRenderGameOverlay(Pre event) {
		World world = mc.level;
		Entity viewer = mc.cameraEntity;
		float partialTick = event.getPartialTicks();
		if (world != null && viewer != null && event.getType() == ElementType.HELMET) {
			float sunGlareBrightness = sunGlare.getGlareBrightness(partialTick);
			if (sunGlareBrightness > 0.0F && mc.options.getCameraType() == PointOfView.FIRST_PERSON) {
				sunGlareBrightness *= 1.0F;
				renderColoredOverlay(sunGlare.getGlareColorRGB(), sunGlareBrightness);
			}

			float mountainsMistFactor;
			if (inRingPortal) {
				mountainsMistFactor = ringPortalTick / 100.0F;
				mountainsMistFactor = Math.min(mountainsMistFactor, 1.0F);
				renderTexturedOverlay(PORTAL_OVERLAY, 0.1F + mountainsMistFactor * 0.6F);
			}

			mountainsMistFactor = mountainsMist.getCurrentMistFactor(viewer, partialTick);
			if (mountainsMistFactor > 0.0F) {
				renderTexturedOverlay(MIST_OVERLAY, mountainsMistFactor * 0.75F);
			}
		}

	}

	@SubscribeEvent
	public void onPreRenderLiving(RenderLivingEvent.Pre<BeeEntity, BeeModel<BeeEntity>> event) {
		LivingEntity entity = event.getEntity();
		World world = entity.level;
		MatrixStack matStack = event.getMatrixStack();
		if (BeeAdjustments.shouldApply(entity, world)) {
			float scale = 0.35F;
			matStack.scale(scale, scale, scale);
		}

	}

	@SubscribeEvent
	public void onRenderFog(RenderFogEvent event) {
		Minecraft.getInstance();
		Entity viewer = event.getInfo().getEntity();
		World world = viewer.level;
		DimensionType dimension = world.dimensionType();
		world.getBiome(viewer.blockPosition());
		float farPlane = event.getFarPlaneDistance();
		FogType fogType = event.getType();
		DimensionRenderInfo dimRenderInfo = DimensionRenderInfo.forType(dimension);
		if (dimRenderInfo instanceof LOTRDimensionRenderInfo) {
			LOTRDimensionRenderInfo lDimRenderInfo = (LOTRDimensionRenderInfo) dimRenderInfo;
			float[] fogStartEnd = lDimRenderInfo.modifyFogIntensity(farPlane, fogType, viewer);
			float fogStart = fogStartEnd[0];
			float fogEnd = fogStartEnd[1];
			float rain = rainMist.getRainMistStrength(renderPartialTick);
			float mountainsMistFactor;
			float sandFog;
			if (rain > 0.0F) {
				mountainsMistFactor = 0.95F;
				sandFog = 0.2F;
				fogStart -= fogStart * rain * mountainsMistFactor;
				fogEnd -= fogEnd * rain * sandFog;
			}

			mountainsMistFactor = mountainsMist.getCurrentMistFactor(viewer, renderPartialTick);
			float ashFog;
			if (mountainsMistFactor > 0.0F) {
				sandFog = 0.95F;
				ashFog = 0.7F;
				fogStart -= fogStart * mountainsMistFactor * sandFog;
				fogEnd -= fogEnd * mountainsMistFactor * ashFog;
			}

			sandFog = sandstormFog.getWeatherFogStrength(renderPartialTick);
			float ashOpacityStart;
			if (sandFog > 0.0F) {
				ashFog = 0.99F;
				ashOpacityStart = 0.75F;
				fogStart -= fogStart * sandFog * ashFog;
				fogEnd -= fogEnd * sandFog * ashOpacityStart;
			}

			ashFog = ashfallFog.getWeatherFogStrength(renderPartialTick);
			if (ashFog > 0.0F) {
				ashOpacityStart = 0.95F;
				float ashOpacityEnd = 0.6F;
				fogStart -= fogStart * ashFog * ashOpacityStart;
				fogEnd -= fogEnd * ashFog * ashOpacityEnd;
			}

			RenderSystem.fogStart(fogStart);
			RenderSystem.fogEnd(fogEnd);
		}

	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		Phase phase = event.phase;
		PlayerEntity player = mc.player;
		if (phase == Phase.START) {
			renderPartialTick = event.renderTickTime;
		}

		if (phase == Phase.END) {
			Screen gui = mc.screen;
			if (player != null) {
				World world = player.level;
				boolean guiEnabled = Minecraft.renderNames();
				boolean isModDimension = LOTRDimensions.isModDimension(world);
				MatrixStack matStack;
				if (guiEnabled && (isModDimension || (Boolean) LOTRConfig.CLIENT.showAlignmentEverywhere.get())) {
					matStack = new MatrixStack();
					alignmentRenderer.renderAlignmentHUDBar(matStack, mc, player, renderPartialTick);
				}

				if (guiEnabled && gui == null) {
					if (isModDimension && (Boolean) LOTRConfig.CLIENT.compass.get() && !mc.options.renderDebug) {
						compassRenderer.renderCompassAndInformation(mc, player, world, renderPartialTick);
					}

					if (LOTRDimensions.isDimension(world, LOTRDimensions.MIDDLE_EARTH_WORLD_KEY)) {
						matStack = new MatrixStack();
						dateDisplay.render(matStack, mc);
					}
				}
			}
		}

	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getInstance();
		ClientWorld world = mc.level;
		float tick = event.getPartialTicks();
		MatrixStack matStack = event.getMatrixStack();
		if ((Boolean) LOTRConfig.CLIENT.northernLights.get() && LOTRDimensions.isDimension(world, LOTRDimensions.MIDDLE_EARTH_WORLD_KEY)) {
			northernLights.render(mc, world, matStack, tick);
		}

		if ((Boolean) LOTRConfig.CLIENT.immersiveSpeech.get() && Minecraft.renderNames() && mc.options.getCameraType() == PointOfView.FIRST_PERSON) {
			world.getProfiler().push("lotrImmersiveSpeech");
			ImmersiveSpeechRenderer.renderAllSpeeches(mc, world, matStack, tick);
			world.getProfiler().pop();
		}

	}

	@SubscribeEvent
	public void onWorldLoad(Load event) {
		IWorld world = event.getWorld();
		if (world instanceof ClientWorld) {
			ClientWorldDimensionTypeHelper.fixDimensionType((ClientWorld) world);
		}

	}

	private void renderColoredOverlay(float[] rgb, float alpha) {
		renderOverlay((ResourceLocation) null, rgb, alpha);
	}

	private void renderOverlay(@Nullable ResourceLocation texture, @Nullable float[] rgb, float alpha) {
		MainWindow mainWindow = mc.getWindow();
		int width = mainWindow.getGuiScaledWidth();
		int height = mainWindow.getGuiScaledHeight();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		if (rgb != null) {
			RenderSystem.color4f(rgb[0], rgb[1], rgb[2], alpha);
		} else {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		}

		if (texture != null) {
			mc.getTextureManager().bind(texture);
		} else {
			RenderSystem.disableTexture();
		}

		double depth = -90.0D;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuilder();
		buf.begin(7, DefaultVertexFormats.POSITION_TEX);
		buf.vertex(0.0D, height, depth).uv(0.0F, 1.0F).endVertex();
		buf.vertex(width, height, depth).uv(1.0F, 1.0F).endVertex();
		buf.vertex(width, 0.0D, depth).uv(1.0F, 0.0F).endVertex();
		buf.vertex(0.0D, 0.0D, depth).uv(0.0F, 0.0F).endVertex();
		tess.end();
		if (texture == null) {
			RenderSystem.enableTexture();
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.enableAlphaTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderTexturedOverlay(ResourceLocation texture, float alpha) {
		renderOverlay(texture, (float[]) null, alpha);
	}

	private void replaceGameRendererIfNotReplaced() {
		replaceFinalMinecraftFieldIfNotReplaced(mc, GameRenderer.class, LOTRGameRenderer.class, mc -> ((Minecraft) mc).gameRenderer, () -> new LOTRGameRenderer(mc, mc.getResourceManager(), mc.renderBuffers()), "GameRenderer");
	}

	private void replaceWorldRendererIfNotReplaced() {
		replaceFinalMinecraftFieldIfNotReplaced(mc, WorldRenderer.class, MiddleEarthWorldRenderer.class, mc -> ((Minecraft) mc).levelRenderer, () -> new MiddleEarthWorldRenderer(mc, mc.renderBuffers()), "WorldRenderer");
	}

	public void setInRingPortal(Entity entity) {
		if (entity == mc.player) {
			inRingPortal = true;
		}

	}

	private void spawnExtraEnvironmentParticles(World world, Entity viewer) {
		world.getProfiler().push("lotrEnvironmentFX");
		Mutable movingParticlePos = new Mutable();

		for (int l = 0; l < 667; ++l) {
			this.spawnExtraEnvironmentParticles(world, viewer, 16, movingParticlePos);
			this.spawnExtraEnvironmentParticles(world, viewer, 32, movingParticlePos);
		}

		world.getProfiler().pop();
	}

	private void spawnExtraEnvironmentParticles(World world, Entity viewer, int range, Mutable movingPos) {
		BlockPos playerPos = viewer.blockPosition();
		Random rand = world.random;
		int x = playerPos.getX() + rand.nextInt(range) - rand.nextInt(range);
		int y = playerPos.getY() + rand.nextInt(range) - rand.nextInt(range);
		int z = playerPos.getZ() + rand.nextInt(range) - rand.nextInt(range);
		movingPos.set(x, y, z);
		BlockState blockState = world.getBlockState(movingPos);
		/*if (blockState.getMaterial() == Material.WATER) {
			LOTRBiomeWrapper biome = LOTRBiomes.getWrapperFor(world.getBiome(movingPos), world);
			if (biome instanceof MirkwoodBiome && rand.nextInt(20) == 0) {
				world.addParticle((IParticleData) LOTRParticles.MIRKWOOD_WATER_EFFECT.get(), (double) (x + rand.nextFloat()), y + 0.75D, (double) (z + rand.nextFloat()), 0.0D, 0.05D, 0.0D);
			}

			if (biome instanceof MorgulValeBiome && rand.nextInt(40) == 0) {
				world.addParticle((IParticleData) LOTRParticles.MORGUL_WATER_EFFECT.get(), (double) (x + rand.nextFloat()), y + 0.75D, (double) (z + rand.nextFloat()), 0.0D, 0.05D, 0.0D);
			}
		}*/

		if (blockState.getMaterial() == Material.WATER && !world.getFluidState(movingPos).isSource()) {
			BlockPos belowPos = movingPos.below();
			BlockState below = world.getBlockState(belowPos);
			if (below.getMaterial() == Material.WATER) {
				int waterRange = 1;
				Mutable waterMovingPos = new Mutable();

				for (int i = -waterRange; i <= waterRange; ++i) {
					for (int k = -waterRange; k <= waterRange; ++k) {
						waterMovingPos.set(x + i, y - 1, z + k);
						BlockState adjBlock = world.getBlockState(waterMovingPos);
						if (adjBlock.getMaterial() == Material.WATER && world.getFluidState(waterMovingPos).isSource() && world.isEmptyBlock(waterMovingPos.above())) {
							for (int l = 0; l < 2; ++l) {
								double px = x + 0.5D + i * rand.nextFloat();
								double py = y + rand.nextFloat() * 0.2F;
								double pz = z + 0.5D + k * rand.nextFloat();
								double speed = MathHelper.nextDouble(rand, 0.03D, 0.07D);
								world.addParticle((IParticleData) LOTRParticles.WATERFALL.get(), px, py, pz, 0.0D, speed, 0.0D);
							}
						}
					}
				}
			}
		}

	}

	private static <T, U extends T> void replaceFinalMinecraftFieldIfNotReplaced(Minecraft mc, Class<T> baseClass, Class<U> subClass, Function<Minecraft, T> getter, Supplier<U> newSubClassSupplier, String loggingNameOfField) {
		T object = (T) getter.apply(mc);
		if (!subClass.isAssignableFrom(object.getClass())) {
			U newSubClassObject = newSubClassSupplier.get();
			Optional<Field> opt_mcField = Stream.<Field>of(Minecraft.class.getDeclaredFields()).filter(f -> (f.getType() == baseClass)).findFirst();
			if (!opt_mcField.isPresent()) {
				LOTRLog.error("Could not locate %s field in Minecraft game class", new Object[] { loggingNameOfField });
			} else {
				Field mcField = opt_mcField.get();
				LOTRUtil.unlockFinalField(mcField);
				try {
					mcField.set(mc, newSubClassObject);
					if (newSubClassObject instanceof IFutureReloadListener) {
						IReloadableResourceManager resMgr = (IReloadableResourceManager) mc.getResourceManager();
						IFutureReloadListener objectAsListener = (IFutureReloadListener) newSubClassObject;
						resMgr.registerReloadListener(objectAsListener);
						if (objectAsListener instanceof IResourceManagerReloadListener)
							((IResourceManagerReloadListener) objectAsListener)
									.onResourceManagerReload((IResourceManager) resMgr);
					}
					LOTRLog.info("Successfully replaced %s instance with subclass instance",
							new Object[] { loggingNameOfField });
				} catch (IllegalArgumentException | IllegalAccessException e) {
					LOTRLog.error("Failed to set new %s", new Object[] { loggingNameOfField });
					e.printStackTrace();
				}
			}
		}
	}
}
