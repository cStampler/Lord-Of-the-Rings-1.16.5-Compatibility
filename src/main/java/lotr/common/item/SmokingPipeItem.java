package lotr.common.item;

import java.util.List;

import lotr.common.entity.projectile.SmokeRingEntity;
import lotr.common.init.LOTRItemGroups;
import lotr.common.init.LOTRItems;
import lotr.common.init.LOTRSoundEvents;
import lotr.common.util.LOTRUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SmokingPipeItem extends Item {
	public SmokingPipeItem() {
		this(new Properties().durability(300).tab(LOTRItemGroups.MISC));
	}

	public SmokingPipeItem(Properties properties) {
		super(properties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World world, List tooltip, ITooltipFlag flag) {
		DyeColor color = getSmokeColor(stack);
		tooltip.add(new TranslationTextComponent(String.format("%s.%s", getOrCreateDescriptionId(), color.getName())).withStyle(TextFormatting.GRAY));
		if (isMagicSmoke(stack)) {
			tooltip.add(new TranslationTextComponent(String.format("%s.%s", getOrCreateDescriptionId(), "magic")).withStyle(TextFormatting.GRAY));
		}

	}

	private boolean canSmoke(LivingEntity entity) {
		if (!(entity instanceof PlayerEntity)) {
			return true;
		}
		PlayerEntity player = (PlayerEntity) entity;
		if (player.abilities.instabuild) {
			return true;
		}
		ItemStack smokedItem = LOTRUtil.findHeldOrInventoryItem(player, hummel -> isSmokable((ItemStack) hummel));
		return !smokedItem.isEmpty();
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList items) {
		if (allowdedIn(group)) {
			DyeColor[] var3 = DyeColor.values();
			int var4 = var3.length;

			int var5;
			DyeColor color;
			ItemStack pipe;
			for (var5 = 0; var5 < var4; ++var5) {
				color = var3[var5];
				pipe = new ItemStack(this);
				setSmokeColor(pipe, color);
				items.add(pipe);
			}

			var3 = DyeColor.values();
			var4 = var3.length;

			for (var5 = 0; var5 < var4; ++var5) {
				color = var3[var5];
				pipe = new ItemStack(this);
				setSmokeColor(pipe, color);
				setMagicSmoke(pipe, true);
				items.add(pipe);
			}
		}

	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		if (canSmoke(entity)) {
			stack.hurtAndBreak(1, entity, e -> {
				e.broadcastBreakEvent(e.getUsedItemHand());
			});
			if (entity instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entity;
				ItemStack smokedItem = LOTRUtil.findHeldOrInventoryItem(player, hummel -> isSmokable((ItemStack) hummel));
				if (!smokedItem.isEmpty()) {
					LOTRUtil.consumeOneInventoryItem(player, smokedItem);
				}

				if (player.canEat(false)) {
					player.getFoodData().eat(2, 0.3F);
				}

				player.awardStat(Stats.ITEM_USED.get(this));
			} else {
				entity.heal(2.0F);
			}

			if (!world.isClientSide) {
				SmokeRingEntity smoke = new SmokeRingEntity(world, entity);
				DyeColor color = getSmokeColor(stack);
				boolean magic = isMagicSmoke(stack);
				smoke.setSmokeColor(color);
				smoke.setMagicSmoke(magic);
				float speed = 0.1F;
				smoke.shootFromRotation(entity, entity.xRot, entity.yRot, 0.0F, speed, 0.0F);
				world.addFreshEntity(smoke);
			}

			entity.playSound(LOTRSoundEvents.SMOKE_PUFF, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
		}

		return stack;
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 40;
	}

	private boolean isSmokable(ItemStack stack) {
		return stack.getItem() == LOTRItems.PIPEWEED.get();
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (canSmoke(player)) {
			player.startUsingItem(hand);
			return ActionResult.consume(heldItem);
		}
		return ActionResult.fail(heldItem);
	}

	public static void clearSmokeColor(ItemStack stack) {
		setSmokeColor(stack, DyeColor.WHITE);
	}

	private static DyeColor getSavedSmokeColor(ItemStack stack) {
		CompoundNBT nbt = stack.getTagElement("pipe");
		return nbt != null && nbt.contains("color", 8) ? DyeColor.byName(nbt.getString("color"), DyeColor.WHITE) : null;
	}

	public static DyeColor getSmokeColor(ItemStack stack) {
		DyeColor color = getSavedSmokeColor(stack);
		return color != null ? color : DyeColor.WHITE;
	}

	public static boolean isMagicSmoke(ItemStack stack) {
		CompoundNBT nbt = stack.getTagElement("pipe");
		return nbt != null ? nbt.getBoolean("magic") : false;
	}

	public static boolean isSmokeDyed(ItemStack stack) {
		DyeColor color = getSavedSmokeColor(stack);
		return color != null && color != DyeColor.WHITE;
	}

	public static void setMagicSmoke(ItemStack stack, boolean flag) {
		stack.getOrCreateTagElement("pipe").putBoolean("magic", flag);
	}

	public static void setSmokeColor(ItemStack stack, DyeColor color) {
		stack.getOrCreateTagElement("pipe").putString("color", color.getSerializedName());
	}
}
