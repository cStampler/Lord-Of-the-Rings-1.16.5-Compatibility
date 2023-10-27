package lotr.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.gui.widget.button.RedBookButton;
import lotr.common.network.CPacketAdoptCustomWaypoint;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.world.map.CustomWaypoint;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class AdoptCustomWaypointScreen extends CustomWaypointScreen {
	private final CustomWaypoint waypointToAdopt;
	private final String createdPlayerName;
	private Button adoptButton;

	public AdoptCustomWaypointScreen(CustomWaypoint wp, String playerName) {
		super(new StringTextComponent("CWP"));
		waypointToAdopt = wp;
		createdPlayerName = playerName;
	}

	@Override
	public void init() {
		super.init();
		adoptButton = this.addButton(new RedBookButton(width / 2 - 60, height / 2 + 65, 120, 20, new TranslationTextComponent("gui.lotr.cwp.adopt.do"), b -> {
			LOTRPacketHandler.sendToServer(new CPacketAdoptCustomWaypoint(waypointToAdopt));
			minecraft.player.closeContainer();
		}));
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float f) {
		this.renderBackground(matStack);
		ITextComponent title = new TranslationTextComponent("gui.lotr.cwp.adopt.title");
		drawCenteredString(matStack, font, title, width / 2, height / 2 - 90, 16777215);
		ITextComponent wpName = waypointToAdopt.getDisplayName();
		ITextComponent byline = new TranslationTextComponent("gui.lotr.cwp.adopt.owner", createdPlayerName);
		drawCenteredString(matStack, font, wpName, width / 2, height / 2 - 55, 16777215);
		drawCenteredString(matStack, font, byline, width / 2, height / 2 - 40, 12632256);
		ITextComponent lore = waypointToAdopt.getDisplayLore();
		if (lore != null) {
			drawCenteredStringLinesWrappedToWidth(matStack, font, lore, 300, width / 2, height / 2 - 20, 12632256);
		}

		ITextComponent helpLine1 = new TranslationTextComponent("gui.lotr.cwp.adopt.help.1");
		ITextComponent helpLine2 = new TranslationTextComponent("gui.lotr.cwp.adopt.help.2");
		int help1Y = drawCenteredStringLinesWrappedToWidth(matStack, font, helpLine1, 300, width / 2, height / 2 + 20, 16777215);
		drawCenteredStringLinesWrappedToWidth(matStack, font, helpLine2, 300, width / 2, help1Y + 6, 16777215);
		super.render(matStack, mouseX, mouseY, f);
	}
}
