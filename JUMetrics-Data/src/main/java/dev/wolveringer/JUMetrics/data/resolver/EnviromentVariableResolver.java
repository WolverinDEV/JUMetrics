package dev.wolveringer.JUMetrics.data.resolver;

import dev.wolveringer.JUMetrics.data.DataResolver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnviromentVariableResolver implements DataResolver<String>{
	private final String name;
	
	public String resolveData() throws Exception {
		return System.getenv(name);
	}
}
