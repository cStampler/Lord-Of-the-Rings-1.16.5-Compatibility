package lotr.common.entity.item;

import java.util.function.Supplier;

import lotr.common.init.LOTRBlocks;
import lotr.common.init.LOTREntities;
import lotr.common.init.LOTRItems;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class LOTRBoatEntity extends BoatEntity {
	private static final DataParameter MOD_BOAT_TYPE;

	static {
		MOD_BOAT_TYPE = EntityDataManager.defineId(LOTRBoatEntity.class, DataSerializers.INT);
	}

	public LOTRBoatEntity(EntityType type, World w) {
		super(type, w);
		blocksBuilding = true;
	}

	public LOTRBoatEntity(World w, double x, double y, double z) {
		this((EntityType) LOTREntities.BOAT.get(), w);
		setPos(x, y, z);
		this.setDeltaMovement(Vector3d.ZERO);
		xo = x;
		yo = y;
		zo = z;
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putString("ModType", getModBoatType().getName());
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(MOD_BOAT_TYPE, LOTRBoatEntity.ModBoatType.PINE.ordinal());
	}

	@Override
	public IPacket getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public Item getDropItem() {
		return getModBoatType().asBoatItem();
	}

	public LOTRBoatEntity.ModBoatType getModBoatType() {
		return LOTRBoatEntity.ModBoatType.byId((Integer) entityData.get(MOD_BOAT_TYPE));
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		if (nbt.contains("ModType", 8)) {
			setModBoatType(LOTRBoatEntity.ModBoatType.getTypeFromString(nbt.getString("ModType")));
		}

	}

	public void setModBoatType(LOTRBoatEntity.ModBoatType type) {
		entityData.set(MOD_BOAT_TYPE, type.ordinal());
	}

	@Override
	public ItemEntity spawnAtLocation(IItemProvider item) {
		if (item == getBoatType().getPlanks()) {
			item = getModBoatType().asPlank();
		}

		return super.spawnAtLocation(item);
	}

	public enum ModBoatType {
		PINE(LOTRBlocks.PINE_PLANKS, LOTRItems.PINE_BOAT, "pine"), MALLORN(LOTRBlocks.MALLORN_PLANKS, LOTRItems.MALLORN_BOAT, "mallorn"), MIRK_OAK(LOTRBlocks.MIRK_OAK_PLANKS, LOTRItems.MIRK_OAK_BOAT, "mirk_oak"), CHARRED(LOTRBlocks.CHARRED_PLANKS, LOTRItems.CHARRED_BOAT, "charred"), APPLE(LOTRBlocks.APPLE_PLANKS, LOTRItems.APPLE_BOAT, "apple"), PEAR(LOTRBlocks.PEAR_PLANKS, LOTRItems.PEAR_BOAT, "pear"), CHERRY(LOTRBlocks.CHERRY_PLANKS, LOTRItems.CHERRY_BOAT, "cherry"), LEBETHRON(LOTRBlocks.LEBETHRON_PLANKS, LOTRItems.LEBETHRON_BOAT, "lebethron"), BEECH(LOTRBlocks.BEECH_PLANKS, LOTRItems.BEECH_BOAT, "beech"), MAPLE(LOTRBlocks.MAPLE_PLANKS, LOTRItems.MAPLE_BOAT, "maple"), ASPEN(LOTRBlocks.ASPEN_PLANKS, LOTRItems.ASPEN_BOAT, "aspen"), LAIRELOSSE(LOTRBlocks.LAIRELOSSE_PLANKS, LOTRItems.LAIRELOSSE_BOAT, "lairelosse"), CEDAR(LOTRBlocks.CEDAR_PLANKS, LOTRItems.CEDAR_BOAT, "cedar"), FIR(LOTRBlocks.FIR_PLANKS, LOTRItems.FIR_BOAT, "fir"), LARCH(LOTRBlocks.LARCH_PLANKS, LOTRItems.LARCH_BOAT, "larch"), HOLLY(LOTRBlocks.HOLLY_PLANKS, LOTRItems.HOLLY_BOAT, "holly"), GREEN_OAK(LOTRBlocks.GREEN_OAK_PLANKS, LOTRItems.GREEN_OAK_BOAT, "green_oak"), CYPRESS(LOTRBlocks.CYPRESS_PLANKS, LOTRItems.CYPRESS_BOAT, "cypress"), ROTTEN(LOTRBlocks.ROTTEN_PLANKS, LOTRItems.ROTTEN_BOAT, "rotten"), CULUMALDA(LOTRBlocks.CULUMALDA_PLANKS, LOTRItems.CULUMALDA_BOAT, "culumalda");

		private final Supplier plankSup;
		private final Supplier boatSup;
		private final String name;

		ModBoatType(Supplier plank, Supplier boat, String s) {
			plankSup = plank;
			boatSup = boat;
			name = s;
		}

		public Item asBoatItem() {
			return (Item) boatSup.get();
		}

		public Block asPlank() {
			return (Block) plankSup.get();
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}

		public static LOTRBoatEntity.ModBoatType byId(int id) {
			LOTRBoatEntity.ModBoatType[] types = values();
			if (id < 0 || id >= types.length) {
				id = 0;
			}

			return types[id];
		}

		public static LOTRBoatEntity.ModBoatType getTypeFromString(String nameIn) {
			LOTRBoatEntity.ModBoatType[] types = values();

			for (ModBoatType type : types) {
				if (type.getName().equals(nameIn)) {
					return type;
				}
			}

			return types[0];
		}
	}
}
