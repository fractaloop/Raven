package raven.math.graph;

import java.io.Reader;

public interface GraphEdgeFactory<T> {
	public T createInstance(Reader reader);
}
