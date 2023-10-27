package lotr.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.gui.map.MiddleEarthMapScreen;
import lotr.client.gui.widget.button.MiddleEarthMenuButton;
import lotr.common.init.LOTRDimensions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MiddleEarthMasterMenuScreen extends BasicIngameScreen {
	public static final ResourceLocation MENU_ICONS = new ResourceLocation("lotr", "textures/gui/menu_icons.png");
	public static Class lastMenuScreen = null;

	public MiddleEarthMasterMenuScreen() {
		super(new StringTextComponent("MENU"));
	}

	@Override
	public void init() {
		super.init();
		resetLastMenuScreen();
		int midX = width / 2;
		int midY = height / 2;
		int buttonGap = 10;
		int buttonSize = 32;
		this.addButton(new MiddleEarthMenuButton(0, 0, MiddleEarthMapScreen.class, new TranslationTextComponent("gui.lotr.menu.map"), 3, 77));
		this.addButton(new MiddleEarthMenuButton(0, 0, MiddleEarthFactionsScreen.class, new TranslationTextComponent("gui.lotr.menu.factions"), 4, 70));
		this.addButton(new MiddleEarthMenuButton(0, 0, (Class) null, new StringTextComponent("?"), 0, -1));
		List menuButtonsToArrange = new ArrayList();
		for (Widget widget : buttons) {
			if (widget instanceof MiddleEarthMenuButton) {
				MiddleEarthMenuButton menuButton = (MiddleEarthMenuButton) widget;
				menuButton.active = menuButton.canDisplayMenu();
				menuButtonsToArrange.add(menuButton);
			}
		}

		int numButtons = menuButtonsToArrange.size();
		int numTopRowButtons = (numButtons - 1) / 2 + 1;
		int numBtmRowButtons = numButtons - numTopRowButtons;
		int topRowLeft = midX - (numTopRowButtons * buttonSize + (numTopRowButtons - 1) * buttonGap) / 2;
		int btmRowLeft = midX - (numBtmRowButtons * buttonSize + (numBtmRowButtons - 1) * buttonGap) / 2;

		for (int l = 0; l < numButtons; ++l) {
			MiddleEarthMenuButton button = (MiddleEarthMenuButton) menuButtonsToArrange.get(l);
			if (l < numTopRowButtons) {
				button.x = topRowLeft + l * (buttonSize + buttonGap);
				button.y = midY - buttonGap / 2 - buttonSize;
			} else {
				button.x = btmRowLeft + (l - numTopRowButtons) * (buttonSize + buttonGap);
				button.y = midY + buttonGap / 2;
			}
		}

	}

	@Override
	public boolean keyPressed(int key, int scan, int param3) {
		for (Widget widget : buttons) {
			if (widget instanceof MiddleEarthMenuButton) {
				MiddleEarthMenuButton menuButton = (MiddleEarthMenuButton) widget;
				if (menuButton.visible && menuButton.active && menuButton.menuKeyCode >= 0 && key == menuButton.menuKeyCode) {
					menuButton.onPress();
					return true;
				}
			}
		}

		return super.keyPressed(key, scan, param3);
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float tick) {
		this.renderBackground(matStack);
		ITextComponent dimensionName = LOTRDimensions.getDisplayName(LOTRDimensions.getCurrentLOTRDimensionOrFallback(minecraft.level));
		ITextComponent title = new TranslationTextComponent("gui.lotr.menu", dimensionName);
		font.drawShadow(matStack, title, width / 2 - font.width(title) / 2, height / 2 - 80, 16777215);
		super.render(matStack, mouseX, mouseY, tick);
		for (Widget widget : buttons) {
			if (widget instanceof MiddleEarthMenuButton) {
				MiddleEarthMenuButton menuButton = (MiddleEarthMenuButton) widget;
				if (menuButton.isHovered() && menuButton.getMessage() != null) {
					this.renderTooltip(matStack, menuButton.getMessage(), mouseX, mouseY);
				}
			}
		}

	}

	public static Screen openMenu(PlayerEntity player) {
		if (lastMenuScreen != null) {
			try {
				return (Screen) lastMenuScreen.newInstance();
			} catch (Exception var2) {
				var2.printStackTrace();
			}
		}

		return new MiddleEarthMasterMenuScreen();
	}

	public static void resetLastMenuScreen() {
		lastMenuScreen = null;
	}
}
