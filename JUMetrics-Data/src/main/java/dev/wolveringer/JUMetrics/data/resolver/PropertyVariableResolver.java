package dev.wolveringer.JUMetrics.data.resolver;

import dev.wolveringer.JUMetrics.data.DataResolver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PropertyVariableResolver implements DataResolver<String>{
	private final String name;
	
	public String resolveData() throws Exception {
		return System.getProperty(name);
	}
}
