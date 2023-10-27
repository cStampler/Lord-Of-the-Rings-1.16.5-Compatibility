package lotr.client.render.entity;

import lotr.client.render.entity.layers.CaracalCollarLayer;
import lotr.client.render.entity.layers.CaracalHeldItemLayer;
import lotr.client.render.entity.model.CaracalModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CaracalRenderer extends MobRenderer {
	private static final ResourceLocation CARACAL_TEXTURE = new ResourceLocation("lotr", "textures/entity/caracal/caracal.png");

	public CaracalRenderer(EntityRendererManager mgr) {
		super(mgr, new CaracalModel(0.0F), 0.4F);
		addLayer(new CaracalCollarLayer(this));
		addLayer(new CaracalHeldItemLayer(this));
	}

	@Override
	public ResourceLocation getTextureLocation(Entity caracal) {
		return CARACAL_TEXTURE;
	}
}
