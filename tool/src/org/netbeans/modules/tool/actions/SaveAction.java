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
            ErrorManager em = ErrorManager.getDefault();
            Throwable t = em.annotate(
                exc, NbBundle.getBundle(ExitDialog.class).getString("EXC_Save")
            );
            em.notify(ErrorManager.EXCEPTION, t);
        }
    }

    /**
     * Implements the method from interface ContextAwareAction.
     */
    public Action createContextAwareInstance(Lookup actionContext) {
        return new SaveAction(actionContext);
    }
}
