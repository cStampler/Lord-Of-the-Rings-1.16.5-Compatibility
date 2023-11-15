package lotr.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.render.entity.model.CaracalModel;
import lotr.common.entity.animal.CaracalEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class CaracalCollarLayer extends LayerRenderer<CaracalEntity, CaracalModel<CaracalEntity>> {
	private static final ResourceLocation CARACAL_COLLAR = new ResourceLocation("lotr", "textures/entity/caracal/caracal_collar.png");
	private final CaracalModel<CaracalEntity> collarModel = new CaracalModel<CaracalEntity>(0.01F);

	public CaracalCollarLayer(IEntityRenderer<CaracalEntity, CaracalModel<CaracalEntity>> renderer) {
		super(renderer);
	}

	@Override
	public void render(MatrixStack matStack, IRenderTypeBuffer buf, int packedLight, CaracalEntity caracal, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (((CaracalEntity) caracal).isTame()) {
			float[] collarRgb = ((CaracalEntity) caracal).getCollarColor().getTextureDiffuseColors();
			coloredCutoutModelCopyLayerRender(getParentModel(), collarModel, CARACAL_COLLAR, matStack, buf, packedLight, (CaracalEntity) caracal, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, collarRgb[0], collarRgb[1], collarRgb[2]);
		}

	}
}
