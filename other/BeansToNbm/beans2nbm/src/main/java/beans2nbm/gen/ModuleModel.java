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
    
    public void setCategory (String s) {
        this.category = s;
    }
    
    String category = "Swing Components";
    private List entries = new ArrayList();
    private List buildEntries() {
        List result = new ArrayList(entries);
        PropertiesFileModel pfm = new PropertiesFileModel (getBasePackageSlashes() + "Bundle.properties");
        result.add (pfm);
        pfm.put("OpenIDE-Module-Display-Name", displayName);
        pfm.put("OpenIDE-Module-Short-Description", description);
        pfm.put("OpenIDE-Module-Display-Category", category);
        pfm.putAll(filesToDisplayNames);
        return result;
    }
}
