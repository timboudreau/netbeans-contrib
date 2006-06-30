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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.teamware.util.diff;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Random access to an array of lines.
 *
 * @author  Martin Entlicher
 */
public class TWLineIndexedAccess extends TWObjectIndexedAccess {

    private static final int BUFF_LENGTH = 1024;

    private List lines;

    /**
     * Creates a new instance of TWLineIndexedAccess
     *
     * @param r The reader to read lines from.
     */
    public TWLineIndexedAccess(Reader r) throws IOException {
        lines = new ArrayList();
        try {
            initLines(r);
        } finally {
            r.close();
        }
    }
    
    private void initLines(Reader r) throws IOException {
        char[] buffer = new char[BUFF_LENGTH];
        int length;
        StringBuffer lineBuff = new StringBuffer();
        while((length = r.read(buffer)) > 0) {
            for (int i = 0; i < length; i++) {
                if (buffer[i] == '\n') {
                    lines.add(lineBuff.toString());
                    lineBuff.delete(0, lineBuff.length());
                } else {
                    lineBuff.append(buffer[i]);
                }
            }
        }
        if (lineBuff.length() > 0) {
            lines.add(lineBuff.toString());
        }
    }
    
    public long length() {
        return lines.size();
    }
    
    public Object readAt(long pos) throws IOException {
        return lines.get((int) pos);
    }
    
    public Object[] readFullyAt(long pos, long length) throws IOException {
        String[] subLines = new String[(int) length];
        for (int i = 0; i < length; i++, pos++) {
            if (pos < length()) {
                subLines[i] = (String) lines.get((int) pos);
            } else {
                throw new EOFException(pos+" >= "+length());
            }
        }
        return subLines;
    }
    
    public void readFullyAt(long pos, Object[] obj) throws IOException {
        for (int i = 0; i < obj.length; i++, pos++) {
            if (pos < length()) {
                obj[i] = lines.get((int) pos);
            } else {
                throw new EOFException(pos+" >= "+length());
            }
        }
    }
    
}
