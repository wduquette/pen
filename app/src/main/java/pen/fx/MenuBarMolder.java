package pen.fx;

import javafx.scene.control.MenuBar;

@SuppressWarnings("unused")
public record MenuBarMolder(MenuBar object)
    implements MenuBarMolderBase<MenuBar, MenuBarMolder>
{
    // See NodeMolderBase for setters
}
