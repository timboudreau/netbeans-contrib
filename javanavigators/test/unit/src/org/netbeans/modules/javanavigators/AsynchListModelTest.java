package org.netbeans.modules.javanavigators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.ListDataEvent;
import junit.framework.*;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Tim
 */
public class AsynchListModelTest extends TestCase {
    
    public AsynchListModelTest(String testName) {
        super(testName);
    }

    private static final List <String> STUFF_ONE = Arrays.<String>asList(new String[] {
        "one", "two", "three", "four"
    });
    
    private static final List <String> STUFF_TWO = Arrays.<String>asList(new String[] {
        "one", "two", "three", "four", "five", "six"
    });
    
    private static final List <String> STUFF_THREE = Arrays.<String>asList(new String[] {
        "three", "four", "five", "six"
    });
    
    private static final List <String> STUFF_FOUR = Arrays.<String>asList(new String[] {
        "one", "three", "five", "seven"
    });
    
    
    private AsynchListModel <String> model = null;
    protected void setUp() throws Exception {
        System.setProperty ("in.asynchmodel.unit.test", "true");
        model = new AsynchListModel <String> ();
        l = new LDL();
        model.addListDataListener(l);
    }

    protected void tearDown() throws Exception {
        model = null;
        l = null;
    }

    /**
     * Test of getSize method, of class net.java.sommer.addressbook.AsynchListModel.
     */
    public void testGetSize() {
        System.out.println("getSize");
        
        AsynchListModel instance = new AsynchListModel();
        assertEquals (0, instance.getSize());
        
        model.setContents (STUFF_ONE, false);
        assertEquals (0, instance.getSize());
        
        waitForModel();
        assertEquals (STUFF_ONE.size(), model.getSize());
        
        model.setContents(STUFF_TWO, false);
        waitForModel();
        
        assertEquals (STUFF_TWO.size(), model.getSize());
    }
    
    private void waitForModel() {
        try {
            synchronized (model) {
                model.wait (10000);
            }
        } catch (InterruptedException e) {
            throw new Error (e);
        }
    }

    /**
     * Test of getElementAt method, of class net.java.sommer.addressbook.AsynchListModel.
     */
    public void testGetElementAt() {
        System.out.println("getElementAt");
        model.setContents(STUFF_ONE, false);
        waitForModel();
        for (int i=0; i < STUFF_ONE.size(); i++) {
            assertSame (STUFF_ONE.get(i), model.get(i));
        }
    }
    
    public void testEvents() {
        System.out.println("events");
        model.setContents (STUFF_ONE, false);
        waitForModel();
        l.assertIntervalAdded();
        
        model.setContents(STUFF_TWO, false);
        waitForModel();
        l.assertIntervalAdded();
        
        model.setContents(STUFF_THREE, false);
        waitForModel();
        l.assertIntervalRemoved();
        
        assertEquals (STUFF_THREE, model.getContents());
    }

    /**
     * Test of addListDataListener method, of class net.java.sommer.addressbook.AsynchListModel.
     */
    
    private LDL l;
    private static class LDL implements ListDataListener {
        private ListDataEvent evt;
        public List <ListDataEvent> events = new ArrayList <ListDataEvent> ();
        
        public ListDataEvent assertEvent () {
            ListDataEvent x = evt;
            evt = null;
            assertNotNull (x);
            return x;
        }
        
        public void assertIntervalAdded() {
            ListDataEvent x = assertEvent();
            assertEquals (ListDataEvent.INTERVAL_ADDED, x.getType());
        }
        
        public void assertIntervalRemoved() {
            ListDataEvent x = assertEvent();
            assertEquals (ListDataEvent.INTERVAL_REMOVED, x.getType());
        }
        
        public void assertContentsChanged() {
            ListDataEvent x = assertEvent();
            assertEquals (ListDataEvent.CONTENTS_CHANGED, x.getType());
        }
        
        public void intervalAdded(ListDataEvent e) {
            this.evt = e;
        }

        public void intervalRemoved(ListDataEvent e) {
            this.evt = e;
        }

        public void contentsChanged(ListDataEvent e) {
            this.evt = e;
        }
    }
    
}
