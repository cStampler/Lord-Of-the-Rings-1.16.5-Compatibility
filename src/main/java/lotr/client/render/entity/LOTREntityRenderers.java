package lotr.client.render.entity;

import lotr.client.render.tileentity.*;
import lotr.common.init.*;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.client.registry.*;

public class LOTREntityRenderers {
	public static void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.RING_PORTAL.get(), RingPortalRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.THROWN_PLATE.get(), ThrownPlateRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.BOAT.get(), LOTRBoatRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.FALLING_TREASURE_BLOCK.get(), FallingTreasureBlockRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.SMOKE_RING.get(), SmokeRingRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.ALIGNMENT_BONUS.get(), AlignmentBonusRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.SPEAR.get(), SpearRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.HOBBIT.get(), HobbitRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.MORDOR_ORC.get(), SmallOrcRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.GONDOR_MAN.get(), GondorManWithOutfitRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.GALADHRIM_ELF.get(), GaladhrimElfRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.GONDOR_SOLDIER.get(), GondorSoldierRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.DWARF.get(), DurinsFolkRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.DWARF_WARRIOR.get(), DurinsFolkRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.GALADHRIM_WARRIOR.get(), GaladhrimElfRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.URUK.get(), UrukRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.ROHAN_MAN.get(), RohanManRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.ROHIRRIM_WARRIOR.get(), RohirrimWarriorRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.GUNDABAD_ORC.get(), SmallOrcRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.DALE_MAN.get(), DaleManRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.DALE_SOLDIER.get(), DaleSoldierRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.DUNLENDING.get(), DunlendingWithOutfitRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.DUNLENDING_WARRIOR.get(), DunlendingRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.LINDON_ELF.get(), HighElfRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.LINDON_WARRIOR.get(), HighElfRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.RIVENDELL_ELF.get(), HighElfRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.RIVENDELL_WARRIOR.get(), HighElfRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.COAST_SOUTHRON.get(), CoastSouthronRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.COAST_SOUTHRON_WARRIOR.get(), CoastSouthronWarriorRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.HARNEDHRIM.get(), HarnedhrimWithOutfitRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.HARNENNOR_WARRIOR.get(), HarnennorWarriorRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.BLUE_MOUNTAINS_DWARF.get(), BlueDwarfRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.BLUE_MOUNTAINS_WARRIOR.get(), BlueDwarfRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.UMBAR_MAN.get(), CoastSouthronRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.UMBAR_SOLDIER.get(), CoastSouthronWarriorRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.GONDOR_ARCHER.get(), GondorSoldierRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.ROHIRRIM_BOWMAN.get(), RohirrimWarriorRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.DALE_BOWMAN.get(), DaleSoldierRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.DUNLENDING_BOWMAN.get(), DunlendingRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.COAST_SOUTHRON_ARCHER.get(), CoastSouthronWarriorRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.HARNENNOR_ARCHER.get(), HarnennorWarriorRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.UMBAR_ARCHER.get(), CoastSouthronWarriorRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.MORDOR_ORC_ARCHER.get(), SmallOrcRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.GUNDABAD_ORC_ARCHER.get(), SmallOrcRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.URUK_ARCHER.get(), UrukRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.DWARF_ARCHER.get(), DurinsFolkRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.BLUE_MOUNTAINS_ARCHER.get(), BlueDwarfRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.WOOD_ELF.get(), WoodElfRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.WOOD_ELF_WARRIOR.get(), WoodElfRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.BREE_MAN.get(), BreeManWithOutfitRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.BREE_HOBBIT.get(), HobbitRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.BREE_GUARD.get(), BreeManRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.GUNDABAD_WARG.get(), WargRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.ISENGARD_WARG.get(), WargRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.MORDOR_WARG.get(), WargRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.ISENGARD_SNAGA.get(), SmallOrcRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.ISENGARD_SNAGA_ARCHER.get(), SmallOrcRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler((EntityType) LOTREntities.CARACAL.get(), CaracalRenderer::new);
	}

	public static void registerTileEntityRenderers() {
		ClientRegistry.bindTileEntityRenderer((TileEntityType) LOTRTileEntities.PLATE.get(), PlateTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer((TileEntityType) LOTRTileEntities.CUSTOM_WAYPOINT_MARKER.get(), CustomWaypointMarkerTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer((TileEntityType) LOTRTileEntities.VESSEL_DRINK.get(), VesselDrinkTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer((TileEntityType) LOTRTileEntities.PALANTIR.get(), PalantirTileEntityRenderer::new);
	}
}
