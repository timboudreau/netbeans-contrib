/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.latex.hints;

import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.napi.gsfret.source.CompilationInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Severity;

/**
 *
 * @author Jan Lahoda
 */
public class UnknownCiteHint implements HintProvider {

    public boolean accept(CompilationInfo info, Node n) {
        return n instanceof ArgumentNode && n.hasAttribute("#cite");
    }

    public List<ErrorDescription> computeHints(CompilationInfo info, Node n) throws Exception {
        LaTeXParserResult lpr = (LaTeXParserResult) info.getParserResult();
        ArgumentNode anode = (ArgumentNode) n;
        String       nodeValue = lpr.getCommandUtilities().getArgumentValue(anode).toString();
        List<ErrorDescription> res = new  LinkedList<ErrorDescription>();
        CharSequence fullText = anode.getFullText();
        int currentOffset = anode.getStartingPosition().getOffsetValue() + (fullText.length() != nodeValue.length() ? 1 : 0);
        int endOffset = anode.getEndingPosition().getOffsetValue() + (fullText.length() - nodeValue.length() == 2 ? -1 : 0);
        
        for (String citation : nodeValue.split(",")) {
            boolean found = false;

            for (PublicationEntry entry : Utilities.getDefault().getAllBibReferences((LaTeXParserResult) info.getParserResult())) {
                if (citation.equals(entry.getTag())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                res.add(ErrorDescriptionFactory.createErrorDescription(Severity.WARNING, "Undefined citation", info.getFileObject(), currentOffset, Math.min(citation.length() + currentOffset, endOffset)));
            }
            
            currentOffset += citation.length() + 1;
        }
        
        return res;
    }
    
}
