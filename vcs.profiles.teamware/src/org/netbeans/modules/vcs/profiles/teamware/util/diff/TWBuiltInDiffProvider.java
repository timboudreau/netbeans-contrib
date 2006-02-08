/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
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
