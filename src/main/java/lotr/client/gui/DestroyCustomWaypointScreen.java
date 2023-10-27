package lotr.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.gui.widget.button.RedBookButton;
import lotr.common.network.CPacketDestroyCustomWaypoint;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.world.map.CustomWaypoint;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class DestroyCustomWaypointScreen extends CustomWaypointScreen {
	private final UpdateCustomWaypointScreen parentScreen;
	private final CustomWaypoint theWaypoint;
	private Button destroyButton;
	private Button cancelButton;
	private int destroyTimer;

	public DestroyCustomWaypointScreen(UpdateCustomWaypointScreen parent, CustomWaypoint wp) {
		super(new StringTextComponent("CWP"));
		parentScreen = parent;
		theWaypoint = wp;
		destroyTimer = 20;
	}

	@Override
	public void init() {
		super.init();
		destroyButton = this.addButton(new RedBookButton(width / 2 + 4, height / 2 + 65, 120, 20, new TranslationTextComponent("gui.lotr.cwp.destroy.do"), b -> {
			LOTRPacketHandler.sendToServer(new CPacketDestroyCustomWaypoint(theWaypoint));
			minecraft.player.closeContainer();
		}).setRedText());
		destroyButton.active = false;
		cancelButton = this.addButton(new RedBookButton(width / 2 - 124, height / 2 + 65, 120, 20, new TranslationTextComponent("gui.lotr.cwp.destroy.cancel"), b -> {
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
		ITextComponent title = new TranslationTextComponent("gui.lotr.cwp.destroy.title");
		drawCenteredString(matStack, font, title, width / 2, height / 2 - 90, 16777215);
		ITextComponent warning1 = new TranslationTextComponent("gui.lotr.cwp.destroy.warning.1", theWaypoint.getDisplayName().getString());
		ITextComponent warning2 = new TranslationTextComponent("gui.lotr.cwp.destroy.warning.2");
		ITextComponent warning3 = new TranslationTextComponent("gui.lotr.cwp.destroy.warning.3");
		int warningY = drawCenteredStringLinesWrappedToWidth(matStack, font, warning1, 300, width / 2, height / 2 - 55, 16777215);
		warningY += 16;
		drawCenteredString(matStack, font, warning2, width / 2, warningY, 16777215);
		warningY += 30;
		warningY = drawCenteredStringLinesWrappedToWidth(matStack, font, warning3, 300, width / 2, warningY, 16777215);
		int adoptedCount = theWaypoint.getAdoptedCountForDisplay();
		if (adoptedCount > 0) {
			ITextComponent warningAdopted = new TranslationTextComponent("gui.lotr.cwp.destroy.warning.adopted", adoptedCount).withStyle(TextFormatting.RED);
			warningY += 12;
			drawCenteredString(matStack, font, warningAdopted, width / 2, warningY, 16777215);
		}

		super.render(matStack, mouseX, mouseY, f);
	}

	@Override
	public void tick() {
		super.tick();
		--destroyTimer;
		if (destroyTimer <= 0) {
			destroyButton.active = true;
		}

	}
}
