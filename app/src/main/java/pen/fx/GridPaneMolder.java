package pen.fx;

import javafx.scene.layout.GridPane;

@SuppressWarnings("unused")
public record GridPaneMolder(GridPane object)
    implements GridPaneMolderBase<GridPane, GridPaneMolder>
{
    // See GridPaneMolderBase for setters
}
