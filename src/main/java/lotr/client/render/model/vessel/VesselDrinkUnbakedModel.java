package lotr.client.render.model.vessel;

import java.util.*;
import java.util.function.Function;

import com.google.common.collect.*;
import com.google.gson.*;

import lotr.client.render.model.DynamicTextureRepository;
import lotr.common.item.VesselType;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.*;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class VesselDrinkUnbakedModel implements IModelGeometry {
	private RenderMaterial cachedLiquidMaterial;

	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
		RenderMaterial baseMaterial = cachedLiquidMaterial;
		VesselDrinkOverrideHandler vesselOverrides = new VesselDrinkOverrideHandler();
		VesselType[] var9 = VesselType.values();
		int var10 = var9.length;

		for (int var11 = 0; var11 < var10; ++var11) {
			VesselType vessel = var9[var11];
			ResourceLocation filledLocation = DynamicTextureRepository.INSTANCE.getFilledVesselTexture(baseMaterial.texture(), vessel);
			RenderMaterial filledMaterial = new RenderMaterial(baseMaterial.atlasLocation(), filledLocation);
			ImmutableList quads = ItemLayerModel.getQuadsForSprites(ImmutableList.of(filledMaterial), modelTransform.getRotation(), spriteGetter);
			SimpleBakedModel vesselModel = new SimpleBakedModel(quads, createEmptyFacingQuadsMap(), false, false, false, (TextureAtlasSprite) null, owner.getCameraTransforms(), overrides);
			vesselOverrides.putOverride(vessel, vesselModel);
		}

		VesselDrinkModel model = new VesselDrinkModel(ImmutableList.of(), (TextureAtlasSprite) null, PerspectiveMapWrapper.getTransforms(modelTransform), vesselOverrides, modelTransform.getRotation().isIdentity(), owner.isSideLit());
		model.setLiquidTexture(baseMaterial);
		return model;
	}

	@Override
	public Collection getTextures(IModelConfiguration owner, Function modelGetter, Set missingTextureErrors) {
		RenderMaterial baseMaterial = owner.resolveTexture("liquid");
		cachedLiquidMaterial = baseMaterial;
		Set materials = Sets.newHashSet();
		materials.add(baseMaterial);
		Map filledTextures = DynamicTextureRepository.INSTANCE.generateVesselDrinkTextures(baseMaterial.texture());
		filledTextures.values().forEach(res -> {
			RenderMaterial filledMaterial = new RenderMaterial(baseMaterial.atlasLocation(), (ResourceLocation) res);
			materials.add(filledMaterial);
		});
		return materials;
	}

	private static Map createEmptyFacingQuadsMap() {
		Map map = new HashMap();
		Direction[] var1 = Direction.values();
		int var2 = var1.length;

		for (int var3 = 0; var3 < var2; ++var3) {
			Direction dir = var1[var3];
			map.put(dir, new ArrayList());
		}

		return map;
	}

	public static final class Loader implements IModelLoader {
		public static final VesselDrinkUnbakedModel.Loader INSTANCE = new VesselDrinkUnbakedModel.Loader();

		private Loader() {
		}

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
		}

		@Override
		public VesselDrinkUnbakedModel read(JsonDeserializationContext deserializationContext, JsonObject modelObj) {
			return new VesselDrinkUnbakedModel();
		}
	}
}
