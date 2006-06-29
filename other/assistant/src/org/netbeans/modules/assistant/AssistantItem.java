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
    private String action;
    /* Type of item */
    public static final int LINK=0;
    public static final int TEXT = 1;
    /*Default type is LINK */
    private int type = this.LINK;

    /**
     *If you define URL and Action then there will be page displayed and
     *action performed. If you set type to TEXT then action and URl will be
     *ignored. In case you define onlyu action and don't define URl, there
     *will be last page displayed in viewer and action performed.
     */
    public AssistantItem(String name, URL url, String action, int type){
        this.name = name; 
        this.url = url;
        this.type = type;
        this.action = action;
    }
    
    public AssistantItem(String name, URL url, int type){
        this(name, url, null,type);
    }
    
    public AssistantItem(String name, String action){
        this.name = name;
        this.action = action;
    }
    
    public AssistantItem(String name, URL url){
        this(name,url,null,LINK);              
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
    
    public String getAction(){
        return action;
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
