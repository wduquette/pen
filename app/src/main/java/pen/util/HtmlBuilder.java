package pen.util;

import java.util.ArrayDeque;
import java.util.Stack;

/**
 * HTML Builder is for building HTML text programmatically for display
 * in the JavaFX WebView.  It is not intended for building static
 * web pages to be saved to disk—at least, not at this time.
 */
public class HtmlBuilder {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The buffer to receive the HTML
    private StringBuilder buff = new StringBuilder();

    // The indent leader
    private final String leader;

    // Indented elements
    private final Stack<String> stack = new Stack<>();

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
    }

    public HtmlBuilder wrapOutput(boolean flag) {
        this.wrapOutput = flag;
        return this;
    }

    public HtmlBuilder h1(String header) {
        return start("h1").text(header).end("h1").nl();
    }

    public HtmlBuilder p() {
        return startln("p");
    }

    public HtmlBuilder p(String text) {
        return startln("p").text(text).end("p").nl();
    }

    public HtmlBuilder pEnd() {
        return end("p");
    }

    public HtmlBuilder ul() {
        return startln("ul");
    }

    public HtmlBuilder li() {
        return startln("li");
    }

    public HtmlBuilder li(String text) {
        return li().text(text).liEnd();
    }

    public HtmlBuilder liEnd() {
        return end("li").nl();
    }

    public HtmlBuilder ulEnd() {
        return endln("ul");
    }

    public HtmlBuilder text(String text) {
        var sanitized = text
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
        buff.append(text);
        return this;
    }

    public HtmlBuilder b() {
        return start("b");
    }

    public HtmlBuilder bEnd() {
        return end("b");
    }

    public HtmlBuilder i() {
        return start("i");
    }

    public HtmlBuilder iEnd() {
        return end("i");
    }

    public HtmlBuilder nl() {
        buff.append("\n");
        return this;
    }

    public HtmlBuilder startln(String tag) {
        if (!buff.toString().endsWith("\n")) {
            buff.append("\n");
        }
        buff.append(leader.repeat(stack.size()));
        return start(tag);
    }

    public HtmlBuilder start(String tag) {
        stack.push(tag);
        buff.append("<").append(tag).append(">");
        return this;
    }

    public HtmlBuilder end(String tag) {
        if (stack.peek().equals(tag)) {
            buff.append("</").append(tag).append(">");
            stack.pop();
        } else {
            throw tagImbalance(tag);
        }
        return this;
    }

    public HtmlBuilder endln(String tag) {
        buff.append("\n")
            .append(leader.repeat(stack.size() - 1));
        end(tag);
        return this;
    }

    private IllegalStateException tagImbalance(String tag) {
        return new IllegalStateException(
            "End tag imbalance, expected </" + stack.peek() +
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
