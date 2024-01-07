package pen.fx;

import javafx.scene.control.SplitPane;

@SuppressWarnings("unused")
public record SplitPaneMolder(SplitPane object)
    implements SplitPaneMolderBase<SplitPane, SplitPaneMolder>
{
}
