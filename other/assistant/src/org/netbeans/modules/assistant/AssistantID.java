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

import java.util.*;
/*
 * AssistantID.java
 * ID represents content of assistant window. It contains sections diplsayed there.
 * Created on October 11, 2002, 1:52 PM
 *
 * @author  Richard Gregor
 */
public class AssistantID {
    private String name;    
    private Vector sections;    
    
    public AssistantID(String name){
        this.name = name;        
        sections = new Vector();
    }
    
    public String getName(){
        return name;
    }
    
    public void addSection(AssistantSection section){
        sections.addElement(section);
    }
    
    public void addSection(AssistantSection[] section){
        debug("addSections");
        if(section != null){
            for(int i=0 ; i< section.length; i++){
                debug("section: "+section[i]);
                sections.add(section[i]);
            }
        }
    }    
    
    public Enumeration getSections(){
        Enumeration enum ;
        if(sections != null)
            enum = sections.elements();
        else
            enum = null;
        return enum;
    }
    
    private boolean debug = false;
    private void debug(String msg){
        if(debug)
            System.err.println("AssistantID: "+msg);
    }
}
