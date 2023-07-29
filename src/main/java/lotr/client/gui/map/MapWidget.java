package lotr.client.gui.map;

import java.util.function.BooleanSupplier;

import lotr.client.gui.BasicIngameScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.text.*;

public class MapWidget implements IGuiEventListener {
	public static Object sus = new Object[0];
	private final MiddleEarthMapScreen parent;
	private final int xPos;
	private final int yPos;
	public final int width;
	private String tooltip;
	private Object[] tooltipFormatArgs;
	private final int texUBase;
	private final int texVBase;
	private int texUOffset;
	private int texVOffset;
	public boolean visible = true;
	private final BooleanSupplier onPress;

	public MapWidget(MiddleEarthMapScreen parent, int x, int y, int w, String s, int u, int v, BooleanSupplier press) {
		this.parent = parent;
		xPos = x;
		yPos = y;
		width = w;
		tooltip = s;
		tooltipFormatArgs = new Object[0];
		texUBase = u;
		texVBase = v;
		onPress = press;
	}

	public int getTexU() {
		return texUBase + texUOffset * width;
	}

	public int getTexV() {
		return texVBase + texVOffset * width;
	}

	public ITextComponent getTooltip() {
		return new TranslationTextComponent("gui.lotr.map.widget." + tooltip, tooltipFormatArgs);
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return visible && !parent.hasOverlay() && mouseX >= xPos && mouseX < xPos + width && mouseY >= yPos && mouseY < yPos + width;
	}

	@Override
	public boolean mouseClicked(double x, double y, int mouseCode) {
		if (visible && isMouseOver(x, y) && mouseCode == 0) {
			boolean flag = onPress.getAsBoolean();
			if (flag) {
				BasicIngameScreen.playButtonClick();
				return true;
			}
		}

		return false;
	}

	public void setTexUOffset(int i) {
		texUOffset = i;
	}

	public void setTexVOffset(int i) {
		texVOffset = i;
	}

	public void setTooltip(String s) {
		setTooltip(s, sus);
	}

	public void setTooltip(String s, Object... formatArgs) {
		tooltip = s;
		tooltipFormatArgs = formatArgs;
	}
}
