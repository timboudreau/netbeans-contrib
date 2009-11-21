/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.fuse.editor;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.php.fuse.lexer.FuseTokenId;
import org.netbeans.modules.php.fuse.lexer.FuseTopTokenId;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 *
 * @author Martin Fousek
 */
public class TmplCompletionProvider implements CompletionProvider {

    public CompletionTask createTask(int queryType, final JTextComponent component) {
        Document doc = Utilities.getDocument(component);

        // hack - added atributes for tmpl lexer which variables are template variables
        InputAttributes inputAttributes = new InputAttributes();
        TmplParseData tmplParseData = new TmplParseData(doc);
        inputAttributes.setValue(FuseTokenId.language(), TmplParseData.class, tmplParseData, false);
        doc.putProperty(InputAttributes.class, inputAttributes);

        int caretOffset = component.getCaret().getDot();
        if (isInFuseTemplates(doc, caretOffset)) {
            return new AsyncCompletionTask(new TmplCompletionQuery(component, queryType, TmplCompletionQuery.QueryType.INNER_QUERY_TASK), component);
        } else {
            return new AsyncCompletionTask(new TmplCompletionQuery(component, queryType, TmplCompletionQuery.QueryType.OUTER_QUERY_TASK), component);
        }
    }

    private boolean isInFuseTemplates(Document doc, int offset){
        TokenHierarchy tokenHierarchy = TokenHierarchy.get(doc);
        TokenSequence tokenSequence = tokenHierarchy.tokenSequence();
        
        tokenSequence.move(offset);
        if (tokenSequence.moveNext() || tokenSequence.movePrevious()) {
            Object tokenID = tokenSequence.token().id();
            if (tokenID == FuseTopTokenId.T_FUSE || tokenID == FuseTopTokenId.T_FUSE_CLOSE_DELIMITER)
                return true;
        }
        return false;
    }

    public int getAutoQueryTypes(JTextComponent component,String typedText) {
        return 0;
    }
    
}

