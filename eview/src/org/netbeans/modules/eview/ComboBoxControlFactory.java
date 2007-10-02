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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import org.netbeans.api.eview.ControlFactory;
import org.netbeans.api.registry.Context;
import org.openide.filesystems.FileObject;

/**
 * ControlFactory creating JComboBoxes.
 * Supports additional attributes "initValue" and "verifier".
 * @author David Strupl
 */
public class ComboBoxControlFactory implements ControlFactory {
    
    private static final Logger log = Logger.getLogger(ComboBoxControlFactory.class.getName());
    private static boolean LOGGABLE = log.isLoggable(Level.FINE);

    /** Shared event instance. */
    private static final PropertyChangeEvent pcEvent = new PropertyChangeEvent(ComboBoxControlFactory.class, "selItem", null, null);
    
    /** String value that is put into the combo right after initialization */
    private String initValue;
    
    /** Display names that are displayed as content of the combo */
    private List/*<String>*/ myNames;
    /** Mapping from names to the values that are returned when the users
     * selects something in the combo.
     */
    private Map/*<String, Object>*/ myValues;
    
    /**
     * Creates a new instance of ComboBoxControlFactory 
     */
    public ComboBoxControlFactory(FileObject f) {
        initConfig(f);
    }

    /**
     * Called by the constructor to read the configuration.
     */
    protected void initConfig(FileObject f) {
        Object o1 = f.getAttribute("configFolder");
        if (o1 instanceof String) {
            String folderName = o1.toString();
            FileObject configFolder = f.getParent().getFileObject(folderName);
            if (configFolder != null) {
                myNames = new ArrayList();
                myValues = new HashMap();
                scanConfigFolder(configFolder, myNames, myValues);
            }
        }
    }
    
    protected final void scanConfigFolder(FileObject folder, List names, Map values) {
        // in order to get the order we need Registry API:
        Context con = Context.getDefault().getSubcontext(folder.getPath());
        List orderedNames = con.getOrderedNames();
        for (Iterator it = orderedNames.iterator(); it.hasNext();) {
            String name = (String) it.next();
            if (LOGGABLE) log.fine("scanFolder checking " + name);
            if (name.endsWith("/")) {
                name = name.substring(0, name.length()-1);
            }
            FileObject child = folder.getFileObject(name);
            String []extensions = { "instance", "ser", "setting", "xml", "shadow" };
            int extNum = 0;
            while ((child == null) && (extNum < extensions.length)) {
                child = folder.getFileObject(name, extensions[extNum++]);
            }
            if (child == null) {
                log.fine("child == null: Registry returned an invalid name " + name + " in folder " + folder.getPath());
                continue;
            }
            if (! child.isValid()) {
                log.fine("!child.isValid(): Registry returned an invalid name " + name + " in folder " + folder.getPath());
                continue;
            }
            if (child.isData()) {
                String displayName = child.getName();
                try {
                    displayName = child.getFileSystem ().getStatus ().annotateName(child.getName(), Collections.singleton(child));
                } catch (Exception x) {
                    log.log(Level.WARNING, "display name cannot be annotated " + displayName, x); // NOI18N
                }
                Object value = displayName;
                Object a1 = child.getAttribute("value");
                if (a1 != null) {
                    value = a1;
                }
                names.add(displayName);
                values.put(displayName, value);
            }
        }
    }
    
    protected void setNamesValues(List newNames, Map newValues) {
        myNames = newNames;
        myValues = newValues;
    }
    
    public void addPropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JComboBox) {
            JComboBox jcb = (JComboBox)c;
            ControlListener controlListener = new ControlListener(l);
            jcb.removeActionListener(controlListener);
            jcb.addActionListener(controlListener);
        }
    }

    public JComponent createComponent() {
        JComboBox result = new JComboBox();
        if (myNames != null) {
            result.setModel(new DefaultComboBoxModel(myNames.toArray()));
        }
        return result;
    }

    public Object getValue(JComponent c) {
        if (c instanceof JComboBox) {
            JComboBox jcb = (JComboBox)c;
            Object selItem = jcb.getSelectedItem();
            if ((selItem != null) && (myValues != null)) {
                return myValues.get(selItem);
            }
        }
        return null;
    }
    
    public String convertValueToString(JComponent c, Object value) {
        if (myNames == null) {
            return null;
        }
        for (Iterator it = myNames.iterator(); it.hasNext();) {
            String key = (String)it.next();
            Object val = myValues.get(key);
            if ((val != null) && (val.equals(value))) {
                return key;
            }
        }
        return null;
    }

    public void removePropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JComboBox) {
            JComboBox jcb = (JComboBox)c;
            ControlListener controlListener = new ControlListener(l);
            jcb.removeActionListener(controlListener);
        }
    }

    public void setValue(JComponent c, Object value) {
        if (c instanceof JComboBox) {
            JComboBox jcb = (JComboBox)c;
            if (myNames != null) {
                for (Iterator it = myNames.iterator(); it.hasNext();) {
                    Object name = it.next();
                    if (myValues != null) {
                        Object v = myValues.get(name);
                        if ( (v != null) && (v.equals(value))) {
                            jcb.setSelectedItem(name);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Listener attached to the control to propagate changes to our
     * listeners.
     */
    private class ControlListener implements ActionListener {
        private PropertyChangeListener pcl;
        public ControlListener(PropertyChangeListener pcl){
            this.pcl = pcl;
        }
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            pcl.propertyChange(pcEvent);
        }

        public boolean equals(Object anotherObject) {
            if ( ! ( anotherObject instanceof ControlListener ) ) {
                return false;
            }
            ControlListener theOtherOne = (ControlListener)anotherObject;
            return pcl.equals(theOtherOne.pcl);
        }
        public int hashCode() {
            return pcl.hashCode();
        }
    }
}
