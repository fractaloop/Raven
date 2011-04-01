package raven.game.navigation;

import java.io.Reader;

public interface GraphEdgeFactory<T> {
	public T createInstance(Reader reader);
}
