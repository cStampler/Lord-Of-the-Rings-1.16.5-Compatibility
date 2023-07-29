package lotr.client.gui;

import java.util.*;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.LOTRClientProxy;
import lotr.client.gui.map.*;
import lotr.client.util.LOTRClientUtil;
import lotr.common.init.LOTRSoundEvents;
import lotr.common.util.LOTRUtil;
import lotr.common.world.map.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.*;
import net.minecraft.util.text.*;

public class FastTravelScreen extends BasicIngameScreen {
	private static final ResourceLocation FAST_TRAVEL_QUOTES = new ResourceLocation("lotr", "fast_travel");
	private MiddleEarthMapScreen mapGui;
	private MiddleEarthMapRenderer mapRenderer;
	private int tickCounter;
	private Waypoint theWaypoint;
	private boolean chunkLoaded = false;
	private boolean playedSound = false;
	private ITextComponent loadingQuote;
	private final float zoomBase;
	private final double mapScaleFactor;
	private float currentZoom;
	private float prevZoom;
	private boolean finishedZoomIn = false;
	private double mapSpeed;
	private double mapVelX;
	private double mapVelY;
	private boolean reachedWP = false;

	public FastTravelScreen(Waypoint waypoint, int x, int z) {
		super(new StringTextComponent("TODO - FAST TRAVEL"));
		theWaypoint = waypoint;
		int startX = x;
		int startZ = z;
		loadingQuote = LOTRClientProxy.getQuoteListLoader().getRandomQuoteComponent(FAST_TRAVEL_QUOTES);
		mapGui = new MiddleEarthMapScreen();
		mapRenderer = new MiddleEarthMapRenderer(true, true);
		MapSettings mapSettings = MapSettingsManager.clientInstance().getCurrentLoadedMap();
		mapRenderer.setInstantaneousPosition(mapSettings.worldToMapX_frac(startX), mapSettings.worldToMapZ_frac(startZ));
		double dx = theWaypoint.getMapX() - mapRenderer.getMapX();
		double dy = theWaypoint.getMapZ() - mapRenderer.getMapY();
		double distSq = dx * dx + dy * dy;
		double dist = Math.sqrt(distSq);
		mapScaleFactor = dist / 100.0D;
		zoomBase = -((float) (Math.log(mapScaleFactor * 0.30000001192092896D) / Math.log(2.0D)));
		currentZoom = prevZoom = zoomBase + 0.5F;
		mapRenderer.setStableZoom((float) Math.pow(2.0D, zoomBase));
	}

	@Override
	public void init(Minecraft mc, int i, int j) {
		super.init(mc, i, j);
		mapGui.init(mc, i, j);
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float f) {
		RenderSystem.enableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		f = Math.min(f, 1.0F);
		mapRenderer.setZoomExp(prevZoom + (currentZoom - prevZoom) * f);
		mapRenderer.renderMap(matStack, this, mapGui, f);
		mapRenderer.renderVignettes(matStack, this, getBlitOffset(), 4);
		RenderSystem.enableBlend();
		ITextComponent title = new TranslationTextComponent("gui.lotr.fasttravel.travel", theWaypoint.getDisplayName());
		int numEllipses = tickCounter / 10 % 4;
		String titleEllipsis = new String(new char[numEllipses]).replace("\u0000", ".");
		ITextComponent fullTitle = new TranslationTextComponent("%s%s", title, titleEllipsis);
		List loadingQuoteLines = font.split(loadingQuote, width - 100);
		float boxAlpha = 0.5F;
		int boxColor = LOTRClientUtil.getRGBA(0, boxAlpha);
		font.getClass();
		int fh = 9;
		int border = fh * 2;
		if (chunkLoaded) {
			fill(matStack, 0, 0, width, 0 + border + fh * 3 + border, boxColor);
		} else {
			fill(matStack, 0, 0, width, 0 + border + fh + border, boxColor);
		}

		int messageY = height - border - loadingQuoteLines.size() * fh;
		fill(matStack, 0, messageY - border, width, height, boxColor);
		RenderSystem.disableBlend();
		font.drawShadow(matStack, fullTitle, width / 2 - font.width(title) / 2, 0 + border, 16777215);

		for (Iterator var15 = loadingQuoteLines.iterator(); var15.hasNext(); messageY += fh) {
			IReorderingProcessor line = (IReorderingProcessor) var15.next();
			font.drawShadow(matStack, line, width / 2 - font.width(line) / 2, messageY, 16777215);
		}

		if (chunkLoaded) {
			ITextComponent skipText = new TranslationTextComponent("gui.lotr.fasttravel.skip", minecraft.options.keyInventory.getTranslatedKeyMessage());
			float skipAlpha = LOTRUtil.normalisedTriangleWave(tickCounter + f, 160.0F, 0.3F, 1.0F);
			int skipColor = LOTRClientUtil.getRGBAForFontRendering(16777215, skipAlpha);
			RenderSystem.enableBlend();
			font.draw(matStack, skipText, width / 2 - font.width(skipText) / 2, 0 + border + fh * 2, skipColor);
		}

		RenderSystem.disableBlend();
		super.render(matStack, mouseX, mouseY, f);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return chunkLoaded;
	}

	@Override
	public void tick() {
		super.tick();
		if (!chunkLoaded && LOTRClientUtil.doesClientChunkExist(minecraft.level, theWaypoint.getWorldX(), theWaypoint.getWorldZ())) {
			chunkLoaded = true;
		}

		if (!playedSound) {
			minecraft.getSoundManager().play(SimpleSound.forAmbientAddition(LOTRSoundEvents.FAST_TRAVEL));
			playedSound = true;
		}

		mapRenderer.tick();
		++tickCounter;
		prevZoom = currentZoom;
		if (!reachedWP) {
			double dx = theWaypoint.getMapX() - mapRenderer.getMapX();
			double dy = theWaypoint.getMapZ() - mapRenderer.getMapY();
			double distSq = dx * dx + dy * dy;
			double dist = Math.sqrt(distSq);
			if (dist <= 1.0D * mapScaleFactor) {
				reachedWP = true;
				mapSpeed = 0.0D;
				mapVelX = 0.0D;
				mapVelY = 0.0D;
			} else {
				mapSpeed += 0.009999999776482582D;
				mapSpeed = Math.min(mapSpeed, 2.0D);
				double vXNew = dx / dist * mapSpeed;
				double vYNew = dy / dist * mapSpeed;
				double a = 0.20000000298023224D;
				mapVelX += (vXNew - mapVelX) * a;
				mapVelY += (vYNew - mapVelY) * a;
			}

			mapRenderer.moveBy(mapVelX * mapScaleFactor, mapVelY * mapScaleFactor);
			currentZoom -= 0.008333334F;
			currentZoom = Math.max(currentZoom, zoomBase);
		} else {
			currentZoom += 0.008333334F;
			currentZoom = Math.min(currentZoom, zoomBase + 0.5F);
			if (currentZoom >= zoomBase + 0.5F) {
				finishedZoomIn = true;
			}
		}

		if (chunkLoaded && reachedWP && finishedZoomIn) {
			minecraft.setScreen((Screen) null);
		}

	}
}
