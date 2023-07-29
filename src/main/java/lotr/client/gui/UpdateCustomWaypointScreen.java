package lotr.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.gui.widget.button.RedBookButton;
import lotr.common.network.*;
import lotr.common.world.map.CustomWaypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.*;

public class UpdateCustomWaypointScreen extends CustomWaypointScreen {
	private static final ITextComponent PUBLIC_TEXT = new TranslationTextComponent("gui.lotr.cwp.create.public.yes");
	private static final ITextComponent NOT_PUBLIC_TEXT = new TranslationTextComponent("gui.lotr.cwp.create.public.no");
	private final CustomWaypoint theWaypoint;
	private final String initialName;
	private final String initialLore;
	private final boolean initialIsPublic;
	private Button updateButton;
	private TextFieldWidget nameField;
	private TextFieldWidget loreField;
	private boolean isPublic;
	private Button publicButton;
	private Button destroyButton;

	public UpdateCustomWaypointScreen(CustomWaypoint wp) {
		super(new StringTextComponent("CWP"));
		theWaypoint = wp;
		initialName = theWaypoint.getRawName();
		initialLore = theWaypoint.getRawLore();
		initialIsPublic = isPublic = theWaypoint.isPublic();
	}

	private void checkChangesForUpdate() {
		boolean anyChanges = !nameField.getValue().trim().equals(initialName.trim()) || !loreField.getValue().trim().equals(initialLore.trim()) || isPublic != initialIsPublic;
		updateButton.active = anyChanges;
	}

	@Override
	public void init() {
		super.init();
		updateButton = this.addButton(new RedBookButton(width / 2 - 100, height / 2 + 65, 97, 20, new TranslationTextComponent("gui.lotr.cwp.update.do"), b -> {
			LOTRPacketHandler.sendToServer(new CPacketUpdateCustomWaypoint(theWaypoint, nameField.getValue(), loreField.getValue(), isPublic));
			minecraft.player.closeContainer();
		}));
		updateButton.active = false;
		nameField = new TextFieldWidget(font, width / 2 - 100, height / 2 - 40, 200, 20, (ITextComponent) null);
		nameField.setMaxLength(40);
		nameField.setValue(initialName);
		addTextFieldAndSetFocused(nameField);
		loreField = new TextFieldWidget(font, width / 2 - 100, height / 2, 200, 20, (ITextComponent) null);
		loreField.setMaxLength(160);
		loreField.setValue(initialLore);
		addTextField(loreField);
		nameField.setResponder(text -> {
			checkChangesForUpdate();
		});
		loreField.setResponder(text -> {
			checkChangesForUpdate();
		});
		checkChangesForUpdate();
		publicButton = this.addButton(new RedBookButton(width / 2 - 30, height / 2 + 30, 60, 20, NOT_PUBLIC_TEXT, b -> {
			isPublic = !isPublic;
			checkChangesForUpdate();
		}));
		if (initialIsPublic) {
			publicButton.active = false;
		}

		updatePublicButton();
		destroyButton = this.addButton(new RedBookButton(width / 2 + 4, height / 2 + 65, 97, 20, new TranslationTextComponent("gui.lotr.cwp.update.destroy"), b -> {
			minecraft.setScreen(new DestroyCustomWaypointScreen(this, theWaypoint));
		}));
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float f) {
		this.renderBackground(matStack);
		ITextComponent title = new TranslationTextComponent("gui.lotr.cwp.update.title");
		drawCenteredString(matStack, font, title, width / 2, height / 2 - 90, 16777215);
		nameField.render(matStack, mouseX, mouseY, f);
		ITextComponent nameFieldLabel = new TranslationTextComponent("gui.lotr.cwp.update.name");
		FontRenderer var10000 = font;
		float var10003 = nameField.x;
		int var10004 = nameField.y;
		font.getClass();
		var10000.draw(matStack, nameFieldLabel, var10003, var10004 - 9 - 3, 16777215);
		loreField.render(matStack, mouseX, mouseY, f);
		ITextComponent loreFieldLabel = new TranslationTextComponent("gui.lotr.cwp.update.lore");
		var10000 = font;
		var10003 = loreField.x;
		var10004 = loreField.y;
		font.getClass();
		var10000.draw(matStack, loreFieldLabel, var10003, var10004 - 9 - 3, 16777215);
		ITextComponent publicLabel = new TranslationTextComponent("gui.lotr.cwp.create.public");
		var10000 = font;
		var10003 = publicButton.x - 4 - font.width(publicLabel);
		var10004 = publicButton.y + publicButton.getHeight() / 2;
		font.getClass();
		var10000.draw(matStack, publicLabel, var10003, var10004 - 9 / 2, 16777215);
		super.render(matStack, mouseX, mouseY, f);
		renderPublicButtonTooltip(publicButton, matStack, mouseX, mouseY);
	}

	@Override
	public void resize(Minecraft mc, int w, int h) {
		String name = nameField.getValue();
		String lore = loreField.getValue();
		super.resize(mc, w, h);
		nameField.setValue(name);
		loreField.setValue(lore);
	}

	@Override
	public void tick() {
		super.tick();
		nameField.tick();
		loreField.tick();
		updatePublicButton();
	}

	private void updatePublicButton() {
		publicButton.setMessage(isPublic ? PUBLIC_TEXT : NOT_PUBLIC_TEXT);
		publicButton.setFGColor(isPublic ? 16711680 : 8019267);
	}
}
