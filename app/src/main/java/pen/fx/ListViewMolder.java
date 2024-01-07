package pen.fx;

import javafx.scene.control.ListView;

@SuppressWarnings("unused")
public record ListViewMolder<T>(ListView<T> object)
    implements ListViewMolderBase<T,ListView<T>, ListViewMolder<T>>
{
}
