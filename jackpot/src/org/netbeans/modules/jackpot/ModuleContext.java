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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot;

import java.io.PrintWriter;
import java.util.logging.*;
import org.netbeans.api.jackpot.QueryContext;
import org.netbeans.modules.jackpot.engine.CommandLineQueryContext;
import org.openide.awt.StatusDisplayer;
import org.openide.windows.InputOutput;

/**
 * The context for executing queries within the IDE.
 *
 * @author Tom Ball
 */
public class ModuleContext extends CommandLineQueryContext implements QueryContext {
   
    /** Creates a new instance of ModuleContext */
    ModuleContext() {
        super();
    }

    public void sendStatusMessage(String message) {
        StatusDisplayer.getDefault().setStatusText(message);
    }

    public void sendErrorMessage(String message, String title) {
        //TODO: ask UI team whether error dialog is preferable
        sendStatusMessage(title + ": " + message);
    }

    public PrintWriter getLogWriter() {
	InputOutput io = JackpotModule.getLogWindow();
        return io.getOut();
    }
}
