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
        while (en.hasMoreElements ()) {
            String prop = (String) en.nextElement ();
            // Exclude environment variables here.
            if (! prop.startsWith ("Env-") && ! prop.startsWith ("env-")) {
                l.add (prop);
            }
        }
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

    /** Get the property sheet.
     * @return the usual, plus environment variables
     */
    protected Sheet createSheet () {
        Sheet s = super.createSheet ();
        Sheet.Set ss = new Sheet.Set ();
        ss.setName ("envvars");
        ss.setDisplayName (bundle.getString ("LBL_envvars_tab"));
        ss.setShortDescription (bundle.getString ("HINT_envvars_tab"));
        Enumeration en = System.getProperties ().propertyNames ();
        while (en.hasMoreElements ()) {
            String prop = (String) en.nextElement ();
            // List environment variables (only the ones with proper case).
            if (prop.startsWith ("Env-")) {
                String env = prop.substring (4); // cut off Env-
                ss.put (new EnvVarProp (env));
            }
        }
        s.put (ss);
        return s;
    }
    
    /** Property representing one environment variable. */
    private static final class EnvVarProp extends PropertySupport.ReadOnly {
        public EnvVarProp (String env) {
            super (env, String.class, env, bundle.getString ("HINT_env_value"));
        }
        public Object getValue () {
            return System.getProperty ("Env-" + getName ());
        }
    }
   
}
