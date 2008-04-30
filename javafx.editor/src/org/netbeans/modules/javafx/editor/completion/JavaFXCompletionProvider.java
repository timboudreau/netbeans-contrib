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

import com.sun.source.tree.*;
import com.sun.source.util.*;


import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.*;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.completion.*;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;


/**
 *
 * @author Dusan Balek
 */
public class JavaFXCompletionProvider implements CompletionProvider {
    
    private static final Logger logger = Logger.getLogger(JavaFXCompletionProvider.class.getName());
    private static final boolean LOGGABLE = logger.isLoggable(Level.FINE);
    
    private static final boolean autoMode = Boolean.getBoolean("org.netbeans.modules.editor.java.completionAutoMode");
    private static final String ERROR = "<error>"; //NOI18N
    
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        // this is temporary for breh:
        if (Boolean.getBoolean("org.netbeans.modules.javafx.editor.unstable.disable")) {
            return 0;
        }
        if (".".equals(typedText) || (autoMode && JavaFXCompletionQuery.isJavaIdentifierPart(typedText))) {
            if (isJavaFXContext(component, component.getSelectionStart() - 1))
                return COMPLETION_QUERY_TYPE;
        }
        return 0;
    }
    
    public static boolean startsWith(String theString, String prefix) {
        if (theString == null || theString.length() == 0 || ERROR.equals(theString))
            return false;
        if (prefix == null || prefix.length() == 0)
            return true;
        return theString.startsWith(prefix);
    }
    
    public static TreePath getPathElementOfKind(Tree.Kind kind, TreePath path) {
        return getPathElementOfKind(EnumSet.of(kind), path);
    }
    
    public static TreePath getPathElementOfKind(EnumSet<Tree.Kind> kinds, TreePath path) {
        while (path != null) {
            if (kinds.contains(path.getLeaf().getKind()))
                return path;
            path = path.getParentPath();
        }
        return null;        
    }        

    public static boolean isJavaFXContext(final JTextComponent component, final int offset) {
        Document doc = component.getDocument();
        if (doc instanceof AbstractDocument) {
            ((AbstractDocument)doc).readLock();
        }
        try {
            TokenSequence<JFXTokenId> ts = getJavaFXTokenSequence(TokenHierarchy.get(doc), offset);
            if (ts == null) {
                return false;
            }
            if (!ts.moveNext() && !ts.movePrevious()) {
                return true;
            }
            if (offset == ts.offset()) {
                return true;
            }
            switch (ts.token().id()) {
                case FLOATING_POINT_LITERAL:
                    if (ts.token().text().charAt(0) == '.') {
                        break;
                    }
                case DOC_COMMENT:
                case STRING_LITERAL:
                case LINE_COMMENT:
                case COMMENT:
                    return false;
            }
            return true;
        } finally {
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument) doc).readUnlock();
            }
        }
    }
    
    public static TokenSequence<JFXTokenId> getJavaFXTokenSequence(final TokenHierarchy hierarchy, final int offset) {
        if (hierarchy != null) {
            TokenSequence<?> ts = hierarchy.tokenSequence();
            while(ts != null && (offset == 0 || ts.moveNext())) {
                ts.move(offset);
                if (ts.language() == JFXTokenId.language())
                    return (TokenSequence<JFXTokenId>)ts;
                if (!ts.moveNext() && !ts.movePrevious()) {
                    log("getJavaFXTokenSequence returning null (1) for offset " + offset);
                    return null;
                }
                ts = ts.embedded();
            }
        }
        log("getJavaFXTokenSequence returning null (2) for offset " + offset);
        return null;
    }
    
    public CompletionTask createTask(int type, JTextComponent component) {
        // this is temporary for breh:
        if (Boolean.getBoolean("org.netbeans.modules.javafx.editor.unstable.disable")) {
            return null;
        }
        if ((type & COMPLETION_QUERY_TYPE) != 0 || type == TOOLTIP_QUERY_TYPE || type == DOCUMENTATION_QUERY_TYPE)
            return new AsyncCompletionTask(new JavaFXCompletionQuery(type, component.getSelectionStart(), true), component);
        return null;
    }
    
    public static List<? extends CompletionItem> query(JavaFXSource source, int queryType, int offset, int substitutionOffset) throws IOException {
        assert source != null;
        assert (queryType & COMPLETION_QUERY_TYPE) != 0;
        JavaFXCompletionQuery query = new JavaFXCompletionQuery(queryType, offset, false);
        source.runUserActionTask(query, false);
        if (offset != substitutionOffset) {
            for (JavaFXCompletionItem jci : query.results) {
                jci.substitutionOffset += (substitutionOffset - offset);
            }
        }
        return query.results;
    }
    
    private static void log(String s) {
        if (LOGGABLE) {
            logger.fine(s);
        }
    }
    
}
