/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * SharedInputStream.java
 *
 * Created on October 31, 2004, 1:16 AM
 */

package org.netbeans.api.cache;
import java.io.*;
import java.nio.*;

/**
 * Input stream class returned by <code>Cache.getInputStream</code>
 *
 * @see Cache
 * @author Tim Boudreau
 */
public abstract class CacheInputStream extends InputStream {
    /**
     * Get an input stream that is a sub-stream of the backing data for
     * this stream.
     */
    public abstract InputStream newStream(long start, long end);
    /**
     * Get a ByteBuffer that represents the backing data for this stream.
     */
    public abstract ByteBuffer getBuffer();
}
