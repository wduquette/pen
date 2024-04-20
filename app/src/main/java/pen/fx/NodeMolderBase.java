package pen.fx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

@SuppressWarnings({"unchecked", "unused"})
public interface NodeMolderBase<N extends Node, Self>
    extends MolderBase<N, Self>
{
    default Self id(String id) {
        object().setId(id);
        return (Self)this;
    }

    default Self visible(boolean value) {
        object().setVisible(value);
        return (Self)this;
    }

    default Self onMouseMoved(EventHandler<MouseEvent> handler) {
        object().setOnMouseMoved(handler);
        return (Self)this;
    }

    /**
     * Adds a style class to the node.
     * @param value The new style class
     * @return The molder
     */
    default Self styleClass(String value) {
        object().getStyleClass().add(value);
        return (Self)this;
    }

    /**
     * Adds one or more styles to the view.
     * @param styles The styles
     * @return The molder
     */
    default Self styles(String... styles) {
        object().setStyle(String.join("\n", styles));
        return (Self)this;
    }
}
