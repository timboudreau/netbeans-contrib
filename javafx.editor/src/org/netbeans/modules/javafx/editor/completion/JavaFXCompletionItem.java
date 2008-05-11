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

package org.netbeans.modules.javafx.editor.completion;

import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javafx.api.JavafxcTrees;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.*;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.NbBundle;
import org.openide.xml.XMLUtil;

/**
 *
 * @author Dusan Balek
 */
public abstract class JavaFXCompletionItem implements CompletionItem {
    public static final String COLOR_END = "</font>"; //NOI18N
    public static final String STRIKE = "<s>"; //NOI18N
    public static final String STRIKE_END = "</s>"; //NOI18N
    public static final String BOLD = "<b>"; //NOI18N
    public static final String BOLD_END = "</b>"; //NOI18N

    protected int substitutionOffset;
    
    protected static int SMART_TYPE = 1000;
    private static final String GENERATE_TEXT = NbBundle.getMessage(JavaFXCompletionItem.class, "generate_Lbl");

    public static final JavaFXCompletionItem createKeywordItem(String kwd, String postfix, int substitutionOffset, boolean smartType) {
        return new KeywordItem(kwd, 0, postfix, substitutionOffset, smartType);
    }
    
    public static final JavaFXCompletionItem createPackageItem(String pkgFQN, int substitutionOffset, boolean isDeprecated) {
        return new PackageItem(pkgFQN, substitutionOffset, isDeprecated);
    }

    public static final JavaFXCompletionItem createVariableItem(String varName, int substitutionOffset, boolean smartType) {
        return new VariableItem(null, varName, substitutionOffset, smartType);
    }
    
    public static final JavaFXCompletionItem createExecutableItem(ExecutableElement elem, ExecutableType type, int substitutionOffset, boolean isInherited, boolean isDeprecated, boolean inImport, boolean smartType) {
        switch (elem.getKind()) {
            case METHOD:
                return new MethodItem(elem, type, substitutionOffset, isInherited, isDeprecated, inImport, smartType);
            default:
                throw new IllegalArgumentException("kind=" + elem.getKind());
        }
    }
    
    public static final JavaFXCompletionItem createTypeItem(TypeElement elem, DeclaredType type, int substitutionOffset, boolean isDeprecated, boolean insideNew, boolean smartType) {
        switch (elem.getKind()) {
            case CLASS:
                return new ClassItem(elem, type, 0, substitutionOffset, isDeprecated, insideNew, smartType);
            default:
                throw new IllegalArgumentException("kind=" + elem.getKind());
        }
    }

    protected JavaFXCompletionItem(int substitutionOffset) {
        this.substitutionOffset = substitutionOffset;
    }
    
    public void defaultAction(JTextComponent component) {
        if (component != null) {
            Completion.get().hideDocumentation();
            Completion.get().hideCompletion();
            int caretOffset = component.getSelectionEnd();
            substituteText(component, substitutionOffset, caretOffset - substitutionOffset, null);
        }
    }

    public void processKeyEvent(KeyEvent evt) {
        if (evt.getID() == KeyEvent.KEY_TYPED) {
            switch (evt.getKeyChar()) {
                case ':':
                case ';':
                case ',':
                case '(':
                    Completion.get().hideDocumentation();
                    Completion.get().hideCompletion();
                    JTextComponent component = (JTextComponent)evt.getSource();
                    int caretOffset = component.getSelectionEnd();
                    substituteText(component, substitutionOffset, caretOffset - substitutionOffset, Character.toString(evt.getKeyChar()));
                    evt.consume();
                    break;
                case '.':
                    Completion.get().hideDocumentation();
                    component = (JTextComponent)evt.getSource();
                    caretOffset = component.getSelectionEnd();
                    substituteText(component, substitutionOffset, caretOffset - substitutionOffset, Character.toString(evt.getKeyChar()));
                    evt.consume();
                    caretOffset = component.getSelectionEnd();
                    try {
                        if (caretOffset > 0 && !".".equals(component.getDocument().getText(caretOffset - 1, 1))) {
                            Completion.get().hideCompletion();
                            break;
                        }
                    } catch (BadLocationException ble) {}
                    Completion.get().showCompletion();
                    break;
            }
        }
    }

    public boolean instantSubstitution(JTextComponent component) {
        if (component != null) {
            try {
                int caretOffset = component.getSelectionEnd();
                if (caretOffset > substitutionOffset) {
                    String text = component.getDocument().getText(substitutionOffset, caretOffset - substitutionOffset);
                    if (!getInsertPrefix().toString().startsWith(text)) {
                        return false;
                    }
                }
            }
            catch (BadLocationException ble) {}
        }
        defaultAction(component);
        return true;
    }
    
    public CompletionTask createDocumentationTask() {
        return null;
    }
    
    public CompletionTask createToolTipTask() {
        return null;
    }
    
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(getLeftHtmlText(), getRightHtmlText(), g, defaultFont);
    }
    
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(getIcon(), getLeftHtmlText(), getRightHtmlText(), g, defaultFont, defaultColor, width, height, selected);
    }

    protected ImageIcon getIcon() {
        return null;
    }
    
    protected String getLeftHtmlText() {
        return null;
    }
    
    protected String getRightHtmlText() {
        return null;
    }

    protected void substituteText(JTextComponent c, int offset, int len, String toAdd) {
        BaseDocument doc = (BaseDocument)c.getDocument();
        CharSequence prefix = getInsertPrefix();
        if (prefix == null)
            return;
        StringBuilder text = new StringBuilder(prefix);
        int semiPos = toAdd != null && toAdd.endsWith(";") ? findPositionForSemicolon(c) : -2; //NOI18N
        if (semiPos > -2)
            toAdd = toAdd.length() > 1 ? toAdd.substring(0, toAdd.length() - 1) : null;
        if (toAdd != null && !toAdd.equals("\n")) {//NOI18N
            char ch;
            int i = 0;
            while(i < toAdd.length() && (ch = toAdd.charAt(i)) <= ' ' ) {
                text.append(ch);
                i++;
            }
            if (i > 0)
                toAdd = toAdd.substring(i);
            TokenSequence<JFXTokenId> sequence = JavaFXCompletionProvider.getJavaFXTokenSequence(TokenHierarchy.get(doc), offset + len);
            if (sequence == null || !sequence.moveNext() && !sequence.movePrevious()) {
                text.append(toAdd);
                toAdd = null;
            }
            boolean added = false;
            while(toAdd != null && toAdd.length() > 0) {
                String tokenText = sequence.token().text().toString();
                if (tokenText.startsWith(toAdd)) {
                    len = sequence.offset() - offset + toAdd.length();
                    text.append(toAdd);
                    toAdd = null;
                } else if (toAdd.startsWith(tokenText)) {
                    sequence.moveNext();
                    len = sequence.offset() - offset;
                    text.append(toAdd.substring(0, tokenText.length()));
                    toAdd = toAdd.substring(tokenText.length());
                    added = true;
                } else if (sequence.token().id() == JFXTokenId.WS && sequence.token().text().toString().indexOf('\n') < 0) {//NOI18N
                    if (!sequence.moveNext()) {
                        text.append(toAdd);
                        toAdd = null;
                    }
                } else {
                    if (!added)
                        text.append(toAdd);
                    toAdd = null;
                }
            }
        }
        // Update the text
        doc.atomicLock();
        try {
            String textToReplace = doc.getText(offset, len);
            if (textToReplace.contentEquals(text)) {
                if (semiPos > -1)
                    doc.insertString(semiPos, ";", null); //NOI18N
                return;
            }                
            Position position = doc.createPosition(offset);
            Position semiPosition = semiPos > -1 ? doc.createPosition(semiPos) : null;
            doc.remove(offset, len);
            doc.insertString(position.getOffset(), text.toString(), null);
            if (semiPosition != null)
                doc.insertString(semiPosition.getOffset(), ";", null);
        } catch (BadLocationException e) {
            // Can't update
        } finally {
            doc.atomicUnlock();
        }
    }
            
    static class KeywordItem extends JavaFXCompletionItem {
        
        private static final String JAVA_KEYWORD = "org/netbeans/modules/java/editor/resources/javakw_16.png"; //NOI18N
        private static final String KEYWORD_COLOR = "<font color=#000099>"; //NOI18N
        private static ImageIcon icon;
        
        private String kwd;
        private int dim;
        private String postfix;
        private boolean smartType;
        private String leftText;

        private KeywordItem(String kwd, int dim, String postfix, int substitutionOffset, boolean smartType) {
            super(substitutionOffset);
            this.kwd = kwd;
            this.dim = dim;
            this.postfix = postfix;
            this.smartType = smartType;
        }
        
        public int getSortPriority() {
            return smartType ? 600 - SMART_TYPE : 600;
        }
        
        public CharSequence getSortText() {
            return kwd;
        }
        
        public CharSequence getInsertPrefix() {
            return kwd;
        }
        
        protected ImageIcon getIcon(){
            if (icon == null) icon = new ImageIcon(org.openide.util.Utilities.loadImage(JAVA_KEYWORD));
            return icon;            
        }
        
        protected String getLeftHtmlText() {
            if (leftText == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(KEYWORD_COLOR);
                sb.append(BOLD);
                sb.append(kwd);
                for(int i = 0; i < dim; i++)
                    sb.append("[]"); //NOI18N
                sb.append(BOLD_END);
                sb.append(COLOR_END);
                leftText = sb.toString();
            }
            return leftText;
        }
        
        protected void substituteText(JTextComponent c, int offset, int len, String toAdd) {
            if (dim == 0) {
                super.substituteText(c, offset, len, toAdd != null ? toAdd : postfix);
                return;
            }
            BaseDocument doc = (BaseDocument)c.getDocument();
            final StringBuilder text = new StringBuilder();
            int semiPos = toAdd != null && toAdd.endsWith(";") ? findPositionForSemicolon(c) : -2; //NOI18N
            if (semiPos > -2)
                toAdd = toAdd.length() > 1 ? toAdd.substring(0, toAdd.length() - 1) : null;
            if (toAdd != null && !toAdd.equals("\n")) {//NOI18N
                TokenSequence<JFXTokenId> sequence = JavaFXCompletionProvider.getJavaFXTokenSequence(TokenHierarchy.get(doc), offset + len);
                if (sequence == null || !sequence.moveNext() && !sequence.movePrevious()) {
                    text.append(toAdd);
                    toAdd = null;
                }
                boolean added = false;
                while(toAdd != null && toAdd.length() > 0) {
                    String tokenText = sequence.token().text().toString();
                    if (tokenText.startsWith(toAdd)) {
                        len = sequence.offset() - offset + toAdd.length();
                        text.append(toAdd);
                        toAdd = null;
                    } else if (toAdd.startsWith(tokenText)) {
                        sequence.moveNext();
                        len = sequence.offset() - offset;
                        text.append(toAdd.substring(0, tokenText.length()));
                        toAdd = toAdd.substring(tokenText.length());
                        added = true;
                    } else if (sequence.token().id() == JFXTokenId.WS && sequence.token().text().toString().indexOf('\n') < 0) {//NOI18N
                        if (!sequence.moveNext()) {
                            text.append(toAdd);
                            toAdd = null;
                        }
                    } else {
                        if (!added)
                            text.append(toAdd);
                        toAdd = null;
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            int cnt = 1;
            sb.append(kwd);
            for(int i = 0; i < dim; i++) {
                sb.append("[${PAR"); //NOI18N
                sb.append(cnt++);
                sb.append(" instanceof=\"int\" default=\"\"}]"); //NOI18N                
            }
            doc.atomicLock();
            try {
                Position semiPosition = semiPos > -1 ? doc.createPosition(semiPos) : null;
                if (len > 0)
                    doc.remove(offset, len);
                if (semiPosition != null)
                    doc.insertString(semiPosition.getOffset(), ";", null); //NOI18N
            } catch (BadLocationException e) {
                // Can't update
            } finally {
                doc.atomicUnlock();
            }
            CodeTemplateManager ctm = CodeTemplateManager.get(doc);
            if (ctm != null) {
                ctm.createTemporary(sb.append(text).toString()).insert(c);
            }
        }
    
        public String toString() {
            return kwd;
        }        
    }
    
    static class PackageItem extends JavaFXCompletionItem {
        
        private static final String PACKAGE = "org/netbeans/modules/java/editor/resources/package.gif"; // NOI18N
        private static final String PACKAGE_COLOR = "<font color=#005600>"; //NOI18N
        private static ImageIcon icon;
        
        private boolean isDeprecated;
        private String simpleName;
        private String sortText;
        private String leftText;

        private PackageItem(String pkgFQN, int substitutionOffset, boolean isDeprecated) {
            super(substitutionOffset);
            this.isDeprecated = isDeprecated;
            int idx = pkgFQN.lastIndexOf('.');
            this.simpleName = idx < 0 ? pkgFQN : pkgFQN.substring(idx + 1);
            this.sortText = this.simpleName + "#" + pkgFQN; //NOI18N
        }
        
        public int getSortPriority() {
            return 900;
        }
        
        public CharSequence getSortText() {
            return sortText;
        }
        
        public CharSequence getInsertPrefix() {
            return simpleName;
        }
        
        protected ImageIcon getIcon(){
            if (icon == null) icon = new ImageIcon(org.openide.util.Utilities.loadImage(PACKAGE));
            return icon;            
        }
        
        protected String getLeftHtmlText() {
            if (leftText == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(PACKAGE_COLOR);
                if (isDeprecated)
                    sb.append(STRIKE);
                sb.append(simpleName);
                if (isDeprecated)
                    sb.append(STRIKE_END);
                sb.append(COLOR_END);
                leftText = sb.toString();
            }
            return leftText;
        }
        
        public String toString() {
            return simpleName;
        }        
    }

    static class VariableItem extends JavaFXCompletionItem {
        
        private static final String LOCAL_VARIABLE = "org/netbeans/modules/editor/resources/completion/localVariable.gif"; //NOI18N
        private static final String PARAMETER_COLOR = "<font color=#00007c>"; //NOI18N
        private static ImageIcon icon;

        private String varName;
        private boolean smartType;
        private String typeName;
        private String leftText;
        private String rightText;
        
        private VariableItem(TypeMirror type, String varName, int substitutionOffset, boolean smartType) {
            super(substitutionOffset);
            this.varName = varName;
            this.smartType = smartType;
            this.typeName = type != null ? type.toString() : null;
        }
        
        public int getSortPriority() {
            return smartType ? 200 - SMART_TYPE : 200;
        }
        
        public CharSequence getSortText() {
            return varName;
        }
        
        public CharSequence getInsertPrefix() {
            return varName;
        }

        protected String getLeftHtmlText() {
            if (leftText == null)
                leftText = PARAMETER_COLOR + BOLD + varName + BOLD_END + COLOR_END;
            return leftText;
        }
        
        protected String getRightHtmlText() {
            if (rightText == null)
                rightText = escape(typeName);
            return rightText;
        }
        
        protected ImageIcon getIcon(){
            if (icon == null) icon = new ImageIcon(org.openide.util.Utilities.loadImage(LOCAL_VARIABLE));
            return icon;            
        }

        public String toString() {
            return (typeName != null ? typeName + " " : "") + varName; //NOI18N
        }
   }
        static class MethodItem extends JavaFXCompletionItem {
        
        private static final String METHOD_PUBLIC = "org/netbeans/modules/editor/resources/completion/method_16.png"; //NOI18N
        private static final String METHOD_PROTECTED = "org/netbeans/modules/editor/resources/completion/method_protected_16.png"; //NOI18N
        private static final String METHOD_PACKAGE = "org/netbeans/modules/editor/resources/completion/method_package_private_16.png"; //NOI18N
        private static final String METHOD_PRIVATE = "org/netbeans/modules/editor/resources/completion/method_private_16.png"; //NOI18N        
        private static final String METHOD_ST_PUBLIC = "org/netbeans/modules/editor/resources/completion/method_static_16.png"; //NOI18N
        private static final String METHOD_ST_PROTECTED = "org/netbeans/modules/editor/resources/completion/method_static_protected_16.png"; //NOI18N
        private static final String METHOD_ST_PRIVATE = "org/netbeans/modules/editor/resources/completion/method_static_private_16.png"; //NOI18N
        private static final String METHOD_ST_PACKAGE = "org/netbeans/modules/editor/resources/completion/method_static_package_private_16.png"; //NOI18N
        private static final String METHOD_COLOR = "<font color=#000000>"; //NOI18N
        private static final String PARAMETER_NAME_COLOR = "<font color=#a06001>"; //NOI18N
        private static ImageIcon icon[][] = new ImageIcon[2][4];

        private boolean isInherited;
        private boolean isDeprecated;
        private boolean inImport;
        private boolean smartType;
        private String simpleName;
        protected Set<Modifier> modifiers;
        private List<ParamDesc> params;
        private String typeName;
        private boolean isPrimitive;
        private String sortText;
        private String leftText;
        private String rightText;
        
        private MethodItem(ExecutableElement elem, ExecutableType type, int substitutionOffset, boolean isInherited, boolean isDeprecated, boolean inImport, boolean smartType) {
            super(substitutionOffset);
            this.isInherited = isInherited;
            this.isDeprecated = isDeprecated;
            this.inImport = inImport;
            this.smartType = smartType;
            this.simpleName = elem.getSimpleName().toString();
            this.modifiers = elem.getModifiers();
            this.params = new ArrayList<ParamDesc>();
            Iterator<? extends VariableElement> it = elem.getParameters().iterator();
            Iterator<? extends TypeMirror> tIt = type.getParameterTypes().iterator();
            while(it.hasNext() && tIt.hasNext()) {
                TypeMirror tm = tIt.next();
                this.params.add(new ParamDesc(tm.toString(), tm.toString(), it.next().getSimpleName().toString()));
            }
            TypeMirror retType = type.getReturnType();
            this.typeName = retType.toString();
            this.isPrimitive = retType.getKind().isPrimitive() || retType.getKind() == TypeKind.VOID;
        }
        
        public int getSortPriority() {
            return smartType ? 500 - SMART_TYPE : 500;
        }
        
        public CharSequence getSortText() {
            if (sortText == null) {
                StringBuilder sortParams = new StringBuilder();
                sortParams.append('(');
                int cnt = 0;
                for(Iterator<ParamDesc> it = params.iterator(); it.hasNext();) {
                    ParamDesc param = it.next();
                    sortParams.append(param.typeName);
                    if (it.hasNext()) {
                        sortParams.append(',');
                    }
                    cnt++;
                }
                sortParams.append(')');
                sortText = simpleName + "#" + ((cnt < 10 ? "0" : "") + cnt) + "#" + sortParams.toString(); //NOI18N
            }
            return sortText;
        }
        
        public CharSequence getInsertPrefix() {
            return simpleName;
        }
        
        protected String getLeftHtmlText() {
            if (leftText == null) {
                StringBuilder lText = new StringBuilder();
                lText.append(METHOD_COLOR);
                if (!isInherited)
                    lText.append(BOLD);
                if (isDeprecated)
                    lText.append(STRIKE);
                lText.append(simpleName);
                if (isDeprecated)
                    lText.append(STRIKE_END);
                if (!isInherited)
                    lText.append(BOLD_END);
                lText.append(COLOR_END);
                lText.append('(');
                for (Iterator<ParamDesc> it = params.iterator(); it.hasNext();) {
                    ParamDesc paramDesc = it.next();
                    lText.append(escape(paramDesc.typeName));
                    lText.append(' ');
                    lText.append(PARAMETER_NAME_COLOR);
                    lText.append(paramDesc.name);
                    lText.append(COLOR_END);
                    if (it.hasNext()) {
                        lText.append(", "); //NOI18N
                    }
                }
                lText.append(')');
                return lText.toString();
            }
            return leftText;
        }
        
        protected String getRightHtmlText() {
            if (rightText == null)
                rightText = escape(typeName);
            return rightText;
        }
        
//        public CompletionTask createDocumentationTask() {
//            return JavaFXCompletionProvider.createDocTask(elementHandle);
//        }

        protected ImageIcon getIcon() {
            int level = getProtectionLevel(modifiers);
            boolean isStatic = modifiers.contains(Modifier.STATIC);
            ImageIcon cachedIcon = icon[isStatic?1:0][level];
            if (cachedIcon != null)
                return cachedIcon;
            
            String iconPath = METHOD_PUBLIC;            
            if (isStatic) {
                switch (level) {
                    case PRIVATE_LEVEL:
                        iconPath = METHOD_ST_PRIVATE;
                        break;

                    case PACKAGE_LEVEL:
                        iconPath = METHOD_ST_PACKAGE;
                        break;

                    case PROTECTED_LEVEL:
                        iconPath = METHOD_ST_PROTECTED;
                        break;

                    case PUBLIC_LEVEL:
                        iconPath = METHOD_ST_PUBLIC;
                        break;
                }
            }else{
                switch (level) {
                    case PRIVATE_LEVEL:
                        iconPath = METHOD_PRIVATE;
                        break;

                    case PACKAGE_LEVEL:
                        iconPath = METHOD_PACKAGE;
                        break;

                    case PROTECTED_LEVEL:
                        iconPath = METHOD_PROTECTED;
                        break;

                    case PUBLIC_LEVEL:
                        iconPath = METHOD_PUBLIC;
                        break;
                }
            }
            ImageIcon newIcon = new ImageIcon(org.openide.util.Utilities.loadImage(iconPath));
            icon[isStatic?1:0][level] = newIcon;
            return newIcon;            
        }
        
        protected void substituteText(final JTextComponent c, int offset, int len, String toAdd) {
            if (toAdd == null) {
                if (isPrimitive) {
                    try {
                        final String[] ret = new String[1];
                        JavaFXSource js = JavaFXSource.forDocument(c.getDocument());
                        js.runUserActionTask(new Task<CompilationController>() {

                            public void run(CompilationController controller) throws Exception {
                                controller.toPhase(Phase.PARSED);
                                TreePath tp = controller.getTreeUtilities().pathFor(c.getSelectionEnd());
                                Tree tree = tp.getLeaf();
                                if (tree.getKind() == Tree.Kind.IDENTIFIER || tree.getKind() == Tree.Kind.PRIMITIVE_TYPE)
                                    tp = tp.getParentPath();
                                if (tp.getLeaf().getKind() == Tree.Kind.MEMBER_SELECT ||
                                    (tp.getLeaf().getKind() == Tree.Kind.METHOD_INVOCATION && ((MethodInvocationTree)tp.getLeaf()).getMethodSelect() == tree))
                                    tp = tp.getParentPath();
                                if (tp.getLeaf().getKind() == Tree.Kind.EXPRESSION_STATEMENT || tp.getLeaf().getKind() == Tree.Kind.BLOCK)
                                    ret[0] = ";"; //NOI18N
                            }
                        }, true);
                        toAdd = ret[0];
                    } catch (IOException ex) {
                    }
                }
            }
            if (inImport || params.isEmpty()) {
                String add = "()"; //NOI18N
                if (toAdd != null && !add.startsWith(toAdd))
                    add += toAdd;
                super.substituteText(c, offset, len, add);
                if ("(".equals(toAdd)) //NOI18N
                    c.setCaretPosition(c.getCaretPosition() - 1);
            } else {
                String add = "()"; //NOI18N
                if (toAdd != null && !add.startsWith(toAdd))
                    add += toAdd;
                BaseDocument doc = (BaseDocument)c.getDocument();
                String text = ""; //NOI18N
                int semiPos = add.endsWith(";") ? findPositionForSemicolon(c) : -2; //NOI18N
                if (semiPos > -2)
                    add = add.length() > 1 ? add.substring(0, add.length() - 1) : null;
                JavaFXSource js = JavaFXSource.forDocument(c.getDocument());
                TokenSequence<JFXTokenId> sequence = js.getTokenHierarchy().tokenSequence();
                sequence = sequence.subSequence(offset + len);
                if (sequence == null || !sequence.moveNext() && !sequence.movePrevious()) {
                    text += add;
                    add = null;
                }
                boolean added = false;
                while(add != null && add.length() > 0) {
                    String tokenText = sequence.token().text().toString();
                    if (tokenText.startsWith(add)) {
                        len = sequence.offset() - offset + add.length();
                        text += add;
                        add = null;
                    } else if (add.startsWith(tokenText)) {
                        sequence.moveNext();
                        len = sequence.offset() - offset;
                        text += add.substring(0, tokenText.length());
                        add = add.substring(tokenText.length());
                        added = true;
                    } else if (sequence.token().id() == JFXTokenId.WS && sequence.token().text().toString().indexOf('\n') < 0) {//NOI18N
                        if (!sequence.moveNext()) {
                            text += add;
                            add = null;
                        }
                    } else {
                        if (!added)
                            text += add;
                        add = null;
                    }
                }
                doc.atomicLock();
                try {
                    Position semiPosition = semiPos > -1 ? doc.createPosition(semiPos) : null;
                    if (len > 0)
                        doc.remove(offset, len);
                    doc.insertString(offset, getInsertPrefix().toString(), null);                    
                    if (semiPosition != null)
                        doc.insertString(semiPosition.getOffset(), ";", null); //NOI18N
                } catch (BadLocationException e) {
                    // Can't update
                } finally {
                    doc.atomicUnlock();
                }
                CodeTemplateManager ctm = CodeTemplateManager.get(doc);
                if (ctm != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append('('); //NOI18N
                    if (text.length() > 1) {
                        for (Iterator<ParamDesc> it = params.iterator(); it.hasNext();) {
                            ParamDesc paramDesc = it.next();
                            sb.append("${"); //NOI18N
                            sb.append(paramDesc.name);
                            sb.append('}'); //NOI18N
                            if (it.hasNext())
                                sb.append(", "); //NOI18N
                        }
                        sb.append(')');//NOI18N
                        if (text.length() > 2)
                            sb.append(text.substring(2));
                    }
                    ctm.createTemporary(sb.toString()).insert(c);
                    Completion.get().showToolTip();
                }
            }
        }        

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Modifier mod : modifiers) {
                sb.append(mod.toString());
                sb.append(' ');
            }
            sb.append(typeName);
            sb.append(' ');
            sb.append(simpleName);
            sb.append('(');
            for (Iterator<ParamDesc> it = params.iterator(); it.hasNext();) {
                ParamDesc paramDesc = it.next();
                sb.append(paramDesc.typeName);
                sb.append(' ');
                sb.append(paramDesc.name);
                if (it.hasNext()) {
                    sb.append(", "); //NOI18N
                }
            }
            sb.append(')');
            return sb.toString();
        }
    }    
    static class ClassItem extends JavaFXCompletionItem {
        
        private static final String CLASS = "org/netbeans/modules/editor/resources/completion/class_16.png"; //NOI18N
        private static final String CLASS_COLOR = "<font color=#560000>"; //NOI18N
        private static final String PKG_COLOR = "<font color=#808080>"; //NOI18N
        private static ImageIcon icon;
        
        private int dim;
        private boolean isDeprecated;
        private boolean insideNew;
        private boolean smartType;
        private String simpleName;
        private String typeName;
        private CharSequence sortText;
        private String leftText;
        private DeclaredType type;
        
        private ClassItem(TypeElement elem, DeclaredType type, int dim, int substitutionOffset, boolean isDeprecated, boolean insideNew, boolean smartType) {
            super(substitutionOffset);
            this.dim = dim;
            this.isDeprecated = isDeprecated;
            this.insideNew = insideNew;
            this.smartType = smartType;
            this.simpleName = elem.getSimpleName().toString();
            this.typeName = type != null ? type.toString() : null;
            this.type = type;
            this.sortText = this.simpleName;
        }
        
        public int getSortPriority() {
            return smartType ? 800 - SMART_TYPE : 800;
        }
        
        public CharSequence getSortText() {
            return sortText;
        }
        
        public CharSequence getInsertPrefix() {
            return simpleName;
        }

        public boolean instantSubstitution(JTextComponent component) {
            return false;
        }
    
//        public CompletionTask createDocumentationTask() {
//            return typeHandle.getKind() == TypeKind.DECLARED ? JavaCompletionProvider.createDocTask(ElementHandle.from(typeHandle)) : null;
//        }

        protected ImageIcon getIcon(){
            if (icon == null) icon = new ImageIcon(org.openide.util.Utilities.loadImage(CLASS));
            return icon;            
        }

        protected String getLeftHtmlText() {
            if (leftText == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(getColor());
                if (isDeprecated)
                    sb.append(STRIKE);
                sb.append(escape(typeName));
                for(int i = 0; i < dim; i++)
                    sb.append("[]"); //NOI18N
                if (isDeprecated)
                    sb.append(STRIKE_END);
                sb.append(COLOR_END);
                leftText = sb.toString();
            }
            return leftText;
        }
        
        protected String getColor() {
            return CLASS_COLOR;
        }

        protected void substituteText(final JTextComponent c, final int offset, int len, String toAdd) {
            final BaseDocument doc = (BaseDocument)c.getDocument();
            final StringBuilder text = new StringBuilder();
            final int semiPos = toAdd != null && toAdd.endsWith(";") ? findPositionForSemicolon(c) : -2; //NOI18N
            if (semiPos > -2)
                toAdd = toAdd.length() > 1 ? toAdd.substring(0, toAdd.length() - 1) : null;
            if (toAdd != null && !toAdd.equals("\n")) {//NOI18N
                JavaFXSource js = JavaFXSource.forDocument(c.getDocument());
                TokenSequence<JFXTokenId> sequence = js.getTokenHierarchy().tokenSequence();
                sequence = sequence.subSequence(offset + len);
                if (sequence == null || !sequence.moveNext() && !sequence.movePrevious()) {
                    text.append(toAdd);
                    toAdd = null;
                }
                boolean added = false;
                while(toAdd != null && toAdd.length() > 0) {
                    String tokenText = sequence.token().text().toString();
                    if (tokenText.startsWith(toAdd)) {
                        len = sequence.offset() - offset + toAdd.length();
                        text.append(toAdd);
                        toAdd = null;
                    } else if (toAdd.startsWith(tokenText)) {
                        sequence.moveNext();
                        len = sequence.offset() - offset;
                        text.append(toAdd.substring(0, tokenText.length()));
                        toAdd = toAdd.substring(tokenText.length());
                        added = true;
                    } else if (sequence.token().id() == JFXTokenId.WS && sequence.token().text().toString().indexOf('\n') < 0) {//NOI18N
                        if (!sequence.moveNext()) {
                            text.append(toAdd);
                            toAdd = null;
                        }
                    } else {
                        if (!added)
                            text.append(toAdd);
                        toAdd = null;
                    }
                }
            }
            final int finalLen = len;
            JavaFXSource js = JavaFXSource.forDocument(doc);
            try {
                js.runUserActionTask(new Task<CompilationController>() {

                    public void run(CompilationController controller) throws IOException {
                        controller.toPhase(Phase.ANALYZED);
                        TypeElement elem = (TypeElement)type.asElement();
                        boolean asTemplate = false;
                        StringBuilder sb = new StringBuilder();
                        int cnt = 1;
                        sb.append("${PAR"); //NOI18N
                        sb.append(cnt++);
                        if ((type == null || type.getKind() != TypeKind.ERROR) &&
                                EnumSet.range(ElementKind.PACKAGE, ElementKind.INTERFACE).contains(elem.getEnclosingElement().getKind())) {
                            sb.append(" type=\""); //NOI18N
                            sb.append(elem.getQualifiedName());
                            sb.append("\" default=\""); //NOI18N
                            sb.append(elem.getSimpleName());
                        } else {
                            sb.append(" default=\""); //NOI18N
                            sb.append(elem.getQualifiedName());
                        }
                        sb.append("\" editable=false}"); //NOI18N
                        Iterator<? extends TypeMirror> tas = type != null ? type.getTypeArguments().iterator() : null;
                        if (tas != null && tas.hasNext()) {
                            sb.append('<'); //NOI18N
                            while (tas.hasNext()) {
                                TypeMirror ta = tas.next();
                                sb.append("${PAR"); //NOI18N
                                sb.append(cnt++);
                                if (ta.getKind() == TypeKind.TYPEVAR) {
                                    TypeVariable tv = (TypeVariable)ta;
                                    if (elem == tv.asElement().getEnclosingElement()) {
                                        sb.append(" type=\""); //NOI18N
                                        ta = tv.getUpperBound();
                                        sb.append(ta.toString());
                                        sb.append("\" default=\""); //NOI18N
                                        sb.append(ta.toString());
                                    } else {
                                        sb.append(" editable=false default=\""); //NOI18N
                                        sb.append(ta.toString());
                                        asTemplate = true;
                                    }
                                    sb.append("\"}"); //NOI18N
                                } else if (ta.getKind() == TypeKind.WILDCARD) {
                                    sb.append(" type=\""); //NOI18N
                                    TypeMirror bound = ((WildcardType)ta).getExtendsBound();
                                    if (bound == null)
                                        bound = ((WildcardType)ta).getSuperBound();
                                    sb.append(bound != null ? bound.toString() : "Object"); //NOI18N
                                    sb.append("\" default=\""); //NOI18N
                                    sb.append(bound != null ? bound.toString() : "Object"); //NOI18N
                                    sb.append("\"}"); //NOI18N
                                    asTemplate = true;
                                } else if (ta.getKind() == TypeKind.ERROR) {
                                    sb.append(" default=\""); //NOI18N
                                    sb.append(((ErrorType)ta).asElement().getSimpleName());
                                    sb.append("\"}"); //NOI18N
                                    asTemplate = true;
                                } else {
                                    sb.append(" type=\""); //NOI18N
                                    sb.append(ta.toString());
                                    sb.append("\" default=\""); //NOI18N
                                    sb.append(ta.toString());
                                    sb.append("\" editable=false}"); //NOI18N
                                    asTemplate = true;
                                }
                                if (tas.hasNext())
                                    sb.append(", "); //NOI18N
                            }
                            sb.append('>'); //NOI18N
                        }
                        for(int i = 0; i < dim; i++) {
                            sb.append("[${PAR"); //NOI18N
                            sb.append(cnt++);
                            sb.append(" instanceof=\"int\" default=\"\"}]"); //NOI18N
                            asTemplate = true;
                        }
                        if (asTemplate) {
                            if (insideNew)
                                sb.append("${cursor completionInvoke}"); //NOI18N
                            if (finalLen > 0) {
                                doc.atomicLock();
                                try {
                                    doc.remove(offset, finalLen);
                                } catch (BadLocationException e) {
                                    // Can't update
                                } finally {
                                    doc.atomicUnlock();
                                }
                            }
                            CodeTemplateManager ctm = CodeTemplateManager.get(doc);
                            if (ctm != null)
                                ctm.createTemporary(sb.append(text).toString()).insert(c);
                        } else {
                            // Update the text
                            doc.atomicLock();
                            try {
                                Position semiPosition = semiPos > -1 && !insideNew ? doc.createPosition(semiPos) : null;
                                TreePath tp = controller.getTreeUtilities().pathFor(offset);
                                CharSequence cs = elem.getSimpleName(); 
                                if (!insideNew)
                                    cs = text.insert(0, cs);
                                String textToReplace = doc.getText(offset, finalLen);
                                if (textToReplace.contentEquals(cs)) return;
                                doc.remove(offset, finalLen);
                                doc.insertString(offset, cs.toString(), null);
                                if (semiPosition != null)
                                    doc.insertString(semiPosition.getOffset(), ";", null); //NOI18N
                            } catch (BadLocationException e) {
                                // Can't update
                            } finally {
                                doc.atomicUnlock();
                            }
                            if (insideNew && type != null && type.getKind() == TypeKind.DECLARED) {
                                ExecutableElement ctor = null;
                                JavafxcTrees trees = controller.getTrees();
                                Scope scope = controller.getTreeUtilities().scopeFor(offset);
                                int val = 0; // no constructors seen yet
                                for (ExecutableElement ee : ElementFilter.constructorsIn(elem.getEnclosedElements())) {
                                    if (trees.isAccessible(scope, ee, type)) {
                                        if (ctor != null) {
                                            val = 2; // more than one accessible constructors seen
                                            break;
                                        }
                                        ctor = ee;
                                    }
                                    val = 1; // constructor seen
                                }
                                if (val != 1 || ctor != null) {
                                    final JavaFXCompletionItem item = null;
                                    
                                    // TODO:
//                                            val == 0 ? 
//                                                createDefaultConstructorItem(elem, offset, true) :
//                                                val == 2 || Utilities.hasAccessibleInnerClassConstructor(elem, scope, trees) ? 
//                                                    null : createExecutableItem(ctor, (ExecutableType)controller.getTypes().asMemberOf(type, ctor), offset, false, false, false, true);
                                    try {
                                        final Position offPosition = doc.createPosition(offset);
                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                if (item != null) {
                                                    item.substituteText(c, offPosition.getOffset(), c.getSelectionEnd() - offPosition.getOffset(), text.toString());
                                                } else {
                                                    //Temporary ugly solution
                                                    SwingUtilities.invokeLater(new Runnable() {
                                                        public void run() {
                                                            Completion.get().showCompletion();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                    catch (BadLocationException e) {}
                                }
                            }
                        }
                    }
                }, true);
            } catch (IOException ioe) {                
            }
        }
        
        public String toString() {
            return simpleName;
        }        
    }

    private static final int PUBLIC_LEVEL = 3;
    private static final int PROTECTED_LEVEL = 2;
    private static final int PACKAGE_LEVEL = 1;
    private static final int PRIVATE_LEVEL = 0;
    
    private static int getProtectionLevel(Set<Modifier> modifiers) {
        if(modifiers.contains(Modifier.PUBLIC))
            return PUBLIC_LEVEL;
        if(modifiers.contains(Modifier.PROTECTED))
            return PROTECTED_LEVEL;
        if(modifiers.contains(Modifier.PRIVATE))
            return PRIVATE_LEVEL;
        return PACKAGE_LEVEL;
    }
    
    private static String escape(String s) {
        if (s != null) {
            try {
                return XMLUtil.toAttributeValue(s);
            } catch (Exception ex) {}
        }
        return s;
    }
    
    private static int findPositionForSemicolon(JTextComponent c) {
        final int[] ret = new int[] {-2};
        final int offset = c.getSelectionEnd();
        try {
            JavaFXSource js = JavaFXSource.forDocument(c.getDocument());
            js.runUserActionTask(new Task<CompilationController>() {

                public void run(CompilationController controller) throws Exception {
                    controller.toPhase(JavaFXSource.Phase.PARSED);
                    Tree t = null;
                    TreePath tp = controller.getTreeUtilities().pathFor(offset);
                    while (t == null && tp != null) {
                        switch(tp.getLeaf().getKind()) {
                            case EXPRESSION_STATEMENT:
                                ExpressionTree expr = ((ExpressionStatementTree)tp.getLeaf()).getExpression();
                                if (expr != null && expr.getKind() == Tree.Kind.ERRONEOUS)
                                    break;
                            case IMPORT:                                
                                t = tp.getLeaf();
                                break;
                            case RETURN:
                                t = ((ReturnTree)tp.getLeaf()).getExpression();
                                break;
                            case THROW:
                                t = ((ThrowTree)tp.getLeaf()).getExpression();
                                break;
                        }
                        tp = tp.getParentPath();
                    }
                    if (t != null) {
                        SourcePositions sp = controller.getTrees().getSourcePositions();
                        int endPos = (int)sp.getEndPosition(tp.getCompilationUnit(), t);
                        TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(controller, offset, endPos);
                        if (ts != null) {
                            ret[0] = ts.token().id() == JFXTokenId.SEMI ? -1 : ts.offset() + ts.token().length();
                        }
                    } else {
                        TokenSequence<JFXTokenId> ts = controller.getTokenHierarchy().tokenSequence(JFXTokenId.language());
                        ts.move(offset);
                        if (ts.moveNext() &&  ts.token().id() == JFXTokenId.SEMI)
                            ret[0] = -1;
                    }
                }
            }, true);
        } catch (IOException ex) {
        }
        return ret[0];
    }
    
    private static TokenSequence<JFXTokenId> findLastNonWhitespaceToken(CompilationController controller, int startPos, int endPos) {
        TokenSequence<JFXTokenId> ts = controller.getTokenHierarchy().tokenSequence(JFXTokenId.language());
        ts.move(endPos);
        while(ts.movePrevious()) {
            int offset = ts.offset();
            if (offset < startPos)
                return null;
            switch (ts.token().id()) {
            case WS:
            case LINE_COMMENT:
            case COMMENT:
            case DOC_COMMENT:
                break;
            default:
                return ts;
            }
        }
        return null;
    }

    static class ParamDesc {
        private String fullTypeName;
        private String typeName;
        private String name;
    
        public ParamDesc(String fullTypeName, String typeName, String name) {
            this.fullTypeName = fullTypeName;
            this.typeName = typeName;
            this.name = name;
        }
    }
}
