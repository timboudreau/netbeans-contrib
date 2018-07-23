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
 * Software is Nokia. Portions Copyright 1997-2006 Nokia. All Rights Reserved.
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

package org.netbeans.modules.propertiestool;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.netbeans.api.propertiestool.BatchUpdate;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Panel with property sheet and Save and Cancel buttons.
 * @author  David Strupl
 */
public class PropertySheetPanel extends javax.swing.JPanel {
    /** Constants indicating the user decision. */
    static final int USER_INPUT_SAVE_CHANGES = 0;
    /** Constants indicating the user decision. */
    static final int USER_INPUT_DISCARD_CHANGES = 2;

    /** A constant for firing a property change event. */
    public static final String SAVE_ENABLED = "SaveEnabled";
    /** A constant for firing a property change event. */
    public static final String TITLE_CHANGED = "TitleChanged";
    
    private static final Logger log = Logger.getLogger(PropertySheetPanel.class.getName());
    private static boolean LOGABLE = log.isLoggable(Level.FINE);

    private Node[] nodesToSet = null;
    /** the nodes that are displayed in the property sheet */
    private Node[] originalNodes = new Node[0];
    /** Our clones of the displayed nodes */
    private BatchUpdateNode[] editedNodes = new BatchUpdateNode[0];
    /** listener to the node changes, especially their destruction */
    transient private final SheetNodesListener snListener;
    
    private RequestProcessor RP = new RequestProcessor("Sheet panel delayer");
    private RequestProcessor.Task recreateTask = null;
    private RequestProcessor.Task setTask = null;
    
    private boolean saveTemporarilyEnabled;

    /** Creates new form PropertySheetPanel */
    public PropertySheetPanel() {
        initComponents();
        snListener = new SheetNodesListener();
        activateSaveCancelHack();
    }
   
    /**
     * This method attaches a listener to the property sheet's table
     * component to be able to set the save and cancel button enabled
     * right after the user starts editing a property value.
     *
     * It is one big hack since property sheet does not provide an API for this.
     * The hack can be broken with future releases - so if the isntallation
     * does not succeed only a warning will be put into the log if the logging
     * level is verbose enough.
     */
    private void activateSaveCancelHack() {
        JComponent jc = propertySheet;
        if (jc.getComponentCount() == 0) {
            if (LOGABLE) log("activateSaveCancelHack failed (1) with " + jc);
            return;
        }
        jc = (JComponent)jc.getComponent(0); // PSheet
        if (LOGABLE) log("jc = " + jc);
        if (jc.getComponentCount() == 0) {
            if (LOGABLE) log("activateSaveCancelHack failed (2) with " + jc);
            return;
        }
        jc = (JComponent)jc.getComponent(0); // JSplitPane
        if (LOGABLE) log("jc = " + jc);
        if (jc.getComponentCount() == 0) {
            if (LOGABLE) log("activateSaveCancelHack failed (3) with " + jc);
            return;
        }
        jc = (JComponent)jc.getComponent(2); // JScrollPane
        if (LOGABLE) log("jc = " + jc);
        if (jc.getComponentCount() == 0) {
            if (LOGABLE) log("activateSaveCancelHack failed (4) with " + jc);
            return;
        }
        jc = (JComponent)jc.getComponent(0); // JViewPort
        if (LOGABLE) log("jc = " + jc);
        if (jc.getComponentCount() == 0) {
            if (LOGABLE) log("activateSaveCancelHack failed (5) with " + jc);
            return;
        }
        jc = (JComponent)jc.getComponent(0); // SheetTable
        if (LOGABLE) log("jc = " + jc);
        if ( ! (jc instanceof JTable)) {
            if (LOGABLE) log("activateSaveCancelHack failed (6) with " + jc);
            return;
        }
        
        final JTable jt = (JTable)jc;
        jt.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if ( jt.isEditing() &&
                    ( ! isSaveCancelEnabled()) && 
                    e.getKeyCode()==0) {
                    
                    enableSaveCancel();
                    saveTemporarilyEnabled = true;
                }
            }
            public void keyReleased(KeyEvent e) {
                if (saveTemporarilyEnabled && e.getKeyCode()==27) {
                    disableSaveCancel();
                }
            }
        });
        jt.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if ( jt.isEditing() &&
                    ( ! isSaveCancelEnabled())) {
                    
                    enableSaveCancel();
                    saveTemporarilyEnabled = true;
                }
            }
        });
    }
    
    /**
     * Get the ProeprtySheet component displaying our nodes.
     */
    public org.openide.explorer.propertysheet.PropertySheet getPropertySheet() {
        return propertySheet;
    }

    /**
     * The nodes that were set by calling setNodes.
     */
    public Node[] getNodes() {
        return originalNodes;
    }

    /** 
     * Our private nodes that the user edits. This method is here mainly
     * for being used by the tests.
     */
    Node[] getEditedNodes() {
        return editedNodes;
    }

    /**
     * Set the nodes that should be displayed in our panel.
     */
    public void setNodes(final Node[] nodes) {
        nodesToSet = nodes;
        if (setTask == null) {
            setTask = RP.post(new Runnable() {
                public void run() {
                    if (LOGABLE) log("setNodes called on " + PropertySheetPanel.this);
                    Node[] n = nodesToSet;
                    snListener.detach();

                    BatchUpdateNode[] newEditedNodes = new BatchUpdateNode[n.length];
                    for (int i = 0; i < newEditedNodes.length; i++) {
                        newEditedNodes[i] = new BatchUpdateNode(n[i]);
                        if (LOGABLE) log("node["+i+"]="+n[i]);
                    }
                    propertySheet.setNodes(newEditedNodes);
                    if (LOGABLE) log("nodes has been set in the propertySheet");
                    editedNodes = newEditedNodes;
                    originalNodes = n;
                    snListener.attach(n);
                    disableSaveCancel();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            firePropertyChange(TITLE_CHANGED, null, null);
                        }
                    });
                }
            }, 100);
        } else {
            setTask.schedule(100);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        propertySheet = new org.openide.explorer.propertysheet.PropertySheet();
        buttonPanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        setFocusCycleRoot(true);
        propertySheet.setFocusCycleRoot(true);
        add(propertySheet, java.awt.BorderLayout.CENTER);

        saveButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/propertiestool/Bundle").getString("CTL_Save"));
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(saveButton);

        cancelButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/propertiestool/Bundle").getString("CTL_Cancel"));
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(cancelButton);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);

    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        saveButton.requestFocusInWindow();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                save();
                propertySheet.requestFocusInWindow();
            }
        });        
    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cancel();
        RP.post(new Runnable() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        propertySheet.requestFocusInWindow();
                    }
                });
            }
        }, 200);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    /**
     * Save pending changes. Implements the caolescing algorithm - please see
     * BatchUpdate.coalesce(BatchUpdate) for explanation.
     */
    public void save() {
        if (LOGABLE) log("saveButtonActionPerformed");
        List nodes = new ArrayList(Arrays.asList(editedNodes));
        while (!nodes.isEmpty()) {
            BatchUpdateNode n = (BatchUpdateNode)nodes.remove(0); // not empty, right?
            BatchUpdate bu = (BatchUpdate)n.getLookup().lookup(BatchUpdate.class);
            if (bu != null) {
                bu.startSaving();
            }
            n.saveChanges(bu);
            if (bu == null) {
                continue;
            }
            for (Iterator it = nodes.iterator(); it.hasNext();) {
                BatchUpdateNode next = (BatchUpdateNode)it.next();
                BatchUpdate buNext = (BatchUpdate)next.getLookup().lookup(BatchUpdate.class);
                if (bu.coalesce(buNext)) {
                    next.saveChanges(buNext);
                    bu = buNext;
                    it.remove();
                }
            }
            if (bu != null) {
                bu.finishSaving();
            }
        }
        disableSaveCancel();
    }

    /**
     * Cancel pending changes.
     */
    public void cancel() {
        setNodes(originalNodes);
        disableSaveCancel();        
    }

    /**
     * Enables the save and cancel buttons.
     */
    public void enableSaveCancel() {
        saveTemporarilyEnabled = false;
        if (LOGABLE) log("enableSaveCancel");
        saveButton.setEnabled(true);
        cancelButton.setEnabled(true);
        firePropertyChange(SAVE_ENABLED, Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Disable the save and cancel buttons.
     */
    public void disableSaveCancel() {
        saveTemporarilyEnabled = false;
        if (LOGABLE) log("disableSaveCancel");
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        firePropertyChange(SAVE_ENABLED, Boolean.FALSE, Boolean.TRUE);
    }

    /**
     * Reports whether the sava and cancel buttons are enabled now.
     */
    public boolean isSaveCancelEnabled() {
        return saveButton.isEnabled();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private org.openide.explorer.propertysheet.PropertySheet propertySheet;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

    /** 
     * Change listener to changes in selected nodes. Used mainly for
     * dealing with node deletion.
     */
    private class SheetNodesListener extends NodeAdapter implements Runnable {

        /* maps nodes to their listeners (Node, WeakListener) */
        private HashMap listenerMap;

        /* maps nodes to their proeprty change listeners (Node, WeakListener)*/
        private HashMap pListenerMap;

        SheetNodesListener() {}

        /** Fired when the node is deleted.
         * @param ev event describing the node
         */
        public void nodeDestroyed(NodeEvent ev) {
            HashMap curListenerMap = null;
            synchronized (this) {
                curListenerMap = listenerMap;
            }
            if (curListenerMap == null) {
                return;
            }
            Node destroyedNode = ev.getNode();
            NodeListener listener = (NodeListener)curListenerMap.get(destroyedNode);
            PropertyChangeListener pListener = (PropertyChangeListener)pListenerMap.get(destroyedNode);
            // stop to listen to destroyed node
            destroyedNode.removeNodeListener(listener);
            destroyedNode.removePropertyChangeListener(pListener);
            curListenerMap.remove(destroyedNode);
            pListenerMap.remove(destroyedNode);
            originalNodes = (Node[])
                (curListenerMap.keySet().toArray(new Node[curListenerMap.size()]));
            recreateNodes();
        }

        /** Attach ourselves to all the displayed nodes. */
        public void attach (Node[] nodes) {
            HashMap newlistenerMap = new HashMap(nodes.length * 2);
            HashMap newpListenerMap = new HashMap(nodes.length * 2);
            NodeListener curListener = null;
            PropertyChangeListener pListener = null;
            // start to listen to all given nodes and map nodes to
            // their listeners
            for (int i = 0; i < nodes.length; i++) {
                curListener = org.openide.nodes.NodeOp.weakNodeListener (this, nodes[i]);
                pListener = org.openide.util.WeakListeners.propertyChange(this, nodes[i]);
                newlistenerMap.put(nodes[i], curListener);
                newpListenerMap.put(nodes[i], pListener);
                nodes[i].addNodeListener(curListener);
                nodes[i].addPropertyChangeListener(pListener);
            };
            synchronized (this) {
                listenerMap = newlistenerMap;
                pListenerMap = newpListenerMap;
            }
        }

        /** Detach ourselves from the nodes. */
        public void detach () {
            HashMap curListenerMap = null;
            HashMap curpListenerMap = null;
            // destroy the map
            synchronized (this) {
                curListenerMap = listenerMap;
                curpListenerMap = pListenerMap;
                listenerMap = null;
                pListenerMap = null;
            }
            if ((curListenerMap == null) || (curpListenerMap == null)) {
                return;
            }
            // stop to listen to all nodes
            for (Iterator iter = curListenerMap.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry curEntry = (Map.Entry)iter.next();
                ((Node)curEntry.getKey()).removeNodeListener((NodeListener)curEntry.getValue());
            }
            for (Iterator iter = curpListenerMap.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry curEntry = (Map.Entry)iter.next();
                ((Node)curEntry.getKey()).removePropertyChangeListener((PropertyChangeListener)curEntry.getValue());
            }
        }

        /** Invoke the firing from the AWT thread. */
        public void propertyChange(PropertyChangeEvent pce) {
            if (LOGABLE) log("propertyChange " + pce);
            if (Node.PROP_DISPLAY_NAME.equals(pce.getPropertyName())) {
                SwingUtilities.invokeLater(this);
                return;
            }
            if (editedNodes == null) {
                return;
            }
            if (Node.PROP_PROPERTY_SETS.equals(pce.getPropertyName())) {
                recreateNodes();
                return;
            }
            BatchUpdateNode[] en = editedNodes;
            for (int i = 0; i < en.length; i++) {
                if (pce == null) {
                    continue;
                }
                if (en[i] == null) {
                    throw new IllegalStateException("en[" + i + "] == null");
                }
                if (en[i].getOriginal() == null) {
                    throw new IllegalStateException("en[" + i + "].getOriginal() == null, node = " + en[i]);
                }
                if (en[i].getOriginal().equals(pce.getSource())) {
                    if (LOGABLE) log("found the original node as a source " + pce.getSource());
                    en[i].refetchPropertyValue(pce.getPropertyName());
                }
            }
        }

        /**
         * Replanned to the AWT event queue.
         */
        public void run() {
            firePropertyChange(TITLE_CHANGED, null, null);
        }

    } // End of SheetNodesListener.

    /**
     * Clear our cache of nodes and re-create from scratch
     */
    private void recreateNodes() {
        if (recreateTask == null) {
            recreateTask = RP.post(new Runnable() {
                public void run() {
                    if (isSaveCancelEnabled()) {
                        int userInput = askUserAboutSavingChanges();
                        if (userInput == USER_INPUT_SAVE_CHANGES) {
                            save();
                        }
                    }
                    setNodes(originalNodes);
                }
            }, 100);
        } else {
            recreateTask.schedule(100);
        }
    }
    
    /**
     * Display a dialog asking the user.
     * @returns one of the USER_INPUT_XXX constants
     */
    static int askUserAboutSavingChanges() {
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                NbBundle.getBundle(PropertiesToolTopComponent.class).getString("CTL_SaveChangesPropertySetChange"),
                NbBundle.getBundle(PropertiesToolTopComponent.class).getString("CTL_SaveChangesTitlePropertySetChange"),
                NotifyDescriptor.YES_NO_OPTION
        );
        DialogDisplayer dd = DialogDisplayer.getDefault();
        Object ok = dd.notify(nd);
        if (ok == NotifyDescriptor.YES_OPTION) {
            return USER_INPUT_SAVE_CHANGES;
        }
        return USER_INPUT_DISCARD_CHANGES;
    }

    
    
    /** 
     * A constant used as a value of properties that don't have 
     * proper value set.
     */
    private static Object NO_VALUE = new Object();
    
    /**
     * We store the user modifications in this node. The new values are
     * stored in our properties and the save operation propagates them
     * to the original node. Most of the aspects of the node are delegated
     * to the original by being a FilterNode but we have to create a special
     * set of properties.
     */
    class BatchUpdateNode extends FilterNode {
        /** Our property sets. */
        private PropertySet[] propertySets;
        /** Public to be accessible from the tests. */
        public BatchUpdateNode(Node original) {
            super(original);
        }

        /**
         * Making the method public to access from the listener
         */
        public Node getOriginal() {
            return super.getOriginal();
        }

        protected NodeListener createNodeListener() {
            return new NodeAdapter(this) {
                protected void propertyChange(FilterNode fn, PropertyChangeEvent ev) {
                    String n = ev.getPropertyName();
                    if (n.equals(Node.PROP_PROPERTY_SETS)) {
                        return;
                    }
                    super.propertyChange(fn, ev);
                }                
            };
        }
        
        /**
         * If the value was not modified by the user inside this property
         * sheet panel, we can try to reset the value to a value coming
         * from the original node.
         */
        public void refetchPropertyValue(String propertyName) {
            PropertySet[] ps = getPropertySets();
            for (int i = 0; i < ps.length; i++) {
                Property[] p = ps[i].getProperties();
                for (int j = 0; j < p.length; j++) {
                    if (propertyName.equals(p[j].getName())) {
                        if (LOGABLE) log("found property to update " + p[j]);
                        if (p[j] instanceof MyProperty) {
                            if (LOGABLE) log("will call setValueIfUnchanged on " + p[j]);
                            ((MyProperty)p[j]).setValueIfUnchanged();
                        }
                    }
                }
            }
        }
        
        /**
         * We need to create our own propety sets.
         */
        public PropertySet[] getPropertySets() {
            if (LOGABLE) log("getPropertySets on " + this);
            if (propertySets != null) {
                return propertySets;
            }
            PropertySet[] origPropertySets = getOriginal().getPropertySets();
            propertySets = new PropertySet[origPropertySets.length];
            for (int i = 0; i < propertySets.length; i++) {
                propertySets[i] = new MyPropertySet(origPropertySets[i]);
            }
            return propertySets;
        }
        
        /**
         * Save changes tries to propagate all the modified property values
         * to the properties of the original node.
         */
        public void saveChanges(BatchUpdate bu) {
            if (LOGABLE) log("saveChanges called on " + this);
            PropertySet[] origPropertySets = getOriginal().getPropertySets();
            PropertySet[] set = getPropertySets();
            for (int i = 0; i < set.length; i++) {
                Node.Property[] props = set[i].getProperties();
                Node.Property[] orirProps = origPropertySets[i].getProperties();
                for (int j = 0; j < props.length; j++) {
                    try {
                        Object newVal = props[j].getValue();
                        if (LOGABLE) log("trying to setValue " + newVal);
                        if (bu != null) {
                            if (LOGABLE) log("using BatchUpdate");
                            bu.savePropertyValue(orirProps[j], newVal);
                        } else {
                            if (LOGABLE) log("not using BatchUpdate, calling setValue directly");
                            orirProps[j].setValue(props[j].getValue());
                        }
                        if (props[j] instanceof MyProperty) {
                            // it is no longer changed since the user has saved
                            // the changes.
                            ((MyProperty)props[j]).changed = false;
                            if (LOGABLE) log("made the property " + props[j] + " unchanged");
                        }
                    } catch (IllegalArgumentException ex) {
                        log.log(Level.WARNING, "", ex);
                    } catch (IllegalAccessException ex) {
                        log.log(Level.WARNING, "", ex);
                    } catch (InvocationTargetException ex) {
                        log.log(Level.WARNING, "", ex);
                    }
                }
            }
        }
        
        /**
         * We use a property set that has the same attributes as the
         * original property set but creates special properties to hold
         * our edited values.
         */
        private class MyPropertySet extends Node.PropertySet {
            /** The original set we take the information from. */
            private Node.PropertySet orig;
            /** Our special properties holding the modified values. */
            Node.Property[] properties;
            /** Public constructor. */
            public MyPropertySet(Node.PropertySet orig) {
                super(orig.getName(), orig.getDisplayName(), orig.getShortDescription());
                if (LOGABLE) log("creating MyPropertySet " + orig.getName() + ":" + 
                        orig.getDisplayName() + ":" + orig.getShortDescription());
                this.orig = orig;
            }

            /**
             * Use orig to create our properties from the original.
             */
            public Node.Property[] getProperties() {
                if (LOGABLE) log("MyPropertySet.getProperties");
                if (properties != null) {
                    return properties;
                }
                Node.Property[] origProp = orig.getProperties();
                properties = new Node.Property[origProp.length];
                for (int i = 0; i < properties.length; i++) {
                    properties[i] = new MyProperty(origProp[i]);
                }
                return properties;
            }

            /** Delegate to the original. */
            public String getHtmlDisplayName() {
                return orig.getHtmlDisplayName();
            }
        }

        /**
         * Property implementation allowing us to hold a modified
         * property value.
         */
        private class MyProperty extends Node.Property {
            /** Reference to the original property. */
            private Node.Property orig;
            /** 
             * Special value indicating that the value of the property
             * has not been set yet.
             */
            private Object value = NO_VALUE;
            boolean changed = false;
            
            /**
             * The constructor remembers the original property and
             * sets the value to the value from the original property.
             */
            public MyProperty(Node.Property orig) {
                super(orig.getValueType());
                if (LOGABLE) log("Creating MyProperty of type " + orig.getValueType());
                this.orig = orig;
                try {
                    if (LOGABLE) log("setting value to " + orig.getValue());
                    value = orig.getValue();
                } catch (InvocationTargetException ex) {
                    value = ex.getTargetException().getLocalizedMessage();
                } catch (IllegalAccessException ex) {
                    value = ex.getLocalizedMessage();
                }
            }
            /** Overriden to delegate to the original property. */
            public boolean canRead() {
                return orig.canRead();
            }

            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                if (LOGABLE) log("getValue on property " + getName() + " value == " + value);
                if (value == NO_VALUE) {
                    if (LOGABLE) log("hmmm, there was no value, using original value for " + getName());
                    value = orig.getValue();
                }
                return value;
            }

            /** Overriden to delegate to the original property. */
            public boolean canWrite() {
                return orig.canWrite();
            }

            public void setValue(Object val) {
                if (LOGABLE) log("setValue on property " + getName() + " with val == " + val);
                Object old = value;
                value = val;
                firePropertyChange(getName(), old, val);
                enableSaveCancel();
                changed = true;
            }

            public void setValueIfUnchanged() {
                if (LOGABLE) log("setValueIfUnchanged " + getName());
                if (changed) {
                    if (LOGABLE) log("doing nothing at setValueIfUnchanged " + getName());
                    return;
                }
                Object val = null;
                try {
                    if (LOGABLE) log("going to ask original property for value of " + orig.getName());
                    val = orig.getValue();
                } catch (InvocationTargetException ex) {
                    val = ex.getTargetException().getLocalizedMessage();
                } catch (IllegalAccessException ex) {
                    val = ex.getLocalizedMessage();
                }
                if (LOGABLE) log("setValueIfUnchanged will change " + getName() + " to val == " + val);
                Object old = value;
                value = val;
                firePropertyChange(getName(), old, val);
            }
            
            /** Overriden to delegate to the original property. */
            public void setShortDescription(String text) {
                orig.setShortDescription(text);
            }

            /** Overriden to delegate to the original property. */
            public void setName(String name) {
                orig.setName(name);
            }

            /** Overriden to delegate to the original property. */
            public void setDisplayName(String displayName) {
                orig.setDisplayName(displayName);
            }

            /**
             * Overriden to delegate to the original property.
             * Please note that this is not the getValue for value
             * of the property but rather get value of a special attribute.
             */
            public Object getValue(String attributeName) {
                return orig.getValue(attributeName);
            }

            /** Overriden to delegate to the original property. */
            public boolean equals(Object property) {
                return orig.equals(property);
            }

            /** Overriden to delegate to the original property. */
            public void setPreferred(boolean preferred) {
                orig.setPreferred(preferred);
            }

            /** Overriden to delegate to the original property. */
            public void setHidden(boolean hidden) {
                orig.setHidden(hidden);
            }

            /** Overriden to delegate to the original property. */
            public void setExpert(boolean expert) {
                orig.setExpert(expert);
            }

            /** Overriden to delegate to the original property. */
            public boolean isHidden() {
                return orig.isHidden();
            }

            /** Overriden to delegate to the original property. */
            public boolean isPreferred() {
                return orig.isPreferred();
            }

            /** Overriden to delegate to the original property. */
            public String getHtmlDisplayName() {
                return orig.getHtmlDisplayName();
            }

            /** Overriden to delegate to the original property. */
            public PropertyEditor getPropertyEditor() {
                return orig.getPropertyEditor();
            }

            /** Overriden to delegate to the original property. */
            public int hashCode() {
                return orig.hashCode();
            }

            /** 
             * We cannot simply delegate to the original. We must restore
             * the value into our copy, not to the original.
             */
            public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
                Object origValue = null;
                try {
                    origValue = orig.getValue();
                } catch (Exception ex) {
                    // report only in debug mode:
                    if (LOGABLE) log.log(Level.FINE, "", ex);
                    return;
                }
                try {
                    orig.restoreDefaultValue();
                    setValue(orig.getValue());
                } catch (Exception ex) {
                    // report only in debug mode:
                    if (LOGABLE) log.log(Level.FINE, "", ex);
                } finally {
                    orig.setValue(origValue); // return back the value
                }
            }

            /** The check must be done on our copy not on the original value.*/
            public boolean isDefaultValue() {
                boolean res = false;
                Object origValue = null;
                try {
                    origValue = orig.getValue();
                } catch (Exception ex) {
                    // report only in debug mode:
                    if (LOGABLE) log.log(Level.FINE, "", ex);
                    return false;
                }
                try {
                    orig.setValue(getValue());
                    res = orig.isDefaultValue();
                } catch (Exception ex) {
                    // report only in debug mode:
                    if (LOGABLE) log.log(Level.FINE, "", ex);
                } finally {
                    try {
                        orig.setValue(origValue);
                    } catch (Exception ex) {
                        // report only in debug mode:
                        if (LOGABLE) log.log(Level.FINE, "", ex);
                    }
                }
                return res;
            }

            /** Overriden to delegate to the original property. */
            public boolean supportsDefaultValue() {
                return orig.supportsDefaultValue();
            }

            /** Overriden to delegate to the original property. */
            public Enumeration attributeNames() {
                return orig.attributeNames();
            }

            /** Overriden to delegate to the original property. */
            public String getDisplayName() {
                return orig.getDisplayName();
            }

            /** Overriden to delegate to the original property. */
            public String getName() {
                return orig.getName();
            }

            /** Overriden to delegate to the original property. */
            public String getShortDescription() {
                return orig.getShortDescription();
            }

            /** Overriden to delegate to the original property. */
            public boolean isExpert() {
                return orig.isExpert();
            }
        }
    }
    /** Log only if the system property has been specified. */
    private static void log(String s) {
        log.fine(s);
    }
}
