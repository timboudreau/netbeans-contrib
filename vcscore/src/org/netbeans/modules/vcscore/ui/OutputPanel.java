/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.ui;

import javax.swing.*;

/**
 * OutputPanel.java
 *
 * Created on December 21, 2003, 7:17 PM 
 * @author  Richard Gregor
 */
public class OutputPanel extends AbstractOutputPanel{
    
    private JTextArea stdOutput;
    private JTextArea errOutput;    
   
    protected JComponent getErrComponent() {
        if(errOutput == null){
            errOutput = new JTextArea();
            errOutput.setEditable(false);
        }
        return errOutput;
    }
    
    protected JComponent getStdComponent() {
        if(stdOutput == null){
            stdOutput = new JTextArea();
            stdOutput.setEditable(false);
        }
        return stdOutput;
    }
    
    protected boolean isErrOutput() {
        if (errOutput.getText().length() > 0)
            return true;
        else
            return false;
    }
    
    protected boolean isStdOutput() {
        if(stdOutput.getText().length() > 0)
            return true;
        else
            return false;
    }
    
}
