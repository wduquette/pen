package pen;

import java.util.function.Consumer;

public record ToolInfo(
    String name,
    String usage,
    String oneLiner,
    String help,
    Consumer<String[]> start
) {
}
