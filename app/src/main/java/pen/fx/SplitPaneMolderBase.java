package pen.fx;


import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

@SuppressWarnings({"unchecked", "unused"})
public interface SplitPaneMolderBase<SP extends SplitPane, Self>
    extends RegionMolderBase<SP, Self>
{
    default Self add(Molder<? extends Node> molder) {
        object().getItems().add(molder.object());
        return (Self)this;
    }

    default Self addBare(Node node) {
        object().getItems().add(node);
        return (Self)this;
    }

    default Self orientation(Orientation orientation) {
        object().setOrientation(orientation);
        return (Self)this;
    }

    default Self setDividerPosition(int index, double pos) {
        object().setDividerPosition(index, pos);
        return (Self)this;
    }
}
