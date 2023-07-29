package lotr.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.render.entity.model.CaracalModel;
import lotr.common.entity.animal.CaracalEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class CaracalHeldItemLayer extends LayerRenderer {
	public CaracalHeldItemLayer(IEntityRenderer renderer) {
		super(renderer);
	}

	@Override
	public void render(MatrixStack matStack, IRenderTypeBuffer buf, int packedLight, Entity caracal, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		boolean child = ((CaracalEntity) caracal).isBaby();
		matStack.pushPose();
		if (child) {
			matStack.scale(0.75F, 0.75F, 0.75F);
			matStack.translate(0.0D, 0.5D, 0.209375D);
		}

		ModelRenderer head = ((CaracalModel) getParentModel()).getHead();
		matStack.translate(head.x / 16.0F, head.y / 16.0F, head.z / 16.0F);
		matStack.mulPose(Vector3f.YP.rotationDegrees(netHeadYaw));
		matStack.mulPose(Vector3f.XP.rotationDegrees(headPitch));
		if (child) {
			matStack.translate(0.06D, 0.11D, -0.4D);
		} else {
			matStack.translate(0.06D, 0.02D, -0.4D);
		}

		matStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
		ItemStack heldItem = ((CaracalEntity) caracal).getItemBySlot(EquipmentSlotType.MAINHAND);
		Minecraft.getInstance().getItemInHandRenderer().renderItem((CaracalEntity) caracal, heldItem, TransformType.GROUND, false, matStack, buf, packedLight);
		matStack.popPose();
	}
}
