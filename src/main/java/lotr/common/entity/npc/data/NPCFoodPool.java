package lotr.common.entity.npc.data;

import java.util.*;
import java.util.stream.Collectors;

import lotr.common.item.*;
import lotr.common.tileentity.PlateTileEntity;
import net.minecraft.item.*;

public class NPCFoodPool extends SuppliableItemTable {
	private List drinkVessels;
	private List drinkVesselsPlaceable;

	private NPCFoodPool(Object... items) {
		super(items);
	}

	public List getDrinkVessels() {
		return drinkVessels;
	}

	public List getPlaceableDrinkVessels() {
		return drinkVesselsPlaceable;
	}

	public ItemStack getRandomFood(Random random) {
		ItemStack food = this.getRandomItem(random);
		setDrinkVessel(food, random, false);
		return food;
	}

	public ItemStack getRandomFoodForPlate(Random random) {
		return this.getRandomItem(random, hummel -> PlateTileEntity.isValidFoodItem((ItemStack) hummel));
	}

	public ItemStack getRandomPlaceableDrink(Random random) {
		ItemStack food = this.getRandomItem(random);
		setDrinkVessel(food, random, true);
		return food;
	}

	public VesselType getRandomPlaceableVessel(Random random) {
		return (VesselType) drinkVesselsPlaceable.get(random.nextInt(drinkVesselsPlaceable.size()));
	}

	public VesselType getRandomVessel(Random random) {
		return (VesselType) drinkVessels.get(random.nextInt(drinkVessels.size()));
	}

	private void setDrinkVessel(ItemStack itemstack, Random random, boolean requirePlaceable) {
		Item item = itemstack.getItem();
		if (item instanceof VesselDrinkItem) {
			VesselType vessel = requirePlaceable ? getRandomPlaceableVessel(random) : getRandomVessel(random);
			VesselDrinkItem.setVessel(itemstack, vessel);
		}

	}

	protected NPCFoodPool setDrinkVessels(VesselType... vessels) {
		if (drinkVessels != null) {
			throw new IllegalStateException("drinkVessels already set!");
		}
		drinkVessels = Arrays.asList(vessels);
		drinkVesselsPlaceable = (List) drinkVessels.stream().filter(hummel -> ((VesselType) hummel).isPlaceable()).collect(Collectors.toList());
		if (drinkVesselsPlaceable.isEmpty()) {
			drinkVesselsPlaceable = Arrays.asList(VesselType.WOODEN_MUG);
		}

		return this;
	}

	public static NPCFoodPool of(Object... items) {
		return new NPCFoodPool(items);
	}
}
