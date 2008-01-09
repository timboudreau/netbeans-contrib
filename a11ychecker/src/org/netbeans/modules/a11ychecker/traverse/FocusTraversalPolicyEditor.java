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
 * FocusTraversalPolicyEditor.java
 *
 * @author Michal Hapala, Pavel Stehlik
 */
package org.netbeans.modules.a11ychecker.traverse;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
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
    RADComponent topComponent;
    //    JButton dehideButton;
    public FocusTraversalPolicyEditor() {

    }

    /**
     * 
     * @return seznam jmen komponent ktere nemaji urceneho naslednika
     */
    public List<String> checkTabTraversalState() {
        MyTraversalPolicy trav = loadMyTraversalPolicy();
        Vector<MySavingButton> tmp = trav.getSavedBtns();
        List<String> compsWithoutNextComp = new ArrayList<String>();
        for (MySavingButton mySavingButton : tmp) {
            if (mySavingButton.getNextName() == null) {
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

    public String generateTraversalClass() {
        if (myFormModel == null || myReplicator == null) {
            return "";
        }

        FocusTraversalPolicyGenerator myGenerator = new FocusTraversalPolicyGenerator(myFormModel, myReplicator);
        String myFocusClass = null;
        if (!myGlassPane.vecButtons.isEmpty()) {
            // get start and end
            if (myGlassPane.startButton == null) {
                myGlassPane.startButton = myGlassPane.vecButtons.get(0);
            }
            if (myGlassPane.endButton == null) {
                myGlassPane.endButton = myGlassPane.getEndButton(myGlassPane.startButton);
            }
            myFocusClass = myGenerator.generate(myGlassPane.startButton, myGlassPane.endButton, myGlassPane.vecButtons);
        }
        return myFocusClass;
    }

    @Override
    public String getJavaInitializationString() {
        if (formContainer != null) {
            return generateTraversalClass();
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

    public void setTopComponent(RADComponent topComponent) {
        this.topComponent = topComponent;
    }

    @Override
    public Component getCustomEditor() {
        CustomFocusTraversalPolicyEditor focusEditorPanel = new CustomFocusTraversalPolicyEditor();

        if (!(myFormModel.getTopRADComponent() instanceof RADVisualComponent)) {
            return focusEditorPanel;
        }

        // get top
        Object[] arrSel = null;
        RADVisualComponent actComp = null;
        if (myEnv != null) {
            arrSel = myEnv.getBeans();
        }
        if (arrSel != null) {
            if (arrSel.length == 1) {
                actComp = (RADVisualComponent) ((RADComponentNode) arrSel[0]).getRADComponent();
            }
        } else {
            actComp = (RADVisualComponent) topComponent;
        }
        if (actComp != null) {
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

                        // create glass pane
                        myGlassPane = new MyGlassPane(this, myRootPane.getContentPane(), formPanel);
                        focusEditorPanel.setGlassPane(myGlassPane);
                        myRootPane.setGlassPane(myGlassPane);

                        for (Component aComp : formPanel.getComponents()) {
                            if (aComp instanceof JLayeredPane) {
                                for (Component aaComp : ((Container) aComp).getComponents()) {
                                    if (aaComp instanceof JPanel) {
                                        traverseFormPanel((Container) aaComp);
                                    }
                                }
                            }
                            if (aComp instanceof JTabbedPane) {
                                myGlassPane.addActiveComponent(aComp);
                            }
                        }

                        //Show the window.
                        focusEditorPanel.setPreferredSize(new Dimension(formPanel.getWidth(), formPanel.getHeight() + 50));
                        focusEditorPanel.add(myRootPane, BorderLayout.CENTER);
                    } else {
                        focusEditorPanel.add(new JLabel(java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("STRING_NOJFRAME")), BorderLayout.CENTER);
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
