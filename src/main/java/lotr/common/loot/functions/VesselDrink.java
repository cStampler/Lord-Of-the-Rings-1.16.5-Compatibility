package lotr.common.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import lotr.common.tileentity.VesselDrinkTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tileentity.TileEntity;

public class VesselDrink extends LootFunction {
	private VesselDrink(ILootCondition[] conditions) {
		super(conditions);
	}

	// $FF: synthetic method
	VesselDrink(ILootCondition[] x0, Object x1) {
		this(x0);
	}

	@Override
	public LootFunctionType getType() {
		return LOTRLootFunctions.VESSEL_DRINK;
	}

	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		TileEntity te = context.getParamOrNull(LootParameters.BLOCK_ENTITY);
		if (te instanceof VesselDrinkTileEntity) {
			VesselDrinkTileEntity vessel = (VesselDrinkTileEntity) te;
			return vessel.getVesselItem();
		}
		return stack;
	}

	public static Builder vesselDrinkBuilder() {
		return simpleBuilder(VesselDrink::new);
	}

	public static class Serializer extends net.minecraft.loot.LootFunction.Serializer {
		@Override
		public VesselDrink deserialize(JsonObject object, JsonDeserializationContext context, ILootCondition[] conditions) {
			return new VesselDrink(conditions);
		}
	}
}
