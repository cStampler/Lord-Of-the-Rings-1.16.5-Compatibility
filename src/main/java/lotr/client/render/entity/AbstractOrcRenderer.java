package lotr.client.render.entity;

import lotr.client.render.RandomTextureVariants;
import lotr.client.render.entity.model.OrcModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractOrcRenderer extends LOTRBipedRenderer {
	private static final RandomTextureVariants SKINS = RandomTextureVariants.loadSkinsList("lotr", "textures/entity/orc/orc");

	public AbstractOrcRenderer(EntityRendererManager mgr, ArmsStyleModelProvider armsStyleModelProvider, OrcModel leggingsModel, OrcModel mainArmorModel) {
		this(mgr, armsStyleModelProvider, leggingsModel, mainArmorModel, 0.5F);
	}

	public AbstractOrcRenderer(EntityRendererManager mgr, ArmsStyleModelProvider armsStyleModelProvider, OrcModel leggingsModel, OrcModel mainArmorModel, float shadowSize) {
		super(mgr, armsStyleModelProvider, leggingsModel, mainArmorModel, shadowSize);
	}

	public ResourceLocation getTextureLocation(Entity orc) {
		return SKINS.getRandomSkin(orc);
	}
}
