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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.latex.refactoring.actions;

import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.retouche.source.CompilationController;
import org.netbeans.api.retouche.source.Phase;
import org.netbeans.api.retouche.source.Source;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.impl.NodeImpl;
import org.netbeans.modules.latex.refactoring.FindUsagesPerformer;
import org.netbeans.modules.latex.refactoring.UsagesQuery;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.ui.ActionsImplementationProvider;
import org.netbeans.modules.refactoring.spi.ui.UI;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class ActionsImplementationProviderImpl extends ActionsImplementationProvider {

    public ActionsImplementationProviderImpl() {
    }

    @Override
    public boolean canFindUsages(Lookup lookup) {
        //not correct currently:
        org.openide.nodes.Node n = lookup.lookup(org.openide.nodes.Node.class);
        
        if (n == null)
            return false;
        
        FileObject f = n.getLookup().lookup(FileObject.class);
        
        return "text/x-tex".equals(FileUtil.getMIMEType(f));
    }

    
    @Override
    public void doFindUsages(Lookup lookup) {
        perform(lookup, true);
    }

    @Override
    public boolean canRename(Lookup lookup) {
        return canFindUsages(lookup);
    }

    @Override
    public void doRename(Lookup lookup) {
        perform(lookup, false);
    }
    
    private void perform(Lookup lookup, boolean whereUsed) {
        try {
        //XXX:
        org.openide.nodes.Node n = lookup.lookup(org.openide.nodes.Node.class);
        
        FileObject file = n.getLookup().lookup(FileObject.class);
        
//        JEditorPane jep = lookup.lookup(JEditorPane.class);
//        final Document doc = jep.getDocument();
//        final int caret = jep.getCaretPosition();
//        DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
//        FileObject file = od.getPrimaryFile();
        
        DataObject od = DataObject.find(file);
        EditorCookie ec = od.getLookup().lookup(EditorCookie.class);
        
        JEditorPane[] panes = ec.getOpenedPanes();
        if (panes == null) {
            System.err.println("#############################");
            return ;
        }
        
        final String[] searchFor = new String[1];
        final Problem[] problem = new Problem[1];
        final String[] originalName = new String[1];
        
        final int caret = panes[0].getCaretPosition();
        
            final Source source = Source.forFileObject(file);
            
            source.runUserActionTask(new CancellableTask<CompilationController>() {
                public void cancel() {}
                public void run(CompilationController parameter) throws Exception {
                    parameter.toPhase(Phase.RESOLVED);
                    
                    LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();
                    Document doc = parameter.getDocument();
                    
                    if (doc == null)
                        return ;
                    
                    if (searchFor(doc, lpr, caret, originalName) != null) {
                        searchFor[0] = "Something";
                    } else {
                        searchFor[0] ="Nothing";
                        problem[0] = new Problem(true, "Cannot resolve");
                    }
                }
            }, true);
        
            UI.openRefactoringUI(new LaTeXFURefactoringUI(source, caret, searchFor[0], originalName[0], problem[0], whereUsed));
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }
    
    public static Object searchFor(Document doc, LaTeXParserResult lpr, int caret, String[] originalName) throws IOException {
        Node n = lpr.getCommandUtilities().findNode(doc, caret);
        FindUsagesPerformer performer = null;
        String name = null;
        
        if (n instanceof ArgumentNode) {
            ArgumentNode anode = (ArgumentNode) n;
            
            if (anode.hasAttribute("#ref") || anode.hasAttribute("#label")) {
                //check for label usages:
                String text = UsagesQuery.getArgumentContent(anode).toString();
                
                originalName[0] = text;
                return text;
            }
            
            if (anode.hasAttribute("#command-def")) {
                name = UsagesQuery.getArgumentContent(anode).toString();
                originalName[0] = name;
                return ((NodeImpl) anode.getCommand()).getCommand(name, true);
            }
            
            if (anode.hasAttribute("#envname")) {
                name = UsagesQuery.getArgumentContent(anode).toString();
                originalName[0] = name;
                return ((NodeImpl) anode.getCommand()).getEnvironment(name, true);
            }
            
            if (anode.hasAttribute("#environmentname")) {
                Node block = anode.getCommand().getParent();
                
                if (block instanceof BlockNode) {
                    Environment en = ((BlockNode) block).getEnvironment();
                    
                    originalName[0] = en.getName();
                    return en;
                }
            }
        }
        
        if (n instanceof CommandNode) {
            //TODO: check if the command has been defined in the document, not in the class or package:
            CommandNode cnode = (CommandNode) n;
            Command cmd = cnode.getCommand();
            
            originalName[0] = cmd.getCommand();
            return cmd;
        }
        
        return null;
    }
}
