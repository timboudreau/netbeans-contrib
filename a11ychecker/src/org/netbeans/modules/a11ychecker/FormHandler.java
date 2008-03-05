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
package org.netbeans.modules.a11ychecker;

import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.modules.a11ychecker.output.ResultPanel;
import org.netbeans.modules.a11ychecker.output.ResultWindowTopComponent;
import org.netbeans.modules.a11ychecker.traverse.MyTraversalPolicy;
import org.netbeans.modules.a11ychecker.utils.A11YFormUtils;
import org.netbeans.modules.form.FormDesignValue;
import org.netbeans.modules.form.FormDesigner;
import org.netbeans.modules.form.FormEditor;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.netbeans.modules.form.FormEditorSupport;
import org.netbeans.modules.form.FormModel;
import org.netbeans.modules.form.RADComponent;
import org.netbeans.modules.form.RADConnectionPropertyEditor.RADConnectionDesignValue;
import org.openide.nodes.Node.Property;
import org.openide.util.NbBundle;
import org.openide.windows.CloneableTopComponent;

/**
 * This class finds out current form intace and provides handlers
 * @author Max Sauer
 * @author Martin Novak
 */
public class FormHandler {

    /**
     * The embracing multiview TopComponent (holds the form designer and
     * java editor) - we remeber just one TopComponent (not all clones)
     */
    private CloneableTopComponent multiviewTC;
    private FormEditorSupport fes;
    private static LinkedList<RADComponent> unboundLabels;
    public static final String MNEMONIC_CAT = "Mnemonic";		// NOI18N
    public static final String A11Y_NAME_CAT = "Name";			// NOI18N
    public static final String A11Y_NAME_PROP = "AccessibleContext.accessibleName";	// NOI18N        
    public static final String A11Y_DESC_CAT = "Description";		// NOI18N
    public static final String A11Y_DESC_PROP = "AccessibleContext.accessibleDescription";	// NOI18N        
    public static final String LABEL_FOR_CAT = "Label for";		// NOI18N
    public static final String LABEL_FOR_PROP = "labelFor";	// NOI18N
    public static final String TAB_TRAV_CAT = "Tab traversal";		// NOI18N
    public static final String TAB_TRAV_PROP = "focusTraversalPolicy";	// NOI18N
    public static final String TOOLTIP_PROP = "toolTipText";	// NOI18N
    public static final String DISP_MNEMONIC_PROP = "displayedMnemonic";	// NOI18N
    public static final String MNEMONIC_PROP = "mnemonic";	// NOI18N
    public static final String TEXT_PROP = "text";	// NOI18N
    public static final String WINDOWS_OS = "windows";	// NOI18N
    public static final String FORM_NAME = "Form";	// NOI18N
    public static final int USER_CODE=666;
    
    public FormHandler(TopComponent tc) {

        //online scanning
        if (MultiViews.findMultiViewHandler(tc) != null) {
            multiviewTC = (CloneableTopComponent) tc;
            //get an FormEditorSupprot instance
            fes = FormEditorSupport.getFormEditor(tc);
            if (unboundLabels == null) {
                unboundLabels = new LinkedList<RADComponent>();
            }
        }
    }

    /**
     * Returns formdesigner for this instance
     */
    public FormDesigner getFormDesigner() {
        if (fes != null) {
            FormEditor formEditor = fes.getFormEditor();
            return formEditor.getFormDesigner(fes.getFormModel());
        } else {
            return null;
        }
    }

    //    /**
    //     * Returns formeditorsuppor for this instance
    //     */
    //    public FormEditorSupport getFormEditorSupport() {
    //        return fes;
    //    }
    /**
     * Returns linked list of unbounded Labels
     */
    public LinkedList<RADComponent> getUnboundLabels() {
        return unboundLabels;
    }

    public void addA11YCheckToFormToolbar() {
        FormEditor formEditor = fes.getFormEditor();
        FormDesigner formDesigner = formEditor.getFormDesigner(fes.getFormModel());
        //	JToolBar frmToolBar = formDesigner.getFormToolBar();
        JButton a11yResultButton = new JButton(new ImageIcon(getClass().getResource("output/a11yIcon.png")));	// NOI18N
        initButton(a11yResultButton);
    //	frmToolBar.add(a11yResultButton);
    }

    public void printListOfComponents() {
        //component list
        FormModel model = fes.getFormModel();
        List<RADComponent> list = model.getComponentList();
        Iterator<RADComponent> compIterator = list.iterator();
        while (compIterator.hasNext()) {
            RADComponent curr = compIterator.next();
        }
    }

    /**
     * Add an error entry to the resultpanel
     * @param rule sec. column
     * @param recomm third column
     * @param component fourth coloumn
     */
    private void addError(String rule, String recomm, String component) {
        ResultWindowTopComponent win = ResultWindowTopComponent.findInstance();
        ResultPanel rp = win.getResultPanel();
        URL imageUrl = rp.getClass().getResource("icon/aerror.png");	// NOI18N
        Vector newElem = new Vector();
        newElem.add(new ImageIcon(imageUrl));
        newElem.add(rule);
        newElem.add(recomm);
        newElem.add(component);
        rp.addNewError(newElem);
    }

    /**
     * Add an warning entry to the resultpanel
     * @param rule sec. column
     * @param recomm third column
     * @param component fourth coloumn
     */
    private void addWarning(String rule, String recomm, String component) {
        ResultWindowTopComponent win = ResultWindowTopComponent.findInstance();
        ResultPanel rp = win.getResultPanel();
        URL imageUrl = rp.getClass().getResource("icon/bwarning.png");	// NOI18N
        Vector newElem = new Vector();
        newElem.add(new ImageIcon(imageUrl));
        newElem.add(rule);
        newElem.add(recomm);
        newElem.add(component);
        rp.addNewWarning(newElem);
    }

    /**
     * Add an info entry to the resultpanel
     * @param rule sec. column
     * @param recomm third column
     * @param component fourth coloumn
     */
    private void addInfo(String rule, String recomm, String component) {
        ResultWindowTopComponent win = ResultWindowTopComponent.findInstance();
        ResultPanel rp = win.getResultPanel();
        URL imageUrl = rp.getClass().getResource("icon/cinfo.png");	// NOI18N
        Vector newElem = new Vector();
        newElem.add(new ImageIcon(imageUrl));
        newElem.add(rule);
        newElem.add(recomm);
        newElem.add(component);
        rp.addNewInfo(newElem);
    }

    /**
     * Checks current component state, adds errors to the list
     * Checks:
     * - AccessibleContext.accessibleDescription
     * - AccessibleContext.accessibleName
     * - mnemonic
     * - labelFor
     * - unreachability by tab traversal
     */
    public synchronized void check() {
        HashMap<String, PossibleDuplicateItem> mnemonicMap = new HashMap<String, PossibleDuplicateItem>();
        HashMap<String, PossibleDuplicateItem> labelForMap = new HashMap<String, PossibleDuplicateItem>();
        HashMap<String, HashMap<String, PossibleDuplicateItem>> menuMnemonics = new HashMap<String, HashMap<String, PossibleDuplicateItem>>();
        ResultWindowTopComponent win = ResultWindowTopComponent.findInstance();
        // HACK to prevent exception when no file was opened yet and action invoked (although I don't get why it is null...)
        if (unboundLabels == null) {
            return;
        }
        unboundLabels.clear();
        win.getResultPanel().eraseAllEntries();
        win.setName(NbBundle.getMessage(FormHandler.class, "Win_no_form_name"));    	// NOI18N
        if (FormBroker.getDefault().isMVCTopComponent(multiviewTC)) {
            if (fes == null) {
                return;
            }
            //get all components
            FormModel model = fes.getFormModel();
            if (model == null) {
                return;
            }

            //---setUp autoi18n CheckBox --
            Integer autoMode = A11YFormUtils.getResourceAutoMode(model);
            win.getResultPanel().setAutoI18nCBSelected((autoMode == null || autoMode == 0) ? false : true);
            //---setup i18n checkbock end --

            win.setName(NbBundle.getMessage(FormHandler.class, "Win_form_name", model.getName()));  	// NOI18N
            List<RADComponent> list = model.getComponentList();
            Iterator<RADComponent> compIterator = list.iterator();
            while (compIterator.hasNext()) {
                RADComponent curr = compIterator.next();
                if (curr instanceof org.netbeans.modules.form.RADVisualComponent) {
                    Class bc = curr.getBeanClass();
//                    check traversal
                    if (curr.getName().equals(FORM_NAME)) {
                        try {
                            Property traversalPolicy = curr.getPropertyByName(TAB_TRAV_PROP);
//							if (traversalPolicy == null) {
//								addInfo(TAB_TRAV_CAT, "This won't most probably happen", curr.getName());
//								continue;
//							}
                            Object v = traversalPolicy.getValue();
                            if (!(v instanceof org.netbeans.modules.a11ychecker.traverse.MyTraversalPolicy)) {
                                addInfo(TAB_TRAV_CAT, NbBundle.getMessage(FormHandler.class, "Not_our_tabTraversal"), curr.getName());  	// NOI18N
                            } else {
                                MyTraversalPolicy mtp = (MyTraversalPolicy) v;
                                List<String> s = mtp.checkTabTraversalState();
                                String compList = "";
                                for (int i = 0; i < s.size(); i++) {
                                    compList += " " + s.get(i);
                                    if (i < s.size() - 1) {
                                        compList += ",";
                                    }
                                }
                                if (!s.isEmpty()) {
                                    addError(TAB_TRAV_CAT, NbBundle.getMessage(FormHandler.class, "Unreachable_components", compList), curr.getName());	// NOI18N
                                }


                            }
                        } catch (IllegalAccessException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (InvocationTargetException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                    //check a11y description
                    Property a11yDesc = curr.getPropertyByName(A11Y_DESC_PROP);
                    if (getPropertyString(a11yDesc) == null || getPropertyString(a11yDesc).equals("")) {
                        addError(A11Y_DESC_CAT, NbBundle.getMessage(FormHandler.class, "Accessible_desc_needed"), curr.getName());	// NOI18N
                    } else {
                        Property ttt = curr.getPropertyByName(TOOLTIP_PROP);
                        if (getPropertyString(a11yDesc).equals(getPropertyString(ttt))) {
                            addInfo(A11Y_DESC_CAT, NbBundle.getMessage(FormHandler.class, "Accessible_desc_copied"), curr.getName());	// NOI18N
                        }
                    }
                    //check a11y name
                    Property a11yName = curr.getPropertyByName(A11Y_NAME_PROP);
                    if (getPropertyString(a11yName) == null || getPropertyString(a11yName).equals("")) {
                        addError(A11Y_NAME_CAT, NbBundle.getMessage(FormHandler.class, "Accessible_name_needed"), curr.getName());	// NOI18N
                    } else {
                        Property text = curr.getPropertyByName(TEXT_PROP);
                        if (text != null) {
                            //  if it actualy has some text, check whether it is the same as a11y name
                            if (getPropertyString(a11yName).equals(getPropertyString(text))) {
                                addInfo(A11Y_NAME_CAT, NbBundle.getMessage(FormHandler.class, "Accessible_name_copied"), curr.getName());	// NOI18N
                            }
                        }
                    }


                    //check label for
                    if (bc.equals(JLabel.class)) {
                        //only if this comp is a JLabel
                        Property labelFor = curr.getPropertyByName(LABEL_FOR_PROP); //NOI18N
                        try {
                            if (labelFor.getValue() == null) {
                                addError(LABEL_FOR_CAT, NbBundle.getMessage(FormHandler.class, "LabelFor_needed"), curr.getName());	// NOI18N
                                unboundLabels.add(curr);
                            } else {
                                // write down what components are bound with labels
                                Object v = labelFor.getValue();
                                if (v instanceof FormDesignValue) {
                                    //                                    v = ((FormDesignValue)v).getDesignValue();
                                    FormDesignValue fdv = (FormDesignValue) v;
                                    if (labelForMap.containsKey(fdv.getDescription())) {
                                        PossibleDuplicateItem pdi = labelForMap.get(fdv.getDescription());
                                        pdi.incrementOccurenceCount();
                                        addWarning(LABEL_FOR_CAT, NbBundle.getMessage(FormHandler.class, "Label_colliding", pdi.getComp().getName(), fdv.getDescription()), curr.getName());	// NOI18N
                                        if (pdi.getOccurenceCount() == 2) {
                                            addWarning(LABEL_FOR_CAT, NbBundle.getMessage(FormHandler.class, "Label_colliding", curr.getName(), fdv.getDescription()), pdi.getComp().getName());	// NOI18N
                                        }
                                    } else {
                                        labelForMap.put(fdv.getDescription(), new PossibleDuplicateItem(curr));
                                    }
                                }
                            }
                        } catch (IllegalAccessException ex) {
                            ex.printStackTrace();
                        } catch (InvocationTargetException ex) {
                            ex.printStackTrace();
                        }
                    }

                    //check mnemonics presence and correctness
                    Property mnemonic;
                    if (bc.equals(JLabel.class)) {
                        mnemonic = curr.getPropertyByName(DISP_MNEMONIC_PROP);
                    } else {
                        mnemonic = curr.getPropertyByName(MNEMONIC_PROP);
                    }
                    if (mnemonic != null) {
                        // is this defined by usercode?
                        if (isUserCode(mnemonic)) {
                            addInfo(MNEMONIC_CAT, NbBundle.getMessage(FormHandler.class, "Mnemonic_usercode"), curr.getName());
                        } else if (getPropertyInteger(mnemonic) == null || getPropertyInteger(mnemonic).equals(0)) {
                            addError(MNEMONIC_CAT, NbBundle.getMessage(FormHandler.class, "Mnemonic_needed"), curr.getName());	// NOI18N
                        } else {
                            int code = getPropertyInteger(mnemonic);
                            if (!((code >= 97 && code <= 122) || (code >= 65 && code <= 90))) {
                                addWarning(MNEMONIC_CAT, NbBundle.getMessage(FormHandler.class, "Mnemonic_suspicious"), curr.getName());	// NOI18N
                            }
                            Property text = curr.getPropertyByName(TEXT_PROP);
                            String t = getPropertyString(text);
                            String s = "" + (char) code;
                            s = s.toLowerCase();
                            //check whether component has some text at all
                            if (t == null || t.equals("")) {
                                //                                TODO pridat mozna novou kategorii text a ten pak umoznit nastavit v dialogu?
                                addWarning(MNEMONIC_CAT, NbBundle.getMessage(FormHandler.class, "Mnemonic_for_no_text"), curr.getName());	// NOI18N
//                                continue; //takhle to odhali i duplikaty mnemonik
                            } else {
                                if (!t.toLowerCase().contains(s) || s.equals("")) {
                                    addWarning(MNEMONIC_CAT, NbBundle.getMessage(FormHandler.class, "Mnemonic_not_present", s), curr.getName());	// NOI18N
                                }
                            }


                            if (bc.equals(JMenu.class)) {
                                //                            only top level menus mnemonics to be checked
                                if (!curr.getParentComponent().getBeanClass().equals(JMenuBar.class)) {
                                    String parentName = curr.getParentComponent().getName();
                                    if (!menuMnemonics.containsKey(parentName)) {
                                        menuMnemonics.put(parentName, new HashMap<String, PossibleDuplicateItem>());
                                    }
                                    HashMap<String, PossibleDuplicateItem> parentMenu = menuMnemonics.get(parentName);
                                    if (parentMenu.containsKey(s)) {
                                        PossibleDuplicateItem pdi = parentMenu.get(s);
                                        pdi.incrementOccurenceCount();
                                        addWarning(MNEMONIC_CAT, NbBundle.getMessage(FormHandler.class, "Mnemonic_collision", s, parentName), curr.getName());	// NOI18N
                                        if (pdi.getOccurenceCount() == 2) {
                                            addWarning(MNEMONIC_CAT, NbBundle.getMessage(FormHandler.class, "Mnemonic_collision", parentName, s), curr.getName());	// NOI18N
                                        }
                                    } else {
                                        menuMnemonics.get(parentName).put(s, new PossibleDuplicateItem(curr));
                                    }
                                    continue;
                                }
                            }
                            //                            ignore menu items for the main check, but add them to their menu hashmap
                            if (bc.equals(JMenuItem.class) || bc.equals(JCheckBoxMenuItem.class) || bc.equals(JRadioButtonMenuItem.class)) {
                                // create new hashmap for parent if it doesn't exist
                                String parentName = curr.getParentComponent().getName();
                                if (!menuMnemonics.containsKey(parentName)) {
                                    menuMnemonics.put(parentName, new HashMap<String, PossibleDuplicateItem>());
                                }
                                HashMap<String, PossibleDuplicateItem> parentMenu = menuMnemonics.get(parentName);
                                if (parentMenu.containsKey(s)) {
                                    PossibleDuplicateItem pdi = parentMenu.get(s);
                                    pdi.incrementOccurenceCount();
                                    addWarning(MNEMONIC_CAT, NbBundle.getMessage(FormHandler.class, "Mnemonic_menu_collision", s, parentName), curr.getName());	// NOI18N
                                    if (pdi.getOccurenceCount() == 2) {
                                        addWarning(MNEMONIC_CAT, NbBundle.getMessage(FormHandler.class, "Mnemonic_menu_collision", s, parentName), pdi.getComp().getName());	// NOI18N
                                    }
                                } else {
                                    menuMnemonics.get(parentName).put(s, new PossibleDuplicateItem(curr));
                                }
                                continue;
                            }
                            if (mnemonicMap.containsKey(s)) {
                                PossibleDuplicateItem pdi = mnemonicMap.get(s);
                                pdi.incrementOccurenceCount();
                                addWarning(MNEMONIC_CAT, NbBundle.getMessage(FormHandler.class, "Mnemonic_collision", s), curr.getName());	// NOI18N
                                if (pdi.getOccurenceCount() == 2) {
                                    addWarning(MNEMONIC_CAT, NbBundle.getMessage(FormHandler.class, "Mnemonic_collision", s), pdi.getComp().getName());	// NOI18N
                                }
                            } else {
                                mnemonicMap.put(s, new PossibleDuplicateItem(curr));
                            }
                        }
                    }
                }
            }

            //  go through components again because of label for
            list = model.getComponentList();
            compIterator = list.iterator();
            while (compIterator.hasNext()) {
                RADComponent curr = compIterator.next();
                if (curr instanceof org.netbeans.modules.form.RADVisualComponent) {
                    Class bc = curr.getBeanClass();
                    if (isComponentClassToCheck(bc)) {
                        if (!labelForMap.containsKey(curr.getName())) {
                            if (bc.equals(JTextField.class) || bc.equals(JTextArea.class) || bc.equals(JFormattedTextField.class) || bc.equals(JPasswordField.class) || bc.equals(JTextPane.class) || bc.equals(JEditorPane.class)) {
                                //                                these should be listed as error
                                addError(LABEL_FOR_CAT, NbBundle.getMessage(FormHandler.class, "No_label_bound"), curr.getName());	// NOI18N
                            } else {
                                //                            the other as warning only
                                addError(LABEL_FOR_CAT, NbBundle.getMessage(FormHandler.class, "No_label_bound"), curr.getName());	// NOI18N
                            }
                        }
                    }
                }
            }
            win.getResultPanel().showSelectedData();
        //win.getResultPanel().setSelectedRow();
        } else {
            ResultWindowTopComponent.findInstance().getResultPanel().eraseAllEntries();
        }
    }

    /**
     *  Decides whether this component should be checked for label pointing at it.
     *  It is omitting all menu related, button related and JSeparator components and
     *  the JFrame, JTabbedPane, JSplitPane, JForm, JPanel components
     *  @return true if it does, false otherwise
     */
    private static boolean isComponentClassToCheck(Class bc) {
        if (!bc.equals(JLabel.class) && !bc.equals(JButton.class) && !bc.equals(JToggleButton.class) && !bc.equals(JCheckBox.class) && !bc.equals(JRadioButton.class) && !bc.equals(JFrame.class) && !bc.equals(JSeparator.class) && !bc.equals(JSplitPane.class) && !bc.equals(JTabbedPane.class) && !bc.equals(JMenuBar.class) && !bc.equals(JMenu.class) && !bc.equals(JMenuItem.class) && !bc.equals(JCheckBoxMenuItem.class) && !bc.equals(JPopupMenu.class) && !bc.equals(JRadioButtonMenuItem.class) && !bc.equals(JFrame.class) && !bc.equals(JPanel.class) && !bc.equals(JScrollPane.class) && !bc.equals(JTable.class)) {
            return true;
        }
        return false;
    }

    /**
     * Gets value of bean property as String.
     */
    public static String getPropertyString(Property property) {
        if (property != null) {
            try {
                if (property.getValueType() == String.class && property.getValue() != null) {
                    Object value = property.getValue();

                    if (value instanceof RADConnectionDesignValue) {
                        value = ((RADConnectionDesignValue) value).getDesignValue();
                    }
                    
                    if (value instanceof FormDesignValue) {
                        value = ((FormDesignValue) value).getDesignValue();
                    }
                    
                    return (String) value;
                }
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ClassCastException cce) {
                //not much to do
                //TODO: detect why this happens
                
            }
        }
        return null;
    }

    /**
     * Returns given property Integer Value
     */
    public static Integer getPropertyInteger(Property property) {
        if (property != null) {
            if (property.getValueType() == int.class) {
                try {
                    Object value = property.getValue();
                    
                    if (value instanceof FormDesignValue) {
                        value = ((FormDesignValue) value).getDesignValue();
                    }
                    
                    return (Integer) value;
                } catch (InvocationTargetException ex) {
                    //ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    //ex.printStackTrace();
                } catch (ClassCastException cce) {
                    //TODO: not much to do -- we must detect when this happens
                }
            }
        }
        return null;
    }
    
    /**
     * @param property property tested for usecode presence
     * @return true if Property contains userCode
     */
    public static boolean isUserCode(Property property) {
        if (property != null) {
            try {
                Object value = property.getValue();

                if (value instanceof RADConnectionDesignValue) {
                    String p = ((RADConnectionDesignValue) value).getCode();
                    if (p != null && !p.equals("")) {
                        return true;
                    }
                }
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        } 
        return false;
    }
    
    /**
     * returns FormEditorSupport
     * @return FormEditorSupport
     */
    public FormEditorSupport getFes() {
        return fes;
    }

    /**
     * setup an form editor toolbar button
     */
    private void initButton(AbstractButton button) {
        if (!(WINDOWS_OS.equals(UIManager.getLookAndFeel().getID()) && (button instanceof JToggleButton))) {	// NOI18N
            button.setBorderPainted(false);
        }
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
    }
}

