package pen.fx;


import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

@SuppressWarnings({"unchecked", "unused"})
public interface ControlMolderBase<C extends Control, Self>
    extends RegionMolderBase<C, Self>
{
    default Self tooltip(Tooltip tooltip) {
        object().setTooltip(tooltip);
        return (Self)this;
    }

    default Self tooltipText(String text) {
        object().setTooltip(new Tooltip(text));
        return (Self)this;
    }
}
