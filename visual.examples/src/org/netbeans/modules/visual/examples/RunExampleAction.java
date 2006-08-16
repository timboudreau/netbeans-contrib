/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.visual.examples;

import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;

/**
 * @author David Kaspar
 */
public final class RunExampleAction extends CallableSystemAction {

    public RunExampleAction () {
        setEnabled (true);
    }

    public void performAction() {
        RunDialog.main(null);
    }

    public String getName() {
        return "Show &Visual Library Examples Dialog";
    }

    public String iconResource() {
        return null;
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

}
