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

import java.io.IOException;
import java.io.Reader;
import org.netbeans.api.diff.Difference;
import org.netbeans.spi.diff.DiffProvider;

/**
 *
 * @author  Martin Entlicher
 */
public class TWBuiltInDiffProvider extends DiffProvider {

    private static final int BUFF_LENGTH = 1024;

    /**
     * Creates a new instance of TWBuiltInDiffProvider
     */
    public TWBuiltInDiffProvider() {
    }


    /**
     * Create the differences of the content two streams.
     * 
     * 
     * @param r1 the first source
     * @param r2 the second source to be compared with the first one.
     * @return the list of differences found, instances of {@link Difference};
     *        or <code>null</code> when some error occured.
     */
    public Difference[] computeDiff(Reader r1, Reader r2) throws IOException {
        TWLineIndexedAccess l1 = new TWLineIndexedAccess(r1);
        TWLineIndexedAccess l2 = new TWLineIndexedAccess(r2);
        return TWLineDiff.diff(l1, l2);
    }
    
}
