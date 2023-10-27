package lotr.client.render.model.connectedtex;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import lotr.client.render.model.BlockModelQuadsHolder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

public class ConnectedTextureBlockModel extends SimpleBakedModel {
	private final ConnectedTextureUnbakedModel.DeferredConnectedTextureModelBakery deferredConnectedModelBakery;
	private final TransformationMatrix blockstateRotation;
	private final ConnectedTexture3DContext itemRenderContext;
	private final ConnectedTexture3DContext.BlockConnectionType connectionType;

	public ConnectedTextureBlockModel(boolean ambOcc, boolean sideLight, boolean g3d, TextureAtlasSprite partTex, ItemCameraTransforms cameraTransforms, ItemOverrideList overrides, ConnectedTextureUnbakedModel.DeferredConnectedTextureModelBakery deferredBakery, TransformationMatrix rotation, ConnectedTexture3DContext itemCtx, ConnectedTexture3DContext.BlockConnectionType cType) {
		super(ImmutableList.of(), ImmutableMap.of(), ambOcc, sideLight, g3d, partTex, cameraTransforms, overrides);
		deferredConnectedModelBakery = deferredBakery;
		blockstateRotation = rotation;
		itemRenderContext = itemCtx;
		connectionType = cType;
	}

	@Override
	public IModelData getModelData(IBlockDisplayReader world, BlockPos pos, BlockState state, IModelData tileData) {
		return ConnectedTexture3DContext.gatherFromWorld(world, pos, state, blockstateRotation, connectionType);
	}

	@Override
	public List getQuads(BlockState state, Direction cullFace, Random rand) {
		return this.getQuads(state, cullFace, rand, itemRenderContext);
	}

	@Override
	public List getQuads(BlockState state, Direction cullFace, Random rand, IModelData extraData) {
		if (extraData instanceof ConnectedTexture3DContext) {
			ConnectedTexture3DContext ctx3d = (ConnectedTexture3DContext) extraData;
			BlockModelQuadsHolder bakedModel = deferredConnectedModelBakery.getOrCreateBakedModelFor3DContext(ctx3d);
			if (bakedModel != null) {
				return bakedModel.getQuads(cullFace);
			}
			throw new IllegalArgumentException("ConnectedTextureBlockModel could not bake a deferred model for " + ctx3d.toString());
		}
		if (extraData instanceof EmptyModelData) {
			return this.getQuads(state, cullFace, rand, itemRenderContext);
		}
		throw new IllegalArgumentException("ConnectedTextureBlockModel can only take ConnectedTexture3DContext model data or EmptyModelData, but " + extraData.getClass().getName() + " was supplied");
	}
}
