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
public class ModuleXMLModel implements FileModel {
    private final String moduleName;
    private final String version;
    
    /** Creates a new instance of ModuleXMLModel */
    public ModuleXMLModel(String moduleNameDots, String version) {
        this.moduleName = moduleNameDots;
        this.version = version;
    }
    
    private String moduleNameDashes() {
        char[] c = moduleName.toCharArray();
        for (int i=0; i < c.length; i++) {
            if (c[i] == '.') {
                c[i] = '-';
            }
        }
        return new String (c);
    }
    
    private static final String MXML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
          "<!DOCTYPE module PUBLIC \"-//NetBeans//DTD Module Status 1.0//EN\"" +
                        " \"http://www.netbeans.org/dtds/module-status-1_0.dtd\">\n";
    /*
<module name="org.yourorghere.testbeanmodule">
    <param name="autoload">false</param>
    <param name="eager">false</param>
    <param name="enabled">true</param>
    <param name="jar">modules/org-yourorghere-testbeanmodule.jar</param>
    <param name="reloadable">false</param>
    <param name="specversion">1.0</param>
</module>
     */

    public String getPath() {
        return "netbeans/config/Modules/" + moduleNameDashes() + ".xml";
    }

    public void write(OutputStream stream) throws IOException {
        stream.write (toString().getBytes("UTF-8"));
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer (MXML_HEADER);
        buf.append("<module name=\"");
        String indent = "    ";
        buf.append (moduleName);
        buf.append ("\">\n");
        buf.append (indent);
        buf.append ("<param name=\"eager\">false</param>\"\n");
        buf.append (indent);
        buf.append ("<param name=\"enabled\">true</param>\n");
        buf.append (indent);
        buf.append ("<param name=\"jar\">modules/");
        buf.append (moduleNameDashes());
        buf.append (".jar</param>\n");
        buf.append (indent);
        buf.append ("<param name=\"reloadable\">false</param>\n");
        buf.append (indent);
        buf.append ("<param name=\"specversion\">" + version + "</param>\n");
        buf.append ("</module>\n");
        return buf.toString();
    }
}
