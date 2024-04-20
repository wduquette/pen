package pen.fx;


import javafx.geometry.Pos;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

@SuppressWarnings({"unchecked", "unused"})
public interface LabeledMolderBase<C extends Labeled, Self>
    extends ControlMolderBase<C, Self>
{
    default Self alignment(Pos value) {
        object().setAlignment(value);
        return (Self)this;
    }

    /**
     * Sets the widget's minWidth to its preferred size.  This
     * prevents labels and buttons from getting squished
     * horizontally so that their text is elided.
     * @return the molder
     */
    default Self fixed() {
        object().setMinWidth(USE_PREF_SIZE);
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
