/*
 * RevisedHighlightingPanelTest.java
 * 
 * Created on Jul 2, 2007, 2:23:24 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.editorthemes;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import junit.framework.TestCase;

/**
 *
 * @author tim
 */
public class RevisedHighlightingPanelTest extends TestCase {

    public RevisedHighlightingPanelTest(String s) {
        super (s);
    }
    
    @Override
    public void setUp() throws Exception {
        JFrame jf = new JFrame();
        jf.getContentPane().setLayout (new BorderLayout());
        RevisedHighlightingPanel pnl = new RevisedHighlightingPanel(UIKind.SYNTAX);
        ColorModel mdl = new ColorModel();
        pnl.update(mdl);
        jf.getContentPane().add (pnl, BorderLayout.CENTER);
        jf.pack();
        jf.setVisible(true);
    }
    
    public void testSomething() throws Exception {
        Thread.sleep (30000);
    }

}
