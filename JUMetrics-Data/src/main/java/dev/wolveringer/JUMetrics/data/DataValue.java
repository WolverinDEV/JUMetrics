package dev.wolveringer.JUMetrics.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DataValue<T> {
	private final DataKey key;
	private final T value;
	private final long timestamp;
}
