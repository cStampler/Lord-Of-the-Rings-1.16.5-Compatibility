package lotr.client.gui;

import lotr.client.LOTRKeyHandler;
import lotr.client.gui.widget.button.LeftRightButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.*;

public abstract class MiddleEarthMenuScreen extends BasicIngameScreen {
	public int xSize = 200;
	public int ySize = 256;
	public int guiLeft;
	public int guiTop;
	protected Button buttonMenuReturn;

	public MiddleEarthMenuScreen(ITextComponent titleComponent) {
		super(titleComponent);
	}

	@Override
	public void init() {
		super.init();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		int buttonW = 100;
		int buttonH = 20;
		int buttonGap = 40;
		buttonMenuReturn = this.addButton(new LeftRightButton(0, guiTop + (ySize + buttonH) / 4, buttonW, buttonH, true, new TranslationTextComponent("gui.lotr.menu.return"), b -> {
			minecraft.setScreen(new MiddleEarthMasterMenuScreen());
		}));
		buttonMenuReturn.x = Math.max(0, guiLeft - buttonGap - buttonW);
	}

	@Override
	public boolean keyPressed(int key, int scan, int param3) {
		if (LOTRKeyHandler.KEY_BIND_MENU.matches(key, scan)) {
			minecraft.setScreen(new MiddleEarthMasterMenuScreen());
			return true;
		}
		return super.keyPressed(key, scan, param3);
	}
}
