/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * SimpleWizardTest.java
 * JUnit based test
 *
 * Created on March 2, 2005, 11:18 PM
 */

package org.netbeans.spi.wizard;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import junit.framework.*;
import org.netbeans.modules.wizard.MergeMap;



/**
 *
 * @author tim
 */
public class InfoTest extends TestCase {
    public InfoTest(java.lang.String testName) {

        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(InfoTest.class);

        return suite;
    }

    /**
     * Test of setWizard method, of class org.netbeans.spi.wizard.SimpleWizard.Info.
     */
    public void testSetWizard() {
        System.out.println("testSetWizard");
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizardInfo info = new SimpleWizardInfo (impl);
        SimpleWizard wiz = new SimpleWizard (info);
        
        assertEquals (wiz, info.getWizard());
    }

    /**
     * Test of getWizard method, of class org.netbeans.spi.wizard.SimpleWizard.Info.
     */
    public void testGetWizard() {
        System.out.println("testGetWizard");
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizardInfo info = new SimpleWizardInfo (impl);
        SimpleWizard wiz = new SimpleWizard (info);
        assertEquals (wiz, info.getWizard());

        wiz = null;

        for (int i=0; i < 10; i++) {
            System.gc();
        }
        assertNull (info.getWizard());
    }

    /**
     * Test of createPanel method, of class org.netbeans.spi.wizard.SimpleWizard.Info.
     */
    public void testCreatePanel() {
        System.out.println("testCreatePanel");
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizardInfo info = new SimpleWizardInfo (impl);
        SimpleWizard wiz = new SimpleWizard (info);

        //Okay, this tests nothing but our impl...
        String id = "a";
        JComponent comp = impl.createPanel(info, id, new MergeMap("a"));
        assertNotNull (comp);
        assertEquals ("a", comp.getName());
    }


    /**
     * Test of validateExistingPanel method, of class org.netbeans.spi.wizard.SimpleWizard.Info.
     */
    public void testRecycleExistingPanel() {
        System.out.println("testRecycleExistingPanel");
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizard wiz = new SimpleWizard (impl);

        MergeMap settings = new MergeMap("a");

        JComponent comp = wiz.navigatingTo("a", settings);

        assertEquals (1, settings.size());
        assertEquals (Boolean.TRUE, settings.get (settings.keySet().iterator().next()));

        settings.push("b");

        JComponent comp2 = wiz.navigatingTo ("b", settings);
        assertEquals (2, settings.size());
        assertEquals (settings.keySet(), new HashSet (Arrays.asList(new String[] {"a", "b"})));

        settings.popAndCalve();
        JComponent comp3 = wiz.navigatingTo ("a", settings);
        assertNull (settings.get("b"));

        assertSame (comp3, comp);

        impl.assertRecycled(comp3);
        impl.assertRecycledId("a");
        impl.clear();

        settings.push ("b");
        JComponent comp4 = wiz.navigatingTo ("b", settings);
        assertSame (comp4, comp2);

        impl.assertRecycled (comp4);
        impl.assertRecycledId ("b");

    }

    /**
     * Test of setProblem method, of class org.netbeans.spi.wizard.SimpleWizard.Info.
     */
    public void testSetProblem() {
        System.out.println("testSetValid");
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizardInfo info = new SimpleWizardInfo (impl);
        SimpleWizard wiz = new SimpleWizard (info);
        WL l = new WL (wiz);

        assertFalse (info.isValid());
        info.setProblem("problem");

//        l.assertNoChange("Going from false to false should not fire an event");

        assertFalse (info.isValid());
        info.setProblem (null);
        assertTrue (info.isValid());
        l.assertCanProceedChanged("Setting valid to true did not fire an event");
    }

    /**
     * Test of setCanFinish method, of class org.netbeans.spi.wizard.SimpleWizard.Info.
     */
    public void testSetCanFinish() {
        System.out.println("testSetCanFinish");
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizardInfo info = new SimpleWizardInfo (impl);
        SimpleWizard wiz = new SimpleWizard (info);

        assertFalse (info.canFinish());
        info.setProblem (null);
        info.setCanFinish (true);
        assertTrue (info.canFinish());

        info.setProblem ("problem");
        assertFalse (info.canFinish());

        info.setProblem (null);
        assertTrue (info.canFinish());

    }

    /**
     * Test of getTitle method, of class org.netbeans.spi.wizard.SimpleWizard.Info.
     */
    public void testGetTitle() {
        System.out.println("testGetTitle");
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizard wiz = new SimpleWizard (impl);
        assertEquals ("Test Wizard", wiz.getTitle());
    }

    /**
     * Test of update method, of class org.netbeans.spi.wizard.SimpleWizard.Info.
     */
    public void testUpdate() {
        System.out.println("testUpdate");
    }

    /**
     * Test of fire method, of class org.netbeans.spi.wizard.SimpleWizard.Info.
     */
    public void testFire() {
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizardInfo info = new SimpleWizardInfo (impl);
        SimpleWizard wiz = new SimpleWizard (info);
        WL wl = new WL (wiz);
        wl.assertNoChange(null);
        info.fire();
        wl.assertCanProceedChanged("Event should have been fired");

    }

private static class WL implements Wizard.WizardListener {
    private Wizard wiz;
    public WL (SimpleWizard wiz) {
        this.wiz = wiz;
        wiz.addWizardListener (this);
    }

    private boolean cpChanged = false;
    public void stepsChanged(Wizard wizard) {
        assertSame (wizard, wiz);
    }

    public void navigabilityChanged(Wizard wizard) {
        assertSame (wizard, wiz);
        cpChanged = true;
    }

    public void assertNoChange (String msg) {
        assertFalse (msg, cpChanged);
    }

    public void assertCanProceedChanged (String msg) {
        boolean was = cpChanged;
        cpChanged = false;
        assertTrue (msg, was);
    }
}

/**
 * Generated implementation of abstract class org.netbeans.spi.wizard.SimpleWizard.Info. Please fill dummy bodies of generated methods.
 */
private static class PanelProviderImpl extends WizardPanelProvider {
    private boolean finished = false;
    private int step = -1;

    PanelProviderImpl(java.lang.String title, java.lang.String[] steps, java.lang.String[] descriptions) {
        super(title, steps, descriptions);
    }

    PanelProviderImpl() {
        super("Test Wizard", new String[] {"a", "b", "c"}, new String[] {"d_a", "d_b", "d_c"});
    }


    String currId = null;
    protected javax.swing.JComponent createPanel(WizardController c, java.lang.String id, java.util.Map settings) {
        step++;
        JPanel result = new JPanel();
        currId = id;
        result.setName (id);
        settings.put (id, Boolean.TRUE);
        return result;
    }

    protected java.lang.Object finish(java.util.Map settings) {
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

