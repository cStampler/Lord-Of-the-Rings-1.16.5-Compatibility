package lotr.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;

public class LOTRBlockMaterial {
	public static final Material CRYSTAL;
	public static final Material ICE_BRICK;
	public static final Material SNOW_BRICK;
	public static final Material PALANTIR;

	static {
		CRYSTAL = new LOTRBlockMaterial.Builder(MaterialColor.NONE).notOpaque().notSolid().pushDestroys().build();
		ICE_BRICK = new LOTRBlockMaterial.Builder(MaterialColor.ICE).build();
		SNOW_BRICK = new LOTRBlockMaterial.Builder(MaterialColor.SNOW).build();
		PALANTIR = new LOTRBlockMaterial.Builder(MaterialColor.NONE).notOpaque().notSolid().build();
	}

	private static class Builder {
		private PushReaction pushReaction;
		private boolean blocksMovement;
		private boolean canBurn;
		private boolean isLiquid;
		private boolean isReplaceable;
		private boolean isSolid;
		private final MaterialColor color;
		private boolean isOpaque;

		public Builder(MaterialColor c) {
			pushReaction = PushReaction.NORMAL;
			blocksMovement = true;
			isSolid = true;
			isOpaque = true;
			color = c;
		}

		public Material build() {
			return new Material(color, isLiquid, isSolid, blocksMovement, isOpaque, canBurn, isReplaceable, pushReaction);
		}

		public LOTRBlockMaterial.Builder notOpaque() {
			isOpaque = false;
			return this;
		}

		public LOTRBlockMaterial.Builder notSolid() {
			isSolid = false;
			return this;
		}

		public LOTRBlockMaterial.Builder pushDestroys() {
			pushReaction = PushReaction.DESTROY;
			return this;
		}
	}
}
