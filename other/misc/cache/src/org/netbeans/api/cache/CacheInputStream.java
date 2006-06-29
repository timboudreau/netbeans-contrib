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
