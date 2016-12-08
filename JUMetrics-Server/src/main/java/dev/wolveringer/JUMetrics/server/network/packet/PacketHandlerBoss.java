package dev.wolveringer.JUMetrics.server.network.packet;

import dev.wolveringer.JUMetrics.network.packet.Packet;
import dev.wolveringer.JUMetrics.network.packet.PacketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;

public class PacketHandlerBoss extends ChannelInboundHandlerAdapter {
	@Getter
	@Setter
	private PacketHandler handler;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (handler == null) {
			super.channelRead(ctx, msg);
			return;
		}

		if (msg instanceof Packet) {
			((Packet) msg).handle(handler);
		} else
			super.channelRead(ctx, msg);
	}
}
