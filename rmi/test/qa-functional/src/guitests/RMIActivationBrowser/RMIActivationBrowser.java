package guitests.RMIActivationBrowser;

/*
 * SimpleNbJUnitTest.java
 * NetBeans JUnit based test
 *
 * Created on November 21, 2001, 2:54 PM
 */                

import junit.framework.*;
import org.netbeans.junit.*;
import support.*;

/**
 *
 * @author tb115823
 */
public class RMIActivationBrowser extends NbTestCase {
    
    private static final String workpackage = "data/work"; // NOI18N
    

    public RMIActivationBrowser(java.lang.String testName) {
        super(testName);
    }        
        
    public static void main(java.lang.String[] args) {
        ActivationBrowserSupport.initActivationBrowserSupport();
        junit.textui.TestRunner.run(suite());
    } 
    
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new NbTestSuite();
        suite.addTest(new RMIActivationBrowser("testaddActivationSystem"));
        suite.addTest(new RMIActivationBrowser("testActivationBrowserRefresh"));
        suite.addTest(new RMIActivationBrowser("testAddActivationGroup"));
        return suite;
    }            
    
    
    
    
    
    
    protected void setUp() {
        /*
        try {
            GUITests.RMIRegistry.RegistrySupport.runRMIServer(workpackage + "/HelloWorldImpl.java",Support.RMI_UNICAST_EXPORT,false);
        } catch(Exception ex) {
           ex.printStackTrace();
        }
         */
    }
    
    
    
    
    // Add test methods here, they have to start with 'test' name.
    // for example: 
    
    public void testActivationBrowserRefresh() {
        ActivationBrowserSupport.refresh();
    }
    
    public void testaddActivationSystem() {
        ActivationBrowserSupport.addActivationSystem();
    }
    
    public void testAddActivationGroup() {
        ActivationBrowserSupport.addActivationGroup();
    }
     
    public void addActivatableObject() {
    }
    
    
} 
 