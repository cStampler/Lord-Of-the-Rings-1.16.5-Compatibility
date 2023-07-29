package lotr.client.render;

import lotr.common.entity.npc.NPCEntity;
import net.minecraft.util.ResourceLocation;

public class GenderedRandomTextureVariants {
	private final RandomTextureVariants maleSkins;
	private final RandomTextureVariants femaleSkins;

	public GenderedRandomTextureVariants(ResourceLocation res) {
		this(res.getNamespace(), res.getPath());
	}

	public GenderedRandomTextureVariants(String namespace, String path) {
		maleSkins = RandomTextureVariants.loadSkinsList(namespace, path + "_male");
		femaleSkins = RandomTextureVariants.loadSkinsList(namespace, path + "_female");
	}

	public ResourceLocation getRandomSkin(NPCEntity npc) {
		return npc.getPersonalInfo().isMale() ? maleSkins.getRandomSkin(npc) : femaleSkins.getRandomSkin(npc);
	}
}
