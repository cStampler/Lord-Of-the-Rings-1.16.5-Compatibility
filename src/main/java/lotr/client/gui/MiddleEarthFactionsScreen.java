package lotr.client.gui;

import java.util.*;

import com.google.common.math.IntMath;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.align.AlignmentFormatter;
import lotr.client.gui.map.MiddleEarthMapScreen;
import lotr.client.gui.util.*;
import lotr.client.gui.widget.ScrollPane;
import lotr.client.gui.widget.button.*;
import lotr.client.util.LOTRClientUtil;
import lotr.common.config.LOTRConfig;
import lotr.common.data.*;
import lotr.common.fac.*;
import lotr.common.init.LOTRDimensions;
import lotr.common.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;

public class MiddleEarthFactionsScreen extends MiddleEarthMenuScreen {
	public static final ResourceLocation FACTIONS_TEXTURE = new ResourceLocation("lotr", "textures/gui/factions.png");
	public static final ResourceLocation FACTIONS_TEXTURE_FULL = new ResourceLocation("lotr", "textures/gui/factions_full.png");
	private static RegistryKey currentDimension;
	private static RegistryKey prevDimension;
	private static FactionRegion currentRegion;
	private static FactionRegion prevRegion;
	private static List currentFactionList;
	private static MiddleEarthFactionsScreen.Page currentPage;
	static {
		currentPage = MiddleEarthFactionsScreen.Page.OVERVIEW;
	}
	private FactionSettings currentLoadedFactions;
	private int currentFactionIndex = 0;
	private int prevFactionIndex = 0;
	private Faction currentFaction;
	private int pageY = 46;
	private int pageWidth = 256;
	private int pageHeight = 128;
	private int pageBorderLeft = 16;
	private int pageBorderTop = 12;
	private int pageMapX = 159;
	private int pageMapY = 22;
	private int pageMapSize = 80;
	private MiddleEarthMapScreen mapDrawGui;
	private final AlignmentTextRenderer alignmentTextRenderer = AlignmentTextRenderer.newGUIRenderer();
	private final AlignmentRenderer alignmentRenderer;
	private Button buttonRegions;
	private Button buttonPagePrev;
	private Button buttonPageNext;
	private Button buttonFactionMap;
	private Button buttonPreferredRanksMasc;
	private Button buttonPreferredRanksFem;
	private Button buttonToggleFriendlyFire;
	private PledgeButton buttonOpenPledgeScreen;
	private PledgeButton buttonPledgeConfirm;
	private PledgeButton buttonPledgeRevoke;
	private float currentScroll;
	private boolean isScrolling;
	private boolean wasMouseDown;
	private boolean isMouseDown;
	private int scrollBarWidth;
	private int scrollBarHeight;
	private int scrollBarX;
	private int scrollBarY;
	private int scrollBarBorder;
	private int scrollWidgetWidth;
	private int scrollWidgetHeight;
	private ScrollPane scrollPaneAlliesEnemies;
	private int scrollAlliesEnemiesX;
	private int numDisplayedAlliesEnemies;
	private List currentAlliesEnemies;
	private boolean isOtherPlayer;
	private String otherPlayerName;
	private Map otherPlayerAlignmentMap;
	private boolean isPledging;

	private boolean isUnpledging;

	public MiddleEarthFactionsScreen() {
		super(new StringTextComponent("FACTIONS"));
		alignmentRenderer = new AlignmentRenderer(alignmentTextRenderer);
		xSize = pageWidth;
		currentScroll = 0.0F;
		isScrolling = false;
		scrollBarWidth = 240;
		scrollBarHeight = 14;
		scrollBarX = xSize / 2 - scrollBarWidth / 2;
		scrollBarY = 180;
		scrollBarBorder = 1;
		scrollWidgetWidth = 17;
		scrollWidgetHeight = 12;
		scrollPaneAlliesEnemies = new ScrollPane(7, 7).setColors(5521198, 8019267);
		scrollAlliesEnemiesX = 138;
		isOtherPlayer = false;
		isPledging = false;
		isUnpledging = false;
		mapDrawGui = new MiddleEarthMapScreen();
		currentLoadedFactions = FactionSettingsManager.clientInstance().getCurrentLoadedFactions();
	}

	private boolean canScroll() {
		return true;
	}

	private LOTRPlayerData getClientPlayerData() {
		return LOTRLevelData.clientInstance().getData(minecraft.player);
	}

	private boolean hasScrollBar() {
		return currentFactionList.size() > 1;
	}

	@Override
	public void init() {
		super.init();
		if (isOtherPlayer) {
			removeButton(buttonMenuReturn);
		}

		buttonRegions = this.addButton(new RedBookButton(guiLeft + xSize / 2 - 60, guiTop + scrollBarY + 20, 120, 20, StringTextComponent.EMPTY, button -> {
			List regionList = currentLoadedFactions.getRegionsForDimension(currentDimension);
			if (!regionList.isEmpty()) {
				int i = regionList.indexOf(currentRegion);
				++i;
				i = IntMath.mod(i, regionList.size());
				currentRegion = (FactionRegion) regionList.get(i);
				updateCurrentDimensionAndFaction();
				setCurrentScrollFromFaction();
				scrollPaneAlliesEnemies.resetScroll();
				isPledging = false;
				isUnpledging = false;
			}

		}));
		buttonPagePrev = this.addButton(new FactionsPageButton(guiLeft + 13, guiTop + pageY + 104, true, new TranslationTextComponent("gui.lotr.factions.page.previous"), button -> {
			MiddleEarthFactionsScreen.Page newPage = currentPage.prev();
			if (newPage != null) {
				currentPage = newPage;
				scrollPaneAlliesEnemies.resetScroll();
				isPledging = false;
				isUnpledging = false;
			}

		}));
		buttonPageNext = this.addButton(new FactionsPageButton(guiLeft + pageWidth - 29, guiTop + pageY + 104, false, new TranslationTextComponent("gui.lotr.factions.page.next"), button -> {
			MiddleEarthFactionsScreen.Page newPage = currentPage.next();
			if (newPage != null) {
				currentPage = newPage;
				scrollPaneAlliesEnemies.resetScroll();
				isPledging = false;
				isUnpledging = false;
			}

		}));
		buttonFactionMap = this.addButton(new FactionsMapButton(guiLeft + pageMapX + pageMapSize - 3 - 8, guiTop + pageY + pageMapY + 3, button -> {
			MiddleEarthMapScreen factionGuiMap = new MiddleEarthMapScreen();
			factionGuiMap.setAreasOfInfluence(currentFaction);
			minecraft.setScreen(factionGuiMap);
		}));
		buttonPreferredRanksMasc = this.addButton(new PreferredRankGenderButton(guiLeft + pageWidth - pageBorderLeft - 24, guiTop + pageY + pageBorderTop, RankGender.MASCULINE, PreferredRankGenderButton::sendPreferenceToServer));
		buttonPreferredRanksFem = this.addButton(new PreferredRankGenderButton(guiLeft + pageWidth - pageBorderLeft - 12, guiTop + pageY + pageBorderTop, RankGender.FEMININE, PreferredRankGenderButton::sendPreferenceToServer));
		buttonToggleFriendlyFire = this.addButton(new FriendlyFireToggleButton(guiLeft + scrollBarX + scrollBarWidth - 16, guiTop + scrollBarY + 22, FriendlyFireToggleButton::sendToggleToServer));
		buttonOpenPledgeScreen = this.addButton(new PledgeButton(this, guiLeft + 14, guiTop + pageY + pageHeight - 42, false, button -> {
			if (getClientPlayerData().getAlignmentData().isPledgedTo(currentFaction)) {
				isUnpledging = true;
			} else {
				isPledging = true;
			}

		}));
		buttonPledgeConfirm = this.addButton(new PledgeButton(this, guiLeft + pageWidth / 2 - 16, guiTop + pageY + pageHeight - 44, false, button -> {
			LOTRPacketHandler.sendToServer(new CPacketSetPledge(currentFaction));
			isPledging = false;
		}));
		buttonPledgeRevoke = this.addButton(new PledgeButton(this, guiLeft + pageWidth / 2 - 16, guiTop + pageY + pageHeight - 44, true, button -> {
			LOTRPacketHandler.sendToServer(new CPacketSetPledge((Faction) null));
			isUnpledging = false;
			minecraft.setScreen((Screen) null);
		}));
		prevDimension = currentDimension = LOTRDimensions.getCurrentLOTRDimensionOrFallback(minecraft.level);
		AlignmentDataModule alignData = LOTRLevelData.clientInstance().getData(minecraft.player).getAlignmentData();
		currentFaction = alignData.getCurrentViewedFactionOrFallbackToFirstIn(currentDimension);
		if (currentFaction != null) {
			prevRegion = currentRegion = currentFaction.getRegion();
			currentFactionList = currentLoadedFactions.getFactionsForRegion(currentRegion);
			prevFactionIndex = currentFactionIndex = currentFactionList.indexOf(currentFaction);
			setCurrentScrollFromFaction();
		}

	}

	@Override
	public void init(Minecraft mc, int w, int h) {
		super.init(mc, w, h);
		mapDrawGui.init(mc, w, h);
	}

	@Override
	public boolean keyPressed(int key, int scan, int param3) {
		if (isEscapeOrInventoryKey(key, scan)) {
			if (isPledging) {
				isPledging = false;
				return true;
			}

			if (isUnpledging) {
				isUnpledging = false;
				return true;
			}

			if (isOtherPlayer) {
				minecraft.player.closeContainer();
				return true;
			}
		}

		return super.keyPressed(key, scan, param3);
	}

	@Override
	public boolean mouseClicked(double x, double y, int code) {
		if (code == 0) {
			isMouseDown = true;
		}

		return super.mouseClicked(x, y, code);
	}

	@Override
	public boolean mouseReleased(double x, double y, int code) {
		if (code == 0) {
			isMouseDown = false;
		}

		return super.mouseReleased(x, y, code);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double scroll) {
		if (super.mouseScrolled(x, y, scroll)) {
			return true;
		}
		if (scroll == 0.0D) {
			return false;
		}
		if (scrollPaneAlliesEnemies.hasScrollBar && scrollPaneAlliesEnemies.mouseOver) {
			int l = currentAlliesEnemies.size() - numDisplayedAlliesEnemies;
			scrollPaneAlliesEnemies.mouseWheelScroll(scroll, l);
		} else {
			if (scroll < 0.0D) {
				currentFactionIndex = Math.min(currentFactionIndex + 1, Math.max(0, currentFactionList.size() - 1));
			}

			if (scroll > 0.0D) {
				currentFactionIndex = Math.max(currentFactionIndex - 1, 0);
			}

			setCurrentScrollFromFaction();
			scrollPaneAlliesEnemies.resetScroll();
			isPledging = false;
			isUnpledging = false;
		}
		return true;
	}

	private void processFactionScrollBar(int mouseX, int mouseY) {
		int i1 = guiLeft + scrollBarX;
		int j1 = guiTop + scrollBarY;
		int i2 = i1 + scrollBarWidth;
		int j2 = j1 + scrollBarHeight;
		if (!wasMouseDown && isMouseDown && mouseX >= i1 && mouseY >= j1 && mouseX < i2 && mouseY < j2) {
			isScrolling = canScroll();
		}

		if (!isMouseDown) {
			isScrolling = false;
		}

		wasMouseDown = isMouseDown;
		if (isScrolling) {
			currentScroll = (mouseX - i1 - scrollWidgetWidth / 2.0F) / ((float) (i2 - i1) - (float) scrollWidgetWidth);
			currentScroll = MathHelper.clamp(currentScroll, 0.0F, 1.0F);
			currentFactionIndex = Math.round(currentScroll * (currentFactionList.size() - 1));
			scrollPaneAlliesEnemies.resetScroll();
		}

		if (currentPage != MiddleEarthFactionsScreen.Page.GOOD_RELATIONS && currentPage != MiddleEarthFactionsScreen.Page.BAD_RELATIONS && currentPage != MiddleEarthFactionsScreen.Page.RANKS) {
			scrollPaneAlliesEnemies.hasScrollBar = false;
		} else {
			List mortals;
			List enemies;
			if (currentPage == MiddleEarthFactionsScreen.Page.GOOD_RELATIONS) {
				currentAlliesEnemies = new ArrayList();
				mortals = currentFaction.getOthersOfRelation(FactionRelation.ALLY);
				if (!mortals.isEmpty()) {
					currentAlliesEnemies.add(FactionRelation.ALLY);
					currentAlliesEnemies.addAll(mortals);
				}

				enemies = currentFaction.getOthersOfRelation(FactionRelation.FRIEND);
				if (!enemies.isEmpty()) {
					if (!currentAlliesEnemies.isEmpty()) {
						currentAlliesEnemies.add((Object) null);
					}

					currentAlliesEnemies.add(FactionRelation.FRIEND);
					currentAlliesEnemies.addAll(enemies);
				}
			} else if (currentPage == MiddleEarthFactionsScreen.Page.BAD_RELATIONS) {
				currentAlliesEnemies = new ArrayList();
				mortals = currentFaction.getOthersOfRelation(FactionRelation.MORTAL_ENEMY);
				if (!mortals.isEmpty()) {
					currentAlliesEnemies.add(FactionRelation.MORTAL_ENEMY);
					currentAlliesEnemies.addAll(mortals);
				}

				enemies = currentFaction.getOthersOfRelation(FactionRelation.ENEMY);
				if (!enemies.isEmpty()) {
					if (!currentAlliesEnemies.isEmpty()) {
						currentAlliesEnemies.add((Object) null);
					}

					currentAlliesEnemies.add(FactionRelation.ENEMY);
					currentAlliesEnemies.addAll(enemies);
				}
			} else if (currentPage == MiddleEarthFactionsScreen.Page.RANKS) {
				currentAlliesEnemies = new ArrayList();
				currentAlliesEnemies.add(new TranslationTextComponent("gui.lotr.factions.ranksHeader"));
				if (LOTRLevelData.clientInstance().getData(minecraft.player).getAlignmentData().getAlignment(currentFaction) <= 0.0F) {
					currentAlliesEnemies.add(currentFaction.getEnemyRank());
				}

				FactionRank rank = currentFaction.getNeutralRank();

				while (true) {
					currentAlliesEnemies.add(rank);
					FactionRank nextRank = currentFaction.getRankAbove(rank);
					if (nextRank == null || nextRank.isDummyRank() || currentAlliesEnemies.contains(nextRank)) {
						break;
					}

					rank = nextRank;
				}
			}

			scrollPaneAlliesEnemies.hasScrollBar = false;
			numDisplayedAlliesEnemies = currentAlliesEnemies.size();
			if (numDisplayedAlliesEnemies > 10) {
				numDisplayedAlliesEnemies = 10;
				scrollPaneAlliesEnemies.hasScrollBar = true;
			}

			scrollPaneAlliesEnemies.paneX0 = guiLeft;
			scrollPaneAlliesEnemies.scrollBarX0 = guiLeft + scrollAlliesEnemiesX;
			ScrollPane var10000;
			if (currentPage == MiddleEarthFactionsScreen.Page.RANKS) {
				var10000 = scrollPaneAlliesEnemies;
				var10000.scrollBarX0 += 50;
			}

			scrollPaneAlliesEnemies.paneY0 = guiTop + pageY + pageBorderTop;
			var10000 = scrollPaneAlliesEnemies;
			int var10001 = scrollPaneAlliesEnemies.paneY0;
			font.getClass();
			var10000.paneY1 = var10001 + 9 * numDisplayedAlliesEnemies;
		}
		scrollPaneAlliesEnemies.mouseDragScroll(mouseX, mouseY, isMouseDown);

	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float tick) {
		LOTRPlayerData clientPD = getClientPlayerData();
		AlignmentDataModule alignData = getClientPlayerData().getAlignmentData();
		boolean mouseOverAlignLock = false;
		boolean mouseOverCivilianKills = false;
		if (!isPledging && !isUnpledging) {
			buttonPagePrev.active = currentPage.prev() != null;
			buttonPageNext.active = currentPage.next() != null;
			buttonFactionMap.visible = buttonFactionMap.active = currentPage != MiddleEarthFactionsScreen.Page.RANKS && currentFaction != null && currentFaction.getMapSquare() != null && LOTRDimensions.getCurrentLOTRDimensionOrFallback(minecraft.level).equals(currentFaction.getDimension());
			if (!AreasOfInfluence.areAreasOfInfluenceEnabled(minecraft.level)) {
				buttonFactionMap.visible = buttonFactionMap.active = false;
			}

			boolean isRanksPage = currentPage == MiddleEarthFactionsScreen.Page.RANKS;
			buttonPreferredRanksMasc.visible = buttonPreferredRanksMasc.active = isRanksPage;
			buttonPreferredRanksFem.visible = buttonPreferredRanksFem.active = isRanksPage;
			buttonToggleFriendlyFire.visible = buttonToggleFriendlyFire.active = true;
			if (!isOtherPlayer && currentPage == MiddleEarthFactionsScreen.Page.OVERVIEW) {
				if (alignData.isPledgedTo(currentFaction)) {
					buttonOpenPledgeScreen.setDisplayAsBroken(buttonOpenPledgeScreen.isHovered());
					buttonOpenPledgeScreen.visible = buttonOpenPledgeScreen.active = true;
					buttonOpenPledgeScreen.setTooltipLines(new TranslationTextComponent("gui.lotr.factions.unpledge"));
				} else {
					buttonOpenPledgeScreen.setDisplayAsBroken(false);
					buttonOpenPledgeScreen.visible = alignData.getPledgeFaction() == null && currentFaction.isPlayableAlignmentFaction() && alignData.getAlignment(currentFaction) >= 0.0F;
					buttonOpenPledgeScreen.active = buttonOpenPledgeScreen.visible && alignData.hasPledgeAlignment(currentFaction);
					ITextComponent desc1 = new TranslationTextComponent("gui.lotr.factions.pledge");
					ITextComponent desc2 = new TranslationTextComponent("gui.lotr.factions.pledge.req", AlignmentFormatter.formatAlignForDisplay(currentFaction.getPledgeAlignment()));
					buttonOpenPledgeScreen.setTooltipLines(desc1, desc2);
				}
			} else {
				buttonOpenPledgeScreen.visible = buttonOpenPledgeScreen.active = false;
			}

			buttonPledgeConfirm.visible = buttonPledgeConfirm.active = false;
			buttonPledgeRevoke.visible = buttonPledgeRevoke.active = false;
		} else {
			buttonPagePrev.active = false;
			buttonPageNext.active = false;
			buttonFactionMap.visible = buttonFactionMap.active = false;
			buttonPreferredRanksMasc.visible = buttonPreferredRanksMasc.active = false;
			buttonPreferredRanksFem.visible = buttonPreferredRanksFem.active = false;
			buttonToggleFriendlyFire.visible = buttonToggleFriendlyFire.active = true;
			buttonOpenPledgeScreen.visible = buttonOpenPledgeScreen.active = false;
			if (isPledging) {
				buttonPledgeConfirm.visible = true;
				buttonPledgeConfirm.active = alignData.canMakeNewPledge() && alignData.canPledgeToNow(currentFaction);
				buttonPledgeConfirm.setTooltipLines(new TranslationTextComponent("gui.lotr.factions.pledge"));
				buttonPledgeRevoke.visible = buttonPledgeRevoke.active = false;
			} else if (isUnpledging) {
				buttonPledgeConfirm.visible = buttonPledgeConfirm.active = false;
				buttonPledgeRevoke.visible = buttonPledgeRevoke.active = true;
				buttonPledgeRevoke.setTooltipLines(new TranslationTextComponent("gui.lotr.factions.unpledge"));
			}
		}

		processFactionScrollBar(mouseX, mouseY);
		this.renderBackground(matStack);
		if (useFullPageTexture()) {
			minecraft.getTextureManager().bind(FACTIONS_TEXTURE_FULL);
		} else {
			minecraft.getTextureManager().bind(FACTIONS_TEXTURE);
		}

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.blit(matStack, guiLeft, guiTop + pageY, 0, 0, pageWidth, pageHeight);
		ITextComponent title = new TranslationTextComponent("gui.lotr.factions.title", LOTRDimensions.getDisplayName(currentDimension));
		if (isOtherPlayer) {
			title = new TranslationTextComponent("gui.lotr.factions.title", otherPlayerName);
		}

		font.draw(matStack, title, guiLeft + xSize / 2 - font.width(title) / 2, guiTop - 30, 16777215);
		if (currentRegion != null && currentLoadedFactions.getRegionsForDimension(currentDimension).size() > 1) {
			buttonRegions.setMessage(currentRegion.getDisplayName());
			buttonRegions.visible = buttonRegions.active = true;
		} else {
			buttonRegions.setMessage(StringTextComponent.EMPTY);
			buttonRegions.visible = buttonRegions.active = false;
		}

		float zoom;
		int x;
		TranslationTextComponent alignmentInfo;
		float conq;
		if (currentFaction != null) {
			float alignment;
			if (isOtherPlayer && otherPlayerAlignmentMap != null) {
				alignment = (Float) otherPlayerAlignmentMap.get(currentFaction);
			} else {
				alignment = alignData.getAlignment(currentFaction);
			}

			x = guiLeft + xSize / 2;
			int y = guiTop;
			alignmentRenderer.renderAlignmentBar(matStack, minecraft, alignment, isOtherPlayer, currentFaction, x, y, true, false, true, true);
			font.getClass();
			y += 9 + 22;
			this.drawCenteredStringNoShadow(matStack, font, currentFaction.getDisplaySubtitle(), x, y, 16777215);
			font.getClass();
			int var10000 = y + 9 * 3;
			int index;
			int px;
			int py;
			if (!useFullPageTexture()) {
				MapSquare mapSquare = currentFaction.getMapSquare();
				int wcX;
				if (mapSquare != null) {
					wcX = mapSquare.mapX;
					index = mapSquare.mapZ;
					int mapR = mapSquare.radius;
					int xMin = guiLeft + pageMapX;
					int xMax = xMin + pageMapSize;
					px = guiTop + pageY + pageMapY;
					py = px + pageMapSize;
					int mapBorder = 1;
					fill(matStack, xMin - mapBorder, px - mapBorder, xMax + mapBorder, py + mapBorder, -16777216);
					zoom = (float) pageMapSize / (float) (mapR * 2);
					float zoomExp = (float) Math.log(zoom) / (float) Math.log(2.0D);
					mapDrawGui.setMapViewportAndPositionAndScale(xMin, xMax, px, py, wcX, index, zoom, zoomExp, zoom);
					mapDrawGui.enableZoomOutObjectFading = false;
					boolean sepia = (Boolean) LOTRConfig.CLIENT.sepiaMap.get();
					mapDrawGui.renderMapAndOverlay(matStack, tick, sepia, 1.0F, true);
				}

				wcX = guiLeft + pageMapX + 3;
				index = guiTop + pageY + pageMapY + pageMapSize + 5;
				int wcWidth = 8;
				minecraft.getTextureManager().bind(FACTIONS_TEXTURE);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				if (currentFaction.approvesCivilianKills()) {
					this.blit(matStack, wcX, index, 33, 142, wcWidth, wcWidth);
				} else {
					this.blit(matStack, wcX, index, 41, 142, wcWidth, wcWidth);
				}

				if (mouseX >= wcX && mouseX < wcX + wcWidth && mouseY >= index && mouseY < index + wcWidth) {
					mouseOverCivilianKills = true;
				}
			}

			x = guiLeft + pageBorderLeft;
			y = guiTop + pageY + pageBorderTop;
			int avgBgColor;
			TranslationTextComponent desc1;
			Object listObj;
			if (!isPledging && !isUnpledging) {
				TranslationTextComponent pledgeText;
				if (currentPage == MiddleEarthFactionsScreen.Page.OVERVIEW) {
					if (isOtherPlayer) {
						alignmentInfo = new TranslationTextComponent("gui.lotr.factions.overview.otherPlayer", otherPlayerName);
						font.draw(matStack, alignmentInfo, x, y, 8019267);
						font.getClass();
						y += 9 * 2;
					}

					alignmentInfo = new TranslationTextComponent("gui.lotr.factions.alignment");
					font.draw(matStack, alignmentInfo, x, y, 8019267);
					x += font.width(alignmentInfo) + 5;
					String alignmentString = AlignmentFormatter.formatAlignForDisplay(alignment);
					alignmentTextRenderer.drawAlignmentText(matStack, font, x, y, new StringTextComponent(alignmentString), 1.0F);
					if (alignData.isPledgeEnemyAlignmentLimited(currentFaction)) {
						minecraft.getTextureManager().bind(FACTIONS_TEXTURE);
						RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
						index = x + font.width(alignmentString) + 5;
						int lockWidth = 16;
						this.blit(matStack, index, y, 0, 200, lockWidth, lockWidth);
						if (mouseX >= index && mouseX < index + lockWidth && mouseY >= y && mouseY < y + lockWidth) {
							mouseOverAlignLock = true;
						}
					}

					font.getClass();
					y += 9;
					x = guiLeft + pageBorderLeft;
					FactionRank curRank = currentFaction.getRankFor(alignment);
					desc1 = new TranslationTextComponent("gui.lotr.factions.alignment.rank", curRank.getDisplayFullName(clientPD.getMiscData().getPreferredRankGender()));
					font.draw(matStack, desc1, x, y, 8019267);
					font.getClass();
					y += 9 * 2;
					if (!isOtherPlayer) {
						FactionStats factionStats = clientPD.getFactionStatsData().getFactionStats(currentFaction);
						if (alignment >= 0.0F) {
							pledgeText = new TranslationTextComponent("gui.lotr.factions.stats.enemiesKilled", factionStats.getEnemiesKilled());
							font.draw(matStack, pledgeText, x, y, 8019267);
							font.getClass();
							y += 9;
							pledgeText = new TranslationTextComponent("gui.lotr.factions.stats.trades", factionStats.getTradeCount());
							font.draw(matStack, pledgeText, x, y, 8019267);
							font.getClass();
							y += 9;
							pledgeText = new TranslationTextComponent("gui.lotr.factions.stats.hires", factionStats.getHireCount());
							font.draw(matStack, pledgeText, x, y, 8019267);
							font.getClass();
							y += 9;
							pledgeText = new TranslationTextComponent("gui.lotr.factions.stats.miniquests", factionStats.getMiniQuestsCompleted());
							font.draw(matStack, pledgeText, x, y, 8019267);
							font.getClass();
							y += 9;
							if (alignData.isPledgedTo(currentFaction)) {
								conq = factionStats.getConquestEarned();
								if (conq != 0.0F) {
									py = Math.round(conq);
									pledgeText = new TranslationTextComponent("gui.lotr.factions.stats.conquest", py);
									font.draw(matStack, pledgeText, x, y, 8019267);
									font.getClass();
									y += 9;
								}
							}
						}

						if (alignment <= 0.0F) {
							pledgeText = new TranslationTextComponent("gui.lotr.factions.stats.membersKilled", factionStats.getMembersKilled());
							font.draw(matStack, pledgeText, x, y, 8019267);
							font.getClass();
							var10000 = y + 9;
						}

						if (buttonOpenPledgeScreen.visible && alignData.isPledgedTo(currentFaction)) {
							pledgeText = new TranslationTextComponent("gui.lotr.factions.pledged");
							px = buttonOpenPledgeScreen.x + buttonOpenPledgeScreen.getWidth() + 8;
							var10000 = buttonOpenPledgeScreen.y + buttonOpenPledgeScreen.getHeight() / 2;
							font.getClass();
							py = var10000 - 9 / 2;
							font.draw(matStack, pledgeText, px, py, 16711680);
						}
					}
				} else {
					int[] minMax;
					if (currentPage == MiddleEarthFactionsScreen.Page.RANKS) {
						FactionRank curRank = currentFaction.getRankFor(clientPD);
						minMax = scrollPaneAlliesEnemies.getMinMaxIndices(currentAlliesEnemies, numDisplayedAlliesEnemies);

						for (index = minMax[0]; index <= minMax[1]; ++index) {
							listObj = currentAlliesEnemies.get(index);
							if (listObj instanceof String) {
								String s = (String) listObj;
								font.draw(matStack, new StringTextComponent(s), x, y, 8019267);
							} else if (listObj instanceof FactionRank) {
								FactionRank rank = (FactionRank) listObj;
								ITextComponent rankName = new StringTextComponent(rank.getDisplayShortName(clientPD.getMiscData().getPreferredRankGender()));
								String rankAlign = AlignmentFormatter.formatAlignForDisplay(rank.getAlignment());
								if (rank.isNameEqual("enemy")) {
									rankAlign = "-";
								}

								boolean hiddenRankName = false;
								if (!alignData.isPledgedTo(currentFaction) && rank.getAlignment() > currentFaction.getPledgeAlignment() && rank.getAlignment() > currentFaction.getRankAbove(curRank).getAlignment()) {
									hiddenRankName = true;
								}

								if (hiddenRankName) {
									rankName = new TranslationTextComponent("gui.lotr.factions.rank.unknown");
								}

								ITextComponent listRank = new TranslationTextComponent("gui.lotr.factions.listRank", rankName, rankAlign);
								if (rank == curRank) {
									alignmentTextRenderer.drawAlignmentText(matStack, font, x, y, listRank, 1.0F);
								} else {
									font.draw(matStack, listRank, x, y, 8019267);
								}
							}

							font.getClass();
							y += 9;
						}
					} else if (currentPage == MiddleEarthFactionsScreen.Page.GOOD_RELATIONS || currentPage == MiddleEarthFactionsScreen.Page.BAD_RELATIONS) {
						avgBgColor = LOTRClientUtil.computeAverageFactionPageColor(minecraft, FACTIONS_TEXTURE, 20, 20, 120, 80);
						minMax = scrollPaneAlliesEnemies.getMinMaxIndices(currentAlliesEnemies, numDisplayedAlliesEnemies);

						for (index = minMax[0]; index <= minMax[1]; ++index) {
							listObj = currentAlliesEnemies.get(index);
							if (listObj instanceof FactionRelation) {
								FactionRelation rel = (FactionRelation) listObj;
								pledgeText = new TranslationTextComponent("gui.lotr.factions.relationHeader", rel.getDisplayName());
								font.draw(matStack, pledgeText, x, y, 8019267);
							} else if (listObj instanceof Faction) {
								Faction fac = (Faction) listObj;
								pledgeText = new TranslationTextComponent("gui.lotr.factions.list", fac.getDisplayName());
								font.draw(matStack, pledgeText, x, y, LOTRClientUtil.findContrastingColor(fac.getColor(), avgBgColor));
							}

							font.getClass();
							y += 9;
						}
					}
				}

				if (scrollPaneAlliesEnemies.hasScrollBar) {
					scrollPaneAlliesEnemies.drawScrollBar(matStack);
				}
			} else {
				avgBgColor = pageWidth - pageBorderLeft * 2;
				List displayLines = new ArrayList();
				if (isPledging) {
					List facsPreventingPledge = alignData.getFactionsPreventingPledgeTo(currentFaction);
					TranslationTextComponent desc2;
					if (facsPreventingPledge.isEmpty()) {
						if (alignData.canMakeNewPledge()) {
							if (alignData.canPledgeToNow(currentFaction)) {
								desc1 = new TranslationTextComponent("gui.lotr.factions.pledge.desc.1", currentFaction.getDisplayName());
								displayLines.addAll(font.split(desc1, avgBgColor));
								displayLines.add(IReorderingProcessor.EMPTY);
								desc2 = new TranslationTextComponent("gui.lotr.factions.pledge.desc.2");
								displayLines.addAll(font.split(desc2, avgBgColor));
							}
						} else {
							ITextComponent brokenPledgeName = Faction.getFactionOrUnknownDisplayName(alignData.getBrokenPledgeFaction());
							desc2 = new TranslationTextComponent("gui.lotr.factions.pledge.breakCooldown", currentFaction.getDisplayName(), brokenPledgeName);
							displayLines.addAll(font.split(desc2, avgBgColor));
							displayLines.add(IReorderingProcessor.EMPTY);
							RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
							minecraft.getTextureManager().bind(FACTIONS_TEXTURE);
							this.blit(matStack, guiLeft + pageWidth / 2 - 97, guiTop + pageY + 56, 0, 240, 194, 16);
							this.blit(matStack, guiLeft + pageWidth / 2 - 75, guiTop + pageY + 60, 22, 232, MathHelper.ceil(alignData.getPledgeBreakCooldownFraction() * 150.0F), 8);
						}
					} else {
						alignData.getClass();
						Collections.sort(facsPreventingPledge, Comparator.comparing(hummel -> alignData.getAlignment((Faction) hummel)).reversed());
						listObj = new StringTextComponent("If you are reading this, something has gone hideously wrong.");
						if (facsPreventingPledge.size() == 1) {
							listObj = new TranslationTextComponent("gui.lotr.factions.pledge.enemies.1", ((Faction) facsPreventingPledge.get(0)).getDisplayName());
						} else if (facsPreventingPledge.size() == 2) {
							listObj = new TranslationTextComponent("gui.lotr.factions.pledge.enemies.2", ((Faction) facsPreventingPledge.get(0)).getDisplayName(), ((Faction) facsPreventingPledge.get(1)).getDisplayName());
						} else if (facsPreventingPledge.size() == 3) {
							listObj = new TranslationTextComponent("gui.lotr.factions.pledge.enemies.3", ((Faction) facsPreventingPledge.get(0)).getDisplayName(), ((Faction) facsPreventingPledge.get(1)).getDisplayName(), ((Faction) facsPreventingPledge.get(2)).getDisplayName());
						} else if (facsPreventingPledge.size() > 3) {
							listObj = new TranslationTextComponent("gui.lotr.factions.pledge.enemies.3+", ((Faction) facsPreventingPledge.get(0)).getDisplayName(), ((Faction) facsPreventingPledge.get(1)).getDisplayName(), ((Faction) facsPreventingPledge.get(2)).getDisplayName(), facsPreventingPledge.size() - 3);
						}

						desc2 = new TranslationTextComponent("gui.lotr.factions.pledge.enemies", currentFaction.getDisplayName(), listObj);
						displayLines.addAll(font.split(desc2, avgBgColor));
						displayLines.add(IReorderingProcessor.EMPTY);
					}
				} else if (isUnpledging) {
					ITextComponent desc11 = new TranslationTextComponent("gui.lotr.factions.unpledge.desc.1", currentFaction.getDisplayName());
					displayLines.addAll(font.split(desc11, avgBgColor));
					displayLines.add(IReorderingProcessor.EMPTY);
					desc11 = new TranslationTextComponent("gui.lotr.factions.unpledge.desc.2");
					displayLines.addAll(font.split(desc11, avgBgColor));
				}

				for (Iterator var44 = displayLines.iterator(); var44.hasNext(); y += 9) {
					IReorderingProcessor line = (IReorderingProcessor) var44.next();
					font.draw(matStack, line, x, y, 8019267);
					font.getClass();
				}
			}
		}

		if (hasScrollBar()) {
			minecraft.getTextureManager().bind(FACTIONS_TEXTURE);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.blit(matStack, guiLeft + scrollBarX, guiTop + scrollBarY, 0, 128, scrollBarWidth, scrollBarHeight);
			int factions = currentFactionList.size();

			for (x = 0; x < factions; ++x) {
				Faction faction = (Faction) currentFactionList.get(x);
				float[] factionColors = faction.getColorComponents();
				float shade = 0.6F;
				RenderSystem.color4f(factionColors[0] * shade, factionColors[1] * shade, factionColors[2] * shade, 1.0F);
				float fracMin = (float) x / (float) factions;
				float fracMax = (float) (x + 1) / (float) factions;
				float uMin = scrollBarBorder + fracMin * (scrollBarWidth - scrollBarBorder * 2);
				float uMax = scrollBarBorder + fracMax * (scrollBarWidth - scrollBarBorder * 2);
				conq = guiLeft + scrollBarX + uMin;
				float xMax = guiLeft + scrollBarX + uMax;
				float yMin = guiTop + scrollBarY + scrollBarBorder;
				zoom = guiTop + scrollBarY + scrollBarHeight - scrollBarBorder;
				LOTRClientUtil.blitFloat(this, matStack, conq, yMin, 0.0F + uMin, 128 + scrollBarBorder, xMax - conq, zoom - yMin);
			}

			minecraft.getTextureManager().bind(FACTIONS_TEXTURE);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (canScroll()) {
				x = (int) (currentScroll * (scrollBarWidth - scrollBarBorder * 2 - scrollWidgetWidth));
				this.blit(matStack, guiLeft + scrollBarX + scrollBarBorder + x, guiTop + scrollBarY + scrollBarBorder, 0, 142, scrollWidgetWidth, scrollWidgetHeight);
			}
		}

		super.render(matStack, mouseX, mouseY, tick);
		this.renderButtonTooltipIfHovered(matStack, buttonFactionMap, new TranslationTextComponent("gui.lotr.factions.viewMap"), mouseX, mouseY);
		MiddleEarthFactionsScreen.Page prevPage = currentPage.prev();
		MiddleEarthFactionsScreen.Page nextPage = currentPage.next();
		if (prevPage != null) {
			this.renderButtonTooltipIfHovered(matStack, buttonPagePrev, prevPage.getDisplayName(), mouseX, mouseY);
		}

		if (nextPage != null) {
			this.renderButtonTooltipIfHovered(matStack, buttonPageNext, nextPage.getDisplayName(), mouseX, mouseY);
		}

		this.renderButtonTooltipIfHovered(matStack, buttonPreferredRanksMasc, new TranslationTextComponent("gui.lotr.factions.rankGender.masc"), mouseX, mouseY);
		this.renderButtonTooltipIfHovered(matStack, buttonPreferredRanksFem, new TranslationTextComponent("gui.lotr.factions.rankGender.fem"), mouseX, mouseY);
		this.renderButtonTooltipIfHovered(matStack, buttonToggleFriendlyFire, FriendlyFireToggleButton.getTooltipLines(), mouseX, mouseY);
		if (mouseOverAlignLock) {
			String alignLimit = AlignmentFormatter.formatAlignForDisplay(alignData.getPledgeEnemyAlignmentLimit(currentFaction));
			alignmentInfo = new TranslationTextComponent("gui.lotr.factions.pledgeLocked", alignLimit, alignData.getPledgeFaction().getDisplayName());
			int stringWidth = 200;
			this.renderTooltip(matStack, font.split(alignmentInfo, stringWidth), mouseX, mouseY);
		}

		if (mouseOverCivilianKills) {
			ITextComponent civilianKills = new TranslationTextComponent(currentFaction.approvesCivilianKills() ? "gui.lotr.factions.civilianKills.yes" : "gui.lotr.factions.civilianKills.no");
			int stringWidth = 200;
			this.renderTooltip(matStack, font.split(civilianKills, stringWidth), mouseX, mouseY);
		}

	}

	private void setCurrentScrollFromFaction() {
		currentScroll = (float) currentFactionIndex / (float) (currentFactionList.size() - 1);
	}

	public void setOtherPlayer(String name, Map alignments) {
		isOtherPlayer = true;
		otherPlayerName = name;
		otherPlayerAlignmentMap = alignments;
	}

	@Override
	public void tick() {
		super.tick();
		updateCurrentDimensionAndFaction();
		AlignmentDataModule alignData = getClientPlayerData().getAlignmentData();
		if (isPledging && !alignData.hasPledgeAlignment(currentFaction)) {
			isPledging = false;
		}

		if (isUnpledging && !alignData.isPledgedTo(currentFaction)) {
			isUnpledging = false;
		}

	}

	private void updateCurrentDimensionAndFaction() {
		AlignmentDataModule alignData = getClientPlayerData().getAlignmentData();
		Map lastViewedRegions = new HashMap();
		if (currentFactionList != null && currentFactionIndex != prevFactionIndex) {
			currentFaction = (Faction) currentFactionList.get(currentFactionIndex);
		}

		prevFactionIndex = currentFactionIndex;
		currentDimension = LOTRDimensions.getCurrentLOTRDimensionOrFallback(minecraft.level);
		if (currentDimension != prevDimension) {
			currentRegion = (FactionRegion) currentLoadedFactions.getRegions().get(0);
		}

		if (currentRegion != prevRegion) {
			alignData.setRegionLastViewedFaction(prevRegion, currentFaction);
			lastViewedRegions.put(prevRegion, currentFaction);
			currentFactionList = currentLoadedFactions.getFactionsForRegion(currentRegion);
			currentFaction = alignData.getRegionLastViewedFaction(currentRegion);
			prevFactionIndex = currentFactionIndex = currentFactionList.indexOf(currentFaction);
		}

		prevDimension = currentDimension;
		prevRegion = currentRegion;
		Faction prevFaction = alignData.getCurrentViewedFaction();
		boolean changes = currentFaction != prevFaction;
		if (changes) {
			alignData.setCurrentViewedFaction(currentFaction);
			lastViewedRegions.forEach((region, fac) -> alignData.setRegionLastViewedFaction((FactionRegion) region, (Faction) fac));
			alignData.sendViewedFactionsToServer();
			isPledging = false;
			isUnpledging = false;
		}

	}

	private boolean useFullPageTexture() {
		return isPledging || isUnpledging || currentPage == MiddleEarthFactionsScreen.Page.RANKS;
	}

	public enum Page {
		OVERVIEW("overview"), RANKS("ranks"), GOOD_RELATIONS("goodRelations"), BAD_RELATIONS("badRelations");

		private final String pageName;

		Page(String name) {
			pageName = name;
		}

		public ITextComponent getDisplayName() {
			return new TranslationTextComponent("gui.lotr.factions.page." + pageName);
		}

		public MiddleEarthFactionsScreen.Page next() {
			int i = ordinal();
			if (i == values().length - 1) {
				return null;
			}
			++i;
			return values()[i];
		}

		public MiddleEarthFactionsScreen.Page prev() {
			int i = ordinal();
			if (i == 0) {
				return null;
			}
			--i;
			return values()[i];
		}
	}
}
