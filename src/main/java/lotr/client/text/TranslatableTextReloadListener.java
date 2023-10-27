/*
 * Decompiled with CFR 0.148.
 *
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.Language
 *  net.minecraft.client.resources.LanguageManager
 *  net.minecraft.client.resources.ReloadListener
 *  net.minecraft.profiler.IProfiler
 *  net.minecraft.resources.IFutureReloadListener
 *  net.minecraft.resources.IReloadableResourceManager
 *  net.minecraft.resources.IResourceManager
 *  net.minecraft.util.ResourceLocation
 */
package lotr.client.text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import lotr.common.LOTRLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public abstract class TranslatableTextReloadListener<T, P> extends ReloadListener {
	private final Minecraft mcInstance;
	private final String fileExtensionType;
	private final IReloadableResourceManager resourceManager;
	private Map<ResourceLocation, T> loadedTextResources = new HashMap<>();

	public TranslatableTextReloadListener(Minecraft mc, String fileExtensionType) {
		this.mcInstance = mc;
		this.resourceManager = (IReloadableResourceManager) this.mcInstance.getResourceManager();
		this.resourceManager.registerReloadListener(this);
		this.fileExtensionType = fileExtensionType;
	}

	@Override
	protected void apply(Object prepared, IResourceManager resMgr, IProfiler profiler) {
		this.loadedTextResources.clear();
	}

	protected abstract ResourceLocation convertToFullResourcePath(ResourceLocation var1);

	protected abstract P createNewParentLoader(ResourceLocation var1, String var2);

	private String getCurrentLanguage() {
		return this.mcInstance.getLanguageManager().getSelected().getCode();
	}

	private ResourceLocation getLangResourceLocation(ResourceLocation basePath, String lang) {
		return new ResourceLocation(basePath.getNamespace(), String.format("%s/%s%s", basePath.getPath(), lang, this.fileExtensionType));
	}

	protected final T getOrLoadTextResource(ResourceLocation basePath) {
		return this.getOrLoadTextResource(basePath, this.getCurrentLanguage(), true, null);
	}

	protected final T getOrLoadTextResource(ResourceLocation basePath, String lang, boolean loadErroredFallback, Object parentLoader) {
		basePath = this.convertToFullResourcePath(basePath);
		return this.loadedTextResources.computeIfAbsent(basePath, bp -> ((T) this.loadTextResource(bp, lang, loadErroredFallback, parentLoader)));
	}

	protected abstract T loadErroredFallbackResource(ResourceLocation var1, String var2);

	protected abstract T loadResource(ResourceLocation var1, BufferedReader var2, P var3);

	private Object loadTextResource(ResourceLocation basePath, String lang, boolean loadErroredFallback, Object parentLoader) {
		ResourceLocation langPath = this.getLangResourceLocation(basePath, lang);
		if (!this.resourceManager.hasResource(langPath)) {
			LOTRLog.warn("Couldn't find translatable resource file %s for current language %s - fallback to %s", basePath, lang, "en_us");
			langPath = this.getLangResourceLocation(basePath, "en_us");
		}

		try {
			IResource resource = this.resourceManager.getResource(langPath);
			Throwable var40 = null;

			Object var10;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
				Throwable var9 = null;

				try {
					if (parentLoader == null) {
						parentLoader = this.createNewParentLoader(basePath, lang);
					}

					var10 = this.loadResource(langPath, reader, (P) parentLoader);
				} catch (Throwable var35) {
					var10 = var35;
					var9 = var35;
					throw var35;
				} finally {
					if (reader != null) {
						if (var9 != null) {
							try {
								reader.close();
							} catch (Throwable var34) {
								var9.addSuppressed(var34);
							}
						} else {
							reader.close();
						}
					}

				}
			} catch (Throwable var37) {
				var40 = var37;
				throw var37;
			} finally {
				if (resource != null) {
					if (var40 != null) {
						try {
							resource.close();
						} catch (Throwable var33) {
							var40.addSuppressed(var33);
						}
					} else {
						resource.close();
					}
				}

			}

			return var10;
		} catch (Exception var39) {
			String errorMsg = String.format("Failed to load resource file %s!", langPath);
			LOTRLog.error(errorMsg);
			var39.printStackTrace();
			return loadErroredFallback ? this.loadErroredFallbackResource(langPath, errorMsg) : null;
		}
	}

	@Override
	protected Object prepare(IResourceManager resMgr, IProfiler profiler) {
		return null;
	}
}
