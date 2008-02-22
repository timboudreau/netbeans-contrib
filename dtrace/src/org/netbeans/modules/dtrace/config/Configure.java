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
 * Configure.java
 *
 * Created on April 5, 2007, 10:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.dtrace.config;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.openide.ErrorManager;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author nassern
 */
public class Configure {
    private String fileName;
    private Document dom;
    
    /** Creates a new instance of Configure */
    public Configure(String fileName) {
        this.fileName = fileName;
        dom = null;
    }
    
    public FileOutputStream getWriter() {
        FileOutputStream fos = null;
        try {
            if (fileName.length() > 0) {
                fos = new FileOutputStream(new File(fileName));
            }
        } catch (IOException ex) {
            //ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot opon %s", fileName);
            //ex.printStackTrace();
        }
        return fos;
    }
    
    public void closeWriter(FileOutputStream fos) {
        try {
            fos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public BufferedInputStream getReader() {
        BufferedInputStream is = null;
        try {
            if (fileName.length() > 0) {
                is = new BufferedInputStream(new FileInputStream(fileName));
            }
        } catch (IOException ex) {
            //ErrorManager.getDefault().log(ErrorManager.WARNING, "");
            //ex.printStackTrace();
        }
        return is;
    }
        
    public void closeReader(InputStream is) {
        try {
            is.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    synchronized public ConfigData read() {
        ConfigReader reader = new ConfigReader();
        
        try {
            InputStream iStream = getReader();
            if (iStream == null) {
                return new ConfigData();
            }
            
            InputSource is = new InputSource(iStream);
            XMLReader xmlReader = XMLUtil.createXMLReader();           
            xmlReader.setContentHandler(reader);
            xmlReader.parse(is);
            closeReader(iStream);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
        
        return reader.getConfigData();
    }
  
    synchronized public void write(ConfigData data) {
        try {
            FileOutputStream fos = getWriter();
            if (fos == null) {
                ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot save the config file");
                return;
            } 
            
            ConfigWriter writer = new ConfigWriter(data);
            writer.createDocument();
            writer.createDOMTree();
            
            dom = writer.getDocoment();
            OutputFormat format = new OutputFormat(dom);
	    format.setIndenting(true);
        
            XMLSerializer serializer = new XMLSerializer(fos, format);
            serializer.serialize(dom);
            
            fos.flush();
            closeWriter(fos);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
