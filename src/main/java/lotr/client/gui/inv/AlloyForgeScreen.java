package lotr.client.gui.inv;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.common.inv.AbstractAlloyForgeContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class AlloyForgeScreen extends ContainerScreen {
	private static final ResourceLocation FORGE_TEXTURE = new ResourceLocation("lotr:textures/gui/alloy_forge.png");

	public AlloyForgeScreen(AbstractAlloyForgeContainer cont, PlayerInventory inv, ITextComponent title) {
		super(cont, inv, title);
		imageHeight = 233;
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void render(MatrixStack matStack, int x, int y, float f) {
		this.renderBackground(matStack);
		super.render(matStack, x, y, f);
		this.renderTooltip(matStack, x, y);
	}

	@Override
	protected void renderBg(MatrixStack matStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(FORGE_TEXTURE);
		int left = leftPos;
		int top = topPos;
		this.blit(matStack, left, top, 0, 0, imageWidth, imageHeight);
		int cook;
		if (((AbstractAlloyForgeContainer) menu).isBurning()) {
			cook = ((AbstractAlloyForgeContainer) menu).getBurnLeftScaled();
			this.blit(matStack, left + 80, top + 112 + 12 - cook, 176, 12 - cook, 14, cook + 2);
		}

		cook = ((AbstractAlloyForgeContainer) menu).getCookProgressionScaled();
		this.blit(matStack, left + 80, top + 58, 176, 14, 16, cook + 1);
	}

	@Override
	protected void renderLabels(MatrixStack matStack, int x, int y) {
		font.draw(matStack, title, imageWidth / 2 - font.width(title) / 2, 6.0F, 4210752);
		font.draw(matStack, inventory.getDisplayName(), 8.0F, 139.0F, 4210752);
	}

	@Override
	public void tick() {
		super.tick();
	}
}
