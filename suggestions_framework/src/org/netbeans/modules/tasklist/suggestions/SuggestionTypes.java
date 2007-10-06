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

package org.netbeans.modules.tasklist.suggestions;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;

/**
 * Registry of all suggestion types. This is singleton and it also keeps
 * the settings whether the suggestion types which are not active are
 * drawn on the background, whther the combining of suggestions is
 * turned on or off etc. These settings are shared by all views.
 *
 * Based on AnnotationTypes.java in the editor package.
 * <p>
 *
 * @author Tor Norbye, David Konecny
 */

final public class SuggestionTypes {
    /** Static map containing all suggestion types:
        suggestion_name <-> suggestion_type */
    private Map allTypes = null;
    
    /** Flag whether the suggestion types were initialized or not */
    private boolean initializedTypes = false;

    /** Single instance of this class */
    private static SuggestionTypes suggestionTypes = null;
    
    private SuggestionTypes() {
    }

    /**  Returns instance of SuggestionTypes singleton. */
    public static SuggestionTypes getDefault() {
        if (suggestionTypes == null) {
            suggestionTypes = new SuggestionTypes();
        }
        return suggestionTypes;
    }
    
    /** Initialize the map of all suggestion types
     * @param map map containing all suggestion types */    
    final void setTypes(Map map) {
        allTypes = map;
        initializedTypes = map != null;
    }

    public final int getCount() {
        loadTypes();
        if (allTypes == null) {
            return 0;
        } else {
            return allTypes.size();
        }
    }
    
    /** Returns SuggestionType instance for the given name of the type
     * @param name suggestion type name
     * @return instance describing suggestion type */    
    public final SuggestionType getType(String name) {
        loadTypes();
        
        if (allTypes == null)
            return null;
        
        return (SuggestionType)allTypes.get(name);
    }

    /** Returns a collection containing all the registered SuggestionTypes.
     * @return collection containing all registered types */    
    public final Collection getAllTypes() {
        loadTypes();
        
        if (allTypes == null)
            return null;
        
        return allTypes.values();
    }

    /** Check if the types were loaded and load them if not */
    private void loadTypes() {
        if (initializedTypes)
            return;

        SuggestionTypesFolder.getSuggestionTypesFolder();

        initializedTypes = true;        
    }
}
