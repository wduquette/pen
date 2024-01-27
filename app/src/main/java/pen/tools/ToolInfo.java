package pen.tools;

import java.util.function.Consumer;

public record ToolInfo(
    String name,
    String argsig,
    String oneLiner,
    String help,
    Consumer<String[]> launcher
) {
    public String usage() {
        return name + " " + argsig;
    }
}
