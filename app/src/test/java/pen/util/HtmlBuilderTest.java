package pen.util;

import org.junit.Before;
import org.junit.Test;
import pen.Ted;

import static pen.checker.Checker.check;
import static pen.checker.Checker.checkThrow;

public class HtmlBuilderTest extends Ted {
    private HtmlBuilder buff;

    @Before
    public void setup() {
        buff = new HtmlBuilder().wrapOutput(false);
    }

    @Test
    public void testEmpty() {
        test("testEmpty");
        buff = new HtmlBuilder();
        check(buff.toString())
            .eq("<html><body>\n\n</body></html>");
    }

    @Test
    public void testUnclosed() {
        test("testUnclosed");
        buff.p();
        checkThrow(() -> buff.toString())
            .containsString("Unclosed element, <p>");
    }

    @Test
    public void testMismatch() {
        test("testMismatch");
        checkThrow(() -> buff.ul().li().ulEnd())
            .containsString("Mismatched end tag, expected </li>, got </ul>.");
    }

    @Test
    public void testH1() {
        test("testH1");
        buff.h1("My Title");

        check(buff.toString()).eq("<h1>My Title</h1>");
    }

    @Test
    public void testP() {
        test("testP");
        buff.p("My para");
        check(buff.toString()).eq("<p>My para</p>");
    }

    @Test
    public void testParas() {
        test("testParas");
        buff.p("Para1");
        buff.p("Para2");
        check(buff.toString()).eq("<p>Para1</p>\n<p>Para2</p>");
    }

    @Test
    public void testUL() {
        test("testUL");

        buff.ul()
            .li("Item1")
            .li("Item2")
            .ulEnd();
        check(buff.toString()).eq("""
            <ul>
              <li>Item1</li>
              <li>Item2</li>
            </ul>""");
    }

    @Test
    public void testOL() {
        test("testOL");

        buff.ol()
            .li("Item1")
            .li("Item2")
            .olEnd();
        check(buff.toString()).eq("""
            <ol>
              <li>Item1</li>
              <li>Item2</li>
            </ol>""");
    }

    @Test
    public void testB() {
        test("testB");
        buff.p().b("boldface").pEnd();
        check(buff.toString()).eq("<p><b>boldface</b></p>");
    }

    @Test
    public void testI() {
        test("testI");
        buff.p().i("italic").pEnd();
        check(buff.toString()).eq("<p><i>italic</i></p>");
    }
}
