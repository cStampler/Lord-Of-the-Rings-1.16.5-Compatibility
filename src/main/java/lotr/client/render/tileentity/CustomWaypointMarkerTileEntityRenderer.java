package lotr.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.util.LOTRClientUtil;
import lotr.common.tileentity.CustomWaypointMarkerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.*;

public class CustomWaypointMarkerTileEntityRenderer extends TileEntityRenderer {
	public CustomWaypointMarkerTileEntityRenderer(TileEntityRendererDispatcher disp) {
		super(disp);
	}

	@Override
	public void render(TileEntity marker, float partialTicks, MatrixStack matStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		String name = ((CustomWaypointMarkerTileEntity) marker).getWaypointName();
		if (name != null && shouldRenderName((CustomWaypointMarkerTileEntity) marker)) {
			matStack.pushPose();
			matStack.translate(0.5D, 0.5D, 0.5D);
			renderName(name, ((CustomWaypointMarkerTileEntity) marker).getFacingDirection(), matStack, buffer, combinedLight);
			matStack.popPose();
		}

	}

	private void renderName(String name, Direction facing, MatrixStack matStack, IRenderTypeBuffer buffer, int packedLight) {
		Minecraft mc = Minecraft.getInstance();
		float height = 0.45F;
		float facingOffset = -0.3F;
		float rotationDeg = 180.0F - facing.toYRot();
		matStack.pushPose();
		matStack.translate(facing.getStepX() * facingOffset, height, facing.getStepZ() * facingOffset);
		matStack.mulPose(Vector3f.YP.rotationDegrees(rotationDeg));
		float scale = 0.025F;
		matStack.scale(-scale, -scale, scale);
		Matrix4f mat = matStack.last().pose();
		float alpha = mc.options.getBackgroundOpacity(0.25F);
		int alphaI = LOTRClientUtil.getAlphaInt(alpha) << 24;
		FontRenderer font = renderer.getFont();
		int xOffset = -font.width(name) / 2;
		font.drawInBatch(name, xOffset, 0.0F, 553648127, false, mat, buffer, true, alphaI, packedLight);
		font.drawInBatch(name, xOffset, 0.0F, -1, false, mat, buffer, false, 0, packedLight);
		matStack.popPose();
	}

	private boolean shouldRenderName(CustomWaypointMarkerTileEntity marker) {
		if (Minecraft.renderNames()) {
			BlockPos pos = marker.getBlockPos();
			double dSq = renderer.camera.getEntity().distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			float renderRange = 32.0F;
			return dSq < renderRange * renderRange;
		}
		return false;
	}
}
