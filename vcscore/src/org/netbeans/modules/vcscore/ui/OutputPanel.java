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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.ui;

import javax.swing.*;
import org.openide.util.NbBundle;

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
            errOutput.getAccessibleContext().setAccessibleName(NbBundle.getBundle(OutputPanel.class).getString("ACS_OutputPanel.ErrComponent"));//NOI18N
            errOutput.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(OutputPanel.class).getString("ACSD_OutputPanel.ErrComponent"));//NOI18N
            java.awt.Font font = errOutput.getFont();
            errOutput.setFont(new java.awt.Font("Monospaced", font.getStyle(), font.getSize()));
        }
        return errOutput;
    }
    
    protected JComponent getStdComponent() {
        if(stdOutput == null){
            stdOutput = new JTextArea();
            stdOutput.setEditable(false);
            stdOutput.getAccessibleContext().setAccessibleName(NbBundle.getBundle(OutputPanel.class).getString("ACS_OutputPanel.StdComponent"));//NOI18N
            stdOutput.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(OutputPanel.class).getString("ACSD_OutputPanel.StdComponent"));//NOI18N
            java.awt.Font font = stdOutput.getFont();
            stdOutput.setFont(new java.awt.Font("Monospaced", font.getStyle(), font.getSize()));
        }
        return stdOutput;
    }
    
}
