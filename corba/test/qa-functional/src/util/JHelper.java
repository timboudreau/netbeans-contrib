/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package util;

import org.netbeans.jellytools.NbFrameOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.EventTool;

public class JHelper {
    
    public static void closeAllProperties () {
        for (;;) {
            NbFrameOperator fr = NbFrameOperator.find ("Propert", 0);
            if (fr == null)
                break;
            fr.close ();
            Helper.sleep (1000);
        }
    }
    
    public static void typeNewText (JTextFieldOperator oper, String text) {
        EventTool ev = new EventTool ();
        int pos = text.length() - 1;
        ev.waitNoEvent (1000);
		oper.selectAll ();
        oper.setText (text.substring(0, pos));
        ev.waitNoEvent (1000);
		oper.selectAll ();
        oper.setCaretPosition(pos);
        oper.typeText (Character.toString (text.charAt (pos)));
        ev.waitNoEvent (1000);
    }
    
}
