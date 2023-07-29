package lotr.common.fac;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;

public class AlignmentBonus {
	public static final AlignmentBonus MARRIAGE_BONUS = createSimpleBonus(5.0F, "lotr.alignment.marriage");
	public static final AlignmentBonus FANGORN_TREE_PENALTY = createSimpleBonus(-1.0F, "lotr.alignment.cutFangornTree");
	public static final AlignmentBonus ROHAN_HORSE_PENALTY = createSimpleBonus(-1.0F, "lotr.alignment.killRohanHorse");
	public static final AlignmentBonus VINEYARD_STEAL_PENALTY = createSimpleBonus(-1.0F, "lotr.alignment.vineyardSteal");
	public static final AlignmentBonus PICKPOCKET_PENALTY = createSimpleBonus(-1.0F, "lotr.alignment.pickpocket");
	public final float bonus;
	public final ITextComponent name;
	public final boolean isKill;
	public final boolean isKillByHiredUnit;
	public final boolean isCivilianKill;

	private AlignmentBonus(float bonus, ITextComponent name, boolean isKill, boolean isKillByHiredUnit, boolean isCivilianKill) {
		this.bonus = bonus;
		this.name = name;
		this.isKill = isKill;
		this.isKillByHiredUnit = isKillByHiredUnit;
		this.isCivilianKill = isCivilianKill;
	}

	public void write(PacketBuffer buf) {
		buf.writeFloat(bonus);
		buf.writeComponent(name);
		buf.writeBoolean(isKill);
		buf.writeBoolean(isKillByHiredUnit);
		buf.writeBoolean(isCivilianKill);
	}

	public static AlignmentBonus createMiniquestBonus(float bonus) {
		return createSimpleBonus(bonus, "lotr.alignment.miniQuest");
	}

	public static AlignmentBonus createPledgePenalty(float bonus) {
		return createSimpleBonus(bonus, "lotr.alignment.break_pledge");
	}

	public static AlignmentBonus createSimpleBonus(float bonus, String name) {
		return new AlignmentBonus(bonus, new TranslationTextComponent(name), false, false, false);
	}

	public static AlignmentBonus forEntityKill(float bonus, ITextComponent name, boolean byHiredUnit, boolean civilian) {
		return new AlignmentBonus(bonus, name, true, byHiredUnit, civilian);
	}

	public static AlignmentBonus read(PacketBuffer buf) {
		return new AlignmentBonus(buf.readFloat(), buf.readComponent(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
	}

	public static float scaleKillPenalty(float penalty, float alignment) {
		if (alignment > 0.0F && penalty < 0.0F) {
			float factor = alignment / 50.0F;
			factor = MathHelper.clamp(factor, 1.0F, 20.0F);
			penalty *= factor;
		}

		return penalty;
	}
}
