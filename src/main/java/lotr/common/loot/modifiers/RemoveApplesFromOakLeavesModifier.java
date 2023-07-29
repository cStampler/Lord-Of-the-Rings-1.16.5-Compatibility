package lotr.common.loot.modifiers;

import java.util.*;

import javax.annotation.Nonnull;

import com.google.gson.*;

import net.minecraft.block.BlockState;
import net.minecraft.item.*;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.*;

public class RemoveApplesFromOakLeavesModifier extends LootModifier {
	private final List blockNames;

	public RemoveApplesFromOakLeavesModifier(ILootCondition[] conds, List names) {
		super(conds);
		blockNames = names;
	}

	@Override
	@Nonnull
	public List doApply(List generatedLoot, LootContext context) {
		BlockState state = context.getParamOrNull(LootParameters.BLOCK_STATE);
		if (state != null && blockNames.contains(state.getBlock().getRegistryName())) {
			generatedLoot.removeIf(item -> (((ItemStack) item).getItem() == Items.APPLE));
		}

		return generatedLoot;
	}

	public static class Serializer extends GlobalLootModifierSerializer {
		@Override
		public RemoveApplesFromOakLeavesModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditions) {
			List blockNames = new ArrayList();
			JsonArray list = object.get("target_blocks").getAsJsonArray();
			for (JsonElement elem : list) {
				String s = elem.getAsString();
				ResourceLocation blockName = new ResourceLocation(s);
				blockNames.add(blockName);
			}

			return new RemoveApplesFromOakLeavesModifier(conditions, blockNames);
		}

		@Override
		public JsonObject write(IGlobalLootModifier instance) {
			JsonObject obj = makeConditions(((RemoveApplesFromOakLeavesModifier) instance).conditions);
			JsonArray list = new JsonArray();
			Iterator var4 = ((RemoveApplesFromOakLeavesModifier) instance).blockNames.iterator();

			while (var4.hasNext()) {
				ResourceLocation blockName = (ResourceLocation) var4.next();
				list.add(blockName.toString());
			}

			obj.add("target_blocks", list);
			return obj;
		}
	}
}
