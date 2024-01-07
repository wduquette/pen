package pen.fx;


import javafx.scene.Node;
import javafx.scene.layout.Pane;

@SuppressWarnings({"unchecked", "unused"})
public interface PaneMolderBase<P extends Pane, Self>
    extends RegionMolderBase<P, Self>
{
    default Self add(Molder<? extends Node> molder) {
        object().getChildren().add(molder.object());
        return (Self)this;
    }

    default Self addBare(Node node) {
        object().getChildren().add(node);
        return (Self)this;
    }
}
