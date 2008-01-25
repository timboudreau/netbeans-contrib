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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netbeans.api.gsf.ColoringAttributes;
import org.netbeans.api.gsf.CompilationInfo;
import org.netbeans.api.gsf.OffsetRange;
import org.netbeans.api.gsf.SemanticAnalyzer;
import net.java.javafx.typeImpl.Compilation;
import java.io.StringReader;
import net.java.javafx.type.expr.CompilationUnit;
import java.util.Iterator;
import net.java.javafx.type.Type;
import net.java.javafx.type.Attribute;
import net.java.javafx.typeImpl.completion.CompletionParser;
import net.java.javafx.typeImpl.completion.SimpleCharStream;
import net.java.javafx.typeImpl.completion.Token;
import java.util.TreeSet;
import net.java.javafx.type.expr.ValidationError;
import net.java.javafx.type.expr.Locatable;
import org.netbeans.modules.javafx.editor.JavaFXDocument;
import org.netbeans.modules.javafx.model.impl.JavaFXModel;

/**
 * Walk through the JRuby AST and note interesting things
 * @todo Use the org.jruby.ast.visitor.NodeVisitor interface
 * @todo Do mixins and includes trip up my unused private method detection code?
 * @todo Treat toplevel methods as private?
 * @todo Show unused highlighting for unused class variables:
 *    private_class_method
 *   See section 7.8 in http://www.rubycentral.com/faq/rubyfaq-7.html
 * @author Tor Norbye
 */


public class SemanticAnalysis implements SemanticAnalyzer {

    private boolean cancelled;
    private Map<OffsetRange, ColoringAttributes> semanticHighlights;

    public SemanticAnalysis() {
    }
            
    public Map<OffsetRange, ColoringAttributes> getHighlights() {
        return semanticHighlights;
    }

    protected final synchronized boolean isCancelled() {
        return cancelled;
    }

    protected final synchronized void resume() {
        cancelled = false;
    }

    public final synchronized void cancel() {
        cancelled = true;
    }

    static public class LineMap {
        private List<Integer> lineMap = new ArrayList<Integer>();
        LineMap(String string) {
            lineMap.add(new Integer(1));
            for (int i = 0; i < string.length(); i++) {
                if (string.charAt(i) == '\n') {
                    lineMap.add(new Integer(i+1));
                }
            }
        }
        public int getOffset(Locatable location) {
            int beginline = location.getBeginLine();
            if ((beginline > 0)&&(beginline <= lineMap.size()))
                return (lineMap.get(location.getBeginLine() - 1)) + location.getBeginColumn() - 1;
            else
                return -1;
        }

        public int getLength(Locatable location) {
            return location.getEndColumn() - location.getBeginColumn() + 1;
        }
        
        public OffsetRange getOffsetRange(Locatable location) {
            int offset = getOffset(location);
            int length = getLength(location);
            if (offset != -1)
                return new OffsetRange(offset, offset + length);
            else
                return null;
        }
        
        public boolean isInLocation(Locatable location, int offset) {
            int errOffset = getOffset(location);
            int errLength = getLength(location);
            if ((offset >= errOffset)&&(offset - errOffset <= errLength))
                return true;
            else
                return false;
        }
    }
    
    private boolean checkRangeForError(Compilation compilation, LineMap lineMap, Locatable location) {
        ValidationError error = compilation.getLastError();
        int offset = lineMap.getOffset(location);
        while (error != null) {
            Locatable errorLocation = error.getLocation();
            if (lineMap.isInLocation(errorLocation, offset))
                return true;
            error = error.getNextError();
        }
        return false;
    }

    /*private Map getCompilationUnits(Compilation compilation) {
        Map result = null;
        try {
            Field field = compilation.getClass().getDeclaredField("mCompilationUnits");
            result = (Map)field.get(compilation);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }*/
    
    private Map <String, TreeSet<String>> classesByAttributeMap = new HashMap <String, TreeSet<String>>();
    private TreeSet<String> getClassByAttribute(Compilation compilation, String attribute)
    {
        TreeSet<String> result = classesByAttributeMap.get(attribute);
        if (result != null)
            return result;
        Class compilationClass = Compilation.class;
        Map unitsMap = compilation.getCompilationUnits();
        Iterator unitsIterator = unitsMap.values().iterator();
        while (unitsIterator.hasNext()) {
            CompilationUnit unit = (CompilationUnit)unitsIterator.next();
            result = new TreeSet<String>();
            Iterator classesListIterator = unit.getClasses().iterator();
            while (classesListIterator.hasNext()) {
                Type type = (Type)classesListIterator.next();
                Iterator attributesIterator = type.getAttributes();
                while (attributesIterator.hasNext()) {
                    Attribute _attribute = (Attribute)attributesIterator.next();
                    if (_attribute.getName().compareTo(attribute) == 0)
                        result.add(type.getName());
                }
            }
        }
        classesByAttributeMap.put(attribute, result);
        return result;
    }
    
    private boolean isClassForAttribute(Compilation compilation, String attribute, String type) {
        TreeSet<String> list = getClassByAttribute(compilation, attribute);
        return list.contains(type);
    }
    
    class LocatableImpl implements Locatable {
        
        private int beginColumn;
        private int endColumn;
        private int beginLine;
        private int endLine;
        private String uRI;
        
        public LocatableImpl(Token token) {
            beginColumn = token.beginColumn;
            endColumn =  token.endColumn;
            beginLine =  token.beginLine;
            endLine =  token.endLine;
        }
        public int getBeginLine() {
            return beginLine;
        }
        public void setBeginLine(int line) {
            beginLine = line;
        }
        public int getEndLine() {
            return beginLine;
        }
        public void setEndLine(int line) {
            endLine = line;
        }
        public int getBeginColumn() {
            return beginColumn;
        }
        public void setBeginColumn(int column) {
            beginColumn = column;
        }
        public int getEndColumn() {
            return endColumn;
        }
        public void setEndColumn(int column) {
            endColumn = column;
        }
        public String getURI() {
            return uRI;
        }
        public void setURI(String uri) {
            uRI = uri;
        }
    }

    private void highlightWithErrorCheck(Compilation compilation, Token token, LineMap lineMap, ColoringAttributes coloring, Map<OffsetRange, ColoringAttributes> highlights) {
        LocatableImpl location = new LocatableImpl(token);
        if (!checkRangeForError(compilation, lineMap, location))
            highlights.put(lineMap.getOffsetRange(location), coloring);
    }
    
    /*private boolean confirmRange(Map<OffsetRange, Set<String>> parametersMap, OffsetRange checkedRange, String variable) {
        for (OffsetRange range: parametersMap.keySet()) {
            if (range.containsInclusive(checkedRange.getStart())) {
                for (String parameter: parametersMap.get(range))
                    if (parameter.contentEquals(variable))
                        return true;
            }
        }
        return false;
    }*/

    public void run(CompilationInfo info) {
        resume();
        if (isCancelled()) {
            return;
        }
        try {
            if (!((JavaFXDocument)info.getDocument()).errorAndSyntaxAllowed()) {
                this.semanticHighlights = null;
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<OffsetRange, ColoringAttributes> highlights =
            new HashMap<OffsetRange, ColoringAttributes>(100);

        String text = null;
        try {
            text = info.getDocument().getText(0, info.getDocument().getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Compilation compilation = JavaFXModel.getCompilation(info.getFileObject());
        
        LineMap lineMap = new LineMap(text);
        TreeSet<String> attributesList = new TreeSet<String>();
        TreeSet<String> localAttributesList = new TreeSet<String>();
        Map<String, Integer> classesList = new HashMap<String, Integer>();
        //Map<OffsetRange, Set<String>> parametersMap = new HashMap<OffsetRange, Set<String>>();
        
        Map unitsMap = compilation.getCompilationUnits();
        Iterator unitsIterator = unitsMap.values().iterator();
        
        CompilationUnit compilationUnit = compilation.getCompilationUnit(info.getFileObject().getPath());
        
        if (compilationUnit == null) {
            this.semanticHighlights = null;
            return;
        }

        /*Iterator functionsListIterator = compilationUnit.getFunctions().iterator();
        while (functionsListIterator.hasNext()) {
            Object obj = functionsListIterator.next();
            if (obj instanceof FunctionDefinition) {
                FunctionDefinition function = (FunctionDefinition)obj;
                ParameterList parameters = function.getParameters();
                Set<String> parametersSet = new TreeSet<String>();
                for (int i = 0; i < parameters.getSize(); i++) {
                    Parameter parameter = parameters.getParameter(i);
                    highlights.put(lineMap.getOffsetRange(parameter), ColoringAttributes.PARAMETER_DECLARATION);
                    parametersSet.add(parameter.getName());
                }
                if (function.getBody() instanceof StatementList) {
                    StatementList statements = (StatementList)function.getBody();
                    if (statements.getSize() > 0) {
                        Statement firstStatement = statements.getStatement(0);
                        Statement lastStatement = statements.getStatement(statements.getSize() - 1);
                        OffsetRange searchRange = new OffsetRange(lineMap.getOffset(firstStatement), lineMap.getOffset(lastStatement) + lineMap.getLength(lastStatement));
                        parametersMap.put(searchRange, parametersSet);
                    }
                }
            }
        }*/
      
        while (unitsIterator.hasNext())
        {
            CompilationUnit unit = (CompilationUnit)unitsIterator.next();
                    
            Iterator classesListIterator = unit.getClasses().iterator();
            while (classesListIterator.hasNext()) {
                Type type = (Type)classesListIterator.next();

                int offset = lineMap.getOffset(type);
                String name = getPureName(unit, type.getName());
                classesList.put(name, offset);
                
                Iterator attributesIterator = type.getAttributes();
                while (attributesIterator.hasNext()) {
                    Attribute attribute = (Attribute)attributesIterator.next();
                    String attrName = attribute.getName();
                    int attrOffset = lineMap.getOffset(attribute);
                    attributesList.add(attrName);
                }
            }
        }
        
        Iterator classesListIterator = compilationUnit.getClasses().iterator();
        while (classesListIterator.hasNext()) {
            Type type = (Type)classesListIterator.next();
            int offset = lineMap.getOffset(type);
            if (offset != -1) {
                highlights.put(lineMap.getOffsetRange(type), ColoringAttributes.CLASS_DECLARATION);
            }

            Iterator attributesIterator = type.getAttributes();
            while (attributesIterator.hasNext()) {
                Attribute attribute = (Attribute)attributesIterator.next();
                localAttributesList.add(attribute.getName());
                offset = lineMap.getOffset(attribute);
                if (attribute.getScope().getName().compareTo(type.getName()) == 0)
                    if (offset != -1) {
                        highlights.put(lineMap.getOffsetRange(attribute), ColoringAttributes.ATTRIBUTE_DECLARATION);
                }
            }
        }
            
        SimpleCharStream charStream = new SimpleCharStream(new StringReader(text));
        CompletionParser completionParser = new CompletionParser(charStream);
        Token token = null;
        try {
            token = completionParser.getNextToken();
        } catch (Error e) {
        }
        Token predToken = null;
        Token predPredToken = null;
        Token predPredPredToken = null;
        boolean breakLoop = false;
        boolean importIdentifier = false;
        
        while ((token.kind != 0) && (!breakLoop)) {
            if (importIdentifier)
                if (token.image.contentEquals(";"))
                    importIdentifier = false;
            switch (token.kind) {
                case CompletionParser.IMPORT: 
                    importIdentifier = true;
                    break;
                case CompletionParser.IDENTIFIER:
                {
                    LocatableImpl location = new LocatableImpl(token);
                    /*if (confirmRange(parametersMap, lineMap.getOffsetRange(location), token.image)) {                        
                        highlightWithErrorCheck(compilation, token, lineMap, ColoringAttributes.PARAMETER_USE, highlights);
                    }*/
                    if (predToken != null) {
                        if (predToken.kind == CompletionParser.function) {
                            if (!classesList.containsKey(token.image))
                                highlightWithErrorCheck(compilation, token, lineMap, ColoringAttributes.FUNCTION_DECLARATION, highlights);
                        }
                        if (predToken.kind == CompletionParser.operation) {
                            if (!classesList.containsKey(token.image))
                                highlightWithErrorCheck(compilation, token, lineMap, ColoringAttributes.OPERATION_DECLARATION, highlights);
                        }
                        if (predPredToken != null)
                            if (predToken.image.compareTo(".") == 0) {
                                if (predPredToken.kind == CompletionParser.IDENTIFIER) {
                                    if (predPredPredToken != null) {
                                        if (predPredPredToken.kind == CompletionParser.function) {
                                            highlightWithErrorCheck(compilation, token, lineMap, ColoringAttributes.FUNCTION_DECLARATION, highlights);
                                        }
                                        if (predPredPredToken.kind == CompletionParser.operation) {
                                            highlightWithErrorCheck(compilation, token, lineMap, ColoringAttributes.OPERATION_DECLARATION, highlights);
                                        }
                                    }
                                }
                            }
                    }
                    
                    if (!importIdentifier) {
                        Integer classOffset = classesList.get(token.image);
                        if (classOffset != null) {
                            int tokenOffset = lineMap.getOffset(location);
                            if (classOffset != tokenOffset) {
                                highlightWithErrorCheck(compilation, token, lineMap, ColoringAttributes.CLASS_USE, highlights);
                            }
                        }
                    }            
                        
                    if (attributesList.contains(token.image)) {
                        boolean skip = false;
                        boolean local = localAttributesList.contains(token.image);
                        if (predToken != null) {
                            switch (predToken.kind) {
                                case CompletionParser.var:
                                case CompletionParser.type:
                                    skip = true;
                                    break;
                            }
                            if (predPredToken != null)
                                if (predToken.image.compareTo(".") == 0) {
                                    if (predPredToken.kind == CompletionParser.IDENTIFIER) {
                                        String packagename = null;
                                        if (compilationUnit != null)
                                            packagename = compilationUnit.getPackageName();
                                        if (packagename == null)
                                            packagename = "";
                                        else
                                            packagename+= ".";
                                        if (!isClassForAttribute(compilation, token.image, packagename + predPredToken.image)) {
                                            skip = true;
                                        }
                                    }
                                    else {
                                        if ((predPredToken.kind != CompletionParser.THIS) || (!local))
                                            skip = true;
                                    }
                                }
                        }
                        if (!skip) {
                            highlightWithErrorCheck(compilation, token, lineMap, ColoringAttributes.ATTRIBUTE_USE, highlights);
                        }
                    }
                    break;
                }
            }
            predPredPredToken = predPredToken;
            predPredToken = predToken;
            predToken = token;
            try {
                token = completionParser.getNextToken();
            } catch (Error e) {
            }
        }

        if (isCancelled()) {
            return;
        }

        if (highlights.size() > 0) {
            this.semanticHighlights = highlights;
        } else {
            this.semanticHighlights = null;
        }
    }
    
    private String getPureName(CompilationUnit unit, String name) {
        String packagename = unit.getPackageName();
        int classNameOffset = 0;
        if (packagename != null) {
            classNameOffset = packagename.length() + 1;
        }
        return name.substring(classNameOffset);
    }
}
