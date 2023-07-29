package lotr.common.world.gen.tree;

import java.util.*;
import java.util.function.IntPredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lotr.common.LOTRLog;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.treedecorator.*;
import net.minecraftforge.common.ToolType;

public class PineStripDecorator extends TreeDecorator {
	public static final Codec<PineStripDecorator> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(deco -> deco.prob), Codec.floatRange(0.0F, 1.0F).fieldOf("strip_begin_height_fraction").forGetter(deco -> deco.stripBeginHeightFraction), Codec.floatRange(0.0F, 1.0F).fieldOf("strip_complete_height_fraction").forGetter(deco -> deco.stripCompleteHeightFraction)).apply(instance, PineStripDecorator::new));
	private final float prob;
	private final float stripBeginHeightFraction;
	private final float stripCompleteHeightFraction;

	public PineStripDecorator(float f, float s0, float s1) {
		prob = f;
		stripBeginHeightFraction = s0;
		stripCompleteHeightFraction = s1;
	}

	@Override
	public void place(ISeedReader world, Random rand, List trunk, List leaves, Set decoSet, MutableBoundingBox bb) {
		if (rand.nextFloat() < prob) {
			int trunkBase = ((BlockPos) trunk.get(0)).getY();
			int trunkHeight = trunk.size();
			int stripBeginY = Math.round(trunkHeight * stripBeginHeightFraction);
			int stripCompleteY = Math.round(trunkHeight * stripCompleteHeightFraction);
			IntPredicate stripTest = y -> {
				int yRel = y - trunkBase;
				if (yRel < stripBeginY) {
					return false;
				}
				if (yRel >= stripCompleteY) {
					return true;
				}
				return rand.nextFloat() < (float) (yRel - stripBeginY + 1) / (float) (stripCompleteY - stripBeginY + 1);
			};
			stripWood(world, trunk, stripTest, bb);
			stripWood(world, decoSet, stripTest, bb);
		}

	}

	private void stripWood(ISeedReader world, Collection posCollection, IntPredicate stripTest, MutableBoundingBox bb) {
		Set temp = new HashSet();
		Iterator var6 = posCollection.iterator();

		while (var6.hasNext()) {
			BlockPos pos = (BlockPos) var6.next();
			if (stripTest.test(pos.getY())) {
				BlockState state = world.getBlockState(pos);

				try {
					BlockState strippedState = state.getToolModifiedState(world.getLevel(), pos, (PlayerEntity) null, ItemStack.EMPTY, ToolType.AXE);
					if (strippedState != null) {
						setBlock(world, pos, strippedState, temp, bb);
					}
				} catch (Exception var10) {
					LOTRLog.error("PineStripDecorator caught an exception while trying to obtain the stripped state of blockstate %s - not altering the blockstate", state.toString());
					var10.printStackTrace();
				}
			}
		}

	}

	@Override
	protected TreeDecoratorType type() {
		return LOTRTreeDecorators.PINE_STRIP;
	}
}
