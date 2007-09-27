/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
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
