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
import java.io.*;
import javax.swing.tree.*;
/*
 * AssistantItem.java
 *
 * Created on October 11, 2002, 1:52 PM
 *
 * @author  Richard Gregor
 */
public class AssistantItem extends Object{
    private String name;
    private URL url;    
    /* Types of item */   
    public static final int LINK=0;
    public static final int TEXT = 1;
    /*Default type is LINK */
    private int type = this.LINK;
   
    public AssistantItem(String name, URL url, int type){
        this.name = name; 
        this.url = url;
        this.type = type;
    }
    
    public AssistantItem(String name, URL url){
        this(name,url,LINK);              
    }
    
    public String getName(){
        return name;
    }    
    
    public URL getURL(){
        return url;
    }
    
    public int getType(){
        return type;
    }
    
    public String toString(){
        String text = "";
        if(getType() == LINK){
            text = "<HTML><A HREF=test.html>"+name+"</A></HTML>";
        }else if(getType() == TEXT){            
         /*   StringBuffer buf = new StringBuffer();
            String inputLine;
            BufferedReader in = null;
            try{
                in = new BufferedReader(new InputStreamReader(url.openStream()));
                while ((inputLine = in.readLine()) != null)
                    buf.append(inputLine);
            }catch(IOException ioe){
                //ioe
            }finally{
                try{
                    in.close();
                }catch(Exception e){
                    //ignore
                }
            }
            text = "<HTML>"+buf.toString()+"</HTML>"; */
            text = "<HTML>"+name+"</HTML>";
        }
        return text;
    }
    
}
