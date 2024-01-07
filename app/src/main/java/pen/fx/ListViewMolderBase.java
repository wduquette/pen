package pen.fx;


import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.util.List;

@SuppressWarnings({"unchecked", "unused"})
public interface ListViewMolderBase<T, SP extends ListView<T>, Self>
    extends ControlMolderBase<SP, Self>
{
    default Self item(T item) {
        object().getItems().add(item);
        return (Self)this;
    }

    default Self items(List<T> items) {
        object().getItems().addAll(items);
        return (Self)this;
    }

    default Self setItems(ObservableList<T> items) {
        object().setItems(items);
        return (Self)this;
    }

}
