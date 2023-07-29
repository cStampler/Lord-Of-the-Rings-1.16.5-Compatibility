package lotr.common.recipe;

import java.util.*;
import java.util.stream.*;

import com.google.gson.*;

import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class DynamicIngredient extends Ingredient {
	private final DynamicIngredient.Type type;

	private DynamicIngredient(DynamicIngredient.Type type) {
		super(type.getItemList());
		this.type = type;
	}

	// $FF: synthetic method
	DynamicIngredient(DynamicIngredient.Type x0, Object x1) {
		this(x0);
	}

	@Override
	public IIngredientSerializer getSerializer() {
		return LOTRRecipes.DYNAMIC_INGREDIENT_SERIALIZER;
	}

	public DynamicIngredient.Type getType() {
		return type;
	}

	public static class Serializer implements IIngredientSerializer {
		@Override
		public Ingredient parse(JsonObject json) {
			if (!json.has("dynamic_type")) {
				throw new JsonSyntaxException("Missing dynamic_type, expected to find something here");
			}
			String typeName = json.get("dynamic_type").getAsString();
			DynamicIngredient.Type type = DynamicIngredient.Type.forCode(typeName);
			if (type != null) {
				return new DynamicIngredient(type);
			}
			throw new JsonSyntaxException("No dynamic_type named " + typeName + " exists");
		}

		@Override
		public Ingredient parse(PacketBuffer buffer) {
			String typeName = buffer.readUtf();
			DynamicIngredient.Type type = DynamicIngredient.Type.forCode(typeName);
			if (type != null) {
				return new DynamicIngredient(type);
			}
			throw new JsonSyntaxException("No dynamic_type named " + typeName + " exists");
		}

		@Override
		public void write(PacketBuffer buffer, Ingredient ingredient) {
			DynamicIngredient dynIng = (DynamicIngredient) ingredient;
			buffer.writeUtf(dynIng.getType().getCode());
		}
	}

	public enum Type {
		MEATS_EXCEPT_ROTTEN_FLESH("meats_except_rotten_flesh");

		private final String code;
		private ItemStack[] cachedItemStacks;

		Type(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		public Stream getItemList() {
			if (cachedItemStacks == null) {
				if (this != MEATS_EXCEPT_ROTTEN_FLESH) {
					throw new IllegalArgumentException("Type " + code + " not yet implemented in code");
				}

				List meats = ForgeRegistries.ITEMS.getValues().stream().filter(item -> (item.isEdible() && item.getFoodProperties().isMeat() && item != Items.ROTTEN_FLESH)).map(ItemStack::new).collect(Collectors.toList());
				cachedItemStacks = (ItemStack[]) meats.toArray(new ItemStack[0]);
			}

			return Arrays.stream(cachedItemStacks).map(SingleItemList::new);
		}

		public static DynamicIngredient.Type forCode(String code) {
			DynamicIngredient.Type[] var1 = values();
			int var2 = var1.length;

			for (int var3 = 0; var3 < var2; ++var3) {
				DynamicIngredient.Type type = var1[var3];
				if (type.code.equals(code)) {
					return type;
				}
			}

			return null;
		}
	}
}
