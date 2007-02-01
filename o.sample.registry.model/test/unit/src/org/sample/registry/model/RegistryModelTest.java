package org.sample.registry.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.*;
import org.netbeans.modules.xml.xdm.nodes.NodeImpl;

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

        Service s1 = model.getFactory().createService();
        s1.setName("s1");
        ServiceProvider sp1 = model.getFactory().createServiceProvider();
        sp1.setName("sp1");
        s1.setProvider(sp1);
        
        model.startTransaction();
        model.getRootComponent().getEntries().addService(s1);
        model.endTransaction();

        model = Util.dumpAndReloadModel(model);
        List<Service> services = new ArrayList<Service>(model.getRootComponent().getEntries().getServices());
        assertEquals("sp1", services.get(1).getProvider().getName());
        
        model.startTransaction();
        services.get(1).getProvider().setURL(newValue);
        model.endTransaction();
        //Util.dumpToFile(model, new File("c:/temp/test.xml"));
        
        model = Util.dumpAndReloadModel(model);
        services = new ArrayList<Service>(model.getRootComponent().getEntries().getServices());
        assertEquals(newValue, services.get(1).getProvider().getURL());
    }
    
    public void testSetPropertyNull() throws Exception {
        RegistryModel model = Util.loadRegistryModel("test1.xml");
        Registry root = model.getRootComponent();
        Entries entries = root.getEntries();
        Service service = entries.getServices().iterator().next();
        ServiceProvider sp = service.getProvider();
        assertEquals(1, sp.getPeer().getElementsByTagName("url").getLength());
        model.startTransaction();
        sp.setURL(null);
        model.endTransaction();
        assertEquals(0, sp.getPeer().getElementsByTagName("url").getLength());
        assertNull(sp.getURL());
        
        model.startTransaction();
        sp.setURL("");
        model.endTransaction();
        assertEquals(2, ((NodeImpl)sp.getPeer().getElementsByTagName("url").item(0)).getTokens().size());
        this.assertEquals("", sp.getURL());
    }
}
