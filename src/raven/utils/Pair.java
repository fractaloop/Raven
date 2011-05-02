package raven.utils;

public class Pair<T1, T2> {
	public T1 first;
	public T2 second;
	
	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Pair<?, ?>) {
			Pair<?, ?> other = (Pair<?, ?>)obj;
			if (first.equals(other.first) && second.equals(other.second)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}
	
	public String toString() {
		return "<" + first.toString() + ", " + second.toString() + ">";
	}
}
