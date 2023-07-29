package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.*;

public class UnderwaterSpongeFeature extends Feature {
	public UnderwaterSpongeFeature(Codec configFactory) {
		super(configFactory);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		BlockClusterFeatureConfig config = (BlockClusterFeatureConfig) confi;
		BlockState placeState = config.stateProvider.getState(rand, pos);
		BlockPos placePos;
		if (config.project) {
			placePos = world.getHeightmapPos(Type.WORLD_SURFACE_WG, pos);
		} else {
			placePos = pos;
		}

		int placed = 0;
		Mutable movingPos = new Mutable();

		for (int i = 0; i < config.tries; ++i) {
			movingPos.set(placePos).move(rand.nextInt(config.xspread + 1) - rand.nextInt(config.xspread + 1), rand.nextInt(config.yspread + 1) - rand.nextInt(config.yspread + 1), rand.nextInt(config.zspread + 1) - rand.nextInt(config.zspread + 1));
			BlockPos belowPos = movingPos.below();
			BlockPos abovePos = movingPos.above();
			BlockState belowState = world.getBlockState(belowPos);
			BlockState aboveState = world.getBlockState(abovePos);
			if (world.getBlockState(movingPos).getMaterial() == Material.WATER && aboveState.getMaterial() == Material.WATER && placeState.canSurvive(world, movingPos) && (config.whitelist.isEmpty() || config.whitelist.contains(belowState.getBlock())) && !config.blacklist.contains(belowState)) {
				config.blockPlacer.place(world, movingPos, placeState, rand);
				++placed;
			}
		}

		return placed > 0;
	}
}
