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
public class ToolboxComponent extends TopComponent{
    static final long serialVersionUID=6031472310168514674L;
    private static ToolboxComponent component = null;
    
    private ToolboxComponent(){
        putClientProperty("PersistenceType", "OnlyOpened");
        setLayout(new BorderLayout());
        setName(NbBundle.getMessage(ToolboxComponent.class, "LBL_Toolbox_Title"));   //NOI18N                    
        JPanel panel = new JPanel();                 
        setCloseOperation(TopComponent.CLOSE_EACH);
        add(panel);
        initAccessibility();
    }
        
    public static ToolboxComponent createComp(){
        if(component == null)
            component = new ToolboxComponent();
        return component;
    }     
    
    static void clearRef(){
        component = null;
    }
    
    public HelpCtx getHelpCtx(){
        return new HelpCtx("toolbox");
    }
    
    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ToolboxComponent.class, "ACS_Toolbox_DESC")); // NOI18N
    }
}

