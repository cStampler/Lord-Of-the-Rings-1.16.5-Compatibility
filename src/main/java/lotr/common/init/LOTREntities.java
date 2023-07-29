package lotr.common.init;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;

import lotr.common.entity.animal.CaracalEntity;
import lotr.common.entity.item.*;
import lotr.common.entity.misc.AlignmentBonusEntity;
import lotr.common.entity.npc.*;
import lotr.common.entity.projectile.*;
import lotr.common.item.LOTRSpawnEggItem;
import net.minecraft.entity.*;
import net.minecraft.entity.EntitySpawnPlacementRegistry.*;
import net.minecraft.entity.EntityType.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

public class LOTREntities {
	public static final DeferredRegister ENTITIES;
	private static final Map ENTITY_ATTRIBUTE_FACTORIES;
	private static final List DEFERRED_SPAWN_EGGS;
	public static final RegistryObject RING_PORTAL;
	public static final RegistryObject THROWN_PLATE;
	public static final RegistryObject BOAT;
	public static final RegistryObject FALLING_TREASURE_BLOCK;
	public static final RegistryObject SMOKE_RING;
	public static final RegistryObject ALIGNMENT_BONUS;
	public static final RegistryObject SPEAR;
	public static final RegistryObject HOBBIT;
	public static final RegistryObject MORDOR_ORC;
	public static final RegistryObject GONDOR_MAN;
	public static final RegistryObject GALADHRIM_ELF;
	public static final RegistryObject GONDOR_SOLDIER;
	public static final RegistryObject DWARF;
	public static final RegistryObject DWARF_WARRIOR;
	public static final RegistryObject GALADHRIM_WARRIOR;
	public static final RegistryObject URUK;
	public static final RegistryObject ROHAN_MAN;
	public static final RegistryObject ROHIRRIM_WARRIOR;
	public static final RegistryObject GUNDABAD_ORC;
	public static final RegistryObject DALE_MAN;
	public static final RegistryObject DALE_SOLDIER;
	public static final RegistryObject DUNLENDING;
	public static final RegistryObject DUNLENDING_WARRIOR;
	public static final RegistryObject LINDON_ELF;
	public static final RegistryObject LINDON_WARRIOR;
	public static final RegistryObject RIVENDELL_ELF;
	public static final RegistryObject RIVENDELL_WARRIOR;
	public static final RegistryObject COAST_SOUTHRON;
	public static final RegistryObject COAST_SOUTHRON_WARRIOR;
	public static final RegistryObject HARNEDHRIM;
	public static final RegistryObject HARNENNOR_WARRIOR;
	public static final RegistryObject BLUE_MOUNTAINS_DWARF;
	public static final RegistryObject BLUE_MOUNTAINS_WARRIOR;
	public static final RegistryObject UMBAR_MAN;
	public static final RegistryObject UMBAR_SOLDIER;
	public static final RegistryObject GONDOR_ARCHER;
	public static final RegistryObject ROHIRRIM_BOWMAN;
	public static final RegistryObject DALE_BOWMAN;
	public static final RegistryObject DUNLENDING_BOWMAN;
	public static final RegistryObject COAST_SOUTHRON_ARCHER;
	public static final RegistryObject HARNENNOR_ARCHER;
	public static final RegistryObject UMBAR_ARCHER;
	public static final RegistryObject MORDOR_ORC_ARCHER;
	public static final RegistryObject GUNDABAD_ORC_ARCHER;
	public static final RegistryObject URUK_ARCHER;
	public static final RegistryObject DWARF_ARCHER;
	public static final RegistryObject BLUE_MOUNTAINS_ARCHER;
	public static final RegistryObject WOOD_ELF;
	public static final RegistryObject WOOD_ELF_WARRIOR;
	public static final RegistryObject BREE_MAN;
	public static final RegistryObject BREE_HOBBIT;
	public static final RegistryObject BREE_GUARD;
	public static final RegistryObject GUNDABAD_WARG;
	public static final RegistryObject ISENGARD_WARG;
	public static final RegistryObject MORDOR_WARG;
	public static final RegistryObject ISENGARD_SNAGA;
	public static final RegistryObject ISENGARD_SNAGA_ARCHER;
	public static final RegistryObject CARACAL;

	static {
		ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, "lotr");
		ENTITY_ATTRIBUTE_FACTORIES = new HashMap();
		DEFERRED_SPAWN_EGGS = new ArrayList();
		RING_PORTAL = regEntity("ring_portal", RingPortalEntity::new, EntityClassification.MISC, 3.0F, 1.5F, 10, 3);
		THROWN_PLATE = regEntity("thrown_plate", ThrownPlateEntity::new, EntityClassification.MISC, 0.5F, 0.5F, 4, 10);
		BOAT = regEntity("boat", LOTRBoatEntity::new, EntityClassification.MISC, 1.375F, 0.5625F, 10, 3);
		FALLING_TREASURE_BLOCK = regEntity("falling_treasure_block", FallingTreasureBlockEntity::new, EntityClassification.MISC, 0.98F, 0.98F, 10, 20);
		SMOKE_RING = regEntity("smoke_ring", SmokeRingEntity::new, EntityClassification.MISC, 0.5F, 0.5F, 4, 10, hummel -> ((Builder) hummel).fireImmune());
		ALIGNMENT_BONUS = regClientsideEntity("alignment_bonus", AlignmentBonusEntity::new, EntityClassification.MISC, 0.5F, 0.5F);
		SPEAR = regEntity("spear", SpearEntity::new, EntityClassification.MISC, 0.5F, 0.5F, 4, 20);
		HOBBIT = regNPC("hobbit", HobbitEntity::new, HobbitEntity::regAttrs, LOTREntities.EntitySizeHolder.hobbitSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(16752511, 8010275));
		MORDOR_ORC = regNPC("mordor_orc", MordorOrcEntity::new, MordorOrcEntity::regAttrs, LOTREntities.EntitySizeHolder.orcSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(3353378, 7042407));
		GONDOR_MAN = regNPC("gondor_man", GondorManEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(13547685, 5652538));
		GALADHRIM_ELF = regNPC("galadhrim_elf", GaladhrimElfEntity::new, ElfEntity::regAttrs, LOTREntities.EntitySizeHolder.elfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(9337185, 15920555));
		GONDOR_SOLDIER = regNPC("gondor_soldier", GondorSoldierEntity::new, GondorSoldierEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(5327948, 15063770));
		DWARF = regNPC("dwarf", DwarfEntity::new, DwarfEntity::regAttrs, LOTREntities.EntitySizeHolder.dwarfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(16353133, 15357472));
		DWARF_WARRIOR = regNPC("dwarf_warrior", DwarfWarriorEntity::new, DwarfWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.dwarfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(2238506, 7108730));
		GALADHRIM_WARRIOR = regNPC("galadhrim_warrior", GaladhrimWarriorEntity::new, GaladhrimWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.elfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(12697274, 15382870));
		URUK = regNPC("uruk", UrukEntity::new, UrukEntity::regAttrs, LOTREntities.EntitySizeHolder.urukSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(2369050, 5790015));
		ROHAN_MAN = regNPC("rohan_man", RohanManEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(16424833, 13406801));
		ROHIRRIM_WARRIOR = regNPC("rohirrim_warrior", RohirrimWarriorEntity::new, RohirrimWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(5524296, 13546384));
		GUNDABAD_ORC = regNPC("gundabad_orc", GundabadOrcEntity::new, OrcEntity::regAttrs, LOTREntities.EntitySizeHolder.orcSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(3352346, 8548435));
		DALE_MAN = regNPC("dale_man", DaleManEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(16755851, 5252113));
		DALE_SOLDIER = regNPC("dale_soldier", DaleSoldierEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(12034458, 480127));
		DUNLENDING = regNPC("dunlending", DunlendingEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(15897714, 3679258));
		DUNLENDING_WARRIOR = regNPC("dunlending_warrior", DunlendingWarriorEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(5192753, 9337975));
		LINDON_ELF = regNPC("lindon_elf", LindonElfEntity::new, ElfEntity::regAttrs, LOTREntities.EntitySizeHolder.elfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(16761223, 15721387));
		LINDON_WARRIOR = regNPC("lindon_warrior", LindonWarriorEntity::new, LindonWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.elfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(14935016, 7040410));
		RIVENDELL_ELF = regNPC("rivendell_elf", RivendellElfEntity::new, ElfEntity::regAttrs, LOTREntities.EntitySizeHolder.elfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(16761223, 15721387));
		RIVENDELL_WARRIOR = regNPC("rivendell_warrior", RivendellWarriorEntity::new, RivendellWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.elfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(14738662, 10723248));
		COAST_SOUTHRON = regNPC("coast_southron", CoastSouthronEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(10779229, 2960685));
		COAST_SOUTHRON_WARRIOR = regNPC("coast_southron_warrior", CoastSouthronWarriorEntity::new, CoastSouthronWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(2171169, 11868955));
		HARNEDHRIM = regNPC("harnedhrim", HarnedhrimEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(9854777, 1181187));
		HARNENNOR_WARRIOR = regNPC("harnennor_warrior", HarnennorWarriorEntity::new, HarnennorWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(7016721, 14852422));
		BLUE_MOUNTAINS_DWARF = regNPC("blue_mountains_dwarf", BlueDwarfEntity::new, DwarfEntity::regAttrs, LOTREntities.EntitySizeHolder.dwarfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(16353133, 15357472));
		BLUE_MOUNTAINS_WARRIOR = regNPC("blue_mountains_warrior", BlueDwarfWarriorEntity::new, BlueDwarfWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.dwarfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(3161673, 6257551));
		UMBAR_MAN = regNPC("umbar_man", UmbarManEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(10779229, 2960685));
		UMBAR_SOLDIER = regNPC("umbar_soldier", UmbarSoldierEntity::new, UmbarSoldierEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(2960680, 13540692));
		GONDOR_ARCHER = regNPC("gondor_archer", GondorArcherEntity::new, GondorSoldierEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(5327948, 15063770));
		ROHIRRIM_BOWMAN = regNPC("rohirrim_bowman", RohirrimBowmanEntity::new, RohirrimWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(5524296, 13546384));
		DALE_BOWMAN = regNPC("dale_bowman", DaleBowmanEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(12034458, 480127));
		DUNLENDING_BOWMAN = regNPC("dunlending_bowman", DunlendingBowmanEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(5192753, 9337975));
		COAST_SOUTHRON_ARCHER = regNPC("coast_southron_archer", CoastSouthronArcherEntity::new, CoastSouthronWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(2171169, 11868955));
		HARNENNOR_ARCHER = regNPC("harnennor_archer", HarnennorArcherEntity::new, HarnennorWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(7016721, 14852422));
		UMBAR_ARCHER = regNPC("umbar_archer", UmbarArcherEntity::new, UmbarSoldierEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(2960680, 13540692));
		MORDOR_ORC_ARCHER = regNPC("mordor_orc_archer", MordorOrcArcherEntity::new, MordorOrcEntity::regAttrs, LOTREntities.EntitySizeHolder.orcSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(3353378, 7042407));
		GUNDABAD_ORC_ARCHER = regNPC("gundabad_orc_archer", GundabadOrcArcherEntity::new, OrcEntity::regAttrs, LOTREntities.EntitySizeHolder.orcSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(3352346, 8548435));
		URUK_ARCHER = regNPC("uruk_archer", UrukArcherEntity::new, UrukEntity::regAttrs, LOTREntities.EntitySizeHolder.urukSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(2369050, 5790015));
		DWARF_ARCHER = regNPC("dwarf_archer", DwarfArcherEntity::new, DwarfWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.dwarfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(2238506, 7108730));
		BLUE_MOUNTAINS_ARCHER = regNPC("blue_mountains_archer", BlueDwarfArcherEntity::new, BlueDwarfWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.dwarfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(3161673, 6257551));
		WOOD_ELF = regNPC("wood_elf", WoodElfEntity::new, ElfEntity::regAttrs, LOTREntities.EntitySizeHolder.elfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(2314529, 16764574));
		WOOD_ELF_WARRIOR = regNPC("wood_elf_warrior", WoodElfWarriorEntity::new, WoodElfWarriorEntity::regAttrs, LOTREntities.EntitySizeHolder.elfSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(12231576, 5856300));
		BREE_MAN = regNPC("bree_man", BreeManEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(14254950, 6573367));
		BREE_HOBBIT = regNPC("bree_hobbit", BreeHobbitEntity::new, HobbitEntity::regAttrs, LOTREntities.EntitySizeHolder.hobbitSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(16752511, 8010275));
		BREE_GUARD = regNPC("bree_guard", BreeGuardEntity::new, AbstractMannishEntity::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(9335640, 3681573));
		GUNDABAD_WARG = regNPC("gundabad_warg", GundabadWargEntity::new, WargEntity::regAttrs, LOTREntities.EntitySizeHolder.wargSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(11706000, 7367272));
		ISENGARD_WARG = regNPC("isengard_warg", IsengardWargEntity::new, WargEntity::regAttrs, LOTREntities.EntitySizeHolder.wargSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(9397581, 5123358));
		MORDOR_WARG = regNPC("mordor_warg", MordorWargEntity::new, WargEntity::regAttrs, LOTREntities.EntitySizeHolder.wargSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(4537914, 2037523));
		ISENGARD_SNAGA = regNPC("isengard_snaga", IsengardSnagaEntity::new, OrcEntity::regAttrs, LOTREntities.EntitySizeHolder.orcSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(4339500, 8352349));
		ISENGARD_SNAGA_ARCHER = regNPC("isengard_snaga_archer", IsengardSnagaArcherEntity::new, OrcEntity::regAttrs, LOTREntities.EntitySizeHolder.orcSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(4339500, 8352349));
		CARACAL = regAnimal("caracal", CaracalEntity::new, CaracalEntity::regAttrs, LOTREntities.EntitySizeHolder.size(0.83F, 0.9F), 10, 3, LOTREntities.SpawnEggInfo.SpawnEggColors.egg(13806980, 12093272));
	}

	private static void addEntitySpawnEggsToMap(RegistryEvent.Register<EntityType<?>>  event) {
		if (event.getRegistry() == ForgeRegistries.ENTITIES) {
			LOTRSpawnEggItem.afterEntityRegistry();
		}
	}

	public static void createEntitySpawnEggs(RegistryEvent.Register<Item> event) {
		IForgeRegistry itemReg = event.getRegistry();
		if (itemReg == ForgeRegistries.ITEMS) {
			DEFERRED_SPAWN_EGGS.forEach(egg -> {
				ResourceLocation entityTypeName = ((SpawnEggInfo) egg).regType.getId();
				ResourceLocation itemName = new ResourceLocation(entityTypeName.getNamespace(), String.format("%s_spawn_egg", entityTypeName.getPath()));
				Item spawnEggItem = new LOTRSpawnEggItem((SpawnEggInfo) egg).setRegistryName(itemName);
				itemReg.register(spawnEggItem);
				LOTRItems.putItemInCreativeTabOrder(itemName);
			});
		}
	}

	private static RegistryObject regAnimal(String key, IFactory factory, Supplier attribFactory, LOTREntities.EntitySizeHolder size, int trackRange, int updateFreq, LOTREntities.SpawnEggInfo.SpawnEggColors eggColors) {
		return regAnimal(key, factory, attribFactory, size, trackRange, updateFreq, eggColors, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::checkAnimalSpawnRules);
	}

	private static RegistryObject regAnimal(String key, IFactory factory, Supplier attribFactory, LOTREntities.EntitySizeHolder size, int trackRange, int updateFreq, LOTREntities.SpawnEggInfo.SpawnEggColors eggColors, PlacementType placementType, Type heightmapType, IPlacementPredicate placementPredicate) {
		return regLiving(key, factory, attribFactory, EntityClassification.CREATURE, size, trackRange, updateFreq, true, eggColors, placementType, heightmapType, placementPredicate);
	}

	private static RegistryObject regClientsideEntity(String key, IFactory factory, EntityClassification classif, float width, float height) {
		return regEntity(key, factory, classif, width, height, builder -> {
			((Builder) builder).noSave().noSummon();
		}, builtType -> {
		});
	}

	private static RegistryObject regEntity(String key, IFactory factory, EntityClassification classif, float width, float height, Consumer extraProps, Consumer builtTypeConsumer) {
		return ENTITIES.register(key, () -> {
			Builder builder = Builder.of(factory, classif).sized(width, height);
			extraProps.accept(builder);
			EntityType builtType = builder.build(new ResourceLocation("lotr", key).toString());
			builtTypeConsumer.accept(builtType);
			return builtType;
		});
	}

	private static RegistryObject regEntity(String key, IFactory factory, EntityClassification classif, float width, float height, int trackRange, int updateFreq) {
		return regEntity(key, factory, classif, width, height, trackRange, updateFreq, builder -> {
		});
	}

	private static RegistryObject regEntity(String key, IFactory factory, EntityClassification classif, float width, float height, int trackRange, int updateFreq, Consumer extraProps) {
		return regEntity(key, factory, classif, width, height, extraProps.andThen(builder -> {
			((Builder) builder).setTrackingRange(trackRange);
			((Builder) builder).setUpdateInterval(updateFreq);
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
		Iterator var1 = ENTITY_ATTRIBUTE_FACTORIES.entrySet().iterator();

		while (var1.hasNext()) {
			Entry entry = (Entry) var1.next();
			EntityType type = (EntityType) entry.getKey();
			Supplier attributeFactory = (Supplier) entry.getValue();
			event.put(type, ((MutableAttribute) attributeFactory.get()).build());
		}

	}

	private static RegistryObject regLiving(String key, IFactory factory, Supplier attribFactory, EntityClassification classif, LOTREntities.EntitySizeHolder size, int trackRange, int updateFreq, boolean velUpdates, LOTREntities.SpawnEggInfo.SpawnEggColors eggColors, PlacementType placementType, Type heightmapType, IPlacementPredicate placementPredicate) {
		RegistryObject regType = regEntity(key, factory, classif, size.width, size.height, builder -> {
			((Builder) builder).setTrackingRange(trackRange);
			((Builder) builder).setUpdateInterval(updateFreq);
			((Builder) builder).setShouldReceiveVelocityUpdates(velUpdates);
		}, builtType -> {
			ENTITY_ATTRIBUTE_FACTORIES.put(builtType, attribFactory);
			EntitySpawnPlacementRegistry.register((EntityType) builtType, placementType, heightmapType, placementPredicate);
		});
		DEFERRED_SPAWN_EGGS.add(new LOTREntities.SpawnEggInfo(regType, eggColors));
		return regType;
	}

	private static RegistryObject regNPC(String key, IFactory factory, Supplier attribFactory, LOTREntities.EntitySizeHolder size, LOTREntities.SpawnEggInfo.SpawnEggColors eggColors) {
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

	public static class SpawnEggInfo {
		public final RegistryObject regType;
		public final LOTREntities.SpawnEggInfo.SpawnEggColors colors;

		public SpawnEggInfo(RegistryObject regType, LOTREntities.SpawnEggInfo.SpawnEggColors colors) {
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
