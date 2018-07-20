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
package org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.PortletXMLChildrenNode;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

public class PortletXMLDataNode extends DataNode implements FileChangeListener {
    
    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/portlet-xml.gif";
    
    public PortletXMLDataNode(PortletXMLDataObject obj) {
        super(obj, new PortletXMLChildrenNode(obj));
        getCookieSet().add(new RefreshChannelChildren((PortletXMLChildrenNode)getChildren()));
        setIconBaseWithExtension(IMAGE_ICON_BASE);
        obj.addFileChangeListener(this);
    }
    PortletXMLDataNode(PortletXMLDataObject obj, Lookup lookup) {
        super(obj, new PortletXMLChildrenNode(obj), lookup);
        
        Lookups.singleton(new RefreshChannelChildren((PortletXMLChildrenNode)getChildren()));
       // getCookieSet().add(new RefreshChannelChildren((PortletChildrenNode)getChildren()));
        setIconBaseWithExtension(IMAGE_ICON_BASE);
        obj.addFileChangeListener(this);
    }
    
    public javax.swing.Action[] getActions(boolean context) {
            javax.swing.Action[] actions = super.getActions(context);
            javax.swing.Action[] finalActions = new javax.swing.Action[actions.length + 1];
            List actionList = new ArrayList();
            for(int i=0;i<actions.length;i++)
            {
                actionList.add(actions[i]);
            }
            actionList.add(SystemAction.get(RefreshPortletXMLAction.class));
            return (javax.swing.Action [])actionList.toArray(new javax.swing.Action[0]);
            
    }

    public void destroy() throws IOException {
        super.destroy();
        DataObject dob = getDataObject();
        if(dob != null && dob instanceof PortletXMLDataObject)
        {
            ((PortletXMLDataObject)dob).removeFileChangeListener(this);
        }
    }

    //    /** Creates a property sheet. */
    //    protected Sheet createSheet() {
    //        Sheet s = super.createSheet();
    //        Sheet.Set ss = s.get(Sheet.PROPERTIES);
    //        if (ss == null) {
    //            ss = Sheet.createPropertiesSet();
    //            s.put(ss);
    //        }
    //        // TODO add some relevant properties: ss.put(...)
    //        return s;
    //    }

    public void fileFolderCreated(FileEvent fe) {
    }

    public void fileDataCreated(FileEvent fe) {
    }

    public void fileChanged(FileEvent fe) {
       RefreshCookie cookie = (RefreshCookie)getCookie(RefreshCookie.class);            
       if (cookie != null)
           cookie.refresh();
        
    }

    public void fileDeleted(FileEvent fe) {
    }

    public void fileRenamed(FileRenameEvent fe) {
    }

    public void fileAttributeChanged(FileAttributeEvent fe) {
    }
    
}

class RefreshChannelChildren implements RefreshCookie{ //RefreshCookie {
    PortletXMLChildrenNode children;
    
    RefreshChannelChildren(PortletXMLChildrenNode children){
        this.children = children;
        
    }
    
    public void refresh() {
        children.refreshPortletDOB();
        children.updateKeys();
    }
}
