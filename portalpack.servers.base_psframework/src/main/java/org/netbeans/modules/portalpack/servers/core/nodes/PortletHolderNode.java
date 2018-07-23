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

package org.netbeans.modules.portalpack.servers.core.nodes;

import org.netbeans.modules.portalpack.servers.core.common.enterprise.NodeTypeConstants;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.RefreshPortletsAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.RefreshCookie;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

 public class PortletHolderNode extends AbstractNode {
        
        private static String ICON_BASE = "org/netbeans/modules/portalpack/servers/core/resources/mfolder.gif";  
        public PortletHolderNode (Lookup lookup, String dn){
            super(new PortletChildrenNode(lookup,NodeTypeConstants.PORTLET,dn));
            setDisplayName("Portlets");  // NOI18N            
            setIconBaseWithExtension(ICON_BASE);        
            setShortDescription(getShortDescription());
            getCookieSet().add(new RefreshPortletChildren ((PortletChildrenNode)getChildren()));
        }
        
        public javax.swing.Action[] getActions(boolean context) {
            return new SystemAction[] {
                   SystemAction.get(RefreshPortletsAction.class)
               };    
        }
    }
    
    
    class RefreshPortletChildren implements RefreshCookie {
        PortletChildrenNode children;
        RefreshPortletChildren (PortletChildrenNode children){
            this.children = children;
        }

        public void refresh() {
            children.updateKeys();
        }
    }
