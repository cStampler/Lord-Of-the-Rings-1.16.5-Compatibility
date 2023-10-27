package lotr.common.util;

@FunctionalInterface
public interface TriConsumer<T, U, V> {
	void accept(T paramT, U paramU, V paramV);
}
