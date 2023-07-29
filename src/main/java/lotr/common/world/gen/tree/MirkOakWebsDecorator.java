package lotr.common.world.gen.tree;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lotr.common.block.HangingWebBlock;
import lotr.common.init.*;
import lotr.common.world.biome.MirkwoodBiome;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.treedecorator.*;

public class MirkOakWebsDecorator extends TreeDecorator {
	public static final Codec<MirkOakWebsDecorator> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.BOOL.fieldOf("only_in_mirkwood").orElse(false).forGetter(deco -> deco.mirkwoodOnly), Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(deco -> deco.prob), Codec.floatRange(0.0F, 1.0F).fieldOf("per_block_chance").forGetter(deco -> deco.perBlockChance), Codec.floatRange(0.0F, 1.0F).fieldOf("double_web_chance").forGetter(deco -> deco.doubleWebChance)).apply(instance, MirkOakWebsDecorator::new));
	private final boolean mirkwoodOnly;
	private final float prob;
	private final float perBlockChance;
	private final float doubleWebChance;

	public MirkOakWebsDecorator(boolean flag, float f, float perBlock, float doubleWeb) {
		mirkwoodOnly = flag;
		prob = f;
		perBlockChance = perBlock;
		doubleWebChance = doubleWeb;
	}

	@Override
	public void place(ISeedReader world, Random rand, List trunk, List leaves, Set decoSet, MutableBoundingBox bb) {
		boolean doDecorate = rand.nextFloat() < prob;
		if (doDecorate && mirkwoodOnly) {
			BlockPos centralBasePos = new BlockPos((bb.x0 + bb.x1) / 2, bb.y0, (bb.z0 + bb.z1) / 2);
			doDecorate &= LOTRBiomes.getWrapperFor(world.getBiome(centralBasePos), world) instanceof MirkwoodBiome;
		}

		if (doDecorate) {
			Iterator var16 = leaves.iterator();

			while (true) {
				BlockPos webPos;
				do {
					BlockPos leavesPos;
					do {
						if (!var16.hasNext()) {
							return;
						}

						leavesPos = (BlockPos) var16.next();
					} while (rand.nextFloat() >= perBlockChance);

					webPos = leavesPos.below();
				} while (!world.isEmptyBlock(webPos));

				BlockState baseWebState = ((Block) LOTRBlocks.HANGING_WEB.get()).defaultBlockState();
				BlockPos belowWebPos = webPos.below();
				boolean placeDouble = rand.nextFloat() < doubleWebChance && world.isEmptyBlock(belowWebPos);
				BlockState topWebState;
				if (placeDouble) {
					topWebState = baseWebState.setValue(HangingWebBlock.WEB_TYPE, HangingWebBlock.Type.DOUBLE_TOP);
					BlockState bottomWebState = baseWebState.setValue(HangingWebBlock.WEB_TYPE, HangingWebBlock.Type.DOUBLE_BOTTOM);
					setBlock(world, webPos, topWebState, decoSet, bb);
					setBlock(world, belowWebPos, bottomWebState, decoSet, bb);
				} else {
					topWebState = baseWebState.setValue(HangingWebBlock.WEB_TYPE, HangingWebBlock.Type.SINGLE);
					setBlock(world, webPos, topWebState, decoSet, bb);
				}

				world.getBlockState(webPos).updateNeighbourShapes(world, webPos, 3);
			}
		}
	}

	@Override
	protected TreeDecoratorType type() {
		return LOTRTreeDecorators.MIRK_OAK_WEBS;
	}
}
