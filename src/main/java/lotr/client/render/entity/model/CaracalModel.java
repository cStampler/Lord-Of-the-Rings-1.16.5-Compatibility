package lotr.client.render.entity.model;

import com.google.common.collect.ImmutableList;

import lotr.common.entity.animal.CaracalEntity;
import lotr.common.util.LOTRUtil;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class CaracalModel extends AgeableModel {
	private final TransformableModelRenderer body;
	private final ModelRenderer head;
	private final ModelRenderer noseBridge;
	private final ModelRenderer noseMark;
	private final ModelRenderer earL;
	private final ModelRenderer earR;
	private final ModelRenderer tuftL;
	private final ModelRenderer tuftR;
	private final ModelRenderer tailMain;
	private final ModelRenderer tailEnd;
	private final ModelRenderer backLegL;
	private final ModelRenderer backLegR;
	private final ModelRenderer frontLegL;
	private final ModelRenderer frontLegR;
	private CaracalModel.State state;

	public CaracalModel(float f) {
		super(true, 10.0F, 4.0F);
		state = CaracalModel.State.NORMAL;
		body = new TransformableModelRenderer(this, 22, 0);
		body.setPos(0.0F, 17.0F, 0.0F);
		body.addBox(-3.0F, -8.0F, -1.0F, 6.0F, 16.0F, 6.0F, f, false);
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 14.0F, -10.0F);
		head.addBox(-3.0F, -4.0F, -3.0F, 6.0F, 5.0F, 5.0F, f, false);
		head.texOffs(0, 15).addBox(-1.5F, -1.0F, -4.5F, 3.0F, 2.0F, 2.0F, f, false);
		noseBridge = new ModelRenderer(this, 0, 10);
		noseBridge.setPos(0.0F, -1.0F, -4.0F);
		noseBridge.addBox(-1.0F, -2.75F, -0.435F, 2.0F, 3.0F, 2.0F, f, false);
		head.addChild(noseBridge);
		noseMark = new ModelRenderer(this, 8, 13);
		noseMark.setPos(1.7F, -2.0F, -3.1F);
		noseMark.addBox(-1.7F, -0.5F, -1.335F, 1.0F, 1.0F, 1.0F, f, false);
		noseMark.yRot = (float) Math.toRadians(30.0D);
		head.addChild(noseMark);
		earL = new ModelRenderer(this, 0, 22);
		earL.setPos(2.0F, -3.0F, 0.0F);
		earL.addBox(-0.5F, -3.0F, -1.0F, 1.0F, 2.0F, 2.0F, f, true);
		head.addChild(earL);
		earR = new ModelRenderer(this, 0, 22);
		earR.setPos(-2.0F, -3.0F, 0.0F);
		earR.addBox(-0.5F, -3.0F, -1.0F, 1.0F, 2.0F, 2.0F, f, false);
		head.addChild(earR);
		tuftL = new ModelRenderer(this, 6, 23);
		tuftL.setPos(0.0F, -3.0F, 0.0F);
		tuftL.addBox(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F, f, true);
		earL.addChild(tuftL);
		tuftR = new ModelRenderer(this, 6, 23);
		tuftR.setPos(0.0F, -3.0F, 0.0F);
		tuftR.addBox(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F, f, false);
		earR.addChild(tuftR);
		tailMain = new ModelRenderer(this, 22, 23);
		tailMain.setPos(0.0F, 14.5F, 6.0F);
		tailMain.addBox(-0.5F, 1.0F, 1.0F, 1.0F, 8.0F, 1.0F, f, false);
		tailEnd = new ModelRenderer(this, 26, 23);
		tailEnd.setPos(0.0F, 9.0F, 1.0F);
		tailEnd.addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, f, false);
		tailMain.addChild(tailEnd);
		backLegL = new ModelRenderer(this, 54, 0);
		backLegL.setPos(1.1F, 14.0F, 6.0F);
		backLegL.addBox(-0.4F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, f, true);
		backLegR = new ModelRenderer(this, 54, 0);
		backLegR.setPos(-1.1F, 14.0F, 6.0F);
		backLegR.addBox(-1.6F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, f, false);
		frontLegL = new ModelRenderer(this, 46, 0);
		frontLegL.setPos(1.2F, 14.0F, -5.0F);
		frontLegL.addBox(-0.4F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, f, true);
		frontLegR = new ModelRenderer(this, 46, 0);
		frontLegR.setPos(-1.2F, 14.0F, -5.0F);
		frontLegR.addBox(-1.6F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, f, false);
	}

	@Override
	protected Iterable bodyParts() {
		return ImmutableList.of(body, backLegL, backLegR, frontLegL, frontLegR, tailMain);
	}

	public ModelRenderer getHead() {
		return head;
	}

	@Override
	protected Iterable headParts() {
		return ImmutableList.of(head);
	}

	private boolean isCaracalAsleepOrOnBed(CaracalEntity caracal) {
		return caracal.isSleeping() || caracal.isLying();
	}

	public void setLivingAnimations(CaracalEntity caracal, float limbSwing, float limbSwingAmount, float partialTick) {
		body.x = 0.0F;
		body.y = 17.0F;
		body.z = 0.0F;
		head.x = 0.0F;
		head.y = 14.0F;
		head.z = -10.0F;
		tailMain.x = 0.0F;
		tailMain.y = 14.5F;
		tailMain.z = 6.0F;
		frontLegL.x = 1.2F;
		frontLegL.y = 14.0F;
		frontLegL.z = -5.0F;
		frontLegR.x = -1.2F;
		frontLegR.y = 14.0F;
		frontLegR.z = -5.0F;
		backLegL.x = 1.1F;
		backLegL.y = 14.0F;
		backLegL.z = 6.0F;
		backLegR.x = -1.1F;
		backLegR.y = 14.0F;
		backLegR.z = 6.0F;
		ModelRenderer var10000;
		if (caracal.isFloppa()) {
			body.setScaleAndTranslation(1.25F, 1.1F, 1.0F, 0.0D, -1.7D, 0.0D);
			float legShift = 0.3F;
			var10000 = frontLegL;
			var10000.x += legShift;
			var10000 = frontLegR;
			var10000.x -= legShift;
			var10000 = backLegL;
			var10000.x += legShift;
			var10000 = backLegR;
			var10000.x -= legShift;
		} else {
			body.resetScaleAndTranslation();
		}

		noseMark.visible = caracal.isFloppa();
		if (!caracal.isInSittingPose() && !isCaracalAsleepOrOnBed(caracal)) {
			if (caracal.isCrouching()) {
				++body.y;
				var10000 = head;
				var10000.y += 2.0F;
				++tailMain.y;
				tailMain.xRot = (float) Math.toRadians(90.0D);
				state = CaracalModel.State.SNEAKING;
			} else if (caracal.isSprinting()) {
				tailMain.xRot = (float) Math.toRadians(90.0D);
				state = CaracalModel.State.SPRINTING;
			} else {
				state = CaracalModel.State.NORMAL;
			}
		} else {
			state = CaracalModel.State.LYING;
			TransformableModelRenderer var6 = body;
			var6.y += 5.0F;
			var6 = body;
			var6.x -= 2.0F;
			var10000 = head;
			var10000.y += 5.0F;
			--head.x;
			var10000 = tailMain;
			var10000.y += 5.0F;
			var10000 = tailMain;
			var10000.x -= 2.0F;
			var10000 = frontLegL;
			var10000.y += 8.0F;
			var10000 = frontLegR;
			var10000.y += 8.0F;
			var10000 = backLegL;
			var10000.y += 8.0F;
			var10000 = backLegR;
			var10000.y += 8.0F;
			var10000 = frontLegR;
			var10000.x -= 2.0F;
			var10000 = frontLegR;
			var10000.z -= 3.0F;
			var10000 = backLegR;
			var10000.z -= 2.0F;
		}

	}

	@Override
	public void setupAnim(Entity caracal, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.xRot = (float) Math.toRadians(headPitch);
		head.yRot = (float) Math.toRadians(netHeadYaw);
		noseBridge.xRot = (float) Math.toRadians(-27.5D);
		ModelRenderer var10000;
		if (isCaracalAsleepOrOnBed((CaracalEntity) caracal)) {
			var10000 = head;
			var10000.xRot += (float) Math.toRadians(30.0D) + MathHelper.cos(ageInTicks / 30.0F) * (float) Math.toRadians(10.0D);
		}

		float idleFlop;
		float idleTuftFlop;
		float motionFlop;
		if (((CaracalEntity) caracal).isFlopping()) {
			idleFlop = 0.75F;
			idleTuftFlop = LOTRUtil.normalisedCos(ageInTicks * idleFlop);
			motionFlop = LOTRUtil.normalisedCos(ageInTicks * idleFlop + 1.5707964F);
			earL.xRot = (float) Math.toRadians(-4.0D - idleTuftFlop * 12.0D);
			earL.yRot = (float) Math.toRadians(6.5D + idleTuftFlop * 35.0D);
			earL.zRot = (float) Math.toRadians(5.0D + idleTuftFlop * 50.0D);
			earR.xRot = earL.xRot;
			earR.yRot = -earL.yRot;
			earR.zRot = -earL.zRot;
			tuftL.zRot = (float) Math.toRadians(3.0D + motionFlop * 40.0D);
		} else if (((CaracalEntity) caracal).areEarsAlert()) {
			earL.xRot = 0.0F;
			earL.yRot = (float) Math.toRadians(10.5D);
			earL.zRot = (float) Math.toRadians(6.0D);
			earR.xRot = earL.xRot;
			earR.yRot = -earL.yRot;
			earR.zRot = -earL.zRot;
			tuftL.zRot = (float) Math.toRadians(4.0D);
		} else {
			idleFlop = MathHelper.cos(ageInTicks / 20.0F);
			idleTuftFlop = MathHelper.cos(ageInTicks / 20.0F + 1.5707964F);
			motionFlop = LOTRUtil.normalisedCos(limbSwing * 1.0F) * limbSwingAmount;
			float motionTuftFlop = LOTRUtil.normalisedCos(limbSwing * 1.0F + 1.5707964F) * limbSwingAmount;
			float floppaHealth = ((CaracalEntity) caracal).getHealth() / ((CaracalEntity) caracal).getMaxHealth();
			double baseTuftTlop = MathHelper.lerp(floppaHealth, 100.0D, 20.0D);
			earL.xRot = (float) Math.toRadians(-4.0D - motionFlop * 30.0D - idleFlop * 4.0D);
			earL.yRot = (float) Math.toRadians(17.5D + motionFlop * 5.0D + idleFlop * 2.0D);
			earL.zRot = (float) Math.toRadians(12.5D + motionFlop * 20.0D + idleFlop * 3.0D);
			if (state == CaracalModel.State.LYING) {
				var10000 = earL;
				var10000.zRot += (float) Math.toRadians(8.0D);
			}

			earR.xRot = earL.xRot;
			earR.yRot = -earL.yRot;
			earR.zRot = -earL.zRot;
			tuftL.zRot = (float) Math.toRadians(baseTuftTlop + motionTuftFlop * 30.0D + idleTuftFlop * 4.0D);
		}
		tuftR.zRot = -tuftL.zRot;

		body.xRot = (float) Math.toRadians(90.0D);
		tailMain.xRot = (float) Math.toRadians(45.0D);
		tailEnd.xRot = (float) Math.toRadians(45.0D);
		tailMain.yRot = tailEnd.yRot = 0.0F;
		if (state != CaracalModel.State.LYING) {
			backLegL.zRot = backLegR.zRot = frontLegL.zRot = frontLegR.zRot = 0.0F;
			if (state == CaracalModel.State.SPRINTING) {
				backLegL.xRot = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
				backLegR.xRot = MathHelper.cos(limbSwing * 0.6662F + 0.3F) * limbSwingAmount;
				frontLegL.xRot = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F + 0.3F) * limbSwingAmount;
				frontLegR.xRot = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * limbSwingAmount;
				tailEnd.xRot = (float) Math.toRadians(44.0D + 18.0D * MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount);
			} else {
				backLegL.xRot = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
				backLegR.xRot = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * limbSwingAmount;
				frontLegL.xRot = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * limbSwingAmount;
				frontLegR.xRot = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
				if (state == CaracalModel.State.NORMAL) {
					tailEnd.xRot = (float) Math.toRadians(44.0D + 45.0D * MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount);
				} else {
					tailEnd.xRot = (float) Math.toRadians(44.0D + 27.0D * MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount);
				}
			}
		} else {
			backLegL.zRot = backLegR.zRot = frontLegL.zRot = frontLegR.zRot = (float) Math.toRadians(-85.0D);
			frontLegL.xRot = (float) Math.toRadians(20.0D);
			frontLegR.xRot = (float) Math.toRadians(-20.0D);
			backLegL.xRot = (float) Math.toRadians(20.0D);
			backLegR.xRot = (float) Math.toRadians(-20.0D);
			tailMain.xRot = (float) Math.toRadians(60.0D);
			tailEnd.xRot = (float) Math.toRadians(30.0D);
			tailMain.yRot = MathHelper.cos(ageInTicks / 30.0F) * (float) Math.toRadians(35.0D);
			tailEnd.yRot = tailMain.yRot * 1.3F;
		}

		if (((CaracalEntity) caracal).isRaidingChest()) {
			frontLegL.xRot = MathHelper.cos(ageInTicks * 1.3F + 3.1415927F) * limbSwingAmount;
			frontLegR.xRot = MathHelper.cos(ageInTicks * 1.3F) * limbSwingAmount;
		}

	}

	private enum State {
		SNEAKING, NORMAL, SPRINTING, LYING;
	}
}
