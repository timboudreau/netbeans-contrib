/*
 * EjbFreeFormActionProviderTest.java
 * JUnit based test
 *
 * Created on 17. leden 2006, 13:14
 */

package org.netbeans.modules.j2ee.ejbfreeform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import junit.framework.*;
import org.netbeans.spi.project.ActionProvider;
import org.openide.util.Lookup;

/**
 *
 * @author jungi
 */
public class EjbFreeFormActionProviderTest extends TestBase {
    
    public EjbFreeFormActionProviderTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        super.setUpProject();
    }

    protected void tearDown() throws Exception {
    }

    public void testGetSupportedActions() {
        ActionProvider ap = (ActionProvider)ejbFF.getLookup().lookup(ActionProvider.class);
        assertNotNull("have an action provider", ap);
        List/*<String>*/ actionNames = new ArrayList(Arrays.asList(ap.getSupportedActions()));
        Collections.sort(actionNames);
        assertEquals("right action names", Arrays.asList(new String[] {"build", "clean", "compile.single", "copy", "debug", "delete", "deploy", "javadoc", "move", "rebuild", "redeploy", "rename", "run", "test"}), actionNames);
        assertTrue("clean is enabled", ap.isActionEnabled("clean", Lookup.EMPTY));
        try {
            ap.isActionEnabled("frobnitz", Lookup.EMPTY);
            fail("Should throw IAE for unrecognized commands");
        } catch (IllegalArgumentException e) {
            // Good.
        }
        try {
            ap.invokeAction("goetterdaemmerung", Lookup.EMPTY);
            fail("Should throw IAE for unrecognized commands");
        } catch (IllegalArgumentException e) {
            // Good.
        }
        // XXX actually test running the action? how to know when it is done though? there is no API for that...
        // when Ant logger API is available, could provide a null InputOutput impl, and test that the right messages are logged
    }
    
}
