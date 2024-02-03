package pen.fx;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

@SuppressWarnings({"unchecked", "unused"})
public interface MenuMolderBase<M extends Menu, Self>
    extends MolderBase<M, Self>
{
    default Self text(String text) {
        object().setText(text);
        return (Self)this;
    }

    default Self item(Molder<? extends MenuItem> molder) {
        object().getItems().add(molder.object());
        return (Self)this;
    }

    default Self bareItem(MenuItem item) {
        object().getItems().add(item);
        return (Self)this;
    }
}
