package lotr.client.render.tileentity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lotr.client.render.model.PlateFoodModels;
import lotr.common.entity.capabilities.PlateFallingData;
import lotr.common.tileentity.PlateTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.util.LazyOptional;

public class PlateTileEntityRenderer extends TileEntityRenderer {
	private Random rand = new Random(42984194L);

	public PlateTileEntityRenderer(TileEntityRendererDispatcher disp) {
		super(disp);
	}

	@Override
	public void render(TileEntity plate, float partialTicks, MatrixStack matStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		ItemStack plateItem = ((PlateTileEntity) plate).getFoodItem();
		LazyOptional fallingData = ((PlateTileEntity) plate).getFallingDataForRender();
		fallingData.map(d -> ((PlateFallingData) d).getPlateOffsetY(partialTicks)).orElse(0.0F);
		if (!plateItem.isEmpty()) {
			matStack.pushPose();
			RenderSystem.disableCull();
			RenderSystem.enableRescaleNormal();
			matStack.translate(0.5D, 0.0D, 0.5D);
			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
			PlateFoodModels.ModelAndHeight specialItemModel = PlateFoodModels.INSTANCE.getSpecialModel(plateItem.getItem());
			float itemHeight = getItemHeight(plateItem, specialItemModel, true);
			int foods = plateItem.getCount();
			float lowerOffset = 0.125F;

			for (int foodSlot = 0; foodSlot < foods; ++foodSlot) {
				matStack.pushPose();
				final int foodSlotF = foodSlot;
				float offset = (Float) fallingData.map(d -> ((PlateFallingData) d).getFoodOffsetY(foodSlotF, partialTicks)).orElse(0.0F);
				offset = Math.max(offset, lowerOffset);
				matStack.translate(0.0D, offset, 0.0D);
				lowerOffset = offset + itemHeight;
				rand.setSeed(plate.getBlockPos().getX() * 3129871 ^ plate.getBlockPos().getZ() * 116129781L ^ plate.getBlockPos().getY() + foodSlot * 5930563L);
				matStack.translate(0.0D, 0.03125D, 0.0D);
				float rotation = rand.nextFloat() * 360.0F;
				matStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
				matStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
				matStack.scale(0.5625F, 0.5625F, 0.5625F);
				if (specialItemModel == null) {
					itemRenderer.renderStatic(plateItem, TransformType.FIXED, combinedLight, combinedOverlay, matStack, buffer);
				} else {
					IBakedModel model = itemRenderer.getItemModelShaper().getModelManager().getModel(specialItemModel.modelRes);
					matStack.pushPose();
					model = ForgeHooksClient.handleCameraTransforms(matStack, model, TransformType.FIXED, false);
					matStack.translate(-0.5D, -0.5D, -0.5D);
					matStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
					matStack.translate(0.0D, -0.55D, 0.0D);
					RenderType renderType = RenderType.cutout();
					IVertexBuilder vertBuilder = ItemRenderer.getFoilBuffer(buffer, renderType, true, plateItem.hasFoil());
					Random itemRenderRand = new Random();
					long seed = 42L;
					Direction[] var25 = Direction.values();
					int var26 = var25.length;

					for (int var27 = 0; var27 < var26; ++var27) {
						Direction direction = var25[var27];
						itemRenderRand.setSeed(seed);
						itemRenderer.renderQuadList(matStack, vertBuilder, model.getQuads((BlockState) null, direction, itemRenderRand, (IModelData) null), plateItem, combinedLight, combinedOverlay);
					}

					itemRenderRand.setSeed(seed);
					itemRenderer.renderQuadList(matStack, vertBuilder, model.getQuads((BlockState) null, (Direction) null, itemRenderRand, (IModelData) null), plateItem, combinedLight, combinedOverlay);
					matStack.popPose();
				}

				matStack.popPose();
			}

			RenderSystem.disableRescaleNormal();
			RenderSystem.enableCull();
			matStack.popPose();
		}

	}

	public static float getItemHeight(ItemStack plateItem) {
		return getItemHeight(plateItem, (PlateFoodModels.ModelAndHeight) null, false);
	}

	private static float getItemHeight(ItemStack plateItem, PlateFoodModels.ModelAndHeight suppliedModel, boolean useSupplied) {
		if (!useSupplied) {
			suppliedModel = PlateFoodModels.INSTANCE.getSpecialModel(plateItem.getItem());
		}

		return suppliedModel != null ? suppliedModel.height : 0.03125F;
	}
}
