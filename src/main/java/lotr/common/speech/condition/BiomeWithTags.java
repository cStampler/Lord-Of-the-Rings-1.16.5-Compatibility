package lotr.common.speech.condition;

import io.netty.buffer.ByteBuf;
import lotr.curuquesta.util.StringSerializer;
import net.minecraft.util.ResourceLocation;

public class BiomeWithTags {
	private final ResourceLocation biomeName;
	private final boolean isFactionHomeBiome;

	public BiomeWithTags(ResourceLocation biomeName, boolean isFactionHomeBiome) {
		if (biomeName == null) {
			throw new IllegalArgumentException("BiomeWithTags: biomeName cannot be null!");
		}
		this.biomeName = biomeName;
		this.isFactionHomeBiome = isFactionHomeBiome;
	}

	public ResourceLocation getBiomeName() {
		return biomeName;
	}

	public boolean isForeignBiome() {
		return !isHomeBiome();
	}

	public boolean isHomeBiome() {
		return isFactionHomeBiome;
	}

	@Override
	public String toString() {
		return String.format("BiomeWithTags[biomeName=%s, isFactionHomeBiome=%s]", biomeName, isFactionHomeBiome);
	}

	public void write(ByteBuf buf) {
		StringSerializer.write(biomeName.toString(), buf);
		buf.writeBoolean(isFactionHomeBiome);
	}

	public static BiomeWithTags read(ByteBuf buf) {
		String biomeString = StringSerializer.read(buf);
		ResourceLocation biomeName = new ResourceLocation(biomeString);
		boolean isFactionHomeBiome = buf.readBoolean();
		return new BiomeWithTags(biomeName, isFactionHomeBiome);
	}
}
