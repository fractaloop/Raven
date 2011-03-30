package raven.utils;

import java.io.Reader;
import java.io.StreamTokenizer;

public class StreamUtils {
	public static Object getValueFromStream(Reader reader) {
		StreamTokenizer tokenizer = new StreamTokenizer(reader);
		tokenizer.parseNumbers();
		if (tokenizer.ttype == StreamTokenizer.TT_NUMBER)
			return tokenizer.nval;
		else
			return (tokenizer.sval);
	}
}
