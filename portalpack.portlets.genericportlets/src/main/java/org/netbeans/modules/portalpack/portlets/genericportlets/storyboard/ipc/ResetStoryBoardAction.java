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
package org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CookieAction;

/**
 *
 * @author Satyaranjan
 */
public class ResetStoryBoardAction extends CookieAction implements Runnable {

    protected int mode() {
        return CookieAction.MODE_ALL;
    }

    protected Class<?>[] cookieClasses() {
        return new Class[]{};
    }

    protected void performAction(Node[] activatedNodes) {

        // RequestProcessor.getDefault().post(new Thread(){
        //     public void run(){
        // currentNode = activatedNodes[0];
        RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
    //  }
    // }};
    }

    public String getName() {
        return NbBundle.getMessage(ResetStoryBoardAction.class, "MENU_RESET_STORY_BOARD");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean enable(Node[] nodes) {

        if (nodes == null || nodes.length < 1) {
            return false;
        }
        return true;
    }

    public void run() {

        //PortletDataObject dataObject = (PortletDataObject) node.getDataObject();
        IPCStoryBoardTopComponent win = IPCStoryBoardTopComponent.findInstance();
        win.open();
        win.resetScene();
        win.requestActive();
    }
}
