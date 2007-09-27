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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.wizards;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

/**
 *
 * @author Jan Lahoda
 */
public class Storage {

    /** Creates a new instance of Storage */
    private Storage() {
    }

    private static Storage instance;
    
    public static synchronized Storage getDefault() {
        if (instance == null) {
            instance = new Storage();
        }
        
        return instance;
    }
    
    public Collection getAllDocumentClasses(boolean includeDefault) {
        Collection result = new ArrayList();
        FileObject latexCommandsFolder = Repository.getDefault().getDefaultFileSystem().findResource("latex/commands");
        
        FileObject[] children = latexCommandsFolder.getChildren();
        
        for (int cntr = 0; cntr < children.length; cntr++) {
            FileObject current = children[cntr];
            Object     typeObj = current.getAttribute("type");
            
            if (!(typeObj instanceof String))
                continue;
            
            String type = (String) typeObj;
            
            if (!"docclass".equals(type))
                continue;
            
            if (!includeDefault) {
                Object defaultFlag = current.getAttribute("default");
                
                if (defaultFlag != null && defaultFlag instanceof Boolean && ((Boolean) defaultFlag).booleanValue())
                    continue;
            }
            
            result.add(current.getName());
        }
        
        return result;
    }
    
    public void addDocumentClass(String name) throws IOException {
        if (getAllDocumentClasses(true).contains(name))
            throw new IllegalArgumentException();
        
        FileObject latexCommandsFolder = Repository.getDefault().getDefaultFileSystem().findResource("latex/commands");
        FileObject newFolder = latexCommandsFolder.createFolder(name);
        
        newFolder.setAttribute("type", "docclass");
    }
    
    public void addFontSize(String docclass, String fontSize) {
        throw new IllegalStateException("");
    }
    
    private List readFile(FileObject options) {
        if (options == null)
            return Collections.EMPTY_LIST;
        
        InputStream ins = null;
        BufferedReader reader  = null;
        List result = new ArrayList();
        
        try {
            ins = options.getInputStream();
            
            reader = new BufferedReader(new InputStreamReader(ins));
            String line;
            
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            return result;
        } catch (IOException e) {
            return result;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    ins = null;
                } catch (IOException e) {
                }
            }
            
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    private List readFile(String name, String fileName) {
        return readFile(Repository.getDefault().getDefaultFileSystem().findResource("latex/commands/" + name + "/" + fileName));
    }
    
    public Collection getOptions(String name) {
        return readFile(name, "options.txt");
    }
    
    public String getInputEncPackageName() {
        return "inputenc";
    }
    
    public Collection getSupportedFontSizes(String docclass) {
        if (!getAllDocumentClasses(true).contains(docclass))
            throw new IllegalArgumentException("");
        
        return readFile(docclass, "fontsizes.txt");
    }
    
    public boolean isDefaultFontSize(String docclass, String fontSize) {
        return readFile(docclass, "fontsizes.txt").get(0).equals(fontSize);
    }

    public Collection getSupportedPaperSizes(String docclass) {
        if (!getAllDocumentClasses(true).contains(docclass))
            throw new IllegalArgumentException("");
        
        return readFile(docclass, "papersizes.txt");
    }
    
    public boolean isDefaultPaperSize(String docclass, String paperSize) {
        return readFile(docclass, "papersizes.txt").get(0).equals(paperSize);
    }
    
    public boolean isDefaultEncoding(String encoding) {
        return readFile(getInputEncPackageName(), "options.txt").get(0).equals(encoding);
    }

}
