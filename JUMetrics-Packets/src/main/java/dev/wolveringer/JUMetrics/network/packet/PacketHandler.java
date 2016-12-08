package dev.wolveringer.JUMetrics.network.packet;

import dev.wolveringer.JUMetrics.network.packets.PacketAuth;
import dev.wolveringer.JUMetrics.network.packets.PacketAuthResponse;
import dev.wolveringer.JUMetrics.network.packets.PacketData;
import dev.wolveringer.JUMetrics.network.packets.PacketDisconnect;
import dev.wolveringer.JUMetrics.network.packets.PacketPing;

public interface PacketHandler {
	public void handle(PacketDisconnect packet) throws Exception;
	public void handle(PacketPing packet) throws Exception;
	public void handle(PacketData packet) throws Exception;
	public void handle(PacketAuth packet) throws Exception;
	public void handle(PacketAuthResponse packet) throws Exception;
}
