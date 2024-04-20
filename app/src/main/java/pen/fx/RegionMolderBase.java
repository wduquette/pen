package pen.fx;


import javafx.geometry.HPos;
import javafx.geometry.Insets;
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
    default Self padding(double padding) {
        object().setPadding(new Insets(padding));
        return (Self)this;
    }

    default Self padding(double top, double right, double bottom, double left) {
        object().setPadding(new Insets(top, right, bottom, left));
        return (Self)this;
    }

    /**
     * Adds a stylesheet to the object.  The stylesheet is found relative to
     * the class.
     * @param cls The class
     * @param name The stylesheet's file name.
     * @return The molder
     */
    default Self stylesheet(Class<?> cls, String name) {
        object().getStylesheets().add(cls.getResource(name).toExternalForm());
        return (Self)this;
    }

    //-------------------------------------------------------------------------
    // Pane-specific constraints

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
