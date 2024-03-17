package pen.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A canvas for plotting characters on an X/Y plane, suitable for output
 * to a terminal or inclusion in source code.  Characters cells are
 * counted from (0,0), which is the upper left cell.  The canvas will
 * expand as needed.
 */
@SuppressWarnings("unused")
public class TextCanvas {
    // From Unicode Box Drawing, Block Elements, Geometric Figures, 2500-25FF
    private static final char LIGHT_HORIZONTAL = '\u2500';
    private static final char LIGHT_VERTICAL = '\u2502';
    private static final char LIGHT_DOWN_AND_HORIZONTAL = '\u252C';
    private static final char LIGHT_UP_AND_HORIZONTAL = '\u2534';
    private static final char LIGHT_VERTICAL_AND_LEFT = '\u2524';
    private static final char WHITE_UP_POINTING_TRIANGLE = '\u25B3';
    private static final char WHITE_DOWN_POINTING_TRIANGLE = '\u25BD';

    //-------------------------------------------------------------------------
    // Instance Variables

    private final List<Row> rows = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructor

    public TextCanvas() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Accessors

    /**
     * Puts a character into the canvas.
     * @param c The column
     * @param r The row
     * @param ch The character
     */
    public void put(int c, int r, char ch) {
        extendRows(r);
        rows.get(r).put(c, ch);
    }

    /**
     * Gets a character from the canvas.
     * @param c The column
     * @param r The row
     */
    public char get(int c, int r) {
        extendRows(r);
        return rows.get(r).get(c);
    }

    /**
     * Gets a character from the canvas as a string.
     * @param c The column
     * @param r The row
     * @param ch The character
     */
    public String gets(int c, int r, char ch) {
        extendRows(r);
        return "" + rows.get(r).get(c);
    }

    /**
     * Puts a text string into the canvas, horizontally, starting at the
     * given cell.
     * @param c The column
     * @param r The row
     * @param text the text
     */
    public void puts(int c, int r, String text) {
        for (int i = 0; i < text.length(); i++) {
            put(c + i, r, text.charAt(i));
        }
    }

    private void extendRows(int r) {
        while (rows.size() < r - 1) {
            rows.add(new Row());
        }
    }

    public int getWidth() {
        return rows.stream()
            .mapToInt(row -> row.data.size())
            .max().orElse(0);
    }

    public int getHeight() {
        return rows.size();
    }

    /**
     * Returns the content of the canvas as a string.  Excess whitespace is
     * removed from the end of the rows.
     * @return The string
     */
    public String toString() {
        return rows.stream()
            .map(Row::toString)
            .collect(Collectors.joining());
    }

    //-------------------------------------------------------------------------
    // Helper Types

    private static class Row {
        private final List<Character> data = new ArrayList<>();

        public void put(int c, char ch) {
            extendData(c);
            data.set(c, ch);
        }
        public char get(int c) {
            extendData(c);
            return data.get(c);
        }

        private void extendData(int c) {
            while (data.size() < c + 1) {
                data.add(' ');
            }
        }

        public String toString() {
            return data.stream()
                .map(ch -> Character.toString(ch))
                .collect(Collectors.joining("")).trim();
        }
    }
}
