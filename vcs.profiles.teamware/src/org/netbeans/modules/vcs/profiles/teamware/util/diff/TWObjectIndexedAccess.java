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
