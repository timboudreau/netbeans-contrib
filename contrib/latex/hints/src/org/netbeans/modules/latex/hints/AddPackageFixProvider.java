/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2007-2008 Sun
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

package org.netbeans.modules.latex.hints;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.text.Position;
import org.netbeans.modules.gsf.api.CancellableTask;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.ParseError;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.CommandPackage;
import org.netbeans.modules.latex.model.command.DefaultTraverseHandler;
import org.netbeans.napi.gsfret.source.CompilationInfo;
import org.netbeans.napi.gsfret.source.ModificationResult.Difference;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source;
import org.netbeans.napi.gsfret.source.WorkingCopy;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.PositionRef;

/**
 *
 * @author Jan Lahoda
 */
public class AddPackageFixProvider implements FixProvider {

    private final boolean command;

    public AddPackageFixProvider(boolean command) {
        this.command = command;
    }
    
    public List<Fix> resolveFixes(CompilationInfo info, ParseError error) {
        List<Fix> result = new LinkedList<Fix>();
        LaTeXParserResult lpr = LaTeXParserResult.get(info);
        Source s = Source.forFileObject(lpr.getMainFile());
        
        if (s == null) {
            return result;
        }
        
        for (String packageName : (Collection<String>) CommandPackage.getKnownPackages()) {
            CommandPackage pack = CommandPackage.getCommandPackageForName(packageName);
            if (getSpecificationMap(pack).get(error.getParameters()[0]) != null) {
                result.add(new FixImpl(s, pack));
            }
        }
        
        return result;
    }
    
    private Map<String, ?> getSpecificationMap(CommandPackage p) {
        if (command)
            return p.getCommands();
        else
            return p.getEnvironments();
    }

    private class FixImpl implements Fix {
        private Source s;
        private CommandPackage pack;

        public FixImpl(Source s, CommandPackage pack) {
            this.s = s;
            this.pack = pack;
        }

        public String getText() {
            return "Add \\usepackage{" + pack.getName() + "}";
        }

        public ChangeInfo implement() throws Exception {
            s.runModificationTask(new CancellableTask<WorkingCopy>() {
                public void cancel() {}
                public void run(WorkingCopy parameter) throws Exception {
                    parameter.toPhase(Phase.RESOLVED);
                    
                    LaTeXParserResult lpr = LaTeXParserResult.get(parameter);

                    final CommandNode[] documentClass = new CommandNode[1];
                    final List<CommandNode> usePackage = new LinkedList<CommandNode>();
                    
                    lpr.getDocument().traverse(new DefaultTraverseHandler() {
                        @Override
                        public boolean commandStart(CommandNode node) {
                            if (node.getCommand().hasAttribute("#usepackage-command")) {
                                usePackage.add(node);
                            }
                            return true;
                        }
                        @Override
                        public boolean argumentStart(ArgumentNode node) {
                            if (node.getArgument().hasAttribute("#documentclass")) {
                                documentClass[0] = (CommandNode) node.getCommand();
                            }
                            return false;
                        }
                        @Override
                        public boolean blockStart(BlockNode node) {
                            return false;
                        }
                    });
                    
                    PositionRef pos;
                    DataObject od = DataObject.find(parameter.getFileObject());
                    EditorCookie ec = od.getLookup().lookup(EditorCookie.class);
                    CloneableEditorSupport support = (CloneableEditorSupport) ec;
                    boolean before = false;
                    
                    if (usePackage.isEmpty()) {
                        pos = support.createPositionRef(documentClass[0].getEndingPosition().getOffsetValue(), Position.Bias.Forward);
                    } else {
                        List<String> packageName = new LinkedList<String>();
                        
                        for (CommandNode n : usePackage) {
                            packageName.add(n.getArgument(1).getText().toString());
                        }
                        
                        int insertIndex = 0;
                        
                        while (insertIndex < packageName.size() && pack.getName().compareTo(packageName.get(insertIndex)) > 0) {
                            insertIndex++;
                        }
                        
                        if (pack.getName().compareTo(packageName.get(packageName.size() - 1)) < 0) {
                            before = true;
//                            insertIndex = insertIndex > 0 ? insertIndex - 1 : 0;
                            pos = support.createPositionRef(usePackage.get(insertIndex).getStartingPosition().getOffsetValue(), Position.Bias.Forward);
                        } else {
                            pos = support.createPositionRef(usePackage.get(usePackage.size() - 1).getEndingPosition().getOffsetValue(), Position.Bias.Forward);
                        }
                    }
                    
                    Difference d = new Difference(Difference.Kind.INSERT, pos, pos, "", (!before ? "\n" : "") + "\\usepackage{" + pack.getName() + "}" + (before ? "\n" : ""), "Add \\usepackage");
                    
                    parameter.addDiff(d);
                }
            }).commit();
            return null;
        }
    }

}
