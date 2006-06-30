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

package util;

import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.EventTool;

public class JHelper {

    public static void closeAllProperties () {
        for (;;) {
            javax.swing.JComponent co = TopComponentOperator.findTopComponent ("Propert", 0);
            if (co == null)
                break;
            new TopComponentOperator (co).close ();
            Helper.sleep (1000);
        }
    }

    public static void typeNewText (JTextFieldOperator oper, String text) {
        oper.setText (text.substring(1));
        oper.typeText (text.substring(0, 1), 0);
    }
    
}
