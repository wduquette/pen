package pen.joe.text;

import com.wjduquette.joe.ProxyType;
import pen.util.TextCanvas;

public class GlyphSingleton extends ProxyType<Void> {
    public static final GlyphSingleton TYPE = new GlyphSingleton();

    //-------------------------------------------------------------------------
    // Constructor

    public GlyphSingleton() {
        super("Glyph");

        staticType();

        // TODO: Consider using unicode escapes rather than TextCanvas.*
        constant("LIGHT_HORIZONTAL",
            TextCanvas.LIGHT_HORIZONTAL);
        constant("LIGHT_VERTICAL",
            TextCanvas.LIGHT_VERTICAL);
        constant("LIGHT_DOWN_AND_HORIZONTAL",
            TextCanvas.LIGHT_DOWN_AND_HORIZONTAL);
        constant("LIGHT_UP_AND_HORIZONTAL",
            TextCanvas.LIGHT_UP_AND_HORIZONTAL);
        constant("LIGHT_VERTICAL_AND_LEFT",
            TextCanvas.LIGHT_VERTICAL_AND_LEFT);
        constant("WHITE_UP_POINTING_TRIANGLE",
            TextCanvas.WHITE_UP_POINTING_TRIANGLE);
        constant("WHITE_DOWN_POINTING_TRIANGLE",
            TextCanvas.WHITE_DOWN_POINTING_TRIANGLE);
        constant("BLACK_LEFT_POINTING_TRIANGLE",
            TextCanvas.BLACK_LEFT_POINTING_TRIANGLE);
    }
}
