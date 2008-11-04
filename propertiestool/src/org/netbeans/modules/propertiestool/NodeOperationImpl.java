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

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAcceptor;
import org.openide.nodes.NodeOperation;
import org.openide.util.Lookup;
import org.openide.util.UserCancelException;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;

/**
 * NodeOperationImpl is registered in the default lookup using the META-INF
 * folder. This implementation assumes that there already is the default
 * implementation that we can delegate to. Our method getDelegate() finds
 * the original implementation and most of the other methods just use
 * the delegate. The only methods changed are the showProperties variants.
 * @author David Strupl
 */
@org.openide.util.lookup.ServiceProvider(service=org.openide.nodes.NodeOperation.class, position=10)
public class NodeOperationImpl extends NodeOperation {
    
    /** Constructor must be public for the lookup. */
    public NodeOperationImpl() {
    }

    /** Tries to open a customization dialog for the specified node.
     * The dialog is
     * modal and the function returns only after
     * customization is finished, if it was possible.
     *
     * @param n the node to customize
     * @return <CODE>true</CODE> if the node had a customizer,
     * <CODE>false</CODE> if not
     * @see Node#hasCustomizer
     * @see Node#getCustomizer
     */
    public boolean customize(Node n) {
        return getDelegate().customize(n);
    }

    /** Explore a node (and its subhierarchy).
     * It will be opened in a new Explorer view, as the root node of that window.
     * @param n the node to explore
     */
    public void explore(Node n) {
        getDelegate().explore(n);
    }

    /** Open a modal Property Sheet on a node.
     * @param n the node to show properties of
     */
    public void showProperties(Node n) {
//        getDelegate().showProperties(new ReadOnlyNode(n));
        TopComponent win = PropertiesToolTopComponent.findInstance();
        win.open();
        win.requestActive();
    }

    /** Open a modal Property Sheet on a set of nodes.
     * @param n the array of nodes to show properties of
     * @see #showProperties(Node)
     */
    public void showProperties(Node[] n) {
//        Node[] nodes = new Node[n.length];
//        for (int i = 0; i < nodes.length; i++) {
//            nodes[i] = new ReadOnlyNode(n[i]);
//        }
//        getDelegate().showProperties(nodes);
        TopComponent win = PropertiesToolTopComponent.findInstance();
        win.open();
        win.requestActive();
    }

    /** Open a modal Explorer on a root node, permitting a node selection to be returned.
     * <p>The acceptor
     * should be asked each time the set of selected nodes changes, whether to accept or
     * reject the current result. This will affect for example the
     * display of the "OK" button.
     *
     * @param title title of the dialog
     * @param rootTitle label at root of dialog. May use <code>&amp;</code> for a {@link javax.swing.JLabel#setDisplayedMnemonic(int) mnemonic}.
     * @param root root node to explore
     * @param acceptor class asked to accept or reject current selection
     * @param top an extra component to be placed on the dialog (may be <code>null</code>)
     * @return an array of selected (and accepted) nodes
     *
     * @exception UserCancelException if the selection is interrupted by the user
     */
    public Node[] select(String title, String rootTitle, Node root, NodeAcceptor acceptor, Component top)
    throws UserCancelException {
        return getDelegate().select(title, rootTitle, root, acceptor, top);
    }
    
    /** 
     * Get the original instance from lookup.
     * @return the original implementor, never returns null, rather
     * @throws IllegalStateException if the implementation was not found
     */
    static NodeOperation getDelegate() {
        NodeOperation no = null;
        Lookup.Result r = Lookup.getDefault().lookup(new Lookup.Template(NodeOperation.class));
        Iterator it = r.allInstances().iterator();
        while (it.hasNext()) {
            NodeOperation n = (NodeOperation)it.next();
            if (! (n instanceof NodeOperationImpl)) {
                no = n;
                break;
            }
        }

        if (no == null) {
            throw new IllegalStateException(
                "To use NodeOperation you should have its implementation around. For example one from openide-explorer.jar" // NOI18N
            );
        }

        return no;
    }
    
    /**
     * We use this filter node from the Properties action. It leaves all
     * aspects of the original node intact except for the properties
     * which are made read only.
     *
     * This class would be private but the tests need to access it 
     * so it is package private.
     */
    class ReadOnlyNode extends FilterNode {
        /** Our property sets are different from the original. */
        private PropertySet[] propertySets;
        private PropertyChangeListener pcl;
        /** The constructor is public mainly for the tests. */
        public ReadOnlyNode(Node original) {
            super(original);
            pcl = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    log("refiring change " + evt);
                    firePropertyChange(evt.getPropertyName(),
                            evt.getOldValue(),
                            evt.getNewValue()
                    );
                }
            };
            original.addPropertyChangeListener(
                WeakListeners.propertyChange(pcl, original));
                    
        }
        /**
         * As we need differenc properties we override this method
         * to return our version of property sets - we walk through
         * the original properties and create our copies.
         */
        public PropertySet[] getPropertySets() {
            log("getPropertySets on " + this);
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
         * The PropertySet remembers the original and creates read only
         * copies of the original properteis.
         */
        private class MyPropertySet extends Node.PropertySet {
            /** Original property set. */
            private Node.PropertySet orig;
            /** Newly created properties */
            Node.Property[] properties;
            /** Use attributes of the original and remember the original. */
            public MyPropertySet(Node.PropertySet orig) {
                super(orig.getName(), orig.getDisplayName(), orig.getShortDescription());
                log("creating MyPropertySet " + orig.getName() + ":" + 
                        orig.getDisplayName() + ":" + orig.getShortDescription());
                this.orig = orig;
            }
            /**
             * Create the read only wrapper properties.
             */
            public Node.Property[] getProperties() {
                log("MyPropertySet.getProperties");
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

            /**
             * Overriden to delegate to the original.
             */
            public String getHtmlDisplayName() {
                return orig.getHtmlDisplayName();
            }
        }

        /**
         * Read-only wrapper property.
         */
        private class MyProperty extends Node.Property {
            /** The original property se delegate to. */
            private Node.Property orig;
            /** Just remember the original. */
            public MyProperty(Node.Property orig) {
                super(orig.getValueType());
                log("Creating MyProperty of type " + orig.getValueType());
                this.orig = orig;
            }
            /** Overriden to delegate to the original property. */
            public boolean canRead() {
                return orig.canRead();
            }
            /** Overriden to delegate to the original property. */
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                return orig.getValue();
            }
            /** We are the read-only copy. */
            public boolean canWrite() {
                return false;
            }
            /** We are the read-only copy. */
            public void setValue(Object val) {
                // do nothing as we are read-only
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
            /** Overriden to delegate to the original property. */
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
            /** Overriden to delegate to the original property. */
            public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
                orig.restoreDefaultValue();
            }
            /** Overriden to delegate to the original property. */
            public boolean isDefaultValue() {
                return orig.isDefaultValue();
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
    
    //
    // Logging:
    //
    private static final Logger log = Logger.getLogger(NodeOperationImpl.class.getName());
    private static boolean LOGABLE = log.isLoggable(Level.FINE);
    
    /**
     * Logs the string only if logging is turned on.
     */
    private static void log(String s) {
        if (LOGABLE) {
            log.fine(s);
        }
    }
}
