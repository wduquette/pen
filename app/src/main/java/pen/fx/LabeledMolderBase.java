package pen.fx;


import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tooltip;

@SuppressWarnings({"unchecked", "unused"})
public interface LabeledMolderBase<C extends Labeled, Self>
    extends ControlMolderBase<C, Self>
{
    default Self text(String text) {
        object().setText(text);
        return (Self)this;
    }
}
