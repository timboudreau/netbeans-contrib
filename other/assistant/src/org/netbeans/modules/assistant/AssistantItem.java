/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
