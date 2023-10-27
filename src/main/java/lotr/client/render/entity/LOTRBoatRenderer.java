package lotr.client.render.entity;

import java.util.HashMap;
import java.util.Map;

import lotr.common.entity.item.LOTRBoatEntity;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.ResourceLocation;

public class LOTRBoatRenderer extends BoatRenderer {
	private final Map boatTextures = new HashMap();

	public LOTRBoatRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	@Override
	public ResourceLocation getTextureLocation(BoatEntity boat) {
		LOTRBoatEntity.ModBoatType type = ((LOTRBoatEntity) boat).getModBoatType();
		ResourceLocation res = (ResourceLocation) boatTextures.get(type);
		if (res == null) {
			res = new ResourceLocation("lotr", String.format("textures/entity/boat/%s.png", type.getName()));
			boatTextures.put(type, res);
		}

		return res;
	}
}
