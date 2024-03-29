package pen.apis;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import pen.stencil.*;
import pen.tcl.Argq;
import pen.tcl.TclEngine;
import pen.tcl.TclExtension;
import tcl.lang.TclException;
import tcl.lang.TclObject;

import static pen.stencil.Stencil.*;

/**
 * A TclEngine extension for drawing using a {@link Stencil}.
 *
 * <p><b>NOTE:</b> at present there's no way to reset the stencil,
 * style map, or font map; DrawTool just creates a new TclEngine for
 * each drawing (which is nice and simple).  But if we use this
 * extension for doing anything other than single drawings, we're
 * going to want a "stencil reset" command.</p>
 */
public class StencilExtension implements TclExtension {
    /**
     * The name of the normal style used with Stencil objects.
     */
    public static final String NORMAL = "normal";

    //-------------------------------------------------------------------------
    // Instance Variables

    // The TclEngine in use.  Set by initialize().
    private TclEngine tcl;

    // The Stencil used to produce drawings.
    private final Stencil stencil;

    // The styleMap
    private StyleMap styleMap;

    // The fontMap
    private PenFontMap fontMap;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new TclEngine extension for drawing using the given stencil.
     * @param stencil The stencil
     */
    public StencilExtension(Stencil stencil) {
        this.stencil = stencil;
        reset();
    }

    public void initialize(TclEngine tcl) {
        this.tcl = tcl;

        // stencil *
        var sten = tcl.ensemble("stencil");

        sten.add("test",      this::cmd_stencilTest);
        sten.add("clear",     this::cmd_stencilClear);
        sten.add("cget",      this::cmd_stencilCget);
        sten.add("configure", this::cmd_stencilConfigure);
        sten.add("line",      this::cmd_stencilLine);
        sten.add("oval",      this::cmd_stencilOval);
        sten.add("rectangle", this::cmd_stencilRectangle);
        sten.add("restore",   this::cmd_stencilRestore);
        sten.add("save",      this::cmd_stencilSave);
        sten.add("symbol",    this::cmd_stencilSymbol);
        sten.add("text",      this::cmd_stencilText);

        // stencil style *
        var style = sten.ensemble("style");
        style.add("cget", this::cmd_stencilStyleCget);
        style.add("configure", this::cmd_stencilStyleConfigure);
        style.add("create", this::cmd_stencilStyleCreate);
        style.add("names", this::cmd_stencilStyleNames);

        // font *
        var font = tcl.ensemble("font");

        font.add("cget", this::cmd_fontCget);
        font.add("create", this::cmd_fontCreate);
        font.add("exists", this::cmd_fontExists);
        font.add("families", this::cmd_fontFamilies);
        font.add("names",  this::cmd_fontNames);

        // symbol *
        var symbol = tcl.ensemble("symbol");
        symbol.add("names", this::cmd_symbolNames);
    }

    @SuppressWarnings("unused")
    public void reset() {
        styleMap = new StyleMap();
        styleMap.make(NORMAL);
        fontMap = new PenFontMap();
    }

    //-------------------------------------------------------------------------
    // Ensemble: stencil *

    // Just a test routine; a convenient place for temporary code.
    private void cmd_stencilTest(TclEngine tcl, Argq argq)
        throws TclException
    {
        throw tcl.error("No test defined");
    }

    // stencil cget ?-option?
    //
    // Gets global stencil parameters.
    private void cmd_stencilCget(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 1, "?option?");

        if (argq.hasNext()) {
            var opt = argq.next().toString();
            switch (opt) {
                case "-background" -> tcl.setResult(stencil.getBackground().toString());
                case "-margin"     -> tcl.setResult(stencil.getMargin());
                case "-minheight"  -> tcl.setResult(stencil.getMinHeight());
                case "-minwidth"   -> tcl.setResult(stencil.getMinWidth());
                // TODO: It would be nice to support "should be one of..."
                default            -> throw tcl.unknownOption(opt);
            }
        } else {
            // Provide list
            tcl.setResult(tcl.list()
                .item("-background")
                .item(stencil.getBackground().toString())
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

    // stencil clear
    //
    // Clears the stencil's canvas to its default background.
    private void cmd_stencilClear(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        stencil.clear();
    }

    // stencil configure ?option value?...
    // stencil configure optionList
    //
    // Configures one or more Stencil options.
    private void cmd_stencilConfigure(TclEngine tcl, Argq argq)
        throws TclException {
        tcl.checkMinArgs(argq, 1, "?option value?...");

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-background" -> stencil.background(tcl.toColor(opt,argq));
                case "-margin"     -> stencil.margin(tcl.toDouble(opt, argq));
                case "-minheight"  -> stencil.minHeight(tcl.toDouble(opt, argq));
                case "-minwidth"   -> stencil.minWidth(tcl.toDouble(opt, argq));
                default            -> throw tcl.unknownOption(opt);
            }
        }
    }

    // stencil label text ?option value?...
    // stencil label text ?optionList?
    //
    // Creates a label with the given text and draws it according to the
    // other options.
    private void cmd_stencilText(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "text ?option value?...");
        var obj = text().style(styleMap.get(NORMAL));
        obj.text(argq.next().toString());

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();
            if (parseStyleOption(obj, opt, argq)) continue;

            switch (opt) {
                case "-at" -> obj.at(tcl.toPoint(opt, argq));
                case "-tack" -> obj.tack(tcl.toEnum(Tack.class, opt, argq));
                default -> throw tcl.unknownOption(opt);
            }
        }

        stencil.draw(obj);
    }

    // stencil line ?option value?...
    // stencil line ?optionList?
    //
    // Creates a line given the options.
    private void cmd_stencilLine(TclEngine tcl, Argq argq)
        throws TclException
    {
        var obj = line().style(styleMap.get(NORMAL));

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();
            if (parseStyleOption(obj, opt, argq)) continue;

            switch (opt) {
                case "-from", "-to"
                               -> obj.to(tcl.toPoint(opt, argq));
                case "-tox"    -> obj.toX(tcl.toDouble(opt, argq));
                case "-toy"    -> obj.toY(tcl.toDouble(opt, argq));
                case "-start"  -> obj.start(tcl.toEnum(Symbol.class, opt, argq));
                case "-end"    -> obj.end(tcl.toEnum(Symbol.class, opt, argq));
                case "-points" -> obj.points(tcl.toPointList(opt, argq));
                default -> throw tcl.unknownOption(opt);
            }
        }

        stencil.draw(obj);
    }

    // stencil oval ?option value?...
    // stencil oval optionList
    //
    // Creates an oval given the options
    private void cmd_stencilOval(TclEngine tcl, Argq argq)
        throws TclException
    {
        var obj = oval().style(styleMap.get(NORMAL));

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            if (parseStyleOption(obj, opt, argq)) continue;
            if (parseBoundedShapeOption(obj, opt, argq)) continue;

            switch (opt) {
                case "-diameter" -> obj.diameter(tcl.toDouble(opt, argq));
                case "-radius" -> obj.radius(tcl.toDouble(opt, argq));
                default -> throw tcl.unknownOption(opt);
            }
        }

        stencil.draw(obj);
    }

    // stencil rectangle ?option value?...
    // stencil rectangle optionList
    //
    // Creates a rectangle given the options
    private void cmd_stencilRectangle(TclEngine tcl, Argq argq)
        throws TclException
    {
        var obj = rectangle().style(styleMap.get(NORMAL));

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            if (parseStyleOption(obj, opt, argq)) continue;
            if (parseBoundedShapeOption(obj, opt, argq)) continue;

            throw tcl.unknownOption(opt);
        }

        stencil.draw(obj);
    }

    // stencil restore
    //
    // Restores the previous pen settings
    private void cmd_stencilRestore(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        stencil.restorePen();
    }

    // stencil save ?option value...?
    //
    // Saves the pen settings, then performs transformations.
    private void cmd_stencilSave(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 0, "?option value?...");

        // FIRST, save the settings
        stencil.savePen();

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        try {
            while (argq.hasNext()) {
                var opt = argq.next().toString();

                switch (opt) {
                    case "-translate": {
                        var point = tcl.toPoint(opt, argq);
                        // TODO: Add Stencil::translate(Point2D)
                        stencil.translate(point.getX(), point.getY());
                        break;
                    }
                    case "-rotate":
                        stencil.rotate(tcl.toDouble(opt, argq));
                        break;
                    case "-scale": {
                        var point = tcl.toPoint(opt, argq);
                        stencil.scale(point.getX(), point.getY());
                        break;
                    }
                    default:
                        throw tcl.unknownOption(opt);
                }
            }
        } catch (TclException ex) {
            // On error, pop the saved settings.
            stencil.restorePen();
            throw ex;
        }
    }

    // stencil symbol name ?option value?...
    // stencil symbol name ?optionList?
    //
    // Draws the named symbol according to the other options.
    private void cmd_stencilSymbol(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "name ?option value?...");
        var obj = symbol().style(styleMap.get(NORMAL));
        obj.symbol(tcl.toEnum(Symbol.class, argq.next()));

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();
            if (parseStyleOption(obj, opt, argq)) continue;

            if (opt.equals("-at")) {
                obj.at(tcl.toPoint(opt, argq));
            } else {
                throw tcl.unknownOption(opt);
            }
        }

        stencil.draw(obj);
    }

    // stencil style create name ?option value?...
    // stencil style create name ?optionList?
    //
    // Creates a new style based on the "normal" style, and configures
    // it.
    private void cmd_stencilStyleCreate(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "name ?option value...?");
        var name = argq.next().toString();
        var style = styleMap.make(name).style(styleMap.get(NORMAL));

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();
            if (!parseStyleOption(style, opt, argq)) {
                throw tcl.unknownOption(opt);
            }
        }
    }

    // stencil style configure name ?option value?...
    // stencil style configure name ?optionList?
    //
    // Configures the named style with new options.
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
            if (!parseStyleOption(style, opt, argq)) {
                throw tcl.unknownOption(opt);
            }
        }
    }

    private boolean parseStyleOption(StyleBase<?> style, String opt, Argq argq)
        throws TclException
    {
        switch (opt) {
            case "-background" -> style.background(tcl.toColor(opt, argq));
            case "-font"       -> style.font(toFont(opt, argq));
            case "-foreground" -> style.foreground(tcl.toColor(opt, argq));
            case "-linewidth"  -> style.lineWidth(tcl.toDouble(opt, argq));
            case "-textcolor"  -> style.textColor(tcl.toColor(opt, argq));
            default -> { return false; }
        }

        return true;
    }

    // stencil style cget name ?option?
    //
    // Gets the value of the given option for the named style, or
    // a dictionary of all the option's values.
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

    // stencil style names
    //
    // Gets the names of the existing styles.
    private void cmd_stencilStyleNames(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");

        tcl.setResult(styleMap.getNames());
    }

    private boolean parseBoundedShapeOption(BoundedShape<?> shape, String opt, Argq argq)
        throws TclException
    {
        switch (opt) {
            case "-at"   -> shape.at(tcl.toPoint(opt, argq));
            case "-size" -> shape.size(tcl.toDim(opt, argq));
            case "-tack" -> shape.tack(tcl.toEnum(Tack.class, opt, argq));
            default -> { return false; }
        }

        return true;
    }

    //-------------------------------------------------------------------------
    // API: font *

    // font cget name ?option?
    //
    // Gets an option value from the named font.
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

    // font create name ?option value...?
    //
    // Creates a named font given the option values.
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

    // font exists name
    //
    // Checks whether there is a font with the given name.
    private void cmd_fontExists(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 1, 1, "name");
        tcl.setResult(fontMap.hasFont(argq.next().toString()));
    }

    // font families
    //
    // Gets a list of the font family names recognized by JavaFX
    // on this host.
    private void cmd_fontFamilies(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        tcl.setResult(Font.getFamilies());
    }

    // font names
    //
    // Gets a list of the names of the named fonts.
    private void cmd_fontNames(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        tcl.setResult(fontMap.getNames());
    }

    //-------------------------------------------------------------------------
    // Enums

    // symbol names
    //
    // Gets the names of the symbols
    private void cmd_symbolNames(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 0, 0, "");
        tcl.setResult(Symbol.values());
    }

    //-------------------------------------------------------------------------
    // Helpers

    /**
     * Gets a PenFont by name as the value of a Tcl argument.
     * @param arg The name
     * @return The font
     * @throws TclException if not found
     */
    public PenFont toFont(TclObject arg) throws TclException {
        var name = arg.toString();
        if (!fontMap.hasFont(name)) {
            throw tcl.expected("font name", name);
        }
        return fontMap.getFont(name);
    }

    /**
     * Gets a PenFont by name as the value of a Tcl option.
     * @param opt The option
     * @param argq The argument queue
     * @return The font
     * @throws TclException if not found
     */
    public PenFont toFont(String opt, Argq argq) throws TclException {
        return toFont(tcl.toOptArg(opt, argq));
    }
}
