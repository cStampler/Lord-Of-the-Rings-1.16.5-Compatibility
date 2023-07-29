package lotr.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.map.*;
import lotr.common.init.LOTRDimensions;
import lotr.common.world.map.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

public class LOTRLoadingTerrainScreen extends Screen {
	private static final ITextComponent ENTERING_DIMENSION = new TranslationTextComponent("gui.lotr.loading.enter_middle_earth");
	private final Screen parentScreen;
	private final MiddleEarthMapScreen mapGui;
	private final MiddleEarthMapRenderer mapRenderer;

	public LOTRLoadingTerrainScreen(Screen parent) {
		super(NarratorChatListener.NO_TITLE);
		parentScreen = parent;
		mapGui = new MiddleEarthMapScreen();
		mapRenderer = new MiddleEarthMapRenderer(true, false);
		mapRenderer.setZoomExp(-0.3F);
		mapRenderer.setStableZoom((float) Math.pow(2.0D, -0.30000001192092896D));
	}

	@Override
	public void init(Minecraft mc, int w, int h) {
		super.init(mc, w, h);
		parentScreen.init(mc, w, h);
		mapGui.init(mc, w, h);
	}

	@Override
	public boolean isPauseScreen() {
		return parentScreen.isPauseScreen();
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float tick) {
		World world = minecraft.level;
		PlayerEntity player = minecraft.player;
		System.out.println(world + ", " + (world == null ? null : world.dimension().location()));
		if (world != null && LOTRDimensions.isDimension(world, LOTRDimensions.MIDDLE_EARTH_WORLD_KEY)) {
			tick = minecraft.getFrameTime();
			renderDirtBackground(0);
			RenderSystem.enableAlphaTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			MapSettings mapSettings = MapSettingsManager.clientInstance().getCurrentLoadedMap();
			mapRenderer.setInstantaneousPosition(mapSettings.worldToMapX(player.getX()), mapSettings.worldToMapX(player.getZ()));
			int x0 = 0;
			int x1 = width;
			int y0 = 40;
			int y1 = height - 40;
			mapRenderer.renderMap(matStack, this, mapGui, tick, x0, y0, x1, y1);
			mapRenderer.renderVignette(matStack, this, getBlitOffset(), x0, y0, x1, y1);
			RenderSystem.disableBlend();
			drawCenteredString(matStack, font, ENTERING_DIMENSION, width / 2, height / 2 - 50, 16777215);
		} else {
			parentScreen.render(matStack, mouseX, mouseY, tick);
		}

	}

	@Override
	public boolean shouldCloseOnEsc() {
		return parentScreen.shouldCloseOnEsc();
	}

	@Override
	public void tick() {
		parentScreen.tick();
	}
}
