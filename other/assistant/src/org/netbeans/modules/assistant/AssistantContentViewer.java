/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
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
        setPage(e.getURL());
    }
    
    private boolean debug = false;
    private void debug(String msg){
        if(debug)
            System.err.println("AssistantContentViewer: "+msg);
    }
}

