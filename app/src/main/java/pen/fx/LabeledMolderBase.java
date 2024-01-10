package pen.fx;


import javafx.scene.control.Labeled;
import javafx.scene.text.Font;

@SuppressWarnings({"unchecked", "unused"})
public interface LabeledMolderBase<C extends Labeled, Self>
    extends ControlMolderBase<C, Self>
{
    default Self font(Font font) {
        object().setFont(font);
        return (Self)this;
    }

    default Self text(String text) {
        object().setText(text);
        return (Self)this;
    }
}
