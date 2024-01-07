package pen.fx;

import javafx.scene.Node;

@SuppressWarnings({"unchecked", "unused"})
public interface NodeMolderBase<N extends Node, Self>
    extends MolderBase<N, Self>
{
    default Self id(String id) {
        object().setId(id);
        return (Self)this;
    }
}
