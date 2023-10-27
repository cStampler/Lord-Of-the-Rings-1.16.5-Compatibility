package lotr.common.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.util.ResourceLocation;

public class LazyReference {
	private final ResourceLocation referenceName;
	private final Function referenceResolver;
	private final Consumer errorLoggerIfResolvingFails;
	private Object resolvedReference;
	private boolean attemptedToResolve = false;

	private LazyReference(ResourceLocation name, Function resolver, Consumer errorLogger) {
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

	public Object resolveReference() {
		if (!attemptedToResolve) {
			resolvedReference = referenceResolver.apply(referenceName);
			attemptedToResolve = true;
			if (resolvedReference == null) {
				errorLoggerIfResolvingFails.accept(referenceName);
			}
		}

		return resolvedReference;
	}

	public static LazyReference of(ResourceLocation name, Function resolver, Consumer errorLogger) {
		return new LazyReference(name, resolver, errorLogger);
	}
}
