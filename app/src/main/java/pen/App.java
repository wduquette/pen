package pen;

import pen.tcl.ArgQ;
import pen.tcl.TclEngine;
import tcl.lang.Interp;
import tcl.lang.TclException;

public class App {
    //------------------------------------------------------------------------
    // Instance Variables

    private final TclEngine engine = new TclEngine();

    //------------------------------------------------------------------------
    // Constructor

    public App() {
        engine.add("hello", this::cmd_hello);
        var ensemble = engine.ensemble("do");
        ensemble.add("howdy", this::cmd_hello);
    }

    //------------------------------------------------------------------------
    // Command definitions

    private void cmd_hello(TclEngine engine, ArgQ argq) throws TclException {
        engine.checkArgs(argq, 0, 1, "?arg?");
        System.out.println(argq);
    }

    //------------------------------------------------------------------------
    // Main-line code

    public void run(String[] args) {
        try {
            engine.eval("do howdy a b");
        } catch (TclException ex) {
            System.out.println("Caught: " + ex);
        }
    }

    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        var app = new App();
        app.run(args);
    }
}
