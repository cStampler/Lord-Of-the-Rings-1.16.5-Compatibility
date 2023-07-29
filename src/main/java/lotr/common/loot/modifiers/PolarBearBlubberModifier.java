package lotr.common.loot.modifiers;

import java.util.*;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.common.collect.*;
import com.google.gson.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.*;

public class PolarBearBlubberModifier extends LootModifier {
	private final LootEntry extraEntry;

	public PolarBearBlubberModifier(ILootCondition[] conds, LootEntry extraEntry) {
		super(conds);
		this.extraEntry = extraEntry;
	}

	@Override
	@Nonnull
	public List doApply(List generatedLoot, LootContext context) {
		Entity entity = context.getParamOrNull(LootParameters.THIS_ENTITY);
		if (entity instanceof PolarBearEntity) {
			Consumer stacksOut = stack -> {
				generatedLoot.add(stack);
			};
			stacksOut = LootTable.createStackSplitter(stacksOut);
			Consumer consumer = ILootFunction.decorate(LootFunctionManager.IDENTITY, stacksOut, context);
			generateExtraLootEntry(consumer, context);
		}

		return generatedLoot;
	}

	private void generateExtraLootEntry(Consumer consumer, LootContext context) {
		Random random = context.getRandom();
		List lootGens = Lists.newArrayList();
		MutableInt totalWeight = new MutableInt();
		UnmodifiableIterator var6 = ImmutableList.of(extraEntry).iterator();

		while (var6.hasNext()) {
			LootEntry lootentry = (LootEntry) var6.next();
			lootentry.expand(context, gen -> {
				int weight = gen.getWeight(context.getLuck());
				if (weight > 0) {
					lootGens.add(gen);
					totalWeight.add(weight);
				}

			});
		}

		int numLootGens = lootGens.size();
		if (totalWeight.intValue() != 0 && numLootGens != 0) {
			if (numLootGens == 1) {
				((ILootGenerator) lootGens.get(0)).createItemStack(consumer, context);
			} else {
				int weight = random.nextInt(totalWeight.intValue());
				Iterator var8 = lootGens.iterator();

				while (var8.hasNext()) {
					ILootGenerator ilootgenerator = (ILootGenerator) var8.next();
					weight -= ilootgenerator.getWeight(context.getLuck());
					if (weight < 0) {
						ilootgenerator.createItemStack(consumer, context);
						return;
					}
				}
			}
		}

	}

	public static class Serializer extends GlobalLootModifierSerializer {
		private static final Gson GSON_WITH_LOOT_ENTRY_ADAPTER = LootSerializers.createFunctionSerializer().create();

		@Override
		public PolarBearBlubberModifier read(ResourceLocation name, JsonObject obj, ILootCondition[] conditions) {
			LootEntry extraEntry = GSON_WITH_LOOT_ENTRY_ADAPTER.fromJson(obj.get("extra_entry"), LootEntry.class);
			return new PolarBearBlubberModifier(conditions, extraEntry);
		}

		@Override
		public JsonObject write(IGlobalLootModifier instance) {
			JsonObject obj = makeConditions(((PolarBearBlubberModifier) instance).conditions);
			obj.add("extra_entry", GSON_WITH_LOOT_ENTRY_ADAPTER.toJsonTree(((PolarBearBlubberModifier) instance).extraEntry));
			return obj;
		}
	}
}
