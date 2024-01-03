package lotr.common.loot.modifiers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

public class RemoveApplesFromOakLeavesModifier extends LootModifier {
	private final List<ResourceLocation> blockNames;

	public RemoveApplesFromOakLeavesModifier(ILootCondition[] conds, List<ResourceLocation> names) {
		super(conds);
		blockNames = names;
	}

	@Override
	@Nonnull
	public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		BlockState state = context.getParamOrNull(LootParameters.BLOCK_STATE);
		if (state != null && blockNames.contains(state.getBlock().getRegistryName())) {
			generatedLoot.removeIf(item -> (item.getItem() == Items.APPLE));
		}

		return generatedLoot;
	}

	public static class Serializer extends GlobalLootModifierSerializer<RemoveApplesFromOakLeavesModifier> {
		@Override
		public RemoveApplesFromOakLeavesModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditions) {
			List<ResourceLocation> blockNames = new ArrayList<>();
			JsonArray list = object.get("target_blocks").getAsJsonArray();
			for (JsonElement elem : list) {
				String s = elem.getAsString();
				ResourceLocation blockName = new ResourceLocation(s);
				blockNames.add(blockName);
			}

			return new RemoveApplesFromOakLeavesModifier(conditions, blockNames);
		}

		@Override
		public JsonObject write(RemoveApplesFromOakLeavesModifier instance) {
			JsonObject obj = makeConditions(instance.conditions);
			JsonArray list = new JsonArray();
			for (ResourceLocation blockName : instance.blockNames)
				list.add(blockName.toString()); 
			obj.add("target_blocks", (JsonElement)list);
			return obj;
		}
	}
}
