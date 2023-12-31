package lotr.common.block;

import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import lotr.common.LOTRLog;
import lotr.common.data.AlignmentDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.fac.AlignmentLevels;
import lotr.common.fac.AlignmentPredicates;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionPointer;
import lotr.common.fac.FactionPointers;
import lotr.common.init.LOTRBlocks;
import lotr.common.init.LOTRContainers;
import lotr.common.inv.FactionCraftingContainer;
import lotr.common.recipe.FactionTableType;
import lotr.common.recipe.LOTRRecipes;
import lotr.common.stat.LOTRStats;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class FactionCraftingTableBlock extends Block {
	private final FactionPointer tableFac;
	private final FactionTableType factionRecipeType;
	private final String tableScreenName;

	public FactionCraftingTableBlock(Properties props, FactionPointer tableFac, FactionTableType factionRecipeType, String tableScreenName) {
		super(props);
		this.tableFac = tableFac;
		this.factionRecipeType = factionRecipeType;
		this.tableScreenName = tableScreenName;
	}

	public FactionCraftingTableBlock(Supplier<? extends Block> block, FactionPointer tableFac, FactionTableType factionRecipeType, String tableScreenName) {
		this(Properties.copy((AbstractBlock) block.get()), tableFac, factionRecipeType, tableScreenName);
	}

	private boolean hasRequiredAligment(PlayerEntity player) {
		AlignmentDataModule alignData = LOTRLevelData.getSidedData(player).getAlignmentData();
		Optional<Faction> optFaction = tableFac.resolveFaction(player.level);
		return (Boolean) optFaction.map(faction -> alignData.hasAlignment((Faction) faction, AlignmentPredicates.greaterThanOrEqual(1.0F))).orElseGet(() -> {
			LOTRLog.warn("Player %s tried to use faction table %s, but the associated faction %s does not exist in the current datapacks! Allowing the use.", player.getName().getString(), getRegistryName(), tableFac.getName());
			return true;
		});
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		if (hasRequiredAligment(player)) {
			if (world.isClientSide) {
			} else {
				ContainerType<FactionCraftingContainer> containerType = LOTRContainers.FACTION_CRAFTING.get();
				ITextComponent containerTitle = new TranslationTextComponent(String.format("container.%s.%s", "lotr", tableScreenName));
				FactionCraftingContainer.FactionCraftingContainerInitData initData = null;
				try {
					initData = new FactionCraftingContainer.FactionCraftingContainerInitData(this, factionRecipeType, (Faction) tableFac.resolveFaction(world).orElseThrow(() -> new IllegalStateException("Faction crafting table couldn't resolve faction " + tableFac.getName() + " when sending container to client")));
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				PacketBuffer initBuf = new PacketBuffer(Unpooled.buffer());
				initData.write(initBuf);
				NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((i, inv, p) -> ((FactionCraftingContainer) containerType.create(i, inv, initBuf)).setWorldPosCallable(IWorldPosCallable.create(world, pos)), containerTitle), initData::write);
				player.awardStat(LOTRStats.INTERACT_FACTION_CRAFTING_TABLE);
			}
			return ActionResultType.SUCCESS;
		}
		if (!world.isClientSide) {
			ServerWorld sWorld = (ServerWorld) world;

			for (int l = 0; l < 8; ++l) {
				double x = pos.getX() + world.random.nextFloat();
				double y = pos.getY() + 1;
				double z = pos.getZ() + world.random.nextFloat();
				sWorld.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
			}

			AlignmentLevels.notifyAlignmentNotHighEnough((ServerPlayerEntity) player, 1.0F, tableFac);
		}

		return ActionResultType.SUCCESS;
	}

	public static class Angmar extends FactionCraftingTableBlock {
		public Angmar(Supplier<? extends Block> block) {
			super(block, FactionPointers.ANGMAR, LOTRRecipes.ANGMAR_CRAFTING, "angmar_crafting");
		}
	}

	public static class BlueMountains extends FactionCraftingTableBlock {
		public BlueMountains(Supplier<? extends Block> block) {
			super(block, FactionPointers.BLUE_MOUNTAINS, LOTRRecipes.BLUE_MOUNTAINS_CRAFTING, "blue_mountains_crafting");
		}
	}

	public static class Bree extends FactionCraftingTableBlock {
		public Bree(Supplier<? extends Block> block) {
			super(block, FactionPointers.BREE, LOTRRecipes.BREE_CRAFTING, "bree_crafting");
		}
	}

	public static class Dale extends FactionCraftingTableBlock {
		public Dale(Supplier<? extends Block> block) {
			super(block, FactionPointers.DALE, LOTRRecipes.DALE_CRAFTING, "dale_crafting");
		}
	}

	public static class DolAmroth extends FactionCraftingTableBlock {
		public DolAmroth(Supplier<? extends Block> block) {
			super(block, FactionPointers.GONDOR, LOTRRecipes.DOL_AMROTH_CRAFTING, "dol_amroth_crafting");
		}
	}

	public static class Dorwinion extends FactionCraftingTableBlock {
		public Dorwinion(Supplier<? extends Block> block) {
			super(block, FactionPointers.DORWINION, LOTRRecipes.DORWINION_CRAFTING, "dorwinion_crafting");
		}
	}

	public static class Dunlending extends FactionCraftingTableBlock {
		public Dunlending(Supplier<? extends Block> block) {
			super(block, FactionPointers.DUNLAND, LOTRRecipes.DUNLENDING_CRAFTING, "dunlending_crafting");
		}
	}

	public static class Dwarven extends FactionCraftingTableBlock {
		public Dwarven(Supplier<? extends Block> block) {
			super(block, FactionPointers.DURINS_FOLK, LOTRRecipes.DWARVEN_CRAFTING, "dwarven_crafting");
		}
	}

	public static class Galadhrim extends FactionCraftingTableBlock {
		public Galadhrim(Supplier<? extends Block> block) {
			super(block, FactionPointers.LOTHLORIEN, LOTRRecipes.GALADHRIM_CRAFTING, "galadhrim_crafting");
		}
	}

	public static class Gondor extends FactionCraftingTableBlock {
		public Gondor(Supplier<? extends Block> block) {
			super(block, FactionPointers.GONDOR, LOTRRecipes.GONDOR_CRAFTING, "gondor_crafting");
		}
	}

	public static class Harad extends FactionCraftingTableBlock {
		public Harad(Supplier<? extends Block> block) {
			super(block, FactionPointers.NEAR_HARAD, LOTRRecipes.HARAD_CRAFTING, "harad_crafting");
		}
	}

	public static class Hobbit extends FactionCraftingTableBlock {
		public Hobbit(Supplier<? extends Block> block) {
			super(block, FactionPointers.HOBBITS, LOTRRecipes.HOBBIT_CRAFTING, "hobbit_crafting");
		}
	}

	public static class Lindon extends FactionCraftingTableBlock {
		public Lindon(Supplier<? extends Block> block) {
			super(block, FactionPointers.HIGH_ELVES, LOTRRecipes.LINDON_CRAFTING, "lindon_crafting");
		}
	}

	public static class Lossoth extends FactionCraftingTableBlock {
		public Lossoth(Supplier<? extends Block> block) {
			super(block, FactionPointers.LOSSOTH, LOTRRecipes.LOSSOTH_CRAFTING, "lossoth_crafting");
		}
	}

	public static class Mordor extends FactionCraftingTableBlock {
		public Mordor(Supplier<? extends Block> block) {
			super(Properties.copy((AbstractBlock) block.get()).lightLevel(LOTRBlocks.constantLight(8)), FactionPointers.MORDOR, LOTRRecipes.MORDOR_CRAFTING, "mordor_crafting");
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
			for (int l = 0; l < 2; ++l) {
				double d0 = pos.getX() + MathHelper.nextFloat(rand, 0.25F, 0.75F);
				double d1 = pos.getY() + 1.0D;
				double d2 = pos.getZ() + MathHelper.nextFloat(rand, 0.25F, 0.75F);
				world.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}

		}
	}

	public static class Ranger extends FactionCraftingTableBlock {
		public Ranger(Supplier<? extends Block> block) {
			super(block, FactionPointers.DUNEDAIN_NORTH, LOTRRecipes.RANGER_CRAFTING, "ranger_crafting");
		}
	}

	public static class Rivendell extends FactionCraftingTableBlock {
		public Rivendell(Supplier<? extends Block> block) {
			super(block, FactionPointers.HIGH_ELVES, LOTRRecipes.RIVENDELL_CRAFTING, "rivendell_crafting");
		}
	}

	public static class Rohan extends FactionCraftingTableBlock {
		public Rohan(Supplier<? extends Block> block) {
			super(block, FactionPointers.ROHAN, LOTRRecipes.ROHAN_CRAFTING, "rohan_crafting");
		}
	}

	public static class Umbar extends FactionCraftingTableBlock {
		public Umbar(Supplier<? extends Block> block) {
			super(block, FactionPointers.NEAR_HARAD, LOTRRecipes.UMBAR_CRAFTING, "umbar_crafting");
		}
	}

	public static class Uruk extends FactionCraftingTableBlock {
		public Uruk(Supplier<? extends Block> block) {
			super(block, FactionPointers.ISENGARD, LOTRRecipes.URUK_CRAFTING, "uruk_crafting");
		}
	}

	public static class WoodElven extends FactionCraftingTableBlock {
		public WoodElven(Supplier<? extends Block> block) {
			super(block, FactionPointers.WOODLAND_REALM, LOTRRecipes.WOOD_ELVEN_CRAFTING, "wood_elven_crafting");
		}
	}
}
