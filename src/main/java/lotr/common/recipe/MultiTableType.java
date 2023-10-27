package lotr.common.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class MultiTableType implements FactionBasedRecipeType {
	private static final Random rand = new Random();
	public final ResourceLocation recipeTypeName;
	private final List tableTypes;
	private FactionTableType randTableType;
	private long lastRandomTime;

	public MultiTableType(ResourceLocation name, List types) {
		recipeTypeName = name;
		tableTypes = new ArrayList(types);
		Iterator var3 = types.iterator();

		while (var3.hasNext()) {
			FactionTableType facType = (FactionTableType) var3.next();
			facType.registerMultiTableType(this);
		}

	}

	@Override
	public ItemStack getFactionTableIcon() {
		if (randTableType == null || System.currentTimeMillis() - lastRandomTime > 1000L) {
			randTableType = (FactionTableType) tableTypes.get(rand.nextInt(tableTypes.size()));
			lastRandomTime = System.currentTimeMillis();
		}

		return randTableType.getFactionTableIcon();
	}

	public boolean includesFactionType(FactionTableType type) {
		return tableTypes.contains(type);
	}

	@Override
	public String toString() {
		return recipeTypeName.toString();
	}
}
