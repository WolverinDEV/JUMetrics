package dev.wolveringer.JUMetrics.server.network;

import java.util.concurrent.TimeUnit;

import dev.wolveringer.JUMetrics.network.packets.PacketPing;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ClientPingTimeoutHandler extends ReadTimeoutHandler{
	private final long timeout;
	private ChannelHandlerContext ch;
	private long lastPing = -1;
	
	public ClientPingTimeoutHandler(long timeout, TimeUnit unit) {
		super(timeout, unit);
		this.timeout = unit.toMillis(timeout);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ch = ctx;
		this.lastPing = System.currentTimeMillis();
		super.channelActive(ctx);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof PacketPing){
			lastPing = System.currentTimeMillis();
			PacketPing pack = (PacketPing) msg;
			ctx.write(new PacketPing(pack.getId()));
		}
		super.channelRead(ctx, msg);
	}
	
	public static class ClientReadTimeout extends Exception {
		public ClientReadTimeout(String message) {
			super(message);
		}
	}
	
	@RequiredArgsConstructor
	class ChannelIdleTask implements Runnable{
		@Getter
		private ScheduledFuture<?> scheduler;
		
		public void run() {
			if (!ch.channel().isOpen()) {
	            return;
	        }

	        long nextDelay = timeout;
	        nextDelay -= System.currentTimeMillis() - lastPing;

	        if (nextDelay <= 0) {
	            // Reader is idle - set a new timeout and notify the callback.
	        	scheduler = ch.executor().schedule(this, timeout, TimeUnit.MILLISECONDS);
	        	ch.fireExceptionCaught(new ClientReadTimeout("Client dont send a ping packet."));
	        } else {
	            // Read occurred before the timeout - set a new timeout with shorter delay.
	        	scheduler = ch.executor().schedule(this, nextDelay, TimeUnit.MILLISECONDS);
	        }
		}
	}
}