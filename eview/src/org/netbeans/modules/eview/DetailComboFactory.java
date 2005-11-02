/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.eview;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.api.eview.ControlFactory;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/**
 * Combobox implementing kind of master/detail functionality. The resulting
 * combo will have several models ready and will use one of them based on
 * the selected item in the master component. 
 *
 * @author David Strupl
 */
public class DetailComboFactory extends ComboBoxControlFactory {
    
    private static ErrorManager log = ErrorManager.getDefault().getInstance(DetailComboFactory.class.getName());
    private static boolean LOGGABLE = log.isLoggable(ErrorManager.INFORMATIONAL);
    
    private String masterID;
    private Map/*<Object, List>*/ myNames;
    private Map/*<Object, Map<String, Object>>*/ myValues;
    private Map/*<Object, ComboBoxModel>*/ myModels;
    
    /** Creates a new instance of DetailComboFactory */
    public DetailComboFactory(FileObject f) {
        super(f);
    }
    
    /**
     * Overriden to read the master control ID + the configurations
     * for individual master values.
     */
    protected void initConfig(FileObject f) {
        Object o1 = f.getAttribute("masterID");
        if (o1 instanceof String) {
            masterID = o1.toString();
        }
        myValues = new HashMap();
        myNames = new HashMap();
        myModels = new HashMap();
        Object o2 = f.getAttribute("configFolder");
        if (o2 instanceof String) {
            String folderName = o2.toString();
            FileObject configFolder = f.getParent().getFileObject(folderName);
            if ((configFolder != null) && (configFolder.isFolder())) {
                FileObject[] children = configFolder.getChildren();
                for (int i = 0; i < children.length; i++) {
                    if (children[i].isFolder()) {
                        Object key = children[i].getAttribute("key");
                        List names = new ArrayList();
                        Map values = new HashMap();
                        scanConfigFolder(children[i], names, values);
                        myValues.put(key, values);
                        myNames.put(key, names);
                    }
                }
            }
        }
    }
    
    public JComponent createComponent() {
        JComboBox result = new DetailComboBox();
        return result;
    }

    private ComboBoxModel getModel(Object key) {
        Object res = myModels.get(key);
        if (res != null) {
            return (ComboBoxModel)res;
        }
        List names = (List)myNames.get(key);
        ComboBoxModel newModel = new DefaultComboBoxModel(names.toArray());
        myModels.put(key, newModel);
        return newModel;
    }
    
    /**
     * Finds the master component and attach a listener to it.
     */
    private JComboBox findMaster(JComboBox me) {
        JComponent parent = (JComponent)me.getParent().getParent();
        if ( ! (parent instanceof EViewPanel)) {
            if (LOGGABLE) log.log("Wrong parent in findMaster " + parent);
            return null;
        }
        EViewPanel panel = (EViewPanel)parent;
        JComboBox res = (JComboBox)panel.getComponent(masterID);
        return res;
    }
    
    /**
     * Finds the master component and attach a listener to it.
     */
    private ControlFactory findMasterFactory(JComboBox me) {
        JComponent parent = (JComponent)me.getParent().getParent();
        if ( ! (parent instanceof EViewPanel)) {
            if (LOGGABLE) log.log("Wrong parent in findMasterFactory " + parent);
            return null;
        }
        EViewPanel panel = (EViewPanel)parent;
        ControlFactory result = panel.getControlFactory(masterID);
        return result;
    }
    
    private class DetailComboBox extends JComboBox {
        public void addNotify() {
            final JComboBox master = findMaster(this);
            master.addActionListener(new ActionListener(){
                public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
                    updateModel(master);
                }
            });
            updateModel(master);
            
            super.addNotify();
        }
        private void updateModel(final JComboBox master) {
            SwingUtilities.invokeLater(new Runnable () {
                public void run() {
                    Object selItem = master.getSelectedItem();
                    ControlFactory masterFactory = findMasterFactory(DetailComboBox.this);
                    Object key = masterFactory.getValue(master);
                    setModel(DetailComboFactory.this.getModel(key));
                    setNamesValues((List)myNames.get(key), (Map)myValues.get(key));
                }
            });
        }
    }
}
