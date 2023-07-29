package lotr.common.world.gen.tree;

import com.mojang.serialization.Codec;

import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraftforge.registries.ForgeRegistries;

public class LOTRTreeDecorators {
	public static TreeDecoratorType PINE_BRANCH;
	public static TreeDecoratorType PINE_STRIP;
	public static TreeDecoratorType MIRK_OAK_WEBS;
	public static TreeDecoratorType MIRK_OAK_LEAVES_GROWTH;

	public static void register() {
		PINE_BRANCH = registerModded("pine_branch", PineBranchDecorator.CODEC);
		PINE_STRIP = registerModded("pine_strip", PineStripDecorator.CODEC);
		MIRK_OAK_WEBS = registerModded("mirk_oak_webs", MirkOakWebsDecorator.CODEC);
		MIRK_OAK_LEAVES_GROWTH = registerModded("mirk_oak_leaves_growth", MirkOakLeavesGrowthDecorator.CODEC);
	}

	private static TreeDecoratorType registerModded(String name, Codec codec) {
		TreeDecoratorType type = new TreeDecoratorType(codec);
		type.setRegistryName("lotr", name);
		ForgeRegistries.TREE_DECORATOR_TYPES.register(type);
		return type;
	}
}
