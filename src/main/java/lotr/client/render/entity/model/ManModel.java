package lotr.client.render.entity.model;

import lotr.common.entity.npc.AbstractMannishEntity;

public class ManModel<E extends AbstractMannishEntity> extends LOTRBipedModel<E> {
	public ManModel(boolean smallArms) {
		this(0.0F, false, smallArms);
	}

	public ManModel(float f) {
		this(f, true, false);
	}

	public ManModel(float f, boolean isArmor, boolean smallArms) {
		super(f, 0.0F, isArmor, smallArms);
		if (!isArmor) {
			createLongHairModel(0.0F, f);
		}

	}
}
