package pen.joe.text;

import com.wjduquette.joe.JoePackage;

public class JoeTextPackage extends JoePackage {
    public static final JoeTextPackage PACKAGE = new JoeTextPackage();

    public JoeTextPackage() {
        super("joe.text");

        type(GlyphSingleton.TYPE);
        type(TextCanvasType.TYPE);
    }
}
