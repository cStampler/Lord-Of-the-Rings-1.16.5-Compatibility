package lotr.common.loot.modifiers;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.loot.ILootGenerator;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootSerializers;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

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
