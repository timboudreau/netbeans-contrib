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
package org.netbeans.modules.scala.editing;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.scala.editing.ast.AstRootScope;
import org.netbeans.modules.scala.editing.ast.AstTreeVisitor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import scala.tools.nsc.CompilationUnits.CompilationUnit;
import scala.tools.nsc.Global;
import scala.tools.nsc.io.AbstractFile;
import scala.tools.nsc.io.PlainFile;
import scala.tools.nsc.io.VirtualFile;
import scala.tools.nsc.util.BatchSourceFile;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaParserResult extends ParserResult {

    public enum Phase {

        Modified,
        Parsed,
        GLOBAL_RESOLVED
    }
    private List<Error> errors;
    private String source;
    private OffsetRange sanitizedRange = OffsetRange.NONE;
    private String sanitizedContents;
    private ScalaParser.Sanitize sanitized;
    private boolean commentsAdded;
    private AstRootScope rootScope;
    private AstRootScope rootScopeForDebugger;
    private Phase phase;
    private ScalaParser parser;

    public ScalaParserResult(ScalaParser parser, Snapshot snapshot, AstRootScope rootScope, List<Error> errors) {
        super(snapshot);
        this.parser = parser;
        this.rootScope = rootScope;
        this.phase = Phase.Parsed;
        this.errors = errors;
    }

    @Override
    protected void invalidate() {
        // XXX: what exactly should we do here?
    }

    @Override
    public List<? extends Error> getDiagnostics() {
        return errors == null ? Collections.<Error>emptyList() : errors;
    }

    public void setErrors(List<? extends Error> errors) {
        this.errors = new ArrayList<Error>(errors);
    }

    public ScalaParser parser() {
        return parser;
    }

    public AstRootScope rootScope() {
        return rootScope;
    }

    public AstRootScope rootScopeForDebugger() {
        if (rootScopeForDebugger == null) {
            FileObject fo = getSnapshot().getSource().getFileObject();
            File file = fo != null ? FileUtil.toFile(fo) : null;
            // We should use absolutionPath here for real file, otherwise, symbol.sourcefile.path won't be abs path
            String filePath = file != null ? file.getAbsolutePath() : "<current>";
            TokenHierarchy th = getSnapshot().getTokenHierarchy();

            Global global = parser.global();

            AbstractFile af = file != null ? new PlainFile(file) : new VirtualFile("<current>", "");
            BatchSourceFile srcFile = new BatchSourceFile(af, getSnapshot().getText().toString().toCharArray());
            try {
                CompilationUnit unit = ScalaGlobal.compileSourceForDebugger(parser.global(), srcFile);
                rootScopeForDebugger = new AstTreeVisitor(global, unit, th, srcFile).getRootScope();
            } catch (AssertionError ex) {
                // avoid scala nsc's assert error
                ScalaGlobal.reset();
            } catch (java.lang.Error ex) {
                // avoid scala nsc's exceptions
            } catch (IllegalArgumentException ex) {
                // An internal exception thrown by ParserScala, just catch it and notify
            } catch (Exception ex) {
                // Scala's global throws too many exceptions
                //ex.printStackTrace();
            }
        }

        return rootScopeForDebugger;
    }

    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Return whether the source code for the parse result was "cleaned"
     * or "sanitized" (modified to reduce chance of parser errors) or not.
     * This method returns OffsetRange.NONE if the source was not sanitized,
     * otherwise returns the actual sanitized range.
     */
    public OffsetRange sanitizedRange() {
        return sanitizedRange;
    }

    public String sanitizedContents() {
        return sanitizedContents;
    }

    /**
     * Set the range of source that was sanitized, if any.
     */
    void setSanitized(ScalaParser.Sanitize sanitized, OffsetRange sanitizedRange, String sanitizedContents) {
        this.sanitized = sanitized;
        this.sanitizedRange = sanitizedRange;
        this.sanitizedContents = sanitizedContents;
    }

    public ScalaParser.Sanitize getSanitized() {
        return sanitized;
    }

    public boolean isCommentsAdded() {
        return commentsAdded;
    }

    public void setCommentsAdded(boolean commentsAdded) {
        this.commentsAdded = commentsAdded;
    }

    public Phase phase() {
        return phase == null ? Phase.Modified : phase;
    }

    public void toGlobalPhase(ParserResult info) {
        if (rootScope == null) {
            return;
        }

//        if (this.phase != Phase.GLOBAL_RESOLVED) {
//            ScalaIndex index = ScalaIndex.get(info);
//            new ScalaTypeInferencer(rootScope, tokenHierarchy).globalInfer(index);
//            this.phase = Phase.GLOBAL_RESOLVED;
//        }
    }

    public void toGlobalPhase(ScalaIndex index) {
        if (rootScope == null) {
            return;
        }

//        if (this.phase != Phase.GLOBAL_RESOLVED) {
//            new ScalaTypeInferencer(rootScope, tokenHierarchy).globalInfer(index);
//            this.phase = Phase.GLOBAL_RESOLVED;
//        }
    }

    @Override
    public String toString() {
        return "ParserResult(file=" + getSnapshot().getSource().getFileObject() + ",rootScope=" + rootScope + ",phase=" + phase + ")";
    }
}
