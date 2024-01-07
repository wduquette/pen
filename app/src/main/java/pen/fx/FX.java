package pen.fx;

import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Class for molder factories.
 */
public class FX {
    public static LabelMolder label() {
        return new LabelMolder(new Label());
    }

    public static LabelMolder label(Label node) {
        return new LabelMolder(node);
    }

    public static PaneMolder pane() {
        return new PaneMolder(new Pane());
    }

    public static PaneMolder pane(Pane node) {
        return new PaneMolder(node);
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
