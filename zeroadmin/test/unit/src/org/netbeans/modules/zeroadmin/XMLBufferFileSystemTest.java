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

package org.netbeans.modules.zeroadmin;

import junit.framework.*;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import org.netbeans.junit.*;

import org.openide.filesystems.*;
import org.netbeans.core.projects.*;
/**
 *
 * @author  David Strupl
 * @version
 */
public class XMLBufferFileSystemTest extends FileSystemFactoryHid {
    XMLBufferFileSystem wxfs;
    /** Creates new JarFileSystemTest */
    public XMLBufferFileSystemTest(Test test) {
        super(test);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTestSuite(RepositoryTestHid.class);
        suite.addTestSuite(FileSystemTestHid.class);
        suite.addTestSuite(FileObjectTestHid.class);
        suite.addTestSuite(AttributesTestHidden.class);
        
        return new XMLBufferFileSystemTest(suite);
    }
    protected void destroyFileSystem(String testName) throws IOException {
        if (wxfs != null) {
            wxfs.removeNotify();// closes root
        }
    }
    
    protected FileSystem[] createFileSystem(String testName, String[] resources) throws IOException{
        java.io.CharArrayWriter xos = new java.io.CharArrayWriter();
        ResourceElement root =  new ResourceElement("");
        
        for (int i = 0; i < resources.length; i++)
            root.add(resources[i]);
        
        PrintWriter pw = new PrintWriter(xos);
//        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE filesystem PUBLIC \"-//NetBeans//DTD Filesystem 1.0//EN\" \"filesystem1_1.dtd\"><filesystem>");
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><filesystem>");
        testStructure(pw,root.getChildren() ,"  ");
        pw.println("</filesystem>");
        pw.close();
        ParseRegen par = new ParseRegen(xos.toCharArray(),
        getClass().getClassLoader().getResource(getClass().getName().replace('.','/')+".class").toString(), false);
        org.w3c.dom.Document doc = par.getDocument();
        if (doc == null) {
            par.getParseException().printStackTrace();
            throw new IOException();
        }
        wxfs = new XMLBufferFileSystem(par);
        
        
        return new FileSystem[] {wxfs};
    }
    
    private  static class ResourceElement {
        String element;
        ResourceElement(String element) {
            //System.out.println(element);
            this.element = element;
        }
        Map children = new HashMap();
        void add(String resource) {
            add(new StringTokenizer(resource,"/"));
        }
        private void add(Enumeration en) {
            //StringTokenizer tokens = StringTokenizer (resource);
            if (en.hasMoreElements()) {
                String chldElem = (String)en.nextElement();
                ResourceElement child = (ResourceElement)children.get(chldElem);
                if (child == null)
                    child = new ResourceElement(chldElem);
                children.put(chldElem,child);
                child.add(en);
            }
        }
        ResourceElement[] getChildren() {
            int i = 0;
            ResourceElement[] retVal =  new ResourceElement[children.entrySet().size()];
            Iterator it = children.entrySet().iterator();
            while (it.hasNext()) {
                retVal[i++] = (ResourceElement)((Map.Entry)it.next()).getValue();
            }
            
            return retVal;
        }
        
        String getName() {
            return element;
        }
    }
    
    private  static void testStructure(PrintWriter pw,ResourceElement[] childern,String tab) {
        for (int i = 0; i < childern.length;i++) {
            ResourceElement[] sub = childern[i].getChildren();
            if (sub.length != 0)
                pw.println(tab+"<folder name=\""+childern[i].getName()+"\">" );
            else
                pw.println(tab+"<file name=\""+childern[i].getName()+"\">" );
            
            testStructure(pw,sub, tab+"  ");
            
            if (sub.length != 0)
                pw.println(tab+"</folder>" );
            else
                pw.println(tab+"</file>" );
        }
    }
    
}
