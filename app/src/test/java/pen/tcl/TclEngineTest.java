package pen.tcl;

import org.junit.Before;
import org.junit.Test;
import tcl.lang.TCL;

import static org.junit.Assert.assertEquals;

public class TclEngineTest {
    TclEngine engine;

    @Before
    public void setup() {
        engine = new TclEngine();
    }

    @Test
    public void testErrorInfo() {
        try {
            engine.eval("""
proc howdy {} {
    error "simulated error"
}
howdy
""");
        } catch (TclEngineException ex) {
            assertEquals("simulated error", ex.getMessage());
            assertEquals(4, ex.getErrorLine());
            assertEquals("""
                simulated error
                    while executing
                "error "simulated error""
                    (procedure "howdy" line 2)
                    invoked from within
                "howdy\"""",
                ex.getErrorInfo());
        }
    }

    private String errorInfo() {
        try {
            return engine.interp().getVar("errorInfo", TCL.GLOBAL_ONLY).toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
