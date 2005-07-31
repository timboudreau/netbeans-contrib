/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
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
