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
