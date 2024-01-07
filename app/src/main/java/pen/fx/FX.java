package pen.fx;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Static JavaFX helper class.  Provides molder factories and listener help.
 */
public class FX {
    //-------------------------------------------------------------------------
    // Listener Helpers

    public static void listenTo(ReadOnlyProperty<?> property, Runnable runnable) {
        property.addListener((p,o,n) -> runnable.run());
    }

    //-------------------------------------------------------------------------
    // Molder Factories

    public static LabelMolder label() {
        return new LabelMolder(new Label());
    }

    public static LabelMolder label(Label node) {
        return new LabelMolder(node);
    }

    public static <T> ListViewMolder<T> listView(ListView<T> node) {
        return new ListViewMolder<>(node);
    }

    public static NodeMolder node(Node node) {
        return new NodeMolder(node);
    }

    public static PaneMolder pane() {
        return new PaneMolder(new Pane());
    }

    public static PaneMolder pane(Pane node) {
        return new PaneMolder(node);
    }

    public static RegionMolder region(Region node) {
        return new RegionMolder(node);
    }

    public static SplitPaneMolder splitPane() {
        return new SplitPaneMolder(new SplitPane());
    }

    public static SplitPaneMolder splitPane(SplitPane node) {
        return new SplitPaneMolder(node);
    }

    public static ToolBarMolder toolBar() {
        return new ToolBarMolder(new ToolBar());
    }

    public static ToolBarMolder toolBar(ToolBar node) {
        return new ToolBarMolder(node);
    }

    public static VBoxMolder vbox() {
        return new VBoxMolder(new VBox());
    }

    public static VBoxMolder vbox(VBox node) {
        return new VBoxMolder(node);
    }
}
