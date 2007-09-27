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
 * Software is Leon Chiver. All Rights Reserved.
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

package org.netbeans.modules.editor.java.doclet;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.ExtSyntaxSupport;
import org.netbeans.editor.ext.java.JavaTokenContext;
import org.netbeans.jmi.javamodel.CallableFeature;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.Feature;
import org.netbeans.jmi.javamodel.Field;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.modules.editor.java.NbJavaJMISyntaxSupport;
import org.netbeans.modules.editor.java.doclet.ast.Attribute;
import org.netbeans.modules.editor.java.doclet.ast.Javadoc;
import org.netbeans.modules.editor.java.doclet.ast.Tag;
import org.netbeans.modules.editor.java.doclet.parser.JavadocParser;
import org.netbeans.modules.editor.java.doclet.parser.ParseException;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.PositionBounds;

/**
 * @author leon chiver
 */
public class DocletCompletionProvider implements CompletionProvider {
    
    private final static String DOCLET_DESCRIPTORS = "/Editors/text/x-java/CompletionProviders/DocletDescriptors";
    
    private List docletDescriptors = new ArrayList();
    
    private static JavadocParser parser;
    
    public DocletCompletionProvider() {
        loadDocletDescriptors();
    }
    
    public CompletionTask createTask(int queryType, JTextComponent component) {
        return new AsyncCompletionTask(new DocletQuery(), component);
    }
    
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        BaseDocument bd = Utilities.getDocument(component);
        int offset = component.getCaret().getDot();
        try {
            if (bd == null) {
                return 0;
            }
            int w = Utilities.getFirstWhiteBwd(bd, offset);
            if (w == -1) {
                return 0;
            }
            String word = bd.getText(w + 1, offset - w - 1);
            if (!word.startsWith("@")) {
                return 0;
            }
            if (existsDescriptorForTag(word)) {
                return COMPLETION_QUERY_TYPE;
            }
        } catch (BadLocationException ex) {
            // Ignore it
        }
        return 0;
    }

    private boolean existsDescriptorForTag(String tag) {
        int sz = docletDescriptors.size();
        for (int i = 0; i < sz; i++) {
            DocletDescriptor dd = (DocletDescriptor) docletDescriptors.get(i);
            String acceptedPrefix = dd.getCompletionPrefix();
            if (acceptedPrefix == null || acceptedPrefix.length() == 0 ||
                    tag.startsWith(acceptedPrefix) || ("@" + tag).startsWith(acceptedPrefix)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isInsideJavadoc(BaseDocument doc, int offset) {
        boolean inside = false;
        try {
            TokenID token = ((ExtSyntaxSupport) doc.getSyntaxSupport()).getTokenID(offset);
            int id = token != null ? token.getNumericID() : 0;
            inside = id == JavaTokenContext.BLOCK_COMMENT_ID;
        } catch (BadLocationException ex) {
            // Ignore it
        }
        return inside;
    }
    
    private void loadDocletDescriptors() {
        FileObject folder = Repository.getDefault().getDefaultFileSystem().
                findResource(DOCLET_DESCRIPTORS);
        if (folder != null) {
            FileObject[] children = folder.getChildren();
            for (int i = 0; i < children.length; i++) {
                FileObject f = children[i];
                try {
                    DataObject obj = DataObject.find(f);
                    InstanceCookie ic = (InstanceCookie) obj.getCookie(InstanceCookie.class);
                    if (ic != null){
                        try {
                            if (DocletDescriptor.class.isAssignableFrom(ic.instanceClass())) {
                                docletDescriptors.add(ic.instanceCreate());
                            }
                        } catch (IOException ioe){
                            ioe.printStackTrace();
                        } catch (ClassNotFoundException cnfe){
                            cnfe.printStackTrace();
                        }
                    }
                } catch (DataObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private List getCompletionItems(BaseDocument doc, int caretOffset) {
        //
        if (docletDescriptors.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        if (!isInsideJavadoc(doc, caretOffset)) {
            return Collections.EMPTY_LIST;
        }
        int bounds[] = getJavadocBounds(doc, caretOffset);
        if (bounds == null) {
            return Collections.EMPTY_LIST;
        }
        //
        try {
            String javadoc = String.valueOf(doc.getChars(bounds[0], bounds[1] - bounds[0]));
            if (javadoc == null || javadoc.length() == 0) {
                return Collections.EMPTY_LIST;
            }
            if (parser == null) {
                parser = new JavadocParser(new StringReader(javadoc));
            } else {
                parser.ReInit(new StringReader(javadoc));
            }
            try {
                Javadoc jd = parser.doc();
                List result = new ArrayList();
                addCompletionItems(doc, caretOffset, jd, bounds, result);
                return result;
            } catch (ParseException ex) {
                // Ignore it
            }
        } catch (BadLocationException ex) {
            // Ignore it
        }
        return Collections.EMPTY_LIST;
    }
    
    private static String removeLeadingWhitespace(String str) {
        while (str.length() > 0) {
            char c0 = str.charAt(0);
            if (c0 == ' ' || c0 == '\t') {
                str = str.substring(1);
            } else {
                break;
            }
        }
        return str;
    }
    
    private static boolean isValidTagStartPosition(BaseDocument doc, int offset, boolean firstLine) throws BadLocationException {
        int startOffset = Utilities.getRowStart(doc, offset);
        String str = String.valueOf(doc.getChars(startOffset, offset - startOffset));
        // Get rid of all leading whitespaces
        str = removeLeadingWhitespace(str);
        // If it's the first line we remove /**
        if (firstLine && str.length() > 2) {
            str = str.substring(3);
        } else if (str.length() > 0 && str.charAt(0) == '*') {
            str = str.substring(1);
        }
        // Once again remove all leading spaces
        str = removeLeadingWhitespace(str);
        if (str.length() == 0) {
            return true;
        }
        if (str.charAt(0) != '@') {
            return false;
        }
        str = str.substring(1);
        while (str.length() > 0) {
            char c = str.charAt(0);
            if (c == ' ' || c == '\t') {
                return false;
            }
            str = str.substring(1);
        }
        return true;
    }
    
    private static int[] getJavadocBounds(BaseDocument doc, int caretOffset) {
        int start = caretOffset;
        int end = caretOffset;
        ExtSyntaxSupport syntSupport = (ExtSyntaxSupport) doc.getSyntaxSupport();
        int[] bounds = new int[2];
        try {
            while (start > 0) {
                TokenID token = syntSupport.getTokenID(start);
                if (token == null || token.getNumericID() != JavaTokenContext.BLOCK_COMMENT_ID) {
                    start++;
                    break;
                }
                start--;
            }
            int max = doc.getLength() - 1;
            while (end < max) {
                TokenID token = syntSupport.getTokenID(end);
                if (token == null || token.getNumericID() != JavaTokenContext.BLOCK_COMMENT_ID) {
                    break;
                }
                end++;
            }
            bounds[0] = start;
            bounds[1] = end;
        } catch (BadLocationException ex) {
            // Ignore it
        }
        return bounds;
    }
    
    private void addCompletionItems(
            BaseDocument doc, int caretOffset, 
            Javadoc jd, int bounds[], List result) throws BadLocationException {
        int docStartLine = Utilities.getLineOffset(doc, bounds[0]);
        int currentLine = Utilities.getLineOffset(doc, caretOffset);
        // The line index inside the javadoc
        int relativeLine = currentLine - docStartLine;
        int column = Utilities.getVisualColumn(doc, caretOffset);
        boolean validTagStartPos = isValidTagStartPosition(doc, caretOffset, relativeLine == 0);
        // Get the current tag
        Tag currentTag = jd.getTagAtLine(relativeLine);
        NbJavaJMISyntaxSupport syntaxSupport = (NbJavaJMISyntaxSupport) doc.getSyntaxSupport();
        //
        Feature f = getFeatureAfterPosition(syntaxSupport, doc, bounds[1]); 
        CallableFeature callableFeat = (f instanceof CallableFeature) ? (CallableFeature) f : null;
        ClassDefinition classDef = (f instanceof ClassDefinition) ? (ClassDefinition) f : null;
        Field field = (f instanceof Field) ? (Field) f : null;
        // It's a valid tag start position, so we should propose all available tags
        if (validTagStartPos) {
            addTags(jd, currentTag, classDef, callableFeat, field, doc, caretOffset, relativeLine, column, result);
        } 
        // Offer tag attributes only if we have a tag
        if (currentTag == null) {
            return;
        }
        String currentTagName = currentTag.getName();
        if (currentTagName == null || currentTagName.length() == 0) {
            return;
        }
        Attribute currentAttribute = null;;
        currentAttribute = currentTag.getAttributeAtPosition(relativeLine, column);
        boolean isValue = false;
        if (currentAttribute == null) {
            // Check if the caret is not inside the attribute's value
            currentAttribute = currentTag.getAttributeWithValueAtPosition(relativeLine, column);
            if (currentAttribute != null) {
                isValue = true;
            }
        } else {
            String name = currentAttribute.getName();
            if (name != null && column < currentAttribute.getEndColumn()) {
                name = name.substring(0, name.length() - (currentAttribute.getEndColumn() - column));
                currentAttribute.setName(name);
            }
        }
        addAttributesAndAttrValues(jd, currentTag, currentAttribute, isValue, classDef, callableFeat, field, doc, caretOffset, result);
    }

    private void addAttributesAndAttrValues(Javadoc jd, Tag currentTag, 
            Attribute currentAttribute, boolean isValue, ClassDefinition classDef, 
            CallableFeature callableFeat, Field field, BaseDocument doc, int caretOffset, List result) {
        int sz = docletDescriptors.size();
        for (int i = 0; i < sz; i++) {
            DocletDescriptor d = (DocletDescriptor) docletDescriptors.get(i);
            if (isValue) {
                if (field != null) {
                    d.addAttributeValues(doc, caretOffset, field, currentTag, currentAttribute, jd, result);
                } else if (classDef != null) {
                    d.addAttributeValues(doc, caretOffset, classDef, currentTag, currentAttribute, jd, result);
                } else if (callableFeat != null) {
                    d.addAttributeValues(doc, caretOffset, callableFeat, currentTag, currentAttribute, jd, result);
                }
            } else {
                if (field != null) {
                    d.addContents(doc, caretOffset, field, currentTag, currentAttribute, jd, result);
                } else if (classDef != null) {
                    d.addContents(doc, caretOffset, classDef, currentTag, currentAttribute, jd, result);
                } else if (callableFeat != null) {
                    d.addContents(doc, caretOffset, callableFeat, currentTag, currentAttribute, jd, result);
                }
            }
        }
    }

    private void addTags(Javadoc jd, Tag currentTag, ClassDefinition classDef, 
            CallableFeature callableFeat, Field field, BaseDocument doc, 
            int caretOffset, int relativeLine, int column, List result) {
        Tag currentLineTag = currentTag != null ?
            (currentTag.getBeginLine() == relativeLine ? currentTag : null) : null;
        String tagName = currentLineTag != null ? currentLineTag.getName() : null;
        if (tagName != null && column < currentLineTag.getNameEndColumn() && 
                column >= currentLineTag.getNameBeginColumn()) {
            tagName = tagName.substring(0, tagName.length() - (currentLineTag.getNameEndColumn() - column));
            currentLineTag.setName(tagName);
        }
        int sz = docletDescriptors.size();
        for (int i = 0; i < sz; i++) {
            DocletDescriptor d = (DocletDescriptor) docletDescriptors.get(i);
            // This is the tag on the current line
            String prefix = d.getCompletionPrefix();
            boolean validPrefix = false;
            if (prefix == null || prefix.length() == 0) {
                validPrefix = true;
            } else if (tagName != null && tagName.length() > 0 && 
                    (tagName.startsWith(prefix) || ("@" + tagName).startsWith(prefix))) {
                validPrefix = true;
            }
            if (!validPrefix) {
                continue;
            }
            if (classDef != null) {
                d.addTags(doc, caretOffset, classDef, currentLineTag, jd, result);
            } else if (callableFeat != null) {
                d.addTags(doc, caretOffset, callableFeat, currentLineTag, jd, result);
            } else if (field != null) {
                d.addTags(doc, caretOffset, field, currentLineTag, jd, result);
            }
        }
    }
    
    private int getFirstFeaturePosition(BaseDocument doc, NbJavaJMISyntaxSupport supp, int offset) 
            throws BadLocationException {
        int length = doc.getLength();
        int pos = offset;
        while (pos < length - 1) {
            int id = supp.getTokenID(pos).getNumericID();
            switch (id) {
                case JavaTokenContext.IDENTIFIER_ID:
                case JavaTokenContext.PUBLIC_ID:
                case JavaTokenContext.PRIVATE_ID:
                case JavaTokenContext.PROTECTED_ID:
                case JavaTokenContext.FINAL_ID:
                case JavaTokenContext.VOLATILE_ID:
                case JavaTokenContext.VOID_ID:
                case JavaTokenContext.ABSTRACT_ID:
                case JavaTokenContext.CLASS_ID:
                case JavaTokenContext.ENUM_ID:
                case JavaTokenContext.INTERFACE_ID:
                case JavaTokenContext.NATIVE_ID:
                    return pos;
            }
        }
        return -1;
    }
    
    private Feature getFeatureAfterPosition(NbJavaJMISyntaxSupport supp, BaseDocument doc, int offset) 
            throws BadLocationException {
        Resource res = supp.getResource();
        if (res == null) {
            return null;
        }
        int length = doc.getLength();
        int pos = offset;
        boolean found = false;
        while (!found && pos < length - 1) {
            int id = supp.getTokenID(pos).getNumericID();
            switch (id) {
                case JavaTokenContext.IDENTIFIER_ID:
                case JavaTokenContext.PUBLIC_ID:
                case JavaTokenContext.PRIVATE_ID:
                case JavaTokenContext.PROTECTED_ID:
                case JavaTokenContext.FINAL_ID:
                case JavaTokenContext.VOLATILE_ID:
                case JavaTokenContext.VOID_ID:
                case JavaTokenContext.ABSTRACT_ID:
                case JavaTokenContext.CLASS_ID:
                case JavaTokenContext.ENUM_ID:
                case JavaTokenContext.INTERFACE_ID:
                case JavaTokenContext.NATIVE_ID:
                    found = true;
                    break;
                default:
                    pos++;
            }
        }
        if (!found) {
            return null;
        }
        Collection els = res.getClassifiers();
        boolean cont = true;
        JavaClass jcls;
        Feature feat = null;
        Feature currentFeat;
        while (cont) {
            cont = false;
            Object[] features = els.toArray();
            for (int i = features.length - 1; i >= 0; i--) {
                currentFeat = (Feature) features[i];
                PositionBounds bounds = JavaMetamodel.getManager().getElementPosition(currentFeat);
                if (pos <= bounds.getBegin().getOffset()) {
                    feat = currentFeat;
                } else if (currentFeat instanceof JavaClass) {
                    jcls = (JavaClass) currentFeat;
                    els = jcls.getFeatures();
                    feat = null;
                    cont = true;
                    break;
                } else {
                    return feat;
                }
            }
        }
        return feat;
    }
    
    private class DocletQuery extends AsyncCompletionQuery {
        
        protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
            if (doc instanceof BaseDocument) {
                JavaMetamodel.getDefaultRepository().beginTrans(false);
                try {
                    resultSet.addAllItems(getCompletionItems((BaseDocument) doc, caretOffset));
                } finally {
                    JavaMetamodel.getDefaultRepository().endTrans();
                }
            }
            resultSet.finish();
        }
    }
}
