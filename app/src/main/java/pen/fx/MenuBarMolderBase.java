package pen.fx;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

@SuppressWarnings({"unchecked", "unused"})
public interface MenuBarMolderBase<MB extends MenuBar, Self>
    extends MolderBase<MB, Self>
{
    default Self menu(Molder<? extends Menu> molder) {
        object().getMenus().add(molder.object());
        return (Self)this;
    }

    default Self bareMenu(Menu menu) {
        object().getMenus().add(menu);
        return (Self)this;
    }
}
