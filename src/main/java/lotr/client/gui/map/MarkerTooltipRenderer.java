package lotr.client.gui.map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.MapImageTextures;
import lotr.client.util.LOTRClientUtil;
import lotr.common.world.map.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class MarkerTooltipRenderer extends MapTooltipRenderer<MapMarker> {
	private static final int NUM_ICONS = MapMarkerIcon.values().length;
	private TextFieldWidget nameField;
	private MapMarker selectedMarker;
	private MapMarkerIcon mouseOverIcon;

	@Override
	public boolean charTyped(char c, int modifiers) {
		return selectedMarker != null && nameField.charTyped(c, modifiers);
	}

	@Override
	public void init(MiddleEarthMapScreen mapScreen, Minecraft mc, FontRenderer font) {
		super.init(mapScreen, mc, font);
		font.getClass();
		nameField = new TextFieldWidget(font, 0, 0, 200, 9, nameField, (ITextComponent) null);
		nameField.setMaxLength(32);
		nameField.setBordered(false);
		nameField.setTextColor(16777215);
	}

	@Override
	public boolean isTextFieldFocused() {
		return selectedMarker != null && nameField.canConsumeInput();
	}

	@Override
	public boolean keyPressed(int key, int scan, int param3) {
		return selectedMarker != null && nameField.keyPressed(key, scan, param3);
	}

	@Override
	public boolean mouseClicked(double x, double y, int code) {
		if (selectedMarker != null && mouseOverIcon != null && code == 0) {
			selectedMarker.changeIconAndSendToServer(mouseOverIcon);
			MiddleEarthMapScreen.playMarkerUpdateSound();
			return true;
		}
		return selectedMarker != null && nameField.mouseClicked(x, y, code) ? true : super.mouseClicked(x, y, code);
	}

	public void onSelect(MapMarker marker) {
		if (selectedMarker != null) {
			String rename = nameField.getValue().trim();
			if (!StringUtils.isNullOrEmpty(rename) && !rename.equals(selectedMarker.getName())) {
				selectedMarker.renameAndSendToServer(rename);
				MiddleEarthMapScreen.playMarkerUpdateSound();
			}
		}

		selectedMarker = marker;
		if (marker != null) {
			nameField.setValue(marker.getName());
			nameField.setFocus(true);
		} else {
			nameField.setValue("");
			nameField.setFocus(false);
		}

	}

	@Override
	public void render(MatrixStack matStack, MapMarker marker, boolean selected, int mouseX, int mouseY, float tick) {
		mouseOverIcon = null;
		float expandProgress = getSelectionExpandProgress();
		float expandAlpha = getExpandingTextAlpha();
		String name = marker.getName();
		double[] pos = mapScreen.transformMapCoords(marker.getMapX(), marker.getMapZ(), tick);
		int rectX = (int) Math.round(pos[0]);
		int rectY = (int) Math.round(pos[1]);
		int strWidth = font.width(name);
		font.getClass();
		int strHeight = 9;
		rectY += 7;
		int border = 3;
		int innerRectWidth = strWidth;
		int innerRectHeight = strHeight;
		int rectWidth;
		int rectHeight;
		if (selected) {
			rectWidth = MapMarkerIcon.values().length;
			rectHeight = Math.max(strWidth, NUM_ICONS * 12);
			rectHeight = Math.max(rectHeight, nameField.getWidth());
			int innerRectHeightExpanded = strHeight + border + 12;
			innerRectWidth = (int) MathHelper.lerp(expandProgress, strWidth, rectHeight);
			innerRectHeight = (int) MathHelper.lerp(expandProgress, strHeight, innerRectHeightExpanded);
		}

		rectWidth = innerRectWidth + border * 2;
		rectX -= rectWidth / 2;
		rectHeight = innerRectHeight + border * 2;
		int mapBorder2 = 2;
		rectX = Math.max(rectX, mapXMin + mapBorder2);
		rectX = Math.min(rectX, mapXMax - mapBorder2 - rectWidth);
		rectY = Math.max(rectY, mapYMin + mapBorder2);
		rectY = Math.min(rectY, mapYMax - mapBorder2 - rectHeight);
		matStack.pushPose();
		matStack.translate(0.0D, 0.0D, 300.0D);
		mapScreen.drawFancyRect(matStack, rectX, rectY, rectX + rectWidth, rectY + rectHeight);
		int midX = rectX + rectWidth / 2;
		if (selected && expandProgress == 1.0F) {
			nameField.x = midX - font.width(nameField.getValue()) / 2;
			nameField.y = rectY + border;
			nameField.render(matStack, mouseX, mouseY, tick);
		} else {
			mapScreen.drawCenteredStringNoShadow(matStack, font, name, midX, rectY + border, 16777215);
		}

		if (selected && expandAlpha > 0.0F) {
			int iconBarWidth = NUM_ICONS * 12;
			int iconLeftX = midX - iconBarWidth / 2;
			int iconY = rectY + border + strHeight + border;
			boolean mouseInBox = mouseY >= iconY && mouseY < iconY + 12;
			AbstractGui.fill(matStack, iconLeftX, iconY, iconLeftX + iconBarWidth, iconY + 12, LOTRClientUtil.getRGBA(14399895, expandAlpha));
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, expandAlpha);
			mc.getTextureManager().bind(MapImageTextures.MAP_ICONS);

			for (int i = 0; i < NUM_ICONS; ++i) {
				MapMarkerIcon icon = MapMarkerIcon.values()[i];
				int iconX = iconLeftX + i * 12;
				boolean highlight = icon == marker.getIcon();
				if (mouseInBox) {
					highlight = mouseX >= iconX && mouseX < iconX + 12;
					if (highlight) {
						mouseOverIcon = icon;
					}
				}

				mapScreen.blit(matStack, iconX + 1, iconY + 1, icon.getU(highlight), icon.getV(highlight), 10, 10);
			}

			RenderSystem.disableBlend();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		matStack.popPose();
	}

	@Override
	public void tick() {
		nameField.tick();
	}
}
