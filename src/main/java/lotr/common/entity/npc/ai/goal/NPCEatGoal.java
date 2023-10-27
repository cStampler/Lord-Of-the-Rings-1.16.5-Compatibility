package lotr.common.entity.npc.ai.goal;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.entity.npc.data.NPCFoodPool;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class NPCEatGoal extends NPCConsumeGoal {
	private final NPCFoodPool foodPool;

	public NPCEatGoal(NPCEntity entity, NPCFoodPool foods, int chance) {
		super(entity, chance);
		foodPool = foods;
	}

	private void addItemParticles(ItemStack itemstack, int count) {
		for (int i = 0; i < count; ++i) {
			Vector3d motion = new Vector3d((rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			motion = motion.xRot((float) Math.toRadians(-theEntity.xRot));
			motion = motion.yRot((float) Math.toRadians(-theEntity.yRot));
			Vector3d pos = new Vector3d((rand.nextFloat() - 0.5D) * 0.3D, -rand.nextFloat() * 0.6D - 0.3D, 0.6D);
			pos = pos.xRot((float) Math.toRadians(-theEntity.xRot));
			pos = pos.yRot((float) Math.toRadians(-theEntity.yRot));
			pos = pos.add(theEntity.getX(), theEntity.getEyeY(), theEntity.getZ());
			((ServerWorld) theEntity.level).sendParticles(new ItemParticleData(ParticleTypes.ITEM, itemstack), pos.x, pos.y, pos.z, 1, motion.x, motion.y + 0.05D, motion.z, 0.0D);
		}

	}

	@Override
	protected void consume() {
		ItemStack itemstack = getHeldConsumingItem();
		addItemParticles(itemstack, 16);
		playEatSound(itemstack);
		Item item = itemstack.getItem();
		if (item.isEdible()) {
			Food food = item.getFoodProperties();
			theEntity.heal(food.getNutrition());
		}

	}

	@Override
	protected ItemStack createConsumable() {
		return foodPool.getRandomFood(rand);
	}

	private void playEatSound(ItemStack itemstack) {
		theEntity.playSound(theEntity.getEatingSound(itemstack), 0.5F + 0.5F * rand.nextInt(2), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
	}

	@Override
	protected void updateConsumeTick(int tick) {
		if (tick % 4 == 0) {
			ItemStack itemstack = getHeldConsumingItem();
			addItemParticles(itemstack, 5);
			playEatSound(itemstack);
		}

	}
}
