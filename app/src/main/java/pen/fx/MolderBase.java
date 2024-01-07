package pen.fx;

import java.util.function.Consumer;

@SuppressWarnings({"unused", "unchecked"})
public interface MolderBase<T, Self> extends Molder<T> {
    default Self mold(Consumer<T> consumer) {
        consumer.accept(object());
        return (Self)this;
    }
}
