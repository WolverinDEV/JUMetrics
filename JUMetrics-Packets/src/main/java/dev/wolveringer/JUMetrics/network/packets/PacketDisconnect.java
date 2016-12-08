package dev.wolveringer.JUMetrics.network.packets;

import dev.wolveringer.JUMetrics.network.packet.Packet;
import dev.wolveringer.JUMetrics.network.packet.PacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PacketDisconnect extends Packet{
	private int state;
	private String message;
	
	@Override
	public void read(ByteBuf buffer, int version) {
		state = buffer.readInt();
		message = readString(buffer);
	}
	
	@Override
	public void write(ByteBuf buffer, int version) {
		buffer.writeInt(state);
		writeString(buffer, message);
	}
	@Override
	public void handle(PacketHandler handler) throws Exception{
		handler.handle((PacketDisconnect) this);
	}
}
