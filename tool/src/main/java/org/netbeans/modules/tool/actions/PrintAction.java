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

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */
package org.netbeans.modules.tool.actions;

import javax.swing.Action;
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
