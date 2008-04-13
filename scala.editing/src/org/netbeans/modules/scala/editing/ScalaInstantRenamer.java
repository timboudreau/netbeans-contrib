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
package org.netbeans.modules.scala.editing;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.InstantRenamer;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.openide.util.NbBundle;

/**
 * Handle instant rename for Fortress
 * 
 * @author Caoyuan Deng
 */
public class ScalaInstantRenamer implements InstantRenamer {

    public ScalaInstantRenamer() {
    }

    public boolean isRenameAllowed(CompilationInfo info, int caretOffset, String[] explanationRetValue) {
        AstScope root = AstUtilities.getRoot(info);

        if (root == null) {
            explanationRetValue[0] = NbBundle.getMessage(ScalaInstantRenamer.class, "NoRenameWithErrors");

            return false;
        }

        int astOffset = AstUtilities.getAstOffset(info, caretOffset);
        if (astOffset == -1) {
            return false;
        }

        ScalaParserResult result = AstUtilities.getParserResult(info);
        AstScope rootScope = result.getRootScope();
        
        AstElement closest = rootScope.getElement(caretOffset);

        switch (closest.getKind()) {
            case FIELD:
            case PARAMETER:
            case VARIABLE:
                return true;
            // TODO - block renaming of GLOBALS! I should already know
            // what's local and global based on JsSemantic...
        }

        return false;
    }

    public Set<OffsetRange> getRenameRegions(CompilationInfo info, int caretOffset) {
        ScalaParserResult result = AstUtilities.getParserResult(info);
        if (result == null) {
            return Collections.emptySet();
        }

        int astOffset = AstUtilities.getAstOffset(info, caretOffset);
        if (astOffset == -1) {
            return Collections.emptySet();
        }

        AstScope rootScope = result.getRootScope();
        
        AstElement closest = rootScope.getElement(caretOffset);

        List<AstElement> occurrences = rootScope.findOccurrences(closest);
        
        Set<OffsetRange> regions = new HashSet<OffsetRange>();
            for (AstElement element : occurrences) {
                regions.add(element.getNameRange());
            }
        
        if (regions.size() > 0) {
            if (result.getTranslatedSource() != null) {
                Set<OffsetRange> translated = new HashSet<OffsetRange>(2*regions.size());
                for (OffsetRange astRange : regions) {
                    OffsetRange lexRange = ScalaLexUtilities.getLexerOffsets(info, astRange);
                    if (lexRange != OffsetRange.NONE) {
                        translated.add(lexRange);
                    }
                }

                regions = translated;
            }
        }
        
        return regions;
    }
}
