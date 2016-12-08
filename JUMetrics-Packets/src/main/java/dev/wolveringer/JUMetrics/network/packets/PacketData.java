package dev.wolveringer.JUMetrics.network.packets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import dev.wolveringer.JUMetrics.data.DataFactory;
import dev.wolveringer.JUMetrics.data.DataResolver;
import dev.wolveringer.JUMetrics.data.DataType;
import dev.wolveringer.JUMetrics.data.DataValue;
import dev.wolveringer.JUMetrics.network.packet.Packet;
import dev.wolveringer.JUMetrics.network.packet.PacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PacketData extends Packet{
	private List<DataValue> values = new ArrayList<DataValue>();

	@Override
	public void read(ByteBuf buffer, int version) {
		int length = buffer.readInt();
		Validate.isTrue(length < 1024, "Invalid data values length!");
		for(int i = 0;i<length;i++){
			String key = readString(buffer);
			DataType type = DataType.values()[buffer.readInt()];
			Object val;
			switch (type) {
			case BOOLEAN:
				val = buffer.readBoolean();
				break;
			case BYTE:
				val = buffer.readByte();
				break;
			case INT:
				val = buffer.readInt();
				break;
			case LONG:
				val = buffer.readLong();
				break;
			case STRING:
				val = readString(buffer);
				break;
			case BYTE_ARRAY:
				int blength = buffer.readInt();
				Validate.isTrue(blength < Short.MAX_VALUE, "Invalid data value length!");
			default:
				throw new UnsupportedOperationException("Cant find reader for "+type);
			}
			
			values.add(new DataValue(DataFactory.registerKey(key, type, DataResolver.NO_RESOLVER), val, buffer.readLong()));
		}
	}

	@Override
	public void write(ByteBuf buffer, int version) {
		buffer.writeInt(values.size());
		for(DataValue val : values){
			writeString(buffer, val.getKey().getKey());
			buffer.writeInt(val.getKey().getType().ordinal());
			switch (val.getKey().getType()) {
			case BOOLEAN:
				buffer.writeBoolean((Boolean) val.getValue());
				break;
			case BYTE:
				buffer.writeByte((Byte) val.getValue());
				break;
			case INT:
				buffer.writeInt((Integer) val.getValue());
				break;
			case LONG:
				buffer.writeLong(((Long) val.getValue()).longValue());
				break;
			case STRING:
				writeString(buffer, (String) val.getValue());
				break;
			case BYTE_ARRAY:
				byte[] data = val.getValue() instanceof Byte[] ? (byte[]) val.getValue() : toByte((Byte[]) val.getValue());
				buffer.writeInt(data.length);
				buffer.writeBytes(data);
				break;
			default:
				throw new UnsupportedOperationException("Cant find writer for "+val.getKey().getType());
			}
			buffer.writeLong(val.getTimestamp());
		}
	}
	
	private byte[] toByte(Byte[] bytes){
		byte[] _new = new byte[bytes.length];
		for(int i = 0;i<bytes.length;i++)
			_new[i] = bytes[i];
		return _new;
	}

	@Override
	public void handle(PacketHandler handler) throws Exception {
		handler.handle(this);
	}
}
