package lotr.common.data;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import lotr.common.LOTRLog;
import lotr.common.fac.FactionPointer;
import lotr.common.util.TriConsumer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;

public class DataUtil {
	public static <T> void fillCollectionFromBuffer(PacketBuffer buf, Collection<T> collection, Supplier<T> bufferToElement) {
		collection.clear();
		int collectionSize = buf.readVarInt();
		for (int i = 0; i < collectionSize; i++) {
			T element = bufferToElement.get();
			if (element != null)
				collection.add(element);
		}
	}

	public static <K, V> void fillMapFromBuffer(PacketBuffer buf, Map<K, V> map, Supplier<Pair<K, V>> bufferToEntry) {
		map.clear();
		int mapSize = buf.readVarInt();
		for (int i = 0; i < mapSize; i++) {
			Pair<K, V> entry = bufferToEntry.get();
			if (entry != null)
				map.put((K) entry.getKey(), (V) entry.getValue());
		}
	}

	public static <T> T getIfNBTContains(T currentValue, CompoundNBT nbt, String key, BiFunction<CompoundNBT, String, T> nbtGetter) {
		return nbt.contains(key) ? nbtGetter.apply(nbt, key) : currentValue;
	}

	public static ResourceLocation getResourceLocation(CompoundNBT nbt, String key) {
		String resString = nbt.getString(key);

		try {
			return new ResourceLocation(resString);
		} catch (ResourceLocationException var4) {
			LOTRLog.error("Invalid resourcelocation string '%s' for NBT key '%s'", resString, key);
			var4.printStackTrace();
			return null;
		}
	}

	public static UUID getUniqueIdBackCompat(CompoundNBT nbt, String key) {
		try {
			return nbt.getUUID(key);
		} catch (NullPointerException | IllegalArgumentException var3) {
			if (hasOldUniqueId(nbt, key)) {
				return new UUID(nbt.getLong(key + "Most"), nbt.getLong(key + "Least"));
			}
			throw var3;
		}
	}

	private static boolean hasOldUniqueId(CompoundNBT nbt, String key) {
		return nbt.contains(key + "Most", 4) && nbt.contains(key + "Least", 4);
	}

	public static boolean hasUniqueIdBackCompat(CompoundNBT nbt, String key) {
		return nbt.hasUUID(key) || hasOldUniqueId(nbt, key);
	}

	public static <T> void loadCollectionFromCompoundListNBT(Collection<T> collection, ListNBT tagList, Function<CompoundNBT, T> nbtToElement) {
		collection.clear();

		for (int i = 0; i < tagList.size(); ++i) {
			CompoundNBT nbt = tagList.getCompound(i);
			T element = nbtToElement.apply(nbt);
			if (element != null) {
				collection.add(element);
			}
		}

	}

	public static <T, P> void loadCollectionFromPrimitiveListNBT(Collection<T> collection, ListNBT tagList, BiFunction<ListNBT, Integer, P> listNbtToPrimitive, Function<P, T> primitiveToElement) {
		collection.clear();
		for (int i = 0; i < tagList.size(); i++) {
			P value = listNbtToPrimitive.apply(tagList, Integer.valueOf(i));
			T element = primitiveToElement.apply(value);
			if (element != null)
				collection.add(element);
		}
	}

	public static <K, V> void loadMapFromListNBT(Map<K, V> map, ListNBT tagList, Function<CompoundNBT, Pair<K, V>> nbtToEntry) {
		map.clear();
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT nbt = tagList.getCompound(i);
			Pair<K, V> entry = nbtToEntry.apply(nbt);
			if (entry != null)
				map.put((K) entry.getKey(), (V) entry.getValue());
		}
	}

	public static void putResourceLocation(CompoundNBT nbt, String key, ResourceLocation value) {
		nbt.putString(key, value.toString());
	}

	public static <T, C extends Collection<T>> C readNewCollectionFromBuffer(PacketBuffer buf, Supplier<C> collectionSupplier, Supplier<T> bufferToElement) {
		Collection<T> collection = collectionSupplier.get();
	    fillCollectionFromBuffer(buf, collection, bufferToElement);
	    return (C) collection;
	}

	public static <K, V, M extends Map<K, V>> M readNewMapFromBuffer(PacketBuffer buf, Supplier<M> mapSupplier, Supplier<Pair<K, V>> bufferToEntry) {
		Map<K, V> map = mapSupplier.get();
		fillMapFromBuffer(buf, map, bufferToEntry);
		return (M) map;
	}

	public static <T> T readNullableFromBuffer(PacketBuffer buf, Function<PacketBuffer, T> bufferToObject) {
	    return readNullableFromBuffer(buf, () -> bufferToObject.apply(buf));
	  }

	public static <T> T readNullableFromBuffer(PacketBuffer buf, Supplier<T> bufferToObject) {
	    boolean hasObject = buf.readBoolean();
	    if (hasObject)
	      return bufferToObject.get(); 
	    return null;
	  }

	public static Optional<FactionPointer> readOptionalFactionPointerFromNBT(CompoundNBT nbt, String key) {
	    Optional<ResourceLocation> optName = readOptionalFromNBT(nbt, key, DataUtil::getResourceLocation);
	    return optName.map(FactionPointer::of);
	  }

	public static <T> Optional<T> readOptionalFromNBT(CompoundNBT nbt, String key, BiFunction<CompoundNBT, String, T> nbtGetter) {
		if (nbt.contains(key))
			return Optional.ofNullable(nbtGetter.apply(nbt, key)); 
		return Optional.empty();
	}

	public static <T> ListNBT saveCollectionAsCompoundListNBT(Collection<T> collection, BiConsumer<CompoundNBT, T> elementToNbt) {
		ListNBT tagList = new ListNBT();
		for (T t : collection) {
			CompoundNBT nbt = new CompoundNBT();
			elementToNbt.accept(nbt, t);
			tagList.add(nbt);
		}
		return tagList;
	}

	public static <T, N extends net.minecraft.nbt.INBT> ListNBT saveCollectionAsPrimitiveListNBT(Collection<T> collection, Function<T, N> elementToNbt) {
		ListNBT tagList = new ListNBT();
		for (T t : collection)
			tagList.add(elementToNbt.apply(t));
		return tagList;
	}

	public static <K, V> ListNBT saveMapAsListNBT(Map<K, V> map, TriConsumer<CompoundNBT, K, V> entryToNbt) {
		ListNBT tagList = new ListNBT();
		for (Map.Entry<K, V> e : map.entrySet()) {
			K key = e.getKey();
			V value = e.getValue();
			CompoundNBT nbt = new CompoundNBT();
			entryToNbt.accept(nbt, key, value);
			tagList.add(nbt);
		}
		return tagList;
	}

	public static <T> void writeCollectionToBuffer(PacketBuffer buf, Collection<T> collection, Consumer<T> elementToBuffer) {
		buf.writeVarInt(collection.size());
		collection.forEach(elementToBuffer);
	}

	public static <K, V> void writeMapToBuffer(PacketBuffer buf, Map<K, V> map, BiConsumer<K, V> entryToBuffer) {
		buf.writeVarInt(map.size());
		map.forEach(entryToBuffer);
	}

	public static <T> void writeNullableToBuffer(PacketBuffer buf, T object, BiConsumer<T, PacketBuffer> elementToBuffer) {
		writeNullableToBuffer(buf, object, () -> {
			elementToBuffer.accept(object, buf);
		});
	}

	public static <T> void writeNullableToBuffer(PacketBuffer buf, T object, BiFunction<PacketBuffer, T, PacketBuffer> elementToBuffer) {
		writeNullableToBuffer(buf, object, () -> {
			elementToBuffer.apply(buf, object);
		});
	}

	public static <T> void writeNullableToBuffer(PacketBuffer buf, T object, Runnable elementToBuffer) {
		boolean hasObject = object != null;
		buf.writeBoolean(hasObject);
		if (hasObject) {
			elementToBuffer.run();
		}

	}

	public static void writeOptionalFactionPointerToNBT(CompoundNBT nbt, String key, Optional<FactionPointer> pointer) {
		Optional<ResourceLocation> optName = pointer.map(hummel -> ((FactionPointer) hummel).getName());
		writeOptionalToNBT(nbt, key, optName, (hummel, hummel2, hummel3) -> DataUtil.getResourceLocation((CompoundNBT) hummel, (String) hummel2));
	}

	public static <T> void writeOptionalToNBT(CompoundNBT nbt, String key, Optional<T> opt, TriConsumer<CompoundNBT, String, T> nbtPutter) {
		opt.ifPresent(val -> nbtPutter.accept(nbt, key, val));
	}
}
