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
package beans2nbm.gen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 *
 * @author Tim Boudreau
 */
public class ModuleModel implements FileModel {
    private final String displayName;
    private final String codeName;
    private final String description;
    private final String version;
    private final String minJDK;

    private final String moduleJarPath;
    
    /** Creates a new instance of ModuleModel */
    public ModuleModel(String moduleJarPath, String codeName, String description, String version, String displayName, String minJDK) {
        this.codeName = codeName;
        this.description = description;
        this.version = version;
        this.displayName = displayName;
        this.moduleJarPath = moduleJarPath;
        this.minJDK = minJDK;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCodeName() {
        return codeName;
    }

    public String getPath() {
        return moduleJarPath;
    }

    public void write(OutputStream stream) throws IOException {
        Manifest manifest = getManifest();
        List l = buildEntries();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JarOutputStream jos = new JarOutputStream (baos, manifest);
        jos.setLevel(0);
//        jos.putNextEntry(new JarEntry ("META-INF/MANIFEST.MF"));
//        manifest.write(jos);
//        jos.closeEntry();
        try {
            for (Iterator i = l.iterator(); i.hasNext();) {
                FileModel file = (FileModel) i.next();
                JarEntry entry = new JarEntry (file.getPath());
                jos.putNextEntry(entry);
                file.write(jos);
                jos.closeEntry();
            }
        } finally {
            jos.flush();
            jos.close();
        }
        stream.write(baos.toByteArray());
    }
    
    private Manifest getManifest() {
        Manifest m = new Manifest();
        Attributes atts = m.getMainAttributes();
        atts.put (Attributes.Name.MANIFEST_VERSION, "1.0");
        atts.putValue("OpenIDE-Module", codeName);
        atts.putValue("OpenIDE-Module-Public-Packages", "-");
        atts.putValue("OpenIDE-Module-Specification-Version", version);
        atts.putValue("OpenIDE-Module-Localizing-Bundle", getBasePackageSlashes() + "Bundle.properties");
        atts.putValue("OpenIDE-Module-Layer", getBasePackageSlashes() + "layer.xml");
        atts.putValue("OpenIDE-Module-Requires", "org.openide.modules.ModuleFormat1");
        if (!"1.4".equals(minJDK)) {
            atts.putValue("OpenIDE-Module-Java-Dependencies",
                    "Java > " + minJDK + ", VM > 1.0");
        }
        return m;
    }

    public String getManifestXML() {
        StringBuffer sb = new StringBuffer("<manifest OpenIDE-Module=\"");
        sb.append (codeName);
        sb.append ("\"\n");
        sb.append ("OpenIDE-Module-Implementation-Version=\"1\"\n");
        sb.append ("OpenIDE-Module-Name=\"");
        sb.append (displayName);
        sb.append ("\"\n");
        sb.append ("OpenIDE-Module-Requires=\"org.openide.modules.ModuleFormat1\"\n");
        sb.append ("OpenIDE-Module-Specification-Version=\"");
        sb.append (version);
        sb.append ("\"\n");
        if (!"1.4".equals(minJDK)) {
            sb.append("OpenIDE-Module-Java-Dependencies=\"");
            sb.append("Java &gt; " + minJDK + ", VM &gt; 1.0\"\n");
        }
        sb.append ("/>\n");
        return sb.toString();
    }
    
    private String getBasePackageSlashes() {
        StringBuffer sb = new StringBuffer (codeName);
        for (int i=0; i < sb.length(); i++) {
            if (sb.charAt(i) == '.') {
                sb.setCharAt(i, '/');
            }
        }
        if (!sb.toString().endsWith ("/")) {
            sb.append ('/');
        }
        return sb.toString();
    }
    
    private Map filesToDisplayNames = new HashMap();
    public void addFileDisplayName (String pathInJar, String displayName) {
        filesToDisplayNames.put (pathInJar, displayName);
    }
    
    public void addFileEntry (FileModel fi) {
        entries.add (fi);
    }
    
    private List entries = new ArrayList();
    private List buildEntries() {
        List result = new ArrayList(entries);
        PropertiesFileModel pfm = new PropertiesFileModel (getBasePackageSlashes() + "Bundle.properties");
        result.add (pfm);
        pfm.put("OpenIDE-Module-Display-Name", displayName);
        pfm.put("OpenIDE-Module-Short-Description", description);
        pfm.put("OpenIDE-Module-Display-Category", "Swing Components");
        pfm.putAll(filesToDisplayNames);
        return result;
    }
}
