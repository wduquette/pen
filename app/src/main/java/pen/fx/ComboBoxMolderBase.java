package pen.fx;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.List;

@SuppressWarnings({"unchecked", "unused"})
public interface ComboBoxMolderBase<T, C extends ComboBox<T>, Self>
    extends ControlMolderBase<C, Self>
{

    default Self editable(boolean value) {
        object().setEditable(value);
        return (Self) this;
    }

    default Self item(T item) {
        object().getItems().add(item);
        return (Self) this;
    }

    default Self setItems(List<T> items) {
        object().getItems().clear();
        object().getItems().addAll(items);
        return (Self) this;
    }

    default Self onAction(EventHandler<ActionEvent> handler) {
        object().setOnAction(handler);
        return (Self)this;
    }

    default Self action(Runnable handler) {
        object().setOnAction(dummy -> handler.run());
        return (Self)this;
    }

    default Self value(T value) {
        object().setValue(value);
        return (Self)this;
    }

    default Self placeholderText(String value) {
        object().setPlaceholder(new Label(value));
        return (Self)this;
    }
}
