package lotr.client.render.speech;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.speech.ImmersiveSpeech;
import lotr.client.util.LOTRClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ImmersiveSpeechRenderer {
	public static void renderAllSpeeches(Minecraft mc, World world, MatrixStack matStack, float tick) {
		matStack.pushPose();
		RenderHelper.turnBackOn();
		RenderSystem.alphaFunc(516, 0.01F);
		Vector3d renderPos = mc.gameRenderer.getMainCamera().getPosition();
		double x = renderPos.x();
		double y = renderPos.y();
		double z = renderPos.z();
		Set removes = new HashSet();
		ImmersiveSpeech.forEach((entityId, timedSpeech) -> {
			Entity entity = world.getEntity(entityId);
			if (entity != null && entity.isAlive()) {
				boolean inRange = entity.shouldRender(x, y, z);
				if (inRange) {
					double entityX = entity.xo + (entity.getX() - entity.xo) * tick;
					double entityY = entity.yo + (entity.getY() - entity.yo) * tick;
					double entityZ = entity.zo + (entity.getZ() - entity.zo) * tick;
					renderSpeech(mc, matStack, entity, timedSpeech.getSpeech(), timedSpeech.getAge(), entityX - x, entityY - y, entityZ - z);
				}
			} else {
				removes.add(entityId);
			}

		});
		removes.forEach(hummel -> ImmersiveSpeech.removeSpeech((int) hummel));
		RenderSystem.defaultAlphaFunc();
		RenderHelper.turnOff();
		mc.gameRenderer.lightTexture().turnOffLightLayer();
		matStack.popPose();
	}

	private static void renderSpeech(Minecraft mc, MatrixStack matStack, Entity entity, String speech, float speechAge, double x, double y, double z) {
		World world = entity.getCommandSenderWorld();
		world.getProfiler().push("renderNPCSpeech");
		mc.getTextureManager();
		EntityRendererManager renderManager = mc.getEntityRenderDispatcher();
		FontRenderer fr = mc.font;
		double distance = 64.0D;
		double distanceSq = entity.distanceToSqr(renderManager.camera.getEntity());
		if (distanceSq <= distance * distance) {
			ITextComponent name = TextComponentUtils.mergeStyles(entity.getName().copy(), Style.EMPTY.withColor(TextFormatting.YELLOW));
			fr.getClass();
			int fontHeight = 9;
			List speechLines = fr.split(new StringTextComponent(speech), 150);
			float alpha = 0.8F;
			if (speechAge < 0.1F) {
				alpha *= speechAge / 0.1F;
			}

			matStack.pushPose();
			matStack.translate(x, y + entity.getBbHeight() + 0.30000001192092896D, z);
			matStack.mulPose(renderManager.camera.rotation());
			RenderSystem.disableLighting();
			RenderSystem.depthMask(false);
			RenderSystem.disableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			float scale = 0.015F;
			matStack.scale(-scale, -scale, scale);
			matStack.translate(0.0D, -fontHeight * (3 + speechLines.size()), 0.0D);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buf = Tessellator.getInstance().getBuilder();
			float blackBoxAlpha = 0.35F * alpha;
			int halfNameW = fr.width(name) / 2;
			RenderSystem.disableTexture();
			RenderSystem.disableAlphaTest();
			Matrix4f matrix = matStack.last().pose();
			buf.begin(7, DefaultVertexFormats.POSITION_COLOR);
			buf.vertex(matrix, -halfNameW - 1, 0.0F, 0.0F).color(0.0F, 0.0F, 0.0F, blackBoxAlpha).endVertex();
			buf.vertex(matrix, -halfNameW - 1, fontHeight, 0.0F).color(0.0F, 0.0F, 0.0F, blackBoxAlpha).endVertex();
			buf.vertex(matrix, halfNameW + 1, fontHeight, 0.0F).color(0.0F, 0.0F, 0.0F, blackBoxAlpha).endVertex();
			buf.vertex(matrix, halfNameW + 1, 0.0F, 0.0F).color(0.0F, 0.0F, 0.0F, blackBoxAlpha).endVertex();
			tessellator.end();
			RenderSystem.enableTexture();
			LOTRClientUtil.drawSeethroughText(fr, name, -halfNameW, 0, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha), matStack);
			matStack.translate(0.0D, fontHeight, 0.0D);
			Iterator var30 = speechLines.iterator();

			while (var30.hasNext()) {
				IReorderingProcessor line = (IReorderingProcessor) var30.next();
				matStack.translate(0.0D, fontHeight, 0.0D);
				int halfLineW = fr.width(line) / 2;
				RenderSystem.disableTexture();
				RenderSystem.disableAlphaTest();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				matrix = matStack.last().pose();
				buf.begin(7, DefaultVertexFormats.POSITION_COLOR);
				buf.vertex(matrix, -halfLineW - 1, 0.0F, 0.0F).color(0.0F, 0.0F, 0.0F, blackBoxAlpha).endVertex();
				buf.vertex(matrix, -halfLineW - 1, fontHeight, 0.0F).color(0.0F, 0.0F, 0.0F, blackBoxAlpha).endVertex();
				buf.vertex(matrix, halfLineW + 1, fontHeight, 0.0F).color(0.0F, 0.0F, 0.0F, blackBoxAlpha).endVertex();
				buf.vertex(matrix, halfLineW + 1, 0.0F, 0.0F).color(0.0F, 0.0F, 0.0F, blackBoxAlpha).endVertex();
				tessellator.end();
				RenderSystem.enableTexture();
				LOTRClientUtil.drawSeethroughText(fr, line, -halfLineW, 0, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha), matStack);
			}

			RenderSystem.disableBlend();
			RenderSystem.enableDepthTest();
			RenderSystem.depthMask(true);
			RenderSystem.enableLighting();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			matStack.popPose();
		}

		world.getProfiler().pop();
	}
}
