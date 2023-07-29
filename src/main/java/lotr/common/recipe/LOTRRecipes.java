package lotr.common.recipe;

import java.util.*;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.crafting.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.crafting.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

public class LOTRRecipes {
	public static IIngredientSerializer DYNAMIC_INGREDIENT_SERIALIZER;
	public static IIngredientSerializer UNDAMAGED_INGREDIENT_SERIALIZER;
	private static final List ALL_INDIVIDUAL_FACTION_TYPES = new ArrayList();
	public static FactionTableType GONDOR_CRAFTING;
	public static FactionTableType MORDOR_CRAFTING;
	public static FactionTableType ROHAN_CRAFTING;
	public static FactionTableType DWARVEN_CRAFTING;
	public static FactionTableType LINDON_CRAFTING;
	public static FactionTableType RIVENDELL_CRAFTING;
	public static FactionTableType GALADHRIM_CRAFTING;
	public static FactionTableType WOOD_ELVEN_CRAFTING;
	public static FactionTableType HARAD_CRAFTING;
	public static FactionTableType UMBAR_CRAFTING;
	public static FactionTableType URUK_CRAFTING;
	public static FactionTableType HOBBIT_CRAFTING;
	public static FactionTableType BLUE_MOUNTAINS_CRAFTING;
	public static FactionTableType RANGER_CRAFTING;
	public static FactionTableType DOL_AMROTH_CRAFTING;
	public static FactionTableType ANGMAR_CRAFTING;
	public static FactionTableType DORWINION_CRAFTING;
	public static FactionTableType DALE_CRAFTING;
	public static FactionTableType LOSSOTH_CRAFTING;
	public static FactionTableType DUNLENDING_CRAFTING;
	public static FactionTableType BREE_CRAFTING;
	public static MultiTableType ANY_FACTION;
	public static MultiTableType ANY_DUNEDAIN;
	public static MultiTableType ANY_NUMENOREAN;
	public static MultiTableType ANY_HOBBIT;
	public static MultiTableType ANY_HARAD;
	public static MultiTableType ANY_ELVEN;
	public static MultiTableType ANY_HIGH_ELVEN;
	public static MultiTableType ANY_DWARVEN;
	public static MultiTableType ANY_ORC;
	public static MultiTableType ANY_MORGUL;
	public static IRecipeType ALLOY_FORGE;
	public static IRecipeType DWARVEN_FORGE;
	public static IRecipeType DWARVEN_FORGE_ALLOY;
	public static IRecipeType ELVEN_FORGE;
	public static IRecipeType ELVEN_FORGE_ALLOY;
	public static IRecipeType ORC_FORGE;
	public static IRecipeType ORC_FORGE_ALLOY;
	public static IRecipeType HOBBIT_OVEN;
	public static IRecipeType HOBBIT_OVEN_ALLOY;
	public static IRecipeType DRINK_BREWING;
	public static final DeferredRegister RECIPE_SERIALIZERS;
	public static final RegistryObject<IRecipeSerializer<FactionShapedRecipe>> FACTION_SHAPED;
	public static final RegistryObject<IRecipeSerializer<FactionShapelessRecipe>> FACTION_SHAPELESS;
	public static final RegistryObject<IRecipeSerializer<VesselDrinkShapelessRecipe>> VESSEL_DRINK_SHAPELESS;
	public static final RegistryObject<IRecipeSerializer<SmokingPipeColoringRecipe>> CRAFTING_SPECIAL_SMOKING_PIPE_COLORING;
	public static final RegistryObject<IRecipeSerializer<PouchRecipe>> CRAFTING_SPECIAL_POUCH;
	public static final RegistryObject<IRecipeSerializer<AbstractAlloyForgeRecipe>> ALLOY_SERIALIZER;
	public static final RegistryObject<IRecipeSerializer<LOTRAbstractCookingRecipe>> DWARVEN_FORGE_SERIALIZER;
	public static final RegistryObject<IRecipeSerializer<AbstractAlloyForgeRecipe>> DWARVEN_FORGE_ALLOY_SERIALIZER;
	public static final RegistryObject<IRecipeSerializer<LOTRAbstractCookingRecipe>> ELVEN_FORGE_SERIALIZER;
	public static final RegistryObject<IRecipeSerializer<AbstractAlloyForgeRecipe>> ELVEN_FORGE_ALLOY_SERIALIZER;
	public static final RegistryObject<IRecipeSerializer<LOTRAbstractCookingRecipe>> ORC_FORGE_SERIALIZER;
	public static final RegistryObject<IRecipeSerializer<AbstractAlloyForgeRecipe>> ORC_FORGE_ALLOY_SERIALIZER;
	public static final RegistryObject<IRecipeSerializer<LOTRAbstractCookingRecipe>> HOBBIT_OVEN_SERIALIZER;
	public static final RegistryObject<IRecipeSerializer<AbstractAlloyForgeRecipe>> HOBBIT_OVEN_ALLOY_SERIALIZER;
	public static final RegistryObject<IRecipeSerializer<DrinkBrewingRecipe>> DRINK_BREWING_SERIALIZER;

	static {
		RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "lotr");

		FACTION_SHAPED = RECIPE_SERIALIZERS.register("faction_shaped", FactionShapedRecipe.Serializer::new);
		FACTION_SHAPELESS = RECIPE_SERIALIZERS.register("faction_shapeless", FactionShapelessRecipe.Serializer::new);
		VESSEL_DRINK_SHAPELESS = RECIPE_SERIALIZERS.register("vessel_drink_shapeless", VesselDrinkShapelessRecipe.Serializer::new);
		CRAFTING_SPECIAL_SMOKING_PIPE_COLORING = RECIPE_SERIALIZERS.register("crafting_special_smoking_pipe_coloring", () -> new SpecialRecipeSerializer<>(SmokingPipeColoringRecipe::new));
		CRAFTING_SPECIAL_POUCH = RECIPE_SERIALIZERS.register("crafting_special_pouch", PouchRecipe.Serializer::new);
		ALLOY_SERIALIZER = RECIPE_SERIALIZERS.register("alloy_forge", () -> new AlloyForgeRecipeSerializer(AlloyForgeRecipe::new, 200));
		DWARVEN_FORGE_SERIALIZER = RECIPE_SERIALIZERS.register("dwarven_forge", () -> new LOTRCookingRecipeSerializer<>(DwarvenForgeRecipe::new, 200));
		DWARVEN_FORGE_ALLOY_SERIALIZER = RECIPE_SERIALIZERS.register("dwarven_forge_alloy", () -> new AlloyForgeRecipeSerializer(DwarvenForgeAlloyRecipe::new, 200));
		ELVEN_FORGE_SERIALIZER = RECIPE_SERIALIZERS.register("elven_forge", () -> new LOTRCookingRecipeSerializer<>(ElvenForgeRecipe::new, 200));
		ELVEN_FORGE_ALLOY_SERIALIZER = RECIPE_SERIALIZERS.register("elven_forge_alloy", () -> new AlloyForgeRecipeSerializer(ElvenForgeAlloyRecipe::new, 200));
		ORC_FORGE_SERIALIZER = RECIPE_SERIALIZERS.register("orc_forge", () -> new LOTRCookingRecipeSerializer<>(OrcForgeRecipe::new, 200));
		ORC_FORGE_ALLOY_SERIALIZER = RECIPE_SERIALIZERS.register("orc_forge_alloy", () -> new AlloyForgeRecipeSerializer(OrcForgeAlloyRecipe::new, 200));
		HOBBIT_OVEN_SERIALIZER = RECIPE_SERIALIZERS.register("hobbit_oven", () -> new LOTRCookingRecipeSerializer<>(HobbitOvenRecipe::new, 200));
		HOBBIT_OVEN_ALLOY_SERIALIZER = RECIPE_SERIALIZERS.register("hobbit_oven_alloy", () -> new AlloyForgeRecipeSerializer(HobbitOvenAlloyRecipe::new, 200));
		DRINK_BREWING_SERIALIZER = RECIPE_SERIALIZERS.register("brewing", () -> new DrinkBrewingRecipeSerializer(12000));

	}

	@Nullable
	public static IRecipeType findRecipeTypeByName(ResourceLocation id) {
		return Registry.RECIPE_TYPE.get(id);
	}

	@Nullable
	public static IRecipeType findRecipeTypeByName(String s) {
		return findRecipeTypeByName(new ResourceLocation(s));
	}

	public static IRecipeType findRecipeTypeByNameOrThrow(ResourceLocation id, Class desiredClass) {
		IRecipeType type = findRecipeTypeByName(id);
		if (!desiredClass.isInstance(type)) {
			throw new IllegalArgumentException(String.format("Recipe type for '%s' (found instance %s) is not an instance of the desired class '%s'", id, type, desiredClass));
		}
		return (IRecipeType) desiredClass.cast(type);
	}

	public static IRecipeType findRecipeTypeByNameOrThrow(String s, Class desiredClass) {
		return findRecipeTypeByNameOrThrow(new ResourceLocation(s), desiredClass);
	}

	public static String findRecipeTypeName(IRecipeType type) {
		return Registry.RECIPE_TYPE.getKey(type).toString();
	}

	public static void register() {
		DYNAMIC_INGREDIENT_SERIALIZER = CraftingHelper.register(new ResourceLocation("lotr", "dynamic_ingredient"), new DynamicIngredient.Serializer());
		UNDAMAGED_INGREDIENT_SERIALIZER = CraftingHelper.register(new ResourceLocation("lotr", "undamaged_ingredient"), new UndamagedIngredient.Serializer());
		GONDOR_CRAFTING = registerFaction("gondor", LOTRBlocks.GONDOR_CRAFTING_TABLE);
		MORDOR_CRAFTING = registerFaction("mordor", LOTRBlocks.MORDOR_CRAFTING_TABLE);
		ROHAN_CRAFTING = registerFaction("rohan", LOTRBlocks.ROHAN_CRAFTING_TABLE);
		DWARVEN_CRAFTING = registerFaction("dwarven", LOTRBlocks.DWARVEN_CRAFTING_TABLE);
		LINDON_CRAFTING = registerFaction("lindon", LOTRBlocks.LINDON_CRAFTING_TABLE);
		RIVENDELL_CRAFTING = registerFaction("rivendell", LOTRBlocks.RIVENDELL_CRAFTING_TABLE);
		GALADHRIM_CRAFTING = registerFaction("galadhrim", LOTRBlocks.GALADHRIM_CRAFTING_TABLE);
		WOOD_ELVEN_CRAFTING = registerFaction("wood_elven", LOTRBlocks.WOOD_ELVEN_CRAFTING_TABLE);
		HARAD_CRAFTING = registerFaction("harad", LOTRBlocks.HARAD_CRAFTING_TABLE);
		UMBAR_CRAFTING = registerFaction("umbar", LOTRBlocks.UMBAR_CRAFTING_TABLE);
		URUK_CRAFTING = registerFaction("uruk", LOTRBlocks.URUK_CRAFTING_TABLE);
		HOBBIT_CRAFTING = registerFaction("hobbit", LOTRBlocks.HOBBIT_CRAFTING_TABLE);
		BLUE_MOUNTAINS_CRAFTING = registerFaction("blue_mountains", LOTRBlocks.BLUE_MOUNTAINS_CRAFTING_TABLE);
		RANGER_CRAFTING = registerFaction("ranger", LOTRBlocks.RANGER_CRAFTING_TABLE);
		DOL_AMROTH_CRAFTING = registerFaction("dol_amroth", LOTRBlocks.DOL_AMROTH_CRAFTING_TABLE);
		ANGMAR_CRAFTING = registerFaction("angmar", LOTRBlocks.ANGMAR_CRAFTING_TABLE);
		DORWINION_CRAFTING = registerFaction("dorwinion", LOTRBlocks.DORWINION_CRAFTING_TABLE);
		DALE_CRAFTING = registerFaction("dale", LOTRBlocks.DALE_CRAFTING_TABLE);
		LOSSOTH_CRAFTING = registerFaction("lossoth", LOTRBlocks.LOSSOTH_CRAFTING_TABLE);
		DUNLENDING_CRAFTING = registerFaction("dunlending", LOTRBlocks.DUNLENDING_CRAFTING_TABLE);
		BREE_CRAFTING = registerFaction("bree", LOTRBlocks.BREE_CRAFTING_TABLE);
		ANY_FACTION = registerMulti("any_faction", ALL_INDIVIDUAL_FACTION_TYPES);
		ANY_DUNEDAIN = registerMulti("any_dunedain", GONDOR_CRAFTING, RANGER_CRAFTING, DOL_AMROTH_CRAFTING);
		ANY_NUMENOREAN = registerMulti("any_numenorean", GONDOR_CRAFTING, UMBAR_CRAFTING, DOL_AMROTH_CRAFTING);
		ANY_HOBBIT = registerMulti("any_hobbit", HOBBIT_CRAFTING, BREE_CRAFTING);
		ANY_HARAD = registerMulti("any_harad", HARAD_CRAFTING, UMBAR_CRAFTING);
		ANY_ELVEN = registerMulti("any_elven", LINDON_CRAFTING, RIVENDELL_CRAFTING, GALADHRIM_CRAFTING, WOOD_ELVEN_CRAFTING);
		ANY_HIGH_ELVEN = registerMulti("any_high_elven", LINDON_CRAFTING, RIVENDELL_CRAFTING);
		ANY_DWARVEN = registerMulti("any_dwarven", DWARVEN_CRAFTING, BLUE_MOUNTAINS_CRAFTING);
		ANY_ORC = registerMulti("any_orc", MORDOR_CRAFTING, URUK_CRAFTING, ANGMAR_CRAFTING);
		ANY_MORGUL = registerMulti("any_morgul", MORDOR_CRAFTING, ANGMAR_CRAFTING);
		"The above registry names are the 'table' in the recipe .json files".length();
		ALLOY_FORGE = register("alloy_forge");
		DWARVEN_FORGE = register("dwarven_forge");
		DWARVEN_FORGE_ALLOY = register("dwarven_forge_alloy");
		ELVEN_FORGE = register("elven_forge");
		ELVEN_FORGE_ALLOY = register("elven_forge_alloy");
		ORC_FORGE = register("orc_forge");
		ORC_FORGE_ALLOY = register("orc_forge_alloy");
		HOBBIT_OVEN = register("hobbit_oven");
		HOBBIT_OVEN_ALLOY = register("hobbit_oven_alloy");
		DRINK_BREWING = register("brewing");
		RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	private static <T extends IRecipe<?>> IRecipeType<T> register(String key) {
		final ResourceLocation res = new ResourceLocation("lotr", key);
		return (IRecipeType) Registry.register((Registry) Registry.RECIPE_TYPE, res, (Object) new IRecipeType<T>() {

			@Override
			public String toString() {
				return res.toString();
			}
		});
	}

	private static FactionTableType registerFaction(String s, Supplier<Block> blockSup) {
		ResourceLocation res = new ResourceLocation("lotr", s);
		FactionTableType type = (FactionTableType) Registry.register((Registry) Registry.RECIPE_TYPE, res, (Object) new FactionTableType(res, blockSup));
		ALL_INDIVIDUAL_FACTION_TYPES.add(type);
		return type;
	}

	private static MultiTableType registerMulti(String s, FactionTableType... types) {
		return registerMulti(s, ImmutableList.copyOf(types));
	}

	private static MultiTableType registerMulti(String s, List<FactionTableType> types) {
		ResourceLocation res = new ResourceLocation("lotr", s);
		return (MultiTableType) Registry.register((Registry) Registry.RECIPE_TYPE, res, (Object) new MultiTableType(res, types));
	}
}
