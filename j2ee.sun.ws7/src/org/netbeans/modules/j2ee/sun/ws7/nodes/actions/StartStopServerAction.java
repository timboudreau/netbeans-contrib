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
 * StartStopServerAction.java  
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes.actions;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70TargetNode;

/**
 *
 * @author Administrator
 */
public class StartStopServerAction extends NodeAction{
    private boolean running;
    
    /** Creates a new instance of StartStopServerAction */
    public StartStopServerAction() {
    }
   protected void performAction(Node[] nodes){
        WS70TargetNode target = (WS70TargetNode)nodes[0].getCookie(WS70TargetNode.class);
        if(target==null){
            System.err.println("TARGET IS NULL");
            return;
        }
        if(!running){
            target.startTarget();            
        }else{
            target.stopTarget();
            
        }
    }
    
    protected boolean enable(Node[] nodes){       
        
        if(nodes.length > 0) {
            Node node = nodes[0];
            
            Object obj = nodes[0].getCookie(WS70TargetNode.class);
            if(obj instanceof WS70TargetNode){
                WS70TargetNode target = (WS70TargetNode)obj;
                if(target!=null){
                    if(target.isRunning()) {
                        running = true;
                    } else {
                        running = false;
                    }               
                }else{
                    System.err.println("Target is null when enable is called on StartStopServer");
                }
            }
        }       
  
        return nodes.length==1;        
 
    }
    
    public String getName(){
        if(!running){
            return NbBundle.getMessage(StartServerAction.class, "LBL_StartInstanceAction");
        }else{
            return NbBundle.getMessage(StartServerAction.class, "LBL_StopInstanceAction");
        }
    }
    
    public HelpCtx getHelpCtx(){
        return HelpCtx.DEFAULT_HELP;
    }        
    
}
