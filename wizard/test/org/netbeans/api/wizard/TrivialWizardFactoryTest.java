/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
/*
 * TrivialWizardFactoryTest.java
 * JUnit based test
 *
 * Created on March 4, 2005, 4:33 PM
 */

package org.netbeans.api.wizard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import junit.framework.*;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.netbeans.spi.wizard.WizardPanelProvider;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardController;


/**
 *
 * @author tim
 */
public class TrivialWizardFactoryTest extends TestCase {
    
    public TrivialWizardFactoryTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(TrivialWizardFactoryTest.class);
        
        return suite;
    }
    
    protected void setUp() throws Exception {
        System.setProperty ("TrivialWizardFactory.test", "true");
    }
    
    public static JButton[] getButtons() {
        return TrivialWizardFactory.buttons;
    }
    
    public void testShow() throws Exception {
        System.out.println("testShow");
        PanelProviderImpl impl = new PanelProviderImpl();
        
        Wizard wiz = impl.createWizard();
        
        show (wiz);
        
        while (!impl.active) {
            Thread.currentThread().sleep (200);
        }
        
        JButton next = TrivialWizardFactory.buttons[0];
        JButton prev = TrivialWizardFactory.buttons[1];
        JButton finish = TrivialWizardFactory.buttons[2];
        JButton cancel = TrivialWizardFactory.buttons[3];
        
        assertFalse (next.isEnabled());
        assertFalse (prev.isEnabled());
        assertFalse (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        
        click (impl.cb);
        JCheckBox mcb = impl.cb;
        assertTrue (next.isEnabled());
        assertFalse (prev.isEnabled());
        assertFalse (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        
        click (next);
        assertTrue (prev.isEnabled());
        assertFalse (next.isEnabled());
        assertFalse (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        assertEquals ("b", impl.cb.getText());
        assertNotSame (impl.cb, mcb);
        
        click (prev);
        assertSame (impl.cb, mcb);
        assertTrue (next.isEnabled());
        assertFalse (prev.isEnabled());
        assertFalse (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        assertEquals ("a", impl.cb.getText());
        
        click (impl.cb);
        assertFalse (next.isEnabled());
        assertFalse (prev.isEnabled());
        assertFalse (finish.isEnabled());
        assertTrue (cancel.isEnabled());

        click (impl.cb);
        assertTrue (next.isEnabled());
        assertFalse (prev.isEnabled());
        assertFalse (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        
        click (next);
        assertTrue (prev.isEnabled());
        assertFalse (next.isEnabled());
        assertFalse (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        assertEquals ("b", impl.cb.getText());
        assertFalse (impl.cb.isSelected());
        
        click (impl.cb);
        
        click (next);
        assertTrue (prev.isEnabled());
        assertFalse (next.isEnabled());
        assertFalse (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        assertEquals ("c", impl.cb.getText());
        
        click (impl.cb);
        assertTrue (prev.isEnabled());
        assertFalse (next.isEnabled());
        assertTrue (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        assertEquals ("c", impl.cb.getText());
        
        click (prev);
        assertTrue (prev.isEnabled());
        assertTrue (next.isEnabled());
        assertFalse (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        assertEquals ("b", impl.cb.getText());
        
        click (impl.cb);
        assertTrue (prev.isEnabled());
        assertFalse (next.isEnabled());
        assertFalse (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        assertEquals ("b", impl.cb.getText());
        
        click (impl.cb);
        click (next);
        
        assertTrue (prev.isEnabled());
        assertFalse (next.isEnabled());
        assertTrue (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        assertEquals ("c", impl.cb.getText());
        
        click (impl.cb);
        assertFalse (finish.isEnabled());
        assertFalse (next.isEnabled());
        
        click (impl.cb);
        assertFalse (next.isEnabled());
        assertTrue (finish.isEnabled());
        
        click (finish);
        assertFalse (impl.cb.isShowing());
        assertTrue (impl.finished);
        
    }
    
    private static void show (final Wizard wiz) {
        try {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new TrivialWizardFactory().show (wiz);
                }
            });
            Thread.currentThread().sleep (1000);
        } catch (Exception e) {
            fail (e.getMessage());
        }
        
    }
    
    private static void click (final AbstractButton button) {
        try {
            SwingUtilities.invokeAndWait (new Runnable() {
                public void run() {
                    button.doClick();
                }
            });
//            Thread.currentThread().sleep (500);
        } catch (Exception ie) {
            ie.printStackTrace();
            fail ("interrupted");
        }
    }

    
    private static class PanelProviderImpl extends WizardPanelProvider {
        private boolean finished = false;
        private int step = -1;

        PanelProviderImpl(java.lang.String title, java.lang.String[] steps, java.lang.String[] descriptions) {
            super(title, steps, descriptions);
        }

        PanelProviderImpl() {
            super("Test Wizard", new String[] {"a", "b", "c"}, new String[] {"d_a", "d_b", "d_c"});
        }

        boolean active = false;
        String currId = null;
        JCheckBox cb = null;
        protected JComponent createPanel(final WizardController controller, final java.lang.String id, final java.util.Map settings) {
            step++;
            active = true;
            JPanel result = new JPanel();
            result.setLayout (new BorderLayout());
            cb = new JCheckBox (id);
            cb.addActionListener (new ActionListener() {
                public void actionPerformed (ActionEvent ae) {
                    controller.setProblem(cb.isSelected() ? null : "problem");
                    settings.put (id, cb.isSelected() ? Boolean.TRUE : Boolean.FALSE);
                }
            });

            result.add (cb, BorderLayout.CENTER);
            controller.setProblem (Boolean.TRUE.equals (settings.get (id)) ? null : "problem");

            currId = id;
            result.setName (id);
            settings.put (id, Boolean.TRUE);
            return result;
        }

        protected java.lang.Object finish(java.util.Map settings) {
            finished = true;
            return "finished";
        }

        public void assertCurrent (String id) {
            if (id == null && currId == null) {
                return;
            } else if ((id == null) != (currId == null)) {
                fail ("Non-match: " + id + ", " + currId);
            } else {
                assertEquals (currId, id);
            }
        }

        private JComponent recycled = null;
        private String recycledId = null;
        private Map recycledSettings = null;
        protected void recycleExistingPanel (String id, WizardController controller, Map settings, JComponent panel) {
            recycled = panel;
            recycledId = id;
            recycledSettings = settings;
            cb = (JCheckBox) panel.getComponents()[0];
            controller.setProblem (cb.isSelected() ? null : "problem");
        }

        public void assertRecycledSettingsContains (String key, String value) {
            assertNotNull (recycledSettings);
            assertEquals (value, recycledSettings.get(key));
        }

        public void assertRecycled (JComponent panel) {
            assertNotNull (recycled);
            assertSame (panel, recycled);
        }

        public void assertRecycledId (String id) {
            assertNotNull (recycledId);
            assertEquals (id, recycledId);
        }

        public void clear() {
            recycled = null;
            recycledId = null;
            recycledSettings = null;
        }

        public void assertStep (int step, String msg) {
            assertTrue (msg, step == this.step);
        }

        public void assertFinished (String msg) {
            assertTrue (msg, finished);
        }

        public void assertNotFinished (String msg) {
            assertFalse (msg, finished);
        }
    }    
    
}
