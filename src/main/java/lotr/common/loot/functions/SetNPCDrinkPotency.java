package lotr.common.loot.functions;

import java.util.Random;

import com.google.gson.*;

import lotr.common.item.VesselDrinkItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;

public class SetNPCDrinkPotency extends LootFunction {
	private SetNPCDrinkPotency(ILootCondition[] conditions) {
		super(conditions);
	}

	// $FF: synthetic method
	SetNPCDrinkPotency(ILootCondition[] x0, Object x1) {
		this(x0);
	}

	private VesselDrinkItem.Potency getRandomPotency(Random rand) {
		int i = rand.nextInt(3);
		if (i == 0) {
			return VesselDrinkItem.Potency.LIGHT;
		}
		return i == 1 ? VesselDrinkItem.Potency.MODERATE : VesselDrinkItem.Potency.STRONG;
	}

	@Override
	public LootFunctionType getType() {
		return LOTRLootFunctions.SET_NPC_DRINK_POTENCY;
	}

	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		if (stack.getItem() instanceof VesselDrinkItem) {
			VesselDrinkItem drink = (VesselDrinkItem) stack.getItem();
			if (drink.hasPotencies) {
				VesselDrinkItem.setPotency(stack, getRandomPotency(context.getRandom()));
			}
		}

		return stack;
	}

	public static Builder setNPCDrinkPotencyBuilder() {
		return simpleBuilder(SetNPCDrinkPotency::new);
	}

	public static class Serializer extends net.minecraft.loot.LootFunction.Serializer {
		@Override
		public SetNPCDrinkPotency deserialize(JsonObject object, JsonDeserializationContext context, ILootCondition[] conditions) {
			return new SetNPCDrinkPotency(conditions);
		}
	}
}
