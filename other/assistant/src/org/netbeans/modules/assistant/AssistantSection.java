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

import java.util.*;
import javax.swing.tree.*;
import javax.swing.*;
import java.net.*;

/*
 * AssistantSection.java
 *
 * Created on October 11, 2002, 1:52 PM
 *
 * @author  Richard Gregor
 */
public class AssistantSection extends DefaultMutableTreeNode{
    private String name;
    private ImageIcon icon;

    public AssistantSection(String name,URL iconURL){
        this.name = name;
        if(iconURL != null)
            setIcon(iconURL);
    }

    public AssistantSection(String name){
        this(name, null);
    }
        
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }    
    
    public void setIcon (URL iconURL){
        icon = new ImageIcon(iconURL);
    }
    
    public ImageIcon getIcon(){
        return this.icon;
    }
    
    public String toString(){
        return "<HTML><B>"+name+"</B></HTML>";
    }
    
}
