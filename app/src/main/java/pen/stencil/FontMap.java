package pen.stencil;

import javafx.scene.text.Font;

import java.util.*;

/**
 * A class for accumulating named fonts; for used by Tcl extensions.
 */
public class FontMap {
    public static final Font SANS12 = Font.font("sans-serif", 12);
    public static final Font SERIF12 = Font.font("serif", 12);
    public static final Font MONO12 = Font.font("monospace", 12);

    //-------------------------------------------------------------------------
    // Instance Variables

    private final Map<String, Font> fontMap = new TreeMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public FontMap() {
        fontMap.put("sans12", SANS12);
        fontMap.put("serif12", SERIF12);
        fontMap.put("mono12", MONO12);
    }

    //-------------------------------------------------------------------------
    // Accessors

    public List<String> getNames() {
        return new ArrayList<>(fontMap.keySet());
    }

    public boolean hasFont(String name) {
        return fontMap.containsKey(name);
    }

    public Font getFont(String name) {
        var font = fontMap.get(name);
        if (font == null) {
            throw new IllegalArgumentException(
                "No such font: \"" + name + "\"");
        }
        return font;
    }

    public void putFont(String name, Font font) {
        if (hasFont(name)) {
            throw new IllegalArgumentException(
                "Font already exists: \"" + name + "\"");
        }

        fontMap.put(name, font);
    }

    public Optional<String> nameOf(Font font) {
        for (var name : fontMap.keySet()) {
            var f = fontMap.get(name);
            if (Objects.equals(f, font)) {
                return Optional.of(name);
            }
        }
        return Optional.empty();
    }
}
