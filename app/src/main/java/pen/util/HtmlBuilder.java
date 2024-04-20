package pen.util;

import java.util.Set;
import java.util.Stack;

/**
 * HTML Builder is for building HTML text programmatically for display
 * in the JavaFX WebView.  It is not intended for building static
 * web pages to be saved to disk—at least, not at this time.
 */
public class HtmlBuilder {
    //-------------------------------------------------------------------------
    // Tables

    // Tags not in one of the following sets are assumed to begin on a new
    // line and end on the same line.

    // Tags that do not affect the indent level or add newlines.
    private final Set<String> spans = Set.of(
        "b", "i"
    );

    // Tags that are always on a line by themselves
    private final Set<String> solos = Set.of(
        "ul", "ol", "dl", "div"
    );

    //-------------------------------------------------------------------------
    // Instance Variables

    // The buffer to receive the HTML
    private StringBuilder buff = new StringBuilder();

    // The indent leader
    private final String leader;

    // Open elements
    private final Stack<String> stack = new Stack<>();

    // Indent depth
    private int depth = 0;

    // Whether to wrap the output in "<html><body>...</body></html>"
    private boolean wrapOutput = true;

    //-------------------------------------------------------------------------
    // Constructor

    public HtmlBuilder() {
        this("  ");
    }

    public HtmlBuilder(String leader) {
        this.leader = leader;
    }

    //-------------------------------------------------------------------------
    // Rendering

    public void clear() {
        buff = new StringBuilder();
        stack.clear();
        depth = 0;
    }

    public HtmlBuilder wrapOutput(boolean flag) {
        this.wrapOutput = flag;
        return this;
    }

    public HtmlBuilder h1(String header) {
        return push("h1").print(header).pop("h1");
    }

    public HtmlBuilder p() {
        return push("p");
    }

    public HtmlBuilder p(String text) {
        return p().print(text).pEnd();
    }

    public HtmlBuilder pEnd() {
        return pop("p");
    }

    public HtmlBuilder ul() {
        return push("ul");
    }

    public HtmlBuilder ol() {
        return push("ol");
    }

    public HtmlBuilder li() {
        return push("li");
    }

    public HtmlBuilder li(String text) {
        return li().print(text).liEnd();
    }

    public HtmlBuilder liEnd() {
        return pop("li");
    }

    public HtmlBuilder ulEnd() {
        return pop("ul");
    }

    public HtmlBuilder olEnd() {
        return pop("ol");
    }

    public HtmlBuilder b() {
        return push("b");
    }

    public HtmlBuilder b(String text) {
        return b().print(text).bEnd();
    }

    public HtmlBuilder bEnd() {
        return pop("b");
    }

    public HtmlBuilder i() {
        return push("i");
    }

    public HtmlBuilder i(String text) {
        return i().print(text).iEnd();
    }

    public HtmlBuilder iEnd() {
        return pop("i");
    }

    public HtmlBuilder print(String text) {
        var sanitized = text
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
        buff.append(sanitized);
        return this;
    }

    public HtmlBuilder println(String text) {
        newline();
        return print(text);
    }

    //-------------------------------------------------------------------------
    // Internals

    private HtmlBuilder push(String tag) {
        stack.push(tag);

        if (spans.contains(tag)) {
            // Spans do not start a new line.
            buff.append(tag(tag));
        } else if (solos.contains(tag)) {
            // Solos start a new line and add an indent level.
            newline();
            buff.append(leader.repeat(depth)).append(tag(tag)).append("\n");
            ++depth;
        } else {
            // Others start a new line but do not increase the stack level.
            newline();
            buff.append(leader.repeat(depth)).append(tag(tag));
        }

        return this;
    }

    private HtmlBuilder pop(String tag) {
        // FIRST, check for tag mismatch
        if (!stack.peek().equals(tag)) {
            throw tagMismatch(tag);
        }

        if (spans.contains(tag)) {
            // Spans just close; they do not end the line or decrease the
            // stack level.
            buff.append(endTag(tag));
        } else if (solos.contains(tag)) {
            // Solos end the line and decrease the stack level.
            newline();
            --depth;
            buff.append(leader.repeat(depth)).append(endTag(tag)).append("\n");
        } else {
            // Others end the line but do not decrease the stack level.
            buff.append(endTag(tag)).append("\n");
        }
        stack.pop();
        return this;
    }

    // Adds a newline if needed.
    private void newline() {
        if (!buff.toString().endsWith("\n")) {
            buff.append("\n");
        }
    }

    private String tag(String tag) {
        return "<" + tag + ">";
    }

    private String endTag(String tag) {
        return "</" + tag + ">";
    }


    private IllegalStateException tagMismatch(String tag) {
        return new IllegalStateException(
            "Mismatched end tag, expected </" + stack.peek() +
                ">, got </" + tag + ">.");
    }


    //-------------------------------------------------------------------------
    // Access

    @Override
    public String toString() {
        if (!stack.isEmpty()) {
            throw new IllegalStateException(
                "Unclosed element, <" + stack.peek() + ">.");
        }

        if (wrapOutput) {
            return "<html><body>\n" + buff.toString().trim() + "\n</body></html>";
        } else {
            return buff.toString().trim();
        }
    }
}
