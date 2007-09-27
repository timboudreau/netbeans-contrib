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
