package lotr.common.entity;

import java.util.Optional;

import lotr.common.fac.FactionPointer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.IDataSerializer;

public class LOTREntityDataSerializers {
	  public static void register() {
	    DataSerializers.registerSerializer(FACTION_POINTER);
	    DataSerializers.registerSerializer(OPTIONAL_FACTION_POINTER);
	  }
	  
	  public static final IDataSerializer<FactionPointer> FACTION_POINTER = new IDataSerializer<FactionPointer>() {
	      public void write(PacketBuffer buf, FactionPointer value) {
	        buf.writeResourceLocation(value.getName());
	      }
	      
	      public FactionPointer read(PacketBuffer buf) {
	        return FactionPointer.of(buf.readResourceLocation());
	      }
	      
	      public FactionPointer copyValue(FactionPointer value) {
	        return value;
	      }

		@Override
		public FactionPointer copy(FactionPointer p_192717_1_) {
			return FactionPointer.of(p_192717_1_.getName());
		}
	    };
	  
	  public static final IDataSerializer<Optional<FactionPointer>> OPTIONAL_FACTION_POINTER = new IDataSerializer<Optional<FactionPointer>>() {
	      public void write(PacketBuffer buf, Optional<FactionPointer> value) {
	        buf.writeBoolean(value.isPresent());
	        if (value.isPresent())
	          buf.writeResourceLocation(((FactionPointer)value.get()).getName()); 
	      }
	      
	      public Optional<FactionPointer> read(PacketBuffer buf) {
	        return !buf.readBoolean() ? Optional.<FactionPointer>empty() : Optional.<FactionPointer>of(FactionPointer.of(buf.readResourceLocation()));
	      }
	      
	      public Optional<FactionPointer> copyValue(Optional<FactionPointer> value) {
	        return value;
	      }

		@Override
		public Optional<FactionPointer> copy(Optional<FactionPointer> p_192717_1_) {
			return Optional.of(p_192717_1_.get());
		}
	    };
	}
