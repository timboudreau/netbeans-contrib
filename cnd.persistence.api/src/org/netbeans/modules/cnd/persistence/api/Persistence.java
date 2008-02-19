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

package org.netbeans.modules.cnd.persistence.api;

import org.netbeans.modules.cnd.persistence.spi.PersistenceListener;
import org.netbeans.modules.cnd.persistence.spi.PersistentObject;
import org.openide.util.Lookup;

/**
 * The core class that provides persistence functionality.
 * It allows to store and retrieve objects by keys.
 * 
 * The persistence is (internally) divided into units.
 * Typically, each unit corresponds the IDE project -
 * though it's up to client how to interpret and use the unit.
 * 
 * The unit is identified by its integer ID.
 * The object withi unit is identified via its long key
 * So the full key is the pair of (int unitID, long keyWithinUnit)
 * 
 * The Unit could be extracted into a separate interface,
 * but I believe it would be less convenient - the client would have to use
 * longer chains Persistence.getDefault().getUnit(unitId).get(objectId); 
 * the code above raises the question, whether the Unit can be null, etc. -
 * while unit itself is ot of interest for a client.
 * 
 * @author Sergey Grinev
 * @author Vladimir Kvashin
 */
public abstract class Persistence {

    private static Persistence instance;
    private static final Object lock = new Object();
    
    /**
     * Gets the Persistence implementation.
     * Note that it does not use the common EMPTY pattern,
     * because for Persistence, it does not make sense
     */
    public static Persistence getDefault() {
        if( instance == null ) {
	    synchronized (lock) {
		if( instance == null ) {
		    instance = Lookup.getDefault().lookup(Persistence.class);
		}
	    }
	}
	return instance;
    }
    

    /**
     * Opens persistence unit
     * @param unitName the unique identifier of the unit to open
     * @return a unit identifier
     */
    public abstract int openUnit(CharSequence unitName);
    
    /**
     * Close Persistence Unit, e.g. Project for IDE
     * @param unit the unit ID
     */
    public abstract void closeUnit(int unit, /*boolean cleanPersistence,*/ int[] requiredUnits);
    
    /**
     * Removes persistence unit from disk
     */
    public abstract void removeUnit(CharSequence unitName);  
    
    
    /**
     * Creates a new key within the given unit
     */
    public abstract long createKey(int unit);
    
    /**
     * Store the object in the in-memory cache, 
     * either to register object for later put
     * or to store temporary inpersistable object 
     * @param unit identifies the unit
     * @param key the key
     * @param obj the object to store
     */
    public abstract void hang(int unit, long key, PersistentObject obj);
    
    /**
     * Store object
     * @param unit identifies the unit
     * @param key the key
     * @param obj the object to store
     */
    public abstract void put(int unit, long key, PersistentObject obj);
    
    /**
     * Retrieve the object
     * @param unit identifies the unit
     * @param key the key of object to get
     * @return an object corresponding to key or null if there is no such one
     */
    public abstract PersistentObject get(int unit, long key);

// I'm not quite sure we really need this:
//    /**
//     * retrieve object if it resides in memory cache; 
//     * @param unit identifies the unit
//     * @param key the key of object to get
//     * @return an object corresponding to key or null if there is no such object in memory cache
//     */
//    abstract Persistent tryGet(int unit, long key);

    /**
     * stop storing object
     * @param unit identifies the unit
     * @param key the key of object to remove
     */
    public abstract void remove(int unit, long key); 
    
    /**
     * store all objects to permanent location 
     * should be called during IDE shutdown 
     */
    public abstract void debugClear();
    
    /**
     * clean the disk caches of all repositories
     */
    public abstract void cleanCaches();
    
    
    /**
     * Prepare persistence and tells the version of the persistent mechanism.
     * The idea behind that is that it's PersistenceFactory implementation,
     * which is responsible for data structures; as soon as the structures change,
     * the version is incremented. Upo startup, the version stored in persistence
     * is compared with the one passed a parameter. If they differ, this mean
     * that the persistence data was created by another version and should
     * be immediately discarded.
     * 
     * @param verison 
     */
    public abstract void startup(int persistMechanismVersion);
    
    /**
     * Shuts down persistence.
     * Should be called during application shutdown 
     */
    public abstract void shutdown();
    

    /**
     * add a listener to the persistence
     * @param aListener the listener
     */
    public abstract void registerPersistenceListener(PersistenceListener aListener);
    
    /**
     * remove a listener from the persistence
     * @param aListener the listener
     */
    public abstract void unregisterPersistenceListener(PersistenceListener aListener);
    
}
