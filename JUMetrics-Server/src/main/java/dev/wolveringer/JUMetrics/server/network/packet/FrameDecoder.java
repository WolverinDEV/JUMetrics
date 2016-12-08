package dev.wolveringer.JUMetrics.server.network.packet;

import java.util.List;

import org.apache.commons.lang3.Validate;

import dev.wolveringer.JUMetrics.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class FrameDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int length = in.readInt();
		Validate.isTrue(length < Packet.MAX_LENGTH, "Try to read a too long packet (Size: " + length + ")");

		if (in.readableBytes() < length) {
			in.resetReaderIndex();
			return;
		}

		if (in.hasMemoryAddress()) {
			out.add(in.slice(in.readerIndex(), length).retain());
			in.skipBytes(length);
		} else {
			ByteBuf dst = ctx.alloc().directBuffer(length);
			in.readBytes(dst);
			out.add(dst);
		}
	}

}
