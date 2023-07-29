package lotr.common.init;

import java.util.function.Supplier;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;

import lotr.common.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

public class LOTRTileEntities {
	public static final DeferredRegister TILE_ENTITIES;
	public static final RegistryObject ALLOY_FORGE;
	public static final RegistryObject DWARVEN_FORGE;
	public static final RegistryObject ELVEN_FORGE;
	public static final RegistryObject ORC_FORGE;
	public static final RegistryObject HOBBIT_OVEN;
	public static final RegistryObject PLATE;
	public static final RegistryObject KEG;
	public static final RegistryObject CUSTOM_WAYPOINT_MARKER;
	public static final RegistryObject GONDOR_BEACON;
	public static final RegistryObject VESSEL_DRINK;
	public static final RegistryObject PALANTIR;

	static {
		TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, "lotr");
		ALLOY_FORGE = register("alloy_forge", () -> Builder.of(AlloyForgeTileEntity::new, (Block) LOTRBlocks.ALLOY_FORGE.get()));
		DWARVEN_FORGE = register("dwarven_forge", () -> Builder.of(DwarvenForgeTileEntity::new, (Block) LOTRBlocks.DWARVEN_FORGE.get()));
		ELVEN_FORGE = register("elven_forge", () -> Builder.of(ElvenForgeTileEntity::new, (Block) LOTRBlocks.ELVEN_FORGE.get()));
		ORC_FORGE = register("orc_forge", () -> Builder.of(OrcForgeTileEntity::new, (Block) LOTRBlocks.ORC_FORGE.get()));
		HOBBIT_OVEN = register("hobbit_oven", () -> Builder.of(HobbitOvenTileEntity::new, (Block) LOTRBlocks.HOBBIT_OVEN.get()));
		PLATE = register("plate", () -> Builder.of(PlateTileEntity::new, (Block) LOTRBlocks.FINE_PLATE.get(), (Block) LOTRBlocks.STONEWARE_PLATE.get(), (Block) LOTRBlocks.WOODEN_PLATE.get()));
		KEG = register("keg", () -> Builder.of(KegTileEntity::new, (Block) LOTRBlocks.KEG.get()));
		CUSTOM_WAYPOINT_MARKER = register("custom_waypoint_marker", () -> Builder.of(CustomWaypointMarkerTileEntity::new, (Block) LOTRBlocks.CUSTOM_WAYPOINT_MARKER.get()));
		GONDOR_BEACON = register("gondor_beacon", () -> Builder.of(GondorBeaconTileEntity::new, (Block) LOTRBlocks.GONDOR_BEACON.get()));
		VESSEL_DRINK = register("vessel_drink", () -> Builder.of(VesselDrinkTileEntity::new, (Block) LOTRBlocks.WOODEN_MUG.get(), (Block) LOTRBlocks.CERAMIC_MUG.get(), (Block) LOTRBlocks.GOLDEN_GOBLET.get(), (Block) LOTRBlocks.SILVER_GOBLET.get(), (Block) LOTRBlocks.COPPER_GOBLET.get(), (Block) LOTRBlocks.WOODEN_CUP.get(), (Block) LOTRBlocks.ALE_HORN.get(), (Block) LOTRBlocks.GOLDEN_ALE_HORN.get()));
		PALANTIR = register("palantir", () -> Builder.of(PalantirTileEntity::new, (Block) LOTRBlocks.PALANTIR.get()));
	}

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		TILE_ENTITIES.register(bus);
	}

	private static RegistryObject register(String key, Supplier builderSup) {
		Type datafixType = null;

		try {
			datafixType = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).getChoiceType(TypeReferences.BLOCK_ENTITY, key);
		} catch (IllegalArgumentException var4) {
			if (SharedConstants.IS_RUNNING_IN_IDE) {
				throw var4;
			}
		}
		final Type datafixType2 = datafixType;
		return TILE_ENTITIES.register(key, () -> ((Builder) builderSup.get()).build(datafixType2));
	}
}
