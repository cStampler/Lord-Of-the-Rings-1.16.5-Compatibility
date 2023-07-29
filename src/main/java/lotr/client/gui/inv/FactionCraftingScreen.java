package lotr.client.gui.inv;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.common.inv.FactionCraftingContainer;
import lotr.common.network.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

public class FactionCraftingScreen extends ContainerScreen {
	private static final ResourceLocation CRAFTING_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");
	private static final ResourceLocation FACTION_CRAFTING_GUI_TEXTURES = new ResourceLocation("lotr", "textures/gui/faction_crafting_table.png");
	private static final ITextComponent STANDARD_CRAFTING_TITLE = new TranslationTextComponent("container.crafting");
	private boolean widthTooNarrow;
	private FactionCraftingToggleButton buttonToggleFaction;
	private FactionCraftingToggleButton buttonToggleStandard;

	public FactionCraftingScreen(FactionCraftingContainer c, PlayerInventory inv, ITextComponent title) {
		super(c, inv, title);
	}

	@Override
	protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
		return mouseX < guiLeftIn || mouseY < guiTopIn || mouseX >= guiLeftIn + imageWidth || mouseY >= guiTopIn + imageHeight;
	}

	@Override
	protected void init() {
		super.init();
		widthTooNarrow = width < 379;
		buttonToggleFaction = this.addButton(new FactionCraftingToggleButton(leftPos + 5, topPos + 25, 18, 18, 0, 0, FACTION_CRAFTING_GUI_TEXTURES, new ItemStack(((FactionCraftingContainer) menu).getCraftingBlock()), b -> {
			toggleCrafting(false);
		}));
		buttonToggleStandard = this.addButton(new FactionCraftingToggleButton(leftPos + 5, topPos + 43, 18, 18, 0, 18, FACTION_CRAFTING_GUI_TEXTURES, new ItemStack(Blocks.CRAFTING_TABLE), b -> {
			toggleCrafting(true);
		}));
		updateToggles();
	}

	@Override
	protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
		return !widthTooNarrow && super.isHovering(x, y, width, height, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double x, double y, int click) {
		return super.mouseClicked(x, y, click);
	}

	@Override
	public void render(MatrixStack matStack, int i, int j, float f) {
		this.renderBackground(matStack);
		super.render(matStack, i, j, f);
		this.renderTooltip(matStack, i, j);
	}

	@Override
	protected void renderBg(MatrixStack matStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(CRAFTING_GUI_TEXTURES);
		int i = leftPos;
		int j = (height - imageHeight) / 2;
		this.blit(matStack, i, j, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(MatrixStack matStack, int mouseX, int mouseY) {
		if (((FactionCraftingContainer) menu).isStandardCraftingActive()) {
			font.draw(matStack, STANDARD_CRAFTING_TITLE, 28.0F, 6.0F, 4210752);
		} else {
			font.draw(matStack, title, 28.0F, 6.0F, 4210752);
		}

		font.draw(matStack, inventory.getDisplayName(), 8.0F, imageHeight - 96 + 2, 4210752);
	}

	@Override
	protected void slotClicked(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		super.slotClicked(slotIn, slotId, mouseButton, type);
	}

	@Override
	public void tick() {
		super.tick();
	}

	private void toggleCrafting(boolean standard) {
		((FactionCraftingContainer) menu).setStandardCraftingActive(standard);
		updateToggles();
		CPacketFactionCraftingToggle packet = new CPacketFactionCraftingToggle(standard);
		LOTRPacketHandler.sendToServer(packet);
	}

	private void updateToggles() {
		boolean standard = ((FactionCraftingContainer) menu).isStandardCraftingActive();
		buttonToggleFaction.setToggled(!standard);
		buttonToggleStandard.setToggled(standard);
	}
}
