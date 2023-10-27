package lotr.client.render.tileentity;

import com.google.common.math.IntMath;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lotr.client.render.tileentity.model.PalantirModel;
import lotr.common.tileentity.PalantirTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class PalantirTileEntityRenderer extends TileEntityRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("lotr", "textures/entity/palantir/palantir.png");
	private static final ResourceLocation[] ANIMATED_INNER_TEXTURE = Util.make(new ResourceLocation[24], arr -> {
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = new ResourceLocation("lotr", String.format("textures/entity/palantir/inner_%d.png", i));
		}

	});
	private final PalantirModel innerOrbModel = new PalantirModel(true);
	private final PalantirModel othersModel = new PalantirModel(false);

	public PalantirTileEntityRenderer(TileEntityRendererDispatcher disp) {
		super(disp);
	}

	private int getInnerOrbLight(int combinedLight) {
		int blockLight = LightTexture.block(combinedLight);
		int skyLight = LightTexture.sky(combinedLight);
		return LightTexture.pack(Math.max(blockLight, 15), skyLight);
	}

	@Override
	public void render(TileEntity palantir, float partialTicks, MatrixStack matStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		int animationTick = ((PalantirTileEntity) palantir).getAnimationTick();
		int frame = IntMath.mod(animationTick / 3, 24);
		ResourceLocation innerTexture = ANIMATED_INNER_TEXTURE[frame];
		matStack.pushPose();
		matStack.translate(0.5D, 0.5D, 0.5D);
		matStack.scale(-1.0F, -1.0F, 1.0F);
		IVertexBuilder vb = buffer.getBuffer(RenderType.entityCutoutNoCull(innerTexture));
		innerOrbModel.renderToBuffer(matStack, vb, getInnerOrbLight(combinedLight), combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
		vb = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
		othersModel.renderToBuffer(matStack, vb, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
		matStack.popPose();
	}
}
