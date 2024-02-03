package pen.fx;

import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Static JavaFX helper class.  Provides molder factories and listener help.
 */
@SuppressWarnings("unused")
public class FX {
    private FX() {} // not instantiable

    //-------------------------------------------------------------------------
    // Listener Helpers

    /**
     * Binds the runnable to the property; it will be called when the property
     * value changes.
     * @param property The property
     * @param runnable The runnable
     */
    public static void listenTo(ReadOnlyProperty<?> property, Runnable runnable) {
        property.addListener((p,o,n) -> runnable.run());
    }

    //-------------------------------------------------------------------------
    // Molder Factories

    /**
     * Creates a LabelMolder containing a new Label.
     * @return The molder
     */
    public static LabelMolder label() {
        return new LabelMolder(new Label());
    }

    /**
     * Creates a LabelMolder containing the given Label.
     * @param object The object to mold
     * @return The molder
     */
    public static LabelMolder label(Label object) {
        return new LabelMolder(object);
    }

    /**
     * Creates a ListViewMolder containing the given ListView
     * @param object The object to mold
     * @param <T> The widget's item type
     * @return The molder
     */
    public static <T> ListViewMolder<T> listView(ListView<T> object) {
        return new ListViewMolder<>(object);
    }

    public static MenuBarMolder menuBar() {
        return new MenuBarMolder(new MenuBar());
    }

    public static MenuBarMolder menuBar(MenuBar object) {
        return new MenuBarMolder(object);
    }

    public static MenuMolder menu() {
        return new MenuMolder(new Menu());
    }

    public static MenuMolder menu(Menu object) {
        return new MenuMolder(object);
    }

    public static MenuItemMolder menuItem() {
        return new MenuItemMolder(new MenuItem());
    }

    public static MenuItemMolder menuItem(MenuItem object) {
        return new MenuItemMolder(object);
    }

    /**
     * Creates a NodeMolder containing the given Node
     * @param object The object to mold
     * @return The molder
     */
    public static NodeMolder node(Node object) {
        return new NodeMolder(object);
    }

    /**
     * Creates a PaneMolder containing a new Pane.
     * @return The molder
     */
    public static PaneMolder pane() {
        return new PaneMolder(new Pane());
    }

    /**
     * Creates a PaneMolder containing the given Pane
     * @param object The object to mold
     * @return The molder
     */
    public static PaneMolder pane(Pane object) {
        return new PaneMolder(object);
    }

    /**
     * Creates a RegionMolder containing the given widget.  This is general
     * used to set basic properties for widgets for which no molder has
     * been defined.
     * @param object The object to mold
     * @return The molder
     */
    public static RegionMolder region(Region object) {
        return new RegionMolder(object);
    }

    /**
     * Creates a SplitPaneMolder containing a new SplitPane.
     * @return The molder
     */
    public static SplitPaneMolder splitPane() {
        return new SplitPaneMolder(new SplitPane());
    }

    /**
     * Creates a SplitPaneMolder containing the given SplitPane
     * @param object The object to mold
     * @return The molder
     */
    public static SplitPaneMolder splitPane(SplitPane object) {
        return new SplitPaneMolder(object);
    }

    /**
     * Creates a ToolBarMolder containing a new ToolBar.
     * @return The molder
     */
    public static ToolBarMolder toolBar() {
        return new ToolBarMolder(new ToolBar());
    }

    /**
     * Creates a ToolBarMolder containing the given ToolBar
     * @param object The object to mold
     * @return The molder
     */
    public static ToolBarMolder toolBar(ToolBar object) {
        return new ToolBarMolder(object);
    }

    /**
     * Creates a VBoxMolder containing a new VBox.
     * @return The molder
     */
    public static VBoxMolder vbox() {
        return new VBoxMolder(new VBox());
    }

    /**
     * Creates a VBoxMolder containing the given VBox
     * @param object The object to mold
     * @return The molder
     */
    public static VBoxMolder vbox(VBox object) {
        return new VBoxMolder(object);
    }
}
