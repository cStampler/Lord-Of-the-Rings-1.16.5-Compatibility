package lotr.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.MiddleEarthMasterMenuScreen;
import lotr.client.gui.MiddleEarthMenuScreen;
import lotr.client.gui.map.MiddleEarthMapScreen;
import lotr.common.LOTRLog;
import lotr.common.init.LOTRWorldTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class MiddleEarthMenuButton extends Button {
	private final Class menuScreenClass;
	private final int verticalIconNumber;
	public final int menuKeyCode;

	public MiddleEarthMenuButton(int xIn, int yIn, Class cls, ITextComponent text, int vertNumber, int key) {
		super(xIn, yIn, 32, 32, text, button -> {
			((MiddleEarthMenuButton) button).openMenuScreen();
		});
		menuScreenClass = cls;
		verticalIconNumber = vertNumber;
		menuKeyCode = key;
	}

	public boolean canDisplayMenu() {
		return menuScreenClass == MiddleEarthMapScreen.class ? LOTRWorldTypes.hasMapFeaturesClientside() : true;
	}

	public void openMenuScreen() {
		if (menuScreenClass != null && canDisplayMenu()) {
			try {
				MiddleEarthMenuScreen screen = (MiddleEarthMenuScreen) menuScreenClass.newInstance();
				Minecraft.getInstance().setScreen(screen);
				MiddleEarthMasterMenuScreen.lastMenuScreen = screen.getClass();
			} catch (Exception var2) {
				LOTRLog.error("Error opening menu button screen");
				var2.printStackTrace();
			}
		}

	}

	@Override
	public void renderButton(MatrixStack matStack, int mouseX, int mouseY, float f) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bind(MiddleEarthMasterMenuScreen.MENU_ICONS);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		this.blit(matStack, x, y, 0 + (active ? 0 : width * 2) + (isHovered() ? width : 0), verticalIconNumber * height, width, height);
	}
}
