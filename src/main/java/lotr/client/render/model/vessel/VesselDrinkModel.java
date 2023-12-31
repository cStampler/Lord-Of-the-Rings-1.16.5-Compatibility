package lotr.client.render.model.vessel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.BakedItemModel;

public class VesselDrinkModel extends BakedItemModel {
	private RenderMaterial liquidMaterial;

	public VesselDrinkModel(ImmutableList quads, TextureAtlasSprite particle, ImmutableMap transforms, ItemOverrideList overrides, boolean untransformed, boolean isSideLit) {
		super(quads, particle, transforms, overrides, untransformed, isSideLit);
	}

	public void setLiquidTexture(RenderMaterial mat) {
		liquidMaterial = mat;
	}

	public static TextureAtlasSprite getLiquidIconFor(ItemStack stack) {
		RenderMaterial material = null;
		IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack);
		if (model instanceof VesselDrinkModel) {
			material = ((VesselDrinkModel) model).liquidMaterial;
		} else {
			material = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, MissingTextureSprite.getLocation());
		}

		return material.sprite();
	}
}
