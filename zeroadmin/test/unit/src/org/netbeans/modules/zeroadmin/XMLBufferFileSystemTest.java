/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
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
