package lotr.common.world.gen.carver;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

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
	public static final DeferredRegister WORLD_CARVERS;
	public static final WorldCarver CAVE;
	public static final WorldCarver CANYON;
	public static final WorldCarver UNDERWATER_CAVE;
	public static final WorldCarver UNDERWATER_CANYON;

	static {
		WORLD_CARVERS = DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, "lotr");
		CAVE = preRegCarver("cave", new MiddleEarthCaveCarver(ProbabilityConfig.CODEC, 256));
		CANYON = preRegCarver("canyon", new MiddleEarthCanyonCarver(ProbabilityConfig.CODEC));
		UNDERWATER_CAVE = preRegCarver("underwater_cave", new MiddleEarthUnderwaterCaveCarver(ProbabilityConfig.CODEC));
		UNDERWATER_CANYON = preRegCarver("underwater_canyon", new MiddleEarthUnderwaterCanyonCarver(ProbabilityConfig.CODEC));
	}

	public static Set listCarvableBlocks() {
		return ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, LOTRBlocks.MORDOR_ROCK.get(), LOTRBlocks.GONDOR_ROCK.get(), (Block) LOTRBlocks.ROHAN_ROCK.get(), (Block) LOTRBlocks.BLUE_ROCK.get(), (Block) LOTRBlocks.RED_ROCK.get(), (Block) LOTRBlocks.CHALK.get(), (Block) LOTRBlocks.DIRTY_CHALK.get(), (Block) LOTRBlocks.CHALK_PATH.get(), Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.GRASS_PATH, (Block) LOTRBlocks.MORDOR_DIRT.get(), (Block) LOTRBlocks.MORDOR_DIRT_PATH.get(), Blocks.SANDSTONE, Blocks.RED_SANDSTONE, (Block) LOTRBlocks.WHITE_SANDSTONE.get(), Blocks.MYCELIUM, Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.PACKED_ICE, (Block) LOTRBlocks.SNOW_PATH.get());
	}

	public static Set listLandOnlyCarvableBlocks() {
		return ImmutableSet.of(Blocks.SAND, Blocks.RED_SAND, LOTRBlocks.WHITE_SAND.get(), Blocks.GRAVEL, LOTRBlocks.MORDOR_GRAVEL.get());
	}

	public static Set listUnderwaterCarvableBlocks() {
		return new Builder().addAll(listCarvableBlocks()).addAll(listLandOnlyCarvableBlocks()).add(Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR).build();
	}

	private static WorldCarver preRegCarver(String name, WorldCarver carver) {
		return (WorldCarver) RegistryOrderHelper.preRegObject(WORLD_CARVERS, name, carver);
	}

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		WORLD_CARVERS.register(bus);
	}
}
