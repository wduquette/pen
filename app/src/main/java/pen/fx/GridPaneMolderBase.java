package pen.fx;


import javafx.scene.Node;
import javafx.scene.layout.GridPane;

@SuppressWarnings({"unchecked", "unused"})
public interface GridPaneMolderBase<G extends GridPane, Self>
    extends PaneMolderBase<G, Self>
{
    default Self at(int col, int row, Molder<? extends Node> molder) {
        object().getChildren().add(molder.object());
        GridPane.setConstraints(molder.object(), col, row);
        return (Self)this;
    }

    default Self bareAt(int col, int row, Node node) {
        object().getChildren().add(node);
        GridPane.setConstraints(node, col, row);
        return (Self)this;
    }

    default Self hgap(double value) {
        object().setHgap(value);
        return (Self)this;
    }

    default Self vgap(double value) {
        object().setVgap(value);
        return (Self)this;
    }

}
