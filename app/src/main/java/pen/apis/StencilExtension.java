package pen.apis;

import javafx.geometry.Pos;
import pen.stencil.Stencil;
import pen.tcl.ArgQ;
import pen.tcl.TclEngine;

import static pen.stencil.Stencil.label;
import static pen.stencil.Stencil.rect;

public class StencilExtension {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final TclEngine engine;
    private final Stencil stencil;

    //-------------------------------------------------------------------------
    // Constructor

    public StencilExtension(TclEngine engine, Stencil stencil) {
        this.engine = engine;
        this.stencil = stencil;
        var ensemble = engine.ensemble("stencil");

        ensemble.add("test", this::cmd_stencilTest);
    }

    //-------------------------------------------------------------------------
    // Ensemble: stencil *

    private void cmd_stencilTest(TclEngine engine, ArgQ argq) {
        stencil.draw(rect().at(10,10).size(100,60));
        stencil.draw(label().at(55,40).pos(Pos.CENTER).text("Stencil Test"));
    }

}
