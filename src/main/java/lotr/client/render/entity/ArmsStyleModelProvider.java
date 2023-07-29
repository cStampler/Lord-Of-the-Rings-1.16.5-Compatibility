package lotr.client.render.entity;

import lotr.client.render.entity.model.LOTRBipedModel;

@FunctionalInterface
public interface ArmsStyleModelProvider {
	LOTRBipedModel getModelForArmsStyle(boolean var1);
}
