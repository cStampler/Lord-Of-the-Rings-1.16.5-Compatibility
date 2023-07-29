package lotr.common.block;

import java.lang.reflect.Field;
import java.util.*;

import lotr.common.LOTRLog;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.ForgeRegistries;

public class SignSetupHelper {
	private static List preparedSignBlocks = new ArrayList();

	public static void add(Block block) {
		if (!preparedSignBlocks.contains(block)) {
			preparedSignBlocks.add(block);
		}

	}

	public static void register(RegistryEvent.Register<Block> event) {
		if (event.getRegistry() == ForgeRegistries.BLOCKS) {
			try {
				TileEntityType signType = TileEntityType.SIGN;
				Field blocksField = null;
				Field[] fs = TileEntityType.class.getDeclaredFields();
				Field[] var4 = fs;
				int var5 = fs.length;

				for (int var6 = 0; var6 < var5; ++var6) {
					Field f = var4[var6];
					LOTRUtil.unlockFinalField(f);
					if (f.getType() == Set.class) {
						blocksField = f;
						break;
					}
				}

				Set blockSet = (Set) blocksField.get(signType);
				Set newSet = new HashSet(blockSet);
				newSet.addAll(preparedSignBlocks);
				blocksField.set(signType, newSet);
				LOTRLog.info("Auto-registered %d sign blocks", preparedSignBlocks.size());
			} catch (IllegalAccessException | IllegalArgumentException var8) {
				LOTRLog.error("Error adding sign blocks to SignEntity");
				var8.printStackTrace();
			}

		}
	}
}
