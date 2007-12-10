/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.latex.hints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.text.Position;
import org.netbeans.api.gsf.CancellableTask;
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
 * @author lahvac
 */
public class AddPackageFixProvider implements FixProvider {

    private final boolean command;

    public AddPackageFixProvider(boolean command) {
        this.command = command;
    }
    
    public List<Fix> resolveFixes(CompilationInfo info, ParseError error) {
        List<Fix> result = new LinkedList<Fix>();
        
        for (String packageName : (Collection<String>) CommandPackage.getKnownPackages()) {
            CommandPackage pack = CommandPackage.getCommandPackageForName(packageName);
            if (getSpecificationMap(pack).get(error.getParameters()[0]) != null) {
                result.add(new FixImpl(info.getSource(), pack));
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
                    
                    LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();

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
