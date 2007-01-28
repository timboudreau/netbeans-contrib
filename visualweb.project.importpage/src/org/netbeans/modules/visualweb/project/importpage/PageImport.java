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
/*
 * ImportPagePanel.java
 *
 * Created on March 26, 2004, 10:08 PM
 */
package org.netbeans.modules.visualweb.project.importpage;

import java.io.File;
import javax.swing.JPanel;

import org.netbeans.api.project.Project;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectUtils;
import org.netbeans.modules.visualweb.project.jsf.api.Importable;


/**
 * XXX Originaly in desinger module
 * (designer/src/org/netbeans/modules/visualweb/designer/PageImport), moved away from there.
 *
 * Provide the ability to import a web page
 * @author  Tor Norbye
 */
public class PageImport implements Importable, Importable.PageImportable {
    private ImportPagePanel dialog;

    public PageImport() {
    }

    // ----------- implements Importable ----------
    public String getDisplayName() {
        return NbBundle.getMessage(ImportPagePanel.class, "ImportWebPage"); // NOI18N
    }

    public void perform(Project project) {
        perform(project, null);
    }

    private void perform(Project project, File file) {
        if (dialog == null) {
            dialog = new ImportPagePanel();
        }

        dialog.setProject(project);

        dialog.showImportDialog(file);
    }

    /** This import is only available into WebAppProjects */
    public boolean enable(Project project) {
        if (project == null) {
            return false;
        }

        if (JsfProjectUtils.getDocumentRoot(project) == null) {
            // Probably not a JSF project
            return false;
        }

        return true;
    }

    // >> Implements Importable.PageImportable >>
    /**
     * If this looks like an importable file, do so and return the import panel used,
     * otherwise return null. (This is useful when you drop a number of pages on the
     * designer so the panel can be reused and so that it remembers user edits
     * for each of the files
     */
    public JPanel importRandomFile(Project project, File file, String extension,
    JPanel panel) {
        if (extension.equalsIgnoreCase("jsp") // NOI18N
        || extension.equalsIgnoreCase("jspx") // NOI18N
        || extension.equalsIgnoreCase("htm") // NOI18N
        || extension.equalsIgnoreCase("html")) { // NOI18N
            PageImport pageImport = new PageImport();
            if(panel instanceof ImportPagePanel) {
                pageImport.dialog = (ImportPagePanel)panel;
            } else {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL,
                        new IllegalArgumentException("The panel argument is not instance of " // NOI18N
                                                    + ImportPagePanel.class));
            }

            if (pageImport.enable(project)) {
                pageImport.perform(project, file);

                return pageImport.dialog;
            }
        }

        return null;
    }
    // << Implements Importable.PageImportable <<
}
