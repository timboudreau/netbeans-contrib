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
 * WS70JVMNode.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;

import javax.swing.Action;
import org.openide.util.Lookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.SystemAction;
import org.openide.actions.PropertiesAction;

import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Administrator
 */
public class WS70JVMNode extends AbstractNode{
    private WS70JVMManagedObject jvm;
    /**
     * Creates a new instance of WS70JVMNode
     */
    public WS70JVMNode(WS70JVMManagedObject jvm) {
        super(Children.LEAF);
        this.jvm = jvm;
        setDisplayName(jvm.getDisplayName());
        setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/JVMIcon.gif");
    }
    public Action getPreferredAction() {
        return SystemAction.get(PropertiesAction.class);
    }  
    public Action[] getActions(boolean context) {
        return new SystemAction[] {
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
        return jvm.updateSheet(sheet);
    }    
}
