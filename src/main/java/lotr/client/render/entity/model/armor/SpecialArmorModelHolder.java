package lotr.client.render.entity.model.armor;

import java.util.*;

import net.minecraft.client.renderer.entity.model.BipedModel;

public class SpecialArmorModelHolder {
	private final Map modelMap = new HashMap();
	private final SpecialArmorModelHolder.SpecialArmorModelFactory modelFactory;

	public SpecialArmorModelHolder(SpecialArmorModelHolder.SpecialArmorModelFactory factory) {
		modelFactory = factory;
	}

	public SpecialArmorModel getSpecialModelFromBipedReference(BipedModel referenceBipedModel) {
		Class modelClass = referenceBipedModel.getClass();
		return (SpecialArmorModel) modelMap.computeIfAbsent(modelClass, cls -> modelFactory.createModel(referenceBipedModel));
	}

	@FunctionalInterface
	public interface SpecialArmorModelFactory {
		SpecialArmorModel createModel(BipedModel var1);
	}
}
