package name.martingeisse.wicket.serializable;

import java.io.Serializable;
import java.util.function.Function;

/**
 *
 */
public interface SerializableFunction<A, B> extends Serializable, Function<A, B> {
}
