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
package org.netbeans.modules.php.fuse.generators;

import java.awt.Dialog;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.php.fuse.FuseFramework;
import org.netbeans.modules.php.fuse.ui.generators.RoutesGenerationPanel;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Item;
import org.openide.util.Lookup.Template;
import org.openide.util.NbBundle;

/**
 * New generator of source code for routing generation.
 * @author Martin Fousek
 */
public class RoutesGenerator implements CodeGenerator {

    private JTextComponent textComp;

    private RoutesGenerator(JTextComponent textComp) {
        this.textComp = textComp;
    }

    /**
     * Factory for creating of code generator.
     */
    public static class Factory implements CodeGenerator.Factory {

        @Override
        public List create(Lookup context) {
            Item<JTextComponent> textCompItem = context.lookupItem(new Template(JTextComponent.class, null, null));
            JTextComponent textComp = textCompItem.getInstance();
            return Collections.singletonList(new RoutesGenerator(textComp));

        }
    }

    @Override
    public String getDisplayName() {
        return "Generate routes...";
    }

    @Override
    public void invoke() {
        Source source = Source.create(textComp.getDocument());
        String fileName = FileUtil.toFile(source.getFileObject()).getName();
        if (fileName.equals(FuseFramework.ROUTES_CONF_FILE)) {
            // creation of dialog for Rules Generation
            RoutesGenerationPanel rgp = new RoutesGenerationPanel();
            JButton[] buttons = new JButton[2];
            buttons[0] = new JButton(NbBundle.getMessage(RoutesGenerator.class, "LBL_generate_button"));
            buttons[0].getAccessibleContext().setAccessibleDescription("A11Y_Generate");
            buttons[1] = new JButton(NbBundle.getMessage(RoutesGenerator.class, "LBL_cancel_button"));
            DialogDescriptor dialogDescriptor = new DialogDescriptor(rgp, "Generate routes",
                    true, buttons, buttons[0], DialogDescriptor.DEFAULT_ALIGN, null, null);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
            dialog.setVisible(true);
            if (dialogDescriptor.getValue() == dialogDescriptor.getDefaultValue()) {
                try {
                    Caret caret = textComp.getCaret();
                    int dot = caret.getDot();
                    textComp.getDocument().insertString(dot, rgp.getPreviewText()+"\n\n", null);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Routes generating is available " +
                    "only for routes.conf.php files.", "Unaccessible action", JOptionPane.ERROR_MESSAGE);
        }
    }
}
