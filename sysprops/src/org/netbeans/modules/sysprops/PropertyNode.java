/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Jesse Glick, Michael Ruflin
 */
package org.netbeans.modules.sysprops;

import java.io.IOException;
import java.util.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.NotifyDescriptor;
import org.openide.TopManager;
import org.openide.util.NbBundle;

/** A Node for a SystemProperty and/or a Parent-Node for SystemProperties.
 *
 * @author Jesse Glick
 * @author Michael Ruflin
 */
public class PropertyNode extends AbstractNode {

    /** ResourceBundle used in this class. */
    private static      ResourceBundle  bundle = NbBundle.getBundle (PropertyNode.class);
    
    /** Name of this Property. */
    protected String       property;
    /** Set of all Keys. Maybe used later to show all Subproperties, too. */
    //public Set          keys;
    /** If true, this Node is really a key, if  false it is a folder for subkeys. */
    protected boolean      isKey;
    /** If true, this Node has Children. */
    protected boolean      hasChildren;
    /** Value of this Property. May be null if this isKey is false. */
    protected String       propertyValue;
    /** true if the Node is deletable. */
    protected boolean      isDeletable = false;
    /** Sheet of this Node. */
    protected Sheet        sheet;
    
    /**
     * Creates a new PropertyNode.
     * @param property the name of the property.
     */
    public PropertyNode (String property) {
        super (new PropertyChildren (property));
        this.property = property;
        
        // set the default Action
        setDefaultAction (SystemAction.get (PropertiesAction.class));
        
        // Set FeatureDescriptor stuff:
        super.setName(property);
        int p = property.lastIndexOf('.');
        if (p > -1) {
            setDisplayName (property.substring(property.lastIndexOf('.') + 1));
        } else {
            setDisplayName(property);
        }
        
        // refresh the Data
        refreshData();
    }

    /**
     * Refreshs the data and view (icon and Sheet) of this Node.
     */
    public void refreshData() {
        searchKeys();
        setIcon();
        
        if (isKey) {
            //setShortDescription ("Property " + property + " = " + propertyValue);
            setShortDescription (property + "=" + propertyValue);
            isDeletable = DeleteChecker.isDeletable(property);
        } else {
            //setShortDescription ("This node isn't a Property");
        }
        
        // Update the sheet
        updateSheet();
    }
    
    /**
     * Sets the right IconBase.
     */
    public void setIcon() {
        if (hasChildren) {
            if (isKey) {
                setIconBase ("/org/netbeans/modules/sysprops/resources/propertyFolder");
            } else {
                setIconBase ("/org/netbeans/modules/sysprops/resources/folder");
            }
        } else {
            setIconBase ("/org/netbeans/modules/sysprops/resources/property");
        }
    }
    
    /**
     * Returns a new NewType-Array. Only a NewType for a new SystemProperty is 
     * returned.
     */
    public NewType[] getNewTypes () {
        return new NewType[] { new SystemPropertyNewType(property) };
    }
    
    
    /**
     * Refreshs this Node and its Children.
     */
    public void refresh() {
        refreshData();
        PropertyChildren c = (PropertyChildren) getChildren();
        c.refreshChildren();
        Node[] nodes = c.getNodes();
        for (int x=0; x < nodes.length; x++) {
            PropertyNode node = (PropertyNode) nodes[x];
            node.refresh();
        }
    }
    
    /** Searchs all Keys - and SubKeys of this Property ands sets the variables
     * isKey and hasChildren.
     */
    public void searchKeys() {
        //keys = new TreeSet(); this Set can be used to show all subProperties too
        isKey = false;
        hasChildren = false;
        
        // search all Keys and subkeys.
        Properties p = System.getProperties();
        Enumeration e = p.propertyNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            
            if (name.startsWith(property) && (name.length() > 0)) {
                if (name.equals(property)) {
                    isKey = true;
                    propertyValue = System.getProperty(name, "error");
                    //keys.add(name);
                } else {
                    if (name.startsWith(property + ".")) {
                        hasChildren = true;
                        //keys.add(name);
                    }
                }
            }
        }
    }
    
    
    /**
     * Returns an Array of Actions allowed by this Node.
     */
    protected SystemAction[] createActions () {
        return new SystemAction[] {
               SystemAction.get (RenameAction.class),
               SystemAction.get (DeleteAction.class),
               null,
               SystemAction.get (NewAction.class),
               null,
               SystemAction.get (ToolsAction.class),
               SystemAction.get (PropertiesAction.class),
        };
    }
    

    /**
     * Returns the HelpContext for this Node.
     */ 
    public HelpCtx getHelpCtx () {
        return new HelpCtx ("org.netbeans.modules.sysprops");
    }

    /**
     * Returns the Children of this Node.
     * Uncomment this method, if you need it.
     */
    /*protected PropertyChildren getPropertyChildren () {
        return (PropertyChildren) getChildren ();
    }*/

    /**
     * Clones this Node.
     */
    public Node cloneNode () {
        return new PropertyNode (property);
    }
  
    /**
     * Returns a Sheet used to change this Property.
     */
    protected Sheet createSheet () {
        sheet = super.createSheet ();
        updateSheet();
        
        return sheet;
    }
    
    /**
     * Updates the Sheet.
     */
    public void updateSheet() {
        if (sheet == null) return;
         
        Sheet.Set props = sheet.get (Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet ();
            sheet.put (props);
        }
        props.put (new PropertySupport.Name (this));
        
        // Only add the key, if the PropertyNode is a key.
        if (isKey) {
            class ValueProp extends PropertySupport.ReadWrite {
                /** Constructor */
                public ValueProp () {
                    super ("value", String.class,
                           bundle.getString ("PROP_value"), bundle.getString ("HINT_value"));
                }
                /** Returns the Value of the PropertySupport. */
                public Object getValue () {
                    return System.getProperty (property);
                }
                /** Sets the Value of the PropertySupport. */
                public void setValue (Object nue) {
                    System.setProperty (property, (String) nue);
                    PropertiesNotifier.changed ();
                }
                /** Returns true, if the PropertySupport is writable. */
                public boolean canWrite() {
                    /*if(isDeletable) {
                        return true;
                    } else {
                        return false;
                    }*/
                    return true;
                }
            }
            props.put (new ValueProp ());
        } else {
            // remove the PropValue if it was added earlier.
            props.remove("value");
        }
    }
    
    /** Returns always false. */
    public boolean canCopy () {
        return false;
    }
    
    /** Returns always false. */
    public boolean canCut () {
        return false;
    }
    
    /**
     * Returns true, if this Node can be renamed.
     *
     * @see org.openide.nodes.Node
     */
    public boolean canRename () {
        if (isKey) {
            return isDeletable;
        } else {
            return false;
        }
    }
    
    /**
     * Sets a new Name for this Property.
     */
    public void setName (String nue) {
        Properties p = System.getProperties ();
        String value = p.getProperty (property);
        p.remove (property);
        if (value != null) p.setProperty (nue, value);
        System.setProperties (p);
        PropertiesNotifier.changed ();
    }
    
    /**
     * Returns true if the Property can be deleted.
     * 
     * @see org.openide.nodes.Node
     */
    public boolean canDestroy () {
        if (isKey) {
            return isDeletable;
        } else {
            return false;
        }
    }
    
    /**
     * Destroys this Node.
     */
    public void destroy () throws IOException {
        Properties p = System.getProperties ();
        p.remove (property);
        System.setProperties (p);
        PropertiesNotifier.changed ();
    }
}