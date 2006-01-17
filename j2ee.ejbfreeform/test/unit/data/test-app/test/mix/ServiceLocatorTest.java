/*
 * ServiceLocatorTest.java
 * JUnit based test
 *
 * Created on 17. leden 2006, 12:25
 */

package mix;

import junit.framework.TestCase;
import junit.framework.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.net.URL;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;
import javax.mail.Session;

/**
 *
 * @author jungi
 */
public class ServiceLocatorTest extends TestCase {
    
    public ServiceLocatorTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ServiceLocatorTest.class);
        
        return suite;
    }

    public void testGetInstance() {
    }

    public void testGetLocalHome() throws Exception {
    }

    public void testGetRemoteHome() throws Exception {
    }

    public void testGetConnectionFactory() throws Exception {
    }

    public void testGetDestination() throws Exception {
    }

    public void testGetDataSource() throws Exception {
    }

    public void testGetSession() throws Exception {
    }

    public void testGetUrl() throws Exception {
    }

    public void testGetBoolean() throws Exception {
    }

    public void testGetString() throws Exception {
    }
    
}
