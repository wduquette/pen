package pen.fx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;

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
