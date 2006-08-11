/*
 * ToTestTest.java
 * JUnit based test
 *
 * Created on August 11, 2006, 8:55 AM
 */

package testapplication;

import junit.framework.TestCase;
import junit.framework.*;

/**
 *
 * @author Tim Boudreau
 */
public class ToTestTest extends TestCase {
    
    public ToTestTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        System.out.println("setup");
    }

    protected void tearDown() throws Exception {
    }

    public void testA() {
        ToTest t = new ToTest();
        assertEquals ("a", t.a());
    }

    public void testB() {
        ToTest t = new ToTest();
        assertEquals ("a", t.b());
    }

    public void testC() {
        fail ("this test fails");
    }

    public void testBad() {
        ToTest t = new ToTest();
        assertEquals (1, t.bad());
    }
    
}
