/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.sun.ide.avk.actions;



import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.HelpCtx;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import java.io.File;
import java.net.URL;
import org.netbeans.modules.j2ee.sun.ide.avk.AVKSupport;

/**
 * Show the avk help.
 * @author  Ludo
 */
public class ShowAVKHelpAction extends CallableSystemAction {
    
    public ShowAVKHelpAction() {
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }
    
    public void performAction() {
        try{
            //File report =  new File(new AVKSupport().getAVKHome().getAbsolutePath(), "docs" + File.separator + "index.html" ); //NOI18N
            String helpUrl = NbBundle.getMessage(ShowAVKHelpAction.class, "AVK_HELP_URL");
            URLDisplayer.getDefault().showURL(new URL(helpUrl));
        }
        catch (Exception e){
            
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowAVKHelpAction.class, "LBL_AVKHelpAction");
    }
    
//    protected String iconResource() {
//        return "org/openide/resources/actions/empty.gif";  //NOI18N
//    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous(){
        return false;
    }
    
}
