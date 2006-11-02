package org.sample.registry.model;

import junit.framework.*;

public class SyncUpdateTest extends TestCase {
    
    public SyncUpdateTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testSyncSimpleTypeElement() throws Exception {
        RegistryModel model = Util.loadRegistryModel("test1.xml");
        ServiceType type = model.getRootComponent().getKnownTypes().getKnownTypes().get(0);
        assertEquals("http://www.finance.org/processor", type.getDefinition());
        
        Util.setDocumentContentTo(model, "test1_simpleTypeElement.xml");
        
        assertEquals("http://www.acme.org/processor", type.getDefinition());
    }
}
