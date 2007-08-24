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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.test.au.launch;

import java.util.logging.Logger;
import javax.swing.Action;
import org.openide.LifecycleManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup.Item;
import org.openide.util.lookup.Lookups;

/**
 */
public class Installer extends ModuleInstall implements Runnable {
    Action action;
    
    
    @Override
    public void restored() {
        for (Item<Action> item : Lookups.forPath("Actions").lookupResult(Action.class).allItems()) {
            if (item.getId().contains("PluginManagerAction")) {
                action = item.getInstance();
                if (action != null) {
                    return;
                }
            }
        }
        Logger.global.warning("Cannot find PluginManagerAction! Enable autoupdate/ui module please!");
    }

    public void run() {
        if (action != null) {
            action.actionPerformed(null);
            LifecycleManager.getDefault().exit();
        }
    }
}
