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

package org.netbeans.modules.corba.browser.ns;

/**
 * This class provides static methods for correcting the strings
 * representing the code which should be inserted to editor
 * @author  tzezula
 * @version 1.0
 */
public class GenerateSupport extends Object {

    /** Creates new GenerateSupport */
    public GenerateSupport() {
    }

    /** This methods overrides the special meaning of some
     *  characters, e.g. \, ', "
     *  @param String code, the code which should be converted
     *  @return String
     */
     public static String correctCode (String code) {
        StringBuffer sb = new StringBuffer(code);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\\') {
                sb.insert(i, '\\');
                i++;
            }
            else if (sb.charAt(i) == '\''){
                sb.insert(i,'\\');
                i++;
            }
            else if (sb.charAt(i) =='\"'){
                sb.insert(i,'\\');
                i++;
            }
        }
        return sb.toString();
     }

}