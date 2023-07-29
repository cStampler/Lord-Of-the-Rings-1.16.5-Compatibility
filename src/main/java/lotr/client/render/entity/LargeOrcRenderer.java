package lotr.client.render.entity;

import lotr.client.render.entity.model.OrcModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class LargeOrcRenderer extends AbstractOrcRenderer {
	public LargeOrcRenderer(EntityRendererManager mgr) {
		super(mgr, OrcModel::new, new OrcModel(0.5F), new OrcModel(1.0F), 0.5F);
	}
}
