package pen.tools.view;

import pen.AppError;
import pen.Tool;

public class ViewTool implements Tool {
    @Override
    public void start(String[] args) throws AppError {
        ViewApp.main(args);
    }

    @Override
    public String usage() {
        return "view drawing.tcl";
    }

    @Override
    public String oneLiner() {
        return "Draws and displays a single drawing.";
    }

    @Override
    public String help() {
        return """
            Allows the user to view a Pen drawing script.""";
    }
}
