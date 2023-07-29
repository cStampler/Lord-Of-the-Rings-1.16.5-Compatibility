package lotr.client.gui.inv;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class FactionCraftingToggleButton extends Button {
	private final int baseU;
	private final int baseV;
	private final ResourceLocation texture;
	private final ItemStack buttonIcon;
	private boolean toggledOn;

	public FactionCraftingToggleButton(int x, int y, int width, int height, int u, int v, ResourceLocation tex, ItemStack icon, IPressable pressFunc) {
		super(x, y, width, height, StringTextComponent.EMPTY, pressFunc);
		baseU = u;
		baseV = v;
		texture = tex;
		buttonIcon = icon;
	}

	@Override
	public void renderButton(MatrixStack matStack, int mouseX, int mouseY, float tick) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bind(texture);
		RenderSystem.disableDepthTest();
		int u = baseU;
		if (isHovered()) {
			u += width * 2;
		} else if (toggledOn) {
			u += width;
		}

		this.blit(matStack, x, y, u, baseV, width, height);
		RenderSystem.enableDepthTest();
		ItemRenderer ir = mc.getItemRenderer();
		ir.renderAndDecorateItem(buttonIcon, x + 1, y + 1);
	}

	public void setToggled(boolean flag) {
		toggledOn = flag;
	}
}
