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

package org.netbeans.modules.assistant.actions;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.*;
import org.openide.util.actions.CallableSystemAction;
import org.openide.awt.HtmlBrowser;
import org.openide.windows.*;

import java.awt.*;
import java.net.*;
import javax.swing.*;
import java.util.*;

import org.netbeans.modules.assistant.*;
/**
 * Action that can always be invoked and work procedurally.
 *
 * @author  Richard Gregor
 */
public class ShowAssistantAction extends CallableSystemAction {
    privateJPanel content;
    private static final String ASSISTANT_MODE_NAME = "assistant"; // NOI18N
    
    public void performAction() {         
        TopComponent assistantComponent = AssistantComponent.createComp(); 
        assistantComponent.open();        
        assistantComponent.requestFocus(); 
        //experimental toolbox
        TopComponent toolbox = ToolboxComponent.createComp(); 
        toolbox.open();
        TopComponent content = AssistantContentViewer.createComp();
        content.open();
    }
    
    public String getName() {
        return NbBundle.getMessage(AssistantComponent.class, "LBL_Action");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/assistant/resources/assistant.gif";
    }
    
    private URL iconURL(){
        URL iconURL = null;
        try{
            iconURL = new URL("nbresloc:/org/netbeans/modules/resources/assistant.gif");
        }catch(MalformedURLException e){
            //todo
        }
        return iconURL;
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (MyAction.class);
    }
    
    
    /** Perform extra initialization of this action's singleton.
     * PLEASE do not use constructors for this purpose!
     * protected void initialize () {
     * super.initialize ();
     * putProperty (Action.SHORT_DESCRIPTION, NbBundle.getMessage (MyAction.class, "HINT_Action"));
     * }
     */
    
}
