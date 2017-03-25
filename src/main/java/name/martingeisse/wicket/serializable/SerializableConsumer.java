package name.martingeisse.wicket.serializable;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 *
 */
public interface SerializableConsumer<T> extends Serializable, Consumer<T> {
}
