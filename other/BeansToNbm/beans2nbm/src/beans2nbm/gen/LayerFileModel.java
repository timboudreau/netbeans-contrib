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
