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

/**
 * An abstract radnom access to an object pool.
 *
 * @author  Martin Entlicher
 */
public abstract class TWObjectIndexedAccess {
    
    public abstract Object readAt(long pos) throws IOException;
    
    public abstract void readFullyAt(long pos, Object obj[]) throws IOException;

    public abstract Object[] readFullyAt(long pos, long length) throws IOException;
    
    public abstract long length();
    
}
