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
