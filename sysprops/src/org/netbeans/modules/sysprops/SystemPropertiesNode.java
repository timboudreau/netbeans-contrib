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
 * @author Michael Ruflin, Jesse Glick
 */
public class SystemPropertiesNode extends PropertyNode {
    
    /** ResourceBundle used in this class. */
    private static ResourceBundle bundle = NbBundle.getBundle (SystemPropertiesNode.class);
    
    /** Listener for changes of the SystemProperties.*/
    private ChangeListener listener;
    
    /** Sheet of this Node. */
    private Sheet sheet;

    /**
     * Creates a new SystemPropertiesNode.
     */
    public SystemPropertiesNode () {
        super (null, listAllProperties ());
        
        // Set FeatureDescriptor stuff:
        setName ("SystemPropertiesNode");
        setDisplayName (bundle.getString ("LBL_AllPropsNode"));
        setShortDescription (bundle.getString ("HINT_AllPropsNode"));
    }
    
    /** Get a list of all system properties.
     * @return all of them
     */
    public static List listAllProperties () {
        List l = new ArrayList ();
        Enumeration en = System.getProperties ().propertyNames ();
        while (en.hasMoreElements ())
            l.add (en.nextElement ());
        return l;
    }
    
    /**
     * Returns an Array of SystemActions allowed by this Node.
     * @return a specialized set of actions
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
     * Clones this Node.
     * @return a clone
     */
    public Node cloneNode () {
        return new SystemPropertiesNode ();
    }
   
}
