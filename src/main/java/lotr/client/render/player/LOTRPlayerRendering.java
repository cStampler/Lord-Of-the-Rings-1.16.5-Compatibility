package lotr.client.render.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.align.AlignmentFormatter;
import lotr.client.gui.util.AlignmentRenderer;
import lotr.client.gui.util.AlignmentTextRenderer;
import lotr.client.util.LOTRClientUtil;
import lotr.common.config.LOTRConfig;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import lotr.common.fac.Faction;
import lotr.common.init.LOTRDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LOTRPlayerRendering {
	private final Minecraft mc;
	private final AlignmentTextRenderer alignmentTextRenderer = AlignmentTextRenderer.newInWorldRenderer().setDefaultSeethrough(true);

	public LOTRPlayerRendering(Minecraft mc) {
		this.mc = mc;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void postRender(Post event) {
		PlayerEntity player = event.getPlayer();
		event.getPartialRenderTick();
		MatrixStack matStack = event.getMatrixStack();
		EntityRendererManager renderMgr = mc.getEntityRenderDispatcher();
		ActiveRenderInfo renderInfo = renderMgr.camera;
		renderInfo.getPosition();
		float yOffset = player.isSleeping() ? -1.5F : 0.0F;
		if (shouldRenderAlignment(player) && (LOTRDimensions.isModDimension(mc.level) || (Boolean) LOTRConfig.CLIENT.showAlignmentEverywhere.get())) {
			LOTRPlayerData clientPD = LOTRLevelData.clientInstance().getData(mc.player);
			LOTRPlayerData otherPD = LOTRLevelData.clientInstance().getData(player);
			Faction currentViewedFaction = clientPD.getAlignmentData().getCurrentViewedFaction();
			float alignment = otherPD.getAlignmentData().getAlignment(currentViewedFaction);
			if (ForgeHooksClient.isNameplateInRenderDistance(player, renderMgr.distanceToSqr(player))) {
				FontRenderer fr = mc.font;
				matStack.pushPose();
				matStack.translate(0.0D, player.getBbHeight() + 0.6F + yOffset, 0.0D);
				matStack.mulPose(renderMgr.cameraOrientation());
				float scale = 0.025F;
				matStack.scale(-scale, -scale, scale);
				RenderSystem.disableLighting();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				float alpha = 1.0F;
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
				ITextComponent strAlign = new StringTextComponent(AlignmentFormatter.formatAlignForDisplay(alignment));
				mc.getTextureManager().bind(AlignmentRenderer.ALIGNMENT_TEXTURE);
				int ringWidth = 18;
				int x = -MathHelper.floor((fr.width(strAlign) + ringWidth) / 2.0D);
				int y = -12;
				LOTRClientUtil.blit(matStack, x, y - 5, 0, 36, 16, 16);
				alignmentTextRenderer.drawAlignmentText(matStack, fr, x + ringWidth, y, strAlign, alpha);
				RenderSystem.disableBlend();
				matStack.popPose();
			}
		}

	}

	private boolean shouldRenderAlignment(PlayerEntity player) {
		if ((Boolean) LOTRConfig.CLIENT.displayAlignmentAboveHead.get() && shouldRenderPlayerHUD(player)) {
			return LOTRLevelData.clientInstance().getData(player).getAlignmentData().displayAlignmentAboveHead();
		}
		return false;
	}

	private boolean shouldRenderPlayerHUD(PlayerEntity player) {
		if (!Minecraft.renderNames()) {
			return false;
		}
		return player != mc.cameraEntity && !player.isDiscrete() && !player.isInvisibleTo(mc.player);
	}
}
