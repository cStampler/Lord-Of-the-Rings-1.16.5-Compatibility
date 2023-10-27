package lotr.client.render;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class LOTRRenderTypes extends RenderType {
	public static final RenderType ENTITY_TRANSLUCENT_NO_TEXTURE;

	static {
		ENTITY_TRANSLUCENT_NO_TEXTURE = create(makeExtendedName("entity_translucent_no_texture"), DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, State.builder().setTextureState(NO_TEXTURE).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
	}

	private LOTRRenderTypes(String name, VertexFormat format, int drawMode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable setupTask, Runnable clearTask) {
		super(name, format, drawMode, bufferSize, useDelegate, needsSorting, setupTask, clearTask);
		throw new UnsupportedOperationException("Don't instantiate this class, it's just for protected access! Use RenderType.makeType instead.");
	}

	private static String makeExtendedName(String name) {
		return new ResourceLocation("lotr", name).toString();
	}
}
