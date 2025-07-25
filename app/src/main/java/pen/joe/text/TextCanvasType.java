package pen.joe.text;

import com.wjduquette.joe.Args;
import com.wjduquette.joe.Joe;
import com.wjduquette.joe.ProxyType;
import com.wjduquette.joe.types.ListValue;
import pen.util.TextCanvas;

public class TextCanvasType extends ProxyType<TextCanvas> {
    public static final TextCanvasType TYPE = new TextCanvasType();

    //-------------------------------------------------------------------------
    // Constructor

    public TextCanvasType() {
        super("TextCanvas");
        proxies(TextCanvas.class);

        //**
        // @package joe.text
        // @type TextCanvas
        // TextCanvas is a canvas for drawing diagrams using monospaced text
        // for output to the terminal.  It can be thought of as a
        // two-dimensional array of character cells, with (0,0) at the
        // top-left with columns extending to the left and rows extending
        // down.  Characters and strings can be inserted at any
        // (*column*, *row*) cell; the canvas will expand automatically.
        initializer(this::_init);

        method("asText",   this::_asText);
        method("get",      this::_get);
        method("height",   this::_height);
        method("put",      this::_put);
        method("size",     this::_size);
        method("width",    this::_width);
        method("toString", this::_toString);
    }

    //-------------------------------------------------------------------------
    // Stringify

    @Override
    public String stringify(Joe joe, Object value) {
        assert value instanceof TextCanvas;
        var tc = (TextCanvas)value;
        return "TextCanvas[" + tc.getWidth() + "x" + tc.getHeight() + "]";
    }

    //-------------------------------------------------------------------------
    // Initializer

    //**
    // @init
    // Creates a new, empty TextCanvas of size [0, 0].
    private Object _init(Joe joe, Args args) {
        args.exactArity(0, "TextCanvas()");
        return new TextCanvas();
    }

    //-------------------------------------------------------------------------
    // Methods

    //**
    // @method asText
    // @result String
    // Returns the contents of the canvas as a String.
    private Object _asText(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(0, "asText()");
        return tc.toString();
    }

    //**
    // @method get
    // @args column, row
    // @result String
    // Gets the character at (*column*, *row*) as a String
    private Object _get(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(2, "get(column, row)");
        var c = joe.toInteger(args.next());
        var r = joe.toInteger(args.next());
        // TODO: Check for negative

        return tc.gets(c, r);
    }

    //**
    // @method height
    // @result Number
    // Returns the height of the canvas in rows.
    private Object _height(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(0, "height()");
        return (double)tc.getHeight();
    }

    //**
    // @method put
    // @args column, row, text
    // @result String
    // Writes the *text* to the canvas at (*column*, *row*).  The first
    // character is placed at (*column*, *row*); subsequent characters
    // are written to the right.  No provision is made for multiline text.
    private Object _put(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(3, "put(c, r, text)");
        var c = joe.toInteger(args.next());
        var r = joe.toInteger(args.next());
        // TODO: Check for negative
        var text = joe.stringify(args.next());

        tc.puts(c, r, text);
        return this;
    }

    //**
    // @method size
    // @result List
    // Returns the size of the canvas as a two-item list, \[*width*, *height*].
    private Object _size(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(0, "size()");
        return ListValue.pair((double)tc.getWidth(), (double)tc.getHeight());
    }

    //**
    // @method width
    // @result Number
    // Returns the width of the canvas in columns.
    private Object _width(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(0, "width()");
        return (double)tc.getWidth();
    }

    //**
    // @method toString
    // @result String
    // Returns the canvas's string representation, which is not its
    // content.
    private Object _toString(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(0, "toString()");
        return stringify(joe, tc);
    }
}
