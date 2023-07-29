package lotr.client.render;

import java.util.*;
import java.util.function.Predicate;

import lotr.common.LOTRLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.entity.Entity;
import net.minecraft.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.*;

public class RandomTextureVariants implements ISelectiveResourceReloadListener {
	private static final Random RAND = new Random();
	private static final Minecraft MC = Minecraft.getInstance();
	private static final Map ALL_RANDOM_SKINS = new HashMap();
	private final ResourceLocation skinPath;
	private final List skins = new ArrayList();

	private RandomTextureVariants(ResourceLocation path) {
		skinPath = path;
		IReloadableResourceManager resMgr = (IReloadableResourceManager) MC.getResourceManager();
		resMgr.registerReloadListener(this);
		loadAllRandomSkins(resMgr);
	}

	public List getAllSkins() {
		return skins;
	}

	public ResourceLocation getRandomSkin() {
		if (skins.isEmpty()) {
			return MissingTextureSprite.getLocation();
		}
		int i = RAND.nextInt(skins.size());
		return (ResourceLocation) skins.get(i);
	}

	public ResourceLocation getRandomSkin(Entity entity) {
		if (skins.isEmpty()) {
			return MissingTextureSprite.getLocation();
		}
		int i = nextInt(entity, skins.size());
		return (ResourceLocation) skins.get(i);
	}

	private void loadAllRandomSkins(IResourceManager resMgr) {
		skins.clear();
		int skinCount = 0;
		int skips = 0;
		int maxSkips = 10;
		boolean foundAfterSkip = false;

		while (true) {
			ResourceLocation skin = new ResourceLocation(skinPath.getNamespace(), skinPath.getPath() + "/" + skinCount + ".png");
			boolean noFile = false;

			try {
				if (resMgr.getResource(skin) == null) {
					noFile = true;
				}
			} catch (Exception var9) {
				noFile = true;
			}

			if (noFile) {
				++skips;
				if (skips >= maxSkips) {
					if (skins.isEmpty()) {
						LOTRLog.warn("No random skins for %s", skinPath);
					}

					if (foundAfterSkip) {
						LOTRLog.warn("Random skins %s skipped a number. This is bad for performance - please number your skins from 0 up, with no gaps!", skinPath);
					}

					return;
				}

				++skinCount;
			} else {
				skins.add(skin);
				++skinCount;
				if (skips > 0) {
					foundAfterSkip = true;
				}
			}
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resMgr, Predicate resPredicate) {
		if (resPredicate.test(VanillaResourceType.TEXTURES)) {
			loadAllRandomSkins(resMgr);
		}

	}

	public static RandomTextureVariants loadSkinsList(ResourceLocation path) {
		return (RandomTextureVariants) ALL_RANDOM_SKINS.computeIfAbsent(path, hummel -> new RandomTextureVariants((ResourceLocation) hummel));
	}

	public static RandomTextureVariants loadSkinsList(String namespace, String path) {
		return loadSkinsList(new ResourceLocation(namespace, path));
	}

	public static float nextFloat(Entity entity) {
		setRandSeedFromEntity(entity);
		return RAND.nextFloat();
	}

	public static int nextInt(Entity entity, int n) {
		setRandSeedFromEntity(entity);
		return RAND.nextInt(n);
	}

	private static void setRandSeedFromEntity(Entity entity) {
		UUID entityUuid = entity.getUUID();
		long l = entityUuid.getLeastSignificantBits();
		l = l * 29506206L * (l ^ entityUuid.getMostSignificantBits()) + 25859L;
		l = l * l * 426430295004L + 25925025L * l;
		RAND.setSeed(l);
	}
}
