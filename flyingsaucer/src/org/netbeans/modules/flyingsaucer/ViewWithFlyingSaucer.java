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
 */
package org.netbeans.modules.flyingsaucer;

import java.net.URL;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;

/**
 * Action to open an XHTML viewer.
 *
 * @author Tim Boudreau
 */
public final class ViewWithFlyingSaucer extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        DataObject c = (DataObject) activatedNodes[0].getCookie(DataObject.class);
        FileObject ob = c.getPrimaryFile();
        try {
            URL url = ob.getURL();
            TopComponent comp = new FlyingSaucerTopComponent (url);
            comp.open();
            comp.requestActive();
        } catch (FileStateInvalidException ex) {
            ErrorManager.getDefault().notify(ErrorManager.USER,
                    ex);
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(ViewWithFlyingSaucer.class, "CTL_ViewWithFlyingSaucer");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataObject.class
        };
    }
    
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
}
