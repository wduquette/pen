package pen.tools.demo;

import pen.AppError;
import pen.Tool;

public class DemoTool implements Tool {
    @Override
    public void start(String[] args) throws AppError {
        if (args.length > 2) {
            throw new AppError("Unexpected arguments on command line.");
        }
        DemoApp.main(args);
    }

    @Override
    public String usage() {
        return "pen demo";
    }

    @Override
    public String oneLiner() {
        return "Illustrates the use of the Java drawing API";
    }

    @Override
    public String help() {
        return """
            Allows the user to view various drawings made using the
            application's internal Java drawing API.""";
    }
}
