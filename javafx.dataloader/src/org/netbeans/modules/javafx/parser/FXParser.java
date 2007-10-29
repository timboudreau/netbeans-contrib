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
package org.netbeans.modules.javafx.parser;

import java.util.List;

import javax.swing.text.Document;
import net.java.javafx.typeImpl.Compilation;

import org.netbeans.api.gsf.CompilationInfo;
import org.netbeans.api.gsf.Element;
import org.netbeans.api.gsf.ElementHandle;
import org.netbeans.api.gsf.OccurrencesFinder;
import org.netbeans.api.gsf.OffsetRange;
import org.netbeans.api.gsf.ParseEvent;
import org.netbeans.api.gsf.ParseListener;
import org.netbeans.api.gsf.Parser;
import org.netbeans.api.gsf.ParserFile;
import org.netbeans.api.gsf.ParserResult;
import org.netbeans.api.gsf.PositionManager;
import org.netbeans.api.gsf.SemanticAnalyzer;
import org.netbeans.api.gsf.SourceFileReader;
import org.netbeans.modules.javafx.editor.JavaFXDocument;

import org.netbeans.modules.javafx.editor.JavaFXPier;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;

import org.openide.filesystems.FileObject;
import java.io.StringReader;
import net.java.javafx.type.expr.ValidationError;
import org.netbeans.spi.gsf.DefaultError;
import org.netbeans.api.gsf.Error;
import org.netbeans.api.gsf.Severity;
import org.netbeans.api.gsf.ParseListener;
import org.netbeans.spi.gsf.DefaultPosition;
import org.netbeans.modules.javafx.JavaFXElementHandleImpl;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.netbeans.api.gsf.ElementKind;
import org.netbeans.api.gsf.Modifier;
import org.openide.loaders.DataObjectNotFoundException;
import org.netbeans.modules.javafx.parser.SemanticAnalysis.LineMap;
import net.java.javafx.type.Type;
import net.java.javafx.type.Attribute;
import net.java.javafx.type.Accessible;
import net.java.javafx.type.expr.VariableDeclaration;
import net.java.javafx.type.expr.FunctionDefinition;
import net.java.javafx.type.expr.ChangeRule;
import net.java.javafx.type.expr.CompilationUnit;
import net.java.javafx.type.expr.StatementList;
import net.java.javafx.type.expr.Statement;
import net.java.javafx.type.expr.ExpressionList;
import net.java.javafx.type.expr.VariableExpression;
import net.java.javafx.type.expr.AllocationExpression;
import net.java.javafx.type.expr.ExpressionStatement;
import net.java.javafx.type.expr.Expression;

/**
 * @author ads
 *
 */
public class FXParser implements Parser {

    private JavaFXPositionManager positionManager = new JavaFXPositionManager();
     /* (non-Javadoc)
     * @see org.netbeans.api.gsf.Parser#createHandle(org.netbeans.api.gsf.CompilationInfo, org.netbeans.api.gsf.Element)
     */
    public <T extends Element> ElementHandle<T> createHandle(
            CompilationInfo info, T element )
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.netbeans.api.gsf.Parser#getMarkOccurrencesTask(int)
     */
    public OccurrencesFinder getMarkOccurrencesTask( int caretPosition ) {
        // TODO Auto-generated method stub
        return null;
    }

    private class JavaFXPositionManager implements PositionManager {

        public OffsetRange getOffsetRange(Element file, Element object) {
            return ((JavaFXElement)object).getOffsetRange();
        }

        public boolean isTranslatingSource() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getLexicalOffset(ParserResult arg0, int arg1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getAstOffset(ParserResult arg0, int arg1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    
    }
    /* (non-Javadoc)
     * @see org.netbeans.api.gsf.Parser#getPositionManager()
     */
    public PositionManager getPositionManager() {
        // TODO Auto-generated method stub
        return positionManager;
    }

    /* (non-Javadoc)
     * @see org.netbeans.api.gsf.Parser#getSemanticAnalysisTask()
     */
    public SemanticAnalyzer getSemanticAnalysisTask() {
        return new SemanticAnalysis();
    }

    private void processErrors(Compilation compilation, LineMap lineMap, FileObject fileObject, ParseListener listener) {
        ValidationError validationError = compilation.getLastError();
        while (validationError != null) {
            int offset = lineMap.getOffset(validationError.getLocation());
            int length = lineMap.getLength(validationError.getLocation());
            Error error = new DefaultError(null, validationError.getErrorMessage(), null, fileObject,
                new DefaultPosition(offset), new DefaultPosition(offset + length), Severity.ERROR);
            listener.error(error);
            validationError = validationError.getNextError();
        }
    }

    /* (non-Javadoc)
     * @see org.netbeans.api.gsf.Parser#parseFiles(java.util.List, org.netbeans.api.gsf.ParseListener, org.netbeans.api.gsf.SourceFileReader)
     */
    public void parseFiles( List<ParserFile> files, ParseListener listener,
            SourceFileReader reader )
    {
        for (ParserFile file : files) {
            ParseEvent beginEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, null);
            listener.started(beginEvent);

            FXParserResult result = null;
            Document doc = null;
            LineMap lineMap = null;
            Compilation compilation = null;
            CompilationUnit unit = null;

            try {
                DataObject dataObject = DataObject.find( file.getFileObject() );
                EditorCookie editorCookie = 
                    dataObject.getCookie(EditorCookie.class);
                doc = editorCookie.getDocument();

                if ((doc != null) && (doc instanceof JavaFXDocument) && ((JavaFXDocument)doc).executionAllowed()){
                    JavaFXPier.sourceChanged((JavaFXDocument)doc);
                }

                if (((JavaFXDocument)doc).errorAndSyntaxAllowed()) {
                    String text = null;
                    try {
                        text = doc.getText(0, doc.getLength());

                        lineMap = new LineMap(text);

                        compilation = JavaFXPier.getCompilation(file.getFileObject());
                        unit = JavaFXPier.readCompilationUnit(compilation, file.getFileObject().getPath(), new StringReader(text));

                        processErrors(compilation, lineMap, file.getFileObject(), listener);
                    } catch (Exception e) {
    //                    e.printStackTrace();
                    }
                }
            }catch (DataObjectNotFoundException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            }catch (Exception e){
                //in case nocument was closed while parser is working
            }

            try {
                CharSequence buffer = reader.read(file);
                int offset = reader.getCaretOffset(file);
                result = new FXParserResult(file);
                if (unit != null)
                    fillResults(unit, lineMap,  result);
            } catch (Exception ioe) {
                listener.exception(ioe);
            }

            ParseEvent doneEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, result);
            listener.finished(doneEvent);
        }
    }

    private void fillResults(CompilationUnit unit, LineMap lineMap, FXParserResult result) {
        Iterator functionsListIterator = unit.getFunctions().iterator();
        while (functionsListIterator.hasNext()) {
            Object obj = functionsListIterator.next();
            if (obj instanceof FunctionDefinition) {
                FunctionDefinition function = (FunctionDefinition)obj;
                if (function.getScope() == null) {
                    JavaFXElement element = new JavaFXElement(ElementKind.METHOD, function.getName(), lineMap.getOffsetRange(function), null);
                    result.addElement(element);
                }
            }
        }
        StatementList statementsList = unit.getStatements();
        for (int i = 0; i < statementsList.getSize(); i++) {
            Statement statement = statementsList.getStatement(i);
            if (statement instanceof VariableDeclaration) {
                VariableDeclaration variableDeclaration = (VariableDeclaration)statement;
                ExpressionList expressionList = variableDeclaration.getVariables();
                for (int j = 0; j < expressionList.getSize(); j++) {
                    VariableExpression expression = (VariableExpression)expressionList.getExpression(j);
                    JavaFXElement element = new JavaFXElement(ElementKind.VARIABLE, expression.getVarName(), lineMap.getOffsetRange(expression), null);
                    result.addElement(element);
                }
            }
            if (statement instanceof ExpressionStatement) {
                ExpressionStatement expressionStatement = (ExpressionStatement)statement;
                Expression expression = expressionStatement.getExpression();
                if (expression instanceof AllocationExpression) {
                    AllocationExpression allocationExpression = (AllocationExpression)expression;
                    JavaFXElement element = new JavaFXElement(ElementKind.CONSTRUCTOR, allocationExpression.getTypeName(), lineMap.getOffsetRange(allocationExpression), null);
                    result.addElement(element);
                }
            }
        }

/*        Iterator triggersListIterator = unit.getTriggers().iterator();
        while (triggersListIterator.hasNext()) {
            ChangeRule trigger = (ChangeRule)triggersListIterator.next();
            JavaFXElement element = new JavaFXElement(ElementKind.OTHER, trigger.toString(), lineMap.getOffsetRange(trigger), null);
            result.addElement(element);       }*/
        Iterator classesListIterator = unit.getClasses().iterator();
        while (classesListIterator.hasNext()) {
            Type type = (Type)classesListIterator.next();
            Set<Modifier> classModifiers = getModifiers(type);
            JavaFXElement element = new JavaFXElement(ElementKind.CLASS, type.getName(), lineMap.getOffsetRange(type), classModifiers);
            Iterator attributesIterator = type.getDeclaredAttributes();
            while (attributesIterator.hasNext()) {
                Attribute attribute = (Attribute)attributesIterator.next();
                Set<Modifier> modifiers = getModifiers(attribute);
                JavaFXElement attributeElement = new JavaFXElement(ElementKind.ATTRIBUTE, attribute.getName(), lineMap.getOffsetRange(attribute), modifiers);
                element.addNested(attributeElement);
            }
            Iterator operationsIterator = type.getDeclaredOperations();
            while (operationsIterator.hasNext()) {
                Type operation = (Type)operationsIterator.next();
                Set<Modifier> modifiers = getModifiers(operation);
                OffsetRange offsetRange = lineMap.getOffsetRange(operation);
                if (offsetRange == null)
                    offsetRange = lineMap.getOffsetRange(type);
                JavaFXElement attributeElement = new JavaFXElement(ElementKind.METHOD, operation.getName(), offsetRange, modifiers);
                element.addNested(attributeElement);
            }
            result.addElement(element);
        }
    }
    
    private Set<Modifier> getModifiers(Accessible type) {
        Set<Modifier> modifiers = new TreeSet<Modifier>();
        if (type.isPrivate())
            modifiers.add(Modifier.PRIVATE);
        if (type.isProtected())
            modifiers.add(Modifier.PROTECTED);
        if (type.isPublic())
            modifiers.add(Modifier.PUBLIC);
        return modifiers;
    }
    
  /* (non-Javadoc)
     * @see org.netbeans.api.gsf.Parser#resolveHandle(org.netbeans.api.gsf.CompilationInfo, org.netbeans.api.gsf.ElementHandle)
     */
    public <T extends Element> T resolveHandle( CompilationInfo info, ElementHandle<T>  handle  ) {
        // TODO Auto-generated method stub
        return (T) ((JavaFXElementHandleImpl) handle).getElement();
    }
}