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
/*
 * PersistentCache.java
 *
 * Created on October 31, 2004, 1:04 PM
 */

package org.netbeans.api.cache;
import java.io.IOException;

/**
 * A Cache with persistence semantics.
 *
 * @see Cache
 * @author Tim Boudreau
 */
public abstract class PersistentCache extends Cache {

    protected PersistentCache() {
    }

    /**
     * Close the cache, saving any unsaved date.  <b>Very important:</b>
     * call this method when you're done with a Cache instance, or it can
     * be in an inconsistent state on restart.
     */
    public abstract void close() throws IOException;
    
    /**
     * Delete a cache.  No read or write methods should be called on a 
     * Cache instance after calling this method.
     */
    public abstract void delete() throws IOException;
    
    /**
     * Determine whether disk storage for the cache exists.
     */
    public abstract boolean exists();
    
    /**
     * Get the last write date of this cache.  Returns Long.MIN_VALUE if
     * exists() == false.
     */
    public abstract long getLastModified();
    
    /**
     * Rename this cache, renaming its data files, possibly creating
     * directories to fulfil the request.  The passed name may contain
     * the system file separator character.
     */
    public abstract void rename (String newName) throws IOException;
}
