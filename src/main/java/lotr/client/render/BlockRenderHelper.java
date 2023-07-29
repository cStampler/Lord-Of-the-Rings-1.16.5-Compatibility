package lotr.client.render;

import java.util.Iterator;

import lotr.common.block.*;
import lotr.common.init.*;
import lotr.common.item.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.color.*;
import net.minecraft.item.*;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.client.event.ColorHandlerEvent.Item;
import net.minecraftforge.fml.RegistryObject;

public class BlockRenderHelper {
	private static IBlockColor createFallenLeavesColorizer(BlockColors colors, Block baseLeafBlock) {
		return (state, lightReader, pos, tintIndex) -> {
			int baseColor = colors.getColor(baseLeafBlock.defaultBlockState(), lightReader, pos, tintIndex);
			int r = baseColor >> 16 & 255;
			int g = baseColor >> 8 & 255;
			int b = baseColor & 255;
			r = (int) (r * 0.75F);
			g = (int) (g * 0.75F);
			b = (int) (b * 0.75F);
			return r << 16 | g << 8 | b;
		};
	}

	public static void setupBlockColors(net.minecraftforge.client.event.ColorHandlerEvent.Block event) {
		BlockColors colors = event.getBlockColors();
		IBlockColor cloverColorizer = (state, lightReader, pos, tintIndex) -> (lightReader != null && pos != null ? BiomeColors.getAverageGrassColor(lightReader, pos) : GrassColors.get(1.0D, 1.0D));
		colors.register(cloverColorizer, (Block) LOTRBlocks.CLOVER.get(), (Block) LOTRBlocks.FOUR_LEAF_CLOVER.get(), (Block) LOTRBlocks.POTTED_CLOVER.get(), (Block) LOTRBlocks.POTTED_FOUR_LEAF_CLOVER.get());
		IBlockColor grassColorizer = (state, lightReader, pos, tintIndex) -> {
			if (tintIndex != 0) {
				return -1;
			}
			return lightReader != null && pos != null ? BiomeColors.getAverageGrassColor(lightReader, pos) : GrassColors.get(0.5D, 1.0D);
		};
		colors.register(grassColorizer, (Block) LOTRBlocks.SHORT_GRASS.get(), (Block) LOTRBlocks.WHEATGRASS.get(), (Block) LOTRBlocks.FLOWERY_GRASS.get(), (Block) LOTRBlocks.THISTLE.get(), (Block) LOTRBlocks.NETTLES.get());
		colors.register(grassColorizer, (Block) LOTRBlocks.PURPLE_MOOR_GRASS.get(), (Block) LOTRBlocks.RED_MOOR_GRASS.get());
		colors.register(grassColorizer, (Block) LOTRBlocks.POTTED_THISTLE.get(), (Block) LOTRBlocks.POTTED_NETTLES.get());
		colors.register(grassColorizer, (Block) LOTRBlocks.TALL_WHEATGRASS.get(), (Block) LOTRBlocks.RUSHES.get());
		colors.register(grassColorizer, (Block) LOTRBlocks.FERNSPROUT.get(), (Block) LOTRBlocks.POTTED_FERNSPROUT.get());
		IBlockColor waterLilyColorizer = (state, lightReader, pos, tintIndex) -> colors.getColor(Blocks.LILY_PAD.defaultBlockState(), lightReader, pos, tintIndex);
		colors.register(waterLilyColorizer, (Block) LOTRBlocks.WHITE_WATER_LILY.get(), (Block) LOTRBlocks.YELLOW_WATER_LILY.get(), (Block) LOTRBlocks.PURPLE_WATER_LILY.get(), (Block) LOTRBlocks.PINK_WATER_LILY.get());
		FallenLeavesBlock.ALL_FALLEN_LEAVES.forEach(fallenLeaves -> {
			colors.register(createFallenLeavesColorizer(colors, ((FallenLeavesBlock) fallenLeaves).getBaseLeafBlock()), (Block) fallenLeaves);
		});
	}

	public static void setupBlocks() {
		Iterator var0 = LOTRBlocks.BLOCKS.getEntries().iterator();

		while (true) {
			while (true) {
				while (var0.hasNext()) {
					RegistryObject regBlock = (RegistryObject) var0.next();
					Block block = (Block) regBlock.get();
					if (!(block instanceof FlowerBlock) && !(block instanceof FlowerLikeBlock) && !(block instanceof FlowerPotBlock) && !(block instanceof SaplingBlock) && !(block instanceof CloverBlock) && !(block instanceof LOTRGrassBlock) && !(block instanceof MushroomBlock)) {
						if (block instanceof DoublePlantBlock || block instanceof CropsBlock) {
							RenderTypeLookup.setRenderLayer(block, RenderType.cutout());
						} else if (block instanceof LeavesBlock || block instanceof DoorBlock || block instanceof TrapDoorBlock || block instanceof LadderBlock) {
							RenderTypeLookup.setRenderLayer(block, RenderType.cutoutMipped());
						} else if (!(block instanceof MordorMossBlock) && !(block instanceof MordorGrassBlock) && !(block instanceof MordorThornBlock)) {
							if (block instanceof DoubleTorchBlock || block instanceof TorchBlock || block instanceof ChandelierBlock) {
								RenderTypeLookup.setRenderLayer(block, RenderType.cutout());
							} else if (block instanceof LOTRBarsBlock) {
								RenderTypeLookup.setRenderLayer(block, RenderType.cutoutMipped());
							} else if (!(block instanceof CrystalBlock) && !(block instanceof TranslucentMineralBlock)) {
								if (!(block instanceof ThatchBlock) && !(block instanceof ThatchSlabBlock) && !(block instanceof ThatchStairsBlock)) {
									if (block instanceof HangingWebBlock || block instanceof LOTRLanternBlock || block instanceof NonWaterloggableLanternBlock) {
										RenderTypeLookup.setRenderLayer(block, RenderType.cutout());
									} else if (block instanceof AbstractGlassBlock) {
										if (block instanceof LOTRStainedGlassBlock) {
											RenderTypeLookup.setRenderLayer(block, RenderType.translucent());
										} else {
											RenderTypeLookup.setRenderLayer(block, RenderType.cutout());
										}
									} else if (block instanceof LOTRGlassPaneBlock) {
										RenderTypeLookup.setRenderLayer(block, RenderType.cutoutMipped());
									} else if (block instanceof LOTRStainedGlassPaneBlock) {
										RenderTypeLookup.setRenderLayer(block, RenderType.translucent());
									} else if (block instanceof ReedsBlock || block instanceof LOTRWaterLilyBlock || block instanceof GondorBeaconBlock) {
										RenderTypeLookup.setRenderLayer(block, RenderType.cutout());
									} else if (block instanceof FallenLeavesBlock || block instanceof ThatchFloorBlock || block instanceof GateBlock || block instanceof WickerFenceBlock) {
										RenderTypeLookup.setRenderLayer(block, RenderType.cutoutMipped());
									} else if (block instanceof PalantirBlock) {
										RenderTypeLookup.setRenderLayer(block, RenderType.translucent());
									}
								} else {
									RenderTypeLookup.setRenderLayer(block, RenderType.cutoutMipped());
								}
							} else {
								RenderTypeLookup.setRenderLayer(block, RenderType.translucent());
							}
						} else {
							RenderTypeLookup.setRenderLayer(block, RenderType.cutout());
						}
					} else {
						RenderTypeLookup.setRenderLayer(block, RenderType.cutout());
					}
				}

				return;
			}
		}
	}

	public static void setupItemColors(Item event) {
		ItemColors colors = event.getItemColors();
		BlockColors blockColors = event.getBlockColors();
		IItemColor baseBlockColorizer = (itemstack, tintIndex) -> {
			BlockState state = ((BlockItem) itemstack.getItem()).getBlock().defaultBlockState();
			return blockColors.getColor(state, (IBlockDisplayReader) null, (BlockPos) null, tintIndex);
		};
		colors.register(baseBlockColorizer, (IItemProvider) LOTRBlocks.CLOVER.get(), (IItemProvider) LOTRBlocks.FOUR_LEAF_CLOVER.get());
		colors.register(baseBlockColorizer, (IItemProvider) LOTRBlocks.SHORT_GRASS.get(), (IItemProvider) LOTRBlocks.WHEATGRASS.get(), (IItemProvider) LOTRBlocks.FLOWERY_GRASS.get(), (IItemProvider) LOTRBlocks.THISTLE.get(), (IItemProvider) LOTRBlocks.NETTLES.get());
		colors.register(baseBlockColorizer, (IItemProvider) LOTRBlocks.PURPLE_MOOR_GRASS.get(), (IItemProvider) LOTRBlocks.RED_MOOR_GRASS.get());
		colors.register(baseBlockColorizer, (IItemProvider) LOTRBlocks.TALL_WHEATGRASS.get(), (IItemProvider) LOTRBlocks.RUSHES.get());
		IItemColor fernsproutColorizer = (itemstack, tintIndex) -> {
			if (tintIndex == 0) {
				return GrassColors.get(0.95D, 0.9D);
			}
			BlockState state = ((BlockItem) itemstack.getItem()).getBlock().defaultBlockState();
			return blockColors.getColor(state, (IBlockDisplayReader) null, (BlockPos) null, tintIndex);
		};
		colors.register(fernsproutColorizer, (IItemProvider) LOTRBlocks.FERNSPROUT.get());
		FallenLeavesBlock.ALL_FALLEN_LEAVES.forEach(fallenLeaves -> {
			colors.register(baseBlockColorizer, (IItemProvider) fallenLeaves);
		});
		LOTRSpawnEggItem.ALL_MOD_SPAWN_EGGS.forEach(spawnEgg -> {
			colors.register((stack, tintIndex) -> ((SpawnEggItem) spawnEgg).getColor(tintIndex), (IItemProvider) spawnEgg);
		});
		IItemColor pouchColorizer = (stack, tintIndex) -> (tintIndex == 0 ? PouchItem.getPouchColor(stack) : 16777215);
		PouchItem.ALL_POUCH_ITEMS.forEach(pouch -> {
			colors.register(pouchColorizer, (IItemProvider) pouch);
		});
	}
}
