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

import org.netbeans.modules.assistant.tests.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.*;
import org.openide.windows.*;

import java.io.*;
import java.awt.*;
import java.net.*;
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
        URL url = getClass().getResource("tests/assistant.xml");
        AssistantContext ctx = new AssistantContext(url);
        model = new DefaultAssistantModel(ctx);
        model.setCurrentID("default_id");
        return model;
        
    }
    
    public HelpCtx getHelpCtx(){
        return new HelpCtx("assistant");
    }

    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(AssistantComponent.class, "ACS_Assistant_DESC")); // NOI18N
    }
}

