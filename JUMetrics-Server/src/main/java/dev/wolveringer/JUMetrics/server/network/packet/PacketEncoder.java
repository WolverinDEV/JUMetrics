package dev.wolveringer.JUMetrics.server.network.packet;

import org.apache.commons.lang3.Validate;

import dev.wolveringer.JUMetrics.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class PacketEncoder extends MessageToByteEncoder<Packet>{
	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
		int packetId = Packet.getPacketId(msg.getClass());
		Validate.isTrue(packetId > -1, "Cant find the packet id!");
		out.writeInt(packetId);
		msg.write(out, 0);
		
	}
}
