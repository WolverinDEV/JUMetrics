package dev.wolveringer.JUMetrics.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import dev.wolveringer.JUMetrics.network.packet.Packet;
import dev.wolveringer.JUMetrics.network.packets.PacketDisconnect;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Metrics {
	private static final int PROTOCOL_VERSION = 1;
	private static final int BUFFER_SIZE = 1024;
	@Getter
	private final String host;
	@Getter
	private final int port;
	@Getter
	@Setter
	private int connectTimeout = 5000;
	@Getter
	@Setter
	private int readTimeout = 5000;
	
	
	private transient Socket socket = null;
	
	private transient InputStream in = null;
	private transient DataInputStream din = null;
	
	private transient OutputStream out = null;
	private transient DataOutputStream dout = null;
	
	private transient boolean sending = false;
	
	
	private transient long lastPing = -1;
	
	public boolean sendMetrics(){
		if(sending || socket != null)
			return false;
		
		try {
			System.out.println("Connection");
			socket = new Socket();
			socket.connect(new InetSocketAddress(host, port), connectTimeout);
			
			din = new DataInputStream(in = socket.getInputStream());
			dout = new DataOutputStream(out = socket.getOutputStream());
			
			System.out.println("Sending");
			sendPacket(new PacketDisconnect(15, "idk its an test!"));
			closeChannel();
			System.out.println("Done");
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void doAuth(){
		
	}
	
	private Packet readPacket() throws Exception {
		ByteBuf buffer = readData();
		Packet packet = Packet.createPacket(buffer.readInt(), buffer);
		buffer.release();
		return packet;
	}
	
	private ByteBuf readData() throws Exception{
		int length = din.readInt();
		ByteBuf out = Unpooled.buffer(length);
		final byte[] buffer = new byte[BUFFER_SIZE];
		int readed = 0;
		while (readed < length) {
			int r = in.read(buffer, 0, Math.min(buffer.length, length-readed));
			readed+=r;
			if(r == 0)
				Thread.sleep(1);
			out.readBytes(buffer, 0, r);
		}
		return out;
	}
	
	private void sendPacket(Packet packet){
		ByteBuf data = Unpooled.buffer();
		packet.write(data, PROTOCOL_VERSION);
		try {
			sendData(Packet.getPacketId(packet.getClass()), data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		data.release();
	}
	
	private synchronized void sendData(int packetId, ByteBuf buffer) throws Exception{
		int length = buffer.readableBytes() + 4;
		byte[] data = new byte[buffer.readableBytes()];
		buffer.readBytes(data, 0, data.length);
		
		dout.writeInt(length);
		dout.writeInt(packetId);
		dout.write(data, 0, data.length);
	}
	
	private void disconnect(String message){
		sendPacket(new PacketDisconnect(0, message));
	}
	
	private void closeChannel(){
		if(in != null) try { in.close(); } catch(Exception e){}
		if(out != null) try { out.flush(); out.close(); } catch(Exception e){}
		if(socket != null) try { socket.close(); } catch(Exception e){}
	}
}
