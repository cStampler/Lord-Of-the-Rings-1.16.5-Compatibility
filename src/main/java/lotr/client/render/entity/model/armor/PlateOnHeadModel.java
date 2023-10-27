package lotr.client.render.entity.model.armor;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lotr.client.event.LOTRTickHandlerClient;
import lotr.common.entity.capabilities.PlateFallingData;
import lotr.common.entity.capabilities.PlateFallingDataProvider;
import lotr.common.init.LOTRBlocks;
import lotr.common.item.PlateItem;
import lotr.common.tileentity.PlateTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.util.LazyOptional;

public class PlateOnHeadModel extends SpecialArmorModel implements ItemStackDependentModel, WearerDependentArmorModel {
	private Block plateBlock;
	private LazyOptional fallingData = LazyOptional.empty();
	private ItemStack currentHeldItem;
	private final PlateTileEntity fakePlateTE;

	public PlateOnHeadModel(BipedModel referenceBipedModel) {
		super(referenceBipedModel, 0.0F);
		currentHeldItem = ItemStack.EMPTY;
		fakePlateTE = new PlateTileEntity();
	}

	@Override
	public void acceptWearingEntity(LivingEntity entity) {
		fallingData = entity.getCapability(PlateFallingDataProvider.CAPABILITY);
		currentHeldItem = entity.getMainHandItem();
	}

	private void renderPlateBlockModel(MatrixStack matStack, IRenderTypeBuffer renderTypeBuf, int light, int overlay) {
		BlockState state = plateBlock.defaultBlockState();
		Minecraft.getInstance().getBlockRenderer().renderBlock(state, matStack, renderTypeBuf, light, overlay, EmptyModelData.INSTANCE);
	}

	@Override
	public void renderToBuffer(MatrixStack matStack, IVertexBuilder buf, int packedLight, int packedOverlay, float r, float g, float b, float a) {
		float partialTick = LOTRTickHandlerClient.renderPartialTick;
		float fallingOffset = (Float) fallingData.map(d -> ((PlateFallingData) d).getPlateOffsetY(partialTick)).orElse(0.0F);
		IRenderTypeBuffer renderTypeBuf = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.disableCull();
		matStack.pushPose();
		head.translateAndRotate(matStack);
		matStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		matStack.translate(-0.5D, 0.45D + fallingOffset * 0.5D, -0.5D);
		renderPlateBlockModel(matStack, renderTypeBuf, packedLight, packedOverlay);
		if (PlateTileEntity.isValidFoodItem(currentHeldItem)) {
			ItemStack heldItemMinusOne = currentHeldItem.copy();
			heldItemMinusOne.shrink(1);
			if (!heldItemMinusOne.isEmpty()) {
				fakePlateTE.setFoodItem(heldItemMinusOne);
				fakePlateTE.setFallingDataForRender(fallingData);
				TileEntityRendererDispatcher.instance.getRenderer(fakePlateTE).render(fakePlateTE, partialTick, matStack, renderTypeBuf, packedLight, packedOverlay);
				fakePlateTE.setFoodItem((ItemStack) null);
			}
		}

		matStack.popPose();
		RenderSystem.enableCull();
		fakePlateTE.setFallingDataForRender(fallingData = LazyOptional.empty());
	}

	@Override
	public void setModelItem(ItemStack stack) {
		if (stack.getItem() instanceof PlateItem) {
			plateBlock = ((PlateItem) stack.getItem()).getBlock();
		} else {
			plateBlock = (Block) LOTRBlocks.FINE_PLATE.get();
		}

	}
}
