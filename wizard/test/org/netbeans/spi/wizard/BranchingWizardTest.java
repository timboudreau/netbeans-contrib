/*
 * BranchingWizardTest.java
 * JUnit based test
 *
 * Created on March 5, 2005, 2:30 PM
 */

package org.netbeans.spi.wizard;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import junit.framework.*;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.modules.wizard.MergeMap;

/**
 *
 * @author tim
 */
public class BranchingWizardTest extends TestCase {
    
    public BranchingWizardTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(BranchingWizardTest.class);
        
        return suite;
    }
    
    public void setUp() throws Exception {
//        javax.swing.UIManager.setLookAndFeel (new javax.swing.plaf.metal.MetalLookAndFeel());
    }
    
    public void testManual() {
        //Uncomment to actually show a wizard and play with it during the
        //test
        
//        Wizard wiz = new MultiBranchController().createWizard();
//        WizardDisplayer.showWizard(wiz);
    }    
    
    public void testSettingsAffectNavigation() {
        BranchControllerImpl bci = new BranchControllerImpl();
        BranchingWizard wiz = (BranchingWizard) bci.createWizard();
        SimpleWizard base = (SimpleWizard) wiz.base;
        SimpleWizardInfo info = base.info;
        
        WL wl = new WL(wiz);
        
        String step = wiz.getNextStep();
        assertEquals (step, "choose");

        MergeMap settings = new MergeMap("step");
        JComponent panel1 = wiz.navigatingTo(step, settings);
        
        assertTrue (settings.isEmpty());
        
        settings.put (KEY_BRANCH, VALUE_FOOD);
        info.setProblem(null);
        wl.assertCanProceedChanged("Can proceed should have changed");
        wl.assertStepsChanged("Steps should have changed");
        assertEquals ("vegetarian", wiz.getNextStep());
        
        settings.put (KEY_BRANCH, VALUE_COLOR);
        info.setProblem (null);
        wl.assertCanProceedChanged("Can proceed should have changed");
        wl.assertStepsChanged("Steps should have changed");
        assertEquals ("orange", wiz.getNextStep());
        
        settings.put (KEY_BRANCH, VALUE_FOOD);
        info.setProblem(null);
        wl.assertCanProceedChanged("Can proceed should have changed");
        wl.assertStepsChanged("Steps should have changed");
        assertEquals ("vegetarian", wiz.getNextStep());
        
        info.setProblem ("problem");
        assertNull (wiz.getNextStep());
        info.setProblem (null);
        assertEquals ("vegetarian", wiz.getNextStep());
        settings.push("vegetarian");
        
        JComponent panel2 = wiz.navigatingTo (wiz.getNextStep(), settings);
        assertNotSame (panel2, panel1);
        
        settings.popAndCalve();
        JComponent panel3 = wiz.navigatingTo (step, settings);
        assertTrue (info.isValid());
        info.setProblem ("bugger");
        wl.assertCanProceedChanged("Can proceed should have changed to false");
        assertFalse (info.isValid());
        info.setProblem (null);
        wl.assertCanProceedChanged("Can proceed should have changed to true");
        assertTrue (info.isValid());
        assertSame ("Not the same panel", panel1, panel3);
        
        JComponent panel4 = wiz.navigatingTo (wiz.getNextStep(), settings);
        assertSame (panel4, panel2);
    }


    
    private static class WL implements Wizard.WizardListener {
        private Wizard wiz;
        public WL (Wizard wiz) {
            this.wiz = wiz;
            wiz.addWizardListener (this);
        }
        
        private boolean cpChanged = false;
        public void stepsChanged(Wizard wizard) {
            assertSame (wizard, wiz);
            stepsChanged = true;
        }
        
        public void navigabilityChanged(Wizard wizard) {
            assertSame (wizard, wiz);
            cpChanged = true;
        }
        
        public void assertNoChange (String msg) {
            assertFalse (msg, cpChanged);
            assertFalse (msg, stepsChanged);
        }
        
        private boolean stepsChanged = false;
        public void assertStepsChanged(String msg) {
            boolean was = stepsChanged;
            stepsChanged = false;
            assertTrue (msg, was);
        }
        
        public void assertCanProceedChanged (String msg) {
            boolean was = cpChanged;
            cpChanged = false;
            assertTrue (msg, was);
        }
    }    

    private static final String KEY_BRANCH = "colorOrFood";
    private static final String VALUE_FOOD = "food";
    private static final String VALUE_COLOR = "color";
    
    private class BranchControllerImpl extends WizardBranchController {
        BranchControllerImpl() {
            super(new Base());
        }
        
        private FoodPanelProvider foodInfo = null;
        private FoodPanelProvider getFoodInfo() {
            if (foodInfo == null) {
                foodInfo = new FoodPanelProvider();
            }
            return foodInfo;
        }
        
        private ColorPanelProvider colorInfo = null;
        private ColorPanelProvider getColorInfo() {
            if (colorInfo == null) {
                colorInfo = new ColorPanelProvider();
            }
            return colorInfo;
        }
        

        protected WizardPanelProvider getPanelProviderForStep (String step, Map settings) {
            String which = (String) settings.get (KEY_BRANCH);
            if (which == null) {
                return null;
            } else if (VALUE_FOOD.equals(which)) {
                return getFoodInfo();
            } else if (VALUE_COLOR.equals(which)) {
                return getColorInfo();
            } else {
                throw new IllegalArgumentException (which);
            }
        }
    }
    
    private static final String KEY_BRANCH2 = "philosophy";
    private static final String VALUE_PRACTICAL = "practical";
    private static final String VALUE_AESTHETIC = "aesthetic";
    
    private class MultiBranchController extends WizardBranchController {
        MultiBranchController() {
            super(new Base2());
        }

        private BranchingWizard bwizard = null;
        public BranchingWizard getBranchWizard() {
            if (bwizard == null) {
                bwizard = new BranchingWizard (new BranchControllerImpl());
            }
            return bwizard;
        }
        
        public SimpleWizard swizard = null;
        public SimpleWizard getSimpleWizard() {
            if (swizard == null) {
                swizard = new SimpleWizard (new PracticalitiesPanelProvider());
            }
            return swizard;
        }

        protected Wizard getWizardForStep(String step, Map settings) {
            String which = (String) settings.get (KEY_BRANCH2);
            if (which == null) {
                return null;
            } else if (VALUE_AESTHETIC.equals(which)) {
                return getBranchWizard();
            } else if (VALUE_PRACTICAL.equals(which)) {
                return getSimpleWizard();
            } else {
                throw new IllegalArgumentException (which);
            }
        }
    }    
    
    
    private static class Base2 extends WizardPanelProvider implements ActionListener {
        JRadioButton aesthetic;
        JRadioButton practical;
        JRadioButton neither;
        Map settings;
        WizardController controller;
        
        public Base2 () {
            super ("The Aesthete's UnWizard", new String[] { "philo" }, new String[] { "State Preference" });
        }
        
        protected JComponent createPanel(WizardController controller, String id, Map settings) {
            this.controller = controller;
            JPanel result = new JPanel();
            result.setLayout (new FlowLayout());
            aesthetic = new JRadioButton ("Aesthetic");
            practical = new JRadioButton ("Practical");
            neither = new JRadioButton ("Neither");
            result.add (aesthetic);
            result.add (practical);
            result.add (neither);
            aesthetic.addActionListener(this);
            practical.addActionListener (this);
            neither.addActionListener (this);
            controller.setProblem ("You must state sort of dude you are");
            this.settings = settings;
            return result;
        }

        protected Object finish(Map settings) throws WizardException {
            throw new Error ("Finish should never be called for base");
        }
        
        public void actionPerformed (ActionEvent ae) {
            if (ae.getSource() == aesthetic) {
                practical.setSelected(false);
                neither.setSelected(false);
                settings.put (KEY_BRANCH2, VALUE_AESTHETIC);
                controller.setProblem (null);
            } else if (ae.getSource() == practical) {
                aesthetic.setSelected(false);
                neither.setSelected(false);
                settings.put (KEY_BRANCH2, VALUE_PRACTICAL);
                controller.setProblem (null);
            } else {
                settings.remove (KEY_BRANCH2);
                aesthetic.setSelected (false);
                practical.setSelected (false);
                controller.setProblem ("Unacceptable!  You must decide!");
            }
            controller.setCanFinish(ae.getSource() != neither);
        }
    }    
    
    private static class Base extends WizardPanelProvider implements ActionListener {
        JRadioButton food;
        JRadioButton colors;
        JRadioButton neither;
        Map settings;
        WizardController controller;
        
        public Base () {
            super ("The Look or Eat Wizard", new String[] { "choose" }, new String[] { "Choose to Eat or Look" });
        }
        
        protected JComponent createPanel(WizardController controller, String id, Map settings) {
            this.controller = controller;
            JPanel result = new JPanel();
            result.setLayout (new FlowLayout());
            food = new JRadioButton ("Food");
            colors = new JRadioButton ("Colors");
            neither = new JRadioButton ("Neither");
            result.add (food);
            result.add (colors);
            result.add (neither);
            food.addActionListener(this);
            colors.addActionListener (this);
            neither.addActionListener (this);
            controller.setProblem ("You must choose a preference");
            this.settings = settings;
            return result;
        }

        protected Object finish(Map settings) throws WizardException {
            throw new Error ("Finish should never be called for base");
        }
        
        public void actionPerformed (ActionEvent ae) {
            if (ae.getSource() == food) {
                colors.setSelected(false);
                neither.setSelected(false);
                settings.put (KEY_BRANCH, VALUE_FOOD);
                controller.setProblem (null);
            } else if (ae.getSource() == colors) {
                food.setSelected(false);
                neither.setSelected(false);
                settings.put (KEY_BRANCH, VALUE_COLOR);
                controller.setProblem (null);
            } else {
                settings.remove (KEY_BRANCH);
                food.setSelected (false);
                colors.setSelected (false);
                controller.setProblem ("Unacceptable!  You must decide!");
            }
            controller.setCanFinish(ae.getSource() != neither);
        }
    }
    
    private static class FoodPanelProvider extends WizardPanelProvider implements ActionListener {
        private JCheckBox meatBox;
        private JCheckBox steakBox;
        private Map settings;
        private WizardController controller;
        
        public FoodPanelProvider() {
            super ("The Look or Eat Wizard", 
                new String[] { "vegetarian", "mealChoice" }, 
                new String[] { "Food preferences", "mealChoice" });
        }
        
        protected JComponent createPanel(WizardController controller, String id, Map settings) {
            this.settings = settings;
            this.controller = controller;
            JPanel result = new JPanel();
            result.setLayout (new FlowLayout());
            
            if ("vegetarian".equals(id)) {
                meatBox = new JCheckBox("I agree to eat meat");
                meatBox.setSelected (Boolean.TRUE.equals (settings.get("likesMeat")));
                meatBox.addActionListener (this);
                result.add (meatBox);
                controller.setProblem (meatBox.isSelected() ? null : "You must eat meat");
                controller.setCanFinish (false);
            } else if ("mealChoice".equals(id)) {
                steakBox = new JCheckBox ("I will have the steak");
                steakBox.addActionListener (this);
                steakBox.setSelected (Boolean.TRUE.equals (settings.get("eatsSteak")));
                result.add (steakBox);
                controller.setProblem (steakBox.isSelected() ? null : "You must order the steak");
                controller.setCanFinish (steakBox.isSelected());
            } else {
                throw new Error ("Unknown ID " + id);
            }
            return result;
        }

        protected Object finish(Map settings) throws WizardException {
            return "Food Finished";
        }
        
        public void actionPerformed (ActionEvent ae) {
            JCheckBox src = (JCheckBox) ae.getSource();
            if (src == meatBox) {
                settings.put ("likesMeat", src.isSelected() ? Boolean.TRUE : Boolean.FALSE);
                controller.setProblem (src.isSelected() ? null : "You must eat meat!");
            } else {
                controller.setCanFinish (src.isSelected());
                settings.put ("eatsSteak", src.isSelected() ? Boolean.TRUE : Boolean.FALSE);
                controller.setProblem (src.isSelected() ? null : "We only serve steak!");
            }
        }
    }
    
    private static class ColorPanelProvider extends WizardPanelProvider implements ActionListener {
        private Map settings;
        private WizardController controller;
        
        public ColorPanelProvider() {
            super ("The Look or Eat Wizard", 
                new String[] { "orange" }, 
                new String[] { "Choose Orange"});
        }
        
        protected JComponent createPanel(WizardController controller, String id, Map settings) {
            this.settings = settings;
            this.controller = controller;
            
            JPanel result = new JPanel();
            result.setLayout (new FlowLayout());
            result.setBackground (Color.ORANGE);
            JCheckBox box = new JCheckBox ("I like orange");
            
            boolean sel = Boolean.TRUE.equals (settings.get("likesOrange"));
            box.setSelected (sel);
            if (!sel) {
                controller.setProblem ("Don't you like orange?");
            }
            
            result.add (box);
            box.addActionListener (this);
            return result;
        }

        protected Object finish(Map settings) throws WizardException {
            return "Colors finished!";
        }
        
        public void actionPerformed (ActionEvent ae) {
            boolean sel = ((JCheckBox) ae.getSource()).isSelected();
            controller.setProblem (sel ? null : "Only people who like orange can finish");
            settings.put ("likesOrange", sel ? Boolean.TRUE : Boolean.FALSE);
        }        
    }
    

    private static class PracticalitiesPanelProvider extends WizardPanelProvider implements ActionListener {
        private Map settings;
        private WizardController controller;
        
        public PracticalitiesPanelProvider() {
            super ("Practicalities", 
                new String[] { "bepractical" }, 
                new String[] { "Choose to be practical"});
        }
        
        protected JComponent createPanel(WizardController controller, String id, Map settings) {
            this.controller = controller;
            this.settings = settings;
            
            JPanel result = new JPanel();
            result.setLayout (new FlowLayout());
            JCheckBox box = new JCheckBox ("I have no use for Wizards");
            
            boolean sel = Boolean.TRUE.equals (settings.get("dislikesWizards"));
            box.setSelected (sel);
            if (!sel) {
                controller.setProblem ("Practical people don't need wizards");
            }
            
            result.add (box);
            box.addActionListener (this);
            return result;
        }

        protected Object finish(Map settings) throws WizardException {
            return "Practicalities finished!";
        }
        
        public void actionPerformed (ActionEvent ae) {
            boolean sel = ((JCheckBox) ae.getSource()).isSelected();
            controller.setProblem (sel ? null : "Practical people don't need wizards");
            settings.put ("likesOrange", sel ? Boolean.TRUE : Boolean.FALSE);
        }        
    }    
    
}
