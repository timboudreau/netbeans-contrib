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
package org.netbeans.modules.javahints;

import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementUtilities.ElementAcceptor;
import org.netbeans.api.java.source.UiUtils;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.java.hints.infrastructure.ErrorHintsProvider;
import org.netbeans.modules.java.hints.spi.ErrorRule;
import org.netbeans.modules.java.hints.spi.ErrorRule.Data;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class TypoDetector implements ErrorRule<Void> {
    
    static final Logger LOG = Logger.getLogger(TypoDetector.class.getName());
    
    public TypoDetector() {}
    
    public Set<String> getCodes() {
        return new HashSet<String>(Arrays.asList(
                "compiler.err.cant.resolve",
                "compiler.err.cant.resolve.location",
                "compiler.err.doesnt.exist",
                "compiler.err.not.stmt"));
    }
    
    private EnumSet<ElementKind> detectKind(TreePath path) {
        Tree leaf = path.getLeaf();
        
        if (leaf.getKind() != Kind.IDENTIFIER)
            return null;
        
        path = path.getParentPath();
        
        Tree parentLeaf = path.getLeaf();
        
        switch (parentLeaf.getKind()) {
            case METHOD_INVOCATION:
                return EnumSet.of(ElementKind.METHOD);
        
            case NEW_CLASS:
                return EnumSet.of(ElementKind.CONSTRUCTOR);
        
            case VARIABLE:
                VariableTree var = (VariableTree) parentLeaf;
                
                if (var.getType() == leaf) {
                    return EnumSet.of(ElementKind.ANNOTATION_TYPE, ElementKind.CLASS, ElementKind.ENUM, ElementKind.INTERFACE);
                }
            case PARAMETERIZED_TYPE:
                return EnumSet.of(ElementKind.ANNOTATION_TYPE, ElementKind.CLASS, ElementKind.ENUM, ElementKind.INTERFACE);
            case ANNOTATION:
                return EnumSet.of(ElementKind.ANNOTATION_TYPE);
        }
        
        return EnumSet.of(ElementKind.ENUM_CONSTANT, ElementKind.EXCEPTION_PARAMETER, ElementKind.FIELD, ElementKind.LOCAL_VARIABLE, ElementKind.PARAMETER);
    }
    
    public List<Fix> run(final CompilationInfo info, String diagnosticKey, final int offset, TreePath treePath, Data<Void> data) {
        try {
        int errorPosition = offset + 1; //TODO: +1 required to work OK, rethink
        
        if (errorPosition == (-1)) {
            LOG.log(Level.FINE, "run: errorPosition=-1"); //NOI18N
            
            return null;
        }
        
        TreePath path = info.getTreeUtilities().pathFor(errorPosition);
        
        EnumSet<ElementKind> kinds = detectKind(path);
        
        if (kinds == null)
            return null;
        
        Token ident = ErrorHintsProvider.findUnresolvedElementToken(info, offset);
        
        if (ident == null)
            return null;
        
        String text = ident.text().toString();
        
        ErrorHintsProvider.LOG.log(Level.FINE, "run: ident={0}", ident); //NOI18N
        
        if (ident == null) {
            return null;
        }
        
        Scope scope = info.getTrees().getScope(path);
        
        List<Fix> foundProposals = new ArrayList<Fix>();
        Document adoc = (Document) info.getDocument();
        
        Set<Element> elements = new HashSet<Element>();
        
        Iterable<? extends Element> els = info.getElementUtilities().getLocalMembersAndVars(scope, new ElementAcceptor() {
            public boolean accept(Element e, TypeMirror type) {
                return true;
            }
        });
        
        for (Element e : els) {
            elements.add(e);
        }
        
        while (scope != null) {
            for (Element e : scope.getLocalElements()) {
                elements.add(e);
            }
            
            scope = scope.getEnclosingScope();
        }
        
        for (Element e : elements) {
            if (!kinds.contains(e.getKind()))
                continue;
            
            float distance = UiUtils.getDistance(e.getSimpleName().toString(), text);
            
            LOG.log(Level.FINE, "element={0}, simple name={1}, distance={2}", new Object[] {e, e.getSimpleName(), distance});
            
            if (distance < 3) {
                foundProposals.add(new FixImpl(adoc, adoc.createPosition(ident.offset(null)), adoc.createPosition(ident.offset(null) + ident.length()), e.getSimpleName().toString()));
            }
        }
        
        return foundProposals;
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
            return null;
        }
    }
    
    public void cancel() {
    }

    public String getId() {
        return TypoDetector.class.getName();
    }

    public String getDisplayName() {
        return "Typo detector";
    }

    public String getDescription() {
        return "Typo detector";
    }

    static final class FixImpl implements Fix {
        
        private Document doc;
        private Position start;
        private Position end;
        private String nueName;
    
        public FixImpl(Document doc, Position start, Position end,
                       String nueName) {
            this.doc = doc;
            this.start = start;
            this.end = end;
            this.nueName = nueName;
        }

        
        public String getText() {
            return "Change to " + nueName;
        }

        public ChangeInfo implement() {
            try {
                NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {
                    public void run() {
                        try {
                            doc.remove(start.getOffset(), end.getOffset() - start.getOffset());
                            doc.insertString(start.getOffset(), nueName, null);
                        } catch (BadLocationException e) {
                            Exceptions.printStackTrace(e);
                        }
                    }
                });
            } catch (BadLocationException e) {
                Exceptions.printStackTrace(e);
            }
            
            return null;
        }
        
        public String toDebugString() {
            return "rename: " + nueName;
        }
    }
    
}
