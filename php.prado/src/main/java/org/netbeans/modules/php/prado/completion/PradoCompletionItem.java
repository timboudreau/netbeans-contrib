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

package org.netbeans.modules.php.prado.completion;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import org.netbeans.modules.gsf.api.CodeCompletionContext;
import org.netbeans.modules.gsf.api.CompletionProposal;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.gsf.api.Modifier;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Petr Pisl
 */
public class PradoCompletionItem implements CompletionProposal {

    private static final String PRADO_ICON = "org/netbeans/modules/php/prado/resources/prado_icon_16x16.png"; //NOI18N
    protected static ImageIcon icon = null;
    protected final CodeCompletionContext context;
    private final String name;
    private final String prefix;

    PradoCompletionItem (CodeCompletionContext context, String name, String prefix) {
        this.context = context;
        this.name = name;
        this.prefix = prefix;
        if (icon == null) {
            icon = new ImageIcon(ImageUtilities.loadImage(PRADO_ICON));
        }
    }

    public int getAnchorOffset() {
        return context.getCaretOffset();
    }

    public ElementHandle getElement() {
        return null;
    }

    public String getName() {
        return name;
    }

    public String getInsertPrefix() {
        return name.substring(prefix.length());
    }

    public String getSortText() {
        return name;
    }

    public String getLhsHtml(HtmlFormatter formatter) {
        return name;
    }

    public String getRhsHtml(HtmlFormatter formatter) {
        return null;
    }

    public ElementKind getKind() {
        return ElementKind.PROPERTY;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public Set<Modifier> getModifiers() {
        return Collections.EMPTY_SET;
    }

    public boolean isSmart() {
        return false;
    }

    public String getCustomInsertTemplate() {
        return null;
    }

    public List<String> getInsertParams() {
        return null;
    }

    public String[] getParamListDelimiters() {
        return null;
    }

    public int getSortPrioOverride() {
       return 1;
    }

}
