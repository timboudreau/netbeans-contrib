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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.persistence.impl;

import java.io.File;
import org.netbeans.modules.cnd.persistence.spi.PersistentObject;

/**
 * A single repository unit
 * @author Vladimir Kvashin
 */
public abstract class Unit {

    private final int id;
    private final CharSequence name;
    private final File baseDir;
    
    private static long nextKey = 0;

    public Unit(int id, CharSequence name, File baseDir) {
	this.id = id;
	this.name = name;
	this.baseDir = baseDir;
    }
    
    int getId() {
	return id;
    }
    
    CharSequence getName() {
	return name;
    }
    
    public long createKey() {
	return nextKey++;
    }
    
    /**
     * Store the object in the in-memory cache, 
     * either to register object for later put
     * or to store temporary inpersistable object 
     * @param key the key
     * @param obj the object to store
     */
    abstract void hang(long key, PersistentObject obj);
    
    /**
     * Store object
     * @param key the key
     * @param obj the object to store
     */
    abstract void put(long key, PersistentObject obj);
    
    /**
     * Retrieve the object
     * @param key the key of object to get
     * @return an object corresponding to key or null if there is no such one
     */
    abstract PersistentObject get(long key);

// I'm not quite sure we really need this:
//    /**
//     * retrieve object if it resides in memory cache; 
//     * @param key the key of object to get
//     * @return an object corresponding to key or null if there is no such object in memory cache
//     */
//    abstract Persistent tryGet(long key);

    /**
     * stop storing object
     * @param key the key of object to remove
     */
    abstract void remove(long key); 
    
    /**
     * store all objects to permanent location 
     * should be called during IDE shutdown 
     */
    abstract void debugClear();
    
    /**
     * clean the disk caches of all repositories
     */
    abstract void cleanCaches();
       
    
    /** Closes the given unit */
    abstract void close();
}
