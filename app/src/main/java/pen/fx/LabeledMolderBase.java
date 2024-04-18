package pen.fx;


import javafx.geometry.Pos;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;

@SuppressWarnings({"unchecked", "unused"})
public interface LabeledMolderBase<C extends Labeled, Self>
    extends ControlMolderBase<C, Self>
{
    default Self alignment(Pos value) {
        object().setAlignment(value);
        return (Self)this;
    }

    default Self font(Font font) {
        object().setFont(font);
        return (Self)this;
    }

    default Self text(String text) {
        object().setText(text);
        return (Self)this;
    }
}
