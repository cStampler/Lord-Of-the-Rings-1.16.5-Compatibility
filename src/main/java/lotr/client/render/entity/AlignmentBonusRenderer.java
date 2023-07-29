package lotr.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.align.AlignmentFormatter;
import lotr.client.gui.util.*;
import lotr.client.util.LOTRClientUtil;
import lotr.common.LOTRMod;
import lotr.common.data.*;
import lotr.common.entity.misc.AlignmentBonusEntity;
import lotr.common.fac.Faction;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;

public class AlignmentBonusRenderer extends EntityRenderer {
	private static final ResourceLocation ALIGNMENT_TEXTURE;
	static {
		ALIGNMENT_TEXTURE = AlignmentRenderer.ALIGNMENT_TEXTURE;
	}

	private final AlignmentTextRenderer alignmentTextRenderer = AlignmentTextRenderer.newInWorldRenderer().setDefaultSeethrough(true);

	public AlignmentBonusRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity entity) {
		return ALIGNMENT_TEXTURE;
	}

	@Override
	public void render(Entity bonusEntity, float yaw, float ticks, MatrixStack matStack, IRenderTypeBuffer buf, int packedLight) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		AlignmentDataModule alignData = LOTRLevelData.getSidedData(player).getAlignmentData();
		Faction viewingFaction = alignData.getCurrentViewedFaction();
		Faction renderFaction = null;
		boolean showConquest = false;
		if (((AlignmentBonusEntity) bonusEntity).shouldDisplayConquestBonus(alignData)) {
			renderFaction = viewingFaction;
			showConquest = true;
		} else {
			renderFaction = ((AlignmentBonusEntity) bonusEntity).getFactionToDisplay(alignData);
		}

		if (renderFaction != null) {
			float alignBonus = ((AlignmentBonusEntity) bonusEntity).getAlignmentBonusFor(renderFaction);
			boolean showAlign = alignBonus != 0.0F;
			float conqBonus = ((AlignmentBonusEntity) bonusEntity).getConquestBonus();
			if (showAlign || showConquest) {
				ITextComponent title = ((AlignmentBonusEntity) bonusEntity).getBonusDisplayText();
				boolean isViewingFaction = renderFaction == viewingFaction;
				boolean showTitle = ((AlignmentBonusEntity) bonusEntity).shouldShowBonusText(showAlign, showConquest);
				float bonusAge = ((AlignmentBonusEntity) bonusEntity).getBonusAgeF(ticks);
				float alpha = bonusAge < 0.75F ? 1.0F : (1.0F - bonusAge) / 0.25F;
				matStack.pushPose();
				matStack.mulPose(entityRenderDispatcher.cameraOrientation());
				matStack.scale(-0.025F, -0.025F, 0.025F);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				renderBonusText(matStack, isViewingFaction, renderFaction, title, showTitle, alignBonus, showAlign, conqBonus, showConquest, alpha);
				RenderSystem.disableBlend();
				matStack.popPose();
			}
		}

	}

	private void renderBonusText(MatrixStack matStack, boolean isViewingFaction, Faction renderFaction, ITextComponent title, boolean showTitle, float align, boolean showAlign, float conq, boolean showConq, float alpha) {
		FontRenderer fr = entityRenderDispatcher.getFont();
		ITextComponent strAlign = new StringTextComponent(AlignmentFormatter.formatAlignForDisplay(align));
		ITextComponent strConq = new StringTextComponent(AlignmentFormatter.formatConqForDisplay(conq, true));
		boolean negativeConq = conq < 0.0F;
		matStack.pushPose();
		if (!isViewingFaction) {
			float scale = 0.5F;
			matStack.scale(scale, scale, 1.0F);
			strAlign = new TranslationTextComponent("%s (%s...)", strAlign, renderFaction.getDisplayName());
		}

		int ringWidth = 18;
		int x = -MathHelper.floor((fr.width(strAlign) + ringWidth) / 2.0D);
		int y = -16;
		if (showAlign) {
			entityRenderDispatcher.textureManager.bind(ALIGNMENT_TEXTURE);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
			LOTRClientUtil.blit(matStack, x, y - 5, 0, 36, 16, 16);
			alignmentTextRenderer.drawAlignmentText(matStack, fr, x + ringWidth, y, strAlign, alpha);
			y += 14;
		}

		if (showTitle) {
			x = -MathHelper.floor(fr.width(title) / 2.0D);
			if (showAlign) {
				alignmentTextRenderer.drawAlignmentText(matStack, fr, x, y, title, alpha);
			} else {
				alignmentTextRenderer.drawConquestText(matStack, fr, x, y, title, negativeConq, alpha);
			}

			y += 16;
		}

		if (showConq) {
			x = -MathHelper.floor((fr.width(strConq) + ringWidth) / 2.0D);
			entityRenderDispatcher.textureManager.bind(ALIGNMENT_TEXTURE);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
			LOTRClientUtil.blit(matStack, x, y - 5, negativeConq ? 16 : 0, 228, 16, 16);
			alignmentTextRenderer.drawConquestText(matStack, fr, x + ringWidth, y, strConq, negativeConq, alpha);
		}

		matStack.popPose();
	}
}
