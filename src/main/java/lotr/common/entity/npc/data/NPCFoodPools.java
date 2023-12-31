package lotr.common.entity.npc.data;

import lotr.common.init.LOTRItems;
import lotr.common.item.VesselType;
import net.minecraft.item.Items;

public class NPCFoodPools {
	public static final NPCFoodPool HOBBIT;
	public static final NPCFoodPool HOBBIT_DRINK;
	public static final NPCFoodPool BREE;
	public static final NPCFoodPool BREE_DRINK;
	public static final NPCFoodPool ELF;
	public static final NPCFoodPool ELF_DRINK;
	public static final NPCFoodPool WOOD_ELF_DRINK;
	public static final NPCFoodPool DWARF;
	public static final NPCFoodPool BLUE_DWARF;
	public static final NPCFoodPool DWARF_DRINK;
	public static final NPCFoodPool DALE;
	public static final NPCFoodPool DALE_DRINK;
	public static final NPCFoodPool DUNLENDING;
	public static final NPCFoodPool DUNLENDING_DRINK;
	public static final NPCFoodPool ROHAN;
	public static final NPCFoodPool ROHAN_DRINK;
	public static final NPCFoodPool GONDOR;
	public static final NPCFoodPool GONDOR_DRINK;
	public static final NPCFoodPool ORC;
	public static final NPCFoodPool ORC_DRINK;
	public static final NPCFoodPool HARNEDHRIM;
	public static final NPCFoodPool HARNEDHRIM_DRINK;
	public static final NPCFoodPool COAST_SOUTHRON;
	public static final NPCFoodPool COAST_SOUTHRON_DRINK;

	static {
		HOBBIT = NPCFoodPool.of(Items.COOKED_PORKCHOP, Items.COOKED_SALMON, Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_RABBIT, LOTRItems.GAMMON, Items.BAKED_POTATO, Items.APPLE, LOTRItems.GREEN_APPLE, Items.BREAD, Items.CARROT, LOTRItems.LETTUCE, Items.MUSHROOM_STEW, Items.RABBIT_STEW, Items.BEETROOT, Items.BEETROOT_SOUP, Items.PUMPKIN_PIE, LOTRItems.PEAR, LOTRItems.CHERRY, Items.COOKIE);
		HOBBIT_DRINK = NPCFoodPool.of(LOTRItems.ALE, LOTRItems.ALE, LOTRItems.CIDER, LOTRItems.PERRY, LOTRItems.CHERRY_LIQUEUR, LOTRItems.APPLE_JUICE).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.CERAMIC_MUG, VesselType.WOODEN_CUP);
		BREE = NPCFoodPool.of(Items.COOKED_PORKCHOP, Items.COOKED_SALMON, Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_RABBIT, LOTRItems.GAMMON, Items.BAKED_POTATO, Items.APPLE, LOTRItems.GREEN_APPLE, Items.BREAD, Items.CARROT, LOTRItems.LETTUCE, Items.MUSHROOM_STEW, Items.RABBIT_STEW, Items.BEETROOT, Items.BEETROOT_SOUP, Items.PUMPKIN_PIE, LOTRItems.PEAR);
		BREE_DRINK = NPCFoodPool.of(LOTRItems.ALE, LOTRItems.ALE, LOTRItems.ALE, LOTRItems.CIDER, LOTRItems.CIDER, LOTRItems.PERRY, LOTRItems.MEAD, LOTRItems.APPLE_JUICE).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.CERAMIC_MUG, VesselType.COPPER_GOBLET, VesselType.WOODEN_CUP);
		ELF = NPCFoodPool.of(Items.BREAD, Items.APPLE, LOTRItems.GREEN_APPLE, LOTRItems.PEAR, LOTRItems.LETTUCE, Items.CARROT, Items.BEETROOT, Items.BEETROOT_SOUP, LOTRItems.LEMBAS, LOTRItems.LEMBAS, LOTRItems.LEMBAS, LOTRItems.LEMBAS, LOTRItems.LEMBAS, Items.COOKED_RABBIT, Items.COOKED_SALMON, Items.COOKED_CHICKEN);
		ELF_DRINK = NPCFoodPool.of(LOTRItems.MIRUVOR, LOTRItems.MIRUVOR, LOTRItems.MIRUVOR, LOTRItems.APPLE_JUICE).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.CERAMIC_MUG, VesselType.GOLDEN_GOBLET, VesselType.SILVER_GOBLET, VesselType.COPPER_GOBLET, VesselType.WOODEN_CUP);
		WOOD_ELF_DRINK = NPCFoodPool.of(LOTRItems.APPLE_JUICE).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.CERAMIC_MUG, VesselType.WOODEN_CUP);
		DWARF = NPCFoodPool.of(Items.COOKED_PORKCHOP, Items.COOKED_SALMON, Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_RABBIT, LOTRItems.GAMMON, Items.BREAD, LOTRItems.CRAM, LOTRItems.CRAM, LOTRItems.CRAM, LOTRItems.CRAM, Items.MUSHROOM_STEW);
		BLUE_DWARF = NPCFoodPool.of(Items.COOKED_PORKCHOP, Items.COOKED_SALMON, Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_RABBIT, LOTRItems.GAMMON, Items.BREAD, Items.MUSHROOM_STEW);
		DWARF_DRINK = NPCFoodPool.of(LOTRItems.DWARVEN_ALE, LOTRItems.DWARVEN_ALE, LOTRItems.DWARVEN_ALE, LOTRItems.DWARVEN_ALE, LOTRItems.DWARVEN_TONIC).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.CERAMIC_MUG, VesselType.GOLDEN_GOBLET, VesselType.SILVER_GOBLET, VesselType.COPPER_GOBLET, VesselType.WOODEN_CUP, VesselType.ALE_HORN);
		DALE = NPCFoodPool.of(Items.COOKED_PORKCHOP, Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_RABBIT, Items.BAKED_POTATO, Items.APPLE, LOTRItems.GREEN_APPLE, LOTRItems.PEAR, Items.CARROT, LOTRItems.LETTUCE, Items.BEETROOT, Items.BEETROOT_SOUP, Items.BREAD, LOTRItems.CRAM, LOTRItems.CRAM, LOTRItems.CRAM, LOTRItems.CRAM);
		DALE_DRINK = NPCFoodPool.of(LOTRItems.MEAD, LOTRItems.ALE, LOTRItems.CIDER, LOTRItems.PERRY, LOTRItems.VODKA, LOTRItems.DWARVEN_ALE, LOTRItems.APPLE_JUICE).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.CERAMIC_MUG, VesselType.GOLDEN_GOBLET, VesselType.SILVER_GOBLET, VesselType.COPPER_GOBLET, VesselType.WOODEN_CUP, VesselType.ALE_HORN);
		DUNLENDING = NPCFoodPool.of(Items.COOKED_PORKCHOP, Items.COOKED_SALMON, Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_RABBIT, LOTRItems.GAMMON, Items.BAKED_POTATO, Items.APPLE, LOTRItems.GREEN_APPLE, LOTRItems.PEAR, Items.CARROT, LOTRItems.LETTUCE, Items.BEETROOT, Items.BEETROOT_SOUP, Items.BREAD);
		DUNLENDING_DRINK = NPCFoodPool.of(LOTRItems.ALE, LOTRItems.ALE, LOTRItems.MEAD, LOTRItems.CIDER, LOTRItems.RUM, LOTRItems.APPLE_JUICE).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.WOODEN_CUP, VesselType.WATERSKIN, VesselType.ALE_HORN);
		ROHAN = NPCFoodPool.of(Items.COOKED_PORKCHOP, Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_RABBIT, Items.BAKED_POTATO, Items.APPLE, LOTRItems.GREEN_APPLE, LOTRItems.PEAR, Items.CARROT, LOTRItems.LETTUCE, Items.BEETROOT, Items.BEETROOT_SOUP, Items.BREAD);
		ROHAN_DRINK = NPCFoodPool.of(LOTRItems.MEAD, LOTRItems.MEAD, LOTRItems.MEAD, LOTRItems.ALE, LOTRItems.CIDER, LOTRItems.PERRY, LOTRItems.APPLE_JUICE).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.CERAMIC_MUG, VesselType.GOLDEN_GOBLET, VesselType.SILVER_GOBLET, VesselType.COPPER_GOBLET, VesselType.WOODEN_CUP, VesselType.ALE_HORN, VesselType.GOLDEN_ALE_HORN);
		GONDOR = NPCFoodPool.of(Items.COOKED_PORKCHOP, Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKED_MUTTON, Items.BAKED_POTATO, Items.APPLE, LOTRItems.GREEN_APPLE, LOTRItems.PEAR, Items.CARROT, LOTRItems.LETTUCE, Items.BEETROOT, Items.BEETROOT_SOUP, Items.BREAD);
		GONDOR_DRINK = NPCFoodPool.of(LOTRItems.ALE, LOTRItems.ALE, LOTRItems.ALE, LOTRItems.MEAD, LOTRItems.CIDER, LOTRItems.PERRY, LOTRItems.APPLE_JUICE).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.CERAMIC_MUG, VesselType.GOLDEN_GOBLET, VesselType.SILVER_GOBLET, VesselType.COPPER_GOBLET, VesselType.WOODEN_CUP, VesselType.ALE_HORN);
		ORC = NPCFoodPool.of(LOTRItems.MAGGOTY_BREAD, LOTRItems.MAGGOTY_BREAD, Items.BREAD, Items.COOKED_RABBIT, Items.COOKED_MUTTON, Items.MUSHROOM_STEW);
		ORC_DRINK = NPCFoodPool.of(LOTRItems.ORC_DRAUGHT).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.WOODEN_CUP, VesselType.WATERSKIN);
		HARNEDHRIM = NPCFoodPool.of(Items.COOKED_PORKCHOP, Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKED_MUTTON, Items.BAKED_POTATO, Items.APPLE, LOTRItems.GREEN_APPLE, LOTRItems.PEAR, Items.CARROT, LOTRItems.LETTUCE, Items.BREAD);
		HARNEDHRIM_DRINK = NPCFoodPool.of(LOTRItems.WATER_DRINK, LOTRItems.ALE, LOTRItems.CIDER).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.CERAMIC_MUG, VesselType.COPPER_GOBLET, VesselType.WOODEN_CUP, VesselType.WATERSKIN);
		COAST_SOUTHRON = NPCFoodPool.of(Items.COOKED_PORKCHOP, Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKED_MUTTON, Items.BAKED_POTATO, Items.APPLE, LOTRItems.GREEN_APPLE, LOTRItems.PEAR, Items.CARROT, LOTRItems.LETTUCE, Items.BREAD);
		COAST_SOUTHRON_DRINK = NPCFoodPool.of(LOTRItems.WATER_DRINK, LOTRItems.ALE, LOTRItems.CIDER).setDrinkVessels(VesselType.WOODEN_MUG, VesselType.CERAMIC_MUG, VesselType.GOLDEN_GOBLET, VesselType.COPPER_GOBLET, VesselType.WOODEN_CUP, VesselType.WATERSKIN);
	}
}
