package lotr.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.render.entity.model.LOTRBipedModel;
import lotr.common.entity.npc.NPCEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel.ArmPose;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;

public class LOTRBipedRenderer extends BipedRenderer {
	public static final float PLAYER_SCALE = 0.9375F;
	protected static final float BIPED_SHADOW_SIZE = 0.5F;
	private final LOTRBipedModel standardArmsModel;
	private final LOTRBipedModel smallArmsModel;

	public LOTRBipedRenderer(EntityRendererManager mgr, ArmsStyleModelProvider armsStyleModelProvider, LOTRBipedModel leggingsModel, LOTRBipedModel mainArmorModel, float shadowSize) {
		super(mgr, armsStyleModelProvider.getModelForArmsStyle(false), shadowSize);
		standardArmsModel = (LOTRBipedModel) model;
		smallArmsModel = armsStyleModelProvider.getModelForArmsStyle(true);
		addLayer(new BipedArmorLayer(this, leggingsModel, mainArmorModel));
	}

	protected void preRenderCallback(NPCEntity entity, MatrixStack matStack, float f) {
		super.scale(entity, matStack, f);
		matStack.scale(0.9375F, 0.9375F, 0.9375F);
	}

	public void render(Entity entity, float yaw, float partialTicks, MatrixStack matStack, IRenderTypeBuffer buf, int packedLight) {
		selectEntityModelForArmsStyle((NPCEntity) entity);
		((LOTRBipedModel) model).crouching = entity.isShiftKeyDown();
		((LOTRBipedModel) model).showChest = ((NPCEntity) entity).shouldRenderNPCChest();
		((LOTRBipedModel) model).isEating = ((NPCEntity) entity).getNPCItemsInv().getIsEating();
		((LOTRBipedModel) model).setTalkAnimation(((NPCEntity) entity).getTalkAnimations(), partialTicks);
		setArmPoses((NPCEntity) entity);
		super.render((MobEntity) entity, yaw, partialTicks, matStack, buf, packedLight);
	}

	private void selectEntityModelForArmsStyle(NPCEntity entity) {
		if (entity.useSmallArmsModel()) {
			model = smallArmsModel;
		} else {
			model = standardArmsModel;
		}

	}

	private void setArmPoses(NPCEntity entity) {
		ArmPose mainArmPose = getArmPose(entity, Hand.MAIN_HAND);
		ArmPose offArmPose = getArmPose(entity, Hand.OFF_HAND);
		if (mainArmPose.isTwoHanded()) {
			offArmPose = entity.getOffhandItem().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
		}

		if (entity.getMainArm() == HandSide.RIGHT) {
			((LOTRBipedModel) model).rightArmPose = mainArmPose;
			((LOTRBipedModel) model).leftArmPose = offArmPose;
		} else {
			((LOTRBipedModel) model).rightArmPose = offArmPose;
			((LOTRBipedModel) model).leftArmPose = mainArmPose;
		}

	}

	private static ArmPose getArmPose(LivingEntity entity, Hand hand) {
		ItemStack heldItem = entity.getItemInHand(hand);
		if (heldItem.isEmpty()) {
			return ArmPose.EMPTY;
		}
		if (entity.getUsedItemHand() == hand && entity.getUseItemRemainingTicks() > 0) {
			UseAction useaction = heldItem.getUseAnimation();
			if (useaction == UseAction.BLOCK) {
				return ArmPose.BLOCK;
			}

			if (useaction == UseAction.BOW) {
				return ArmPose.BOW_AND_ARROW;
			}

			if (useaction == UseAction.SPEAR) {
				return ArmPose.THROW_SPEAR;
			}

			if (useaction == UseAction.CROSSBOW && hand == entity.getUsedItemHand()) {
				return ArmPose.CROSSBOW_CHARGE;
			}
		} else if (!entity.swinging && heldItem.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(heldItem)) {
			return ArmPose.CROSSBOW_HOLD;
		}

		return ArmPose.ITEM;
	}
}
