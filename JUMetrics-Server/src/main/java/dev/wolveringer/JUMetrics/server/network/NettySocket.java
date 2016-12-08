package dev.wolveringer.JUMetrics.server.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dev.wolveringer.JUMetrics.server.network.packet.FrameDecoder;
import dev.wolveringer.JUMetrics.server.network.packet.FrameEncoder;
import dev.wolveringer.JUMetrics.server.network.packet.PacketDecoder;
import dev.wolveringer.JUMetrics.server.network.packet.PacketEncoder;
import dev.wolveringer.JUMetrics.server.network.packet.PacketHandlerBoss;
import io.netty.bootstrap.ChannelFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NettySocket {
	private static final List<RemoteApplicationClient> clients = new ArrayList<RemoteApplicationClient>();
	
	public static List<RemoteApplicationClient> getClients(){
		synchronized (clients) {
			return Collections.unmodifiableList(clients);
		}
	}
	
	private static final String FRAME_DECODER = "frame-decoder";
	
	private static final String FRAME_ENCODER = "frame-encoder";
	private static final FrameEncoder FRAME_ENCODER_INSTANCE = new FrameEncoder();
	
	private static final String PACKET_DECODER = "packet-decoder";
	
	private static final String PACKET_ENCODER = "packet-encoder";
	private static final PacketEncoder PACKET_ENCODER_INSTANCE = new PacketEncoder();
	
	private static final String PACKET_HANDLER_BOSS = "packet-handler";
	
	private static final String TIMEOUT_HANDLER = "handler-timeout";
	
	private final String host;
	private final int port;
	@Getter
	private boolean running;
	
	private Channel channel;
	
	public boolean start() {
		ServerBootstrap boot = new ServerBootstrap();

		boot.group(new NioEventLoopGroup(12));
		boot.channel(NioServerSocketChannel.class);
		
		boot.childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				try {
					ch.config().setOption(ChannelOption.IP_TOS, 0x18);
				} catch (ChannelException ex) {
					// IP_TOS is not supported (Windows XP / Windows Server 2003)
				}
				ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
				
				ch.pipeline().addLast(FRAME_DECODER, new FrameDecoder());
				ch.pipeline().addAfter(FRAME_DECODER, PACKET_DECODER, new PacketDecoder());
				
				ch.pipeline().addLast(FRAME_ENCODER, FRAME_ENCODER_INSTANCE);
				ch.pipeline().addAfter(FRAME_ENCODER, PACKET_ENCODER, PACKET_ENCODER_INSTANCE);
				
				ch.pipeline().addLast(TIMEOUT_HANDLER, new ClientPingTimeoutHandler(20, TimeUnit.SECONDS));
				
				RemoteApplicationClient client = new RemoteApplicationClient(clients);
				PacketHandlerBoss boss = new PacketHandlerBoss();
				boss.setHandler(client);
				
				ch.pipeline().addLast(PACKET_HANDLER_BOSS, boss);
				ch.pipeline().addLast("client", client);
			}
		});
		
		boot.localAddress(host, port);
		return running = boot.bind().addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()){
					channel = future.channel();
					System.out.println("JUMetrics server listen now on "+host+":"+port);
					running = true;
				}
				else{
					System.err.println("Could not bind JUMetrics server!");
					future.cause().printStackTrace(System.err);
					running = false;
				}
			}
		}).syncUninterruptibly().isSuccess() && running;
	}
	
	public void stop(){
		for(RemoteApplicationClient client : new ArrayList<RemoteApplicationClient>(clients))
			try {
				client.disconnect("JUMetrics server is shuting down.");
			}catch (Exception e) {}
		channel.close().syncUninterruptibly();
	}
}
