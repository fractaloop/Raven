package raven.math.graph;

import java.io.Reader;

public interface GraphNodeFactory<T> {
	public T createInstance(Reader reader);
}
