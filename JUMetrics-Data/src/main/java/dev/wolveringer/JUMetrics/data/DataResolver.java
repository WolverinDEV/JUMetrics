package dev.wolveringer.JUMetrics.data;

public interface DataResolver<T> {
	public T resolveData() throws Exception;

	public static DataResolver<?> NO_RESOLVER = new NoResolver();
	
	public static class NoResolver<T> implements DataResolver<T>{
		public T resolveData() throws Exception {
			throw new UnsupportedOperationException("Cant resolve data!");
		}
	}
}
