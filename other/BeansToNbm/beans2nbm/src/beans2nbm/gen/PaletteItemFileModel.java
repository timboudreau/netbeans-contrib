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

/**
 *
 * @author Tim Boudreau
 */
public class PaletteItemFileModel implements FileModel {
    private static final String PI_HEADER="<?xml version=\"1.0\" " +
            "encoding=\"UTF-8\"?>\n<palette_item version=\"1.0\">\n";

    private String path;

    private String libraryName;

    private String className;

    /** Creates a new instance of PaletteItemFileModel */
    public PaletteItemFileModel(String path, String libraryName, String className) {
     this.path = path;
        this.libraryName = libraryName;
        this.className = className;
    }

    public String getPath() {
        return path;
    }

    public void write(OutputStream stream) throws IOException {
        stream.write (toString().getBytes("UTF-8"));
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer (PI_HEADER);
        sb.append ("  <component classname=\"");
        sb.append (className);
        sb.append ("\"/>\n");
        sb.append ("  <classpath>\n");
        sb.append ("    <resource type=\"library\" name=\"");
        sb.append (libraryName);
        sb.append ("\"/>\n");
        sb.append ("  </classpath>\n");
        sb.append ("</palette_item>\n");
        return sb.toString();
    }
    
}
