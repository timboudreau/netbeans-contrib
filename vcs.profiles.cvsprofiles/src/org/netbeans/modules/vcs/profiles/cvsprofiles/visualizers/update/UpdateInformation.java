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
package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update;

import java.io.*;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.FileInfoContainer;

/**
 * Describes checkout/tag/update information for a file.
 * The fields in instances of this object are populated by response handlers.
 *
 * @author  Thomas Singer
 */
public class UpdateInformation extends FileInfoContainer implements Serializable {

    public static final String PERTINENT_STATE = "Y"; //NOI18N
    public static final String MERGED_FILE = "G"; //NOI18N
    private File file;

    private String type;

    private static final long serialVersionUID = 1L;

    public UpdateInformation() {
    }

    /**
     * Returns the associated file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns true if the associated file is a directory.
     */
    public boolean isDirectory() {
        File file = getFile();
        if (file == null) {
            return false;
        }
        return file.isDirectory();
    }

    /**
     * Sets the associated file.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Returns the type.
     * Mostly the type value equals to the states returned by update and tag command.
     * see description in cvs manual.
     * Some states are added:
     *   G - file was merged (when using the cvs update -j <rev> <file> command.
     *   D - file was deleted - no longer pertinent.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Return a string representation of this object. Useful for debugging.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(type);
        buffer.append("  "); //NOI18N
        if (isDirectory()) {
            buffer.append("Directory "); //NOI18N
        }
        buffer.append(file != null ? file.getAbsolutePath()
                      : "null"); //NOI18N
        return buffer.toString();
    }
}
