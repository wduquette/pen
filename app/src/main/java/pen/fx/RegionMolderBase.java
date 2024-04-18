package pen.fx;


import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

@SuppressWarnings({"unchecked", "unused"})
public interface RegionMolderBase<R extends Region, Self>
    extends NodeMolderBase<R, Self>
{
    default Self vgrow() {
        VBox.setVgrow(object(), Priority.ALWAYS);
        return (Self)this;
    }

    default Self splitResizableWithParent(boolean flag) {
        SplitPane.setResizableWithParent(object(), flag);
        return (Self)this;
    }

    default Self gridHalignment(HPos value) {
        GridPane.setHalignment(object(), value);
        return (Self)this;
    }

    default Self gridHgrow() {
        GridPane.setHgrow(object(), Priority.ALWAYS);
        return (Self)this;
    }

    default Self gridValignment(VPos value) {
        GridPane.setValignment(object(), value);
        return (Self)this;
    }

    default Self gridVgrow() {
        GridPane.setVgrow(object(), Priority.ALWAYS);
        return (Self)this;
    }
}
