package lotr.client.render.model.connectedtex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lotr.client.render.model.BlockModelUtil;
import lotr.common.LOTRLog;
import net.minecraft.util.JSONUtils;

public class UnresolvedTextureConnectionProperties {
	public final String textureName;
	public final Optional elementPathOverrides;
	public final boolean includeBaseElement;
	public final boolean makeFromSingleIcon;

	public UnresolvedTextureConnectionProperties(String textureName, Optional elementPathOverrides, boolean includeBaseElement, boolean makeFromSingleIcon) {
		this.textureName = textureName;
		this.elementPathOverrides = elementPathOverrides;
		this.includeBaseElement = includeBaseElement;
		this.makeFromSingleIcon = makeFromSingleIcon;
	}

	public static UnresolvedTextureConnectionProperties read(String textureName, JsonObject json) {
		Optional elementPathOverrides;
		if (json.has("connection_elements")) {
			Map elementsMap = new HashMap();
			JsonObject elementsObj = JSONUtils.getAsJsonObject(json, "connection_elements");
			Iterator var5 = elementsObj.entrySet().iterator();

			while (var5.hasNext()) {
				Entry entry = (Entry) var5.next();
				String elementName = (String) entry.getKey();
				ConnectedTextureElement element = ConnectedTextureElement.getNonBaseElementByName(elementName);
				if (element != null) {
					String overrideString = ((JsonElement) entry.getValue()).getAsString();
					if (BlockModelUtil.validateTextureString(overrideString)) {
						elementsMap.put(element, overrideString);
					} else {
						LOTRLog.error("Error loading TextureConnectionProperties for texture '%s' - override texture '%s' is not a valid texture path or #reference", textureName, overrideString);
					}
				} else {
					LOTRLog.error("Error loading TextureConnectionProperties for connected texture '%s' - no connected texture element by name '%s'", textureName, elementName);
				}
			}

			elementPathOverrides = Optional.of(elementsMap);
		} else {
			elementPathOverrides = Optional.empty();
		}

		boolean includeBaseElement = JSONUtils.getAsBoolean(json, "include_base", true);
		boolean makeFromSingleIcon = JSONUtils.getAsBoolean(json, "make_from_single_icon", false);
		return new UnresolvedTextureConnectionProperties(textureName, elementPathOverrides, includeBaseElement, makeFromSingleIcon);
	}
}
