package lotr.common.item;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import lotr.common.dispenser.DispensePlate;
import lotr.common.entity.projectile.ThrownPlateEntity;
import lotr.common.init.LOTRMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.GameData;

public class PlateItem extends LOTRArmorItem {
	private final BlockItem internalBlockItem;

	public PlateItem(Block block, Properties properties) {
		super(LOTRMaterial.COSMETIC, EquipmentSlotType.HEAD, properties);
		internalBlockItem = new BlockItem(block, properties);
		DispenserBlock.registerBehavior(this, new DispensePlate());
		GameData.getBlockItemMap().put(block, this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List tooltip, ITooltipFlag flag) {
		internalBlockItem.appendHoverText(stack, world, tooltip, flag);
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList items) {
		internalBlockItem.fillItemCategory(group, items);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return PlayerContainer.BLOCK_ATLAS.toString();
	}

	public Block getBlock() {
		return internalBlockItem.getBlock();
	}

	@Override
	public Multimap getDefaultAttributeModifiers(EquipmentSlotType slot) {
		Multimap map = HashMultimap.create(super.getDefaultAttributeModifiers(slot));
		map.removeAll(Attributes.ARMOR);
		map.removeAll(Attributes.ARMOR_TOUGHNESS);
		return map;
	}

	@Override
	public String getDescriptionId() {
		return internalBlockItem.getDescriptionId();
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 64;
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (!world.isClientSide) {
			ThrownPlateEntity plate = new ThrownPlateEntity(world, heldItem, player);
			plate.setThrownRetrograde(hand == Hand.OFF_HAND);
			plate.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 1.5F, 1.0F);
			world.addFreshEntity(plate);
		}

		world.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
		player.awardStat(Stats.ITEM_USED.get(this));
		if (!player.abilities.instabuild) {
			heldItem.shrink(1);
		}

		return ActionResult.success(heldItem);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		return internalBlockItem.useOn(context);
	}
}
