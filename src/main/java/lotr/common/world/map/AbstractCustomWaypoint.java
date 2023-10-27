package lotr.common.world.map;

import java.util.UUID;

import javax.annotation.Nullable;

import lotr.common.data.DataUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractCustomWaypoint extends VerticallyPositionedWaypoint {
	public static final int MAX_NAME_LENGTH = 40;
	public static final int MAX_LORE_LENGTH = 160;
	private final UUID createdPlayer;
	private final int id;
	private String name;
	private String lore;
	private final BlockPos worldPos;
	private final double mapX;
	private final double mapZ;

	public AbstractCustomWaypoint(MapSettings map, UUID createdPlayer, int id, String name, String lore, BlockPos worldPos) {
		this.createdPlayer = createdPlayer;
		this.id = id;
		this.name = name;
		this.lore = lore;
		this.worldPos = worldPos;
		mapX = map.worldToMapX_frac(worldPos.getX());
		mapZ = map.worldToMapZ_frac(worldPos.getZ());
	}

	public UUID getCreatedPlayer() {
		return createdPlayer;
	}

	public int getCustomId() {
		return id;
	}

	@Override
	@Nullable
	public ITextComponent getDisplayLore() {
		return !lore.isEmpty() ? new StringTextComponent(lore) : null;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent(name);
	}

	@Override
	public Waypoint.WaypointDisplayState getDisplayState(PlayerEntity player) {
		return Waypoint.WaypointDisplayState.CUSTOM;
	}

	@Override
	public double getMapX() {
		return mapX;
	}

	@Override
	public double getMapZ() {
		return mapZ;
	}

	@Override
	public ITextComponent getNotUnlockedMessage(PlayerEntity player) {
		return StringTextComponent.EMPTY;
	}

	public BlockPos getPosition() {
		return worldPos;
	}

	public String getRawLore() {
		return lore;
	}

	@Override
	public String getRawName() {
		return name;
	}

	@Override
	public BlockPos getTravelPosition(ServerWorld world, PlayerEntity player) {
		return CustomWaypointStructureHandler.INSTANCE.findRandomTravelPositionForCompletedWaypoint(world, this, player);
	}

	@Override
	public int getWorldX() {
		return worldPos.getX();
	}

	@Override
	public int getWorldY() {
		return worldPos.getY();
	}

	@Override
	public int getWorldZ() {
		return worldPos.getZ();
	}

	@Override
	public boolean hasPlayerUnlocked(PlayerEntity player) {
		return true;
	}

	@Override
	public boolean isCustom() {
		return true;
	}

	@Override
	public boolean isSharedCustom() {
		return false;
	}

	@Override
	public boolean isSharedHidden() {
		return false;
	}

	protected abstract void removeFromPlayerData(PlayerEntity var1);

	protected void save(CompoundNBT nbt) {
		nbt.putUUID("CreatedPlayer", createdPlayer);
		nbt.putInt("ID", id);
		nbt.putString("Name", name);
		nbt.putString("Lore", lore);
		nbt.putInt("PosX", worldPos.getX());
		nbt.putInt("PosY", worldPos.getY());
		nbt.putInt("PosZ", worldPos.getZ());
	}

	protected void setLore(String lore) {
		this.lore = lore;
	}

	protected void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean verifyFastTravellable(ServerWorld world, PlayerEntity player) {
		return CustomWaypointStructureHandler.INSTANCE.checkCompletedWaypointHasMarkerAndHandleIfNot(world, this, player);
	}

	protected void write(PacketBuffer buf) {
		buf.writeUUID(createdPlayer);
		buf.writeVarInt(id);
		buf.writeUtf(name);
		buf.writeUtf(lore);
		buf.writeBlockPos(worldPos);
	}

	protected static AbstractCustomWaypoint baseLoad(MapSettings map, CompoundNBT nbt, AbstractCustomWaypoint.AbstractCustomWaypointConstructor constructor) {
		UUID createdPlayer = DataUtil.getUniqueIdBackCompat(nbt, "CreatedPlayer");
		int id = nbt.getInt("ID");
		String name = nbt.getString("Name");
		String lore = nbt.getString("Lore");
		int posX = nbt.getInt("PosX");
		int posY = nbt.getInt("PosY");
		int posZ = nbt.getInt("PosZ");
		BlockPos worldPos = new BlockPos(posX, posY, posZ);
		return constructor.create(map, createdPlayer, id, name, lore, worldPos);
	}

	protected static AbstractCustomWaypoint baseRead(MapSettings map, PacketBuffer buf, AbstractCustomWaypoint.AbstractCustomWaypointConstructor constructor) {
		UUID createdPlayer = buf.readUUID();
		int id = buf.readVarInt();
		String name = buf.readUtf();
		String lore = buf.readUtf();
		BlockPos worldPos = buf.readBlockPos();
		return constructor.create(map, createdPlayer, id, name, lore, worldPos);
	}

	@FunctionalInterface
	protected interface AbstractCustomWaypointConstructor {
		AbstractCustomWaypoint create(MapSettings var1, UUID var2, int var3, String var4, String var5, BlockPos var6);
	}
}
