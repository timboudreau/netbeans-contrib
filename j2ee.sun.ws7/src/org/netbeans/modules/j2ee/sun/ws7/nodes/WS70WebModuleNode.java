/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
