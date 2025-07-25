package pen.joe.text;

import com.wjduquette.joe.JoePackage;

public class JoeTextPackage extends JoePackage {
    public static final JoeTextPackage PACKAGE = new JoeTextPackage();

    public JoeTextPackage() {
        super("joe.text");

        //**
        // @package joe.text
        //
        // The `joe.text` package contains APIs for advanced text
        // formatting, and particularly for the formatting of
        // monospace text for display in the terminal.

        type(GlyphSingleton.TYPE);
        type(TextCanvasType.TYPE);
    }
}
