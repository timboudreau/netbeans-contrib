/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.tool.actions;

import java.io.IOException;
import javax.swing.Action;
import org.netbeans.modules.tool.ExitDialog;
import org.openide.ErrorManager;
import org.openide.cookies.PrintCookie;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * PrintAction based on CookieObjectAction.
 * @author David Strupl
 */
public class PrintAction extends CookieObjectAction {
    
    /** Creates a new instance of PrintAction */
    public PrintAction() {
        this(Utilities.actionsGlobalContext());
    }

    /** Creates a new instance of PrintAction */
    public PrintAction(Lookup context) {
        super(PrintCookie.class, context);
        putValue(NAME, NbBundle.getMessage(PrintAction.class, "Print"));
        putValue("iconBase", "org/netbeans/modules/tool/resources/print.png");
    }
    
    /**
     * Implements the saving by calling PrintCookie.print().
     */
    protected void handleCookie(Object cookie) {
        if ( !(cookie instanceof PrintCookie)) {
            throw new IllegalStateException("Cookie was " + cookie); // NOI18N
        }
        PrintCookie printer = (PrintCookie)cookie;
        printer.print();
    }

    /**
     * Implements the method from interface ContextAwareAction.
     */
    public Action createContextAwareInstance(Lookup actionContext) {
        return new PrintAction(actionContext);
    }
}
