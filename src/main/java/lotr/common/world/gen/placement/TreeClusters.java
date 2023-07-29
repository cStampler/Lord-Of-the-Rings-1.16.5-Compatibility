package lotr.common.world.gen.placement;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.stream.*;

import com.mojang.serialization.Codec;

import lotr.common.world.map.RoadPointCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.*;

public class TreeClusters extends Placement {
	private static final Random CLUSTER_RAND = new Random(2353233561358230584L);

	public TreeClusters(Codec codec) {
		super(codec);
	}

	@Override
	public Stream getPositions(WorldDecoratingHelper helper, Random rand, IPlacementConfig confi, BlockPos pos) {
		TreeClustersConfig config = (TreeClustersConfig) confi;
		int numPositions = config.count;
		if (rand.nextFloat() < config.extraChance) {
			numPositions += config.extraCount;
		}

		float reciprocalTreeFactor = 1.0F;
		int cluster = Math.round(config.clusterChance * reciprocalTreeFactor);
		if (cluster > 0) {
			long seed = rand.nextLong();
			seed += pos.getX() / config.clusterScale * 3129871 ^ pos.getZ() / config.clusterScale * 116129781L;
			seed = seed * seed * 42317861L + seed * 11L;
			CLUSTER_RAND.setSeed(seed);
			if (CLUSTER_RAND.nextInt(cluster) == 0) {
				numPositions += config.clusterExtraCount + rand.nextInt(config.clusterRandomExtraCount + 1);
			}
		}

		Field privateField = null;
		try {
			privateField = WorldDecoratingHelper.class.getDeclaredField("level");
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		privateField.setAccessible(true);
		ISeedReader level = null;
		try {
			level = (ISeedReader) privateField.get(helper);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		Stream positions = IntStream.range(0, numPositions).mapToObj(index -> {
			int x = rand.nextInt(16) + pos.getX();
			int z = rand.nextInt(16) + pos.getZ();
			int y = helper.getHeight(Type.MOTION_BLOCKING, x, z);
			return new BlockPos(x, y, z);
		}).filter(RoadPointCache.filterNotGeneratingOnRoad(level));
		if (config.layerLimit >= 0) {
			positions = positions.filter(aPos -> {
				if (config.isLayerUpperLimit) {
					return ((Vector3i) aPos).getY() <= config.layerLimit;
				}
				return ((Vector3i) aPos).getY() >= config.layerLimit;
			});
		}

		return positions;
	}
}
