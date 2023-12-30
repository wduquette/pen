package pen.tools.draw;

import pen.AppError;
import pen.Tool;

public class DrawTool implements Tool {
    @Override
    public void start(String[] args) throws AppError {
        DrawApp.main(args);
    }

    @Override
    public String usage() {
        return "draw drawing.tcl";
    }

    @Override
    public String oneLiner() {
        return "Saves a drawing to disk as a PNG file.";
    }

    @Override
    public String help() {
        return """
            Saves the file 'myfile.tcl' to disk as 'myfile.png'.""";
    }
}
