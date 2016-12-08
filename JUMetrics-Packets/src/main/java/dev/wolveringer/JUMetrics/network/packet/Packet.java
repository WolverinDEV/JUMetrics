package dev.wolveringer.JUMetrics.network.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;

import dev.wolveringer.JUMetrics.network.packets.PacketAuth;
import dev.wolveringer.JUMetrics.network.packets.PacketAuthResponse;
import dev.wolveringer.JUMetrics.network.packets.PacketData;
import dev.wolveringer.JUMetrics.network.packets.PacketDisconnect;
import dev.wolveringer.JUMetrics.network.packets.PacketPing;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;

public abstract class Packet {
	public static int MAX_LENGTH = Short.MAX_VALUE * 2;
	
	private static HashMap<Integer, Constructor<? extends Packet>> packets = new HashMap<Integer, Constructor<? extends Packet>>();
	
	static {
		registerPacket(0x000, PacketPing.class);
		registerPacket(0x001, PacketDisconnect.class);
		
		registerPacket(0x100, PacketAuth.class);
		registerPacket(0x101, PacketAuthResponse.class);
		
		registerPacket(0x200, PacketData.class);
	}
	
	public static int getPacketId(@NonNull Class packet){
		for(Entry<Integer, Constructor<? extends Packet>> cons : packets.entrySet())
			if(cons.getValue().getDeclaringClass().equals(packet))
				return cons.getKey();
		return -1;
	}
	
	public static <T> T createPacket(int id) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Constructor<? extends Packet> cons = packets.get(id);
		Validate.isTrue(cons != null, "Cant find packet constructor for "+id);
		return (T) cons.newInstance();
	}
	
	public static <T> T createPacket(int id, ByteBuf data) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Packet packet = createPacket(id);
		packet.read(data, 0);
		return (T) packet;
	}
	
	public static void registerPacket(int id,@NonNull Class<? extends Packet> clazz){
		for(Constructor c : clazz.getConstructors())
			if(c.getParameterTypes().length == 0){
				packets.put(id, c);
				return;
			}
		System.err.println("Cant find defualt constructor for: "+clazz.getName());
		return;
	}
	
	@Getter
	public static class PacketException extends Exception{
		private final Packet packet;

		public PacketException(Packet packet) {
			super();
			this.packet = packet;
		}

		public PacketException(Packet packet,String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
			this.packet = packet;
		}

		public PacketException(Packet packet,String message, Throwable cause) {
			super(message, cause);
			this.packet = packet;
		}

		public PacketException(Packet packet,String message) {
			super(message);
			this.packet = packet;
		}

		public PacketException(Packet packet,Throwable cause) {
			super(cause);
			this.packet = packet;
		}
		
		
	}
	
	public abstract void read(ByteBuf buffer, int version);
	public abstract void write(ByteBuf buffer, int version);
	public abstract void handle(PacketHandler handler) throws Exception;
	
	protected void writeString(ByteBuf buffer, String message){
		int length = message == null ? -1 : message.toCharArray().length;
		Validate.isTrue(length < Short.MAX_VALUE * 2, "Invalid string length!");
		
		buffer.writeInt(length);
		for(char c : message.toCharArray())
			buffer.writeChar(c);
	}
	
	protected String readString(ByteBuf buffer){
		int length = buffer.readInt();
		if(length < 0)
			return null;
		Validate.isTrue(length < Short.MAX_VALUE * 2, "Invalid string length!");
		char[] chars = new char[length];
		for(int i = 0;i<length;i++)
			chars[i] = buffer.readChar();
		
		return new String(chars, 0, length);
	}
}
