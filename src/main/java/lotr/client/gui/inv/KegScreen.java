package lotr.client.gui.inv;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.widget.button.KegBrewButton;
import lotr.client.render.model.vessel.VesselDrinkModel;
import lotr.client.util.LOTRClientUtil;
import lotr.common.inv.KegContainer;
import lotr.common.network.CPacketKegBrewButton;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.tileentity.KegTileEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class KegScreen extends ContainerScreen {
	public static final ResourceLocation KEG_SCREEN = new ResourceLocation("lotr", "textures/gui/keg/keg.png");
	private static final ResourceLocation KEG_BREWING = new ResourceLocation("lotr", "textures/gui/keg/brewing.png");
	private KegBrewButton brewButton;
	private int brewAnim;
	private int brewAnimPrev;

	public KegScreen(KegContainer cont, PlayerInventory inv, ITextComponent title) {
		super(cont, inv, title);
		imageWidth = 210;
		imageHeight = 221;
	}

	@Override
	public void init() {
		super.init();
		brewButton = this.addButton(new KegBrewButton(leftPos + 87, topPos + 92, b -> {
			LOTRPacketHandler.sendToServer(new CPacketKegBrewButton());
		}, this::renderBrewButtonTooltip));
	}

	@Override
	public void render(MatrixStack matStack, int x, int y, float f) {
		this.renderBackground(matStack);
		if (((KegContainer) menu).getKegMode() == KegTileEntity.KegMode.EMPTY) {
			brewButton.active = ((KegContainer) menu).hasBrewingResult();
		} else if (((KegContainer) menu).getKegMode() == KegTileEntity.KegMode.BREWING) {
			brewButton.active = ((KegContainer) menu).canFinishBrewingNow();
		} else if (((KegContainer) menu).getKegMode() == KegTileEntity.KegMode.FULL) {
			brewButton.active = false;
		}

		super.render(matStack, x, y, f);
		this.renderTooltip(matStack, x, y);
	}

	@Override
	protected void renderBg(MatrixStack matStack, float partialTicks, int mouseX, int mouseY) {
		partialTicks = minecraft.getFrameTime();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(KEG_SCREEN);
		int left = leftPos;
		int top = topPos;
		this.blit(matStack, left, top, 0, 0, imageWidth, imageHeight);
		KegTileEntity.KegMode mode = ((KegContainer) menu).getKegMode();
		int fullAmount = ((KegContainer) menu).getBarrelFullAmountScaled(96);
		float fullAmount16 = ((KegContainer) menu).getBarrelFullAmountScaled(16000) / 1000.0F;
		if (mode == KegTileEntity.KegMode.BREWING) {
			fullAmount = ((KegContainer) menu).getBrewProgressScaled(96);
			fullAmount16 = ((KegContainer) menu).getBrewProgressScaled(16000) / 1000.0F;
		}

		float brewAnimF = brewAnimPrev + (brewAnim - brewAnimPrev) * partialTicks;
		brewAnimF /= 32.0F;
		float brewAnimScaled = brewAnimF * 97.0F;
		if (mode == KegTileEntity.KegMode.BREWING || mode == KegTileEntity.KegMode.FULL) {
			int x0 = leftPos + 148;
			int x1 = leftPos + 196;
			int y0 = topPos + 34;
			int y1 = topPos + 130;
			int yFull = y1 - fullAmount;
			float yAnim = y1 - brewAnimScaled;
			ItemStack result = ((KegContainer) menu).getBrewingResult();
			if (!result.isEmpty()) {
				TextureAtlasSprite icon = VesselDrinkModel.getLiquidIconFor(result);
				minecraft.getTextureManager().bind(icon.atlas().location());
				float minU = icon.getU(8.0D);
				float maxU = icon.getU(16.0D);
				float minV = icon.getV(16.0F - fullAmount16);
				float maxV = icon.getV(16.0D);
				Tessellator tess = Tessellator.getInstance();
				BufferBuilder buf = tess.getBuilder();
				buf.begin(7, DefaultVertexFormats.POSITION_TEX);
				int z = getBlitOffset();
				buf.vertex(x0, y1, z).uv(minU, maxV).endVertex();
				buf.vertex(x1, y1, z).uv(maxU, maxV).endVertex();
				buf.vertex(x1, yFull, z).uv(maxU, minV).endVertex();
				buf.vertex(x0, yFull, z).uv(minU, minV).endVertex();
				tess.end();
				int fullColor = 2167561;
				this.fillGradient(matStack, x0, yFull, x1, y1, 0, -16777216 | fullColor);
			}

			if (mode == KegTileEntity.KegMode.BREWING) {
				minecraft.getTextureManager().bind(KEG_BREWING);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.disableAlphaTest();
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, brewAnimF * 0.75F);
				LOTRClientUtil.blitFloat(this, matStack, x0, yAnim, 51.0F, 0.0F, x1 - x0, y1 - yAnim);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.enableAlphaTest();
				RenderSystem.disableBlend();
			}

			minecraft.getTextureManager().bind(KEG_BREWING);
			this.blit(matStack, x0, y0, 1, 0, x1 - x0, y1 - y0);
		}

	}

	private void renderBrewButtonTooltip(Button b, MatrixStack matStack, int mouseX, int mouseY) {
		List tooltipLines = new ArrayList();
		if (((KegContainer) menu).getKegMode() == KegTileEntity.KegMode.EMPTY) {
			tooltipLines.add(new TranslationTextComponent("container.lotr.keg.start_brewing"));
		} else if (((KegContainer) menu).getKegMode() == KegTileEntity.KegMode.BREWING) {
			tooltipLines.add(new TranslationTextComponent("container.lotr.keg.finish_brewing"));
			if (((KegContainer) menu).canFinishBrewingNow()) {
				tooltipLines.add(new TranslationTextComponent("container.lotr.keg.finish_brewing.tooltip.allowed", ((KegContainer) menu).getInterruptBrewingPotency().getDisplayName()).withStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
			} else {
				tooltipLines.add(new TranslationTextComponent("container.lotr.keg.finish_brewing.tooltip.not_allowed", ((KegContainer) menu).getMinimumPotency().getDisplayName()).withStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
			}
		} else if (((KegContainer) menu).getKegMode() == KegTileEntity.KegMode.FULL) {
			tooltipLines.add(new TranslationTextComponent("container.lotr.keg.start_brewing"));
		}

		if (!tooltipLines.isEmpty()) {
			renderComponentTooltip(matStack, tooltipLines, mouseX, mouseY);
		}

	}

	@Override
	protected void renderLabels(MatrixStack matStack, int x, int y) {
		ITextComponent kegTitle = ((KegContainer) menu).getKegTitle();
		ITextComponent kegSubtitle = ((KegContainer) menu).getKegSubtitle();
		font.draw(matStack, kegTitle, 106 - font.width(kegTitle) / 2, 6.0F, 4210752);
		font.draw(matStack, kegSubtitle, 106 - font.width(kegSubtitle) / 2, 17.0F, 4210752);
		font.draw(matStack, inventory.getDisplayName(), 25.0F, 127.0F, 4210752);
	}

	@Override
	public void tick() {
		super.tick();
		brewAnimPrev = brewAnim;
		if (((KegContainer) menu).getKegMode() == KegTileEntity.KegMode.BREWING) {
			++brewAnim;
			if (brewAnim >= 32) {
				brewAnim = 0;
				brewAnimPrev = brewAnim;
			}
		} else {
			brewAnim = 0;
			brewAnimPrev = brewAnim;
		}

	}
}
