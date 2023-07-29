package lotr.client.render.model;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import lotr.client.render.model.connectedtex.*;
import lotr.common.LOTRLog;
import lotr.common.item.VesselType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.*;
import net.minecraft.util.ResourceLocation;

public class DynamicTextureRepository {
	public static final DynamicTextureRepository INSTANCE = new DynamicTextureRepository();
	private final Map virtualPacks = new HashMap();
	private final Map cachedConnectedLocations = new HashMap();
	private final Map cachedVesselLocations = new HashMap();

	private DynamicTextureRepository() {
	}

	private NativeImage copyAreaFromIcon(NativeImage baseImage, double x0, double y0, double x1, double y1) {
		int iconWidth = baseImage.getWidth();
		int iconHeight = baseImage.getHeight();
		NativeImage elementImage = new NativeImage(baseImage.format(), iconWidth, iconHeight, true);
		int x0I = (int) Math.round(x0 / 16.0D * iconWidth);
		int x1I = (int) Math.round(x1 / 16.0D * iconWidth);
		int y0I = (int) Math.round(y0 / 16.0D * iconHeight);
		int y1I = (int) Math.round(y1 / 16.0D * iconHeight);

		for (int y = y0I; y < y1I; ++y) {
			for (int x = x0I; x < x1I; ++x) {
				elementImage.setPixelRGBA(x, y, baseImage.getPixelRGBA(x, y));
			}
		}

		return elementImage;
	}

	public List generateAllConnectedTextures(TextureConnectionProperties textureProperties) {
		Minecraft mc = Minecraft.getInstance();
		IResourceManager resMgr = mc.getResourceManager();
		ResourceLocation base = textureProperties.getBaseTextureName();
		boolean includeBase = textureProperties.includeBaseElement();
		String namespace = base.getNamespace();
		ResourceLocation cacheKeyLocation = textureProperties.getCanonicalCacheKey();
		if (cachedConnectedLocations.containsKey(cacheKeyLocation)) {
			boolean checkResourceExists = resMgr.hasResource(DynamicTextureResourcePack.createDynamicTextureSetIsLoadedMarker(cacheKeyLocation));
			if (checkResourceExists) {
				return (List) cachedConnectedLocations.get(cacheKeyLocation);
			}
		}

		ArrayList allList = new ArrayList();

		try {
			ResourceLocation baseTextureFullPath = convertTextureFullPath(base);
			NativeImage baseImage = NativeImage.read(resMgr.getResource(baseTextureFullPath).getInputStream());
			int iconWidth = baseImage.getWidth();
			int iconHeight = baseImage.getHeight();
			Map elementImages = new HashMap();
			ConnectedTextureElement[] var14 = ConnectedTextureElement.values();
			int var15 = var14.length;

			NativeImage connectedImage;
			for (int var16 = 0; var16 < var15; ++var16) {
				ConnectedTextureElement elem = var14[var16];
				if (elem == ConnectedTextureElement.BASE) {
					if (includeBase) {
						elementImages.put(elem, baseImage);
					}
				} else if (textureProperties.makeFromSingleIcon()) {
					elementImages.put(elem, makePartFromSingleIcon(baseImage, elem));
				} else {
					Optional optElementPath = textureProperties.getElementIconPath(elem);
					if (optElementPath.isPresent()) {
						ResourceLocation elementPath = convertTextureFullPath((ResourceLocation) optElementPath.get());
						connectedImage = NativeImage.read(resMgr.getResource(elementPath).getInputStream());
						if (connectedImage.getWidth() == iconWidth && connectedImage.getHeight() == iconHeight) {
							elementImages.put(elem, connectedImage);
						} else {
							LOTRLog.error("All connected texture icons for %s must have the same dimensions!", base);
							LOTRLog.error("%s: base icon is %dx%d, but %s icon is %dx%d", base, iconWidth, iconHeight, elem.elementName, connectedImage.getWidth(), connectedImage.getHeight());
							elementImages.put(elem, createErroredImage(iconWidth, iconHeight));
						}
					}
				}
			}

			Map permutationSet = includeBase ? ConnectedTextureElement.ALL_COMBINATIONS_WITH_BASE : ConnectedTextureElement.ALL_COMBINATIONS_WITHOUT_BASE;
			Iterator var31 = permutationSet.entrySet().iterator();

			label84: while (var31.hasNext()) {
				Entry entry = (Entry) var31.next();
				entry.getKey();
				Set elementSet = (Set) entry.getValue();
				List sortedList = ConnectedTextureElement.sortIconSet(elementSet);
				connectedImage = new NativeImage(baseImage.format(), iconWidth, iconHeight, true);
				if (includeBase) {
					connectedImage.copyFrom(baseImage);
				}

				Iterator var21 = sortedList.iterator();

				while (true) {
					NativeImage elementImage;
					do {
						ConnectedTextureElement elem;
						do {
							if (!var21.hasNext()) {
								DynamicTexture dynamic = new DynamicTexture(connectedImage);
								ResourceLocation connectedRes = getConnectedTextureLocation(base, elementSet);
								ResourceLocation connectedFullPath = convertTextureFullPath(connectedRes);
								mc.getTextureManager().register(connectedFullPath, dynamic);
								getVirtualPack(namespace).addDynamicTexture(cacheKeyLocation, connectedFullPath, dynamic);
								allList.add(connectedRes);
								continue label84;
							}

							elem = (ConnectedTextureElement) var21.next();
						} while (elem == ConnectedTextureElement.BASE);

						elementImage = (NativeImage) elementImages.get(elem);
					} while (elementImage == null);

					for (int x = 0; x < connectedImage.getWidth(); ++x) {
						for (int y = 0; y < connectedImage.getHeight(); ++y) {
							int rgba = elementImage.getPixelRGBA(x, y);
							int alpha = rgba >> 24 & 255;
							if (alpha != 0) {
								connectedImage.setPixelRGBA(x, y, rgba);
							}
						}
					}
				}
			}
		} catch (IOException var28) {
			LOTRLog.error("Error generating connected textures for %s", cacheKeyLocation);
			var28.printStackTrace();
		}

		cachedConnectedLocations.put(cacheKeyLocation, allList);
		return allList;
	}

	public Map generateVesselDrinkTextures(ResourceLocation liquidTex) {
		Minecraft mc = Minecraft.getInstance();
		IResourceManager resMgr = mc.getResourceManager();
		String namespace = liquidTex.getNamespace();
		if (cachedVesselLocations.containsKey(liquidTex)) {
			boolean checkResourceExists = resMgr.hasResource(DynamicTextureResourcePack.createDynamicTextureSetIsLoadedMarker(liquidTex));
			if (checkResourceExists) {
				return (Map) cachedVesselLocations.get(liquidTex);
			}
		}

		HashMap allMap = new HashMap();

		try {
			ResourceLocation liquidTexFullPath = convertTextureFullPath(liquidTex);
			NativeImage liquidImage = NativeImage.read(resMgr.getResource(liquidTexFullPath).getInputStream());
			int iconWidth = liquidImage.getWidth();
			int iconHeight = liquidImage.getHeight();
			VesselType[] var10 = VesselType.values();
			int var11 = var10.length;

			for (int var12 = 0; var12 < var11; ++var12) {
				VesselType ves = var10[var12];
				ResourceLocation vesPath = convertTextureFullPath(ves.getEmptySpritePath());
				NativeImage vesImage = NativeImage.read(resMgr.getResource(vesPath).getInputStream());
				if (iconWidth < vesImage.getWidth() || iconHeight < vesImage.getHeight()) {
					LOTRLog.error("The loaded drink liquid icon %s is too small! Must be at least the size of loaded vessel icons, and ideally 2x2x", liquidTex);
					LOTRLog.error("%s: liquid icon is %dx%d, but %s icon is %dx%d", liquidTex, iconWidth, iconHeight, ves.getEmptyIconName(), vesImage.getWidth(), vesImage.getHeight());
					vesImage = createErroredImage(iconWidth, iconHeight);
				}

				NativeImage filledDrinkImage = new NativeImage(vesImage.format(), vesImage.getWidth(), vesImage.getHeight(), true);
				filledDrinkImage.copyFrom(vesImage);

				for (int x = 0; x < filledDrinkImage.getWidth(); ++x) {
					for (int y = 0; y < filledDrinkImage.getHeight(); ++y) {
						int rgb = filledDrinkImage.getPixelRGBA(x, y) & 16777215;
						if (rgb == 16711935) {
							int liquidRgba = liquidImage.getPixelRGBA(x, y);
							filledDrinkImage.setPixelRGBA(x, y, liquidRgba);
						}
					}
				}

				DynamicTexture dynamic = new DynamicTexture(filledDrinkImage);
				ResourceLocation filledRes = getFilledVesselLocation(liquidTex, ves);
				ResourceLocation filledFullPath = convertTextureFullPath(filledRes);
				mc.getTextureManager().register(filledFullPath, dynamic);
				getVirtualPack(namespace).addDynamicTexture(liquidTex, filledFullPath, dynamic);
				allMap.put(ves, filledRes);
			}
		} catch (IOException var21) {
			LOTRLog.error("Error generating filled vessel textures for %s", liquidTex);
			var21.printStackTrace();
		}

		cachedVesselLocations.put(liquidTex, allMap);
		return allMap;
	}

	public ResourceLocation getConnectedTexture(TextureConnectionProperties textureProperties, Set elements) {
		ResourceLocation cacheKey = textureProperties.getCanonicalCacheKey();
		return !cachedConnectedLocations.containsKey(cacheKey) ? MissingTextureSprite.getLocation() : getConnectedTextureLocation(textureProperties.getBaseTextureName(), elements);
	}

	public ResourceLocation getFilledVesselTexture(ResourceLocation liquidTex, VesselType vessel) {
		return !cachedVesselLocations.containsKey(liquidTex) ? MissingTextureSprite.getLocation() : getFilledVesselLocation(liquidTex, vessel);
	}

	private DynamicTextureResourcePack getVirtualPack(String namespace) {
		Minecraft mc = Minecraft.getInstance();
		SimpleReloadableResourceManager resMgr = (SimpleReloadableResourceManager) mc.getResourceManager();
		DynamicTextureResourcePack pack = (DynamicTextureResourcePack) virtualPacks.get(namespace);
		if (pack == null || pack != null && !resMgr.hasResource(pack.packIsLoadedMarkerResource)) {
			pack = new DynamicTextureResourcePack(ResourcePackType.CLIENT_RESOURCES, namespace);
			virtualPacks.put(namespace, pack);
			resMgr.add(pack);
		}

		return pack;
	}

	private NativeImage makePartFromSingleIcon(NativeImage baseImage, ConnectedTextureElement elem) {

		switch (elem) {
		case SIDE_LEFT:
			return copyAreaFromIcon(baseImage, 0.0D, 0.0D, 3.0D, 16.0D);
		case SIDE_RIGHT:
			return copyAreaFromIcon(baseImage, 13.0D, 0.0D, 16.0D, 16.0D);
		case SIDE_TOP:
			return copyAreaFromIcon(baseImage, 0.0D, 0.0D, 16.0D, 3.0D);
		case SIDE_BOTTOM:
			return copyAreaFromIcon(baseImage, 0.0D, 13.0D, 16.0D, 16.0D);
		case CORNER_TOPLEFT:
		case INVCORNER_TOPLEFT:
			return copyAreaFromIcon(baseImage, 0.0D, 0.0D, 3.0D, 3.0D);
		case CORNER_TOPRIGHT:
		case INVCORNER_TOPRIGHT:
			return copyAreaFromIcon(baseImage, 13.0D, 0.0D, 16.0D, 3.0D);
		case CORNER_BOTTOMLEFT:
		case INVCORNER_BOTTOMLEFT:
			return copyAreaFromIcon(baseImage, 0.0D, 13.0D, 3.0D, 16.0D);
		case CORNER_BOTTOMRIGHT:
		case INVCORNER_BOTTOMRIGHT:
			return copyAreaFromIcon(baseImage, 13.0D, 13.0D, 3.0D, 16.0D);
		default:
			throw new IllegalArgumentException("Unknown connected texture element " + elem.toString() + "!");
		}
	}

	private static ResourceLocation convertTextureFullPath(ResourceLocation texture) {
		return new ResourceLocation(texture.getNamespace(), String.format("textures/%s.png", texture.getPath()));
	}

	private static NativeImage createErroredImage(int width, int height) {
		NativeImage errored = new NativeImage(width, height, true);

		for (int x = 0; x < errored.getWidth(); ++x) {
			for (int y = 0; y < errored.getHeight(); ++y) {
				int rgb;
				if ((x + y) % 2 == 0) {
					rgb = 16711680;
				} else {
					rgb = 0;
				}

				errored.setPixelRGBA(x, y, -16777216 | rgb);
			}
		}

		return errored;
	}

	private static ResourceLocation getConnectedTextureLocation(ResourceLocation base, Set elements) {
		int key = ConnectedTextureElement.getIconSetKey(elements);
		return new ResourceLocation(base.getNamespace(), base.getPath() + "_" + key);
	}

	private static ResourceLocation getFilledVesselLocation(ResourceLocation liquidTex, VesselType vessel) {
		return new ResourceLocation(liquidTex.getNamespace(), liquidTex.getPath() + "_" + vessel.getCodeName());
	}
}
