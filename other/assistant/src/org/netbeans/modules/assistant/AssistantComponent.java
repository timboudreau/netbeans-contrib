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

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.*;
import org.openide.windows.*;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.accessibility.*;

/**
 *
 * @author  Richard Gregor
 */
public class AssistantComponent extends TopComponent{
    static final long serialVersionUID=6021472310168514674L;
    private static AssistantComponent component = null;
    private AssistantModel model;
    
    
    private AssistantComponent(){
        putClientProperty("PersistenceType", "OnlyOpened");
        setLayout(new BorderLayout());
        setName(NbBundle.getMessage(AssistantComponent.class, "LBL_Title"));   //NOI18N
        JPanel panel = new AssistantView(getModel());
        JScrollPane scroll = new JScrollPane(panel);
        setCloseOperation(TopComponent.CLOSE_EACH);
        add(scroll);
        initAccessibility();
    }
    
    public static AssistantComponent createComp(){
        if(component == null){
            component = new AssistantComponent();
        }
        return component;
    }
    
    static void clearRef(){
        component = null;
    }
    
    private AssistantModel getModel(){
        AssistantSection[] sections = new AssistantSection[3];
        sections[0] = new AssistantSection("IDE Navigator");
        sections[1] = new AssistantSection("Dynamic Help");
        sections[2] = new AssistantSection("Description");        
        java.net.URL url = null;
        java.net.URL url1 = null;
        java.net.URL url2 = null;
        java.net.URL url3 = null;
        java.net.URL descURL = null;
        try{
            url = getClass().getResource("/org/netbeans/modules/assistant/pages/DefaultContent.html");
            url1 = getClass().getResource("/org/netbeans/modules/assistant/pages/DefaultContent1.html");
            url2 = getClass().getResource("/org/netbeans/modules/assistant/pages/DefaultContent2.html");
            url3 = getClass().getResource("/org/netbeans/modules/assistant/pages/DefaultContent3.html");
            descURL = getClass().getResource("/org/netbeans/modules/assistant/pages/DefaultDescription.html");                        
        }catch(Exception e){
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
        }
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test1","TestOne",url3)));
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test2","TestTwo",url1)));
        sections[1].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test3","TestThree",url2)));
        AssistantItem item = new AssistantItem("desc", "text of description", descURL,AssistantItem.DESCRIPTION);
        sections[2].add(new javax.swing.tree.DefaultMutableTreeNode(item));
        
        model = new DefaultAssistantModel(sections);
        return model;
    }
    
    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(AssistantComponent.class, "ACS_Assistant_DESC")); // NOI18N
    }
}

