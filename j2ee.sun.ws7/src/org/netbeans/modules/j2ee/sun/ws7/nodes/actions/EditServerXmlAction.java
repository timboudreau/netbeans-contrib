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
 * EditServerXmlAction.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes.actions;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70TargetNode;
import org.openide.ErrorManager;

/**
 *
 * @author Mukesh Garg
 */
public class EditServerXmlAction  extends NodeAction{

    /**
     * Creates a new instance of EditServerXmlAction
     */
    public EditServerXmlAction() {
    }
    protected void performAction(Node[] nodes){
        WS70TargetNode target = (WS70TargetNode)nodes[0].getCookie(WS70TargetNode.class);
        if(target==null){
            ErrorManager.getDefault().log(
                ErrorManager.ERROR, NbBundle.getMessage(EditServerXmlAction.class, "ERR_NULL_TARGET", this.getClass().getName()));
            return;
        }
        target.showServerXml();
    }
    
    protected boolean enable(Node[] nodes){
        if(nodes.length > 0) {
            Object obj = nodes[0].getCookie(WS70TargetNode.class);
            if(obj!=null && obj instanceof WS70TargetNode){
                WS70TargetNode target = (WS70TargetNode)obj;
                if(target!=null){
                    return target.isLocalServer();
                }else{
                    return false;
                }
            }
        }
        return nodes.length==1;
    }
    
    public String getName(){
        return NbBundle.getMessage(EditServerXmlAction.class, "LBL_EditServerXmlAction");
    }
    
    public HelpCtx getHelpCtx(){
        return HelpCtx.DEFAULT_HELP;
    }    
    
}
