package name.martingeisse.wicket.serializable;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 *
 */
public interface SerializableSupplier<T> extends Serializable, Supplier<T> {
}
