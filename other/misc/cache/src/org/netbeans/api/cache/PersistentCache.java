/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
