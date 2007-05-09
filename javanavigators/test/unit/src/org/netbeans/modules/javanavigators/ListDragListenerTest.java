/*
 * ListDragListenerTest.java
 * JUnit based test
 *
 * Created on April 24, 2007, 10:10 AM
 */

package org.netbeans.modules.javanavigators;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.WindowConstants;
import junit.framework.TestCase;

/**
 *
 * @author Tim
 */
public class ListDragListenerTest extends TestCase {
    
    public ListDragListenerTest(String testName) {
        super(testName);
    }

    JFrame jf;
    JList list;
    protected void setUp() throws Exception {
        jf = new JFrame();
        list = new JList();
        
        AsynchListModel <Description> mdl = new AsynchListModel <Description> (
                Description.POSITION_COMPARATOR);
        
        List <Description> descs = new ArrayList <Description>();
        for (char c = 'A'; c <= 'Z'; c++) {
            Description d = new Description();
            d.name = "Item " + c;
            d.htmlHeader = d.name;
            d.pos = (int) c * 4;
            descs.add (d);
        }
        LDL ldl = new LDL (list);
        list.setModel (mdl);
        list.setCellRenderer(new CellRenderer());
        
        jf.setLayout (new BorderLayout());
        jf.add (list, BorderLayout.CENTER);
        jf.setBounds (20, 20, 500, 500);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setVisible (true);
        mdl.setContents(descs, true);
    }

    protected void tearDown() throws Exception {
        jf.dispose();
    }
    
    public void testSomething() throws Exception {
        Thread.sleep(20000);
    }
    
    static class LDL extends ListDragListener {
        LDL (JList list) {
            super (list);
        }
        protected void repositionElementRelativeTo (Description d, boolean above) {
            
        }
    }

}
