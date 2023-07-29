package lotr.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.gui.widget.button.RedBookButton;
import lotr.common.network.*;
import lotr.common.world.map.AdoptedCustomWaypoint;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.*;

public class ForsakeAdoptedCustomWaypointScreen extends CustomWaypointScreen {
	private final ViewAdoptedCustomWaypointScreen parentScreen;
	private final AdoptedCustomWaypoint theWaypoint;
	private Button forsakeButton;
	private Button cancelButton;
	private int forsakeTimer;

	public ForsakeAdoptedCustomWaypointScreen(ViewAdoptedCustomWaypointScreen parent, AdoptedCustomWaypoint wp) {
		super(new StringTextComponent("CWP"));
		parentScreen = parent;
		theWaypoint = wp;
		forsakeTimer = 20;
	}

	@Override
	public void init() {
		super.init();
		forsakeButton = this.addButton(new RedBookButton(width / 2 + 4, height / 2 + 65, 120, 20, new TranslationTextComponent("gui.lotr.cwp.forsake.do"), b -> {
			LOTRPacketHandler.sendToServer(new CPacketForsakeAdoptedCustomWaypoint(theWaypoint));
			minecraft.player.closeContainer();
		}).setRedText());
		forsakeButton.active = false;
		cancelButton = this.addButton(new RedBookButton(width / 2 - 124, height / 2 + 65, 120, 20, new TranslationTextComponent("gui.lotr.cwp.forsake.cancel"), b -> {
			onClose();
		}));
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parentScreen);
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float f) {
		this.renderBackground(matStack);
		ITextComponent title = new TranslationTextComponent("gui.lotr.cwp.forsake.title");
		drawCenteredString(matStack, font, title, width / 2, height / 2 - 90, 16777215);
		ITextComponent warning1 = new TranslationTextComponent("gui.lotr.cwp.forsake.warning.1", theWaypoint.getDisplayName());
		ITextComponent warning2 = new TranslationTextComponent("gui.lotr.cwp.forsake.warning.2");
		int warningY = drawCenteredStringLinesWrappedToWidth(matStack, font, warning1, 300, width / 2, height / 2 - 55, 16777215);
		warningY += 30;
		drawCenteredStringLinesWrappedToWidth(matStack, font, warning2, 300, width / 2, warningY, 16777215);
		super.render(matStack, mouseX, mouseY, f);
	}

	@Override
	public void tick() {
		super.tick();
		--forsakeTimer;
		if (forsakeTimer <= 0) {
			forsakeButton.active = true;
		}

	}
}
