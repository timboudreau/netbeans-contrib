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

import org.netbeans.modules.assistant.tests.*;
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
       return new TestModel().getModel();         
        
    }
    
    public HelpCtx getHelpCtx(){
        return new HelpCtx("assistant");
    }

    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(AssistantComponent.class, "ACS_Assistant_DESC")); // NOI18N
    }
}

