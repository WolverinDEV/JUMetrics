package dev.wolveringer.JUMetrics.server.network.packet;

import dev.wolveringer.JUMetrics.network.packet.PacketHandler;
import dev.wolveringer.JUMetrics.network.packets.PacketAuthResponse;
import dev.wolveringer.JUMetrics.network.packets.PacketPing;
import io.netty.channel.ChannelDuplexHandler;

public abstract class AbstractServerPacketHandler extends ChannelDuplexHandler implements PacketHandler {
	public void handle(PacketPing packet) throws Exception{}
	public void handle(PacketAuthResponse packet) throws Exception{}
}
