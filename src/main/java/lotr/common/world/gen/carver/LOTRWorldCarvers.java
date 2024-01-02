package lotr.common.world.gen.carver;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import lotr.common.init.LOTRBlocks;
import lotr.common.init.RegistryOrderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class LOTRWorldCarvers {
	public static final DeferredRegister<WorldCarver<?>> WORLD_CARVERS;
	public static final WorldCarver<ProbabilityConfig> CAVE;
	public static final WorldCarver<ProbabilityConfig> CANYON;
	public static final WorldCarver<ProbabilityConfig> UNDERWATER_CAVE;
	public static final WorldCarver<ProbabilityConfig> UNDERWATER_CANYON;

	static {
		WORLD_CARVERS = DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, "lotr");
		CAVE = preRegCarver("cave", new MiddleEarthCaveCarver(ProbabilityConfig.CODEC, 256));
		CANYON = preRegCarver("canyon", new MiddleEarthCanyonCarver(ProbabilityConfig.CODEC));
		UNDERWATER_CAVE = preRegCarver("underwater_cave", new MiddleEarthUnderwaterCaveCarver(ProbabilityConfig.CODEC));
		UNDERWATER_CANYON = preRegCarver("underwater_canyon", new MiddleEarthUnderwaterCanyonCarver(ProbabilityConfig.CODEC));
	}

	public static Set<Block> listCarvableBlocks() {
		return ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, LOTRBlocks.MORDOR_ROCK.get(), LOTRBlocks.GONDOR_ROCK.get(), (Block) LOTRBlocks.ROHAN_ROCK.get(), (Block) LOTRBlocks.BLUE_ROCK.get(), (Block) LOTRBlocks.RED_ROCK.get(), (Block) LOTRBlocks.CHALK.get(), (Block) LOTRBlocks.DIRTY_CHALK.get(), (Block) LOTRBlocks.CHALK_PATH.get(), Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.GRASS_PATH, (Block) LOTRBlocks.MORDOR_DIRT.get(), (Block) LOTRBlocks.MORDOR_DIRT_PATH.get(), Blocks.SANDSTONE, Blocks.RED_SANDSTONE, (Block) LOTRBlocks.WHITE_SANDSTONE.get(), Blocks.MYCELIUM, Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.PACKED_ICE, (Block) LOTRBlocks.SNOW_PATH.get());
	}

	public static Set<Block> listLandOnlyCarvableBlocks() {
		return ImmutableSet.of(Blocks.SAND, Blocks.RED_SAND, LOTRBlocks.WHITE_SAND.get(), Blocks.GRAVEL, LOTRBlocks.MORDOR_GRAVEL.get());
	}

	public static Set<Block> listUnderwaterCarvableBlocks() {
		  Set<Block> blocks = new HashSet<Block>();
		  blocks.addAll(listCarvableBlocks());
		  blocks.addAll(listLandOnlyCarvableBlocks());
		  blocks.addAll(Stream.of(Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR).collect(Collectors.toSet()));
	    return blocks;
	  }

	private static <CC extends net.minecraft.world.gen.carver.ICarverConfig, C extends WorldCarver<CC>> C preRegCarver(String name, C carver) {
		return (C)RegistryOrderHelper.preRegObject(WORLD_CARVERS, name, carver);
	}

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		WORLD_CARVERS.register(bus);
	}
}
