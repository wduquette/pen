package pen.apis;

import javafx.geometry.Pos;
import pen.stencil.Stencil;
import pen.stencil.StyleMap;
import pen.tcl.ArgQ;
import pen.tcl.TclEngine;
import tcl.lang.TclException;

import static pen.stencil.Stencil.label;
import static pen.stencil.Stencil.rect;

public class StencilExtension {
    public static final String NORMAL = "normal";

    //-------------------------------------------------------------------------
    // Instance Variables

    private final TclEngine tcl;
    private final Stencil stencil;
    private final StyleMap styleMap = new StyleMap();

    //-------------------------------------------------------------------------
    // Constructor

    public StencilExtension(TclEngine engine, Stencil stencil) {
        this.tcl = engine;
        this.stencil = stencil;
        styleMap.make(NORMAL);

        // stencil *
        var sten = engine.ensemble("stencil");

        sten.add("test", this::cmd_stencilTest);

        // stencil style *
        var style = sten.ensemble("style");
        style.add("cget", this::cmd_stencilStyleCget);
        style.add("names", this::cmd_stencilStyleNames);

    }

    //-------------------------------------------------------------------------
    // Ensemble: stencil *

    private void cmd_stencilTest(TclEngine tcl, ArgQ argq) {
        stencil.draw(rect().at(10,10).size(100,60));
        stencil.draw(label().at(60,40).pos(Pos.CENTER).text("Stencil Test"));
    }

    private void cmd_stencilStyleCget(TclEngine tcl, ArgQ argq)
        throws TclException
    {
        tcl.checkArgs(argq, 1, 2, "name ?option?");
        var name = argq.next().toString();

        if (!styleMap.hasStyle(name)) {
            throw tcl.expected("style name", name);
        }
        var style = styleMap.get(name);

        if (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-background":
                    tcl.setResult(style.getBackground().toString());
                    break;
                case "-font":
                    tcl.setResult("TODO");
                    break;
                case "-foreground":
                    tcl.setResult(style.getForeground().toString());
                    break;
                case "-linewidth":
                    tcl.setResult(style.getLineWidth());
                    break;
                case "-textcolor":
                    tcl.setResult(style.getTextColor().toString());
                    break;
                default:
                    throw tcl.expected("style option", opt);
            }
        } else {
            tcl.setResult(tcl.list()
                .item("-background")
                .item(style.getBackground().toString())
                .item("-font")
                .item("TODO")
                .item("-foreground")
                .item(style.getForeground().toString())
                .item("-linewidth")
                .item(Double.toString(style.getLineWidth()))
                .item("-textcolor")
                .item(style.getTextColor().toString())
                .get());
        }
    }

    private void cmd_stencilStyleNames(TclEngine tcl, ArgQ argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");

        tcl.setResult(styleMap.getNames());
    }

}
