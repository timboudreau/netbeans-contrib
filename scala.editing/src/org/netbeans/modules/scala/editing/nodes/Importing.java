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
package org.netbeans.modules.scala.editing.nodes;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.scala.editing.nodes.types.Type;

/**
 *
 * @author Caoyuan Deng
 */
public class Importing extends AstElement {

    private List<AstId> paths;
    private List<Type> importedTypes;
    private boolean wild;

    public Importing(Token idToken, AstScope bindingScope) {
        super(null, idToken, bindingScope, ElementKind.OTHER);
    }

    @Override
    public Name getSimpleName() {
        if (super.getSimpleName() == null) {
            setSimpleName(NO_MEANING_NAME);
        }
        return super.getSimpleName();
    }

    public void setPaths(List<AstId> paths) {
        this.paths = paths;
    }

    public List<AstId> getPaths() {
        return paths;
    }

    public void setImportedTypes(List<Type> importedTypes) {
        this.importedTypes = importedTypes;
    }

    public List<Type> getImportedTypes() {
        return importedTypes == null ? Collections.<Type>emptyList() : importedTypes;
    }

    public void setWild() {
        this.wild = true;
    }

    public boolean isWild() {
        return wild;
    }

    public String getPackageName() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<AstId> itr = paths.iterator(); itr.hasNext();) {
            sb.append(itr.next().getSimpleName());
            if (itr.hasNext()) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean isMirroredBy(AstMirror mirror) {
        return false;
    }

    @Override
    public boolean mayEqual(AstElement element) {
        return false;
    }        
    
    /** @Todo should define another named method */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (AstId id : paths) {
            sb.append(id.getSimpleName()).append(".");
        }

        if (isWild()) {
            sb.append("_");
        } else if (getImportedTypes().size() == 1) {
            sb.append(getImportedTypes().get(0).getSimpleName());
        }

        return sb.toString();
    }
}
