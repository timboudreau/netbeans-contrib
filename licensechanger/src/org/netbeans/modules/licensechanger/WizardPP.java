/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.licensechanger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.modules.licensechanger.FileChildren.FileItem;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPanelProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
class WizardPP extends WizardPanelProvider {
    private FileObject[] roots;
    private enum Steps {
        chooseFileTypes,
        chooseLicense,
        chooseFolders,
        preview;

        public String getDisplayName() {
            return gs(name());
        }
    }

    public WizardPP(FileObject[] roots) {
        super(gs("WIZARD_TITLE"), new String[]{
            Steps.chooseFileTypes.name(),
            Steps.chooseLicense.name(),
            Steps.chooseFolders.name(),
            Steps.preview.name(),
        },
                new String[]{
              Steps.chooseFileTypes.getDisplayName(),
              Steps.chooseLicense.getDisplayName(),
              Steps.chooseFolders.getDisplayName(),
              Steps.preview.getDisplayName(),
        });
        this.roots = roots;
    }

    private static String gs(String key) {
        return NbBundle.getMessage(WizardPP.class, key);
    }

    @Override
    protected Object finish(Map settings) throws WizardException {
        return new DeferredWizardResult() {

            @Override
            public void start(Map settings, ResultProgressHandle handle) {
                handle.setBusy("Finding items");
                Set<FileItem> items = (Set<FileItem>) settings.get(PreviewPanel.KEY_ITEMS);
                String licenseText = (String) settings.get(LicenseChooserPanel.KEY_LICENSE_TEXT);
                int ix=0;
                int max = items.size();
                Charset enc;
                for (FileItem item : items) {
                    handle.setProgress(item.getFile().getNameExt(), ix, max);
                    try {
                        String content = PreviewPanel.loadFile(item.file);
                        String nue = item.handler.transform(content, licenseText);
                        enc = FileEncodingQuery.getEncoding(item.file);
                        BufferedOutputStream out = new BufferedOutputStream (item.file.getOutputStream());
                        byte[] bytes;
                        try {
                            bytes = nue.getBytes(enc.name());
                        } catch (UnsupportedEncodingException e) {
                            //properties files get resource_bundle_charset
                            bytes = nue.getBytes(FileEncodingQuery.getDefaultEncoding().name());
                        }
                        ByteArrayInputStream in = new ByteArrayInputStream (bytes);
                        try {
                            FileUtil.copy(in, out);
                        } finally {
                            out.close();
                            in.close();
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    ix++;
                }
                handle.finished(null);
            }
        };
    }

    @Override
    protected void recycleExistingPanel(String id, WizardController controller, Map wizardData, JComponent panel) {
        //XXX update things
        super.recycleExistingPanel(id, controller, wizardData, panel);
    }

    Map<Steps, JComponent> panelForStep = new HashMap<Steps, JComponent>();

    @Override
    protected JComponent createPanel(WizardController arg0, String stepId, Map args) {
        Steps step = Steps.valueOf(stepId);
        JComponent result = panelForStep.get(step);
        if (result == null) {
            switch (step) {
                case chooseFileTypes :
                    result = new ChooseFileTypesPanel(args);
                    break;
                case chooseFolders :
                    result = new SelectFoldersPanel(roots, args);
                    break;
                case chooseLicense :
                    result = new LicenseChooserPanel(args);
                    break;
                case preview :
                    result = new PreviewPanel(args);
                    break;
                default :
                    throw new AssertionError();
            }
        }
        return result;
    }
}
