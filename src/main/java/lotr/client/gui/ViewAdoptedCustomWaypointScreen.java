package lotr.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.gui.widget.button.RedBookButton;
import lotr.common.world.map.AdoptedCustomWaypoint;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.*;

public class ViewAdoptedCustomWaypointScreen extends CustomWaypointScreen {
	private final AdoptedCustomWaypoint theWaypoint;
	private final String createdPlayerName;
	private Button forsakeButton;

	public ViewAdoptedCustomWaypointScreen(AdoptedCustomWaypoint wp, String playerName) {
		super(new StringTextComponent("CWP"));
		theWaypoint = wp;
		createdPlayerName = playerName;
	}

	@Override
	public void init() {
		super.init();
		forsakeButton = this.addButton(new RedBookButton(width / 2 - 60, height / 2 + 65, 120, 20, new TranslationTextComponent("gui.lotr.cwp.adopted.forsake"), b -> {
			minecraft.setScreen(new ForsakeAdoptedCustomWaypointScreen(this, theWaypoint));
		}));
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float f) {
		this.renderBackground(matStack);
		ITextComponent title = new TranslationTextComponent("gui.lotr.cwp.adopted.title");
		drawCenteredString(matStack, font, title, width / 2, height / 2 - 90, 16777215);
		ITextComponent wpName = theWaypoint.getDisplayName();
		ITextComponent byline = new TranslationTextComponent("gui.lotr.cwp.adopted.owner", createdPlayerName);
		drawCenteredString(matStack, font, wpName, width / 2, height / 2 - 55, 16777215);
		drawCenteredString(matStack, font, byline, width / 2, height / 2 - 40, 12632256);
		ITextComponent lore = theWaypoint.getDisplayLore();
		if (lore != null) {
			drawCenteredStringLinesWrappedToWidth(matStack, font, lore, 300, width / 2, height / 2 - 20, 12632256);
		}

		super.render(matStack, mouseX, mouseY, f);
	}
}
