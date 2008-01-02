/*
 * FocusTraversalPolicyEditor.java
 *
 * @author Michal Hapala, Pavel Stehlik
 */
package org.netbeans.modules.a11ychecker.traverse;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import org.netbeans.modules.form.*;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node.Property;
import org.netbeans.modules.form.VisualReplicator;

public class FocusTraversalPolicyEditor extends PropertyEditorSupport implements FormAwareEditor, ExPropertyEditor {

    FormModel myFormModel;
    MyGlassPane myGlassPane;
    PropertyEnv myEnv;
    VisualReplicator myReplicator;
    Vector<MySavingButton> savedBtns;
    String startName;
    String endName;
    RADVisualContainer formContainer;
    JButton checkButton;
    //    JButton dehideButton;

    public FocusTraversalPolicyEditor() {

    }

    /**
     * 
     * @return seznam jmen komponent ktere nemaji urceneho naslednika
     */
    public List<String> checkTabTraversalState() {
        System.out.println("checking...");
        MyTraversalPolicy trav = loadMyTraversalPolicy();
        Vector<MySavingButton> tmp = trav.getSavedBtns();
        List<String> compsWithoutNextComp = new ArrayList<String>();
        for (MySavingButton mySavingButton : tmp) {
            System.out.println("name: " + (mySavingButton.getName() != null ? mySavingButton.getName() : "null") + ", nextname: " + (mySavingButton.getNextName() != null ? mySavingButton.getNextName() : "null"));
            if(mySavingButton.getNextName() == null) {
                compsWithoutNextComp.add(mySavingButton.getName());
            }
        }
        return compsWithoutNextComp;
    }

    public MyTraversalPolicy loadMyTraversalPolicy() {
        MyTraversalPolicy trav = new MyTraversalPolicy();
        trav.setSavedBtns(new Vector<MySavingButton>());
        if (myGlassPane != null) {
            if (myGlassPane.startButton != null) {
                trav.start = ((OverflowLbl) myGlassPane.startButton).mycomp.getName();
            }
            if (myGlassPane.endButton != null) {
                trav.end = ((OverflowLbl) myGlassPane.endButton).mycomp.getName();
            }
            for (OverflowLbl overflowLbl : myGlassPane.vecButtons) {
                MySavingButton s = new MySavingButton();
                s.setName(overflowLbl.mycomp.getName());
                if (overflowLbl.nextcomp != null) {
                    s.setNextName(overflowLbl.nextcomp.getName());
                }
                trav.getSavedBtns().add(s);
            }
        }
        return trav;
    }

    public void saveOk() {
        if (formContainer != null) {
            Property property = formContainer.getPropertyByName("focusCycleRoot");
            try {
                property.setValue(property.getValue());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //    public void HideButtonClickEvent(java.awt.event.MouseEvent evt) {
//        boolean s = myGlassPane.switchVisualization();
//        if (s) {
//            hideButton.setText("Hide visualization");
//        } else {
//            hideButton.setText("Show visualization");
//        }
//    }

    @Override
    public Object getValue() {
        if (formContainer != null) {
            return loadMyTraversalPolicy();
        } else {
            return null;
        }
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof MyTraversalPolicy) {
            MyTraversalPolicy trav = (MyTraversalPolicy) value;
            savedBtns = trav.getSavedBtns();
            startName = trav.start;
            endName = trav.end;
        }
    }

    @Override
    public String getJavaInitializationString() {
        if (formContainer != null) {
            return myGlassPane.generateTraversalClass();
        }
        return "null";
    }

    private void traverseFormPanel(Container container) {
        // projit komponenty a ziskat jen ty ktere mohou mit urceny tabtraversal
        for (Component aComp : container.getComponents()) {
            //pokud je komponenta JPanel rekurzivne opakuj
            if (aComp instanceof JPanel) {
                traverseFormPanel((Container) aComp);
            }
            //prirad komponente jmeno
            String id = myReplicator.getClonedComponentId(aComp);
            RADComponent r = myFormModel.getMetaComponent(id);
            if (r != null) {
                aComp.setName(r.getName());
            }
        }
    }

    @Override
    public Component getCustomEditor() {
        CustomFocusTraversalPolicyEditor focusEditorPanel = new CustomFocusTraversalPolicyEditor();

        if (!(myFormModel.getTopRADComponent() instanceof RADVisualComponent)) {
            return focusEditorPanel;
        }

        // get top
        Object[] arrSel = myEnv.getBeans();

        if (arrSel.length == 1) {
            RADVisualComponent actComp = (RADVisualComponent) ((RADComponentNode) arrSel[0]).getRADComponent();
            formContainer = actComp instanceof RADVisualContainer ? (RADVisualContainer) actComp : null;

            if (formContainer != null) {
                try {
                    myReplicator = new VisualReplicator(false, new ViewConverter[]{new MyConverter()}, null);
                    myReplicator.setTopMetaComponent(actComp);

                    Container formPanel = (Container) myReplicator.createClone();
                    if (formPanel instanceof JRootPane) {
                        FormDesigner designer = FormEditor.getFormDesigner(myFormModel);
                        formPanel.setSize(((Component) designer.getComponent(actComp)).getPreferredSize());
                        //formPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        JRootPane myRootPane = new JRootPane();
                        Container contentPane = myRootPane.getContentPane();
                        contentPane.setLayout(null);
                        contentPane.add(formPanel);

                        for (Component aComp : formPanel.getComponents()) {
                            if (aComp instanceof JLayeredPane) {
                                for (Component aaComp : ((Container) aComp).getComponents()) {
                                    if (aaComp instanceof JPanel) {
                                        traverseFormPanel((Container) aaComp);
                                    }
                                }
                            }
                        }

                        // create glass pane
                        myGlassPane = new MyGlassPane(this, myRootPane.getContentPane(), formPanel);
                        focusEditorPanel.setGlassPane(myGlassPane);
                        myRootPane.setGlassPane(myGlassPane);

                        checkButton = new JButton("Check...");
                        checkButton.addActionListener(new ActionListener() {

                                    public void actionPerformed(ActionEvent e) {
                                        checkTabTraversalState();
                                    }
                                });


                        //Show the window.
                        focusEditorPanel.setPreferredSize(new Dimension(formPanel.getWidth(), formPanel.getHeight() + 50));
                        focusEditorPanel.add(myRootPane, BorderLayout.CENTER);
                        focusEditorPanel.add(checkButton, BorderLayout.LINE_END);
                    } else {
                        focusEditorPanel.add(new JLabel("This custom editor is only for JFrame container."), BorderLayout.CENTER);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return focusEditorPanel;
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    public void setContext(FormModel formModel, FormProperty formProperty) {
        this.myFormModel = formModel;
    }

    public void attachEnv(PropertyEnv env) {
        myEnv = env;
        env.setState(PropertyEnv.STATE_NEEDS_VALIDATION);
        env.addVetoableChangeListener(new VetoableChangeListener() {

                    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                        if (PropertyEnv.PROP_STATE.equals(evt.getPropertyName())) {
                            saveOk();
                        }
                    }
                });
    }

    public void updateFormVersionLevel() {
    // nothing to do ?
    }
}