package pen.stencil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A collection of named styles.
 */
@SuppressWarnings("unused")
public class StyleMap {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The map of named styles
    private final Map<String,Style> map = new TreeMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates an empty style map.
     */
    public StyleMap() {
        // nothing to do
    }

    //-------------------------------------------------------------------------
    // Getters

    /**
     * Gets the style with the given name.
     * @param name The name
     * @return The style
     * @throws IllegalArgumentException if the style is undefined.
     */
    public Style get(String name) {
        var style = map.get(name);

        if (style == null) {
            throw new IllegalArgumentException("Unknown style: \"" + name + "\"");
        }

        return style;
    }

    /**
     * Gets the style with the given name, creating it if need be.
     * @param name The name
     * @return The style
     */
    public Style make(String name) {
        var style = map.get(name);

        if (style == null) {
            style = new Style();
            map.put(name, style);
        }

        return style;
    }

    /**
     * Puts a copy of the style object into the map with the given name.
     * @param name The name
     * @param style the style to copy
     */
    public void put(String name, StyleBase<?> style) {
        map.put(name, new Style(style));
    }

    /**
     * Gets the names of the existing styles.
     * @return The names
     */
    public List<String> getNames() {
        return new ArrayList<>(map.keySet());
    }
}
