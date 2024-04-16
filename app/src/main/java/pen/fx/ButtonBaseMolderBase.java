package pen.fx;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;

@SuppressWarnings({"unchecked", "unused"})
public interface ButtonBaseMolderBase<C extends ButtonBase, Self>
    extends LabeledMolderBase<C, Self>
{
    default Self onAction(EventHandler<ActionEvent> value) {
        object().setOnAction(value);
        return (Self)this;
    }

    default Self action(Runnable value) {
        object().setOnAction(evt -> value.run());
        return (Self)this;
    }
}
