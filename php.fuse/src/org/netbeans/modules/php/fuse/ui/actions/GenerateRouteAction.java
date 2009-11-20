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
package org.netbeans.modules.php.fuse.ui.actions;

import java.awt.Dialog;
import java.io.File;
import javax.swing.JButton;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.csl.spi.GsfUtilities;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.fuse.FuseFramework;
import org.netbeans.modules.php.fuse.generators.RoutesGenerator;
import org.netbeans.modules.php.fuse.ui.generators.RoutesGenerationPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * @author Martin Fousek
 */
public final class GenerateRouteAction extends BaseAction {

    private static final long serialVersionUID = 1;
    private static final GenerateRouteAction INSTANCE = new GenerateRouteAction();

    private GenerateRouteAction() {
    }

    public static GenerateRouteAction getInstance() {
        return INSTANCE;
    }

    @Override
    public void actionPerformed(PhpModule phpModule) {
        final FileObject fObj = FileUtil.toFileObject(new File(phpModule.getSourceDirectory() + "/config/" + FuseFramework.ROUTES_CONF_FILE));
        GsfUtilities.open(fObj, -1, null);
        RoutesGenerationPanel rgp = new RoutesGenerationPanel();
        JButton[] buttons = new JButton[2];
        buttons[0] = new JButton(NbBundle.getMessage(RoutesGenerator.class, "LBL_generate_button"));
        buttons[0].getAccessibleContext().setAccessibleDescription("A11Y_Generate");
        buttons[1] = new JButton(NbBundle.getMessage(RoutesGenerator.class, "LBL_cancel_button"));
        DialogDescriptor dialogDescriptor = new DialogDescriptor(rgp, "Generate routes",
                true, buttons, buttons[0], DialogDescriptor.DEFAULT_ALIGN, null, null);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.setVisible(true);
        dialog.requestFocus();
        if (dialogDescriptor.getValue() == dialogDescriptor.getDefaultValue()) {
            try {
                Source source = Source.create(fObj);
                Document doc = source.getDocument(true);
                int position = getCaretPositionAfterPhpDelimiter(doc);
                JTextComponent textComp = EditorRegistry.lastFocusedComponent();
                textComp.getDocument().insertString(position, rgp.getPreviewText() + "\n\n", null);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    protected String getPureName() {
        return NbBundle.getMessage(GenerateRouteAction.class, "LBL_GenerateRoute");
    }

    private int getCaretPositionAfterPhpDelimiter(Document doc) throws BadLocationException {
        String docText = doc.getText(0, doc.getLength());
        return docText.indexOf("?>");
    }
}
