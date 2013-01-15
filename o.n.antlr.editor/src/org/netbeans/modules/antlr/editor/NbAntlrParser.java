/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.antlr.editor;

import java.util.Collection;
import javax.swing.event.ChangeListener;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.antlr.editor.gen.ANTLRv3Lexer;
import org.netbeans.modules.antlr.editor.gen.ANTLRv3Parser;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserFactory;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

/**
 *
 * @author mfukala@netbeans.org
 */
public class NbAntlrParser extends Parser {

    private NbAntlrParserResult result;

    public static NbAntlrParserResult parse(Snapshot snapshot) throws ParseException {
        if (snapshot == null) {
            return null;
        }
        CharSequence input = snapshot.getText();

        try {
            ANTLRv3Lexer lex = new ANTLRv3Lexer(new ANTLRStringStream(input.toString()));
            CommonTokenStream tokens = new CommonTokenStream(lex);
            ANTLRv3Parser parser = new ANTLRv3Parser(tokens, null);
            CommonTree tree = (CommonTree) parser.grammarDef().getTree();
            return new NbAntlrParserResult(snapshot, tree);
        } catch (RecognitionException ex) {
            throw new ParseException(String.format("Error parsing %s snapshot.", snapshot), ex); //NOI18N
        }
    }

   

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
        result = parse(snapshot);
    }

    @Override
    public ParserResult getResult(Task task) throws ParseException {
        return result;
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        //no-op
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        //no-op
    }

    @MimeRegistration(mimeType = "text/antlr", service = ParserFactory.class)
    public static class Factory extends ParserFactory {

        @Override
        public Parser createParser(Collection<Snapshot> snapshots) {
            return new NbAntlrParser();
        }
    }
}
