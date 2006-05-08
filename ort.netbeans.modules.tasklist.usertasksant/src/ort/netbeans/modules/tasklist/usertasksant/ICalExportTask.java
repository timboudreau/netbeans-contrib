/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
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
