package lotr.client.gui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.align.AlignmentFormatter;
import lotr.client.gui.PlayerMessageScreen;
import lotr.client.util.LOTRClientUtil;
import lotr.common.config.LOTRConfig;
import lotr.common.data.AlignmentDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import lotr.common.fac.AreasOfInfluence;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionRank;
import lotr.common.fac.FactionSettings;
import lotr.common.fac.FactionSettingsManager;
import lotr.common.fac.RankGender;
import lotr.common.util.LOTRUtil;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class AlignmentRenderer {
	public static final ResourceLocation ALIGNMENT_TEXTURE = new ResourceLocation("lotr", "textures/gui/alignment.png");
	private final AlignmentTextRenderer textRenderer;
	private int alignmentXBase;
	private int alignmentYBase;
	private int alignmentXCurrent;
	private int alignmentYCurrent;
	private int alignmentXPrev;
	private int alignmentYPrev;
	private boolean firstAlignmentRender = true;
	private final Map factionTickers = new HashMap();
	private int alignDrainTick;
	private int alignDrainNum;

	public AlignmentRenderer(AlignmentTextRenderer textRenderer) {
		this.textRenderer = textRenderer;
	}

	public void displayAlignmentDrain(int numFactions) {
		alignDrainTick = 200;
		alignDrainNum = numFactions;
	}

	private AlignmentTicker getOrCreateTickerForFaction(Faction fac) {
		return (AlignmentTicker) factionTickers.computeIfAbsent(fac.getName(), hummel -> new AlignmentTicker((ResourceLocation) hummel));
	}

	private boolean initialiseTickers() {
		FactionSettings currentFactionSettings = FactionSettingsManager.clientInstance().getCurrentLoadedFactions();
		if (currentFactionSettings != null) {
			List allFactions = currentFactionSettings.getAllPlayableAlignmentFactions();
			allFactions.forEach(hummel -> getOrCreateTickerForFaction((Faction) hummel));
			return true;
		}
		return false;
	}

	private boolean isBossActive() {
		return false;
	}

	private boolean isInvasionWatched() {
		return false;
	}

	public void renderAlignmentBar(MatrixStack matStack, Minecraft mc, float alignment, boolean isOtherPlayer, Faction faction, float x, float y, boolean renderFacName, boolean renderValue, boolean renderLimits, boolean renderLimitValues) {
		renderAlignmentBar1(matStack, mc, mc.player, alignment, faction, x, y, renderFacName, renderValue, renderLimits, renderLimitValues);
	}

	private void renderAlignmentBar1(MatrixStack matStack, Minecraft mc, PlayerEntity player, float alignment, Faction faction, float x, float y, boolean renderFacName, boolean renderValue, boolean renderLimits, boolean renderLimitValues) {
		LOTRPlayerData clientPD = LOTRLevelData.clientInstance().getData(player);
		AlignmentDataModule alignData = clientPD.getAlignmentData();
		FactionRank rank = faction.getRankFor(alignment);
		RankGender preferredRankGender = clientPD.getMiscData().getPreferredRankGender();
		boolean pledged = alignData.isPledgedTo(faction);
		AlignmentTicker ticker = getOrCreateTickerForFaction(faction);
		float alignMin = 0.0F;
		float alignMax = 0.0F;
		FactionRank rankMin = null;
		FactionRank rankMax = null;
		float firstRankAlign;
		if (!rank.isDummyRank()) {
			alignMin = rank.getAlignment();
			rankMin = rank;
			FactionRank nextRank = faction.getRankAbove(rank);
			if (nextRank != null && !nextRank.isDummyRank() && nextRank != rank) {
				alignMax = nextRank.getAlignment();
				rankMax = nextRank;
			} else {
				alignMax = rank.getAlignment() * 10.0F;

				for (rankMax = rank; alignment >= alignMax; alignMax *= 10.0F) {
					alignMin = alignMax;
				}
			}
		} else {
			FactionRank firstRank = faction.getFirstRank();
			if (firstRank != null && !firstRank.isDummyRank()) {
				firstRankAlign = firstRank.getAlignment();
			} else {
				firstRankAlign = 10.0F;
			}

			if (Math.abs(alignment) < firstRankAlign) {
				alignMin = -firstRankAlign;
				alignMax = firstRankAlign;
				rankMin = faction.getEnemyRank();
				rankMax = firstRank != null && !firstRank.isDummyRank() ? firstRank : faction.getNeutralRank();
			} else if (alignment < 0.0F) {
				alignMax = -firstRankAlign;
				alignMin = alignMax * 10.0F;

				for (rankMin = rankMax = faction.getEnemyRank(); alignment <= alignMin; alignMin = alignMax * 10.0F) {
					alignMax *= 10.0F;
				}
			} else {
				alignMin = firstRankAlign;
				alignMax = firstRankAlign * 10.0F;

				for (rankMin = rankMax = faction.getNeutralRank(); alignment >= alignMax; alignMax *= 10.0F) {
					alignMin = alignMax;
				}
			}
		}

		firstRankAlign = (alignment - alignMin) / (alignMax - alignMin);
		mc.getTextureManager().bind(ALIGNMENT_TEXTURE);
		int barWidth = 232;
		int barHeight = 14;
		int activeBarWidth = 220;
		float z = 0.0F;
		float[] factionColors = faction.getColorComponents();
		RenderSystem.color4f(factionColors[0], factionColors[1], factionColors[2], 1.0F);
		LOTRClientUtil.blitFloat(matStack, x - barWidth / 2, y, z, 0.0F, 14.0F, barWidth, barHeight);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		LOTRClientUtil.blitFloat(matStack, x - barWidth / 2, y, z, 0.0F, 0.0F, barWidth, barHeight);
		float ringProgressAdj = (firstRankAlign - 0.5F) * 2.0F;
		int ringSize = 16;
		float ringX = x - ringSize / 2 + ringProgressAdj * activeBarWidth / 2.0F;
		float ringY = y + barHeight / 2 - ringSize / 2;
		int flashTick = ticker.getFlashTick();
		if (pledged) {
			LOTRClientUtil.blitFloat(matStack, ringX, ringY, z, 16 * Math.round(flashTick / 3), 212.0F, ringSize, ringSize);
		} else {
			LOTRClientUtil.blitFloat(matStack, ringX, ringY, z, 16 * Math.round(flashTick / 3), 36.0F, ringSize, ringSize);
		}

		int numericalTick;
		if (faction.isPlayableAlignmentFaction()) {
			float alpha = 0.0F;
			boolean definedZone = false;
			AreasOfInfluence areasOfInfluence = faction.getAreasOfInfluence();
			if (areasOfInfluence.isInArea(player)) {
				alpha = 1.0F;
				definedZone = areasOfInfluence.isInDefinedArea(player);
			} else {
				alpha = areasOfInfluence.getAlignmentMultiplier(player);
				definedZone = true;
			}

			if (alpha > 0.0F) {
				int arrowSize = 14;
				int y0 = definedZone ? 60 : 88;
				numericalTick = definedZone ? 74 : 102;
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.color4f(factionColors[0], factionColors[1], factionColors[2], alpha);
				LOTRClientUtil.blitFloat(matStack, x - barWidth / 2 - arrowSize, y, z, 0.0F, numericalTick, arrowSize, arrowSize);
				LOTRClientUtil.blitFloat(matStack, x + barWidth / 2, y, z, arrowSize, numericalTick, arrowSize, arrowSize);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
				LOTRClientUtil.blitFloat(matStack, x - barWidth / 2 - arrowSize, y, z, 0.0F, y0, arrowSize, arrowSize);
				LOTRClientUtil.blitFloat(matStack, x + barWidth / 2, y, z, arrowSize, y0, arrowSize, arrowSize);
				RenderSystem.disableBlend();
			}
		}

		FontRenderer fr = mc.font;
		int textX = Math.round(x);
		int textY = Math.round(y + barHeight + 4.0F);
		String alignS;
		if (renderLimits) {
			alignS = rankMin.getDisplayShortName(preferredRankGender);
			String sMax = rankMax.getDisplayShortName(preferredRankGender);
			if (renderLimitValues) {
				alignS = I18n.get("gui.lotr.alignment.limits", alignS, AlignmentFormatter.formatAlignForDisplay(alignMin));
				sMax = I18n.get("gui.lotr.alignment.limits", sMax, AlignmentFormatter.formatAlignForDisplay(alignMax));
			}

			numericalTick = barWidth / 2 - 6;
			int xMin = Math.round(x - numericalTick);
			int xMax = Math.round(x + numericalTick);
			matStack.pushPose();
			matStack.scale(0.5F, 0.5F, 0.5F);
			textRenderer.drawAlignmentText(matStack, fr, xMin * 2 - fr.width(alignS) / 2, textY * 2, new StringTextComponent(alignS), 1.0F);
			textRenderer.drawAlignmentText(matStack, fr, xMax * 2 - fr.width(sMax) / 2, textY * 2, new StringTextComponent(sMax), 1.0F);
			matStack.popPose();
		}

		if (renderFacName) {
			ITextComponent name = faction.getDisplayName();
			textRenderer.drawAlignmentText(matStack, fr, textX - fr.width(name) / 2, textY, name, 1.0F);
		}

		if (renderValue) {
			alignS = AlignmentFormatter.formatAlignForDisplay(alignment);
			float alignAlpha = 1.0F;
			numericalTick = ticker.getDisplayNumericalTick();
			if (numericalTick > 0) {
				alignS = AlignmentFormatter.formatAlignForDisplay(alignment);
				alignAlpha = LOTRUtil.normalisedTriangleWave(numericalTick, 30.0F, 0.7F, 1.0F);
				int fadeTick = 15;
				if (numericalTick < fadeTick) {
					alignAlpha *= (float) numericalTick / (float) fadeTick;
				}
			} else {
				alignS = rank.getDisplayShortName(preferredRankGender);
				alignAlpha = 1.0F;
			}

			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			AlignmentTextRenderer var10000 = textRenderer;
			int var10003 = textX - fr.width(alignS) / 2;
			fr.getClass();
			var10000.drawAlignmentText(matStack, fr, var10003, textY + 9 + 3, new StringTextComponent(alignS), alignAlpha);
			RenderSystem.disableBlend();
		}

	}

	public void renderAlignmentDrain(MatrixStack matStack, Minecraft mc, int x, int y, int numFactions) {
		this.renderAlignmentDrain(matStack, mc, x, y, numFactions, 1.0F);
	}

	public void renderAlignmentDrain(MatrixStack matStack, Minecraft mc, int x, int y, int numFactions, float alpha) {
		float z = 0.0F;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		mc.getTextureManager().bind(ALIGNMENT_TEXTURE);
		LOTRClientUtil.blitFloat(matStack, x, y, z, 0.0F, 128.0F, 16.0F, 16.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		ITextComponent drainText = new StringTextComponent("-" + numFactions);
		FontRenderer fr = mc.font;
		AlignmentTextRenderer var10000 = textRenderer;
		int var10003 = x + 8 - fr.width(drainText) / 2;
		int var10004 = y + 8;
		fr.getClass();
		var10000.drawBorderedText(matStack, fr, var10003, var10004 - 9 / 2, drainText, 16777215, alpha);
		RenderSystem.disableBlend();
	}

	public void renderAlignmentHUDBar(MatrixStack matStack, Minecraft mc, PlayerEntity player, float partialTick) {
		if (!firstAlignmentRender) {
			LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
			AlignmentDataModule alignData = pd.getAlignmentData();
			Faction viewingFac = alignData.getCurrentViewedFaction();
			if (viewingFac != null) {
				float alignmentXF = alignmentXPrev + (alignmentXCurrent - alignmentXPrev) * partialTick;
				float alignmentYF = alignmentYPrev + (alignmentYCurrent - alignmentYPrev) * partialTick;
				boolean text = alignmentYCurrent == alignmentYBase;
				float alignment = getOrCreateTickerForFaction(viewingFac).getInterpolatedAlignment(partialTick);
				renderAlignmentBar1(matStack, mc, player, alignment, viewingFac, alignmentXF, alignmentYF, text, text, text, false);
				if (alignDrainTick > 0 && text) {
					float alpha = 1.0F;
					int fadeTick = 20;
					if (alignDrainTick < fadeTick) {
						alpha = (float) alignDrainTick / (float) fadeTick;
					}

					this.renderAlignmentDrain(matStack, mc, (int) alignmentXF - 155, (int) alignmentYF + 2, alignDrainNum, alpha);
				}
			}

		}
	}

	public void resetInMenu() {
		firstAlignmentRender = true;
		factionTickers.clear();
		alignDrainTick = 0;
		alignDrainNum = 0;
	}

	private void setBarBasePosition(Minecraft mc) {
		MainWindow mainWindow = mc.getWindow();
		int width = mainWindow.getGuiScaledWidth();
		mainWindow.getGuiScaledHeight();
		alignmentXBase = width / 2 + (Integer) LOTRConfig.CLIENT.alignmentXOffset.get();
		alignmentYBase = 4 + (Integer) LOTRConfig.CLIENT.alignmentYOffset.get();
		if (isBossActive()) {
			alignmentYBase += 20;
		}

		if (isInvasionWatched()) {
			alignmentYBase += 20;
		}

	}

	private void updateAllTickers(PlayerEntity player, boolean forceInstant) {
		factionTickers.values().forEach(ticker -> {
			((AlignmentTicker) ticker).update(player, forceInstant);
		});
	}

	private void updateBarCurrentPosition(Minecraft mc) {
		alignmentXPrev = alignmentXCurrent;
		alignmentYPrev = alignmentYCurrent;
		alignmentXCurrent = alignmentXBase;
		int yMove = (int) ((alignmentYBase - -20) / 3.0F);
		boolean alignmentOnscreen = (mc.screen == null || mc.screen instanceof PlayerMessageScreen) && !mc.options.keyPlayerList.isDown() && !mc.options.renderDebug;
		if (alignmentOnscreen) {
			alignmentYCurrent = Math.min(alignmentYCurrent + yMove, alignmentYBase);
		} else {
			alignmentYCurrent = Math.max(alignmentYCurrent - yMove, -20);
		}

	}

	public void updateHUD(Minecraft mc, PlayerEntity player) {
		setBarBasePosition(mc);
		updateBarCurrentPosition(mc);
		if (firstAlignmentRender) {
			if (initialiseTickers()) {
				updateAllTickers(player, true);
				alignmentXPrev = alignmentXCurrent = alignmentXBase;
				alignmentYPrev = alignmentYCurrent = -20;
				firstAlignmentRender = false;
			}
		} else {
			updateAllTickers(player, false);
		}

		if (alignDrainTick > 0) {
			--alignDrainTick;
			if (alignDrainTick <= 0) {
				alignDrainNum = 0;
			}
		}

	}
}
