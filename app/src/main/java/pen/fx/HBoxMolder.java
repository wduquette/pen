package pen.fx;

import javafx.scene.layout.HBox;

@SuppressWarnings("unused")
public record HBoxMolder(HBox object)
    implements HBoxMolderBase<HBox, HBoxMolder>
{
    // See HBoxMolderBase for setters
}
