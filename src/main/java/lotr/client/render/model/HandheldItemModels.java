package lotr.client.render.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.common.LOTRLog;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

public class HandheldItemModels implements ISelectiveResourceReloadListener {
	public static final HandheldItemModels INSTANCE = new HandheldItemModels();
	private List specialHandheldItemNames = new ArrayList();

	private HandheldItemModels() {
	}

	private void addSpecialHandheld(ResourceLocation itemName) {
		specialHandheldItemNames.add(itemName);
		ModelLoader.addSpecialModel(getHandheldModelLocation(itemName));
	}

	private void detectSpecialHandhelds(IResourceManager resMgr) {
		specialHandheldItemNames.clear();
		for (ResourceLocation itemName : ForgeRegistries.ITEMS.getKeys()) {
			ResourceLocation fullHandheldModelPath = new ResourceLocation(itemName.getNamespace(), String.format("models/item/%s_%s.json", itemName.getPath(), "handheld"));
			if (resMgr.hasResource(fullHandheldModelPath)) {
				addSpecialHandheld(itemName);
			}
		}

		LOTRLog.info("Automatically detected and registered %d special handheld items!", specialHandheldItemNames.size());
	}

	private ModelResourceLocation getHandheldModelLocation(ResourceLocation itemName) {
		return new ModelResourceLocation(String.format("%s_%s", itemName, "handheld"), "inventory");
	}

	public void onModelBake(ModelBakeEvent event) {
		Map modelMap = event.getModelRegistry();
		Iterator var3 = specialHandheldItemNames.iterator();

		while (var3.hasNext()) {
			ResourceLocation itemName = (ResourceLocation) var3.next();
			ResourceLocation modelName = new ModelResourceLocation(itemName, "inventory");
			ResourceLocation handheldModelName = getHandheldModelLocation(itemName);
			IBakedModel defaultModel = (IBakedModel) modelMap.get(modelName);
			IBakedModel handheldModel = (IBakedModel) modelMap.get(handheldModelName);
			if (defaultModel == null) {
				throw new IllegalStateException("Could not find default inventory model for " + modelName);
			}

			if (handheldModel == null) {
				throw new IllegalStateException("Could not find handheld model for " + handheldModelName);
			}

			HandheldItemModels.HandheldWrapperModel.remapHandheldModelOverrides(modelName, defaultModel, handheldModel);
			IBakedModel wrapperModel = new HandheldItemModels.HandheldWrapperModel(defaultModel, handheldModel);
			modelMap.put(modelName, wrapperModel);
		}

	}

	@Override
	public void onResourceManagerReload(IResourceManager resMgr, Predicate predicate) {
		if (predicate.test(VanillaResourceType.MODELS)) {
			detectSpecialHandhelds(resMgr);
		}

	}

	public void setupAndDetectModels(Minecraft mc) {
		SimpleReloadableResourceManager resMgr = (SimpleReloadableResourceManager) mc.getResourceManager();
		resMgr.registerReloadListener(this);
		detectSpecialHandhelds(resMgr);
	}

	private static class HandheldWrapperModel implements IBakedModel {
		private final IBakedModel defaultModel;
		private final IBakedModel handheldModel;

		public HandheldWrapperModel(IBakedModel defaultModel, IBakedModel handheldModel) {
			this.defaultModel = defaultModel;
			this.handheldModel = handheldModel;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return handheldModel.getOverrides();
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return getParticleTexture(EmptyModelData.INSTANCE);
		}

		@Override
		public TextureAtlasSprite getParticleTexture(IModelData extraData) {
			return defaultModel.getParticleTexture(extraData);
		}

		@Override
		public List getQuads(BlockState state, Direction cullFace, Random rand) {
			return this.getQuads(state, cullFace, rand, EmptyModelData.INSTANCE);
		}

		@Override
		public List getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
			return defaultModel.getQuads(state, side, rand, extraData);
		}

		@Override
		public IBakedModel handlePerspective(TransformType transformType, MatrixStack mat) {
			IBakedModel modelToUse = defaultModel;
			if (transformType == TransformType.FIRST_PERSON_LEFT_HAND || transformType == TransformType.FIRST_PERSON_RIGHT_HAND || transformType == TransformType.THIRD_PERSON_LEFT_HAND || transformType == TransformType.THIRD_PERSON_RIGHT_HAND) {
				modelToUse = handheldModel;
			}

			return ForgeHooksClient.handlePerspective(modelToUse, transformType, mat);
		}

		@Override
		public boolean isCustomRenderer() {
			return defaultModel.isCustomRenderer();
		}

		@Override
		public boolean isGui3d() {
			return defaultModel.isGui3d();
		}

		@Override
		public boolean useAmbientOcclusion() {
			return defaultModel.useAmbientOcclusion();
		}

		@Override
		public boolean usesBlockLight() {
			return defaultModel.usesBlockLight();
		}

		public static void remapHandheldModelOverrides(ResourceLocation modelName, IBakedModel defaultModel, IBakedModel handheldModel) {
			try {
				ItemOverrideList overrides = handheldModel.getOverrides();
				Field f_overrideBakedModels = ObfuscationReflectionHelper.findField(ItemOverrideList.class, "overrideModels");
				LOTRUtil.unlockFinalField(f_overrideBakedModels);
				List overrideModels = (List) f_overrideBakedModels.get(overrides);
				List remappedOverrideModels = (List) overrideModels.stream().map(handheldOverride -> new HandheldItemModels.HandheldWrapperModel(defaultModel, (IBakedModel) handheldOverride)).collect(Collectors.toList());
				f_overrideBakedModels.set(overrides, remappedOverrideModels);
			} catch (Exception var7) {
				LOTRLog.error("Failed to remap handheld model overrides for model %s", modelName);
				var7.printStackTrace();
			}

		}
	}
}
