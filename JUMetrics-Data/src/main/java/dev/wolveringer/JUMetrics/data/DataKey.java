package dev.wolveringer.JUMetrics.data;

import org.apache.commons.lang3.Validate;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataKey {
	@Getter
	@NonNull private final String key;
	@Getter
	private final String description;
	@Getter
	@NonNull private DataType type = DataType.STRING;
	@Getter
	private DataResolver<?> resolver;

	public <T> DataKey(String key, String description, DataType type, DataResolver<T> resolver) {
		this(key, description, type, resolver, null);
	}
	
	public <T> DataKey(String key, String description, DataType type, DataResolver<T> resolver, DataValue<T> defaultData) {
		Validate.isTrue(DataFactory.getKey(key) == null, "Key `"+key+"` alredy registered!");
		DataFactory.keys.put(key, this);
		
		this.key = key;
		this.description = description;
		this.type = type;
		this.resolver = resolver;
		this.resolvedData = defaultData;
	}
	
	private DataValue<?> resolvedData = null;
	
	public DataValue<?> resolveData() throws Exception{
		return resolveData(this.resolver);
	}
	
	public DataValue<?> resolveData(DataResolver<?> resolver) throws Exception{
		return resolveData(resolver, -1);
	}
	
	public DataValue<?> resolveData(DataResolver<?> resolver,int maxAge) throws Exception{
		return resolveData(resolver, maxAge, false);
	}
	
	public DataValue<?> resolveData(DataResolver<?> resolver,int maxAge, boolean force) throws Exception{
		if(force || resolvedData == null || (maxAge > 0 ? System.currentTimeMillis()-resolvedData.getTimestamp() > maxAge : false)){
			Validate.isTrue(resolver != null, "Cant resolve data without a resolver!");
			resolvedData = new DataValue(this, resolver.resolveData(), System.currentTimeMillis());
		}
		return resolvedData;
	}
	
	public DataValue<?> resolveDataSave(DataResolver<?> resolver,int maxAge, boolean force) throws Exception{
		try {
			return resolveData(resolver, maxAge, force);
		}catch(Exception e){
			System.err.println("An error occurent while resolving data "+key);
			e.printStackTrace();
		}
		return null;
	}
	
	public DataValue<?> getData(){
		return resolvedData;
	}
}
