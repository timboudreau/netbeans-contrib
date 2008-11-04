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
import java.util.ArrayList;
import java.util.Collection;
import org.netbeans.modules.cnd.persistence.api.Persistence;
import org.netbeans.modules.cnd.persistence.spi.PersistenceListener;
import org.netbeans.modules.cnd.persistence.spi.PersistentObject;
import org.openide.util.NotImplementedException;
/**
 *
 * @author Vladimir Kvashin
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.cnd.persistence.api.Persistence.class)
public class PersistenceImpl extends Persistence {

    private Unit[] units = new Unit[0];
    UnitIndex unitIndex = null;//new UnitIndex();
    
    private Unit getUnit(int unit) {
        return units[unit];
    }
    
    private Collection<Unit> getUnits() {
	Collection<Unit> result = new ArrayList<Unit>();
	for (Unit unit : units) {
	    if( unit != null ) {
		result.add(unit);
	    }
	}
	return result;
    }
    

    public int openUnit(CharSequence unitName) {
	throw new NotImplementedException();
    }
    
    public void closeUnit(int unit, /*boolean cleanPersistence,*/ int[] requiredUnits) {
	throw new NotImplementedException();
    }
    
    public void removeUnit(CharSequence unitName) {
	throw new NotImplementedException();
    }

    public long createKey(int unit) {
	return getUnit(unit).createKey();
    }
        
    public void hang(int unit, long key, PersistentObject obj) {
	getUnit(unit).hang(key, obj);
    }
    
    public void put(int unit, long key, PersistentObject obj) {
	getUnit(unit).put(key, obj);
    }
    
    public PersistentObject get(int unit, long key) {
	return getUnit(unit).get(key);
    }

// I'm not quite sure we really need this:
//    public Persistent tryGet(int unit, long key);

    public void remove(int unit, long key) {
	getUnit(unit).remove(key);
    }
    
    public void debugClear() {
	for (Unit unit : getUnits()) {
	    unit.debugClear();
	}
    }
    
    public void cleanCaches() {
	for (Unit unit : getUnits()) {
	    unit.cleanCaches();
	}
    }
    
    
    public void startup(int persistMechanismVersion) {
	unitIndex.startup();
    }
    
    public void shutdown() {
        unitIndex.shutdown();
    }

    public void registerPersistenceListener(PersistenceListener aListener) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unregisterPersistenceListener(PersistenceListener aListener) {
	throw new UnsupportedOperationException("Not supported yet.");
    }
        
}
