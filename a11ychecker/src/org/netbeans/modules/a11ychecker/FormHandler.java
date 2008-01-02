/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.modules.a11ychecker.output.ResultPanel;
import org.netbeans.modules.a11ychecker.output.ResultWindowTopComponent;
import org.netbeans.modules.a11ychecker.utils.A11YFormUtils;
import org.netbeans.modules.form.FormDesignValue;
import org.netbeans.modules.form.FormDesigner;
import org.netbeans.modules.form.FormEditor;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.netbeans.modules.form.FormEditorSupport;
import org.netbeans.modules.form.FormModel;
import org.netbeans.modules.form.RADComponent;
import org.openide.nodes.Node.Property;
import org.openide.windows.CloneableTopComponent;

/**
 * This class finds out current form intace and provides handlers
 * @author Max Sauer
 */
public class FormHandler {

    /**
     * The embracing multiview TopComponent (holds the form designer and
     * java editor) - we remeber just one TopComponent (not all clones)
     */
    private CloneableTopComponent multiviewTC;
    private FormEditorSupport fes;
    private static LinkedList<RADComponent> unboundLabels;
    private static final String MNEMONIC_CAT = "Mnemonic";
    private static final String A11Y_NAME_CAT = "Name";
    private static final String A11Y_DESC_CAT = "Description";
    private static final String LABEL_FOR_CAT = "Label for";

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
        JButton a11yResultButton = new JButton(new ImageIcon(getClass().getResource("output/a11yIcon.png")));
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
        URL imageUrl = rp.getClass().getResource("icon/aerror.png");
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
        URL imageUrl = rp.getClass().getResource("icon/bwarning.png");
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
        URL imageUrl = rp.getClass().getResource("icon/cinfo.png");
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
     */
    public synchronized void check() {
        HashMap<String, PossibleDuplicateItem> mnemonicMap = new HashMap<String, PossibleDuplicateItem>();
        HashMap<String, PossibleDuplicateItem> labelForMap = new HashMap<String, PossibleDuplicateItem>();
        HashMap<String, HashMap<String, PossibleDuplicateItem>> menuMnemonics = new HashMap<String, HashMap<String, PossibleDuplicateItem>>();
        ResultWindowTopComponent win = ResultWindowTopComponent.findInstance();
        unboundLabels.clear();
        win.getResultPanel().eraseAllEntries();
        win.setName(java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("WIN_NAME"));
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

            win.setName(java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("WIN_FORM_OPENED_NAME") + model.getName());
            List<RADComponent> list = model.getComponentList();
            Iterator<RADComponent> compIterator = list.iterator();
            while (compIterator.hasNext()) {
                RADComponent curr = compIterator.next();
                if (curr instanceof org.netbeans.modules.form.RADVisualComponent) {
                    Class bc = curr.getBeanClass();
                    //check a11y description
                    Property a11yDesc = curr.getPropertyByName("AccessibleContext.accessibleDescription"); //NOI18N
                    if (getPropertyString(a11yDesc) == null || getPropertyString(a11yDesc).equals("")) {
                        addError(A11Y_DESC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("ACCESSIBLE_DESCRIPTION_NEEDED"), curr.getName());
                    } else {
                        Property ttt = curr.getPropertyByName("toolTipText");
                        if (getPropertyString(a11yDesc).equals(getPropertyString(ttt))) {
                            addInfo(A11Y_DESC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("ACCESSIBLE_DESCRIPTION_COPIED"), curr.getName());
                        }
                    }
                    //check a11y name
                    Property a11yName = curr.getPropertyByName("AccessibleContext.accessibleName"); //NOI18N
                    if (getPropertyString(a11yName) == null || getPropertyString(a11yName).equals("")) {
                        addError(A11Y_NAME_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("ACCESSIBLE_NAME_NEEDED"), curr.getName());
                    } else {
                        Property text = curr.getPropertyByName("text");
                        if (text != null) {
                            //  if it actualy has some text, check whether it is the same as a11y name
                            if (getPropertyString(a11yName).equals(getPropertyString(text))) {
                                addInfo(A11Y_NAME_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("ACCESSIBLE_NAME_COPIED"), curr.getName());
                            }
                        }
                    }


                    //check label for
                    if (bc.equals(JLabel.class)) {
                        //only if this comp is a JLabel
                        Property labelFor = curr.getPropertyByName("labelFor"); //NOI18N
                        try {
                            if (labelFor.getValue() == null) {
                                addError(LABEL_FOR_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("LABELFOR_NEEDED"), curr.getName());
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
                                        addWarning(LABEL_FOR_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("LABEL_COLLIDING_1") + pdi.getComp().getName() + java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("LABEL_COLLIDING_2") + fdv.getDescription(), curr.getName());
                                        if (pdi.getOccurenceCount() == 2) {
                                            addWarning(LABEL_FOR_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("LABEL_COLLIDING_1") + curr.getName() + java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("LABEL_COLLIDING_2") + fdv.getDescription(), pdi.getComp().getName());
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
                        mnemonic = curr.getPropertyByName("displayedMnemonic");
                    } else {
                        mnemonic = curr.getPropertyByName("mnemonic");
                    }
                    if (mnemonic != null) {
                        if (getPropertyInteger(mnemonic) == null || getPropertyInteger(mnemonic).equals(0)) {
                            addError(MNEMONIC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_NEEDED"), curr.getName());
                        } else {
                            int code = getPropertyInteger(mnemonic);
                            if (!((code >= 97 && code <= 122) || (code >= 65 && code <= 90))) {
                                addWarning(MNEMONIC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_SUSPICIOUS"), curr.getName());
                            }
                            Property text = curr.getPropertyByName("text");
                            String t = getPropertyString(text);
                            String s = "" + (char) code;
                            s = s.toLowerCase();
                            //check whether component has some text at all
                            if (t == null || t.equals("")) {
                                //                                TODO pridat mozna novou kategorii text a ten pak umoznit nastavit v dialogu?
                                addWarning(MNEMONIC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_FOR_NO_TEXT"), curr.getName());
//                                continue; //takhle to odhali i duplikaty mnemonik
                            } else {
                                if (!t.toLowerCase().contains(s) || s.equals("")) {
                                    addWarning(MNEMONIC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_NOT_PRESENT_1") + s + java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_NOT_PRESENT_2"), curr.getName());
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
                                        //                                        addWarning(MNEMONIC_CAT, "Mnemonic '" + s + "' collides with mnemonic of " + pdi.getComp().getName() + " in " + parentName, curr.getName());
                                        addWarning(MNEMONIC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_COLLISION_1") + s + java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_COLLISION_2") + parentName, curr.getName());
                                        if (pdi.getOccurenceCount() == 2) {
                                            //                                            addWarning(MNEMONIC_CAT, "Mnemonic '" + s + "' collides with mnemonic of " + curr.getName() + " in " + parentName, pdi.getComp().getName());
                                            addWarning(MNEMONIC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_COLLISION_1") + s + java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_COLLISION_2") + parentName, pdi.getComp().getName());
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
                                    //                                    addWarning(MNEMONIC_CAT, "Mnemonic '" + s + "' collides with mnemonic of " + pdi.getComp().getName() + " in " + parentName, curr.getName());
                                    addWarning(MNEMONIC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_MENU_COLLISION_1") + s + java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_MENU_COLLISION_2") + parentName, curr.getName());
                                    if (pdi.getOccurenceCount() == 2) {
                                        //                                        addWarning(MNEMONIC_CAT, "Mnemonic '" + s + "' collides with mnemonic of " + curr.getName() + " in " + parentName, pdi.getComp().getName());
                                        addWarning(MNEMONIC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_MENU_COLLISION_1") + s + java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_MENU_COLLISION_2") + parentName, pdi.getComp().getName());
                                    }
                                } else {
                                    menuMnemonics.get(parentName).put(s, new PossibleDuplicateItem(curr));
                                }
                                continue;
                            }
                            if (mnemonicMap.containsKey(s)) {
                                PossibleDuplicateItem pdi = mnemonicMap.get(s);
                                pdi.incrementOccurenceCount();
                                //                                addWarning(MNEMONIC_CAT, "Mnemonic '" + s + "' collides with mnemonic of " + pdi.getComp().getName(), curr.getName());
                                addWarning(MNEMONIC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_COLLISION_1") + s + java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_COLLISION_2"), curr.getName());
                                if (pdi.getOccurenceCount() == 2) {
                                    //                                    addWarning(MNEMONIC_CAT, "Mnemonic '" + s + "' collides with mnemonic of " + curr.getName(), pdi.getComp().getName());
                                    addWarning(MNEMONIC_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_COLLISION_1") + s + java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("MNEMONIC_COLLISION_2"), pdi.getComp().getName());
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
                                addError(LABEL_FOR_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("NO_LABEL_BOUND"), curr.getName());
                            } else {
                                //                            the other as warning only
                                addWarning(LABEL_FOR_CAT, java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("NO_LABEL_BOUND"), curr.getName());
                            }
                        }
                    }
                }
            }
            win.getResultPanel().setSelectedData();
        //win.getResultPanel().setSelectedRow();
        } else {
            ResultWindowTopComponent.findInstance().getResultPanel().eraseAllEntries();
        }
    }

    /**
     *  Decides whether this component should be checked for label pointing at it.
     *  It is omitting all menu related, button related and JSeparator components and
     *  the JFrame, JTabbedPane, JSplitPane components
     *  @return true if it does, false otherwise
     */
    private static boolean isComponentClassToCheck(Class bc) {
        if (!bc.equals(JLabel.class) && !bc.equals(JButton.class) && !bc.equals(JToggleButton.class) && !bc.equals(JCheckBox.class) && !bc.equals(JRadioButton.class) && !bc.equals(JFrame.class) && !bc.equals(JSeparator.class) && !bc.equals(JSplitPane.class) && !bc.equals(JTabbedPane.class) && !bc.equals(JMenuBar.class) && !bc.equals(JMenu.class) && !bc.equals(JMenuItem.class) && !bc.equals(JCheckBoxMenuItem.class) && !bc.equals(JPopupMenu.class) && !bc.equals(JRadioButtonMenuItem.class)) {
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
//            FIXME fce blbne - vraci null i kdyz je treba text jbuttonu nastavenej, opraveno
//            PropertyEditor prop = ((FormProperty) property).getCurrentEditor();
                if (property.getValueType() == String.class && property.getValue() != null) {
//                String s = prop.getAsText();
                    Object value = property.getValue();

                    if (value instanceof FormDesignValue) {
                        value = ((FormDesignValue) value).getDesignValue();
                    }
                    return (String) value;
                }
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
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
                    return (Integer) property.getValue();
                } catch (InvocationTargetException ex) {
                //ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                //ex.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * setup an form editor toolbar button
     */
    private void initButton(AbstractButton button) {
        if (!("Windows".equals(UIManager.getLookAndFeel().getID()) && (button instanceof JToggleButton))) {
            button.setBorderPainted(false);
        }
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
    }
}
