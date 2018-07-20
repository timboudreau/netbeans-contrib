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
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.java.addproperty.actions;

import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.StyledDocument;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.modules.java.addproperty.impl.AddPropertyCodeGenerator;
import org.netbeans.modules.java.editor.codegen.CodeGenerator;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 * Add Property action.
 * 
 * @author  Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class AddPropertyAction extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        EditorCookie editorCookie = activatedNodes[0].getLookup().lookup(EditorCookie.class);
        JEditorPane[] editorPanes = editorCookie.getOpenedPanes();
        if (editorPanes != null) {
            final JEditorPane editorPane = editorPanes[0];
            final StyledDocument document = (StyledDocument) editorPane.getDocument();

            JavaSource js = JavaSource.forDocument(document);
            if (js != null) {
                try {
                    final int caretOffset = editorPane.getCaretPosition();
                    final List<CodeGenerator> gens = new LinkedList<CodeGenerator>();
                    js.runUserActionTask(new Task<CompilationController>() {
                        public void run(CompilationController controller) throws Exception {
                            controller.toPhase(JavaSource.Phase.PARSED);
                            TreePath path = controller.getTreeUtilities().pathFor(caretOffset);
                            for (CodeGenerator gen : new AddPropertyCodeGenerator.Factory().create(controller, path)) {
                                gens.add(gen);
                            }
                        }
                    }, true);
                    if (gens.size() > 0) {
                        gens.get(0).invoke(editorPane);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(AddPropertyAction.class, "CTL_AddPropertyAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        final boolean enabled = super.enable(activatedNodes);
        if (enabled) {
            DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
            if (dataObject != null) {
                FileObject fileObject = dataObject.getPrimaryFile();
                if (fileObject != null) {
                    return "text/x-java".equals(fileObject.getMIMEType());
                }
            }
        }
        return false;
    }

//
//    @Override
//    protected String iconResource() {
//        return "org/netbeans/modules/java/addproperty/ui/resources/addproperty.png";
//    }
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

