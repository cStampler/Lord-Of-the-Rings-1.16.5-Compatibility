package lotr.client.render.entity.model;

public class ManModel extends LOTRBipedModel {
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
