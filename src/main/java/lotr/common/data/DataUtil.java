package lotr.common.data;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;

import org.apache.commons.lang3.tuple.Pair;

import lotr.common.LOTRLog;
import lotr.common.fac.FactionPointer;
import lotr.common.util.TriConsumer;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;

public class DataUtil {
	public static void fillCollectionFromBuffer(PacketBuffer buf, Collection collection, Supplier bufferToElement) {
		collection.clear();
		int collectionSize = buf.readVarInt();

		for (int i = 0; i < collectionSize; ++i) {
			Object element = bufferToElement.get();
			if (element != null) {
				collection.add(element);
			}
		}

	}

	public static void fillMapFromBuffer(PacketBuffer buf, Map map, Supplier bufferToEntry) {
		map.clear();
		int mapSize = buf.readVarInt();

		for (int i = 0; i < mapSize; ++i) {
			Pair entry = (Pair) bufferToEntry.get();
			if (entry != null) {
				map.put(entry.getKey(), entry.getValue());
			}
		}

	}

	public static Object getIfNBTContains(Object currentValue, CompoundNBT nbt, String key, BiFunction nbtGetter) {
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

	public static void loadCollectionFromCompoundListNBT(Collection collection, ListNBT tagList, Function nbtToElement) {
		collection.clear();

		for (int i = 0; i < tagList.size(); ++i) {
			CompoundNBT nbt = tagList.getCompound(i);
			Object element = nbtToElement.apply(nbt);
			if (element != null) {
				collection.add(element);
			}
		}

	}

	public static void loadCollectionFromPrimitiveListNBT(Collection collection, ListNBT tagList, BiFunction listNbtToPrimitive, Function primitiveToElement) {
		collection.clear();

		for (int i = 0; i < tagList.size(); ++i) {
			Object value = listNbtToPrimitive.apply(tagList, i);
			Object element = primitiveToElement.apply(value);
			if (element != null) {
				collection.add(element);
			}
		}

	}

	public static void loadMapFromListNBT(Map map, ListNBT tagList, Function nbtToEntry) {
		map.clear();

		for (int i = 0; i < tagList.size(); ++i) {
			CompoundNBT nbt = tagList.getCompound(i);
			Pair entry = (Pair) nbtToEntry.apply(nbt);
			if (entry != null) {
				map.put(entry.getKey(), entry.getValue());
			}
		}

	}

	public static void putResourceLocation(CompoundNBT nbt, String key, ResourceLocation value) {
		nbt.putString(key, value.toString());
	}

	public static Collection readNewCollectionFromBuffer(PacketBuffer buf, Supplier collectionSupplier, Supplier bufferToElement) {
		Collection collection = (Collection) collectionSupplier.get();
		fillCollectionFromBuffer(buf, collection, bufferToElement);
		return collection;
	}

	public static Map readNewMapFromBuffer(PacketBuffer buf, Supplier mapSupplier, Supplier bufferToEntry) {
		Map map = (Map) mapSupplier.get();
		fillMapFromBuffer(buf, map, bufferToEntry);
		return map;
	}

	public static Object readNullableFromBuffer(PacketBuffer buf, Function bufferToObject) {
		return readNullableFromBuffer(buf, () -> bufferToObject.apply(buf));
	}

	public static Object readNullableFromBuffer(PacketBuffer buf, Supplier bufferToObject) {
		boolean hasObject = buf.readBoolean();
		return hasObject ? bufferToObject.get() : null;
	}

	public static Optional readOptionalFactionPointerFromNBT(CompoundNBT nbt, String key) {
		Optional optName = readOptionalFromNBT(nbt, key, (hummel, hummel2) -> DataUtil.getResourceLocation((CompoundNBT) hummel, (String) hummel2));
		return optName.map(hummel -> FactionPointer.of((ResourceLocation) hummel));
	}

	public static Optional readOptionalFromNBT(CompoundNBT nbt, String key, BiFunction nbtGetter) {
		return nbt.contains(key) ? Optional.ofNullable(nbtGetter.apply(nbt, key)) : Optional.empty();
	}

	public static ListNBT saveCollectionAsCompoundListNBT(Collection collection, BiConsumer elementToNbt) {
		ListNBT tagList = new ListNBT();
		Iterator var3 = collection.iterator();

		while (var3.hasNext()) {
			Object t = var3.next();
			CompoundNBT nbt = new CompoundNBT();
			elementToNbt.accept(nbt, t);
			tagList.add(nbt);
		}

		return tagList;
	}

	public static ListNBT saveCollectionAsPrimitiveListNBT(Collection collection, Function elementToNbt) {
		ListNBT tagList = new ListNBT();
		Iterator var3 = collection.iterator();

		while (var3.hasNext()) {
			Object t = var3.next();
			tagList.add((INBT) elementToNbt.apply(t));
		}

		return tagList;
	}

	public static ListNBT saveMapAsListNBT(Map map, TriConsumer entryToNbt) {
		ListNBT tagList = new ListNBT();
		Iterator var3 = map.entrySet().iterator();

		while (var3.hasNext()) {
			Entry e = (Entry) var3.next();
			Object key = e.getKey();
			Object value = e.getValue();
			CompoundNBT nbt = new CompoundNBT();
			entryToNbt.accept(nbt, key, value);
			tagList.add(nbt);
		}

		return tagList;
	}

	public static void writeCollectionToBuffer(PacketBuffer buf, Collection collection, Consumer elementToBuffer) {
		buf.writeVarInt(collection.size());
		collection.forEach(elementToBuffer);
	}

	public static void writeMapToBuffer(PacketBuffer buf, Map map, BiConsumer entryToBuffer) {
		buf.writeVarInt(map.size());
		map.forEach(entryToBuffer);
	}

	public static void writeNullableToBuffer(PacketBuffer buf, Object object, BiConsumer elementToBuffer) {
		writeNullableToBuffer(buf, object, () -> {
			elementToBuffer.accept(object, buf);
		});
	}

	public static void writeNullableToBuffer(PacketBuffer buf, Object object, BiFunction elementToBuffer) {
		writeNullableToBuffer(buf, object, () -> {
			elementToBuffer.apply(buf, object);
		});
	}

	public static void writeNullableToBuffer(PacketBuffer buf, Object object, Runnable elementToBuffer) {
		boolean hasObject = object != null;
		buf.writeBoolean(hasObject);
		if (hasObject) {
			elementToBuffer.run();
		}

	}

	public static void writeOptionalFactionPointerToNBT(CompoundNBT nbt, String key, Optional pointer) {
		Optional optName = pointer.map(hummel -> ((FactionPointer) hummel).getName());
		writeOptionalToNBT(nbt, key, optName, (hummel, hummel2, hummel3) -> DataUtil.getResourceLocation((CompoundNBT) hummel, (String) hummel2));
	}

	public static void writeOptionalToNBT(CompoundNBT nbt, String key, Optional opt, TriConsumer nbtPutter) {
		opt.ifPresent(val -> {
			nbtPutter.accept(nbt, key, val);
		});
	}
}
