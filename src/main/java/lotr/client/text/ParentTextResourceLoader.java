/*
 * Decompiled with CFR 0.148.
 *
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 */
package lotr.client.text;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

public abstract class ParentTextResourceLoader<T> {
	private final ResourceLocation topLevelResource;
	private final Set<ResourceLocation> inheritanceRecord;
	protected final String langCode;

	public ParentTextResourceLoader(ResourceLocation topLevelResource, String langCode) {
		this.topLevelResource = topLevelResource;
		this.inheritanceRecord = new HashSet<>();
		this.inheritanceRecord.add(topLevelResource);
		this.langCode = langCode;
	}

	protected final void checkInheritanceRecord(ResourceLocation parent) {
		if (this.inheritanceRecord.contains(parent)) {
			throw this.createCircularReferenceException(this.topLevelResource, parent);
		}
		this.inheritanceRecord.add(parent);
	}

	protected abstract IllegalArgumentException createCircularReferenceException(ResourceLocation var1, ResourceLocation var2);

	public abstract Optional<T> getOrLoadParentResource(ResourceLocation var1);

	public static class NoopParentLoader extends ParentTextResourceLoader {
		public NoopParentLoader() {
			super(null, null);
		}

		@Override
		protected IllegalArgumentException createCircularReferenceException(ResourceLocation topLevelPath, ResourceLocation parentPath) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Optional getOrLoadParentResource(ResourceLocation parent) {
			throw new UnsupportedOperationException();
		}
	}

}
