package lotr.client.render.entity;

import lotr.client.render.entity.model.LOTRBipedModel;
import lotr.common.entity.npc.NPCEntity;

@FunctionalInterface
public interface ArmsStyleModelProvider<E extends NPCEntity, M extends LOTRBipedModel<E>> {
	M getModelForArmsStyle(boolean var1);
}
