package lotr.common.loot.functions;

import com.google.gson.*;

import lotr.common.fac.*;
import lotr.common.item.PouchItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

public class SetPouchColorFromEntityFaction extends LootFunction {
	private final float chance;

	private SetPouchColorFromEntityFaction(ILootCondition[] conditions, float chance) {
		super(conditions);
		this.chance = chance;
	}

	// $FF: synthetic method
	SetPouchColorFromEntityFaction(ILootCondition[] x0, float x1, Object x2) {
		this(x0, x1);
	}

	@Override
	public LootFunctionType getType() {
		return LOTRLootFunctions.SET_POUCH_COLOR_FROM_ENTITY_FACTION;
	}

	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		if (context.getRandom().nextFloat() < chance && stack.getItem() instanceof PouchItem) {
			Entity entity = context.getParamOrNull(LootParameters.THIS_ENTITY);
			if (entity != null) {
				Faction faction = EntityFactionHelper.getFaction(entity);
				if (faction.isPlayableAlignmentFaction()) {
					PouchItem.setPouchDyedByFaction(stack, faction);
				}
			}
		}

		return stack;
	}

	public static Builder setPouchColorFromEntityFactionBuilder(float chance) {
		return simpleBuilder(conditions -> new SetPouchColorFromEntityFaction(conditions, chance));
	}

	public static class Serializer extends net.minecraft.loot.LootFunction.Serializer {
		@Override
		public SetPouchColorFromEntityFaction deserialize(JsonObject object, JsonDeserializationContext context, ILootCondition[] conditions) {
			float chance = JSONUtils.getAsFloat(object, "chance", 1.0F);
			return new SetPouchColorFromEntityFaction(conditions, chance);
		}

		public void serialize(JsonObject object, SetPouchColorFromEntityFaction function, JsonSerializationContext context) {
			super.serialize(object, function, context);
			object.addProperty("chance", function.chance);
		}
	}
}
