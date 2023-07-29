package lotr.client.gui;

import java.util.*;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.*;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.map.*;
import lotr.client.gui.widget.button.RedBookButton;
import lotr.common.world.map.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.BrandingControl;

public class LOTRMainMenuScreen extends MainMenuScreen {
	private static final ResourceLocation TITLE_TEXTURE = new ResourceLocation("textures/gui/title/minecraft.png");
	private static final ResourceLocation TITLE_EDITION = new ResourceLocation("textures/gui/title/edition.png");
	private static final ResourceLocation PANORAMA_OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
	private static boolean isFirstMenu = true;
	private static final IFormattableTextComponent MOD_TITLE = new TranslationTextComponent("lotr.menu.title");
	private static final IFormattableTextComponent MOD_SUBTITLE;
	private static MiddleEarthMapRenderer mapRenderer;
	private static int tickCounter;
	private static Random rand;
	private static List waypointRoute;
	private static int currentWPIndex;
	private static boolean randomWPStart;
	private static float mapSpeed;
	private static float mapVelX;
	private static float mapVelY;
	static {
		MOD_SUBTITLE = new TranslationTextComponent("lotr.menu.subtitle").withStyle(TextFormatting.ITALIC);
		rand = new Random();
		waypointRoute = new ArrayList();
		randomWPStart = false;
	}
	private boolean fadeIn = false;
	private long firstRenderTime;
	private boolean minceraft;
	private String splashText;
	private String copyrightText = "Powered by Hummel009";
	private int widthCopyright;
	private int widthCopyrightRest;
	private NotificationModUpdateScreen modUpdateNotification;
	private MiddleEarthMapScreen mapGui;

	public LOTRMainMenuScreen() {
		fadeIn = isFirstMenu;
		isFirstMenu = false;
		minceraft = new Random().nextFloat() < 1.0E-4D;
		mapGui = new MiddleEarthMapScreen();
		mapRenderer = new MiddleEarthMapRenderer(false, false);
		mapRenderer.setStableZoom((float) Math.pow(2.0D, -0.10000000149011612D));
		setupWaypoints();
		if (!waypointRoute.isEmpty()) {
			if (randomWPStart) {
				currentWPIndex = rand.nextInt(waypointRoute.size());
			} else {
				currentWPIndex = 0;
			}

			MapWaypoint wp = (MapWaypoint) waypointRoute.get(currentWPIndex);
			mapRenderer.setInstantaneousPosition(wp.getMapX(), wp.getMapZ());
		} else {
			MapSettings mapSettings = MapSettingsManager.clientInstance().getCurrentLoadedMap();
			mapRenderer.setInstantaneousPosition(mapSettings.getOriginX(), mapSettings.getOriginZ());
		}

	}

	@Override
	public void init() {
		super.init();
		if (splashText == null) {
			splashText = minecraft.getSplashManager().getSplash();
		}

		widthCopyright = font.width(copyrightText);
		widthCopyrightRest = width - widthCopyright - 2;
		Button modbutton = (Button) buttons.stream().filter(widget -> (widget instanceof Button && widget.getMessage().getString().equals(new TranslationTextComponent("fml.menu.mods").getString()))).findFirst().orElse((Widget) null);
		modUpdateNotification = NotificationModUpdateScreen.init(this, modbutton);
		int lowerButtonMaxY = 0;
		int moveDown;
		for (Widget button : buttons) {
			moveDown = button.y + button.getHeight();
			if (moveDown > lowerButtonMaxY) {
				lowerButtonMaxY = moveDown;
			}
		}

		int idealMoveDown = 50;
		int lowestSuitableHeight = height - 25;
		moveDown = Math.min(idealMoveDown, lowestSuitableHeight - lowerButtonMaxY);
		moveDown = Math.max(moveDown, 0);

		for (int i = 0; i < buttons.size(); ++i) {
			Widget button = buttons.get(i);
			button.y += moveDown;
			if (button.getClass() == Button.class) {
				Button bt = (Button) button;
				Widget newButton = new RedBookButton(bt.x, bt.y, bt.getWidth(), bt.getHeight(), bt.getMessage(), b -> {
					bt.onPress();
				});
				buttons.set(i, newButton);
			}
		}

	}

	@Override
	public void init(Minecraft mc, int i, int j) {
		super.init(mc, i, j);
		mapGui.loadCurrentMapTextures();
		mapGui.init(mc, i, j);
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float tick) {
		tick = minecraft.getFrameTime();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		if (firstRenderTime == 0L && fadeIn) {
			firstRenderTime = Util.getMillis();
		}

		float fade = fadeIn ? (Util.getMillis() - firstRenderTime) / 1000.0F : 1.0F;
		fill(matStack, 0, 0, width, height, -1);
		float zoom = -0.1F + MathHelper.cos((tickCounter + tick) * 0.003F) * 0.8F;
		if (fadeIn) {
			float slowerFade = fade * 0.5F;
			float fadeInZoom = MathHelper.clamp(1.0F - slowerFade, 0.0F, 1.0F) * -2.5F;
			zoom += fadeInZoom;
		}

		mapRenderer.setZoomExp(zoom);
		mapRenderer.renderMap(matStack, this, mapGui, tick);
		mapRenderer.renderVignettes(matStack, this, getBlitOffset(), 2);
		int i = 274;
		int j = width / 2 - i / 2;
		minecraft.getTextureManager().bind(PANORAMA_OVERLAY);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, fadeIn ? MathHelper.clamp(1.0F - fade, 0.0F, 1.0F) : 0.0F);
		blit(matStack, 0, 0, width, height, 0.0F, 0.0F, 16, 128, 16, 128);
		float f1 = fadeIn ? MathHelper.clamp(fade - 1.0F, 0.0F, 1.0F) : 1.0F;
		int l = MathHelper.ceil(f1 * 255.0F) << 24;
		if ((l & -67108864) != 0) {
			minecraft.getTextureManager().bind(TITLE_TEXTURE);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
			if (minceraft) {
				this.blit(matStack, j + 0, 30, 0, 0, 99, 44);
				this.blit(matStack, j + 99, 30, 129, 0, 27, 44);
				this.blit(matStack, j + 99 + 26, 30, 126, 0, 3, 44);
				this.blit(matStack, j + 99 + 26 + 3, 30, 99, 0, 26, 44);
			} else {
				this.blit(matStack, j + 0, 30, 0, 0, 155, 44);
			}
			this.blit(matStack, j + 155, 30, 0, 45, 155, 44);

			drawString(matStack, font, MOD_TITLE, width / 2 - font.width(MOD_TITLE) / 2, 86, -1);
			drawString(matStack, font, MOD_SUBTITLE, width / 2 - font.width(MOD_SUBTITLE) / 2, 96, -2236963);
			minecraft.getTextureManager().bind(TITLE_EDITION);
			blit(matStack, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
			ForgeHooksClient.renderMainMenu(this, matStack, font, width, height);
			String s = "Minecraft " + SharedConstants.getCurrentVersion().getName();
			if (minecraft.isDemo()) {
				s = s + " Demo";
			} else {
				s = s + ("release".equalsIgnoreCase(minecraft.getVersionType()) ? "" : "/" + minecraft.getVersionType());
			}

			if (minecraft.isProbablyModded()) {
				s = s + I18n.get("menu.modded");
			}

			BrandingControl.forEachLine(true, true, (brdline, brd) -> {
				FontRenderer var10001 = font;
				int var10004 = height;
				int var10006 = brdline;
				font.getClass();
				drawString(matStack, var10001, brd, 2, var10004 - (10 + var10006 * (9 + 1)), 16777215 | l);
			});
			BrandingControl.forEachAboveCopyrightLine((brdline, brd) -> {
				FontRenderer var10001 = font;
				int var10003 = width - font.width(brd);
				int var10004 = height;
				int var10006 = brdline + 1;
				font.getClass();
				drawString(matStack, var10001, brd, var10003, var10004 - (10 + var10006 * (9 + 1)), 16777215 | l);
			});
			drawString(matStack, font, copyrightText, widthCopyrightRest, height - 10, 16777215 | l);
			if (mouseX > widthCopyrightRest && mouseX < widthCopyrightRest + widthCopyright && mouseY > height - 10 && mouseY < height) {
				fill(matStack, widthCopyrightRest, height - 1, widthCopyrightRest + widthCopyright, height, 16777215 | l);
			}

			Iterator var12 = buttons.iterator();

			Widget widget;
			while (var12.hasNext()) {
				widget = (Widget) var12.next();
				widget.setAlpha(f1);
			}

			var12 = buttons.iterator();

			while (var12.hasNext()) {
				widget = (Widget) var12.next();
				widget.render(matStack, mouseX, mouseY, tick);
			}

			modUpdateNotification.render(matStack, mouseX, mouseY, tick);
		}

	}

	@Override
	public void tick() {
		super.tick();
		++tickCounter;
		mapRenderer.tick();
		if (!waypointRoute.isEmpty()) {
			if (currentWPIndex >= waypointRoute.size()) {
				currentWPIndex = 0;
			}

			MapWaypoint wp = (MapWaypoint) waypointRoute.get(currentWPIndex);
			double dx = wp.getMapX() - mapRenderer.getMapX();
			double dy = wp.getMapZ() - mapRenderer.getMapY();
			double distSq = dx * dx + dy * dy;
			double dist = Math.sqrt(distSq);
			if (dist <= 12.0D) {
				++currentWPIndex;
				if (currentWPIndex >= waypointRoute.size()) {
					currentWPIndex = 0;
				}
			} else {
				mapSpeed += 0.01F;
				mapSpeed = Math.min(mapSpeed, 0.8F);
				float vXNew = (float) (dx / dist) * mapSpeed;
				float vYNew = (float) (dy / dist) * mapSpeed;
				float a = 0.02F;
				mapVelX += (vXNew - mapVelX) * a;
				mapVelY += (vYNew - mapVelY) * a;
			}
		}

		mapRenderer.moveBy(mapVelX, mapVelY);
	}

	private static void setupWaypoints() {
		waypointRoute.clear();
		MapSettings mapSettings = MapSettingsManager.clientInstance().getCurrentLoadedMap();
		waypointRoute.addAll(mapSettings.getMenuWaypointRoute());
	}
}
