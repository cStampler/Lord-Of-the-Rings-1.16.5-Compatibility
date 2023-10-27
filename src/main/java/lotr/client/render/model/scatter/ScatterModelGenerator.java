package lotr.client.render.model.scatter;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class ScatterModelGenerator {
	private final Random rand = new Random();
	private final int minElements;
	private final int maxElements;
	private final int minXSize;
	private final int maxXSize;
	private final int minYSize;
	private final int maxYSize;
	private final int minZSize;
	private final int maxZSize;

	private ScatterModelGenerator(int minElements, int maxElements, int minXSize, int maxXSize, int minYSize, int maxYSize, int minZSize, int maxZSize) {
		this.minElements = minElements;
		this.maxElements = maxElements;
		this.minXSize = minXSize;
		this.maxXSize = maxXSize;
		this.minYSize = minYSize;
		this.maxYSize = maxYSize;
		this.minZSize = minZSize;
		this.maxZSize = maxZSize;
		if (minElements < 0 || minElements > maxElements) {
			throw new IllegalArgumentException("Invalid num-elements range");
		}
		if (minXSize < 0 || maxXSize > 16 || minXSize > maxXSize) {
			throw new IllegalArgumentException("Invalid x-size range");
		}
		if (minYSize < 0 || maxYSize > 16 || minYSize > maxYSize) {
			throw new IllegalArgumentException("Invalid y-size range");
		}
		if (minZSize < 0 || maxZSize > 16 || minZSize > maxZSize) {
			throw new IllegalArgumentException("Invalid z-size range");
		}
	}

	private BlockFaceUV generateDirectionalFaceUV(Direction faceDir, int xSize, int ySize, int zSize) {
		Axis axis = faceDir.getAxis();
		if (axis == Axis.Y) {
			return generateFaceUV(xSize, zSize);
		}
		return axis == Axis.X ? generateFaceUV(zSize, ySize) : generateFaceUV(xSize, ySize);
	}

	private BlockPartFace generateDirectionalPartFace(Direction faceDir, int xSize, int ySize, int zSize, Direction cullFace, int tintIndex, String texture) {
		BlockFaceUV faceUV = generateDirectionalFaceUV(faceDir, xSize, ySize, zSize);
		return new BlockPartFace(cullFace, tintIndex, texture, faceUV);
	}

	private BlockFaceUV generateFaceUV(int uDirectionSize, int vDirectionSize) {
		int minU = MathHelper.nextInt(rand, 0, 16 - uDirectionSize);
		int minV = MathHelper.nextInt(rand, 0, 16 - vDirectionSize);
		int maxU = minU + uDirectionSize;
		int maxV = minV + vDirectionSize;
		return new BlockFaceUV(new float[] { minU, minV, maxU, maxV }, 0);
	}

	public List generateNRandomModels(int n, String texture) {
		rand.setSeed(5251224673490637827L);
		rand.setSeed(rand.nextLong() ^ texture.hashCode() * (1156258289 + n));
		boolean hasHeight = minYSize > 0 || maxYSize > 0;
		return IntStream.range(0, n).mapToObj(i -> {
			int elements = MathHelper.nextInt(rand, minElements, maxElements);
			return (List) IntStream.range(0, elements).mapToObj(e -> {
				float elementFrac = (float) e / (float) elements;
				float posX = MathHelper.nextFloat(rand, 0.0F, 16.0F);
				MathHelper.nextFloat(rand, 0.0F, 16.0F);
				float posY = hasHeight ? 0.0F + elementFrac * 0.1F : 0.16F + elementFrac * 1.6F;
				int xSize = MathHelper.nextInt(rand, minXSize, maxXSize);
				int ySize = hasHeight ? MathHelper.nextInt(rand, minYSize, maxYSize) : 0;
				int zSize = MathHelper.nextInt(rand, minZSize, maxZSize);
				Map mapFaces = generatePartFaceMap(texture, xSize, ySize, zSize);
				float halfXSize = xSize / 2.0F;
				float halfZSize = zSize / 2.0F;
				Vector3f posFrom = new Vector3f(posX - halfXSize, posY, posX - halfZSize);
				Vector3f posTo = new Vector3f(posX + halfXSize, posY + ySize, posX + halfZSize);
				float rotAngle = rand.nextFloat() * 360.0F;
				Vector3f rotOrigin = new Vector3f(0.5F, 0.5F, 0.5F);
				boolean rescale = false;
				BlockPartRotation rotation = new BlockPartRotation(rotOrigin, Axis.Y, rotAngle, rescale);
				boolean shade = ySize > 0;
				return new BlockPart(posFrom, posTo, mapFaces, rotation, shade);
			}).collect(Collectors.toList());
		}).collect(Collectors.toList());
	}

	private Map generatePartFaceMap(String texture, int xSize, int ySize, int zSize) {
		Direction cullFace = null;
		int tintIndex = 0;
		Stream faceDirs = ySize == 0 ? Stream.of(Direction.UP) : Stream.of(Direction.values());
		return (Map) faceDirs.collect(Collectors.toMap(UnaryOperator.identity(), faceDir -> generateDirectionalPartFace((Direction) faceDir, xSize, ySize, zSize, cullFace, tintIndex, texture)));
	}

	public static ScatterModelGenerator parse(JsonObject json) {
		int[] elementsRange = parseIntRange(json, "num_elements_range");
		int[] xSizeRange = parseIntRange(json, "x_size_range");
		int[] ySizeRange = parseIntRangeOrDefault(json, "y_size_range", 0, 0);
		int[] zSizeRange = parseIntRange(json, "z_size_range");
		return new ScatterModelGenerator(elementsRange[0], elementsRange[1], xSizeRange[0], xSizeRange[1], ySizeRange[0], ySizeRange[1], zSizeRange[0], zSizeRange[1]);
	}

	private static int[] parseIntRange(JsonObject json, String key) {
		JsonArray array = json.get(key).getAsJsonArray();
		if (array.size() != 2) {
			throw new IllegalArgumentException("Range " + key + " should be an array of 2 values");
		}
		int min = array.get(0).getAsInt();
		int max = array.get(1).getAsInt();
		return new int[] { min, max };
	}

	private static int[] parseIntRangeOrDefault(JsonObject json, String key, int defaultMin, int defaultMax) {
		return json.has(key) ? parseIntRange(json, key) : new int[] { defaultMin, defaultMax };
	}
}
