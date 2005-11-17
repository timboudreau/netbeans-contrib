/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.zeroadmin.test;

import javax.naming.*;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.ErrorManager;

/** 
 * Action creating some number of the TestSetting instances.
 *
 * @author David Strupl
 */
public class TestAction extends CallableSystemAction {
    
    private static int number;
    /** Cache the reference to the initial context*/
    private Context incon;
     
    public void performAction() {
        try {
            int n = getNumber();
            Context c = getInitialContext();
            for (int i = 0; i < n; i++) {
                TestSetting ts = new TestSetting();
                ts.setSize(n);
                String name="test" + i;
                boolean foundAvailable = false;
                while (! foundAvailable) {
                    try {
                        c.lookup(name);
                        name += i;
                        if (name.length() > 1000000) {
                            throw new IllegalStateException();
                        }
                    } catch (NameNotFoundException nnfe) {
                        foundAvailable = true;
                    }
                }
                c.bind(name, ts);
            }
        } catch (Exception x) {
            ErrorManager.getDefault().notify(x);
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(TestAction.class, "LBL_Action");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/zeroadmin/test/TestActionIcon.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx(TestAction.class);
    }

    private static int getNumber() {
        if (number != 0) {
            return number;
        }
        try {
            String s = System.getProperty("test.setting.size");
            if (s != null) {
                number = Integer.parseInt(s);
            } else {
                number = 10;
            }
        } catch (Exception x) {
            ErrorManager.getDefault().notify(x);
        }
        return number;
    }
    
    /**
     * Lazy initialization of the initial context
     */
    private Context getInitialContext() throws NamingException {
        if (incon == null) {
            incon = (Context)new InitialContext().lookup("nbres:/");
        }
        return incon;
    }

}
