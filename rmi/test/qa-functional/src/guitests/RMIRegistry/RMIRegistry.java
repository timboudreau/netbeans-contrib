package guitests.RMIRegistry;

/*
 * SimpleNbJUnitTest.java
 * NetBeans JUnit based test
 *
 * Created on November 21, 2001, 2:54 PM
 */                

import junit.framework.*;
import org.netbeans.junit.*;
import support.*;
import org.netbeans.modules.rmi.registry.*;
import org.netbeans.modules.rmi.settings.*;

/**
 * 
 * @author tb115823
 */
public class RMIRegistry extends NbTestCase {
    
    private static final String workpackage = "data/work"; // NOI18N
    

    public RMIRegistry(java.lang.String testName) {
        super(testName);
    }        
        
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    } 
    
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new NbTestSuite();
        
        suite.addTest(new RMIRegistry("testRunLocalRegistry"));
        suite.addTest(new RMIRegistry("testRegistryRefresh"));
        suite.addTest(new RMIRegistry("testCheckInterface"));
        suite.addTest(new RMIRegistry("testDeleteRegistryItem"));
        suite.addTest(new RMIRegistry("testAddRegistry"));
        suite.addTest(new RMIRegistry("testResetLoader"));
        
        
        return suite;
    }            
    
    protected void setUp() {
      
    }
    
    
    
    
    // Add test methods here, they have to start with 'test' name.
    // for example: 
    
    public void testRunLocalRegistry() {
        try {
            //adding local registry item
            RegistryItem regitem = RMIRegistrySupport.addLocalRegistryItem();
            RMIRegistrySupport.setInternalRegistryPort();
            try { Thread.currentThread().sleep(5000);}
            catch(InterruptedException e) {}
            
            //runLocalRegistry();
            
        } catch(Exception ex) {
            System.out.println("unexpected excepton");
            log(ex.getMessage());
            fail(ex.getMessage());
        }    
    }
    
    public void testRegistryRefresh() {
        try {
            log("... trying to run " + workpackage + "/HelloWorldImpl.java");
            RegistrySupport.runRMIServer(workpackage + "/HelloWorldImpl.java",Support.RMI_UNICAST_EXPORT,"",false);
            RegistrySupport.refreshRegistry("localhost:1099");
            RegistrySupport.checkInterface("RMI Registry|localhost:1099|data.work.HelloWorldImpl","HelloWorld","sayHello");
        } catch(Exception ex) {
            ex.printStackTrace(getLog());
            fail(ex.getMessage());
        }    
    }
    
    public void testCheckInterface() {
        try {
            RegistrySupport.checkInterface("RMI Registry|localhost:1099|data.work.HelloWorldImpl","HelloWorld","sayHello");
            RegistrySupport.checkInterface("RMI Registry|localhost:1099|data.work.HelloWorldImpl","Remote","");
        } catch(Exception ex) {
            ex.printStackTrace();
            ex.printStackTrace(getLog());
            fail(ex.getMessage());
        }    
    }
    
    public void testDeleteRegistryItem() {
        try {
            RegistrySupport.deleteRegistryItem("RMI Registry|localhost:1099|data.work.HelloWorldImpl");
        } catch(Exception ex) {
            ex.printStackTrace();
            ex.printStackTrace(getLog());
            fail(ex.getMessage());
        }
    }

    
    public void testAddRegistry() {
        try {
            RegistrySupport.deleteRegistryItem("RMI Registry|localhost:1099");
            RegistrySupport.addRegistry();
        } catch(Exception ex) {
            ex.printStackTrace();
            ex.printStackTrace(getLog());
            fail(ex.getMessage());
        }    
    }

    public void testResetLoader() {
        
        try {
            RegistrySupport.runRMIServer(workpackage + "/HelloWorldImpl.java",Support.RMI_UNICAST_EXPORT,"",false);
            RegistrySupport.runRMIServer(workpackage + "/New/HelloWorldImpl.java",Support.RMI_EXECUTOR,"data.work.HelloWorldImpl",false);
        
            RegistrySupport.resetLoader();
            RegistrySupport.checkInterface("RMI Registry|localhost:1099|data.work.HelloWorldImpl","HelloWorld","hello");
        } catch(Exception ex) {
            ex.printStackTrace();
            ex.printStackTrace(getLog());
            fail(ex.getMessage());
        }    
    }
    
} 
 