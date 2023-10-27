package lotr.common;

import java.io.File;
import java.util.Optional;

import lotr.common.entity.item.RingPortalEntity;
import lotr.common.network.SPacketSetAttackTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class LOTRServerProxy implements LOTRProxy {
	@Override
	public PlayerEntity getClientPlayer() {
		throw new UnsupportedOperationException("Cannot get the client player on the server side");
	}

	@Override
	public World getClientWorld() {
		throw new UnsupportedOperationException("Cannot get the client world on the server side");
	}

	@Override
	public File getGameRootDirectory() {
		return ServerLifecycleHooks.getCurrentServer().getServerDirectory();
	}

	@Override
	public Optional<LivingEntity> getSidedAttackTarget(MobEntity entity) {
		return Optional.ofNullable(entity.getTarget());
	}

	@Override
	public boolean isClient() {
		return false;
	}

	@Override
	public boolean isSingleplayer() {
		return false;
	}

	@Override
	public void receiveClientAttackTarget(SPacketSetAttackTarget packet) {
		throw new UnsupportedOperationException("Cannot receive client attack targets on the server side");
	}

	@Override
	public void setInRingPortal(Entity entity, RingPortalEntity portal) {
		LOTRMod.serverTickHandler.prepareRingPortal(entity, portal);
	}
}
