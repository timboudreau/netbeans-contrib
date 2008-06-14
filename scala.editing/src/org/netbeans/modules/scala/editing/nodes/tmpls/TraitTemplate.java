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
package org.netbeans.modules.scala.editing.nodes.tmpls;

import java.util.Iterator;
import javax.lang.model.element.ElementKind;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.AstId;
import org.netbeans.modules.scala.editing.nodes.types.TypeParam;
import org.netbeans.modules.scala.editing.nodes.types.WithTypeParams;

/**
 *
 * @author Caoyuan Deng
 */
public class TraitTemplate extends Template implements WithTypeParams {

    public TraitTemplate(AstId id, AstScope bindingScope) {
        super(id, bindingScope, ElementKind.INTERFACE);
    }

    @Override
    public boolean isCaseOne() {
        return false;
    }

    @Override
    public String getBinaryName() {
        return getSimpleName().toString();
    }

    @Override
    public void htmlFormat(HtmlFormatter formatter) {
        formatter.appendText(getSimpleName().toString());
        if (!getTypeParameters().isEmpty()) {
            formatter.appendText("[");

            for (Iterator<? extends TypeParam> itr = getTypeParameters().iterator(); itr.hasNext();) {
                TypeParam typeParam = itr.next();
                typeParam.htmlFormat(formatter);

                if (itr.hasNext()) {
                    formatter.appendHtml(", ");
                }
            }

            formatter.appendText("]");
        }
    }
}
