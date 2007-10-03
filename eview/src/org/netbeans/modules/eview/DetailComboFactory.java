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
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.eview;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.api.eview.ControlFactory;
import org.openide.filesystems.FileObject;

/**
 * Combobox implementing kind of master/detail functionality. The resulting
 * combo will have several models ready and will use one of them based on
 * the selected item in the master component. 
 *
 * @author David Strupl
 */
public class DetailComboFactory extends ComboBoxControlFactory {
    
    private static final Logger log = Logger.getLogger(DetailComboFactory.class.getName());
    private static boolean LOGABLE = log.isLoggable(Level.FINE);
    
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
            if (LOGABLE) log.fine("Wrong parent in findMaster " + parent);
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
            if (LOGABLE) log.fine("Wrong parent in findMasterFactory " + parent);
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
