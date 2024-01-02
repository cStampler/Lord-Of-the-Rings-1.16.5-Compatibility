package lotr.client.gui.map;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.MapImageTextures;
import lotr.common.world.map.Waypoint;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

public class WaypointTooltipRenderer extends MapTooltipRenderer<Waypoint> {
	private List<TooltipSection> sections = new ArrayList<>();

	private int calculateSectionsTotalHeight() {
		return (Integer) sections.stream().collect(Collectors.summingInt((TooltipSection section) -> section.height));
	}

	private ITextComponent getCoordsText(Waypoint waypoint) {
		return waypoint.getCoordsText();
	}

	private ITextComponent getDistanceText(Waypoint waypoint) {
		if (mc.player != null) {
			double dist = waypoint.getDistanceFromPlayer(mc.player);
			return WaypointDistanceDisplay.getDistanceText(dist);
		}
		return null;
	}

	private ITextComponent getLoreText(Waypoint waypoint) {
		return waypoint.getDisplayLore();
	}

	private ITextComponent getNameText(Waypoint waypoint) {
		return waypoint.getDisplayName();
	}

	private ITextComponent getNumTravelsText(Waypoint waypoint, boolean selected) {
		if (selected && mc.player != null) {
			int numTravels = mapScreen.getClientPlayerData().getFastTravelData().getWPUseCount(waypoint);
			if (numTravels > 0 && waypoint.hasPlayerUnlocked(mc.player)) {
				return new StringTextComponent(String.valueOf(numTravels));
			}
		}

		return null;
	}

	private ITextComponent getOwnershipText(Waypoint waypoint) {
		return waypoint.getDisplayOwnership();
	}

	@Override
	public void render(MatrixStack matStack, Waypoint waypoint, boolean selected, int mouseX, int mouseY, float tick) {
		float expandProgress = getSelectionExpandProgress();
		float textAlpha = getExpandingTextAlpha();
		ITextComponent name = getNameText(waypoint);
		ITextComponent coords = getCoordsText(waypoint);
		ITextComponent distanceText = getDistanceText(waypoint);
		ITextComponent loreText = getLoreText(waypoint);
		ITextComponent ownershipText = getOwnershipText(waypoint);
		ITextComponent numTravelsText = getNumTravelsText(waypoint, selected);
		float guiScale = (float) mc.getWindow().getGuiScale();
		float loreScale = guiScale - 1.0F;
		if (guiScale <= 2.0F) {
			loreScale = guiScale;
		}

		float loreScaleRel = loreScale / guiScale;
		float loreScaleRelInv = 1.0F / loreScaleRel;
		font.getClass();
		int loreFontHeight = MathHelper.ceil(9.0F * loreScaleRel);
		double[] pos = mapScreen.transformMapCoords(waypoint.getMapX(), waypoint.getMapZ(), tick);
		int rectX = (int) Math.round(pos[0]);
		int rectY = (int) Math.round(pos[1]);
		rectY += 5;
		int border = 3;
		font.getClass();
		int fontHeight = 9;
		int innerRectWidth = font.width(name);
		int innerRectWidthCompletelyExpanded = innerRectWidth;
		int rectWidth;
		if (selected) {
			rectWidth = Math.max(innerRectWidth, font.width(coords));
			if (loreText != null) {
				rectWidth += 50;
				rectWidth = Math.round(rectWidth * (loreScaleRel / 0.66667F));
			}

			innerRectWidth = (int) MathHelper.lerp(expandProgress, innerRectWidth, rectWidth);
			innerRectWidthCompletelyExpanded = rectWidth;
		}

		rectWidth = innerRectWidth + border * 2;
		rectX -= rectWidth / 2;
		sections.clear();
		sections.add(new WaypointTooltipRenderer.TooltipSection(fontHeight + border * 2, (midX, y, highlight) -> {
			mapScreen.drawCenteredStringNoShadow(matStack, font, name, midX, y + border, getTextColor(highlight, 1.0F));
		}));
		int rectHeight = calculateSectionsTotalHeight();
		if (selected) {
			int coordsAndTravelsHeight = fontHeight + border;
			if (numTravelsText != null) {
				coordsAndTravelsHeight += loreFontHeight + border;
			}

			sections.add(new WaypointTooltipRenderer.TooltipSection(coordsAndTravelsHeight, (midX, y, highlight) -> {
				if (textAlpha > 0.0F) {
					mapScreen.drawCenteredStringNoShadow(matStack, font, coords, midX, y, getTextColor(highlight, textAlpha));
					if (numTravelsText != null) {
						y += fontHeight + border;
						int iconSize = 8;
						int iconPlusTextWidth = iconSize + border + (int) (font.width(numTravelsText) * loreScaleRel);
						mc.getTextureManager().bind(MapImageTextures.MAP_ICONS);
						float brightness = highlight ? 1.0F : 0.82F;
						RenderSystem.color4f(brightness, brightness, brightness, textAlpha);
						RenderSystem.enableBlend();
						RenderSystem.defaultBlendFunc();
						mapScreen.blit(matStack, midX - iconPlusTextWidth / 2, y, 0, 216, iconSize, iconSize);
						RenderSystem.disableBlend();
						RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
						matStack.pushPose();
						matStack.scale(loreScaleRel, loreScaleRel, 1.0F);
						font.draw(matStack, numTravelsText, (int) ((midX - iconPlusTextWidth / 2 + iconSize + border) * loreScaleRelInv), (int) ((y + (fontHeight - loreFontHeight) / 2) * loreScaleRelInv), getTextColor(highlight, textAlpha));
						matStack.popPose();
					}
				}

			}));
			if (distanceText != null) {
				sections.add(new WaypointTooltipRenderer.TooltipSection(fontHeight + border, (midX, y, highlight) -> {
					if (textAlpha > 0.0F) {
						mapScreen.drawCenteredStringNoShadow(matStack, font, distanceText, midX, y, getTextColor(highlight, textAlpha));
					}

				}));
			}
			if (distanceText != null)
		        this.sections.add(new TooltipSection(fontHeight + border, (midX, y, highlight) -> {
		                if (textAlpha > 0.0F)
		                  this.mapScreen.drawCenteredStringNoShadow(matStack, this.font, distanceText, midX, y, getTextColor(highlight, textAlpha)); 
		              })); 
		      int stableRectWidth = innerRectWidthCompletelyExpanded;
		      if (loreText != null) {
		        List<IReorderingProcessor> loreLines = this.font.split((ITextProperties)loreText, (int)(stableRectWidth * loreScaleRelInv));
		        int loreTextHeight = loreLines.size() * loreFontHeight;
		        this.sections.add(new TooltipSection(loreTextHeight + border * 2, (midX, y, highlight) -> {
		                if (textAlpha > 0.0F) {
		                  y += border;
		                  matStack.pushPose();
		                  matStack.scale(loreScaleRel, loreScaleRel, 1.0F);
		                  for (IReorderingProcessor line : loreLines) {
		                    this.mapScreen.drawCenteredStringNoShadow(matStack, this.font, line, (int)(midX * loreScaleRelInv), (int)(y * loreScaleRelInv), getTextColor(highlight, textAlpha));
		                    y += loreFontHeight;
		                  } 
		                  matStack.popPose();
		                } 
		              }));
		      } 
		      if (ownershipText != null) {
		        List<IReorderingProcessor> ownershipLines = this.font.split((ITextProperties)ownershipText, (int)(stableRectWidth * loreScaleRelInv));
		        int ownershipTextHeight = ownershipLines.size() * loreFontHeight;
		        this.sections.add(new TooltipSection(ownershipTextHeight + border, (midX, y, highlight) -> {
		                if (textAlpha > 0.0F) {
		                  matStack.pushPose();
		                  matStack.scale(loreScaleRel, loreScaleRel, 1.0F);
		                  for (IReorderingProcessor line : ownershipLines) {
		                    this.mapScreen.drawCenteredStringNoShadow(matStack, this.font, line, (int)(midX * loreScaleRelInv), (int)(y * loreScaleRelInv), getTextColor(highlight, textAlpha));
		                    y += loreFontHeight;
		                  } 
		                  matStack.popPose();
		                } 
		              }));
		      } 
		    int expandedRectHeight = calculateSectionsTotalHeight();
			rectHeight = (int) MathHelper.lerp(expandProgress, rectHeight, expandedRectHeight);
		}

		int mapBorder2 = 2;
		rectX = Math.max(rectX, mapXMin + mapBorder2);
		rectX = Math.min(rectX, mapXMax - mapBorder2 - rectWidth);
		rectY = Math.max(rectY, mapYMin + mapBorder2);
		rectY = Math.min(rectY, mapYMax - mapBorder2 - rectHeight);
		boolean mouseWithinTooltip = mouseX >= rectX && mouseX <= rectX + rectWidth && mouseY >= rectY && mouseY <= rectY + rectHeight;
		matStack.pushPose();
		matStack.translate(0.0D, 0.0D, 300.0D);
		mapScreen.drawFancyRect(matStack, rectX, rectY, rectX + rectWidth, rectY + rectHeight);
		int midX = rectX + rectWidth / 2;
		int sectionY = rectY;
		TooltipSection highlightedSection = null;
	    if (mouseWithinTooltip)
	      for (TooltipSection section : this.sections) {
	        if (mouseY >= sectionY && mouseY < sectionY + section.height) {
	          highlightedSection = section;
	          break;
	        } 
	        sectionY += section.height;
	      }

	    if (highlightedSection == null && !this.sections.isEmpty())
	        highlightedSection = this.sections.get(0); 
	    sectionY = rectY;
	      for (TooltipSection section : this.sections) {
	        section.renderer.render(midX, sectionY, (section == highlightedSection));
	        sectionY += section.height;
	      }

		matStack.popPose();
	}

	@FunctionalInterface
	private interface SectionRenderer {
		void render(int var1, int var2, boolean var3);
	}

	private static class TooltipSection {
		private final int height;
		private final WaypointTooltipRenderer.SectionRenderer renderer;

		public TooltipSection(int height, WaypointTooltipRenderer.SectionRenderer renderer) {
			this.height = height;
			this.renderer = renderer;
		}
	}
}
