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

package org.netbeans.modules.apisupport.beanbrowser;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Radek Matous
 */
public class PreferencesNode extends AbstractNode {
    private static final int NB_USER = 0;
    private static final int DEFAULT_USER = 1;
    private static final int DEFAULT_SYSTEM = 2;
    
    public PreferencesNode() {
        super(new RootChildren());
        setDisplayName("Preferences");
    }
    
    private static class RootChildren extends Children.Array {
        RootChildren() {
            super();
        }
        
        protected void addNotify() {
            add(new Node[] {
                new PreferencesNodeImpl(PreferencesNode.NB_USER),
                new PreferencesNodeImpl(PreferencesNode.DEFAULT_USER),
                new PreferencesNodeImpl(PreferencesNode.DEFAULT_SYSTEM)
            });
        }
    }
    
    private static class PreferencesNodeImpl extends AbstractNode {
        /** Creates a new instance of PreferencesNode */
        public PreferencesNodeImpl(int type) {
            this(type, "/");
            switch(type) {
                case PreferencesNode.NB_USER:
                    setDisplayName("NetBeans Preferences");
                    break;
                case PreferencesNode.DEFAULT_USER:
                    setDisplayName("User Preferences");
                    break;
                case PreferencesNode.DEFAULT_SYSTEM:
                    setDisplayName("System Preferences");
                    break;
                default:
                    assert false;
            }
        }
        
        private  PreferencesNodeImpl(int type, String absolutePath) {
            super(new PreferencesChildren(type, absolutePath));
            setDisplayName(getPreferences().name());
        }
        
        protected Sheet createSheet() {
            Sheet sh = super.createSheet();
            populatePropertiesSet(sh);
            return sh;
        }
        
        private Preferences getPreferences() {
            return ((PreferencesChildren)getChildren()).getPreferences();
        }
        
        private void populatePropertiesSet(final Sheet sheet) {
            Sheet.Set set = getPropertySet(sheet);
            final String[] keys;
            try {
                keys = getPreferences().keys();
                for (int i = 0; i < keys.length; i++) {
                    set.put(new PreferencesProperty(keys[i]));
                }
            } catch (BackingStoreException ex) {
                Logger.getLogger("org.netbeans.modules.apisupport.beanbrowser.PreferencesNode").//NOI18N
                        log(Level.WARNING, null, ex);
            }
        }
        
        private Sheet.Set getPropertySet(final Sheet sheet) {
            Sheet.Set set = sheet.get(Sheet.PROPERTIES);
            if (set == null) {
                final Sheet.Set set2 = Sheet.createPropertiesSet();
                set = set2;
                sheet.put(set);
                PreferenceChangeListener pL = new PreferenceChangeListener() {
                    public void preferenceChange(PreferenceChangeEvent evt) {
                        if (!evt.getNode().equals(getPreferences())) {
                            return;
                        }
                        Property p = set2.get(evt.getKey());
                        Object newValue = evt.getNewValue();
                        if (p == null) {
                            set2.put(new PreferencesProperty(evt.getKey()));
                        } else if (newValue == null) {
                            set2.remove(evt.getKey());
                        } else {
                            try {
                                if (p.getValue().equals(evt.getNewValue())) return;
                                p.setValue(evt.getNewValue());
                            } catch (IllegalArgumentException ex) {
                                Logger.getLogger("org.netbeans.modules.apisupport.beanbrowser.PreferencesNode").//NOI18N
                                        log(Level.WARNING, null, ex);
                            } catch (InvocationTargetException ex) {
                                Logger.getLogger("org.netbeans.modules.apisupport.beanbrowser.PreferencesNode").//NOI18N
                                        log(Level.WARNING, null, ex);
                            } catch (IllegalAccessException ex) {
                                Logger.getLogger("org.netbeans.modules.apisupport.beanbrowser.PreferencesNode").//NOI18N
                                        log(Level.WARNING, null, ex);                        }
                        }
                    }
                };
                getPreferences().addPreferenceChangeListener(pL);
            }
            return set;
        }
        
        private class PreferencesProperty extends PropertySupport.ReadWrite  {
            PreferencesProperty(String key) {
                super(key, String.class, key, key);
            }
            
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                return getPreferences().get(getName(), "");//NOI18N;
            }
            
            public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException {
                Object oldValue = getValue();
                if (oldValue.equals(val)) return;
                getPreferences().put(getName(), val.toString());//NOI18N
                firePropertyChange(getName(), oldValue,val);
            }
        }
        
        private static class PreferencesChildren extends Children.Keys {
            private final String absolutePath;
            private int type;
            PreferencesChildren(int type, final String absolutePath) {
                super();
                this.type = type;
                this.absolutePath = absolutePath;
            }
            
            protected void addNotify() {
                refreshKeys();
                NodeChangeListener nL = new NodeChangeListener() {
                    public void childAdded(NodeChangeEvent evt) {
                        if (!evt.getParent().equals(getPreferences())) {
                            return;
                        }
                        refreshKeys();
                    }
                    public void childRemoved(NodeChangeEvent evt) {
                        childAdded(evt);
                    }
                };
                getPreferences().addNodeChangeListener(nL);
            }
            
            private void refreshKeys() {
                Preferences parent = getPreferences();
                try {
                    String[] names = parent.childrenNames();
                    List children = new ArrayList();
                    for (int i = 0; i < names.length; i++) {
                        children.add(parent.node(names[i]).absolutePath());
                    }
                    setKeys(children);
                } catch (BackingStoreException ex) {
                    setKeys(new String[0]);
                }
            }
            
            
            protected Node[] createNodes(Object key) {
                assert key != null;
                if (key instanceof String) {
                    return new Node[] {new PreferencesNodeImpl(type,(String)key)};
                } else {
                    throw new IllegalStateException(key.getClass().getName());
                }
            }
            
            protected Preferences getPreferences() {
                Preferences root = null;
                switch(type) {
                    case PreferencesNode.NB_USER:
                        root = NbPreferences.root();
                        break;
                    case PreferencesNode.DEFAULT_USER:
                        root = Preferences.userRoot();
                        break;
                    case PreferencesNode.DEFAULT_SYSTEM:
                        root = Preferences.systemRoot();
                        break;
                    default:
                        assert false;
                }
                return root.node(absolutePath);
            }
        }
    }
}
