package lotr.client.render.model.vessel;

import java.util.*;

import javax.annotation.Nullable;

import lotr.common.item.*;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class VesselDrinkOverrideHandler extends ItemOverrideList {
	private Map vesselModels = new HashMap();

	public void putOverride(VesselType vessel, IBakedModel model) {
		vesselModels.put(vessel, model);
	}

	@Override
	public IBakedModel resolve(IBakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity) {
		VesselType vessel = VesselDrinkItem.getVessel(stack);
		return (IBakedModel) vesselModels.get(vessel);
	}
}
