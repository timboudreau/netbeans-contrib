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

package org.netbeans.modules.sfsbrowser;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;

/**
 * Factory for creating actions to show URLs in the Browser.
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public final class ShowURLActionFactory {

    private ShowURLActionFactory() {
    }

    /**
     *
     * @param file
     * @return
     */
    public static Action create(FileObject file) {
        try {
            String urlString = (String) file.getAttribute("url");
            String displayName = (String) file.getAttribute("displayName");
            if (displayName == null) {
                displayName = file.getName();
            }
            return new ShowURLAction(displayName, new URL(urlString));
        } catch (MalformedURLException ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    static class ShowURLAction extends AbstractAction {
        private URL url;

        ShowURLAction(String name, URL url) {
            super(name);
            this.url = url;
            if (url != null) {
                putValue(SHORT_DESCRIPTION, url.toExternalForm());
                putValue(LONG_DESCRIPTION, url.toExternalForm());
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (url != null) {
                HtmlBrowser.URLDisplayer.getDefault().showURL(url);
            }
        }
    }
}
