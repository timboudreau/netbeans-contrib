
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */


package com.sun.tthub.gde.util;

import com.sun.tthub.gde.logic.GDEClassMngmtException;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

import java.io.*;
import java.util.zip.*;

/**
 * JarResources maps all resources included in a Zip or Jar file.
 * Additionaly, it provides a method to extract one as a blob.
 *
 * <strong>Example</strong>
 * Let's say you have a JAR file which jarred up a bunch of gif image
 * files. Now, by using JarResources, you could extract, create, and display
 * those images on-the-fly.
 * <pre>
 *     ...
 *     JarResources JR=new JarResources("GifBundle.jar");
 *     Image image=Toolkit.createImage(JR.getResource("logo.gif");
 *     Image logo=Toolkit.getDefaultToolkit().createImage(
 *                   JR.getResources("logo.gif")
 *                   );
 *     ...
 * </pre>
 *
 * @author Hareesh Ravindran
 */
public final class JarResources {
        
    private Map sizesMap = new HashMap();
    private Map jarContentsMap = new HashMap();
    private String jarFileName;
    
    
    /**
     * creates a JarResources. It extracts all resources from a Jar
     * into an internal hashtable, keyed by resource names.
     *
     * @param jarFileName a jar or zip file
     */
    public JarResources(String jarFileName) throws GDEClassMngmtException {
        this.jarFileName = jarFileName;
        init();
    }
    
    /** 
     * The default constructor. If this consturctor is used, the client should
     * call the inti() method separately.
     */    
    public JarResources() {}
    
    public void setJarFileName(String jarFileName) 
                    { this.jarFileName = jarFileName; }
    
    /**
     * Extracts a jar resource as a blob.
     * @param name a resource name.
     */
    public byte[] getResource(String name) {
        return (byte[])jarContentsMap.get(name);
    }
    
    /**
     * Initializes internal hash maps with Jar file resources. The function 
     * loads each of the entry in the jar file as a byte array and will store
     * it in the internal map so that when the getResource() method is called,
     * the jar file need not be scanned again.
     * 
     * @throws com.sun.tthub.gde.util.GGDEClassMngmtException if the specified
     *      jar file is not found, or if the jar file is not properly zipped 
     *      (i.e. it is not in the jar file format) or if there is an IOException
     *      while reading from the jar file.
     */
    public void init() throws GDEClassMngmtException {
        // Clear both the internal hash maps.
        sizesMap.clear();
        jarContentsMap.clear();
        
        try {
            // extracts just sizes only.
            ZipFile zf = new ZipFile(jarFileName);
            Enumeration e = zf.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze = (ZipEntry) e.nextElement();
                sizesMap.put(ze.getName(), new Integer((int) ze.getSize()));
            }
            zf.close();
            
            // extract resources and put them into the hashtable.
            FileInputStream fis = new FileInputStream(jarFileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ZipInputStream zis = new ZipInputStream(bis);
            ZipEntry ze = null;
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.isDirectory()) { continue; }                
                int size = (int) ze.getSize();
                // -1 means unknown size.
                if (size == -1) {
                    size = ((Integer) sizesMap.get(ze.getName())).intValue();
                }
                byte[] bytes = new byte[(int)size];
                int rb = 0;
                int chunk = 0;
                while (((int)size - rb) > 0) {
                    chunk = zis.read(bytes, rb, size - rb);
                    if (chunk == -1) { break; }
                    rb += chunk;
                }
                // add to internal resource hash map.
                jarContentsMap.put(ze.getName(), bytes);
            }            
        } catch (ZipException ex) {
            throw new GDEClassMngmtException("A zip file format error has " +
                    "occured. Check if the file is jarred properly", ex);
        } catch (FileNotFoundException ex) {
            throw new GDEClassMngmtException("The specified zip file '" +
                    jarFileName + "' is not found or is not readable.", ex);
        } catch (IOException ex) {
            throw new GDEClassMngmtException("Error loading the entries from" +
                    "the jar file" + ex);
        }
    }
         
    /**
     * Dumps a zip entry into a string. Can be used for debugging purposes.
     * The string returned will contain the following details about the zip
     * entry: 1. Whether the zip entry is a folder/file, 2. If the zip entry is
     * compressed or not, 3. The name of the zip entry, 4, Size of the zip entry,
     * 5. The compressed size of the zip entry if the zip entry is compressed.
     *
     * @param ze a ZipEntry
     */
    private String dumpZipEntry(ZipEntry ze) {
        StringBuffer sb = new StringBuffer();
        if (ze.isDirectory()) {
            sb.append("d ");
        } else {
            sb.append("f ");
        }
        if (ze.getMethod() == ZipEntry.STORED) {
            sb.append("stored   ");
        } else {
            sb.append("deflated ");
        }
        sb.append(ze.getName());
        sb.append("\t");
        sb.append("" + ze.getSize());
        if (ze.getMethod() == ZipEntry.DEFLATED) {
            sb.append("/" + ze.getCompressedSize());
        }
        return (sb.toString());
    }    
}
