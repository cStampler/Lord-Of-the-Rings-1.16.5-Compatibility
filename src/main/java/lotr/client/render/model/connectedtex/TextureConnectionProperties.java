package lotr.client.render.model.connectedtex;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.util.ResourceLocation;

public class TextureConnectionProperties {
	private final ResourceLocation textureName;
	private final Optional elementPathOverrides;
	private final boolean includeBaseElement;
	private final boolean makeFromSingleIcon;

	public TextureConnectionProperties(ResourceLocation textureName, Optional elementPathOverrides, boolean includeBaseElement, boolean makeFromSingleIcon) {
		this.textureName = textureName;
		this.elementPathOverrides = elementPathOverrides;
		this.includeBaseElement = includeBaseElement;
		this.makeFromSingleIcon = makeFromSingleIcon;
	}

	public ResourceLocation getBaseTextureName() {
		return textureName;
	}

	public ResourceLocation getCanonicalCacheKey() {
		return new ResourceLocation(textureName.getNamespace(), String.format("%s.connectedproperties__overrides_%s__includebaseelement_%s__makefromsingleicon_%s", textureName.getPath(), getCanonicalFormForElementPathOverrides(), includeBaseElement, makeFromSingleIcon));
	}

	private String getCanonicalFormForElementPathOverrides() {
		if (!elementPathOverrides.isPresent()) {
			return "none";
		}
		Map map = (Map) elementPathOverrides.get();
		List sortedKeys = new ArrayList(map.keySet());
		Collections.sort(sortedKeys, Comparator.comparingInt(elem -> ((Enum<ConnectedTextureElement>) elem).ordinal()));
		return (String) sortedKeys.stream().map(elem -> {
			String elemName = ((ConnectedTextureElement) elem).elementName;
			String pathOverride = ((ResourceLocation) map.get(elem)).toString().replace(':', '.');
			return elemName + "_" + pathOverride;
		}).collect(Collectors.joining("_"));
	}

	public Optional getElementIconPath(ConnectedTextureElement element) {
		if (element == ConnectedTextureElement.BASE) {
			throw new IllegalArgumentException("This method should not be used to determine the base icon - this is a development error");
		}
		return elementPathOverrides.isPresent() ? Optional.ofNullable(((Map) elementPathOverrides.get()).get(element)) : Optional.of(new ResourceLocation(textureName + element.getDefaultIconSuffix()));
	}

	public boolean includeBaseElement() {
		return includeBaseElement;
	}

	public boolean makeFromSingleIcon() {
		return makeFromSingleIcon;
	}

	public static TextureConnectionProperties defaultProps(ResourceLocation textureName) {
		return new TextureConnectionProperties(textureName, Optional.empty(), true, false);
	}

	public static TextureConnectionProperties resolveFrom(UnresolvedTextureConnectionProperties unresolved, Function textureResolver) {
		return new TextureConnectionProperties((ResourceLocation) textureResolver.apply(unresolved.textureName), resolveUnresolvedElementPathOverrides(unresolved.elementPathOverrides, textureResolver), unresolved.includeBaseElement, unresolved.makeFromSingleIcon);
	}

	private static Optional resolveUnresolvedElementPathOverrides(Optional unresolvedOpt, Function textureResolver) {
		if (unresolvedOpt.isPresent()) {
			Map unresolvedMap = (Map) unresolvedOpt.get();
			Map resolvedMap = (Map) unresolvedMap.entrySet().stream().collect(Collectors.toMap(hummel -> ((Entry) hummel).getKey(), e -> ((ResourceLocation) textureResolver.apply(((Entry) e).getValue()))));
			return Optional.of(resolvedMap);
		}
		return Optional.empty();
	}
}
