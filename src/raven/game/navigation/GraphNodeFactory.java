package raven.game.navigation;

import java.io.Reader;

public interface GraphNodeFactory<T> {
	public T createInstance(Reader reader);
}
