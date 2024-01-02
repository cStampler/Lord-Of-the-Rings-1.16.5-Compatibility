package lotr.common.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.util.ResourceLocation;

public class LazyReference<T> {
	private final ResourceLocation referenceName;
	private final Function<ResourceLocation, T> referenceResolver;
	private final Consumer<ResourceLocation> errorLoggerIfResolvingFails;
	private T resolvedReference;
	private boolean attemptedToResolve = false;

	private LazyReference(ResourceLocation name, Function<ResourceLocation, T> resolver, Consumer<ResourceLocation> errorLogger) {
		Objects.requireNonNull(name, "Reference name must not be null");
		Objects.requireNonNull(resolver, "Reference resolver function must not be null");
		Objects.requireNonNull(errorLogger, "Error logger also must not be null");
		referenceName = name;
		referenceResolver = resolver;
		errorLoggerIfResolvingFails = errorLogger;
	}

	public ResourceLocation getReferenceName() {
		return referenceName;
	}

	public T resolveReference() {
		if (!attemptedToResolve) {
			resolvedReference = referenceResolver.apply(referenceName);
			attemptedToResolve = true;
			if (resolvedReference == null) {
				errorLoggerIfResolvingFails.accept(referenceName);
			}
		}

		return resolvedReference;
	}

	public static <T> LazyReference<T> of(ResourceLocation name, Function<ResourceLocation, T> resolver, Consumer<ResourceLocation> errorLogger) {
		return new LazyReference<>(name, resolver, errorLogger);
	}
}
