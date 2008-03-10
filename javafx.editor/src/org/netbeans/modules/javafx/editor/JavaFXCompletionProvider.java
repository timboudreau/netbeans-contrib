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

package org.netbeans.modules.javafx.editor;

//import net.java.javafx.type.Attribute;
//import net.java.javafx.type.Type;
//import net.java.javafx.type.expr.VariableDeclarator;
//import net.java.javafx.typeImpl.Compilation;
//import net.java.javafx.typeImpl.completion.CompletionProcessor;
//import net.java.javafx.typeImpl.completion.Token;
import java.awt.EventQueue;
import javax.swing.Action;

//import net.java.javafx.typeImpl.Compilation;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import java.util.HashSet;

/**
 *
 * @author pvarghese
 */
public class JavaFXCompletionProvider /*implements CompletionProvider*/ {
    
    /** Creates a new instance of F3CompletionProvider */
    public JavaFXCompletionProvider() {
    }
    
    
//    String formatVar(VariableDeclarator decl) {
//        return decl.getVarName() + " " +formatType(decl.getType());
//    }
//    
//    String formatType(Type t) {
//        String typeName = t.getName();
//        int dot = typeName.lastIndexOf('.');
//        if (dot > 0) {
//            typeName = typeName.substring(dot + 1);
//        }
//        return typeName;
//    }
//    
//    String formatAttr(Attribute attr) {
//        StringBuffer buf = new StringBuffer();
//        buf.append(attr.getName());
//        buf.append(" ");
//        buf.append(formatType(attr.getType()));
//        if (attr.isOneToMany() || attr.isManyToMany()) {
//            if (attr.isOptional()) {
//                buf.append("*");
//            } else {
//                buf.append("+");
//            }
//        } else if (attr.isOptional()) {
//            buf.append("?");
//        }
//        buf.append(" - ");
//        buf.append(formatType(attr.getScope()));
//        return buf.toString();
//    }
//    
//    String formatOp(Type method) {
//        String name = method.getName();
//        StringBuffer buf = new StringBuffer();
//        buf.append(name);
//        buf.append("(");
//        Iterator iter = method.getAttributes();
//        String sep = "";
//        Attribute retType = null;
//        while (iter.hasNext()) {
//            Attribute attr = (Attribute)iter.next();
//            if (attr.getName().equals("this")) {
//                continue;
//            }
//            if (attr.getName().equals("return")) {
//                retType = attr;
//                continue;
//            }
//            buf.append(sep);
//            Type type = attr.getType();
//            buf.append(attr.getName());
//            buf.append(":");
//            String typeName = formatType(type);
//            buf.append(typeName);
//            if (attr.isOneToMany() || attr.isManyToMany()) {
//                if (attr.isOptional()) {
//                    buf.append("*");
//                } else {
//                    buf.append("+");
//                }
//            } else if (attr.isOptional()) {
//                buf.append("?");
//            }
//            sep = ", ";
//        }
//        buf.append(")");
//        buf.append(" ");
//        if (retType != null) {
//            buf.append(formatType(retType.getType()));
//        }
//        if (method.getScope() != null) {
//            buf.append(" - ");
//            buf.append(formatType(method.getScope()));
//        }
//        return buf.toString();
//        
//    }
    
    String formatType(Class type) {
        if (type.isArray()) {
            return formatType(type.getComponentType()) + "[]";
        }
        String typeName = type.getName();
        int dot = typeName.lastIndexOf('.');
        if (dot > 0) {
            typeName = typeName.substring(dot + 1);
        }
        return typeName;
    }
    
    
    String formatMethod(Method method) {
        String name = method.getName();
        StringBuffer buf = new StringBuffer();
        buf.append(name);
        buf.append("(");
        Class[] parms = method.getParameterTypes();
        String sep = "";
        for (int i = 0; i < parms.length; i++) {
            buf.append(sep);
            String typeName = formatType(parms[i]);
            buf.append(typeName);
            buf.append(" ");
            buf.append("arg"+i);
            sep = ", ";
        }
        buf.append(")");
        buf.append(" ");
        buf.append(formatType(method.getReturnType()));
        buf.append(" - ");
        buf.append(formatType(method.getDeclaringClass()));
        return buf.toString();
    }
    
//    java.util.Set getDerivedTypes(Type t) {
//        java.util.Set result = new HashSet();
//        getDerivedTypes(t.getDerivedTypes(), result);
//        return result;
//    }
//    
//    void getDerivedTypes(Iterator iter, java.util.Set result) {
//        while (iter.hasNext()) {
//            Type t = (Type)iter.next();
//            if (!result.contains(t)) {
//                result.add(t);
//                getDerivedTypes(t.getDerivedTypes(), result);
//            }
//        }
//    }
    
    String getIndentAtOffset(String sourceCode, int offset) {
        int nl = sourceCode.lastIndexOf("\n", offset) + 1;
        int end = nl;
        if (nl < sourceCode.length()) {
            while (end < sourceCode.length() && Character.isWhitespace(sourceCode.charAt(end))) {
                end++;
            }
            return sourceCode.substring(nl, end);
        }
        return "";
    }
    
    /**
     * Creates a task that performs a query of the given type on the given component.
     * <br>
     * This method is invoked in AWT thread only and the returned task
     * may either be synchronous (if it's not complex)
     * or it may be asynchonous
     * (see {@link org.netbeans.spi.editor.completion.support.AsyncCompletionTask}).
     * <br>
     * The task usually inspects the component's document, the
     * text up to the caret position and returns the appropriate result.
     *
     * @param queryType a type ot the query. It can be one of the {@link #COMPLETION_QUERY_TYPE},
     *  {@link #DOCUMENTATION_QUERY_TYPE}, or {@link #TOOLTIP_QUERY_TYPE}
     *  (but not their combination).
     * @param component a component on which the query is performed
     *
     * @return a task performing the query.
     */
//    public CompletionTask createTask(final int queryType, final JTextComponent component) {
//        return new AsyncCompletionTask(new AsyncCompletionQuery() {
//            protected void query(final CompletionResultSet resultSet, final Document doc, final int caretOffset) {
//                /*
//                 * The AsyncCompletionTask is called on the default RequestProcessor thread by the IDE framework. The event is then 
//                 * queued on the AWT-EventQueue thread for accessing the compilation.
//                 */
//                EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        List resultList = new LinkedList();
//                        JavaFXDocument document = (JavaFXDocument)component.getDocument();
//                        Compilation compilation = JavaFXPier.getCompilation(document);
//                        String fileName = document.getDataObject().getPrimaryFile().getPath();
//                        
//                        if (compilation == null) {
//                            resultSet.finish();
//                            return;
//                        }
//                        CompletionProcessor processor = new CompletionProcessor();
//                        String sourceCode = component.getText().replaceAll("\r\n", "\n");
//                        int offset = component.getCaretPosition() - 1;
//                        //System.out.println("offset: " + offset + " caretOffset = "+caretOffset);
//                        
//                        Object[] members = null;
//                        try {
//                            members = processor.getMembers(compilation, fileName, sourceCode, offset);
//                        } catch (Exception err) {
//                            // do nothing as the "as you type" validation will process the error annotations
//                            //ErrorManager.getDefault().notify(err);
//                            resultSet.finish();
//                            return;
//                        }
//  
//                        Token lastToken = processor.getLastTokenBeforeOffset();
//                        Token nextToken = processor.getNextTokenAfterOffset();
//
//                        String prefix = processor.getPrefix();
//
//                        if (prefix == null) {
//                            prefix = "";
//                        }
//                        else {
//                            if (prefix.length() > 0) {
//                                JavaFXTokenId javaFXTokenId = null;
//                                for (JavaFXTokenId javaFXToken: javaFXTokenId.values()) {
//                                    String token = javaFXToken.fixedText();
//                                    if ((token != null) &&
//                                        (prefix.length() <= token.length()) &&
//                                        (token.substring(0, prefix.length()).equals(prefix))) {
//                                        JavaFXCompletionItem item = new JavaFXCompletionItem(token, token, JavaFXCompletionItem.KEYWORD, offset - prefix.length(), 0, null);
//                                        resultList.add(item);
//                                    }
//                                }
//                            }
//                        }
//                        String documentation = "";
//                        int completionStartPos = offset - prefix.length();
//                        //String pattern = null; // ToDo: to use with javax.text.MessageFormat
//                        for (int i = 0, size = members == null ? 0 : members.length; i < size; i++) {
//                            String replacementText = "";
//                            String displayValue = "";
//                            int cursorOffset = 0;
//                            Object obj = members[i];
//                            int type = 0;
//                            if (obj instanceof String) {
//                                // import completion
//                                String s = (String)obj;
//                                replacementText = s;
//                                displayValue = s;   
//                                cursorOffset = 0;
//                                type = JavaFXCompletionItem.CLASS;
//                            } else if (obj instanceof Type) {
//                                Type t = (Type)obj;
//                                if (t.isOperation()) {
//                                    Type op = t;
//                                    if (!op.getName().startsWith(prefix)) {
//                                        continue;
//                                    }
//                                    if (!op.isPublic()) {
//                                        continue;
//                                    }
//                                    Iterator iter = op.getDeclaredAttributes();
//                                    int pcount = 0;
//                                    while (iter.hasNext()) {
//                                        Attribute a = (Attribute)iter.next();
//                                        if (a.getName().equals("this") || a.getName().equals("return")) {
//                                            
//                                        } else {
//                                            pcount++;
//                                        }
//                                    }
//                                    replacementText = op.getName()+"()";
//                                    displayValue = formatOp(op);
//                                    cursorOffset = pcount > 0  ? -1 : 0;
//                                    documentation = (op.getDocumentation() != null) ? op.getDocumentation() : "TODO JavaDoc Operation";
//                                    type = JavaFXCompletionItem.METHOD;
//                                } else {
//                                    Type c = t;
//                                    if (!c.isPublic() || c.isAbstract()) {
//                                        continue;
//                                    }
//                                    String name = formatType(c);
//                                    if (!name.startsWith(prefix)) {
//                                        continue;
//                                    }
//                                    String replacement = name;
//                                    cursorOffset = 0;
//                                    if (!nextToken.image.equals("{")) {
//                                        String indent = getIndentAtOffset(sourceCode, offset);
//                                        replacement = name + " {\n"+indent+"\t\n"+indent+"}";
//                                        cursorOffset = -(indent.length() + 2);
//                                    }
//                                    type = JavaFXCompletionItem.CLASS;
//                                    replacementText = replacement;
//                                    displayValue = name;
//                                    documentation = (t.getDocumentation() != null) ? t.getDocumentation() : "TODO JavaDoc Type";
//                                }
//                                
//                            } else if (obj instanceof Attribute) {
//                                Attribute a = (Attribute)obj;
//                                if (!a.isPublic()) {
//                                    continue;
//                                }
//                                if (!a.getName().startsWith(prefix)) {
//                                    continue;
//                                }
//                                String name = a.getName();
//                                String replacement = name;
//                                cursorOffset = 0;
//                                if (processor.isObjLiteral() &&
//                                        !nextToken.image.equals(":")) {
//                                    replacement = a.getName()+": ";
//                                    if (a.isOneToMany() || a.isManyToMany()) {
//                                        String indent = getIndentAtOffset(sourceCode, offset);
//                                        replacement = replacement+"\n"+indent+"[]";
//                                        cursorOffset = -1;
//                                    }
//                                }
//                                replacementText = replacement;
//                                displayValue = name;
//                                type = JavaFXCompletionItem.FIELD;
//                                documentation = (a.getDocumentation() != null) ? a.getDocumentation() : "TODO JavaDoc Attribute";
//                            } else if (obj instanceof Method) {
//                                Method method = (Method)obj;
//                                String name = method.getName();
//                                if (!name.startsWith(prefix)) {
//                                    continue;
//                                }
//                                String displayName = formatMethod(method);
//                                replacementText = name+"()";
//                                displayValue = displayName;
//                                cursorOffset =  method.getParameterTypes().length == 0 ? 0 : -1;
//                                type = JavaFXCompletionItem.METHOD;
//                                // ToDo: have to find a way to access the Java method javadocs....
//                                documentation = "TODO JavaDoc Method";
//                            } else if (obj instanceof VariableDeclarator) {
//                                VariableDeclarator decl = (VariableDeclarator)obj;
//                                System.out.println("decl="+decl);
//                                replacementText = decl.getVarName();
//                                if (!replacementText.startsWith(prefix)) {
//                                    continue;
//                                }
//                                displayValue = formatVar(decl);
//                                type = JavaFXCompletionItem.FIELD;
//                            } else if (obj instanceof Map.Entry) {
//                                Map.Entry m = (Map.Entry)obj;
//                                String key = (String)m.getKey();
//                                Object value = m.getValue();
//                                String displayString = key;
//                                if (!key.startsWith(prefix)) {
//                                    continue;
//                                }
//                                replacementText = key;
//                                displayValue = displayString;
//                                type = JavaFXCompletionItem.FIELD;
//                            }
//                            // ToDo: have to find a pattern to format the message.
//                            DocItem docItem = new DocItem(documentation);//MessageFormat.format(pattern, new Object[] {documentation}));
//                            JavaFXCompletionItem item = new JavaFXCompletionItem(replacementText, displayValue, type, completionStartPos, cursorOffset, docItem);
//                            resultList.add(item);
//                        }
//                        final JavaFXCompletionItem[] items = new JavaFXCompletionItem[resultList.size()];
//                        resultList.toArray(items);
//                        
//                        for (int i = 0; i < items.length; i++) {
//                            JavaFXCompletionItem f3CompItem = (JavaFXCompletionItem) items[i];
//                            resultSet.addItem(f3CompItem);
//                        }
//                        resultSet.finish();
//                    }
//                });
//                while (!resultSet.isFinished()) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        }, component);
//    }
    
    boolean isAutoCompletionChar(char ch) {
        return ch == '.';
    }
    
    /**
     * Called by the code completion infrastructure to check whether a text just typed
     * into a text component triggers an automatic query invocation.
     * <br>
     * If the particular query type is returned the infrastructure
     * will then call {@link #createTask(int, JTextComponent)}.
     *
     * @param component a component in which typing appeared
     * @param typedText a typed text
     *
     * @return a combination of the {@link #COMPLETION_QUERY_TYPE},
     *         {@link #DOCUMENTATION_QUERY_TYPE}, and {@link #TOOLTIP_QUERY_TYPE}
     *         values, or zero if no query should be automatically invoked.
     */
//    public int getAutoQueryTypes(JTextComponent component, String typedText) {
//        try {
//            String ch = component.getDocument().getText(component.getCaretPosition()-1, 1);
//            if (isAutoCompletionChar(ch.charAt(0))) {
//                return COMPLETION_QUERY_TYPE;
//            }
//        } catch (BadLocationException e) {
//            return 0;
//        }
//        return 0;
//    }
    
    
    private static class DocItem implements CompletionDocumentation {
        
        private String text;
        
        public DocItem(String text) {
            this.text = text;
        }
        
        public CompletionDocumentation resolveLink(String link) {
            return null;
        }
        
        public String getText() {
            return text;
        }
        
        public URL getURL() {
            return null;
        }
        
        public Action getGotoSourceAction() {
            return null;
        }
        
    }
    
    
}
