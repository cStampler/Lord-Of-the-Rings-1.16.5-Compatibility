package lotr.common.block;

import java.util.*;
import java.util.function.Consumer;

import net.minecraft.block.WoodType;

public class LOTRSignTypes {
	private static final List allModTypes = new ArrayList();
	public static final WoodType PINE = register("pine");
	public static final WoodType MALLORN = register("mallorn");
	public static final WoodType MIRK_OAK = register("mirk_oak");
	public static final WoodType CHARRED = register("charred");
	public static final WoodType APPLE = register("apple");
	public static final WoodType PEAR = register("pear");
	public static final WoodType CHERRY = register("cherry");
	public static final WoodType LEBETHRON = register("lebethron");
	public static final WoodType BEECH = register("beech");
	public static final WoodType MAPLE = register("maple");
	public static final WoodType ASPEN = register("aspen");
	public static final WoodType LAIRELOSSE = register("lairelosse");
	public static final WoodType CEDAR = register("cedar");
	public static final WoodType FIR = register("fir");
	public static final WoodType LARCH = register("larch");
	public static final WoodType HOLLY = register("holly");
	public static final WoodType GREEN_OAK = register("green_oak");
	public static final WoodType CYPRESS = register("cypress");
	public static final WoodType ROTTEN = register("rotten");
	public static final WoodType CULUMALDA = register("culumalda");

	private static final String createFullName(String s) {
		return "lotr/" + s;
	}

	public static void forEach(Consumer action) {
		allModTypes.forEach(action);
	}

	private static WoodType register(String name) {
		WoodType type = WoodType.create(createFullName(name));
		WoodType.register(type);
		allModTypes.add(type);
		return type;
	}
}
