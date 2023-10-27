package lotr.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.matrix.MatrixStack.Entry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lotr.client.render.model.vessel.VesselDrinkModel;
import lotr.common.block.VesselDrinkBlock;
import lotr.common.item.VesselType;
import lotr.common.tileentity.VesselDrinkTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class VesselDrinkTileEntityRenderer extends TileEntityRenderer {
	public VesselDrinkTileEntityRenderer(TileEntityRendererDispatcher disp) {
		super(disp);
	}

	private Direction getRotationToMatchBlockstateJson(Direction stateFacing) {
		return stateFacing != Direction.NORTH && stateFacing != Direction.SOUTH ? stateFacing : stateFacing.getOpposite();
	}

	@Override
	public void render(TileEntity vessel, float partialTicks, MatrixStack matStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (!((VesselDrinkTileEntity) vessel).isEmpty()) {
			ItemStack vesselItem = ((VesselDrinkTileEntity) vessel).getVesselItem();
			VesselType vesselType = ((VesselDrinkTileEntity) vessel).getVesselType();
			RenderSystem.enableRescaleNormal();
			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			matStack.pushPose();
			matStack.translate(0.5D, 0.0D, 0.5D);
			Direction rotateDir = getRotationToMatchBlockstateJson(vessel.getBlockState().getValue(VesselDrinkBlock.FACING));
			matStack.mulPose(Vector3f.YP.rotationDegrees(rotateDir.toYRot()));
			matStack.translate(-0.5D, 0.0D, -0.5D);
			matStack.scale(0.0625F, 0.0625F, 0.0625F);
			TextureAtlasSprite liquidIcon = VesselDrinkModel.getLiquidIconFor(vesselItem);
			RenderType renderType = RenderType.entitySolid(liquidIcon.atlas().location());
			IVertexBuilder vb = buffer.getBuffer(renderType);
			if (vesselType != VesselType.WOODEN_MUG && vesselType != VesselType.CERAMIC_MUG) {
				if (vesselType != VesselType.GOLDEN_GOBLET && vesselType != VesselType.SILVER_GOBLET && vesselType != VesselType.COPPER_GOBLET && vesselType != VesselType.WOODEN_CUP) {
					if (vesselType == VesselType.ALE_HORN || vesselType == VesselType.GOLDEN_ALE_HORN) {
						this.renderLiquidSurface(matStack, vb, liquidIcon, 6, 9, 9.5F, 6.875F, 12.5F, 9.125F, 6.375F, combinedLight, combinedOverlay);
					}
				} else {
					this.renderLiquidSurface(matStack, vb, liquidIcon, 6, 9, 2.25F, 6.0F, combinedLight, combinedOverlay);
				}
			} else {
				this.renderLiquidSurface(matStack, vb, liquidIcon, 6, 10, 3.0F, 5.25F, combinedLight, combinedOverlay);
			}

			matStack.popPose();
			RenderSystem.disableBlend();
			RenderSystem.enableCull();
			RenderSystem.disableRescaleNormal();
		}

	}

	private void renderLiquidSurface(MatrixStack matStack, IVertexBuilder vb, TextureAtlasSprite icon, int uvMin, int uvMax, float x0, float z0, float x1, float z1, float y, int light, int overlay) {
		float actualUVMin = uvMin / 2.0F;
		float actualUVMax = uvMax / 2.0F;
		float minU = icon.getU(actualUVMin);
		float maxU = icon.getU(actualUVMax);
		float minV = icon.getV(actualUVMin);
		float maxV = icon.getV(actualUVMax);
		Entry last = matStack.last();
		Matrix4f mat = last.pose();
		Matrix3f normal = last.normal();
		vb.vertex(mat, x0, y, z1).color(1.0F, 1.0F, 1.0F, 1.0F).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
		vb.vertex(mat, x1, y, z1).color(1.0F, 1.0F, 1.0F, 1.0F).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
		vb.vertex(mat, x1, y, z0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
		vb.vertex(mat, x0, y, z0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
	}

	private void renderLiquidSurface(MatrixStack matStack, IVertexBuilder vb, TextureAtlasSprite icon, int uvMin, int uvMax, float width, float y, int light, int overlay) {
		float halfWidth = width / 2.0F;
		float x0 = 8.0F - halfWidth;
		float z0 = 8.0F - halfWidth;
		float x1 = 8.0F + halfWidth;
		float z1 = 8.0F + halfWidth;
		this.renderLiquidSurface(matStack, vb, icon, uvMin, uvMax, x0, z0, x1, z1, y, light, overlay);
	}
}
