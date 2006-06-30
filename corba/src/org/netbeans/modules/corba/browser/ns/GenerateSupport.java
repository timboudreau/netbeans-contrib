/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * GenerateSupport.java
 * Created on May 17, 2000, 2:34 PM
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