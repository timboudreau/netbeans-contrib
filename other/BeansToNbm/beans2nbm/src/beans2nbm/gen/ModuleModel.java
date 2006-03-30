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

import java.io.BufferedOutputStream;
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

    private final String moduleJarPath;
    
    /** Creates a new instance of ModuleModel */
    public ModuleModel(String moduleJarPath, String codeName, String description, String version, String displayName) {
        this.codeName = codeName;
        this.description = description;
        this.version = version;
        this.displayName = displayName;
        this.moduleJarPath = moduleJarPath;
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
