package pen.apis;

import javafx.geometry.Pos;
import pen.stencil.Stencil;
import pen.stencil.StyleBase;
import pen.stencil.StyleMap;
import pen.tcl.Argq;
import pen.tcl.TclEngine;
import tcl.lang.TclException;

import static pen.stencil.Stencil.*;

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

        sten.add("test",  this::cmd_stencilTest);
        sten.add("label", this::cmd_stencilLabel);
        sten.add("line",  this::cmd_stencilLine);
        sten.add("rect",  this::cmd_stencilRect);

        // stencil style *
        var style = sten.ensemble("style");
        style.add("cget", this::cmd_stencilStyleCget);
        style.add("configure", this::cmd_stencilStyleConfigure);
        style.add("create", this::cmd_stencilStyleCreate);
        style.add("names", this::cmd_stencilStyleNames);
    }

    //-------------------------------------------------------------------------
    // Ensemble: stencil *

    private void cmd_stencilTest(TclEngine tcl, Argq argq) {
        stencil.draw(rect().at(10,10).size(100,60));
        stencil.draw(label().at(60,40).pos(Pos.CENTER).text("Stencil Test"));
    }

    // stencil label text ?option value?...
    // stencil label text ?optionList?
    private void cmd_stencilLabel(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "text ?option value?...");
        var obj = label();
        obj.text(argq.next().toString());

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-at" -> obj.at(tcl.toPoint(opt, argq));
                case "-pos" -> obj.pos(tcl.toEnum(Pos.class, opt, argq));
                default -> parseStyleOption(obj, opt, argq);
            }
        }

        stencil.draw(obj);
    }

    // stencil line ?option value?...
    // stencil line ?optionList?
    private void cmd_stencilLine(TclEngine tcl, Argq argq)
        throws TclException
    {
        var obj = line();

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-to" -> obj.to(tcl.toPoint(opt, argq));
                case "-tox" -> obj.toX(tcl.toDouble(opt, argq));
                case "-toy" -> obj.toY(tcl.toDouble(opt, argq));
                case "-points" -> obj.points(tcl.toPointList(opt, argq));
                default -> parseStyleOption(obj, opt, argq);
            }
        }

        stencil.draw(obj);
    }

    // stencil rect ?option value?...
    private void cmd_stencilRect(TclEngine tcl, Argq argq)
        throws TclException
    {
        var rect = rect();

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-at" -> rect.at(tcl.toPoint(opt, argq));
                case "-size" -> rect.size(tcl.toDim(opt, argq));
                default -> parseStyleOption(rect, opt, argq);
            }
        }

        stencil.draw(rect);
    }

    // stencil style create name ?option value?...
    // stencil style create name ?optionList?
    private void cmd_stencilStyleCreate(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "name ?option value...?");
        var name = argq.next().toString();
        var style = styleMap.make(name);

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();
            parseStyleOption(style, opt, argq);
        }
    }

    // stencil style configure name ?option value?...
    // stencil style configure name ?optionList?
    private void cmd_stencilStyleConfigure(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "name ?option value...?");
        var name = argq.next().toString();
        if (!styleMap.hasStyle(name)) {
            throw tcl.expected("style", name);
        }
        var style = styleMap.get(name);

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();
            parseStyleOption(style, opt, argq);
        }
    }

    private void parseStyleOption(StyleBase<?> style, String opt, Argq argq)
        throws TclException
    {

        switch (opt) {
            case "-background" -> style.background(tcl.toColor(opt, argq));
            case "-font"       -> throw tcl.error("TODO");
            case "-foreground" -> style.foreground(tcl.toColor(opt, argq));
            case "-linewidth"  -> style.lineWidth(tcl.toDouble(opt, argq));
            case "-textcolor"  -> style.textColor(tcl.toColor(opt, argq));
            default -> throw tcl.badValue("unknown option", opt);
        }
    }

    // stencil style cget name ?option?
    private void cmd_stencilStyleCget(TclEngine tcl, Argq argq)
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
                case "-background" -> tcl.setResult(style.getBackground().toString());
                case "-font"       -> tcl.setResult("TODO");
                case "-foreground" -> tcl.setResult(style.getForeground().toString());
                case "-linewidth"  -> tcl.setResult(style.getLineWidth());
                case "-textcolor"  -> tcl.setResult(style.getTextColor().toString());
                default            -> throw tcl.expected("style option", opt);
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

    private void cmd_stencilStyleNames(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");

        tcl.setResult(styleMap.getNames());
    }
}
