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
 */
package org.netbeans.modules.flyingsaucer;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Displays an ad-hoc URL in a new XHTML component.
 *
 * @author Tim Boudreau
 */
public class ChooseURLAction extends AbstractAction {
    
    public ChooseURLAction() {
        putValue (NAME, NbBundle.getMessage (
                ChooseURLAction.class, "LBL_AdhocUrl")); //NOI18N
    }

    private String lastUrl = "http://csszengarden.com/"; //NOI18N
    public void actionPerformed(ActionEvent e) {
        String title = NbBundle.getMessage (
                ChooseURLAction.class, "TTL_AdhocUrl"); //NOI18N
        String caption = NbBundle.getMessage (
                ChooseURLAction.class, "CAP_AdhocUrl"); //NOI18N
        NotifyDescriptor.InputLine line = 
                new NotifyDescriptor.InputLine (title, caption);
        line.setInputText(lastUrl);
        if (DialogDisplayer.getDefault().notify(line) == NotifyDescriptor.OK_OPTION) {
            lastUrl = line.getInputText();
            try {
                URL url = new URL (lastUrl);
                TopComponent tc = new 
                        FlyingSaucerTopComponent (url);
                tc.open();
                tc.requestActive();
            } catch (MalformedURLException mue) {
                Toolkit.getDefaultToolkit().beep();
                StatusDisplayer.getDefault().setStatusText(
                        mue.getMessage());
            }
        }
    }
}
