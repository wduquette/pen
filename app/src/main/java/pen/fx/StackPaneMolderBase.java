package pen.fx;


import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

@SuppressWarnings({"unchecked", "unused"})
public interface StackPaneMolderBase<P extends StackPane, Self>
    extends PaneMolderBase<P, Self>
{
    default Self alignment(Pos value) {
        object().setAlignment(value);
        return (Self)this;
    }
}
