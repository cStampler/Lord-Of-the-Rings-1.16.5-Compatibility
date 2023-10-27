package lotr.common.world.gen.placement;

import java.util.Random;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

public class ByWater extends Placement {
	public ByWater(Codec codec) {
		super(codec);
	}

	@Override
	public Stream getPositions(WorldDecoratingHelper helper, Random rand, IPlacementConfig confi, BlockPos pos) {
		ByWaterConfig config = (ByWaterConfig) confi;
		Mutable nearbyPos = new Mutable();

		for (int l = 0; l < config.tries; ++l) {
			int dx = MathHelper.nextInt(rand, -config.range, config.range);
			int dy = MathHelper.nextInt(rand, -config.range, config.range);
			int dz = MathHelper.nextInt(rand, -config.range, config.range);
			nearbyPos.setWithOffset(pos, dx, dy, dz);
			if (helper.getBlockState(nearbyPos).getMaterial() == Material.WATER) {
				return Stream.of(pos);
			}
		}

		return Stream.empty();
	}
}
