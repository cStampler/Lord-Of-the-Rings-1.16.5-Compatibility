package lotr.common.datafix;

import java.util.function.BiFunction;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;

import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.NamespacedSchema;

public class LOTRDataFixes {
	private static final BiFunction NAMESPACED_SCHEMA_FACTORY = (hummel, hummel2) -> new NamespacedSchema((int) hummel, (Schema) hummel2);

	public static void addFixers(DataFixerBuilder builder) {
		builder.addSchema(2230, NAMESPACED_SCHEMA_FACTORY);
	}

	public static void printCurrentVersion() {
		System.out.println(SharedConstants.getCurrentVersion().getWorldVersion());
	}
}
