/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jndi.utils;

import org.openide.TopManager;
import org.openide.ErrorManager;
/**
 *
 * @author  tzezula
 * @version 
 */
public class Request implements Runnable {
    
    protected APCTarget target;
    
    public Request (APCTarget target) {
        this.target = target;
    }
    
    public void run() {
        try {
            this.target.preAction();
            this.target.performAction();
        }catch (Exception e) {
            TopManager.getDefault().getErrorManager().notify (ErrorManager.USER,e);
        }
        finally {
        }
    }
    
}
