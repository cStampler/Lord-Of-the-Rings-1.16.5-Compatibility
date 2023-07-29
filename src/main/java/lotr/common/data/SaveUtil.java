package lotr.common.data;

import java.io.*;

import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;

public class SaveUtil {
	private static final FolderName LOTR_FOLDER = new FolderName("lotr");

	public static File getOrCreateLOTRDir(ServerWorld world) {
		MinecraftServer server = world.getServer();
		File dir = server.getWorldPath(LOTR_FOLDER).toFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return dir;
	}

	public static CompoundNBT loadNBTFromFile(File file) throws FileNotFoundException, IOException {
		if (file.exists()) {
			FileInputStream fis = new FileInputStream(file);
			CompoundNBT nbt = CompressedStreamTools.readCompressed(fis);
			fis.close();
			return nbt;
		}
		return new CompoundNBT();
	}

	public static void saveNBTToFile(File file, CompoundNBT nbt) throws FileNotFoundException, IOException {
		CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(file));
	}
}
