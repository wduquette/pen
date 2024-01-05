package pen.apis;

import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import pen.stencil.*;
import pen.tcl.Argq;
import pen.tcl.TclEngine;
import tcl.lang.TclException;
import tcl.lang.TclObject;

import static pen.stencil.Stencil.*;

/**
 * A TclEngine extension for drawing using a {@link Stencil}.
 */
public class StencilExtension {
    /**
     * The name of the normal style used with Stencil objects.
     */
    public static final String NORMAL = "normal";

    //-------------------------------------------------------------------------
    // Instance Variables

    private final TclEngine tcl;
    private final Stencil stencil;
    private final StyleMap styleMap = new StyleMap();
    private final PenFontMap fontMap = new PenFontMap();

    //-------------------------------------------------------------------------
    // Constructor

    public StencilExtension(TclEngine engine, Stencil stencil) {
        this.tcl = engine;
        this.stencil = stencil;
        styleMap.make(NORMAL);

        // stencil *
        var sten = engine.ensemble("stencil");

        sten.add("test",      this::cmd_stencilTest);
        sten.add("clear",     this::cmd_stencilClear);
        sten.add("cget",      this::cmd_stencilCget);
        sten.add("configure", this::cmd_stencilConfigure);
        sten.add("label",     this::cmd_stencilLabel);
        sten.add("line",      this::cmd_stencilLine);
        sten.add("rect",      this::cmd_stencilRect);

        // stencil style *
        var style = sten.ensemble("style");
        style.add("cget", this::cmd_stencilStyleCget);
        style.add("configure", this::cmd_stencilStyleConfigure);
        style.add("create", this::cmd_stencilStyleCreate);
        style.add("names", this::cmd_stencilStyleNames);

        // font *
        var font = engine.ensemble("font");

        font.add("cget", this::cmd_fontCget);
        font.add("create", this::cmd_fontCreate);
        font.add("exists", this::cmd_fontExists);
        font.add("families", this::cmd_fontFamilies);
        font.add("names",  this::cmd_fontNames);
    }

    //-------------------------------------------------------------------------
    // Ensemble: stencil *

    private void cmd_stencilTest(TclEngine tcl, Argq argq) {
        stencil.draw(rect().at(10,10).size(100,60));
        stencil.draw(label().at(60,40).pos(Pos.CENTER).text("Stencil Test"));
    }

    // stencil cget ?-option?
    private void cmd_stencilCget(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 1, "?option?");

        if (argq.hasNext()) {
            var opt = argq.next().toString();
            switch (opt) {
                case "-margin"    -> tcl.setResult(stencil.getMargin());
                case "-minheight" -> tcl.setResult(stencil.getMinHeight());
                case "-minwidth"  -> tcl.setResult(stencil.getMinWidth());
                default           -> throw tcl.unknownOption(opt);
            }
        } else {
            // Provide list
            tcl.setResult(tcl.list()
                .item("-margin")
                .item(stencil.getMargin())
                .item("-minheight")
                .item(stencil.getMinHeight())
                .item("-minwidth")
                .item(stencil.getMinWidth())
                .get()
            );
        }
    }

    // stencil clear ?color?
    private void cmd_stencilClear(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 1, "?color?");

        if (argq.hasNext()) {
            var color = tcl.toColor(argq.next());
            stencil.clear(color);
        } else {
            stencil.clear();
        }
    }
    // stencil configure ?option value?...
    private void cmd_stencilConfigure(TclEngine tcl, Argq argq)
        throws TclException {
        tcl.checkMinArgs(argq, 2, "?option value?...");

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-margin"    -> stencil.margin(tcl.toDouble(opt, argq));
                case "-minheight" -> stencil.minHeight(tcl.toDouble(opt, argq));
                case "-minwidth"  -> stencil.minWidth(tcl.toDouble(opt, argq));
                default           -> throw tcl.unknownOption(opt);
            }
        }
    }

    // stencil label text ?option value?...
    // stencil label text ?optionList?
    private void cmd_stencilLabel(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "text ?option value?...");
        var obj = label().style(styleMap.get(NORMAL));
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
        var obj = line().style(styleMap.get(NORMAL));

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
        var rect = rect().style(styleMap.get(NORMAL));

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
            case "-font"       -> style.font(toFont(opt, argq));
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
                case "-font"       -> tcl.setResult(style.getFont().getName());
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
                .item(style.getFont().getName())
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

    //-------------------------------------------------------------------------
    // API: font *

    // font cget name ?option?
    private void cmd_fontCget(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 1, 2, "name ?option?");
        var name = argq.next().toString();

        if (!fontMap.hasFont(name)) {
            throw tcl.expected("font name", name);
        }
        var font = fontMap.getFont(name);

        if (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-family"   -> tcl.setResult(font.getFamily());
                case "-size"     -> tcl.setResult(font.getSize());
                case "-weight"   -> tcl.setResult(font.getWeight());
                case "-posture"  -> tcl.setResult(font.getPosture());
                default          -> throw tcl.expected("font option", opt);
            }
        } else {
            tcl.setResult(tcl.list()
                .item("-family")
                .item(font.getFamily())
                .item("-size")
                .item(font.getSize())
                .item("-weight")
                .item(font.getWeight())
                .item("-posture")
                .item(font.getPosture())
                .get());
        }
    }
    private void cmd_fontCreate(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "name ?options...?");
        var name = argq.next().toString();

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        if (fontMap.hasFont(name)) {
            throw tcl.badValue("Font already exists", name);
        }

        if (!name.matches("^[a-zA-Z]\\w*$")) {
            throw tcl.badValue("Invalid font name", name);
        }

        var builder = new PenFont.Builder(name);

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-family" -> builder.family(tcl.toString(opt, argq));
                case "-size" -> builder.size(tcl.toDouble(opt, argq));
                case "-weight" -> builder.weight(tcl.toEnum(FontWeight.class, opt, argq));
                case "-posture" -> builder.posture(tcl.toEnum(FontPosture.class, opt, argq));
                default -> throw tcl.badValue("Unknown option", opt);
            }
        }

        fontMap.putFont(builder.build());
        tcl.setResult(name);
    }

    private void cmd_fontExists(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 1, 1, "name");
        tcl.setResult(fontMap.hasFont(argq.next().toString()));
    }

    private void cmd_fontFamilies(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        tcl.setResult(Font.getFamilies());
    }

    private void cmd_fontNames(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        tcl.setResult(fontMap.getNames());
    }

    //-------------------------------------------------------------------------
    // Helpers

    public PenFont toFont(TclObject arg) throws TclException {
        var name = arg.toString();
        if (!fontMap.hasFont(name)) {
            throw tcl.expected("font name", name);
        }
        return fontMap.getFont(name);
    }

    public PenFont toFont(String opt, Argq argq) throws TclException {
        return toFont(tcl.toOptArg(opt, argq));
    }
}
