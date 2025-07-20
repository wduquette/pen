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

    private Object _init(Joe joe, Args args) {
        args.exactArity(0, "TextCanvas()");
        return new TextCanvas();
    }

    //-------------------------------------------------------------------------
    // Methods

    private Object _asText(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(0, "asText()");
        return tc.toString();
    }

    private Object _get(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(2, "get(c, r)");
        var c = joe.toInteger(args.next());
        var r = joe.toInteger(args.next());
        // TODO: Check for negative

        return tc.gets(c, r);
    }

    private Object _height(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(0, "height()");
        return (double)tc.getHeight();
    }

    private Object _put(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(3, "put(c, r, text)");
        var c = joe.toInteger(args.next());
        var r = joe.toInteger(args.next());
        // TODO: Check for negative
        var text = joe.stringify(args.next());

        tc.puts(c, r, text);
        return this;
    }

    private Object _size(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(0, "size()");
        return ListValue.pair((double)tc.getWidth(), (double)tc.getHeight());
    }

    private Object _width(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(0, "width()");
        return (double)tc.getWidth();
    }

    private Object _toString(TextCanvas tc, Joe joe, Args args) {
        args.exactArity(0, "toString()");
        return stringify(joe, tc);
    }
}
