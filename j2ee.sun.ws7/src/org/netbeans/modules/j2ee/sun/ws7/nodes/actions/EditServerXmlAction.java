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
            if(obj instanceof WS70TargetNode){
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
