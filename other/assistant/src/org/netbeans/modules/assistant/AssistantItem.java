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

import java.net.*;
import javax.swing.tree.*;
/*
 * AssistantID.java
 *
 * Created on October 11, 2002, 1:52 PM
 *
 * @author  Richard Gregor
 */
public class AssistantItem extends Object{
    private String name;
    private String displayName;      
    private URL url;
    private int type;
    
    public static final int LINK=0;
    public static final int DESCRIPTION = 1;
    public static final int SEARCH = 2;
    
    public AssistantItem(String name, String displayName, URL url, int type){
        this.name = name;
        this.displayName = displayName;
        this.url = url;
        this.type = type;
    }
    
    public AssistantItem(String name, String displayName, URL url){
        this(name,displayName, url,AssistantItem.LINK);              
    }
    
    public String getName(){
        return name;
    }    
    
    public URL getURL(){
        return url;
    }
    
    public String getDisplayName(){
        return displayName;
    }
    
    public int getType(){
        return type;
    }
    
    public String toString(){
        String text = "";
        if(getType() == AssistantItem.LINK){
            text = "<HTML><A HREF=test.html>"+displayName+"</A></HTML>";
        }else if(getType() == AssistantItem.DESCRIPTION){            
            text = "<HTML>This is description used </BR>for demonstration purposes only.</BR> Welcome in NetBeans</HTML>";
        }
        return text;
    }
    
}
