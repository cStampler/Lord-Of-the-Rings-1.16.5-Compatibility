package lotr.common.world.gen.placement;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

public class AtSurfaceLayerLimitedWithExtra extends Placement {
	public AtSurfaceLayerLimitedWithExtra(Codec codec) {
		super(codec);
	}

	@Override
	public Stream getPositions(WorldDecoratingHelper helper, Random rand, IPlacementConfig confi, BlockPos pos) {
		AtSurfaceLayerLimitedWithExtraConfig config = (AtSurfaceLayerLimitedWithExtraConfig) confi;
		int i = config.count;
		if (rand.nextFloat() < config.extraChance) {
			i += config.extraCount;
		}

		return IntStream.range(0, i).mapToObj(index -> {
			int x = rand.nextInt(16) + pos.getX();
			int z = rand.nextInt(16) + pos.getZ();
			int y = helper.getHeight(Type.MOTION_BLOCKING, x, z);
			return new BlockPos(x, y, z);
		}).filter(aPos -> {
			if (config.isLayerUpperLimit) {
				return aPos.getY() <= config.layerLimit;
			}
			return aPos.getY() >= config.layerLimit;
		});
	}
}
