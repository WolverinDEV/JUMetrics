package dev.wolveringer.JUMetrics.server.network.packet;

import java.util.List;

import org.apache.commons.lang3.Validate;

import dev.wolveringer.JUMetrics.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.Channel.Unsafe;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.ByteToMessageDecoder;

public class PacketDecoder extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int packetId = in.readInt();
		Validate.isTrue(packetId > -1, "Invalid packet id (State: "+packetId+")");
		out.add(Packet.createPacket(packetId, in));
		Validate.isTrue(in.readableBytes() == 0, "Did not read all bytes from buffer.");
	}

}
