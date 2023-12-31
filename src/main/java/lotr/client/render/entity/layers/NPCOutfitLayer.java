package lotr.client.render.entity.layers;

import java.util.function.Predicate;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.render.RandomTextureVariants;
import lotr.client.render.entity.ArmsStyleModelProvider;
import lotr.client.render.entity.model.LOTRBipedModel;
import lotr.common.entity.npc.NPCEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.EquipmentSlotType;

public class NPCOutfitLayer<E extends NPCEntity, M extends LOTRBipedModel<E>> extends LayerRenderer<E, M> {
	private final M standardArmsOutfitModel;
	private final M smallArmsOutfitModel;
	private final RandomTextureVariants outfitSkins;
	private final EquipmentSlotType requiredEmptySlot;
	private final float proportionWithOutfit;
	private final Predicate<E> genderCheck;

	public NPCOutfitLayer(IEntityRenderer<E, M> renderer, ArmsStyleModelProvider<E, M> armsStyleModelProvider, RandomTextureVariants skins, EquipmentSlotType slot) {
		this(renderer, armsStyleModelProvider, skins, slot, 1.0F);
	}

	public NPCOutfitLayer(IEntityRenderer<E, M> renderer, ArmsStyleModelProvider<E, M> armsStyleModelProvider, RandomTextureVariants skins, EquipmentSlotType slot, float prop) {
		this(renderer, armsStyleModelProvider, skins, slot, prop, hummel -> NPCOutfitLayer.anyGender((NPCEntity) hummel));
	}

	public NPCOutfitLayer(IEntityRenderer<E, M> renderer, ArmsStyleModelProvider<E, M> armsStyleModelProvider, RandomTextureVariants skins, EquipmentSlotType slot, float prop, Predicate<E> gender) {
		super(renderer);
		standardArmsOutfitModel = armsStyleModelProvider.getModelForArmsStyle(false);
		smallArmsOutfitModel = armsStyleModelProvider.getModelForArmsStyle(true);
		outfitSkins = skins;
		requiredEmptySlot = slot;
		proportionWithOutfit = prop;
		genderCheck = gender;
	}

	@Override
	public void render(MatrixStack matStack, IRenderTypeBuffer buf, int packedLight, E entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (genderCheck.test(entity) && ((NPCEntity) entity).getItemBySlot(requiredEmptySlot).isEmpty() && RandomTextureVariants.nextFloat(entity) < proportionWithOutfit) {
			M outfitModel = ((NPCEntity) entity).useSmallArmsModel() ? smallArmsOutfitModel : standardArmsOutfitModel;
			coloredCutoutModelCopyLayerRender(getParentModel(), outfitModel, outfitSkins.getRandomSkin(entity), matStack, buf, packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
		}

	}

	public static boolean anyGender(NPCEntity entity) {
		return true;
	}

	public static boolean femaleOnly(NPCEntity entity) {
		return entity.getPersonalInfo().isFemale();
	}

	public static boolean maleOnly(NPCEntity entity) {
		return entity.getPersonalInfo().isMale();
	}
}
