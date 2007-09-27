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
package org.netbeans.modules.latex.refactoring;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.refactoring.spi.SimpleRefactoringElementImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.PositionBounds;
import org.openide.text.PositionRef;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.xml.XMLUtil;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXRefactoringElementImplementation extends SimpleRefactoringElementImplementation {

    private FileObject file;
    private PositionBounds bounds;
    private String dname;
    private boolean wasArgument;
    
    private String originalName;
    private String rewriteTo;
    
    public LaTeXRefactoringElementImplementation(Node n, String originalName, String rewriteTo) {
        this(n);
        this.originalName = originalName;
        this.rewriteTo = rewriteTo;
    }
    
    public LaTeXRefactoringElementImplementation(Node n) {
        dname = computeHtmlDisplayName(n);
        file = (FileObject) n.getStartingPosition().getFile();
        
        try {
            DataObject od = DataObject.find(file);
            CloneableEditorSupport ces = od.getLookup().lookup(CloneableEditorSupport.class);
            PositionRef prefStart = ces.createPositionRef(n.getStartingPosition().getOffsetValue(), Position.Bias.Backward);
            PositionRef prefEnd = ces.createPositionRef(n.getEndingPosition().getOffsetValue(), Position.Bias.Forward);

            bounds = new PositionBounds(prefStart, prefEnd);
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
        
        if (n instanceof ArgumentNode) {
            wasArgument = true;
        } else {
            if (n instanceof CommandNode) {
                wasArgument = false;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public String getText() {
        return dname;
    }

    public String getDisplayText() {
        return dname;
    }

    public void performChange() {
        if (isEnabled() && rewriteTo != null) {
            rewriteTo(rewriteTo);
        }
    }

    @Override
    public void undoChange() {
        if (isEnabled() && rewriteTo != null) {
            rewriteTo(originalName);
        }
    }

    private void rewriteTo(String target) {
        try {
            //XXX: locking
            Document doc = Utilities.getDefault().openDocument(file);
            int start = bounds.getBegin().getOffset();
            int end   = bounds.getEnd().getOffset();
            
            if (wasArgument) {
                doc.remove(start, end - start);
                doc.insertString(start, "{" + target + "}", null);
            } else {
                //                    if (cnode.getArgumentCount() > 0) {
                //                        endOffset = cnode.getArgument(0).getStartingPosition().getOffsetValue() - 1;
                //                    }
                
                doc.remove(start, end - start);
                doc.insertString(start, target, null);
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    public Lookup getLookup() {
        return Lookup.EMPTY;
    }

    public FileObject getParentFile() {
        return file;
    }

    public PositionBounds getPosition() {
        return bounds;
    }

    @Override
    protected String getNewFileContent() {
        try {
            //XXX: locking
            Document doc = Utilities.getDefault().openDocument(file);
            StringBuffer text = new StringBuffer(doc.getText(0, doc.getLength()));
            int start = bounds.getBegin().getOffset();
            int end   = bounds.getEnd().getOffset();
            
            if (wasArgument) {
                text.replace(start, end, "{" + rewriteTo + "}");
            } else {
                text.replace(start, end, rewriteTo);
            }
            
            return text.toString();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
        }
        
        return null;
    }

    private static String computeHtmlDisplayName(Node node) {
        try {
            SourcePosition sp = node.getStartingPosition();
            SourcePosition ep = node.getEndingPosition();
            Document doc = Utilities.getDefault().openDocument(sp.getFile());
            int rowStartOffset = org.netbeans.editor.Utilities.getRowStart((BaseDocument) doc, sp.getOffsetValue()); //TODO
            int rowEndOffset = org.netbeans.editor.Utilities.getRowEnd((BaseDocument) doc, sp.getOffsetValue()); //TODO
            String line = doc.getText(rowStartOffset, rowEndOffset - rowStartOffset);
            int start = sp.getOffsetValue() - rowStartOffset;
            int end   = ep.getOffsetValue() <= rowEndOffset ? ep.getOffsetValue() - rowStartOffset : rowEndOffset - rowStartOffset;
            
            String prefix = line.substring(0, start);
            String text   = line.substring(start, end);
            String suffix = line.substring(end);
            
            return "<html>" + XMLUtil.toElementContent(prefix) + "<b>" + XMLUtil.toElementContent(text) + "</b>" + XMLUtil.toElementContent(suffix);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
            return node.getFullText().toString();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            return node.getFullText().toString();
        }
    }
    
}
