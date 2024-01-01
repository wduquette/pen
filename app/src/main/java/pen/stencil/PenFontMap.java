package pen.stencil;

import java.util.*;

/**
 * A class for accumulating {@link PenFont} instances by assigned name.
 */
public class PenFontMap {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The fonts by name
    private final Map<String, PenFont> name2font = new TreeMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new font map containing the three standard fonts.
     */
    public PenFontMap() {
        putFont(PenFont.SANS12);
        putFont(PenFont.SERIF12);
        putFont(PenFont.MONO12);
    }

    //-------------------------------------------------------------------------
    // Accessors

    /**
     * Gets the list of the names of the defined fonts.
     * @return The list
     */
    public List<String> getNames() {
        return new ArrayList<>(name2font.keySet());
    }

    /**
     * Gets whether there is a font with the given client-assigned name.
     * @param name The name
     * @return True or false
     */
    public boolean hasFont(String name) {
        return name2font.containsKey(name);
    }

    /**
     * Gets the font with the given client-assigned name.
     * @param name The name
     * @return The font.
     * @throws IllegalArgumentException if there is no such font.
     */
    public PenFont getFont(String name) {
        var font = name2font.get(name);
        if (font == null) {
            throw new IllegalArgumentException(
                "No such font: \"" + name + "\"");
        }
        return font;
    }

    /**
     * Puts the font into the font map.  The font will replace any existing
     * font with the same name.
     * @param font The font
     */
    public void putFont(PenFont font) {
        name2font.put(font.getName(), font);
    }
}
