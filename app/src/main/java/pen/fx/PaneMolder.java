package pen.fx;

import javafx.scene.layout.Pane;

@SuppressWarnings("unused")
public record PaneMolder(Pane object)
    implements PaneMolderBase<Pane, PaneMolder>
{
}
