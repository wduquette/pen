package pen.fx;

import javafx.scene.control.ComboBox;

@SuppressWarnings("unused")
public record ComboBoxMolder<T>(ComboBox<T> object)
    implements ComboBoxMolderBase<T, ComboBox<T>, ComboBoxMolder<T>>
{
    // See ComboBoxMolderBase<T> for methods.
}
