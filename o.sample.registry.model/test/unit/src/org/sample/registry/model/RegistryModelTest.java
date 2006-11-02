package org.sample.registry.model;

import junit.framework.*;
import org.netbeans.modules.xml.xam.dom.DocumentModel;

public class RegistryModelTest extends TestCase {
    
    public RegistryModelTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(RegistryModelTest.class);
        
        return suite;
    }

    public void testReadWrite() throws Exception {
        RegistryModel model = Util.loadRegistryModel("test1.xml");
        Registry root = model.getRootComponent();
        Entries entries = root.getEntries();
        Service service = entries.getServices().iterator().next();
        assertEquals("service1", service.getName());
        ServiceProvider sp = service.getProvider();
        assertEquals("acme.com", sp.getName());
        assertEquals("http://www.acme.com/finance", sp.getURL());
        
        String newValue = "http://www.acme.org/finance";
        model.startTransaction();
        sp.setURL(newValue);
        model.endTransaction();
        
        model = Util.dumpAndReloadModel(model);
        service = model.getRootComponent().getEntries().getServices().iterator().next();
        assertEquals(newValue, service.getProvider().getURL());
    }
    
}
