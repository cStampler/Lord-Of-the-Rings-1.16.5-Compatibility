package lotr.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lotr.client.LOTRClientProxy;
import lotr.client.render.entity.RingPortalRenderer;
import lotr.client.render.entity.model.*;
import lotr.client.util.LOTRClientUtil;
import lotr.common.config.LOTRConfig;
import lotr.common.init.LOTRBiomes;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class OnScreenCompassRenderer extends AbstractGui {
	private static final ResourceLocation COMPASS_TEXTURE = new ResourceLocation("lotr", "textures/gui/on_screen_compass.png");
	private OnScreenCompassModel compassModel = new OnScreenCompassModel();
	private Model ringModel = new RingPortalModel(false);
	private Model scriptModel = new RingPortalModel(true);

	public void renderCompassAndInformation(Minecraft mc, PlayerEntity player, World world, float renderTick) {
		MatrixStack matStack = new MatrixStack();
		MainWindow window = mc.getWindow();
		int width = window.getGuiScaledWidth();
		window.getGuiScaledHeight();
		int compassX = width - 60;
		int compassY = 44;
		matStack.pushPose();
		matStack.translate(compassX, compassY, 0.0D);
		float rotation = player.yRotO + (player.yRot - player.yRotO) * renderTick;
		rotation = 180.0F - rotation;
		renderCompassModel(mc, matStack, 16.0F, rotation);
		matStack.popPose();
		if ((Boolean) LOTRConfig.CLIENT.compassInfo.get()) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			matStack.pushPose();
			float scale = 0.5F;
			float invScale = 1.0F / scale;
			compassX = (int) (compassX * invScale);
			compassY = (int) (compassY * invScale);
			matStack.scale(scale, scale, scale);
			BlockPos playerPos = player.blockPosition();
			ITextComponent coords = new TranslationTextComponent("gui.lotr.compass.coords", playerPos.getX(), playerPos.getY(), playerPos.getZ());
			FontRenderer fontRenderer = mc.font;
			int coordsWidth = fontRenderer.width(coords);
			int coordsY = compassY + 68;
			int rectBorder = 2;
			int rectColor = 1056964608;
			int var10001 = compassX - coordsWidth / 2 - rectBorder;
			int var10002 = coordsY - rectBorder;
			int var10003 = compassX + coordsWidth / 2 + rectBorder;
			fontRenderer.getClass();
			fill(matStack, var10001, var10002, var10003, coordsY + 9 + rectBorder, rectColor);
			fontRenderer.draw(matStack, coords, compassX - coordsWidth / 2, coordsY, 16777215);
			if (LOTRClientUtil.doesClientChunkExist(world, playerPos)) {
				Biome biome = world.getBiome(playerPos);
				ITextComponent biomeName = LOTRBiomes.getBiomeDisplayName(biome, world);
				int biomeNameWidth = fontRenderer.width(biomeName);
				int biomeNameY = compassY - 74;
				var10001 = compassX - biomeNameWidth / 2 - rectBorder;
				var10002 = biomeNameY - rectBorder;
				var10003 = compassX + biomeNameWidth / 2 + rectBorder;
				fontRenderer.getClass();
				fill(matStack, var10001, var10002, var10003, biomeNameY + 9 + rectBorder, rectColor);
				fontRenderer.draw(matStack, biomeName, compassX - biomeNameWidth / 2, biomeNameY, 16777215);
			}

			matStack.popPose();
			RenderSystem.disableBlend();
		}

	}

	private void renderCompassModel(Minecraft mc, MatrixStack matStack, float scale, float rotation) {
		RenderSystem.pushLightingAttributes();
		RenderHelper.setupForFlatItems();
		matStack.scale(1.0F, 1.0F, -1.0F);
		matStack.mulPose(Vector3f.XP.rotationDegrees(40.0F));
		matStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
		matStack.scale(scale, scale, scale);
		IRenderTypeBuffer typeBuffer = mc.renderBuffers().bufferSource();
		int packedLight = LOTRClientProxy.MAX_LIGHTMAP;
		int packedOverlay = LOTRClientUtil.getPackedNoOverlay();
		float r = 1.0F;
		float g = 1.0F;
		float b = 1.0F;
		float a = 1.0F;
		IVertexBuilder buf = typeBuffer.getBuffer(RenderType.entityCutoutNoCull(COMPASS_TEXTURE));
		matStack.pushPose();
		float compassScale = 2.0F;
		matStack.scale(compassScale, compassScale, compassScale);
		compassModel.renderToBuffer(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		matStack.popPose();
		buf = typeBuffer.getBuffer(RenderType.entityCutoutNoCull(RingPortalRenderer.RING_TEXTURE));
		ringModel.renderToBuffer(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		buf = typeBuffer.getBuffer(RenderType.entityTranslucent(RingPortalRenderer.SCRIPT_TEXTURE));
		matStack.pushPose();
		float outerScriptScale = 1.05F;
		matStack.scale(outerScriptScale, outerScriptScale, outerScriptScale);
		scriptModel.renderToBuffer(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		matStack.popPose();
		matStack.pushPose();
		float innerScriptScale = 0.85F;
		matStack.scale(innerScriptScale, innerScriptScale, innerScriptScale);
		scriptModel.renderToBuffer(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		matStack.popPose();
		RenderSystem.popAttributes();
	}
}
