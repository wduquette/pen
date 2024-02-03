package pen.fx;

import javafx.scene.control.Menu;

@SuppressWarnings("unused")
public record MenuMolder(Menu object)
    implements MenuMolderBase<Menu, MenuMolder>
{
    // See NodeMolderBase for setters
}
