/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.python.editor;

import org.antlr.runtime.ANTLRStringStream;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.Parser;
import org.netbeans.modules.gsf.api.PositionManager;

import org.netbeans.modules.gsf.api.ParseEvent;
import org.netbeans.modules.gsf.api.ParseListener;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.gsf.api.Severity;
import org.netbeans.modules.gsf.api.SourceFileReader;
import org.netbeans.modules.gsf.spi.DefaultError;
import org.python.antlr.ModuleParser;
import org.python.antlr.ParseException;
import org.python.antlr.PythonTree;

/**
 * Parser for Python. Wraps Jython.
 * 
 * @todo Parser error recovery
 * 
 * @author Frank Wierzbicki
 * @author Tor Norbye
 */
public class PythonParser implements Parser {

    public PythonParserResult parse(String source, ParserFile file) throws Exception {
        try {
            ModuleParser g = new ModuleParser(new ANTLRStringStream(source));
            PythonTree t = g.file_input();
            return new PythonParserResult(t, this, file);
        } catch (ParseException pe) {
            int offset = pe.getOffset();
            assert offset >= 0;
            String desc = pe.getLocalizedMessage();
            if (desc == null) {
                desc = pe.getMessage();
            }
            String key = null;
            DefaultError error = new DefaultError(key, desc, null, file.getFileObject(), offset, offset, Severity.ERROR);
            PythonParserResult parserResult = new PythonParserResult(null, this, file);
            parserResult.addError(error);
            return parserResult;
        }
    }
    
    private static String asString(CharSequence sequence) {
        if (sequence instanceof String) {
            return (String)sequence;
        } else {
            return sequence.toString();
        }
    }

    public void parseFiles(Job job) {
        ParseListener listener = job.listener;
        SourceFileReader reader = job.reader;
        
        for (ParserFile file : job.files) {
            ParseEvent beginEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, null);
            listener.started(beginEvent);
            
            ParserResult result = null;

            try {
                CharSequence buffer = reader.read(file);
                String source = asString(buffer);
                
                int caretOffset = reader.getCaretOffset(file);
                if (caretOffset != -1 && job.translatedSource != null) {
                    caretOffset = job.translatedSource.getAstOffset(caretOffset);
                }
                result = parse(source, file);
            } catch (Exception ioe) {
                listener.exception(ioe);
            }

            ParseEvent doneEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, result);
            listener.finished(doneEvent);
        }
    }

    public PositionManager getPositionManager() {
        return new PythonPositionManager();
    }

    private class PythonPositionManager implements PositionManager {
        public OffsetRange getOffsetRange(CompilationInfo info, ElementHandle object) {
            return OffsetRange.NONE;
        }
    }
}
