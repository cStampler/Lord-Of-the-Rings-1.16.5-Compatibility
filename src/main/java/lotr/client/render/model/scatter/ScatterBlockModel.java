package lotr.client.render.model.scatter;

import java.util.*;

import com.google.common.math.LongMath;

import lotr.client.render.model.BlockModelQuadsHolder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.*;

public class ScatterBlockModel extends SimpleBakedModel {
	private final List scatterVariantModels;

	public ScatterBlockModel(List variantModels, boolean ambOcc, boolean sideLight, boolean g3d, TextureAtlasSprite partTex, ItemCameraTransforms transform, ItemOverrideList overrides) {
		super(new ArrayList(), new HashMap(), ambOcc, sideLight, g3d, partTex, transform, overrides);
		scatterVariantModels = variantModels;
		if (scatterVariantModels.isEmpty()) {
			throw new IllegalArgumentException("Model variant list cannot be empty!");
		}
	}

	@Override
	public IModelData getModelData(IBlockDisplayReader world, BlockPos pos, BlockState state, IModelData tileData) {
		return ScatterPositionContext.forPosition(world, pos, state);
	}

	@Override
	public List getQuads(BlockState state, Direction side, Random rand) {
		return this.getQuads(state, side, rand, ScatterPositionContext.newEmptyContext());
	}

	@Override
	public List getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		if (extraData instanceof ScatterPositionContext) {
			ScatterPositionContext posContext = (ScatterPositionContext) extraData;
			long hash = posContext.getPositionHash();
			int index = LongMath.mod(hash, scatterVariantModels.size());
			return ((BlockModelQuadsHolder) scatterVariantModels.get(index)).getQuads(side);
		}
		if (extraData instanceof EmptyModelData) {
			return this.getQuads(state, side, rand);
		}
		throw new IllegalArgumentException("ScatterBlockModel can only take ScatterPositionContext model data or EmptyModelData, but " + extraData.getClass().getName() + " was supplied");
	}
}
