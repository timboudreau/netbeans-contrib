/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.lib.apicheck;

import java.io.File;
import org.apache.tools.ant.BuildException;

/** Takes a JAR files and generates a golden file listing all its
 * visible methods and fields. Can also compare a give JAR file
 * to the golden file and generate list of compatible/non compatible
 * changes.
 *
 * @author Jaroslav Tulach
 */
public class SignatureCheck extends org.apache.tools.ant.Task {
    /** jar file to check */
    private File jar;
    /** output golden file */
    private File goldenOut;
    /** input golden file */
    private File goldenIn;
    
    
    
    /** api created from the jar file */
    private API jarAPI;
    
    public SignatureCheck () {
    }
    
    
    public void setJar (File f) {
        jar = f;
    }
    public void setOutput (File f) {
        goldenOut = f;
    }
    public void setInput (File f) {
        goldenIn = f;
    }

    public void execute () throws org.apache.tools.ant.BuildException {
        if (goldenOut != null) {
            generateOut ();
        }
        if (goldenIn != null) {
            
        }
        if (jar != null) {
            try {
                jarAPI = API.readJARFile (jar);
            } catch (java.io.IOException ex) {
                throw new BuildException (ex);
            }
        }
    }
    
    private void generateOut () throws BuildException {
    }
    
    final java.util.Set getIncompatibleChanges () {
        return java.util.Collections.EMPTY_SET;
    }
}
