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
 * WS70ResourceNode.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.swing.Action;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;
import org.openide.actions.PropertiesAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.DeleteResourceAction;

/**
 *
 * @author Administrator
 */
public class WS70ResourceNode extends AbstractNode implements Node.Cookie{
    
    private WS70Resource resource;
    /**
     * Creates a new instance of WS70ResourceNode 
     */
    public WS70ResourceNode(WS70Resource res) {
        super(Children.LEAF);
        this.resource = res;
        setDisplayName(resource.getJndiName());
        getCookieSet().add(resource);
        setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/ResourceIcon.gif");        
    }
    public Action getPreferredAction() {
        return SystemAction.get(PropertiesAction.class);
    }  
    public Action[] getActions(boolean context) {
        return new SystemAction[] {            
            SystemAction.get(DeleteResourceAction.class),
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
        return resource.updateSheet(sheet);
    }
}
