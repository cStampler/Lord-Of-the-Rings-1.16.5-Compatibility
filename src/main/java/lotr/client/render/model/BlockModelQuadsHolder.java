package lotr.client.render.model;

import java.util.*;

import net.minecraft.util.Direction;

public class BlockModelQuadsHolder {
	public final List generalQuads;
	public final Map faceQuads;

	public BlockModelQuadsHolder(List general, Map faces) {
		generalQuads = general;
		faceQuads = faces;
	}

	public List getQuads(Direction side) {
		return side == null ? generalQuads : (List) faceQuads.get(side);
	}
}
