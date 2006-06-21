/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package beans2nbm.gen;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Tim Boudreau
 */
public class LibDescriptorModel implements FileModel {

    private final String path;

    private final String jarNameSimple;
    private final String srcJarNameSimple;
    private final String docsJarNameSimple;
    private final String basePackageDots;
    private final String libName;
    
    /** Creates a new instance of LibDescriptorModel */
    public LibDescriptorModel(String path, String libName, String basePackageDots, String jarNameSimple, String srcJarNameSimple, String docsJarNameSimple) {
        this.path = path;
        this.jarNameSimple = jarNameSimple;
        assert path != null;
        assert jarNameSimple != null;
        this.srcJarNameSimple = srcJarNameSimple;
        this.docsJarNameSimple = docsJarNameSimple;
        this.basePackageDots = basePackageDots;
        this.libName = libName;
    }

    public String getPath() {
        return path;
    }

    public void write(OutputStream stream) throws IOException {
        stream.write(toString().getBytes("UTF-8"));
    }
    
    private static final String LIB_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<!DOCTYPE library PUBLIC \"-//NetBeans//DTD Library Declaration 1.0//EN\" " +
    "\"http://www.netbeans.org/dtds/library-declaration-1_0.dtd\">\n";
    
    public String toString() {
        StringBuffer sb = new StringBuffer (LIB_HEADER);
        sb.append ("<library version=\"1.0\">\n");
        sb.append ("  <name>");
        sb.append (libName);
        sb.append("</name>\n");
        sb.append ("  <type>j2se</type>\n");
        sb.append ("  <localizing-bundle>");
        sb.append (basePackageDots);
        sb.append (".Bundle</localizing-bundle>\n");
        sb.append ("    <volume>\n");
        sb.append ("      <type>classpath</type>\n");
        // XXX preferable to pass the module CNB in the host field of the nbinst URL
        sb.append ("      <resource>jar:nbinst:///libs/");
        sb.append (jarNameSimple);
        sb.append ("!/</resource>\n");
        sb.append ("    </volume>\n");
        if (srcJarNameSimple != null && !"".equals(srcJarNameSimple)) {
            sb.append ("    <volume>\n");
            sb.append ("      <type>src</type>\n");
            sb.append ("      <resource>jar:nbinst:///sources/");
            sb.append (srcJarNameSimple);
            sb.append ("!/</resource>\n");
            sb.append ("    </volume>\n");
        }
        if (docsJarNameSimple != null && !"".equals(docsJarNameSimple)) {
            sb.append ("    <volume>\n");
            sb.append ("      <type>javadoc</type>\n");
            sb.append ("      <resource>jar:nbinst:///docs/");
            sb.append (docsJarNameSimple);
            sb.append ("!/</resource>\n");
            sb.append ("    </volume>\n");
        }
        sb.append ("</library>\n");
        return sb.toString();
    }
    
    /*
<library version="1.0">
    <name>TestBeanLib</name>
    <type>j2se</type>
    <localizing-bundle>org.yourorghere.testbeanmodule.Bundle</localizing-bundle>
    <volume>
        <type>classpath</type>
        <resource>jar:nbinst:///libs/TestBean.jar!/</resource>
    </volume>
    <volume>
        <type>src</type>
        <resource>jar:nbinst:///sources/testBeanSource.zip!/</resource>
    </volume>
    <volume>
        <type>javadoc</type>            
        <resource>jar:nbinst:///docs/testBeanJavadoc.zip!/</resource>
    </volume>
</library>
     */
    
}
