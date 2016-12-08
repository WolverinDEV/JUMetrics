package dev.wolveringer.JUMetrics.server.network.packet;

import org.apache.commons.lang3.Validate;

import dev.wolveringer.JUMetrics.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class FrameEncoder extends MessageToByteEncoder<ByteBuf>{

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		int length = msg.readableBytes();
		Validate.isTrue(length < Packet.MAX_LENGTH, "Packet length is to long!");
		
		out.ensureWritable(length + 4);
		out.writeInt(length);
		out.writeBytes(msg);
		
	}

}
