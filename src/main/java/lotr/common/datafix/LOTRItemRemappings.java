package lotr.common.datafix;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.init.LOTRBiomes;
import lotr.common.init.LOTRBlocks;
import lotr.common.init.LOTRItems;
import lotr.common.init.LOTRSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class LOTRItemRemappings {
	private static final Map<ResourceLocation, Block> BLOCK_REMAPS = new HashMap<>();
	private static final Map<ResourceLocation, Item> ITEM_REMAPS = new HashMap<>();
	private static final Map<ResourceLocation, Biome> BIOME_REMAPS = new HashMap<>();
	private static final Map<ResourceLocation, SoundEvent> SOUND_REMAPS = new HashMap<>();
	private static boolean init = false;

	private static void addBiomeRemap(String oldName, Supplier<? extends Biome> newBiome) {
		BIOME_REMAPS.put(new ResourceLocation("lotr", oldName), newBiome.get());
	}

	private static void addBlockRemap(String oldName, Supplier<? extends Block> newBlock) {
		BLOCK_REMAPS.put(new ResourceLocation("lotr", oldName), newBlock.get());
	}

	private static void addBlockRemap(String oldName, Supplier<? extends Block> newBlock, Supplier<? extends Item> newItem) {
		addBlockRemap(oldName, newBlock);
		addItemRemap(oldName, newItem);
	}

	private static void addItemRemap(String oldName, Supplier<? extends Item> newItem) {
		ITEM_REMAPS.put(new ResourceLocation("lotr", oldName), newItem.get());
	}

	private static void addSoundRemap(String oldName, Supplier<? extends SoundEvent> newSound) {
		SOUND_REMAPS.put(new ResourceLocation("lotr", oldName), newSound.get());
	}

	@SuppressWarnings("unchecked")
	public static <T extends IForgeRegistryEntry<T>> void handle(MissingMappings<T> event) {
	    for (Mapping<T> mapping : event.getAllMappings()) {
	      ResourceLocation itemName = mapping.key;
	      if (itemName.getNamespace().equals("lotr")) {
	        if (!init)
	          init(); 
	        if (event.getRegistry().getRegistrySuperType() == Block.class) {
	          if (BLOCK_REMAPS.containsKey(itemName)) {
	            Block block = BLOCK_REMAPS.get(itemName);
	            mapping.remap((T)block);
	            LOTRLog.info("Remapped old block id %s to new id %s", new Object[] { itemName, block.getRegistryName() });
	          } 
	          continue;
	        } 
	        if (event.getRegistry().getRegistrySuperType() == Item.class) {
	          if (ITEM_REMAPS.containsKey(itemName)) {
	            Item item = ITEM_REMAPS.get(itemName);
	            mapping.remap((T)item);
	            LOTRLog.info("Remapped old item id %s to new id %s", new Object[] { itemName, item.getRegistryName() });
	          } 
	          continue;
	        } 
	        if (event.getRegistry().getRegistrySuperType() == Biome.class) {
	          if (BIOME_REMAPS.containsKey(itemName)) {
	            Biome biome = BIOME_REMAPS.get(itemName);
	            mapping.remap((T)biome);
	            LOTRLog.info("Remapped old biome id %s to new id %s", new Object[] { itemName, biome.getRegistryName() });
	          } 
	          continue;
	        } 
	        if (event.getRegistry().getRegistrySuperType() == SoundEvent.class)
	          if (SOUND_REMAPS.containsKey(itemName)) {
	            SoundEvent sound = SOUND_REMAPS.get(itemName);
	            mapping.remap((T)sound);
	            LOTRLog.info("Remapped old sound id %s to new id %s", new Object[] { itemName, sound.getRegistryName() });
	          }  
	      } 
	    }
	}

	private static void init() {
		init = true;
		addBlockRemap("shire_pine_log", LOTRBlocks.PINE_LOG, LOTRItems.PINE_LOG);
		addBlockRemap("shire_pine_wood", LOTRBlocks.PINE_WOOD, LOTRItems.PINE_WOOD);
		addBlockRemap("shire_pine_planks", LOTRBlocks.PINE_PLANKS, LOTRItems.PINE_PLANKS);
		addBlockRemap("shire_pine_leaves", LOTRBlocks.PINE_LEAVES, LOTRItems.PINE_LEAVES);
		addBlockRemap("shire_pine_sapling", LOTRBlocks.PINE_SAPLING, LOTRItems.PINE_SAPLING);
		addBlockRemap("potted_shire_pine_sapling", LOTRBlocks.POTTED_PINE_SAPLING);
		addBlockRemap("shire_pine_slab", LOTRBlocks.PINE_SLAB, LOTRItems.PINE_SLAB);
		addBlockRemap("shire_pine_stairs", LOTRBlocks.PINE_STAIRS, LOTRItems.PINE_STAIRS);
		addBlockRemap("shire_pine_fence", LOTRBlocks.PINE_FENCE, LOTRItems.PINE_FENCE);
		addBlockRemap("shire_pine_fence_gate", LOTRBlocks.PINE_FENCE_GATE, LOTRItems.PINE_FENCE_GATE);
		addBlockRemap("shire_pine_pressure_plate", LOTRBlocks.PINE_PRESSURE_PLATE, LOTRItems.PINE_PRESSURE_PLATE);
		addBlockRemap("shire_pine_button", LOTRBlocks.PINE_BUTTON, LOTRItems.PINE_BUTTON);
		addBlockRemap("stripped_shire_pine_log", LOTRBlocks.STRIPPED_PINE_LOG, LOTRItems.STRIPPED_PINE_LOG);
		addBlockRemap("stripped_shire_pine_wood", LOTRBlocks.STRIPPED_PINE_WOOD, LOTRItems.STRIPPED_PINE_WOOD);
		addBlockRemap("shire_pine_beam", LOTRBlocks.PINE_BEAM, LOTRItems.PINE_BEAM);
		addBlockRemap("shire_pine_sign", LOTRBlocks.PINE_SIGN, LOTRItems.PINE_SIGN);
		addBlockRemap("shire_pine_wall_sign", LOTRBlocks.PINE_WALL_SIGN);
		addBlockRemap("durnaur_ore", LOTRBlocks.DURNOR_ORE, LOTRItems.DURNOR_ORE);
		addBlockRemap("durnaur_block", LOTRBlocks.DURNOR_BLOCK, LOTRItems.DURNOR_BLOCK);
		addBlockRemap("speleothem", LOTRBlocks.DRIPSTONE, LOTRItems.DRIPSTONE);
		addBlockRemap("mordor_speleothem", LOTRBlocks.MORDOR_DRIPSTONE, LOTRItems.MORDOR_DRIPSTONE);
		addBlockRemap("obsidian_speleothem", LOTRBlocks.OBSIDIAN_DRIPSTONE, LOTRItems.OBSIDIAN_DRIPSTONE);
		addBlockRemap("ice_speleothem", LOTRBlocks.ICE_DRIPSTONE, LOTRItems.ICE_DRIPSTONE);
		addBlockRemap("gondor_speleothem", LOTRBlocks.GONDOR_DRIPSTONE, LOTRItems.GONDOR_DRIPSTONE);
		addBlockRemap("rohan_speleothem", LOTRBlocks.ROHAN_DRIPSTONE, LOTRItems.ROHAN_DRIPSTONE);
		addBlockRemap("blue_speleothem", LOTRBlocks.BLUE_DRIPSTONE, LOTRItems.BLUE_DRIPSTONE);
		addBlockRemap("red_speleothem", LOTRBlocks.RED_DRIPSTONE, LOTRItems.RED_DRIPSTONE);
		addBlockRemap("andesite_speleothem", LOTRBlocks.ANDESITE_DRIPSTONE, LOTRItems.ANDESITE_DRIPSTONE);
		addBlockRemap("diorite_speleothem", LOTRBlocks.DIORITE_DRIPSTONE, LOTRItems.DIORITE_DRIPSTONE);
		addBlockRemap("granite_speleothem", LOTRBlocks.GRANITE_DRIPSTONE, LOTRItems.GRANITE_DRIPSTONE);
		addBlockRemap("sandstone_speleothem", LOTRBlocks.SANDSTONE_DRIPSTONE, LOTRItems.SANDSTONE_DRIPSTONE);
		addBlockRemap("red_sandstone_speleothem", LOTRBlocks.RED_SANDSTONE_DRIPSTONE, LOTRItems.RED_SANDSTONE_DRIPSTONE);
		addItemRemap("durnaur", LOTRItems.DURNOR);
		addBiomeRemap("gondor", LOTRBiomes.ANORIEN.supplyInitialisedBiome());
		addBiomeRemap("western_gondor", LOTRBiomes.FURTHER_GONDOR.supplyInitialisedBiome());
		addSoundRemap("block.plate.break", () -> LOTRSoundEvents.CERAMIC_BREAK);
	}
}
