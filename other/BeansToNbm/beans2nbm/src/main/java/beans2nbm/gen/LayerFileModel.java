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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Tim Boudreau
 */
public class LayerFileModel implements FileModel {

    private final String path;
    private final String basePackageDots;
    private final String paletteFolder;

    /** Creates a new instance of LayerFileModel */
    public LayerFileModel(String path, String paletteFolder, String basePackageDots) {
      this.path = path;
        this.paletteFolder = paletteFolder;
        this.basePackageDots = basePackageDots;
    }

    public String getPath() {
        return path;
    }
    
    private static final String LAYER_HEADER="<?xml version=\"1.0\" " +
            "encoding=\"UTF-8\"?>\n<!DOCTYPE filesystem PUBLIC " +
            "\"-//NetBeans//DTD Filesystem 1.1//EN\" \"http://" +
            "www.netbeans.org/dtds/filesystem-1_1.dtd\">\n" +
            "<filesystem>\n";
    
    public String toString() {
        StringBuffer sb = new StringBuffer(LAYER_HEADER);
        sb.append ("   <folder name=\"FormDesignerPalette\">\n");
        for (Iterator i = folders2paletteEntries.keySet().iterator(); i.hasNext();) {
            String fld = (String) i.next();
            List items = (List) folders2paletteEntries.get(fld);
            sb.append ("        <folder name=\"");
            sb.append (fld);
            sb.append ("\">\n");
            sb.append ("            <attr name=\"SystemFileSystem.localizingBundle\" stringvalue=\""+ basePackageDots + ".Bundle\"/>\n");
            for (Iterator j = items.iterator(); j.hasNext();) {
                String palEntry = (String) j.next();
                sb.append ("            <file name=\"");
                sb.append (palEntry);
                sb.append (".palette_item\" url=\"");
                sb.append (palEntry);
                sb.append ("_paletteItem.xml\"/>\n");
            }
            sb.append ("        </folder>\n");
        }
        sb.append("    </folder>\n");
        sb.append ("    <folder name=\"org-netbeans-api-project-libraries\">\n");
        sb.append ("        <folder name=\"Libraries\">\n");
        for (Iterator i = libraryNames.iterator(); i.hasNext();) {
            String libId = (String) i.next();
            sb.append ("            <file name=\"");
            sb.append (libId);
            sb.append (".xml\" url=\"");
            sb.append (libId);
            sb.append (".xml\">\n");
            sb.append ("                <attr name=\"SystemFileSystem.localizingBundle\" stringvalue=\""+ basePackageDots + ".Bundle\"/>\n");
            sb.append ("            </file>\n");
        }
        sb.append ("        </folder>\n    </folder>\n");
        sb.append ("</filesystem>\n");
        return sb.toString();
    }
    
    private List libraryNames = new ArrayList();
    public void addLibraryName (String libName) {
        libraryNames.add (libName);
    }
    
    private Map folders2paletteEntries = new HashMap();
    public void addBeanEntry (String folder, String filename) {
        List l = (List) folders2paletteEntries.get (folder);
        if (l == null) {
            l = new ArrayList();
            folders2paletteEntries.put (folder, l);
        }
        l.add (filename);
    }
    
    public void write(OutputStream stream) throws IOException {
        stream.write(toString().getBytes("UTF-8"));
    }
    
}
