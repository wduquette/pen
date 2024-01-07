package pen.fx;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

@SuppressWarnings("unused")
public record LabelMolder(Label object)
    implements LabeledMolderBase<Label, LabelMolder>
{
    // See LabeledMolderBase for setters
}
