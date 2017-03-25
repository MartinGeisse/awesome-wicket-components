package name.martingeisse.wicket.serializable;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 *
 */
public interface SerializableBiFunction<A, B, C> extends Serializable, BiFunction<A, B, C> {
}
