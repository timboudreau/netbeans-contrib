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
package org.netbeans.modules.searchandreplace.model;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

/**
 * Represents a search item that cannot be replaced due to some IO or other
 * issue.
 *
 * @author Tim Boudreau
 */
public class Problem extends Item {
    private final String msg;
    /** Creates a new instance of Problem */
    public Problem(File f, IOException ioe) {
        super (f);
        this.msg = ioe.getLocalizedMessage();
    }

    public Point getLocation() {
        return new Point();
    }

    public void replace() throws IOException {
        throw new IOException ("Can't replace a problem");
    }

    public boolean isValid() {
        return false;
    }

    String getText() throws IOException {
        return msg;
    }

    public String getDescription() {
        return msg;
    }

    public void setShouldReplace (boolean replace) {
        throw new UnsupportedOperationException();
    }
}
