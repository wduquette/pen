package pen.fx;

import javafx.scene.control.MenuItem;

@SuppressWarnings("unused")
public record MenuItemMolder(MenuItem object)
    implements MenuItemMolderBase<MenuItem, MenuItemMolder>
{
    // See NodeMolderBase for setters
}
