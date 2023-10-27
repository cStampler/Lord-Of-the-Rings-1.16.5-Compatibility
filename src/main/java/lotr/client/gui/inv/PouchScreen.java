package lotr.client.gui.inv;

import org.apache.commons.lang3.StringUtils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.common.inv.PouchContainer;
import lotr.common.network.CPacketRenamePouch;
import lotr.common.network.LOTRPacketHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PouchScreen extends ContainerScreen<PouchContainer> {
	private static final ResourceLocation POUCH_TEXTURE = new ResourceLocation("lotr:textures/gui/pouch.png");
	private final int pouchRows;
	private TextFieldWidget renameField;

	public PouchScreen(PouchContainer cont, PlayerInventory inv, ITextComponent title) {
		super(cont, inv, title);
		pouchRows = cont.getPouchCapacity() / 9;
		imageHeight = 180;
	}

	@Override
	public void init() {
		super.init();
		renameField = new TextFieldWidget(font, leftPos + imageWidth / 2 - 80, topPos + 7, 160, 20, (ITextComponent) null) {
			@Override
			public void setFocus(boolean isFocused) {
				super.setFocus(isFocused);
				if (!isFocused && StringUtils.trim(renameField.getValue()).isEmpty()) {
					setValue(((PouchContainer) PouchScreen.this.menu).getPouchDefaultDisplayName().getString());
				}

			}
		};
		renameField.setMaxLength(64);
		renameField.setValue(((PouchContainer) menu).getPouchDisplayName().getString());
		renameField.setResponder(this::renamePouch);
		children.add(renameField);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return !minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode)) || !renameField.canConsumeInput() && !renameField.keyPressed(keyCode, scanCode, modifiers) ? super.keyPressed(keyCode, scanCode, modifiers) : true;
	}

	private void renamePouch(String text) {
		if (text.equals(((PouchContainer) menu).getPouchDefaultDisplayName().getString())) {
			text = "";
		}

		((PouchContainer) menu).renamePouch(text);
		LOTRPacketHandler.sendToServer(new CPacketRenamePouch(text));
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matStack);
		super.render(matStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack matStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(POUCH_TEXTURE);
		this.blit(matStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		for (int l = 0; l < pouchRows; ++l) {
			this.blit(matStack, leftPos + 7, topPos + 29 + l * 18, 0, 180, 162, 18);
		}

		renameField.render(matStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderLabels(MatrixStack matStack, int mouseX, int mouseY) {
		font.draw(matStack, inventory.getDisplayName(), 8.0F, imageHeight - 96 + 2, 4210752);
	}

	@Override
	public void tick() {
		super.tick();
		renameField.tick();
	}
}
