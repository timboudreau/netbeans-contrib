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
 *
 * Contributor(s): Jesse Glick, Michael Ruflin
 */

package org.netbeans.modules.sysprops;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import org.openide.actions.DeleteAction;
import org.openide.actions.NewAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

/** A Node for a SystemProperty and/or a Parent-Node for SystemProperties.
 *
 * @author Jesse Glick
 * @author Michael Ruflin
 */
public class PropertyNode extends AbstractNode {
    
    /** Name of this Property; null for the root node. */
    protected final String property;
    /** Current value of the property, or null if unset. */
    protected String value;
    /** List of all child properties (may be empty). */
    protected List/*<String>*/ kids;
    /** Listener to change in properties. */
    private ChangeListener listener;
    /** Current property sheet. */
    private Sheet sheet;
    
    /**
     * Creates a new PropertyNode.
     * @param prop the name of the property (null for root node)
     * @param kids list of properties starting with this prefix, not including this one
     */
    public PropertyNode(String prop, List kids) {
        super(kids.isEmpty() ?
            Children.LEAF :
            new PropertyChildren(prop));
        property = prop;
        this.kids = kids;
        
        if (property != null) {
            // Set FeatureDescriptor stuff:
            super.setName(property);
            setDisplayName(shorten(property));
            value = System.getProperty(property);
        } else {
            value = null;
        }
        updateShortDescription();
        
        updateIcon();
        listener = new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
                fireAllChanges();
            }
        };
        PropertiesNotifier.getDefault().addChangeListener
                (WeakListeners.change(listener, PropertiesNotifier.getDefault()));
    }
    
    /** Ensure that the tool tip for the node is correct. */
    private void updateShortDescription() {
        if (value != null) {
            setShortDescription(NbBundle.getMessage(PropertyNode.class, "HINT_property_name_and_value", getDisplayName(), value));
        } else {
            setShortDescription(getDisplayName());
        }
    }
    
    /** Shorten a property name to its last component.
     * @param property the full name
     * @return the shorter version
     */
    private static String shorten(String property) {
        int p = property.lastIndexOf('.');
        if (p > -1) {
            return property.substring(p + 1);
        } else {
            return property;
        }
    }
    
    /** Refresh all display aspects of this node. */
    private void fireAllChanges() {
        value = (property == null ? null : System.getProperty(property));
        kids = (property == null ?
            SystemPropertiesNode.listAllProperties() :
            PropertyChildren.findSubProperties(property));
        updateIcon();
        updateSheet();
        updateShortDescription();
        // I.e. changes in the values of node properties, not the node itself:
        firePropertyChange(null, null, null);
    }
    
    /** Refresh this node's icon. */
    private void updateIcon() {
        if (property == null) {
            setIconBaseWithExtension("org/netbeans/modules/sysprops/resources/propertiesRoot.gif");
        } else {
            if (!kids.isEmpty()) {
                if (value != null) {
                    setIconBaseWithExtension("org/netbeans/modules/sysprops/resources/propertyFolder.gif");
                } else {
                    setIconBaseWithExtension("org/netbeans/modules/sysprops/resources/folder.gif");
                }
            } else {
                setIconBaseWithExtension("org/netbeans/modules/sysprops/resources/property.gif");
            }
        }
    }
    
    /**
     * Returns a new NewType-Array. Only a NewType for a new SystemProperty is
     * returned.
     * @return one new type
     */
    public NewType[] getNewTypes() {
        return new NewType[] { new SystemPropertyNewType(property) };
    }
    
    /**
     * Returns an Array of Actions allowed by this Node.
     * @return a list of standard actions
     */
    public Action[] getActions(boolean context) {
        return new Action[] {
            SystemAction.get(RenameAction.class),
            SystemAction.get(DeleteAction.class),
            null,
            SystemAction.get(NewAction.class),
            null,
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class),
        };
    }
    
    public Action getPreferredAction() {
        return SystemAction.get(PropertiesAction.class);
    }
    
    /**
     * Clones this Node.
     * @return a similar one
     */
    public Node cloneNode() {
        return new PropertyNode(property, kids);
    }
    
    /**
     * Returns a Sheet used to change this Property.
     * @return the property sheet
     */
    protected Sheet createSheet() {
        sheet = super.createSheet();
        updateSheet();
        
        return sheet;
    }
    
    /** A property for a system property and its value. */
    private static class ValueProp extends PropertySupport.ReadWrite {
        /** The property name. */
        private String property;
        /** Make a property.
         * @param property the property name to use
         */
        public ValueProp(String property) {
            super(property, String.class,
                    /* [PENDING] could be localized */ property,
                    NbBundle.getMessage(PropertyNode.class, "HINT_value"));
            this.property = property;
        }
        /** Returns the Value of the system property.
         * @return the value
         */
        public Object getValue() {
            return System.getProperty(property);
        }
        /** Sets the Value of the PropertySupport.
         * @param nue the new value to use
         */
        public void setValue(Object nue) {
            System.setProperty(property, (String) nue);
            PropertiesNotifier.getDefault().changed();
        }
    }
    
    /**
     * Updates the property sheet.
     * Adds a Name property for real properties.
     * Also adds the system property with its value for this
     * property and all subproperties.
     */
    public void updateSheet() {
        if (sheet == null) return;
        // [PENDING] should avoid deleting and recreating properties
        // unless it actually has to...
        Sheet.Set props = Sheet.createPropertiesSet();
        sheet.put(props);
        if (value != null) {
            props.put(new PropertySupport.Name(this));
            props.put(new ValueProp(property));
        }
        Iterator it = kids.iterator();
        while (it.hasNext()) {
            props.put(new ValueProp((String) it.next()));
        }
    }
    
    /** Can someone copy it?
     * @return yes
     */
    public boolean canCopy() {
        return true;
    }
    
    /** Can someone cut it?
     * @return no
     */
    public boolean canCut() {
        return false;
    }
    
    /**
     * Can someone rename it?
     * @return yes, if it is a real property and not a system built-in
     */
    public boolean canRename() {
        if (value != null) {
            return DeleteChecker.isDeletable(property);
        } else {
            return false;
        }
    }
    
    /**
     * Sets a new Name for this Property.
     * @param nue the new name
     */
    public void setName(String nue) {
        String old = getName();
        if (old.equals(nue)) {
            return;
        }
        Properties p = System.getProperties();
        String value = (String) p.remove(property);
        if (value != null) {
            p.setProperty(nue, value);
        }
        System.setProperties(p);
        PropertiesNotifier.getDefault().changed();
        // this node will be removed and a new one added with the new name
    }
    
    /**
     * Can someone delete the node?
     * @return yes if it is really a property and not a system built-in
     */
    public boolean canDestroy() {
        // [PENDING] better to permit the whole subtree to be deleted if
        // all are deletable
        if (value != null) {
            return DeleteChecker.isDeletable(property);
        } else {
            return false;
        }
    }
    
    /**
     * Delete the node.
     * @throws IOException actually does not
     */
    public void destroy() throws IOException {
        // [PENDING] destroy also subproperties
        Properties p = System.getProperties();
        p.remove(property);
        System.setProperties(p);
        PropertiesNotifier.getDefault().changed();
    }
}
