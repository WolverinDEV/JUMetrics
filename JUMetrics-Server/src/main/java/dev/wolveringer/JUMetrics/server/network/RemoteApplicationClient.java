package dev.wolveringer.JUMetrics.server.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import dev.wolveringer.JUMetrics.network.packet.Packet;
import dev.wolveringer.JUMetrics.network.packets.PacketAuth;
import dev.wolveringer.JUMetrics.network.packets.PacketData;
import dev.wolveringer.JUMetrics.network.packets.PacketDisconnect;
import dev.wolveringer.JUMetrics.server.netty.NettyCompleteChannelFuture;
import dev.wolveringer.JUMetrics.server.network.packet.AbstractServerPacketHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

@Getter
public class RemoteApplicationClient extends AbstractServerPacketHandler {
	private ChannelHandlerContext ctx;

	private boolean closed;
	private List<RemoteApplicationClient> refApplicationClients;
	private int state = 0;
	
	public RemoteApplicationClient(List<RemoteApplicationClient> clients) {
		this.refApplicationClients = clients;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		disconnect("Error occurent!");
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		synchronized (refApplicationClients) {
			refApplicationClients.add(this);
		}
		System.out.println("Client "+getAddress()+" connected!");
		super.channelActive(ctx);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if(!closed)
			System.out.println("Client " + getAddress() + " disconnected (Channel closed)");
		this.ctx = null;
		synchronized (refApplicationClients) {
			refApplicationClients.remove(this);
		}
		super.channelInactive(ctx);
	}

	public void handle(PacketAuth packet) throws Exception {
		
	}
	
	public void handle(PacketDisconnect packet) throws Exception {
		if (!closed) {
			closed = true;
			ctx.close();
			System.out.println("Client " + getAddress() + " disconnected (" + packet.getMessage()+")");
		}
	}

	public void handle(PacketData packet) throws Exception {
		System.out.println("having data packet: " + packet.getValues().size());
		disconnect("Data recived thank you :)");
	}

	public ChannelFuture writePacket(Packet packet) {
		if (closed || ctx == null)
			return new NettyCompleteChannelFuture.FailedChannelFuture(ctx.channel(), null, new Exception("Channel closed!"));
		return ctx.write(packet);
	}

	public void disconnect(String message) {
		if (closed)
			return;
		if(ctx != null)
			ctx.writeAndFlush(new PacketDisconnect(0, message)).addListener(ChannelFutureListener.CLOSE);
	}

	public InetAddress getAddress() {
		return ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress();
	}
}
