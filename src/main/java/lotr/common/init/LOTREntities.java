package lotr.common.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import lotr.common.entity.animal.CaracalEntity;
import lotr.common.entity.item.FallingTreasureBlockEntity;
import lotr.common.entity.item.LOTRBoatEntity;
import lotr.common.entity.item.RingPortalEntity;
import lotr.common.entity.misc.AlignmentBonusEntity;
import lotr.common.entity.npc.AbstractMannishEntity;
import lotr.common.entity.npc.BlueDwarfArcherEntity;
import lotr.common.entity.npc.BlueDwarfEntity;
import lotr.common.entity.npc.BlueDwarfWarriorEntity;
import lotr.common.entity.npc.BreeGuardEntity;
import lotr.common.entity.npc.BreeHobbitEntity;
import lotr.common.entity.npc.BreeManEntity;
import lotr.common.entity.npc.CoastSouthronArcherEntity;
import lotr.common.entity.npc.CoastSouthronEntity;
import lotr.common.entity.npc.CoastSouthronWarriorEntity;
import lotr.common.entity.npc.DaleBowmanEntity;
import lotr.common.entity.npc.DaleManEntity;
import lotr.common.entity.npc.DaleSoldierEntity;
import lotr.common.entity.npc.DunlendingBowmanEntity;
import lotr.common.entity.npc.DunlendingEntity;
import lotr.common.entity.npc.DunlendingWarriorEntity;
import lotr.common.entity.npc.DwarfArcherEntity;
import lotr.common.entity.npc.DwarfEntity;
import lotr.common.entity.npc.DwarfWarriorEntity;
import lotr.common.entity.npc.ElfEntity;
import lotr.common.entity.npc.GaladhrimElfEntity;
import lotr.common.entity.npc.GaladhrimWarriorEntity;
import lotr.common.entity.npc.GondorArcherEntity;
import lotr.common.entity.npc.GondorManEntity;
import lotr.common.entity.npc.GondorSoldierEntity;
import lotr.common.entity.npc.GundabadOrcArcherEntity;
import lotr.common.entity.npc.GundabadOrcEntity;
import lotr.common.entity.npc.GundabadWargEntity;
import lotr.common.entity.npc.HarnedhrimEntity;
import lotr.common.entity.npc.HarnennorArcherEntity;
import lotr.common.entity.npc.HarnennorWarriorEntity;
import lotr.common.entity.npc.HobbitEntity;
import lotr.common.entity.npc.IsengardSnagaArcherEntity;
import lotr.common.entity.npc.IsengardSnagaEntity;
import lotr.common.entity.npc.IsengardWargEntity;
import lotr.common.entity.npc.LindonElfEntity;
import lotr.common.entity.npc.LindonWarriorEntity;
import lotr.common.entity.npc.MordorOrcArcherEntity;
import lotr.common.entity.npc.MordorOrcEntity;
import lotr.common.entity.npc.MordorWargEntity;
import lotr.common.entity.npc.OrcEntity;
import lotr.common.entity.npc.RivendellElfEntity;
import lotr.common.entity.npc.RivendellWarriorEntity;
import lotr.common.entity.npc.RohanManEntity;
import lotr.common.entity.npc.RohirrimBowmanEntity;
import lotr.common.entity.npc.RohirrimWarriorEntity;
import lotr.common.entity.npc.UmbarArcherEntity;
import lotr.common.entity.npc.UmbarManEntity;
import lotr.common.entity.npc.UmbarSoldierEntity;
import lotr.common.entity.npc.UrukArcherEntity;
import lotr.common.entity.npc.UrukEntity;
import lotr.common.entity.npc.WargEntity;
import lotr.common.entity.npc.WoodElfEntity;
import lotr.common.entity.npc.WoodElfWarriorEntity;
import lotr.common.entity.projectile.SmokeRingEntity;
import lotr.common.entity.projectile.SpearEntity;
import lotr.common.entity.projectile.ThrownPlateEntity;
import lotr.common.item.LOTRSpawnEggItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.IPlacementPredicate;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class LOTREntities {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, "lotr");
	private static final Map<EntityType<? extends LivingEntity>, Supplier<AttributeModifierMap.MutableAttribute>> ENTITY_ATTRIBUTE_FACTORIES = new HashMap<>();
	private static final List<SpawnEggInfo<?>> DEFERRED_SPAWN_EGGS = new ArrayList<>();
	public static final RegistryObject<EntityType<RingPortalEntity>> RING_PORTAL = regEntity("ring_portal", RingPortalEntity::new, EntityClassification.MISC, 3.0F, 1.5F, 10, 3);
	public static final RegistryObject<EntityType<ThrownPlateEntity>> THROWN_PLATE = regEntity("thrown_plate", ThrownPlateEntity::new, EntityClassification.MISC, 0.5F, 0.5F, 4, 10);
	public static final RegistryObject<EntityType<LOTRBoatEntity>> BOAT = regEntity("boat", LOTRBoatEntity::new, EntityClassification.MISC, 1.375F, 0.5625F, 10, 3);
	public static final RegistryObject<EntityType<FallingTreasureBlockEntity>> FALLING_TREASURE_BLOCK = regEntity("falling_treasure_block", FallingTreasureBlockEntity::new, EntityClassification.MISC, 0.98F, 0.98F, 10, 20);
	public static final RegistryObject<EntityType<SmokeRingEntity>> SMOKE_RING = regEntity("smoke_ring", SmokeRingEntity::new, EntityClassification.MISC, 0.5F, 0.5F, 4, 10, EntityType.Builder::fireImmune);
	public static final RegistryObject<EntityType<AlignmentBonusEntity>> ALIGNMENT_BONUS = regClientsideEntity("alignment_bonus", AlignmentBonusEntity::new, EntityClassification.MISC, 0.5F, 0.5F);
	public static final RegistryObject<EntityType<SpearEntity>> SPEAR = regEntity("spear", SpearEntity::new, EntityClassification.MISC, 0.5F, 0.5F, 4, 20);
	public static final RegistryObject<EntityType<HobbitEntity>> HOBBIT = regNPC("hobbit", HobbitEntity::new, HobbitEntity::regAttrs, EntitySizeHolder.hobbitSize(), SpawnEggInfo.SpawnEggColors.egg(16752511, 8010275));
	public static final RegistryObject<EntityType<MordorOrcEntity>> MORDOR_ORC = regNPC("mordor_orc", MordorOrcEntity::new, MordorOrcEntity::regAttrs, EntitySizeHolder.orcSize(), SpawnEggInfo.SpawnEggColors.egg(3353378, 7042407));
	public static final RegistryObject<EntityType<GondorManEntity>> GONDOR_MAN = regNPC("gondor_man", GondorManEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(13547685, 5652538));
	public static final RegistryObject<EntityType<GaladhrimElfEntity>> GALADHRIM_ELF = regNPC("galadhrim_elf", GaladhrimElfEntity::new, ElfEntity::regAttrs, EntitySizeHolder.elfSize(), SpawnEggInfo.SpawnEggColors.egg(9337185, 15920555));
	public static final RegistryObject<EntityType<GondorSoldierEntity>> GONDOR_SOLDIER = regNPC("gondor_soldier", GondorSoldierEntity::new, GondorSoldierEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(5327948, 15063770));
	public static final RegistryObject<EntityType<DwarfEntity>> DWARF = regNPC("dwarf", DwarfEntity::new, DwarfEntity::regAttrs, EntitySizeHolder.dwarfSize(), SpawnEggInfo.SpawnEggColors.egg(16353133, 15357472));
	public static final RegistryObject<EntityType<DwarfWarriorEntity>> DWARF_WARRIOR = regNPC("dwarf_warrior", DwarfWarriorEntity::new, DwarfWarriorEntity::regAttrs, EntitySizeHolder.dwarfSize(), SpawnEggInfo.SpawnEggColors.egg(2238506, 7108730));
	public static final RegistryObject<EntityType<GaladhrimWarriorEntity>> GALADHRIM_WARRIOR = regNPC("galadhrim_warrior", GaladhrimWarriorEntity::new, GaladhrimWarriorEntity::regAttrs, EntitySizeHolder.elfSize(), SpawnEggInfo.SpawnEggColors.egg(12697274, 15382870));
	public static final RegistryObject<EntityType<UrukEntity>> URUK = regNPC("uruk", UrukEntity::new, UrukEntity::regAttrs, EntitySizeHolder.urukSize(), SpawnEggInfo.SpawnEggColors.egg(2369050, 5790015));
	public static final RegistryObject<EntityType<RohanManEntity>> ROHAN_MAN = regNPC("rohan_man", RohanManEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(16424833, 13406801));
	public static final RegistryObject<EntityType<RohirrimWarriorEntity>> ROHIRRIM_WARRIOR = regNPC("rohirrim_warrior", RohirrimWarriorEntity::new, RohirrimWarriorEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(5524296, 13546384));
	public static final RegistryObject<EntityType<GundabadOrcEntity>> GUNDABAD_ORC = regNPC("gundabad_orc", GundabadOrcEntity::new, OrcEntity::regAttrs, EntitySizeHolder.orcSize(), SpawnEggInfo.SpawnEggColors.egg(3352346, 8548435));
	public static final RegistryObject<EntityType<DaleManEntity>> DALE_MAN = regNPC("dale_man", DaleManEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(16755851, 5252113));
	public static final RegistryObject<EntityType<DaleSoldierEntity>> DALE_SOLDIER = regNPC("dale_soldier", DaleSoldierEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(12034458, 480127));
	public static final RegistryObject<EntityType<DunlendingEntity>> DUNLENDING = regNPC("dunlending", DunlendingEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(15897714, 3679258));
	public static final RegistryObject<EntityType<DunlendingWarriorEntity>> DUNLENDING_WARRIOR = regNPC("dunlending_warrior", DunlendingWarriorEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(5192753, 9337975));
	public static final RegistryObject<EntityType<LindonElfEntity>> LINDON_ELF = regNPC("lindon_elf", LindonElfEntity::new, ElfEntity::regAttrs, EntitySizeHolder.elfSize(), SpawnEggInfo.SpawnEggColors.egg(16761223, 15721387));
	public static final RegistryObject<EntityType<LindonWarriorEntity>> LINDON_WARRIOR = regNPC("lindon_warrior", LindonWarriorEntity::new, LindonWarriorEntity::regAttrs, EntitySizeHolder.elfSize(), SpawnEggInfo.SpawnEggColors.egg(14935016, 7040410));
	public static final RegistryObject<EntityType<RivendellElfEntity>> RIVENDELL_ELF = regNPC("rivendell_elf", RivendellElfEntity::new, ElfEntity::regAttrs, EntitySizeHolder.elfSize(), SpawnEggInfo.SpawnEggColors.egg(16761223, 15721387));
	public static final RegistryObject<EntityType<RivendellWarriorEntity>> RIVENDELL_WARRIOR = regNPC("rivendell_warrior", RivendellWarriorEntity::new, RivendellWarriorEntity::regAttrs, EntitySizeHolder.elfSize(), SpawnEggInfo.SpawnEggColors.egg(14738662, 10723248));
	public static final RegistryObject<EntityType<CoastSouthronEntity>> COAST_SOUTHRON = regNPC("coast_southron", CoastSouthronEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(10779229, 2960685));
	public static final RegistryObject<EntityType<CoastSouthronWarriorEntity>> COAST_SOUTHRON_WARRIOR = regNPC("coast_southron_warrior", CoastSouthronWarriorEntity::new, CoastSouthronWarriorEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(2171169, 11868955));
	public static final RegistryObject<EntityType<HarnedhrimEntity>> HARNEDHRIM = regNPC("harnedhrim", HarnedhrimEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(9854777, 1181187));
	public static final RegistryObject<EntityType<HarnennorWarriorEntity>> HARNENNOR_WARRIOR = regNPC("harnennor_warrior", HarnennorWarriorEntity::new, HarnennorWarriorEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(7016721, 14852422));
	public static final RegistryObject<EntityType<BlueDwarfEntity>> BLUE_MOUNTAINS_DWARF = regNPC("blue_mountains_dwarf", BlueDwarfEntity::new, DwarfEntity::regAttrs, EntitySizeHolder.dwarfSize(), SpawnEggInfo.SpawnEggColors.egg(16353133, 15357472));
	public static final RegistryObject<EntityType<BlueDwarfWarriorEntity>> BLUE_MOUNTAINS_WARRIOR = regNPC("blue_mountains_warrior", BlueDwarfWarriorEntity::new, BlueDwarfWarriorEntity::regAttrs, EntitySizeHolder.dwarfSize(), SpawnEggInfo.SpawnEggColors.egg(3161673, 6257551));
	public static final RegistryObject<EntityType<UmbarManEntity>> UMBAR_MAN = regNPC("umbar_man", UmbarManEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(10779229, 2960685));
	public static final RegistryObject<EntityType<UmbarSoldierEntity>> UMBAR_SOLDIER = regNPC("umbar_soldier", UmbarSoldierEntity::new, UmbarSoldierEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(2960680, 13540692));
	public static final RegistryObject<EntityType<GondorArcherEntity>> GONDOR_ARCHER = regNPC("gondor_archer", GondorArcherEntity::new, GondorSoldierEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(5327948, 15063770));
	public static final RegistryObject<EntityType<RohirrimBowmanEntity>> ROHIRRIM_BOWMAN = regNPC("rohirrim_bowman", RohirrimBowmanEntity::new, RohirrimWarriorEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(5524296, 13546384));
	public static final RegistryObject<EntityType<DaleBowmanEntity>> DALE_BOWMAN = regNPC("dale_bowman", DaleBowmanEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(12034458, 480127));
	public static final RegistryObject<EntityType<DunlendingBowmanEntity>> DUNLENDING_BOWMAN = regNPC("dunlending_bowman", DunlendingBowmanEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(5192753, 9337975));
	public static final RegistryObject<EntityType<CoastSouthronArcherEntity>> COAST_SOUTHRON_ARCHER = regNPC("coast_southron_archer", CoastSouthronArcherEntity::new, CoastSouthronWarriorEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(2171169, 11868955));
	public static final RegistryObject<EntityType<HarnennorArcherEntity>> HARNENNOR_ARCHER = regNPC("harnennor_archer", HarnennorArcherEntity::new, HarnennorWarriorEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(7016721, 14852422));
	public static final RegistryObject<EntityType<UmbarArcherEntity>> UMBAR_ARCHER = regNPC("umbar_archer", UmbarArcherEntity::new, UmbarSoldierEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(2960680, 13540692));
	public static final RegistryObject<EntityType<MordorOrcArcherEntity>> MORDOR_ORC_ARCHER = regNPC("mordor_orc_archer", MordorOrcArcherEntity::new, MordorOrcEntity::regAttrs, EntitySizeHolder.orcSize(), SpawnEggInfo.SpawnEggColors.egg(3353378, 7042407));
	public static final RegistryObject<EntityType<GundabadOrcArcherEntity>> GUNDABAD_ORC_ARCHER = regNPC("gundabad_orc_archer", GundabadOrcArcherEntity::new, OrcEntity::regAttrs, EntitySizeHolder.orcSize(), SpawnEggInfo.SpawnEggColors.egg(3352346, 8548435));
	public static final RegistryObject<EntityType<UrukArcherEntity>> URUK_ARCHER = regNPC("uruk_archer", UrukArcherEntity::new, UrukEntity::regAttrs, EntitySizeHolder.urukSize(), SpawnEggInfo.SpawnEggColors.egg(2369050, 5790015));
	public static final RegistryObject<EntityType<DwarfArcherEntity>> DWARF_ARCHER = regNPC("dwarf_archer", DwarfArcherEntity::new, DwarfWarriorEntity::regAttrs, EntitySizeHolder.dwarfSize(), SpawnEggInfo.SpawnEggColors.egg(2238506, 7108730));
	public static final RegistryObject<EntityType<BlueDwarfArcherEntity>> BLUE_MOUNTAINS_ARCHER = regNPC("blue_mountains_archer", BlueDwarfArcherEntity::new, BlueDwarfWarriorEntity::regAttrs, EntitySizeHolder.dwarfSize(), SpawnEggInfo.SpawnEggColors.egg(3161673, 6257551));
	public static final RegistryObject<EntityType<WoodElfEntity>> WOOD_ELF = regNPC("wood_elf", WoodElfEntity::new, ElfEntity::regAttrs, EntitySizeHolder.elfSize(), SpawnEggInfo.SpawnEggColors.egg(2314529, 16764574));
	public static final RegistryObject<EntityType<WoodElfWarriorEntity>> WOOD_ELF_WARRIOR = regNPC("wood_elf_warrior", WoodElfWarriorEntity::new, WoodElfWarriorEntity::regAttrs, EntitySizeHolder.elfSize(), SpawnEggInfo.SpawnEggColors.egg(12231576, 5856300));
	public static final RegistryObject<EntityType<BreeManEntity>> BREE_MAN = regNPC("bree_man", BreeManEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(14254950, 6573367));
	public static final RegistryObject<EntityType<BreeHobbitEntity>> BREE_HOBBIT = regNPC("bree_hobbit", BreeHobbitEntity::new, HobbitEntity::regAttrs, EntitySizeHolder.hobbitSize(), SpawnEggInfo.SpawnEggColors.egg(16752511, 8010275));
	public static final RegistryObject<EntityType<BreeGuardEntity>> BREE_GUARD = regNPC("bree_guard", BreeGuardEntity::new, AbstractMannishEntity::regAttrs, EntitySizeHolder.manSize(), SpawnEggInfo.SpawnEggColors.egg(9335640, 3681573));
	public static final RegistryObject<EntityType<GundabadWargEntity>> GUNDABAD_WARG = regNPC("gundabad_warg", GundabadWargEntity::new, WargEntity::regAttrs, EntitySizeHolder.wargSize(), SpawnEggInfo.SpawnEggColors.egg(11706000, 7367272));
	public static final RegistryObject<EntityType<IsengardWargEntity>> ISENGARD_WARG = regNPC("isengard_warg", IsengardWargEntity::new, WargEntity::regAttrs, EntitySizeHolder.wargSize(), SpawnEggInfo.SpawnEggColors.egg(9397581, 5123358));
	public static final RegistryObject<EntityType<MordorWargEntity>> MORDOR_WARG = regNPC("mordor_warg", MordorWargEntity::new, WargEntity::regAttrs, EntitySizeHolder.wargSize(), SpawnEggInfo.SpawnEggColors.egg(4537914, 2037523));
	public static final RegistryObject<EntityType<IsengardSnagaEntity>> ISENGARD_SNAGA = regNPC("isengard_snaga", IsengardSnagaEntity::new, OrcEntity::regAttrs, EntitySizeHolder.orcSize(), SpawnEggInfo.SpawnEggColors.egg(4339500, 8352349));
	public static final RegistryObject<EntityType<IsengardSnagaArcherEntity>> ISENGARD_SNAGA_ARCHER = regNPC("isengard_snaga_archer", IsengardSnagaArcherEntity::new, OrcEntity::regAttrs, EntitySizeHolder.orcSize(), SpawnEggInfo.SpawnEggColors.egg(4339500, 8352349));
	public static final RegistryObject<EntityType<CaracalEntity>> CARACAL = regAnimal("caracal", CaracalEntity::new, CaracalEntity::regAttrs, EntitySizeHolder.size(0.83F, 0.9F), 10, 3, SpawnEggInfo.SpawnEggColors.egg(13806980, 12093272));

	private static void addEntitySpawnEggsToMap(RegistryEvent.Register<EntityType<?>>  event) {
		if (event.getRegistry() == ForgeRegistries.ENTITIES) {
			LOTRSpawnEggItem.afterEntityRegistry();
		}
	}

	public static void createEntitySpawnEggs(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> itemReg = event.getRegistry();
		if (itemReg == ForgeRegistries.ITEMS) {
			DEFERRED_SPAWN_EGGS.forEach(egg -> {
				ResourceLocation entityTypeName = egg.regType.getId();
				ResourceLocation itemName = new ResourceLocation(entityTypeName.getNamespace(), String.format("%s_spawn_egg", entityTypeName.getPath()));
				Item spawnEggItem = new LOTRSpawnEggItem((SpawnEggInfo<?>) egg).setRegistryName(itemName);
				itemReg.register(spawnEggItem);
				LOTRItems.putItemInCreativeTabOrder(itemName);
			});
		}
	}

	private static <T extends AnimalEntity> RegistryObject<EntityType<T>> regAnimal(String key, IFactory<T> factory, Supplier<AttributeModifierMap.MutableAttribute> attribFactory, LOTREntities.EntitySizeHolder size, int trackRange, int updateFreq, LOTREntities.SpawnEggInfo.SpawnEggColors eggColors) {
		return regAnimal(key, factory, attribFactory, size, trackRange, updateFreq, eggColors, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::checkAnimalSpawnRules);
	}

	private static <T extends AnimalEntity> RegistryObject<EntityType<T>> regAnimal(String key, IFactory<T> factory, Supplier<AttributeModifierMap.MutableAttribute> attribFactory, LOTREntities.EntitySizeHolder size, int trackRange, int updateFreq, LOTREntities.SpawnEggInfo.SpawnEggColors eggColors, PlacementType placementType, Type heightmapType, IPlacementPredicate<T> placementPredicate) {
		return regLiving(key, factory, attribFactory, EntityClassification.CREATURE, size, trackRange, updateFreq, true, eggColors, placementType, heightmapType, placementPredicate);
	}

	private static <T extends Entity> RegistryObject<EntityType<T>> regClientsideEntity(String key, IFactory<T> factory, EntityClassification classif, float width, float height) {
		return regEntity(key, factory, classif, width, height, builder -> {
			builder.noSave().noSummon();
		}, builtType -> {
		});
	}

	private static <T extends Entity> RegistryObject<EntityType<T>> regEntity(String key, IFactory<T> factory, EntityClassification classif, float width, float height, Consumer<EntityType.Builder<T>> extraProps, Consumer<EntityType<T>> builtTypeConsumer) {
		return ENTITIES.register(key, () -> {
			Builder<T> builder = Builder.of(factory, classif).sized(width, height);
			extraProps.accept(builder);
			EntityType<T> builtType = builder.build(new ResourceLocation("lotr", key).toString());
			builtTypeConsumer.accept(builtType);
			return builtType;
		});
	}

	private static <T extends Entity> RegistryObject<EntityType<T>> regEntity(String key, IFactory<T> factory, EntityClassification classif, float width, float height, int trackRange, int updateFreq) {
		return regEntity(key, factory, classif, width, height, trackRange, updateFreq, builder -> {
		});
	}

	private static <T extends Entity> RegistryObject<EntityType<T>> regEntity(String key, IFactory<T> factory, EntityClassification classif, float width, float height, int trackRange, int updateFreq, Consumer<EntityType.Builder<T>> extraProps) {
		return regEntity(key, factory, classif, width, height, extraProps.andThen(builder -> {
			builder.setTrackingRange(trackRange);
			builder.setUpdateInterval(updateFreq);
		}), builtType -> {
		});
	}

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ENTITIES.register(bus);
		// bus.addGenericListener(EntityType.class,
		// VanillaEntitySpawnChanges::makeChanges);
		bus.addGenericListener(EntityType.class, LOTREntities::addEntitySpawnEggsToMap);
	}

	public static void registerEntityTypeAttributes(EntityAttributeCreationEvent event) {
	    for (Map.Entry<EntityType<? extends LivingEntity>, Supplier<AttributeModifierMap.MutableAttribute>> entry : ENTITY_ATTRIBUTE_FACTORIES.entrySet()) {
	      EntityType<? extends LivingEntity> type = entry.getKey();
	      Supplier<AttributeModifierMap.MutableAttribute> attributeFactory = entry.getValue();
	      event.put(type, ((AttributeModifierMap.MutableAttribute)attributeFactory.get()).build());
	    } 
	  }

	private static <T extends MobEntity> RegistryObject<EntityType<T>> regLiving(String key, IFactory<T> factory, Supplier<AttributeModifierMap.MutableAttribute> attribFactory, EntityClassification classif, LOTREntities.EntitySizeHolder size, int trackRange, int updateFreq, boolean velUpdates, LOTREntities.SpawnEggInfo.SpawnEggColors eggColors, PlacementType placementType, Type heightmapType, IPlacementPredicate<T> placementPredicate) {
		RegistryObject<EntityType<T>> regType = regEntity(key, factory, classif, size.width, size.height, builder -> {
			builder.setTrackingRange(trackRange);
			builder.setUpdateInterval(updateFreq);
			builder.setShouldReceiveVelocityUpdates(velUpdates);
		}, builtType -> {
			ENTITY_ATTRIBUTE_FACTORIES.put(builtType, attribFactory);
			EntitySpawnPlacementRegistry.register(builtType, placementType, heightmapType, placementPredicate);
		});
		DEFERRED_SPAWN_EGGS.add(new LOTREntities.SpawnEggInfo<>(regType, eggColors));
		return regType;
	}

	private static <T extends lotr.common.entity.npc.NPCEntity> RegistryObject<EntityType<T>> regNPC(String key, EntityType.IFactory<T> factory, Supplier<AttributeModifierMap.MutableAttribute> attribFactory, LOTREntities.EntitySizeHolder size, LOTREntities.SpawnEggInfo.SpawnEggColors eggColors) {
		return regLiving(key, factory, attribFactory, LOTREntityClassifications.NPC, size, 10, 3, true, eggColors, PlacementType.NO_RESTRICTIONS, Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::checkMobSpawnRules);
	}

	public static class EntitySizeHolder {
		public final float width;
		public final float height;

		private EntitySizeHolder(float w, float h) {
			width = w;
			height = h;
		}

		public static LOTREntities.EntitySizeHolder dwarfSize() {
			return size(0.474F, 1.422F);
		}

		public static LOTREntities.EntitySizeHolder elfSize() {
			return size(0.6F, 1.9125F);
		}

		public static LOTREntities.EntitySizeHolder hobbitSize() {
			return size(0.39F, 1.04F);
		}

		public static LOTREntities.EntitySizeHolder manSize() {
			return size(0.6F, 1.8F);
		}

		public static LOTREntities.EntitySizeHolder orcSize() {
			return size(0.471F, 1.459F);
		}

		public static LOTREntities.EntitySizeHolder size(float w, float h) {
			return new LOTREntities.EntitySizeHolder(w, h);
		}

		public static LOTREntities.EntitySizeHolder urukSize() {
			return manSize();
		}

		public static LOTREntities.EntitySizeHolder wargSize() {
			return size(1.4F, 1.5F);
		}
	}

	public static class SpawnEggInfo <T extends Entity>{
		public final RegistryObject<? extends EntityType<T>> regType;
		public final LOTREntities.SpawnEggInfo.SpawnEggColors colors;

		public SpawnEggInfo(RegistryObject<? extends EntityType<T>> regType, LOTREntities.SpawnEggInfo.SpawnEggColors colors) {
			this.regType = regType;
			this.colors = colors;
		}

		public static class SpawnEggColors {
			public final int primaryColor;
			public final int secondaryColor;

			public SpawnEggColors(int primary, int secondary) {
				primaryColor = primary;
				secondaryColor = secondary;
			}

			public static LOTREntities.SpawnEggInfo.SpawnEggColors egg(int primary, int secondary) {
				return new LOTREntities.SpawnEggInfo.SpawnEggColors(primary, secondary);
			}
		}
	}
}
