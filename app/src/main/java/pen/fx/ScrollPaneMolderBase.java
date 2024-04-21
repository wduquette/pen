package pen.fx;


import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

@SuppressWarnings({"unchecked", "unused"})
public interface ScrollPaneMolderBase<SP extends ScrollPane, Self>
    extends RegionMolderBase<SP, Self>
{
    default Self content(Molder<? extends Node> molder) {
        object().setContent(molder.object());
        return (Self)this;
    }
}
