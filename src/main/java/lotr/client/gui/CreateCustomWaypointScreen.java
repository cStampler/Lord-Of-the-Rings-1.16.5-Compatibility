package lotr.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.gui.widget.button.RedBookButton;
import lotr.common.network.CPacketCreateCustomWaypoint;
import lotr.common.network.LOTRPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CreateCustomWaypointScreen extends CustomWaypointScreen {
	private static final ITextComponent PUBLIC_TEXT = new TranslationTextComponent("gui.lotr.cwp.create.public.yes");
	private static final ITextComponent NOT_PUBLIC_TEXT = new TranslationTextComponent("gui.lotr.cwp.create.public.no");
	private Button createButton;
	private TextFieldWidget nameField;
	private TextFieldWidget loreField;
	private Button publicButton;
	private boolean isPublic = false;

	public CreateCustomWaypointScreen() {
		super(new StringTextComponent("CWP"));
	}

	@Override
	public void init() {
		super.init();
		createButton = this.addButton(new RedBookButton(width / 2 - 60, height / 2 + 65, 120, 20, new TranslationTextComponent("gui.lotr.cwp.create.do"), b -> {
			LOTRPacketHandler.sendToServer(new CPacketCreateCustomWaypoint(nameField.getValue(), loreField.getValue(), isPublic));
			minecraft.player.closeContainer();
		}));
		createButton.active = false;
		nameField = new TextFieldWidget(font, width / 2 - 100, height / 2 - 40, 200, 20, (ITextComponent) null);
		nameField.setMaxLength(40);
		nameField.setResponder(text -> {
			createButton.active = !text.trim().isEmpty();
		});
		addTextFieldAndSetFocused(nameField);
		loreField = new TextFieldWidget(font, width / 2 - 100, height / 2, 200, 20, (ITextComponent) null);
		loreField.setMaxLength(160);
		addTextField(loreField);
		publicButton = this.addButton(new RedBookButton(width / 2 - 30, height / 2 + 30, 60, 20, NOT_PUBLIC_TEXT, b -> {
			isPublic = !isPublic;
		}));
		updatePublicButton();
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float f) {
		this.renderBackground(matStack);
		ITextComponent title = new TranslationTextComponent("gui.lotr.cwp.create.title");
		drawCenteredString(matStack, font, title, width / 2, height / 2 - 90, 16777215);
		nameField.render(matStack, mouseX, mouseY, f);
		ITextComponent nameFieldLabel = new TranslationTextComponent("gui.lotr.cwp.create.name");
		FontRenderer var10000 = font;
		float var10003 = nameField.x;
		int var10004 = nameField.y;
		font.getClass();
		var10000.draw(matStack, nameFieldLabel, var10003, var10004 - 9 - 3, 16777215);
		loreField.render(matStack, mouseX, mouseY, f);
		ITextComponent loreFieldLabel = new TranslationTextComponent("gui.lotr.cwp.create.lore");
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
