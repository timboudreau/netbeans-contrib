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
 * PovRayDataNode.java
 *
 * Created on February 16, 2005, 4:36 PM
 */

package org.netbeans.modules.povray;

import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.povproject.MainFileProvider;
import org.netbeans.api.povproject.ViewService;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.ErrorManager;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.EditAction;
import org.openide.actions.RenameAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;
import org.openide.util.Utilities;


/**
 *
 * @author Timothy Boudreau
 */
public class PovRayDataNode extends DataNode {
    
    /** Creates a new instance of PovRayDataNode */
    public PovRayDataNode(PovRayDataObject obj) {
        super (obj, Children.LEAF);
    }
    
    public Action[] getActions(boolean context) {
        Action[] result = new Action[] {
            (Action) SharedClassObject.findObject(EditAction.class),
            new ViewImageAction((PovRayDataObject) getDataObject()),
            (Action) SharedClassObject.findObject(CutAction.class),
            (Action) SharedClassObject.findObject(CopyAction.class),
            (Action) SharedClassObject.findObject(RenameAction.class),
            (Action) SharedClassObject.findObject(DeleteAction.class),
            new SetMainFileAction((PovRayDataObject) getDataObject()),
        };
        if (result[0] == null) {
            result[0] = new EditAction ();
        }
        if (result[2] == null) {
            result[2] = new CutAction();
        }
        if (result[3] == null) {
            result[3] = new CopyAction();
        }
        if (result[4] == null) {
            result[4] = new RenameAction();
        }
        return result;
    }
    
    public Action getPreferredAction() {
        return (Action) getActions(true) [0];
    }
    
    public Image getIcon(int type) {
        return Utilities.loadImage("org/netbeans/modules/povray/resources/povicon.gif");
    }
    
    public String getHtmlDisplayName() {
        if (isMainFile()) {
            return "<b>" + getDisplayName() + "</b>";
        } else {
            return null;
        }
    }
    
    private boolean isMainFile() {
        FileObject file = getDataObject().getPrimaryFile();
        if (file.isValid()) {
            Project project = FileOwnerQuery.getOwner(file);
            if (project != null) {
                MainFileProvider provider = (MainFileProvider) project.getLookup().lookup(MainFileProvider.class);
                if (provider != null && provider.getMainFile() != null && provider.getMainFile().equals(file)) {
                    System.err.println("IsMainFile is true" );
                    return true;
                }
            }
        }
        return false;
    }
    
    public Node.Cookie getCookie(Class clazz) {
        if (clazz == NotifyMainChangedCookie.class) {
            return new NotifyMainChangedCookie();
        } else {
            return super.getCookie(clazz);
        }
    }
    
    private class NotifyMainChangedCookie implements Node.Cookie {
        public void notifyChanged (boolean isMain) {
            fireDisplayNameChange ("<b>" + getDisplayName() + "</b>", getDisplayName());
        }
    }
    
    private final class SetMainFileAction extends AbstractAction {
        private final PovRayDataObject obj;
        public SetMainFileAction (PovRayDataObject obj) {
            this.obj = obj;
            putValue (Action.NAME, NbBundle.getMessage(PovRayDataNode.class, "ACTION_SetMainFile"));
        }
        
        public void actionPerformed (ActionEvent ae) {
            Project project = FileOwnerQuery.getOwner(obj.getPrimaryFile());
            if (project != null) { //If so, we're in the favorites tab or something
                MainFileProvider provider = (MainFileProvider) project.getLookup().lookup(MainFileProvider.class);
                if (provider != null) { //If so we're in something like a java project
                    FileObject oldMain = provider.getMainFile();
                    provider.setMainFile(obj.getPrimaryFile());
                    fireDisplayNameChange(getDisplayName(), getHtmlDisplayName());
                    if (oldMain != null && oldMain.isValid()) {
                        try {
                            NotifyMainChangedCookie cookie = (NotifyMainChangedCookie) 
                                DataObject.find(oldMain).getNodeDelegate().getCookie(NotifyMainChangedCookie.class);
                            if (cookie != null) {
                                cookie.notifyChanged(false);
                            }
                        } catch (DataObjectNotFoundException donfe) {
                            ErrorManager.getDefault().notify (donfe);
                        }
                    }
                }
            }
            System.err.println("setMainFile to " + obj.getPrimaryFile().getPath());
        }
        
        public boolean isEnabled () {
            return !isMainFile();
        }
    }
    
    private final class ViewImageAction extends AbstractAction {
        private final PovRayDataObject obj;
        
        public ViewImageAction (PovRayDataObject obj) {
            this.obj = obj;
            putValue (Action.NAME, NbBundle.getMessage (PovRayDataNode.class, "ACTION_ViewImage"));
        }

        public void actionPerformed (ActionEvent ae) {
            Project project = FileOwnerQuery.getOwner(obj.getPrimaryFile());
            if (project != null) {
                ViewService view = (ViewService) project.getLookup().lookup(ViewService.class);
                if (view != null) {
                    view.view(obj.getPrimaryFile());
                }
            }
        }
        
        public boolean isEnabled() {
            Project project = FileOwnerQuery.getOwner(obj.getPrimaryFile());
            if (project != null) {
                ViewService view = (ViewService) project.getLookup().lookup(ViewService.class);
                if (view != null) {
                    return true;
                }
            }
            return false;
        }
    }
}
