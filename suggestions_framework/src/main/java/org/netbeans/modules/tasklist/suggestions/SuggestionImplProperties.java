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

import org.netbeans.modules.tasklist.core.TaskProperties;
import org.netbeans.modules.tasklist.filter.SuggestionProperty;

/**
 *
 * This class is coupled to SuggestionImpl, but defined outside just
 * for code readability.
 */
public class SuggestionImplProperties extends TaskProperties   {

    public static final String PROPID_LINE_NUMBER = "line";
    public static final String PROPID_FILENAME = "file";
    public static final String PROPID_LOCATION = "location";
    public static final String PROPID_CATEGORY = "category";
    
    
    public static SuggestionProperty getProperty(String propID) {
        if (propID.equals(PROPID_LINE_NUMBER)) { return PROP_LINE_NUMBER;}
        else if (propID.equals(PROPID_FILENAME)) { return PROP_FILENAME;}
        else if (propID.equals(PROPID_LOCATION)) { return PROP_LOCATION;}
        else if (propID.equals(PROPID_CATEGORY)) { return PROP_CATEGORY;}
        else return TaskProperties.getProperty(propID);
    }
    
    public static final SuggestionProperty PROP_LINE_NUMBER =
    new SuggestionProperty(PROPID_LINE_NUMBER, Integer.class) {
        public Object getValue(Object obj) {return new Integer(((SuggestionImpl) obj).getLineNumber()); }
    };
    
    public static final SuggestionProperty PROP_FILENAME =
    new SuggestionProperty(PROPID_FILENAME, String.class) {
        public Object getValue(Object obj) {return ((SuggestionImpl) obj).getFileBaseName(); }
    };
    
    public static final SuggestionProperty PROP_LOCATION =
    new SuggestionProperty(PROPID_LOCATION, String.class) {
        public Object getValue(Object obj) {return ((SuggestionImpl) obj).getLocation(); }
    };
    
    public static final SuggestionProperty PROP_CATEGORY =
    new SuggestionProperty(PROPID_CATEGORY, String.class) {
        public Object getValue(Object obj) {return ((SuggestionImpl)obj).getCategory(); }
    };
    
}

