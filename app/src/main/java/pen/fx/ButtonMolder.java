package pen.fx;

import javafx.scene.control.Button;

@SuppressWarnings("unused")
public record ButtonMolder(Button object)
    implements ButtonBaseMolderBase<Button, ButtonMolder>
{
    // See ButtonBaseMolderBase for setters
}
