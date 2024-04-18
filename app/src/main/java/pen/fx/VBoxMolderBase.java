package pen.fx;


import javafx.scene.layout.VBox;

@SuppressWarnings({"unchecked", "unused"})
public interface VBoxMolderBase<V extends VBox, Self>
    extends PaneMolderBase<V, Self>
{
    default Self spacing(double value) {
        object().setSpacing(value);
        return (Self)object();
    }
}
