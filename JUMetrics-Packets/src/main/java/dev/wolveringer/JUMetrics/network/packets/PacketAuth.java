package dev.wolveringer.JUMetrics.network.packets;

import dev.wolveringer.JUMetrics.network.packet.Packet;
import dev.wolveringer.JUMetrics.network.packet.PacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PacketAuth extends Packet{
	private int protocolVersion;
	private String aplicationName;
	private int connectionMode;
	
	@Override
	public void read(ByteBuf buffer, int version) {
		protocolVersion = buffer.readInt();
		aplicationName = readString(buffer);
		connectionMode = buffer.readInt();
	}

	@Override
	public void write(ByteBuf buffer, int version) {
		buffer.writeInt(protocolVersion);
		writeString(buffer, aplicationName);
		buffer.writeInt(connectionMode);
	}

	@Override
	public void handle(PacketHandler handler) throws Exception {
		handler.handle(this);
	}
	
}
