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

package org.netbeans.modules.ant.moduleinfotask;

import java.io.File;

import org.apache.tools.ant.*;

/**
 * This is a special ant task for generating an html document with modules
 * dependencies crossreference.
 * 
 * @author Sandip Chitale
 */
public class ModuleInfoTask extends Task {
    
    private File htmlFile;
    public void setHtmlOutputFile(File f) {
        htmlFile = f;
    }
    
    public void execute() throws BuildException {        
        if (htmlFile == null) {
            throw new BuildException("Please specify the html output file.");
        }
        log("Generating module info to " + htmlFile.getAbsolutePath());
        ModuleInfoGenerator.generateHTML(htmlFile);
        log("Generating module info....Done.");
    }        
}