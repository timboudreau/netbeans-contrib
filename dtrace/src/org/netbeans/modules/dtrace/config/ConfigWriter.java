/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.

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

 * Contributor(s):

 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.

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

/*
 * ConfigWriter.java
 *
 * Created on April 8, 2007, 5:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.dtrace.config;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 *
 * @author nassern
 */
public class ConfigWriter {
    private ConfigData configData;
    private Document dom;
    
    /** Creates a new instance of ConfigWriter */
    public ConfigWriter(ConfigData data) {
        configData = data;
        dom = null;
    }
    
    public void createDocument() {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
	try {
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    dom = db.newDocument();
        } catch(ParserConfigurationException pce) {
            pce.printStackTrace();
	    //System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
	    // System.exit(1);
	}
    }
    
    public void createDOMTree(){
	Element scriptEle = dom.createElement("Script");
	dom.appendChild(scriptEle);
        
        if (configData.getProcessId().length() > 0 &&
                configData.getProcessId().charAt(0) != '\n') {
            Element pidEle = dom.createElement("ProcessId");
            Text pidText = dom.createTextNode(configData.getProcessId());
            pidEle.appendChild(pidText);
            scriptEle.appendChild(pidEle);
        }
        
        if (configData.getExecPath().length() > 0 && 
                configData.getExecPath().charAt(0) != '\n') {
            Element execPathEle = dom.createElement("ExecPath");
            Text execPathText = dom.createTextNode(configData.getExecPath());
            execPathEle.appendChild(execPathText);
            scriptEle.appendChild(execPathEle);
        }
        
        if (configData.getExecArgs().length() > 0 &&
                configData.getExecArgs().charAt(0) != '\n') {
            Element execArgsEle = dom.createElement("ExecArgs");
            Text execArgsText = dom.createTextNode(configData.getExecArgs());
            execArgsEle.appendChild(execArgsText);
            scriptEle.appendChild(execArgsEle);
        }
        
        if (configData.getScriptName().length() > 0 &&
                configData.getScriptName().charAt(0) != '\n') {
            Element scriptNameEle = dom.createElement("ScriptName");
            Text scriptNameText = dom.createTextNode(configData.getScriptName());
            scriptNameEle.appendChild(scriptNameText);
            scriptEle.appendChild(scriptNameEle);   
        }
        
        if (configData.getScriptPath().length() > 0 &&
                configData.getScriptPath().charAt(0) != '\n') {
            Element scriptPathEle = dom.createElement("ScriptPath");
            Text scriptPathText = dom.createTextNode(configData.getScriptPath());
            scriptPathEle.appendChild(scriptPathText);
            scriptEle.appendChild(scriptPathEle); 
        }
        
        if (configData.getScriptArgs().length() > 0 &&
                configData.getScriptArgs().charAt(0) != '\n') {
            Element scriptArgsEle = dom.createElement("ScriptArgs");
            Text scriptArgsText = dom.createTextNode(configData.getScriptArgs());
            scriptArgsEle.appendChild(scriptArgsText);
            scriptEle.appendChild(scriptArgsEle); 
        }
        
    }
    
    public Document getDocoment() {
        return dom;
    }
}
