package pen.fx;

import javafx.scene.Node;
import javafx.scene.control.ToolBar;

@SuppressWarnings({"unchecked", "unused"})
public interface ToolBarMolderBase<TB extends ToolBar, Self>
    extends ControlMolderBase<TB, Self>
{
    default Self add(Molder<? extends Node> molder) {
        object().getItems().add(molder.object());
        return (Self)this;
    }

    default Self addBare(Node node) {
        object().getItems().add(node);
        return (Self)this;
    }
}
