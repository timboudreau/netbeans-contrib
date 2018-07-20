package org.sample.registry.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.*;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.xml.xdm.nodes.NodeImpl;
import org.sample.registry.model.v09.Registry09;
import org.sample.registry.model.v09.Service09;

public class RegistryModelTest extends NbTestCase {
    
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

    public void NO_testReadWrite() throws Exception {
        RegistryModel model = Util.loadRegistryModel("test1.xml");
        Registry root = (Registry) model.getRootComponent();
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
        root = (Registry) model.getRootComponent();
        service = root.getEntries().getServices().iterator().next();
        assertEquals(newValue, service.getProvider().getURL());

        Service s1 = model.getFactory().createService();
        s1.setName("s1");
        ServiceProvider sp1 = model.getFactory().createServiceProvider();
        sp1.setName("sp1");
        s1.setProvider(sp1);
        
        model.startTransaction();
        root.getEntries().addService(s1);
        model.endTransaction();

        model = Util.dumpAndReloadModel(model);
        root = (Registry) model.getRootComponent();
        List<Service> services = new ArrayList<Service>(root.getEntries().getServices());
        assertEquals("sp1", services.get(1).getProvider().getName());
        
        model.startTransaction();
        services.get(1).getProvider().setURL(newValue);
        model.endTransaction();
        //Util.dumpToFile(model, new File(getWorkDir(), "test1.xml"));
        
        model = Util.dumpAndReloadModel(model);
        root = (Registry) model.getRootComponent();
        services = new ArrayList<Service>(root.getEntries().getServices());
        assertEquals(newValue, services.get(1).getProvider().getURL());
    }
    
    public void NO_testSetPropertyNull() throws Exception {
        RegistryModel model = Util.loadRegistryModel("test1.xml");
        Registry root = (Registry) model.getRootComponent();
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
    
    public void testNoNamepsace() throws Exception {
        RegistryModel model = Util.loadRegistryModel("noNS_test1.xml");
        Registry09 root = (Registry09) model.getRootComponent();
        assertEquals("service1", root.getServices().iterator().next().getName());
        
        model.startTransaction();
        Service09 service = model.getFactory().createService09();
        root.addService(service);
        service.setName("service2");
        model.endTransaction();
        
        //Util.dumpToFile(model, new File(getWorkDir(), "test1.xml"));
        assertNull(service.getPeer().getNamespaceURI());
        assertNull(service.getPeer().getPrefix());
    }
}
