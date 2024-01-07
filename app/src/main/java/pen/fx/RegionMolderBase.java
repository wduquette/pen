package pen.fx;


import javafx.scene.control.SplitPane;
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
}
