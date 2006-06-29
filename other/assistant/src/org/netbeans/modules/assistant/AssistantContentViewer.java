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

package org.netbeans.modules.assistant;

import org.netbeans.modules.assistant.event.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.*;
import org.openide.windows.*;

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import javax.accessibility.*;

/**
 *
 * @author  Richard Gregor
 */
public class AssistantContentViewer extends TopComponent implements AssistantModelListener{
    static final long serialVersionUID=6281472310168514674L;
    private static AssistantContentViewer component = null;
    private JEditorPane editor = null;
    private AssistantModel model;
    
    private AssistantContentViewer(){
        putClientProperty("PersistenceType", "OnlyOpened");
        setLayout(new BorderLayout());
        setName(NbBundle.getMessage(AssistantComponent.class, "LBL_Content_Title"));   //NOI18N
        editor = new JEditorPane();
        editor.setEditorKit(new javax.swing.text.html.HTMLEditorKit());
        JScrollPane scroll = new JScrollPane(editor);
        setCloseOperation(TopComponent.CLOSE_EACH);
        add(scroll);
        initAccessibility();          
    }
    
    public static AssistantContentViewer createComp(){
        if(component == null){
            component = new AssistantContentViewer();
        }
        return component;
    }
        
    public void setPage(URL url){
        if(editor != null){
            try{
                editor.setPage(url);
            }catch(Exception e){
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            }
        }
    }
    
    static void clearRef(){
        component = null;
    }    
    
    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(AssistantComponent.class, "ACS_Assistant_Content_DESC")); // NOI18N
    }
    
    /** Tells the listener that the current ID in the AssistantModel has
     * changed.
     *
     * @param e The event
     *
     */
    public void idChanged(AssistantModelEvent e) {
        debug("id changed");
    }
    
    /** Tells the listener that the current URL has changed.
     *
     * @param e The event
     *
     */
    public void urlChanged(AssistantModelEvent e) {
        debug("set page:"+e.getURL());
        open();
        requestFocus();        
        setPage(e.getURL());
    }
    
    public HelpCtx getHelpCtx(){
        return new HelpCtx("assistant");
    }
    
    private boolean debug = false;
    private void debug(String msg){
        if(debug)
            System.err.println("AssistantContentViewer: "+msg);
    }
}

