package pen.fx;

import javafx.scene.Node;

@SuppressWarnings("unused")
public record NodeMolder(Node object)
    implements NodeMolderBase<Node, NodeMolder>
{
    // See NodeMolderBase for setters
}
