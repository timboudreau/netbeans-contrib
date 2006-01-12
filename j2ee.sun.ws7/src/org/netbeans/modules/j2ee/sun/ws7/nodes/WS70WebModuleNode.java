/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70WebModuleNode.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;
import org.openide.actions.PropertiesAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.UndeployAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.EnableDisableModuleAction;     

import java.util.Collection;
import javax.swing.Action;
/**
 *
 * @author Administrator
 */
public class WS70WebModuleNode extends AbstractNode implements Node.Cookie{
    
    private WS70WebModule webModule;
    /** Creates a new instance of WS70WebModuleNode */
    public WS70WebModuleNode(WS70WebModule module) {
        super(Children.LEAF);
        this.webModule = module;
        setDisplayName(module.getName());
        getCookieSet().add(module);
        setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/DeployedModuleWarIcon.gif");
    }
    public Action getPreferredAction() {
        return SystemAction.get(PropertiesAction.class);
    }  
    public Action[] getActions(boolean context) {
        return new SystemAction[] {
            SystemAction.get(EnableDisableModuleAction.class),
            SystemAction.get(UndeployAction.class),            
            SystemAction.get(PropertiesAction.class)
        };
    }   
    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        
        // PROPERTIES
        Sheet.Set ssProp = sheet.get (Sheet.PROPERTIES);       
        if (ssProp == null) {
	    ssProp = Sheet.createPropertiesSet ();
            sheet.put (ssProp);
	}        
        return webModule.updateSheet(sheet);
    }   

}
