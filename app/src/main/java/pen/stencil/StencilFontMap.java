package pen.stencil;

import java.util.*;

/**
 * A class for accumulating named fonts; for used by Tcl extensions.
 */
public class StencilFontMap {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final Map<String, StencilFont> name2font = new TreeMap<>();
    private final Map<StencilFont, String> font2name = new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public StencilFontMap() {
        putFont(StencilFont.SANS12);
        putFont(StencilFont.SERIF12);
        putFont(StencilFont.MONO12);
    }

    //-------------------------------------------------------------------------
    // Accessors

    public List<String> getNames() {
        return new ArrayList<>(name2font.keySet());
    }

    public boolean hasFont(String name) {
        return name2font.containsKey(name);
    }

    public StencilFont getFont(String name) {
        var font = name2font.get(name);
        if (font == null) {
            throw new IllegalArgumentException(
                "No such font: \"" + name + "\"");
        }
        return font;
    }

    public void putFont(StencilFont font) {
        if (hasFont(font.getName())) {
            throw new IllegalArgumentException(
                "Font already exists: \"" + font.getName() + "\"");
        }

        name2font.put(font.getName(), font);
        font2name.put(font, font.getName());
    }

    public Optional<String> nameOf(StencilFont font) {
        return Optional.ofNullable(font2name.get(font));
    }
}
