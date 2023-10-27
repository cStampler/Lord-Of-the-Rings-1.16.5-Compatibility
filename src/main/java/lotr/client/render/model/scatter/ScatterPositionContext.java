package lotr.client.render.model.scatter;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class ScatterPositionContext implements IModelData {
	private final long positionHash;

	private ScatterPositionContext(long hash) {
		positionHash = hash;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other != null && other.getClass() == this.getClass()) {
			ScatterPositionContext otherData = (ScatterPositionContext) other;
			return positionHash == otherData.positionHash;
		}
		return false;
	}

	@Override
	public Object getData(ModelProperty prop) {
		return null;
	}

	public long getPositionHash() {
		return positionHash;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(positionHash);
	}

	@Override
	public boolean hasProperty(ModelProperty prop) {
		return false;
	}

	@Override
	public Object setData(ModelProperty prop, Object data) {
		return null;
	}

	@Override
	public String toString() {
		return String.format("ScatterPositionContext[%d]", positionHash);
	}

	public static ScatterPositionContext forPosition(IBlockDisplayReader world, BlockPos pos, BlockState state) {
		long hash = MathHelper.getSeed(pos);
		return new ScatterPositionContext(hash);
	}

	public static ScatterPositionContext newEmptyContext() {
		return new ScatterPositionContext(0L);
	}
}
