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
public class PacketPing extends Packet{
	private int id;
	
	@Override
	public void read(ByteBuf buffer, int version) {
		this.id = buffer.readInt();
	}

	@Override
	public void write(ByteBuf buffer, int version) {
		buffer.writeInt(id);
	}
	
	@Override
	public void handle(PacketHandler handler) throws Exception {
		handler.handle((PacketPing) this);
	}
}
