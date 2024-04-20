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
    public void testUnbalanced() {
        test("testUnbalanced");
        checkThrow(() -> buff.ul().li().ulEnd())
            .containsString("End tag imbalance, expected </li>, got </ul>.");
    }

    @Test
    public void testIndent() {
        test("testIndent");
        buff.startln("a")
            .startln("b")
            .startln("c")
            .p("My Para")
            .endln("c")
            .endln("b")
            .endln("a");
        println("[" + buff.toString() + "]");
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
    public void testP_pEnd() {
        test("testP_pEnd");
        buff.p().text("My para").pEnd();
        check(buff.toString()).eq("<p>My para</p>");
    }
}
