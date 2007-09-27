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

import org.netbeans.modules.assistant.parsing.*;
//import org.openide.*;
import org.xml.sax.*;

import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.tree.*;
/*
 * AssistantContext.java
 *
 * Created on October 11, 2002, 1:52 PM
 *
 * @author  Richard Gregor
 */
public class AssistantContext {
    private String name;
    private Vector ids;

    public AssistantContext(){
        this((AssistantID)null);
    }

    public AssistantContext(AssistantID id){
        ids = new Vector();
        if(id != null)
            addID(id);
    }
    
    public AssistantContext(String xmlFile){
        if(xmlFile == null)
            return;
        File file = new File(xmlFile);
        AssistantParser parser = new AssistantParser(new DefaultAssistantHandler(),null);
        try{
            parser.parse(new InputSource(new FileReader(xmlFile)));
        }catch(Exception e){
            System.err.println("AssistantContext 1: "+e);
            //ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
        }
    }
    
    public AssistantContext(URL xmlURL){
        ids = new Vector();
        if (xmlURL == null)
            return;
        AssistantParser parser = new AssistantParser(new DefaultAssistantHandler(),null);
        try{
            parser.parse(xmlURL);
        }catch(Exception e){
            System.err.println("AssistantContext 2: "+e);
            //ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
        }
    }
    
    public void addID(AssistantID id){
        ids.addElement(id);
    }
    public Enumeration getIDs(){
        Enumeration enum ;
        if(ids != null)
            enum = ids.elements();
        else
            enum = null;
        return enum;
    }
    
    public void addID(AssistantID[] id){
        if(id == null)
            return;
        for(int i = 0 ; i < id.length; i++){
            addID(id[i]);
        }
    }
    
    public class DefaultAssistantHandler implements AssistantHandler {
        private AssistantID id;
        private AssistantSection section;
        private AssistantItem item;
        
        public static final boolean DEBUG = false;
        
        public void handle_item(final java.lang.String data, final AttributeList meta) throws SAXException {            
            if (DEBUG) System.err.println("handle_item: " + data);
            URL url = null;
            url = getClass().getResource(meta.getValue("url"));
            String action = meta.getValue("action");
            int type = AssistantItem.LINK;
            if((meta.getValue("type") != null) &&(meta.getValue("type").equals("text")))
                type = AssistantItem.TEXT;
            item = new AssistantItem(data,url,action,type);
            section.add(new DefaultMutableTreeNode(item));                        
        }
        
        public void start_section(final AttributeList meta) throws SAXException {           
            if (DEBUG) System.err.println("start_section: " + meta);            
            String icon = meta.getValue("icon");            
            URL iconURL = null;
            if(icon != null)
                iconURL = getClass().getResource(icon);
            section = new AssistantSection(meta.getValue("name"),iconURL);
        }
        
        public void end_section() throws SAXException {
            if (DEBUG) System.err.println("end_section()"); 
            id.addSection(section);
        }
        
        public void start_assistant(final AttributeList meta) throws SAXException {
            if (DEBUG) System.err.println("start_assistant: " + meta);
        }
        
        public void end_assistant() throws SAXException {
            if (DEBUG) System.err.println("end_assistant()");
        }
        
        public void start_id(final AttributeList meta) throws SAXException {
            if (DEBUG) System.err.println("start_id: " + meta);
            id = new AssistantID(meta.getValue("name"));
        }
        
        public void end_id() throws SAXException {
            if (DEBUG) System.err.println("end_id()");
            addID(id);
        }
        
    }
}
