package lotr.client.render.entity;

import lotr.client.render.GenderedRandomTextureVariants;
import lotr.common.entity.npc.NPCEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class BlueDwarfRenderer extends AbstractDwarfRenderer {
	private static final GenderedRandomTextureVariants SKINS = new GenderedRandomTextureVariants("lotr", "textures/entity/dwarf/blue_mountains");

	public BlueDwarfRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	public ResourceLocation getTextureLocation(Entity dwarf) {
		return SKINS.getRandomSkin((NPCEntity) dwarf);
	}
}
