/*
 * SingleColoringPanelTest.java
 * JUnit 4.x based test
 *
 * Created on July 2, 2007, 12:17 PM
 */

package org.netbeans.modules.editorthemes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
//import org.junit.Test;
import junit.framework.TestCase;
//import static org.junit.Assert.*;

/**
 *
 * @author tim
 */
public class SingleColoringPanelTest extends TestCase {
    
    public SingleColoringPanelTest(String nm) {
        super (nm);
    }

//    @org.junit.Before
    public void setUp() throws Exception {
        JFrame jf = new JFrame();
        jf.getContentPane().setLayout (new BorderLayout());
        JPanel pnl = new JPanel();
        pnl.setLayout (new BoxLayout(pnl, BoxLayout.PAGE_AXIS));
        JScrollPane pane = new JScrollPane(pnl);
        jf.getContentPane().add (pane, BorderLayout.CENTER);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        Set <Controls> config = EnumSet.of(Controls.BACKGROUND,
                Controls.FOREGROUND, Controls.BOLD);
                
        
        SingleColoringPanel.Factory factory = new SingleColoringPanel.Factory (new C());
        for (int i=0; i < 15; i ++) {
            SingleColoringPanel p = factory.create();
//            p.configure(config);
            String name = "Color " + i;
            Color bgColor = Color.WHITE;
            Color fgColor = Color.BLACK;
            Color fxColor = Color.GREEN;
            boolean inheritFg = true;
            boolean inheritBg = true;
            boolean inheritEnabled = i != 0;
            boolean italic = false;
            boolean bold = false;
            boolean strike = false;
            boolean underline = false;
            Color defaultBg = Color.WHITE;
            Color defaultFg = Color.BLACK;
            Color defaultFx = Color.RED;
            p.setup(name, name, bgColor, fgColor, fxColor, inheritFg, inheritBg, 
                    inheritEnabled, italic, bold, strike, 
                    underline, defaultBg, defaultFg, defaultFx);
            pnl.add (p);
        }
        jf.pack();
        jf.setVisible(true);
    }

    public void testSomething() throws Exception {
        Thread.sleep(60000);
    }
    
    private static class C implements SingleColoringPanel.Factory.Controller {

        public void selectionChanged(SingleColoringPanel old, SingleColoringPanel nue) {
            System.err.println("Sel changed to " + (nue == null ? "[null]" : nue.name()));
        }

        public void changed(SingleColoringPanel pnl) {
            System.err.println("Changed " + pnl.name());
        }
        
    }

}
