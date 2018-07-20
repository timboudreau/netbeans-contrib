/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */



/*
 * StartStopServerAction.java
 */



package org.netbeans.modules.j2ee.sun.ws7.nodes.actions;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.ErrorManager;
import org.openide.util.actions.NodeAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70TargetNode;

/**
 *
 * @author Administrator
 */

public class StartStopServerAction extends NodeAction{

    private boolean running;
    private WS70TargetNode target;

    /** Creates a new instance of StartStopServerAction */

    public StartStopServerAction() {
    }

   protected void performAction(Node[] nodes){
        WS70TargetNode target = (WS70TargetNode)nodes[0].getCookie(WS70TargetNode.class);
        if(target==null){
            ErrorManager.getDefault().log(
                ErrorManager.ERROR, NbBundle.getMessage(StartStopServerAction.class, "ERR_NULL_TARGET", this.getClass().getName()));            
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
            if(obj!=null && obj instanceof WS70TargetNode){
                target = (WS70TargetNode)obj;
            }
        }
        return nodes.length==1;
    }
    
    public String getName(){        
        if(target!=null){
            running = target.isRunning();        
        }
        if(!running){
            return NbBundle.getMessage(StartStopServerAction.class, "LBL_StartInstanceAction");
        }else{
            return NbBundle.getMessage(StartStopServerAction.class, "LBL_StopInstanceAction");
        }
        
    }
    public HelpCtx getHelpCtx(){
        return HelpCtx.DEFAULT_HELP;
    }
}

