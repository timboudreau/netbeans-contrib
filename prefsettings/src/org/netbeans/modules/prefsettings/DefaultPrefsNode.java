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
package org.netbeans.modules.prefsettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.netbeans.modules.prefsettings.api.PrefsNode;
import org.openide.ErrorManager;
/**
 *
 * @author Timothy Boudreau
 */
final class DefaultPrefsNode extends PrefsNode {
    private PrefsDataObject ob;
    private static final String TYPE = ".type"; //NOI18N
    private static final String POSSIBLE_VALUES = ".possibleValues"; //NOI18N
    private static final String INT = "int"; //NOI18N
    private static final String STRING = "string"; //NOI18N
    public static final String BOOL = "boolean"; //NOI18N
    public DefaultPrefsNode(PrefsDataObject obj) {
        super (obj);
        //Yes, we're keeping two copies of the field obj, one in the superclass,
        //one here; otherwise we'd have to expose API 
        //for this which the user would never need
        this.ob = obj;
    }
    
    protected final void initProperties() {
        List props = new ArrayList(7);
        for (Enumeration en = ob.getPrimaryFile().getAttributes(); en.hasMoreElements();) {
            String attr = (String) en.nextElement();
            if (!PrefsDataObject.KEY_BUNDLE.equals(attr) && 
                !PrefsDataObject.KEY_HELPCTX.equals(attr) && 
                !PrefsDataObject.KEY_NODE_CLASS.equals(attr) && 
                !PrefsDataObject.KEY_ICON.equals(attr) && 
                !attr.endsWith(TYPE) &&
                !attr.endsWith(POSSIBLE_VALUES)) {
                
                props.add (attr);
            }
        }
        //Use alpha sorting - users can always be clever with property
        //names to do effective sort orders
        Collections.sort(props);
        for (Iterator it=props.iterator(); it.hasNext();) {

                String prefsKey = (String) it.next();
                String defValue = (String) ob.getPrimaryFile().getAttribute(prefsKey);
                if (defValue == null) {
                    ErrorManager.getDefault().notify(new IllegalArgumentException ("Default value not specified in layer for " + prefsKey));
                    continue;
                }
                Class clazz = null;
                String typeStr = (String) ob.getPrimaryFile().getAttribute(prefsKey + TYPE);
                if (typeStr != null) {
                    if (INT.equals(typeStr)) {
                        clazz = Integer.TYPE;
                    } else if (BOOL.equals(typeStr)) {
                        clazz = Boolean.TYPE;
                    } else if (!STRING.equals(typeStr)) {
                        ErrorManager.getDefault().notify(new IllegalArgumentException("Unsupported type for " + prefsKey + ":" + typeStr));
                        continue;
                    }
                }
                String pvalStr = (String) ob.getPrimaryFile().getAttribute(prefsKey + POSSIBLE_VALUES);
                String[] possibleValues = null;
                if (pvalStr != null) {
                    StringTokenizer tok = new StringTokenizer (pvalStr, ","); //NOI18N
                    possibleValues = new String[tok.countTokens()];
                    for (int i=0; i < possibleValues.length; i++) {
                        possibleValues[i] = tok.nextToken().intern();
                    }
                    addProperty (prefsKey, null, defValue, possibleValues);
                    continue;
                }
                addProperty(prefsKey, null, defValue, clazz);
        }
    }
}
