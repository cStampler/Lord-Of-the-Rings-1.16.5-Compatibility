package lotr.common.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;

public class MessageDataModule extends PlayerDataModule {
	private Set sentMessages = new HashSet();

	protected MessageDataModule(LOTRPlayerData pd) {
		super(pd);
	}

	@Override
	public void load(CompoundNBT playerNBT) {
		sentMessages.clear();
		ListNBT sentMessageTags = playerNBT.getList("SentMessageTypes", 8);

		for (int i = 0; i < sentMessageTags.size(); ++i) {
			String messageName = sentMessageTags.getString(i);
			PlayerMessageType messageType = PlayerMessageType.forSaveName(messageName);
			if (messageType != null) {
				sentMessages.add(messageType);
			} else {
				playerData.logPlayerError("Loaded nonexistent player message type %s", messageName);
			}
		}

	}

	@Override
	public void save(CompoundNBT playerNBT) {
		ListNBT sentMessageTags = new ListNBT();
		Iterator var3 = sentMessages.iterator();

		while (var3.hasNext()) {
			PlayerMessageType message = (PlayerMessageType) var3.next();
			sentMessageTags.add(StringNBT.valueOf(message.getSaveName()));
		}

		playerNBT.put("SentMessageTypes", sentMessageTags);
	}

	public void sendMessageIfNotReceived(PlayerMessageType message) {
		executeIfPlayerExistsServerside(player -> {
			if (!sentMessages.contains(message)) {
				sentMessages.add(message);
				markDirty();
				message.displayTo((ServerPlayerEntity) player, false);
			}

		});
	}
}
