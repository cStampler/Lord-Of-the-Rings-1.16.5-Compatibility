package lotr.client.render.entity.model.armor;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lotr.client.event.LOTRTickHandlerClient;
import lotr.common.config.LOTRConfig;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class DolAmrothChestplateModel extends SpecialArmorModel implements WearerDependentArmorModel {
	private final float modelSize;
	private int numWings;
	private ModelRenderer[] wingsRight;
	private ModelRenderer[] wingsLeft;

	public DolAmrothChestplateModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public DolAmrothChestplateModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		modelSize = f;
		clearNonChestplateParts();
		numWings = getNumWings();
		recreateBodyAndWings();
		rightArm = new ModelRenderer(this, 24, 0);
		rightArm.setPos(-5.0F, 2.0F, 0.0F);
		rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		leftArm = new ModelRenderer(this, 24, 0);
		leftArm.setPos(5.0F, 2.0F, 0.0F);
		leftArm.mirror = true;
		leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
	}

	@Override
	public void acceptWearingEntity(LivingEntity entity) {
		float partialTicks = LOTRTickHandlerClient.renderPartialTick;
		float limbSwing = 0.0F;
		float limbSwingAmount = 0.0F;
		boolean shouldSit = entity.isPassenger() && entity.getVehicle() != null && entity.getVehicle().shouldRiderSit();
		if (!shouldSit && entity.isAlive()) {
			limbSwingAmount = MathHelper.lerp(partialTicks, entity.animationSpeedOld, entity.animationSpeed);
			limbSwing = entity.animationPosition - entity.animationSpeed * (1.0F - partialTicks);
			if (entity.isBaby()) {
				limbSwing *= 3.0F;
			}

			limbSwingAmount = Math.min(limbSwingAmount, 1.0F);
		}

		float ageInTicks = entity.tickCount + partialTicks;
		float netHeadYaw = 0.0F;
		float headPitch = 0.0F;
		this.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	}

	private void recreateBodyAndWings() {
		float f = modelSize;
		body = new ModelRenderer(this, 0, 0);
		body.setPos(0.0F, 0.0F, 0.0F);
		body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, f);
		if (numWings > 0) {
			wingsRight = new ModelRenderer[numWings];

			int i;
			ModelRenderer wing;
			for (i = 0; i < wingsRight.length; ++i) {
				wing = new ModelRenderer(this, 0, 16);
				wing.setPos(-2.0F, 0.0F, 0.0F);
				wing.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F, 0.0F);
				wing.texOffs(6, 16).addBox(-2.0F, 1.0F, 0.5F, 2.0F, 10.0F, 0.0F, 0.0F);
				wingsRight[i] = wing;
			}

			for (i = 0; i < wingsRight.length - 1; ++i) {
				wingsRight[i].addChild(wingsRight[i + 1]);
			}

			wingsRight[0].setPos(-2.0F, 1.0F, 1.0F);
			body.addChild(wingsRight[0]);
			wingsLeft = new ModelRenderer[numWings];

			for (i = 0; i < wingsLeft.length; ++i) {
				wing = new ModelRenderer(this, 0, 16);
				wing.setPos(2.0F, 0.0F, 0.0F);
				wing.mirror = true;
				wing.addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F, 0.0F);
				wing.texOffs(6, 16).addBox(0.0F, 1.0F, 0.5F, 2.0F, 10.0F, 0.0F, 0.0F);
				wingsLeft[i] = wing;
			}

			for (i = 0; i < wingsLeft.length - 1; ++i) {
				wingsLeft[i].addChild(wingsLeft[i + 1]);
			}

			wingsLeft[0].setPos(2.0F, 1.0F, 1.0F);
			body.addChild(wingsLeft[0]);
		} else {
			wingsRight = new ModelRenderer[0];
			wingsLeft = new ModelRenderer[0];
		}

	}

	@Override
	public void renderToBuffer(MatrixStack matStack, IVertexBuilder buf, int packedLight, int packedOverlay, float r, float g, float b, float a) {
		int currentConfigNumWings = getNumWings();
		if (numWings != currentConfigNumWings) {
			numWings = currentConfigNumWings;
			recreateBodyAndWings();
		}

		super.renderToBuffer(matStack, buf, packedLight, packedOverlay, r, g, b, a);
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		if (numWings > 0) {
			float motion = limbSwingAmount;
			float motionPhase = limbSwing;
			float wingYaw;
			if (entity != null && entity.getVehicle() instanceof LivingEntity) {
				LivingEntity mount = (LivingEntity) entity.getVehicle();
				wingYaw = LOTRTickHandlerClient.renderPartialTick;
				motion = mount.animationSpeedOld + (mount.animationSpeed - mount.animationSpeedOld) * wingYaw;
				motionPhase = mount.animationPosition - mount.animationSpeed * (1.0F - wingYaw);
				motion *= 1.5F;
				motionPhase *= 2.0F;
			}

			float wingAngleBase = (float) Math.toRadians(10.0D);
			wingAngleBase += MathHelper.sin(ageInTicks * 0.02F) * 0.01F;
			wingAngleBase += MathHelper.sin(motionPhase * 0.2F) * 0.03F * motion;
			wingYaw = (float) Math.toRadians(50.0D);
			wingYaw += MathHelper.sin(ageInTicks * 0.03F) * 0.05F;
			wingYaw += MathHelper.sin(motionPhase * 0.25F) * 0.12F * motion;

			for (int i = 0; i < wingsRight.length; ++i) {
				float factor = i + 1;
				float wingAngle = wingAngleBase / (factor / 3.4F);
				wingsRight[i].zRot = wingAngle;
				wingsLeft[i].zRot = -wingAngle;
			}

			wingsRight[0].yRot = MathHelper.sin(wingYaw);
			wingsRight[0].xRot = MathHelper.cos(wingYaw);
			wingsLeft[0].yRot = MathHelper.sin(-wingYaw);
			wingsLeft[0].xRot = MathHelper.cos(-wingYaw);
		}

	}

	private static int getNumWings() {
		return (Integer) LOTRConfig.CLIENT.dolAmrothChestplateWings.get();
	}
}
