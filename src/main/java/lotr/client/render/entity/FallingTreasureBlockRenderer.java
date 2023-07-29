package lotr.client.render.entity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.common.entity.item.FallingTreasureBlockEntity;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

public class FallingTreasureBlockRenderer extends EntityRenderer {
	public FallingTreasureBlockRenderer(EntityRendererManager mgr) {
		super(mgr);
		shadowRadius = 0.5F;
	}

	@Override
	public ResourceLocation getTextureLocation(Entity entity) {
		return PlayerContainer.BLOCK_ATLAS;
	}

	@Override
	public void render(Entity entity, float yaw, float tick, MatrixStack matStack, IRenderTypeBuffer buf, int packedLight) {
		BlockState state = ((FallingTreasureBlockEntity) entity).getFallTile();
		if (state.getRenderShape() == BlockRenderType.MODEL) {
			World world = entity.getCommandSenderWorld();
			if (state != world.getBlockState(entity.blockPosition()) && state.getRenderShape() != BlockRenderType.INVISIBLE) {
				matStack.pushPose();
				BlockPos pos = new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
				matStack.translate(-0.5D, 0.0D, -0.5D);
				BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
				for (RenderType type : RenderType.chunkBufferLayers()) {
					if (RenderTypeLookup.canRenderInLayer(state, type)) {
						ForgeHooksClient.setRenderLayer(type);
						blockrendererdispatcher.getModelRenderer().renderModel(world, blockrendererdispatcher.getBlockModel(state), state, pos, matStack, buf.getBuffer(type), false, new Random(), state.getSeed(((FallingTreasureBlockEntity) entity).getOrigin()), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
					}
				}

				ForgeHooksClient.setRenderLayer((RenderType) null);
				matStack.popPose();
				super.render(entity, yaw, tick, matStack, buf, packedLight);
			}
		}

	}
}
