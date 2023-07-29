package lotr.common.recipe;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.*;

public class UndamagedIngredient extends Ingredient {
	private UndamagedIngredient(Stream itemLists) {
		super(itemLists);
	}

	// $FF: synthetic method
	UndamagedIngredient(Stream x0, Object x1) {
		this(x0);
	}

	@Override
	public IIngredientSerializer getSerializer() {
		return LOTRRecipes.UNDAMAGED_INGREDIENT_SERIALIZER;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return super.test(stack) && !stack.isDamaged();
	}

	public static class Serializer implements IIngredientSerializer {
		private final IIngredientSerializer internalVanillaSerializer = new VanillaIngredientSerializer();

		@Override
		public UndamagedIngredient parse(JsonObject json) {
			return new UndamagedIngredient(Stream.of(Ingredient.valueFromJson(json)));
		}

		@Override
		public UndamagedIngredient parse(PacketBuffer buffer) {
			return new UndamagedIngredient(Stream.generate(() -> new SingleItemList(buffer.readItem())).limit(buffer.readVarInt()));
		}

		@Override
		public void write(PacketBuffer buffer, Ingredient ingredient) {
			internalVanillaSerializer.write(buffer, ingredient);
		}
	}
}
