package lotr.common.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.ForgeRegistries;

public class FallenLeavesHelper {
	private static final List ALL_FALLEN_LEAVES = new ArrayList();
	private static final Map LEAVES_TO_FALLEN_LEAVES = new HashMap();

	public static List getFallenLeavesBlocks() {
		return (List) ALL_FALLEN_LEAVES.stream().map(hummel -> ((Supplier) hummel).get()).collect(Collectors.toList());
	}

	public static void register(Register event) {
		if (event.getRegistry() == ForgeRegistries.BLOCKS) {

		}
	}

	public static void registerFallenLeavesBlock(Block block) {
	}
}
