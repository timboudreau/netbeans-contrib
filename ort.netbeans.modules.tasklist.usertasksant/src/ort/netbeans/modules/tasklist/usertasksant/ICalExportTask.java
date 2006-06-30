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

package ort.netbeans.modules.tasklist.usertasksant;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.netbeans.modules.tasklist.usertasks.translators.ICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.XmlExportFormat;


/**
 * Export .ics files as XML, HTML or plain text.
 *
 * @author tl
 */
public class ICalExportTask extends Task {
    private File in, out;
    private String format;

    /**
     * Creates a new instance of ICalExportTask.
     */
    public ICalExportTask() {
    }

    public File getIn() {
        return in;
    }

    public void setIn(File in) {
        this.in = in;
    }

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void execute() throws BuildException {
        if ("xml".equals(format)) {
            XmlExportFormat ef = new XmlExportFormat();
            // ef.createXml()
        } else if ("html/effort".equals(format)) {
            // TODO
        } else if ("plain".equals(format)) {
            // TODO
        } else {
            throw new BuildException("Unknown export format: " + format);
        }
    }
}
