package lotr.common.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LOTRPottedPlantBlock extends FlowerPotBlock {
	public LOTRPottedPlantBlock(Supplier plant) {
		super(() -> ((FlowerPotBlock) Blocks.FLOWER_POT), plant, Properties.of(Material.DECORATION).strength(0.0F).noOcclusion());
		registerPottedPlant((Block) plant.get(), this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		getContent().animateTick(getContent().defaultBlockState(), world, pos, rand);
	}

	private static void registerPottedPlant(Block plant, Block potted) {
		FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
		pot.addPlant(plant.getRegistryName(), () -> potted);
	}
}
