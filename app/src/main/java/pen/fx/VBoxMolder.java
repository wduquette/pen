package pen.fx;

import javafx.scene.layout.VBox;

@SuppressWarnings("unused")
public record VBoxMolder(VBox object)
    implements VBoxMolderBase<VBox, VBoxMolder>
{
    // See VBoxMolderBase for setters
}
