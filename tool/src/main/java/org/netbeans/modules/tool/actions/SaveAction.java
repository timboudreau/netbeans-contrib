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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.modules.tool.ExitDialog;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.SaveCookie;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * SaveAction based on CookieObjectAction.
 * @author David Strupl
 */
public class SaveAction extends CookieObjectAction {

    /** Creates a new instance of SaveAction */
    public SaveAction() {
        this(Utilities.actionsGlobalContext());
    }

    /** Creates a new instance of SaveAction */
    public SaveAction(Lookup context) {
        super(SaveCookie.class, context);
        putValue(NAME, NbBundle.getMessage(SaveAction.class, "Save"));
        putValue("iconBase", "org/netbeans/modules/tool/resources/save.png");
    }
    
    /**
     * Implements the saving by calling SaveCookie.save().
     */
    protected void handleCookie(Object cookie) {
        if ( !(cookie instanceof SaveCookie)) {
            throw new IllegalStateException("Cookie was " + cookie); // NOI18N
        }
        SaveCookie saver = (SaveCookie)cookie;
        try {
            saver.save();
        } catch (IOException exc) {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Saving failed.", exc); // NOI18N
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    NbBundle.getBundle(ExitDialog.class).getString("EXC_Save"),
                    NotifyDescriptor.ERROR_MESSAGE
            );
            DialogDisplayer dd = DialogDisplayer.getDefault();
            dd.notify(nd);
        }
    }

    /**
     * Implements the method from interface ContextAwareAction.
     */
    public Action createContextAwareInstance(Lookup actionContext) {
        return new SaveAction(actionContext);
    }
}
