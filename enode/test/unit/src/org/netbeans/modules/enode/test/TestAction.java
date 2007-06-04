/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2006 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.enode.test;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;

/**
 *
 * @author David Strupl
 */
public class TestAction extends AbstractAction implements ContextAwareAction {
    
    /** Creates a new instance of TestAction */
    public TestAction() {
        putValue(NAME, "TestAction");
    }

    public void actionPerformed(ActionEvent e) {
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        return new TestAction();
    }
    
}
