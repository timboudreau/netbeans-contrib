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

import javax.swing.event.*;
import java.io.*;
import java.util.*;

import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.NotifyDescriptor;
import org.openide.TopManager;
import org.openide.util.NbBundle;

/** Root Node for all SystemProperties.
 *
 * @author Michael Ruflin
 */
public class SystemPropertiesNode extends AbstractNode {
    
    /** ResourceBundle used in this class. */
    private static ResourceBundle  bundle = NbBundle.getBundle (SystemPropertiesNode.class);
    
    /** Listener for changes of the SystemProperties.*/
    private ChangeListener listener;
    
    /** Sheet of this Node. */
    private Sheet sheet;

    /**
     * Creates a new SystemPropertiesNode.
     */
    public SystemPropertiesNode () {
        super (new SystemPropertiesChildren ());
        
        // set the IconBase
        setIconBase ("/org/netbeans/modules/sysprops/resources/propertiesRoot");
        
        // DefaultAction
        setDefaultAction (SystemAction.get (PropertiesAction.class));
        
        // Set FeatureDescriptor stuff:
        setName ("SystemPropertiesNode");
        setDisplayName (bundle.getString ("LBL_AllPropsNode"));
        setShortDescription (bundle.getString ("HINT_AllPropsNode"));
        
        // Listener for changes of the Systemproperties
        listener = new ChangeListener () {
            public void stateChanged (ChangeEvent ev) {
                refresh();
            }
        };
        PropertiesNotifier.addChangeListener (listener);
    }
    
    /**
     * Returns an Array of SystemActions allowed by this Node.
     */
    protected SystemAction[] createActions () {
        return new SystemAction[] {
            SystemAction.get (RefreshPropertiesAction.class),
            null,
            SystemAction.get (OpenLocalExplorerAction.class),
            null,
            SystemAction.get (NewAction.class),
            null,
            SystemAction.get (ToolsAction.class),
            null,
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
     * Refreshs the hierarchy of the nodes and the nodes itsself.
     */
    public void refresh() {
        // update the Sheet
        updateSheet();
        
        SystemPropertiesChildren c = (SystemPropertiesChildren) getChildren();
        c.refreshChildren();
        
        // refresh all subnodes.
        Node[] nodes = c.getNodes();
        for (int x=0; x < nodes.length; x++) {
            PropertyNode node = (PropertyNode) nodes[x];
            node.refresh();
        }
    }

    /**
     * Clones this Node.
     */
    public Node cloneNode () {
        return new SystemPropertiesNode ();
    }
   
    /**
     * Finalizes this Object.
     * 
     */
    protected void finalize () throws Throwable {
        super.finalize ();
        if (listener != null) {
            PropertiesNotifier.removeChangeListener (listener);
        }
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

        // Get the Properties Sheet.Set
        // Create one if not yet created -> Bug??? Also needed to create a new
        // one and put it, if already one is created, or a deadlock (one minute
        // or so) occures.
        Sheet.Set props = sheet.get (Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet ();
            sheet.put (props);
        } else {
            // hack !
            props = Sheet.createPropertiesSet ();
            sheet.put (props);
            // remove all Properties -> only needed if the Sheet works correctly
            /*Node.Property[] pro = props.getProperties();
            for (int x=0; x < pro.length; x++) {
                Node.Property nnn= props.remove(pro[x].getName());
            }*/
        }
        
        // Displays a Property with the key and its value
        class ValuePropView extends PropertySupport.ReadOnly {
            String propertyName;
            /** Constructor */
            public ValuePropView (String propertyName) {
                super (propertyName, String.class, propertyName, "");
                this.propertyName = propertyName;
            }
            /** Returns the Value of the PropertySupport. */
            public Object getValue () {
                return System.getProperty (propertyName);
            }
            /** Returns the Name of the PropertySupport. */
            /*public String toString() {
                return propertyName;
            }*/
        }
        
        // get all SystemProperties and order them
        Set set = new TreeSet();
        Enumeration e = System.getProperties().propertyNames();
        while (e.hasMoreElements()) set.add(e.nextElement());
        
        // add all Properties
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String s = (String) it.next();
            props.put (new ValuePropView(s));
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
     * Returns a new NewType-Array. Only a NewType for a new SystemProperty is 
     * returned.
     */
    public NewType[] getNewTypes () {
        return new NewType[] { new SystemPropertyNewType() };            
    }

    /**
     * Returns the Children of the SystemPropertiesNode.
     */
    /*protected SystemPropertiesChildren getSystemPropertiesChildren () {
        return (SystemPropertiesChildren) getChildren ();
    }*/
}