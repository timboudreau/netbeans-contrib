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
 * Contributor(s): Tim Boudreau
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.pojoeditors.api;

import java.awt.EventQueue;
import java.beans.PropertyEditor;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.dynactions.ActionFactory;
import org.netbeans.api.objectloader.ObjectLoader;
import org.netbeans.modules.dynactions.nodes.DynamicActionsDataNode;
import org.netbeans.modules.dynactions.nodes.PropertiesFactory;
import org.netbeans.modules.dynactions.nodes.PropertiesFactory.InfoProvider;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;

/**
 * DataNode subclass for use with PojoDataObject, supporting pluggable actions.
 * Allows dynamic action registration against the node's lookup.
 * <p/>
 * A PojoDataNode represents one Java object which is stored by the data object.
 * Actions may be registered against the contents of its lookup in the system
 * fileystem.
 * <p/>
 * By providing an array of property names and overriding a few methods, you
 * can also have automatic reflection-based property lookup on the pojo,
 * with proper handling of the not-yet-loaded state.
 *
 * @author Tim Boudreau
 */
public class PojoDataNode<T extends Serializable> extends DynamicActionsDataNode {
    protected PojoDataNode (PojoDataObject<T> dob, Children kids, Lookup lkp, String actionContext) {
        super (dob, kids, lkp, actionContext);
    }
    
    protected PojoDataNode (PojoDataObject<T> dob, Children kids, String actionContext) {
        super (dob, kids, actionContext);
    }
    
    protected PojoDataNode (PojoDataObject<T> dob, Children kids, Lookup lkp, ActionFactory factory) {
        super (dob, kids, lkp, factory);
    }
    
    /**
     * Determines whether the superclass actions should be included in the list
     * of acions for this node.
     * 
     * @return
     */
    protected boolean includeDefaultActions() {
        return true;
    }
    
    @Override
    public final Action[] getActions (boolean popup) {
        Action[] base = includeDefaultActions() ? super.getActions(popup) : new Action[0];
        List <Action> actions = null;
        EditorFactory f = getLookup().lookup (EditorFactory.class);
        if (f != null) {
            actions = f.getOpenActions();
        } else {
            actions = new LinkedList<Action>();
        }
        actions.addAll(Arrays.asList(base));
        onGetActions (actions);
        Action[] result = new Action[actions.size()];
        result = actions.toArray(result);
        return result;
    }
    
    /**
     * Called when the pojo of the owning DataObject is unloaded because 
     * modifications were reverted.  Typical implementation refreshes the children
     * of this node.  Default implementation does nothing.
     */
    protected void hintChildrenChanged() {
        //do nothing
    }

    /**
     * Override to attach weak listeners to the pojo.  Default implementation
     * does nothing.
     * @param pojo The pojo
     */
    protected void onLoad (T pojo) {
        //do nothing
    }
    
    final void loaded (T pojo) {
        assert EventQueue.isDispatchThread() : "Wrong thread"; //NOI18N
        try {
            onLoad (pojo);
        } finally {
            if (getPropertyNames() != null && sheetCreated) {
                updateSheet(pojo);
            }
        }
    }
    
    @Override
    public Action getPreferredAction() {
        EditorFactory factory = getLookup().lookup (EditorFactory.class);
        if (factory != null) {
            return factory.getDefaultOpenAction();
        }
        return super.getPreferredAction();
    }
    
    /**
     * Add or sort actions to the list to have them appear on the popup.  The list
     * is pre-populated with whatever open actions the DataObject supports.
     * 
     * @param actions A list of actions that may be modified
     */
    protected void onGetActions(List<Action> actions) {
        //do nothing by default
    }
    
    void modificationsDiscarded() {
        sheetCreated = false;
        onModificationsDiscarded();
        updateSheet (null);
    }
    
    /**
     * Called when modifications to the underlying pojo have been discarded
     */
    protected void onModificationsDiscarded() {
        //do nothing by default
    }

    private boolean sheetCreated = false;
    
    /**
     * By default, creates a property sheet with properties matching those
     * names returned by getPropertyNames().
     * 
     * @return
     */
    @Override
    protected Sheet createSheet() {
        Sheet sheet = null;
        String[] s = getPropertyNames();
        if (s != null) {
            ObjectLoader ldr = getLookup().lookup (ObjectLoader.class);
            if (ldr != null) {
                T t = (T) ldr.getCachedInstance();
                PropertiesFactory factory = PropertiesFactory.create(ldr.type(), 
                        new IP(), s);
                if (t != null) {
                    if (includeDefaultProperties()) {
                        sheet = super.createSheet();
                        factory.populateSheet(sheet, t);
                    } else {
                        sheet = factory.createSheet(t);
                    }
                } else {
                    if (includeDefaultProperties()) {
                        sheet = super.createSheet();
                        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
                        set.put (factory.createPleaseWaitProperty());
                    } else {
                        sheet = factory.createPleaseWaitSheet();                        
                    }
                    ((PojoDataObject) getDataObject()).requestLoad();
                }
            }
        }
        if (sheet == null) {
            sheet = includeDefaultProperties() ? super.createSheet() : 
                Sheet.createDefault();
        }
        sheetCreated = true;
        return sheet;
    }
    
    private void updateSheet(T pojo) {
        PropertySet[] old = getPropertySets();
        setSheet (createSheet());
        PropertySet[] nue = getPropertySets();
        firePropertySetsChange(old, nue);
    }
    
    /**
     * Get a localized display name for a property.  By default, it tries to
     * create capitalized, non-localized names based on inserting spaces at
     * points of bicapitalization.  Should always be overridden in any application
     * that needs to be localized.
     * 
     * @param propName The name of a property from the pojo
     * @return
     */
    protected String displayNameForProperty(String propName) {
        return mangle (propName);
    }
    
    private static String mangle (String s) {
        StringBuilder sb = new StringBuilder(s.length() + 3);
        boolean lastWasUpperCase = false;
        char[] c = s.toCharArray();
        for (int i=0; i < c.length; i++) {
            if (i == 0) {
                sb.append (Character.toUpperCase(c[i]));
            } else {
                boolean isUpperCase = Character.isUpperCase(c[i]);
                if (isUpperCase != lastWasUpperCase) {
                    if (isUpperCase && !lastWasUpperCase) {
                        sb.append (' ');
                    }
                    sb.append (c[i]);
                } else {
                    sb.append (c[i]);
                }
                lastWasUpperCase = isUpperCase;
            }
        }
        return sb.toString();
    }
    
    /**
     * Determines whether the superclass properties returned by DataNode.createSheet()
     * should be included in the properties on the property sheet.
     * @return True to include the default properties
     */
    protected boolean includeDefaultProperties() {
        return true;
    }
    
    /**
     * Get a list of property names on the Pojo which should automatically get
     * properties on the property sheet via introspection.  If you have not
     * overridden createSheet(), this will be handled automatically.  The
     * default implementation returns null, which turns off auto-generation of
     * properties.
     * 
     * @return An array of property names that match bean properties on the 
     *  pojo (e.g. getFirstName() -&gt; &quot;firstName&quot;
     */
    protected String[] getPropertyNames() {
        return null;
    }
    
    /**
     * Get a localized, human readable description for a property of the pojo
     * 
     * @param propName The name of the property
     * @return A description or null
     */
    protected String descriptionForProperty(String propName) {
        return null;
    }
    
    /**
     * Get a property editor for a particular property on the pojo.
     * @param propName The name of the property
     * @param valueType The type of the value
     * @return A property editor, or null if the default property editor (if any)
     * shoudl be used
     */
    protected PropertyEditor propertyEditorForProperty(String propName, Class valueType) {
        return null;
    }
    
    private class IP implements InfoProvider {

        public String displayNameForProperty(String propName) {
            return PojoDataNode.this.displayNameForProperty(propName);
        }

        public String descriptionForProperty(String propName) {
            return PojoDataNode.this.descriptionForProperty(propName);
        }

        public PropertyEditor propertyEditorForProperty(String property, Class valueType) {
            return PojoDataNode.this.propertyEditorForProperty (property, valueType);
        }
    }
}
