package dev.wolveringer.JUMetrics.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import dev.wolveringer.JUMetrics.data.resolver.LocalIpResolver;
import dev.wolveringer.JUMetrics.data.resolver.PropertyVariableResolver;

public class DataFactory {
	protected static final HashMap<String, DataKey> keys = new HashMap<String, DataKey>();
	
	static {
		registerKey("local.ip", "local ip", DataType.STRING, new LocalIpResolver());
		
		registerKey("user.language", "operating system type", DataType.STRING, new PropertyVariableResolver("user.language"));
		
		registerKey("os.type", "operating system type", DataType.STRING, new PropertyVariableResolver("os.name"));
		registerKey("os.version", "operating system version", DataType.STRING, new PropertyVariableResolver("os.version"));
		registerKey("os.arch", "operating system arch", DataType.STRING, new PropertyVariableResolver("os.arch"));
		registerKey("os.model", "operating system arch", DataType.STRING, new PropertyVariableResolver("sun.arch.data.model"));
		
		registerKey("java.version", "java version", DataType.STRING, new PropertyVariableResolver("java.runtime.version"));
		
		registerKey("jvm.mode", "java virtual machine mode type", DataType.STRING, new PropertyVariableResolver("java.vm.info"));
		registerKey("jvm.version", "java virtual machine version", DataType.STRING, new PropertyVariableResolver("java.vm.version"));
		registerKey("jvm.interminal", "is the jvm started in terminal", DataType.BOOLEAN, new DataResolver<Boolean>() {
			public Boolean resolveData() throws Exception {
				return System.console() != null;
			}
		});
		
		registerKey("system.cores", "count of the system cores", DataType.INT, new DataResolver<Integer>() {
			public Integer resolveData() throws Exception {
				return Runtime.getRuntime().availableProcessors();
			}
		});
		registerKey("system.memory.max", "memory statistics", DataType.LONG, new DataResolver<Long>() {
			public Long resolveData() throws Exception {
				return Runtime.getRuntime().maxMemory();
			}
		});
		registerKey("system.memory.allocated", "memory statistics", DataType.LONG, new DataResolver<Long>() {
			public Long resolveData() throws Exception {
				return Runtime.getRuntime().totalMemory();
			}
		});
		registerKey("system.memory.used", "memory statistics", DataType.LONG, new DataResolver<Long>() {
			public Long resolveData() throws Exception {
				return Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			}
		});
		registerKey("system.memory.unused", "memory statistics", DataType.LONG, new DataResolver<Long>() {
			public Long resolveData() throws Exception {
				return Runtime.getRuntime().freeMemory();
			}
		});
	}
	
	public static List<String> getKeys(){
		return new ArrayList<String>(keys.keySet());
	}
	
	public static DataKey registerKey(String key, DataType type, DataResolver resolver){
		DataKey dkey = getKey(key);
		if(dkey != null)
			return dkey;
		return new DataKey(key, "no discription", type, resolver); //Register automatic in constructor!
	}
	
	public static DataKey registerKey(String key, String description, DataType type, DataResolver resolver){
		DataKey dkey = getKey(key);
		if(dkey != null)
			return dkey;
		return new DataKey(key, description, type, resolver); //Register automatic in constructor!
	}
	
	public static DataKey getKey(String name){
		for(Entry<String, DataKey> key : keys.entrySet())
			if(key.getKey().equalsIgnoreCase(name))
				return key.getValue();
		return null;
	}
}
