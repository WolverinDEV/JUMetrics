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
public class PacketAuthResponse extends Packet{
	private int state;
	private String session;
	private int maxCallbackTime;
	
	@Override
	public void read(ByteBuf buffer, int version) {
		this.state = buffer.readInt();
		this.session = readString(buffer);
		this.maxCallbackTime = buffer.readInt();
	}

	@Override
	public void write(ByteBuf buffer, int version) {
		buffer.writeInt(state);
		writeString(buffer, session);
		buffer.writeInt(maxCallbackTime);
	}

	@Override
	public void handle(PacketHandler handler) throws Exception {
		handler.handle(this);
	}

}
