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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.SaveCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.explorer.ExplorerUtils;

/**
 * Top component which displays the properties of globally activated node
 * (listens on Utilities.actionsGlobalContext().lookup(Node.class)).
 * @author David Strupl
 */
final class PropertiesToolTopComponent extends TopComponent implements LookupListener {
    /**
     * Name of a property that can be passed in a Node instance. The value
     * of the property must be String and can be an alternative to displayName.
     */
    private static final String PROP_LONGER_DISPLAY_NAME = "longerDisplayName"; // NOI18N
    /** Our singleton instance */
    private static PropertiesToolTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/modules/propertiestool/properties.gif";
    /** The ID for saving */
    static final String PREFERRED_ID = "PropertiesToolTopComponent";
    /** The result that we listen on. */
    private Lookup.Result resultOfActionsGlobalContextLookup;
    /** My lookup content allowing us to add/remove objects */
    private InstanceContent myLookupContent;
    /** My lookup used mainly for supplying the SaveCookie. */
    private Lookup myLookup;
    /** Save cookie reference. */
    private SaveCookieDummyNode mySaveCookie;
    /** cache the title formatters, they are used frequently and are slow to construct */
    private static MessageFormat globalPropertiesFormat = null;
    /** Constants indicating the user decision. */
    private static final int USER_INPUT_SAVE_CHANGES = 0;
    /** Constants indicating the user decision. */
    private static final int USER_INPUT_GO_BACK = 1;
    /** Constants indicating the user decision. */
    private static final int USER_INPUT_DISCARD_CHANGES = 2;
    
    private static final Logger log = Logger.getLogger(PropertiesToolTopComponent.class.getName());
    private static boolean LOGABLE = log.isLoggable(Level.FINE);
    
    /** Logs the given string only if the system property is set for this logger. */
    private static void log(String s) {
        if (LOGABLE) {
            log.fine(s);
        }
    }
    /**
     * The constructor is private because the method findInstance() should
     * be used for all outside this class calls.
     */
    private PropertiesToolTopComponent() {
        mySaveCookie = new SaveCookieDummyNode();
        myLookupContent = new InstanceContent();
        myLookup = new AbstractLookup(myLookupContent);
        associateLookup(myLookup);
        initComponents();
        propertySheetPanel.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent pce) {
                if (PropertySheetPanel.SAVE_ENABLED.equals(pce.getPropertyName())) {
                    updateSaveCookie();
                }
                if (PropertySheetPanel.TITLE_CHANGED.equals(pce.getPropertyName())) {
                    updateTitle();
                }
            }
        });
        updateTitle();
        setIcon(Utilities.loadImage(ICON_PATH, true));
        getAccessibleContext ().setAccessibleName (
            NbBundle.getBundle(PropertiesToolTopComponent.class).getString ("ACSN_PropertiesSheet"));
        getAccessibleContext ().setAccessibleDescription (
            NbBundle.getBundle(PropertiesToolTopComponent.class).getString ("ACSD_PropertiesSheet"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        propertySheetPanel = new org.netbeans.modules.propertiestool.PropertySheetPanel();

        setLayout(new java.awt.BorderLayout());

        add(propertySheetPanel, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.propertiestool.PropertySheetPanel propertySheetPanel;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Needed from the tests.
     */
    PropertySheetPanel getPropertySheetPanel() {
        return propertySheetPanel;
    }
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized PropertiesToolTopComponent getDefault() {
        if (instance == null) {
            instance = new PropertiesToolTopComponent();
        }
        return instance;
    }

    /**
     * Updates the presence of the SaveCookie in our lookup.
     */
    void updateSaveCookie() {
        log("updateSaveCookie");
        if (getLookup().lookup(SaveCookieDummyNode.class) != null) {
            log("updateSaveCookie removing mySaveCookie");
            myLookupContent.remove(mySaveCookie);
        }
        if (propertySheetPanel.isSaveCancelEnabled()) {
            log("updateSaveCookie adding mySaveCookie");
            myLookupContent.add(mySaveCookie);
        }
    }
    
    /**
     * Transfer the focus to the property sheet.
     * @deprecated Without this method pressing F1 will not always fetch the 
     *   correct help ID. I don't know how to fix this without using the
     *   deprecated method.
     */
    public void requestFocus () {
        propertySheetPanel.getPropertySheet().requestFocus();
    }
    
    /**
     * Transfer the focus to the property sheet.
     * @deprecated Without this method pressing F1 will not always fetch the 
     *   correct help ID. I don't know how to fix this without using the
     *   deprecated method.
     */
    public boolean requestFocusInWindow () {
        return propertySheetPanel.getPropertySheet().requestFocusInWindow();
    }
    
    /**
     * Obtain the PropertiesToolTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized PropertiesToolTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            log.info("Cannot find PropertiesTool component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof PropertiesToolTopComponent) {
            return (PropertiesToolTopComponent)win;
        }
        log.info("There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    /**
     * Overriden to be always present. As we alter the serialization the 
     * singleton instance is always displayed on its place.
     */
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    /**
     * Start listenning on Utilities.actionsGlobalContext().
     */
    public void componentOpened() {
        resultOfActionsGlobalContextLookup = Utilities.actionsGlobalContext().lookup(new Lookup.Template(Node.class));
        Collection c = resultOfActionsGlobalContextLookup.allInstances();
        resultOfActionsGlobalContextLookup.addLookupListener(this);
        Node nodes[] = (Node[])c.toArray(new Node[c.size()]);
        propertySheetPanel.setNodes(nodes);
        updateTitle();
    }
    
    /**
     * Finish listenning on Utilities.actionsGlobalContext().
     */
    public void componentClosed() {
        resultOfActionsGlobalContextLookup.removeLookupListener(this);
        resultOfActionsGlobalContextLookup = null; // forget it for now, should be GCed
        propertySheetPanel.setNodes(new Node[0]);
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    // JavaDoc from the inherited method ...
    protected String preferredID() {
        return PREFERRED_ID;
    }

    /**
     * LookupListener method. We listen on Utilities.actionsGlobalContext().
     * Whenever there is a new node selected we want to update our tool.
     */
    public void resultChanged(LookupEvent ev) {
        log("resultChanged " + ev);
        if (resultOfActionsGlobalContextLookup == null) {
            // already closed, do nothing
            return;
        }
        Collection c = resultOfActionsGlobalContextLookup.allInstances();
        Node nodes[] = (Node[])c.toArray(new Node[c.size()]);
        log("nodes == " + nodes);
        if ((nodes != null) && (nodes.length > 0)) {
            log("new nodes length == " + nodes.length);
            if (LOGABLE) {
                for (int i = 0; i < nodes.length; i++) {
                    log("node[" + i + "]="+nodes[i]);
                }
            }
            Node[] oldNodes = propertySheetPanel.getNodes();
            if (isSelectionTheSame(oldNodes, nodes)) {
                return;
            }
            if ((nodes.length == 1) && (nodes[0] == mySaveCookie)) {
                // ignore our own node
                return;
            }
            if (propertySheetPanel.isSaveCancelEnabled()) {
                int userInput = askUserAboutSavingChanges();
                if (userInput == USER_INPUT_SAVE_CHANGES) {
                    propertySheetPanel.save();
                }
                if (userInput == USER_INPUT_GO_BACK) {
                    requestActive();
                    return;
                }
                if (userInput == USER_INPUT_DISCARD_CHANGES) {
                    // ok, just go to the new nodes
                }
            }
            propertySheetPanel.setNodes(nodes);
        }
    }
    
    public boolean canClose() {
        if (!isOpened()) {
            return false;
        }
        if (propertySheetPanel.isSaveCancelEnabled()) {
            int userInput = askUserAboutSavingChanges();
            if (userInput == USER_INPUT_SAVE_CHANGES) {
                propertySheetPanel.save();
            }
            if (userInput == USER_INPUT_GO_BACK) {
                requestActive();
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Compares 2 arrays of nodes using method equals(Object).
     */
    private static boolean isSelectionTheSame(Node[] oldSel, Node[] newSel) {
        if ((oldSel == null) && (newSel == null)) {
            return true;
        }
        if ((oldSel != null) && (newSel == null)) {
            return false;
        }
        if ((oldSel == null) && (newSel != null)) {
            return false;
        }
        if (oldSel.length != newSel.length) {
            return false;
        }
        for (int i = 0; i < oldSel.length; i++) {
            if (!oldSel[i].equals(newSel[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the title according to the displayed node.
     */
    private void updateTitle () {
        if (propertySheetPanel == null) {
            // already closed, do nothing
            return;
        }
        Node nodes[] = propertySheetPanel.getNodes();
        if (nodes == null) {
            return;
        }
        String nodeTitle =  null;

        // Fix a bug #12890, copy the nodes to prevent race condition.
        List copyNodes = new ArrayList(Arrays.asList(nodes));

        Node node = null;
        if(!copyNodes.isEmpty()) {
            node = (Node)copyNodes.get(0);
        }

        if(node == null) {
            nodeTitle = "";  // NOI18N
        } else {
            nodeTitle = node.getDisplayName();
            Object alternativeDisplayName = node.getValue(PROP_LONGER_DISPLAY_NAME);
            if (alternativeDisplayName instanceof String) {
                nodeTitle = (String)alternativeDisplayName;
            }
        }
        Object[] titleParams = new Object[] {
            new Integer(copyNodes.size()),
            nodeTitle
        };
        if (globalPropertiesFormat == null) {
            globalPropertiesFormat = new MessageFormat(NbBundle.getMessage(
                PropertiesToolTopComponent.class, "CTL_FMT_GlobalProperties"
            ));
        }
        setName(globalPropertiesFormat.format(titleParams));
        setToolTipText(getName());
    }
    
    /**
     * Display a dialog asking the user.
     * @returns one of the USER_INPUT_XXX constants
     */
    private static int askUserAboutSavingChanges() {
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                NbBundle.getBundle(PropertiesToolTopComponent.class).getString("CTL_SaveChanges"),
                NbBundle.getBundle(PropertiesToolTopComponent.class).getString("CTL_SaveChangesTitle"),
                NotifyDescriptor.YES_NO_CANCEL_OPTION
        );
        DialogDisplayer dd = DialogDisplayer.getDefault();
        Object ok = dd.notify(nd);
        if (ok == NotifyDescriptor.YES_OPTION) {
            return USER_INPUT_SAVE_CHANGES;
        }
        if (ok == NotifyDescriptor.NO_OPTION) {
            return USER_INPUT_DISCARD_CHANGES;
        }
        return USER_INPUT_GO_BACK;
    }

    /**
     * Help context is taken from the displayed nodes.
     */
    public HelpCtx getHelpCtx () {
        Node nodes[] = propertySheetPanel.getNodes();
        return ExplorerUtils.getHelpCtx(nodes, new HelpCtx(PropertiesToolTopComponent.class));
    }

    /** Our serialization helper class */
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return PropertiesToolTopComponent.getDefault();
        }
    }

    /**
     * This Node is put into our lookup to enable the global save/saveAll
     * actions.
     */
    private class SaveCookieDummyNode extends AbstractNode implements SaveCookie {
        public SaveCookieDummyNode() {
            this(null);
        }
        public SaveCookieDummyNode(InstanceContent ic) {
            super(Children.LEAF, new AbstractLookup(ic = new InstanceContent()));
            ic.add(this);
            setName("SaveCookieDummyNode"); // NOI18N this should not be visible
        }

        public void save() {
            log("save triggered from SaveCookieDummyNode");
            propertySheetPanel.save();
        }
    }
}
