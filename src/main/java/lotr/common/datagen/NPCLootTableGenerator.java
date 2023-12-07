package lotr.common.datagen;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lotr.common.init.LOTREntities;
import lotr.common.init.LOTRItems;
import lotr.common.item.VesselDrinkItem;
import lotr.common.loot.functions.SetNPCDrinkPotency;
import lotr.common.loot.functions.SetPouchColorFromEntityFaction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.StandaloneLootEntry;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.KilledByPlayer;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.loot.conditions.RandomChanceWithLooting;
import net.minecraft.loot.functions.LootingEnchantBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class NPCLootTableGenerator implements IDataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator dataGenerator;
	private final Map<ResourceLocation, LootTable.Builder> lootTables = new HashMap<>();
	private final LootParameterSet parameterSet;

	public NPCLootTableGenerator(DataGenerator dataGenerator) {
		parameterSet = LootParameterSets.ENTITY;
		this.dataGenerator = dataGenerator;
	}

	private void constructLootTables() {
		ResourceLocation orcBones = makeCommonPart("orc_bones", tableWithSinglePoolOfItemWithCount(LOTRItems.ORC_BONE.get(), 1.0F));
		ResourceLocation elfBones = makeCommonPart("elf_bones", tableWithSinglePoolOfItemWithCount(LOTRItems.ELF_BONE.get(), 1.0F));
		ResourceLocation manBones = makeCommonPart("man_bones", tableWithSinglePoolOfItemWithCount(Items.BONE, 1.0F));
		ResourceLocation dwarfBones = makeCommonPart("dwarf_bones", tableWithSinglePoolOfItemWithCount(LOTRItems.DWARF_BONE.get(), 1.0F));
		ResourceLocation hobbitBones = makeCommonPart("hobbit_bones", tableWithSinglePoolOfItemWithCount(LOTRItems.HOBBIT_BONE.get(), 1.0F));
		ResourceLocation wargBones = makeCommonPart("warg_bones", LootTable.lootTable().withPool(poolWithItemEntryWithCount(LOTRItems.WARG_BONE.get(), 1.0F, 3.0F)));
		ResourceLocation wargFurs = makeCommonPart("warg_furs", LootTable.lootTable().withPool(poolWithItemEntryWithCount(LOTRItems.FUR.get(), 1.0F, 3.0F)));
		ResourceLocation arrows = makeCommonPart("arrows", tableWithSinglePoolOfItemWithCount(Items.ARROW, 2.0F));
		ResourceLocation elfLembas = makeCommonPart("elf_lembas", LootTable.lootTable().withPool(poolWithItemEntry(LOTRItems.LEMBAS.get())));
		ResourceLocation dwarfRareDrops = makeCommonPart("dwarf_rare_drops", LootTable.lootTable().withPool(pool().add(itemLootEntry(Items.IRON_INGOT)).add(itemLootEntry(LOTRItems.DWARVEN_STEEL_INGOT.get())).add(itemLootEntryWithCountAndLootingBonus(Items.GOLD_NUGGET, 1.0F, 3.0F, 0.0F, 1.0F)).add(itemLootEntryWithCountAndLootingBonus((IItemProvider) LOTRItems.SILVER_NUGGET.get(), 1.0F, 3.0F, 0.0F, 1.0F))));
		ResourceLocation pouch = makeCommonPart("pouch", LootTable.lootTable().withPool(pool().add(itemLootEntry(LOTRItems.SMALL_POUCH.get()).setWeight(6)).add(itemLootEntry(LOTRItems.MEDIUM_POUCH.get()).setWeight(3)).add(itemLootEntry((IItemProvider) LOTRItems.LARGE_POUCH.get()).setWeight(1)).apply(SetPouchColorFromEntityFaction.setPouchColorFromEntityFactionBuilder(0.5F)).when(KilledByPlayer.killedByPlayer()).when(RandomChance.randomChance(0.016666F))));
		ResourceLocation orcBase = makeCommonPart("orc", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(orcBones)).withPool(poolWithSingleEntryOfOtherTable(pouch)).withPool(poolWithItemEntryWithCount(Items.ROTTEN_FLESH, 2.0F)).withPool(this.poolWithItemEntryWithCount((IItemProvider) LOTRItems.MAGGOTY_BREAD.get(), 1.0F, 2.0F).when(RandomChance.randomChance(0.1F))).withPool(poolWithItemEntryWithDrinkPotency((IItemProvider) LOTRItems.ORC_DRAUGHT.get()).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.025F, 0.008F))));
		ResourceLocation orcWithOrcSteel = makeCommonPart("orc_with_orc_steel", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(orcBase)).withPool(poolWithItemEntryWithCount(LOTRItems.ORC_STEEL_INGOT.get(), 1.0F, 2.0F).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.025F, 0.008F))));
		ResourceLocation orcWithUrukSteel = makeCommonPart("orc_with_uruk_steel", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(orcBase)).withPool(poolWithItemEntryWithCount(LOTRItems.URUK_STEEL_INGOT.get(), 1.0F, 2.0F).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.025F, 0.008F))));
		ResourceLocation elfBase = makeCommonPart("elf", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(elfBones)).withPool(poolWithSingleEntryOfOtherTable(pouch)).withPool(poolWithSingleEntryOfOtherTable(arrows)));
		ResourceLocation elfWithMiruvor = makeCommonPart("elf_with_miruvor", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(elfBase)).withPool(poolWithItemEntryWithDrinkPotency(LOTRItems.MIRUVOR.get()).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.05F, 0.016F))));
		ResourceLocation manBase = makeCommonPart("man", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(manBones)).withPool(poolWithSingleEntryOfOtherTable(pouch)));
		ResourceLocation dwarfBase = makeCommonPart("dwarf", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(dwarfBones)).withPool(poolWithSingleEntryOfOtherTable(pouch)).withPool(poolWithSingleEntryOfOtherTable(dwarfRareDrops).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.05F, 0.016F))).withPool(this.poolWithItemEntry((IItemProvider) LOTRItems.BOOK_OF_TRUE_SILVER.get()).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.025F, 0.005F))));
		ResourceLocation hobbitBase = makeCommonPart("hobbit", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(hobbitBones)).withPool(poolWithSingleEntryOfOtherTable(pouch)));
		ResourceLocation wargBase = makeCommonPart("warg", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(wargBones)).withPool(poolWithSingleEntryOfOtherTable(wargFurs)));
		ResourceLocation hobbit = makeFactionBase("hobbit", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(hobbitBase)));
		ResourceLocation breeMan = makeFactionBase("bree_man", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(manBase)));
		ResourceLocation breeHobbit = makeFactionBase("bree_hobbit", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(hobbitBase)));
		ResourceLocation blueMountains = makeFactionBase("blue_mountains", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(dwarfBase)));
		ResourceLocation lindon = makeFactionBase("lindon", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(elfWithMiruvor)));
		ResourceLocation rivendell = makeFactionBase("rivendell", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(elfWithMiruvor)));
		ResourceLocation gundabad = makeFactionBase("gundabad", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(orcWithOrcSteel)));
		ResourceLocation woodElf = makeFactionBase("wood_elf", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(elfBase)));
		ResourceLocation dale = makeFactionBase("dale", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(manBase)));
		ResourceLocation durinsFolk = makeFactionBase("durins_folk", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(dwarfBase)));
		ResourceLocation galadhrim = makeFactionBase("galadhrim", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(elfWithMiruvor)).withPool(poolWithSingleEntryOfOtherTable(elfLembas).when(KilledByPlayer.killedByPlayer()).when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.025F, 0.008F))));
		ResourceLocation dunlending = makeFactionBase("dunlending", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(manBase)));
		ResourceLocation uruk = makeFactionBase("uruk", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(orcWithUrukSteel)));
		ResourceLocation isengardSnaga = makeFactionBase("isengard_snaga", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(orcWithOrcSteel)));
		ResourceLocation rohan = makeFactionBase("rohan", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(manBase)));
		ResourceLocation gondor = makeFactionBase("gondor", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(manBase)));
		ResourceLocation mordor = makeFactionBase("mordor", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(orcWithOrcSteel)));
		ResourceLocation harnedhrim = makeFactionBase("harnedhrim", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(manBase)));
		ResourceLocation coastSouthron = makeFactionBase("coast_southron", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(manBase)));
		ResourceLocation umbar = makeFactionBase("umbar", LootTable.lootTable().withPool(poolWithSingleEntryOfOtherTable(manBase)));
		makeEntityTableFromPools(LOTREntities.HOBBIT, hobbit);
		makeEntityTableFromPools(LOTREntities.MORDOR_ORC, mordor);
		makeEntityTableFromPools(LOTREntities.GONDOR_MAN, gondor);
		makeEntityTableFromPools(LOTREntities.GALADHRIM_ELF, galadhrim);
		makeEntityTableFromPools(LOTREntities.GONDOR_SOLDIER, gondor);
		makeEntityTableFromPools(LOTREntities.DWARF, durinsFolk);
		makeEntityTableFromPools(LOTREntities.DWARF_WARRIOR, durinsFolk);
		makeEntityTableFromPools(LOTREntities.GALADHRIM_WARRIOR, galadhrim);
		makeEntityTableFromPools(LOTREntities.URUK, uruk);
		makeEntityTableFromPools(LOTREntities.ROHAN_MAN, rohan);
		makeEntityTableFromPools(LOTREntities.ROHIRRIM_WARRIOR, rohan);
		makeEntityTableFromPools(LOTREntities.GUNDABAD_ORC, gundabad);
		makeEntityTableFromPools(LOTREntities.DALE_MAN, dale);
		makeEntityTableFromPools(LOTREntities.DALE_SOLDIER, dale);
		makeEntityTableFromPools(LOTREntities.DUNLENDING, dunlending);
		makeEntityTableFromPools(LOTREntities.DUNLENDING_WARRIOR, dunlending);
		makeEntityTableFromPools(LOTREntities.LINDON_ELF, lindon);
		makeEntityTableFromPools(LOTREntities.LINDON_WARRIOR, lindon);
		makeEntityTableFromPools(LOTREntities.RIVENDELL_ELF, rivendell);
		makeEntityTableFromPools(LOTREntities.RIVENDELL_WARRIOR, rivendell);
		makeEntityTableFromPools(LOTREntities.COAST_SOUTHRON, coastSouthron);
		makeEntityTableFromPools(LOTREntities.COAST_SOUTHRON_WARRIOR, coastSouthron);
		makeEntityTableFromPools(LOTREntities.HARNEDHRIM, harnedhrim);
		makeEntityTableFromPools(LOTREntities.HARNENNOR_WARRIOR, harnedhrim);
		makeEntityTableFromPools(LOTREntities.BLUE_MOUNTAINS_DWARF, blueMountains);
		makeEntityTableFromPools(LOTREntities.BLUE_MOUNTAINS_WARRIOR, blueMountains);
		makeEntityTableFromPools(LOTREntities.UMBAR_MAN, umbar);
		makeEntityTableFromPools(LOTREntities.UMBAR_SOLDIER, umbar);
		makeEntityTableFromPools(LOTREntities.GONDOR_ARCHER, gondor, arrows);
		makeEntityTableFromPools(LOTREntities.ROHIRRIM_BOWMAN, rohan, arrows);
		makeEntityTableFromPools(LOTREntities.DALE_BOWMAN, dale, arrows);
		makeEntityTableFromPools(LOTREntities.DUNLENDING_BOWMAN, dunlending, arrows);
		makeEntityTableFromPools(LOTREntities.COAST_SOUTHRON_ARCHER, coastSouthron, arrows);
		makeEntityTableFromPools(LOTREntities.HARNENNOR_ARCHER, harnedhrim, arrows);
		makeEntityTableFromPools(LOTREntities.UMBAR_ARCHER, umbar, arrows);
		makeEntityTableFromPools(LOTREntities.MORDOR_ORC_ARCHER, mordor, arrows);
		makeEntityTableFromPools(LOTREntities.GUNDABAD_ORC_ARCHER, gundabad, arrows);
		makeEntityTableFromPools(LOTREntities.URUK_ARCHER, uruk, arrows);
		makeEntityTableFromPools(LOTREntities.DWARF_ARCHER, durinsFolk, arrows);
		makeEntityTableFromPools(LOTREntities.BLUE_MOUNTAINS_ARCHER, blueMountains, arrows);
		makeEntityTableFromPools(LOTREntities.WOOD_ELF, woodElf);
		makeEntityTableFromPools(LOTREntities.WOOD_ELF_WARRIOR, woodElf);
		makeEntityTableFromPools(LOTREntities.BREE_MAN, breeMan);
		makeEntityTableFromPools(LOTREntities.BREE_HOBBIT, breeHobbit);
		makeEntityTableFromPools(LOTREntities.BREE_GUARD, breeMan);
		makeEntityTableFromPools(LOTREntities.GUNDABAD_WARG, wargBase);
		makeEntityTableFromPools(LOTREntities.ISENGARD_WARG, wargBase);
		makeEntityTableFromPools(LOTREntities.MORDOR_WARG, wargBase);
		makeEntityTableFromPools(LOTREntities.ISENGARD_SNAGA, isengardSnaga);
		makeEntityTableFromPools(LOTREntities.ISENGARD_SNAGA_ARCHER, isengardSnaga, arrows);
	}

	@Override
	public String getName() {
		return "LOTRNPCLootTables";
	}

	private StandaloneLootEntry.Builder<?> itemLootEntry(IItemProvider item) {
		return ItemLootEntry.lootTableItem(item);
	}

	private StandaloneLootEntry.Builder<?> itemLootEntryWithCount(IItemProvider item, float minCount, float maxCount) {
		return itemLootEntry(item).apply(SetCount.setCount(RandomValueRange.between(minCount, maxCount)));
	}

	private StandaloneLootEntry.Builder<?> itemLootEntryWithCountAndLootingBonus(IItemProvider item, float minCount, float maxCount, float minBonus, float maxBonus) {
		return itemLootEntryWithCount(item, minCount, maxCount).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(minBonus, maxBonus)));
	}

	private ResourceLocation makeCommonPart(String name, Builder builder) {
		ResourceLocation path = new ResourceLocation("lotr", "entities/common/parts/" + name);
		lootTables.put(path, builder);
		return path;
	}

	private void makeEntityTable(Supplier<? extends EntityType<? extends Entity>> entityType, Builder builder) {
		lootTables.put(new ResourceLocation("lotr", "entities/" + entityType.get().getRegistryName().getPath()), builder);
	}

	private void makeEntityTableFromPools(Supplier<? extends EntityType<? extends Entity>> entityType, ResourceLocation... basePools) {
		Builder tableBuilder = LootTable.lootTable();
		Stream.of(basePools).forEach(pool -> {
			tableBuilder.withPool(poolWithSingleEntryOfOtherTable(pool));
		});
		makeEntityTable(entityType, tableBuilder);
	}

	private ResourceLocation makeFactionBase(String name, Builder builder) {
		ResourceLocation path = new ResourceLocation("lotr", "entities/common/faction_bases/" + name);
		lootTables.put(path, builder);
		return path;
	}

	private LootPool.Builder pool() {
		return this.pool(1);
	}

	private LootPool.Builder pool(int rolls) {
		return LootPool.lootPool().setRolls(ConstantRange.exactly(rolls));
	}

	private LootPool.Builder poolWithItemEntry(IItemProvider item) {
		return this.poolWithItemEntry(item, itemLootEntry -> {
		});
	}

	private LootPool.Builder poolWithItemEntry(IItemProvider item, Consumer<StandaloneLootEntry.Builder<?>> lootFunctionAdder) {
		net.minecraft.loot.StandaloneLootEntry.Builder<?> itemLootEntry = itemLootEntry(item);
		lootFunctionAdder.accept(itemLootEntry);
		return this.pool().add(itemLootEntry);
	}

	private LootPool.Builder poolWithItemEntryWithCount(IItemProvider item, float maxCount) {
		return this.poolWithItemEntryWithCount(item, 0.0F, maxCount);
	}

	private LootPool.Builder poolWithItemEntryWithCount(IItemProvider item, float minCount, float maxCount) {
		return this.poolWithItemEntry(item, itemLootEntry -> {
			itemLootEntry.apply(SetCount.setCount(RandomValueRange.between(minCount, maxCount))).apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F)));
		});
	}

	private LootPool.Builder poolWithItemEntryWithDrinkPotency(IItemProvider item) {
		Item asItem = item.asItem();
		if (!(asItem instanceof VesselDrinkItem) || !((VesselDrinkItem) asItem).hasPotencies) {
			throw new IllegalArgumentException(asItem.getRegistryName() + " is not a drink item with potencies");
		}
		return this.poolWithItemEntry(item, itemLootEntry -> {
			itemLootEntry.apply(SetNPCDrinkPotency.setNPCDrinkPotencyBuilder());
		});
	}

	private LootPool.Builder poolWithSingleEntryOfOtherTable(ResourceLocation otherTable) {
		return this.pool().add(TableLootEntry.lootTableReference(otherTable));
	}

	@Override
	public void run(DirectoryCache cache) {
		constructLootTables();
		Path rootPath = dataGenerator.getOutputFolder();
		Map<ResourceLocation, LootTable> tables = Maps.newHashMap();
		lootTables.forEach((name, tableBuilder) -> {
			if (tables.put(name, tableBuilder.setParamSet(parameterSet).build()) != null) {
				throw new IllegalStateException("Duplicate loot table " + name);
			}
		});
		ValidationTracker validationTracker = new ValidationTracker(LootParameterSets.ALL_PARAMS, name -> null, tables::get);
		validate(tables, validationTracker);
		Multimap<String, String> problems = validationTracker.getProblems();
		if (!problems.isEmpty()) {
			problems.forEach((tableName, problem) -> {
				LOGGER.warn("Found validation problem in " + tableName + ": " + problem);
			});
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		}
		tables.forEach((name, lootTable) -> {
			Path path = getPath(rootPath, name);

			try {
				IDataProvider.save(GSON, cache, LootTableManager.serialize(lootTable), path);
			} catch (IOException var6) {
				LOGGER.error("Couldn't generate loot table {}", path, var6);
			}

		});
	}

	private Builder tableWithSinglePoolOfItemWithCount(IItemProvider item, float maxCount) {
		return LootTable.lootTable().withPool(this.poolWithItemEntryWithCount(item, maxCount));
	}

	private void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationTracker) {
		UnmodifiableIterator<ResourceLocation> var3 = Sets.intersection(LootTables.all(), map.keySet()).iterator();

		while (var3.hasNext()) {
			ResourceLocation name = (ResourceLocation) var3.next();
			validationTracker.reportProblem("Shouldn't be overwriting built-in table: " + name);
		}

		map.keySet().stream().filter(namex -> !"lotr".equals(namex.getNamespace())).forEach(namex -> {
			validationTracker.reportProblem("Table " + namex + " is not in the mod's own namespace");
		});
		map.forEach((namex, lootTable) -> {
			LootTableManager.validate(validationTracker, namex, lootTable);
		});
	}

	private static Path getPath(Path path, ResourceLocation name) {
		return path.resolve("data/" + name.getNamespace() + "/loot_tables/" + name.getPath() + ".json");
	}
}
