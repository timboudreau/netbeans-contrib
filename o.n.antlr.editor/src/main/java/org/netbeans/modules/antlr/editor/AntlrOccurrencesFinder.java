/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.antlr.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.OccurrencesFinder;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;

/**
 *
 * @author marekfukala
 */
public class AntlrOccurrencesFinder extends OccurrencesFinder<NbAntlrParserResult> {

    private int caretDocumentPosition;
    private Map<OffsetRange, ColoringAttributes> occurrencesMap = new HashMap<OffsetRange, ColoringAttributes>();

    @Override
    public void setCaretPosition(int position) {
        caretDocumentPosition = position;
    }

    @Override
    public Map<OffsetRange, ColoringAttributes> getOccurrences() {
        return occurrencesMap;
    }

    @Override
    public void run(NbAntlrParserResult result, SchedulerEvent event) {
        if (caretDocumentPosition == -1) {
            return;
        }

        //uses just lexer
        TokenHierarchy<?> tokenHierarchy = result.getSnapshot().getTokenHierarchy();
        TokenSequence<AntlrTokenId> ts = tokenHierarchy.tokenSequence(AntlrTokenId.language());
        int diff = ts.move(caretDocumentPosition);

        Token<AntlrTokenId> curr = null;
        if (diff == 0) {
            if (ts.movePrevious()) {
                curr = ts.token();
            }
        } else {
            if (ts.moveNext()) {
                curr = ts.token();
            }
        }
        if (curr == null) {
            return;
        }

        switch (curr.id()) {
            case TOKEN_REF:
            case RULE_REF:
                break;
            default:
                return;
        }

        occurrencesMap.clear();

        //we are on a token_ref or rule_ref token
        ts.moveStart();
        while (ts.moveNext()) {
            Token<AntlrTokenId> token = ts.token();
            if (token.id() == curr.id() && Utils.equals(token.text(), curr.text(), false, false)) {
                occurrencesMap.put(new OffsetRange(ts.offset(), ts.offset() + token.length()), ColoringAttributes.MARK_OCCURRENCES);
            }
        }

    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return null;
    }

    @Override
    public void cancel() {
    }
}
