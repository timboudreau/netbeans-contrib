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

package org.netbeans.jemmy.operators;

import org.netbeans.jemmy.operators.Operator;

public class JComboBoxNoBlockOperator extends JComboBoxOperator {
    
    public JComboBoxNoBlockOperator (ContainerOperator con, int index) {
        super (con, index);
    }
    
    public void selectItemNoBlock (final String item) {
        produceNoBlocking (new Operator.NoBlockingAction ("JComboBoxNoBlockOperator - " + item) {
            public Object doAction (Object param) {
                selectItem (item);
                return null;
            }
        });
    }
    
}
