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
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.cnd.utils.cache.CharSequenceKey;
import org.openide.util.NotImplementedException;

/**
 * Maintains the correspondence between 
 * unit unique names and their integer identifiers.
 * 
 * Used mostly at the moment unit is opening
 * It is not supposed to be used for the units that are already opened.
 * 
 * @author Vladimir Kvashin
 */
public class UnitIndex {
    
    public static class UnitInfo {
	
	public final int id;
	public final CharSequence name;
	public final String dir;

	public UnitInfo(int id, CharSequence name, String dir) {
	    this.id = id;
	    this.name = name;
	    this.dir = dir;
	}
	
    }
    
    private class Delegate {
	
	private Map<CharSequenceKey, UnitInfo> nameMap = new HashMap<CharSequenceKey, UnitInfo>();
	private Map<Integer, UnitInfo> idMap = new HashMap<Integer, UnitInfo>();
	
        Delegate() {	    
	}
	
	void write() {	    
	}

        public UnitInfo getUnitInfo(int id) {
            return idMap.get(id);
        }

        public UnitInfo getUnitInfo(CharSequence name) {
            return nameMap.get(name);
        }
	
    }
    
    private File persistenceBase;
    private File indexFile;
    private SoftReference<Delegate> delegate;

    public UnitIndex(File persistenceBase) {
	this.persistenceBase = persistenceBase;
	indexFile = new File(persistenceBase, "units"); 
    }
        
    public void startup() {
    }
    
    public void shutdown() {	
    }
    
    private synchronized Delegate getDelegate() {
	Delegate result = null;
	if( delegate != null ) {
	    result = delegate.get();
	}
	if( result == null ) {
	    result = new Delegate();
	    delegate = new SoftReference<Delegate>(result);
	}
	return result;
    }
    
    public UnitInfo getUnitInfo(int id) {
	return getDelegate().getUnitInfo(id);
    }
    
    public UnitInfo getUnitInfo(CharSequence name) {
	return getDelegate().getUnitInfo(name);
    }
}
