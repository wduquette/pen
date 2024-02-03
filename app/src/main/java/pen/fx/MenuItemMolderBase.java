package pen.fx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;

@SuppressWarnings({"unchecked", "unused"})
public interface MenuItemMolderBase<MI extends MenuItem, Self>
    extends MolderBase<MI, Self>
{
    default Self text(String value) {
        object().setText(value);
        return (Self)this;
    }

    default Self accelerator(String value) {
        object().setAccelerator(
            KeyCombination.keyCombination(value));
        return (Self)this;
    }

    default Self accelerator(KeyCombination value) {
        object().setAccelerator(value);
        return (Self)this;
    }

    default Self action(Runnable value) {
        object().setOnAction(evt -> value.run());
        return (Self)this;
    }

    default Self onAction(EventHandler<ActionEvent> value) {
        object().setOnAction(value);
        return (Self)this;
    }

}
