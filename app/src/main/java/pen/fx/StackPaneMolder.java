package pen.fx;

import javafx.scene.layout.StackPane;

@SuppressWarnings("unused")
public record StackPaneMolder(StackPane object)
    implements StackPaneMolderBase<StackPane, StackPaneMolder>
{
    // See StackPaneMolderBase for setters
}
