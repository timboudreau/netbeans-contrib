/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi;

import java.beans.*;
import java.util.ResourceBundle;

import org.openide.util.NbBundle;

/** Version constants.
 *
 * @author  mryzl
 * @version 
 */
public class Version {

    /** Default. */
    public static final int DEFAULT = 0;
    /** Compatible. */
    public static final int VCOMPAT = 1;
    /* Version 1.1 */
    public static final int V1_1 = 2;
    /* Version 1.2 */
    public static final int V1_2 = 3;

    /** Get replacement tag for the version.
     * @param i - version 
     * @return replacement tag
     */
    public static String getReplaceTag(int i) {
        switch (i) {
            case VCOMPAT: return "-vcompat"; // NOI18N
            case V1_1: return "-v1.1"; // NOI18N
            case V1_2: return "-v1.2"; // NOI18N
        }
        return ""; // NOI18N
    }
    
    /** Property editor.
     */
    public static class PE extends PropertyEditorSupport {

        Integer version = new Integer(V1_2);
        private String[] tags = null;
        
        public void setValue(Object o) {
            this.version = (Integer) o;
            firePropertyChange ();
        }

        public Object getValue() {
            return version;
        }

        public boolean supportsCustomEditor() {
            return false;
        }

        /** 
         * Postcondition: never null, at least one item. 
         * @return an array of tags
         */
        public synchronized String[] getTags() {
            if (tags == null) {
                ResourceBundle rb = NbBundle.getBundle(Version.class);
                tags = new String[] {
                    rb.getString("LBL_Version_Default"), // NOI18N
                    rb.getString("LBL_Version_Compat"), // NOI18N
                    rb.getString("LBL_Version_11"), // NOI18N
                    rb.getString("LBL_Version_12"), // NOI18N
                };
            }
            return tags;
        }

        public String getAsText() {
            int i = version.intValue();
            String[] t = getTags();
            if ((i < 0) || (i >= t.length)) return t[0];
            return t[i];
        }

        public void setAsText(String name) throws IllegalArgumentException {
            int v = DEFAULT;
            String[] t = getTags();
            for(int i = 0; i < t.length; i++) {
                if (name.equals(t[i])) {
                    v = i;
                    break;
                }
            }
            if (v != version.intValue()) setValue(new Integer(v));
        }
    }

}
