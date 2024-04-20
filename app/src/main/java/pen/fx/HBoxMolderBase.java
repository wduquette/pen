package pen.fx;


import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

@SuppressWarnings({"unchecked", "unused"})
public interface HBoxMolderBase<H extends HBox, Self>
    extends PaneMolderBase<H, Self>
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
