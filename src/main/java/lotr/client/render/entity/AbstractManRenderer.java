package lotr.client.render.entity;

import lotr.client.render.entity.model.ManModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public abstract class AbstractManRenderer extends LOTRBipedRenderer {
	public AbstractManRenderer(EntityRendererManager mgr) {
		super(mgr, ManModel::new, new ManModel(0.5F), new ManModel(1.0F), 0.5F);
	}
}
