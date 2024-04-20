package pen.fx;


import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

@SuppressWarnings({"unchecked", "unused"})
public interface VBoxMolderBase<V extends VBox, Self>
    extends PaneMolderBase<V, Self>
{
    default Self alignment(Pos value) {
        object().setAlignment(value);
        return (Self)this;
    }

    default Self spacing(double value) {
        object().setSpacing(value);
        return (Self)this;
    }
}
