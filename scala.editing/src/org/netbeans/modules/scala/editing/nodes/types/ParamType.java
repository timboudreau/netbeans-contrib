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
package org.netbeans.modules.scala.editing.nodes.types;

import javax.lang.model.element.ElementKind;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.gsf.api.HtmlFormatter;

/**
 * TypeRef of parameters
 *
 * @author Caoyuan Deng
 */
public class ParamType extends TypeRef {
    
    public enum More {

        Raw,
        Star,
        ByName,
    }
           
    private More more;
    private TypeRef rawType;

    public ParamType(Token idToken, ElementKind kind) {
        super(null, idToken, kind);
    }

    public void setRawType(TypeRef rawType) {
        this.rawType = rawType;
    }

    public TypeRef getRawType() {
        return rawType;
    }

    public void setMore(More more) {
        this.more = more;
    }

    public More getMore() {
        return more;
    }

    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        switch (more) {
            case Star:
                sb.append(rawType.getName());
                sb.append("*");
                break;
            case ByName:
                sb.append("=>");
                sb.append(rawType.getName());
                break;
            default:
                sb.append(rawType.getName());
        }
        return sb.toString();
    }


    @Override
    public void htmlFormat(HtmlFormatter formatter) {
        switch (more) {
            case Star:
                rawType.htmlFormat(formatter);
                formatter.appendText("*");
                break;
            case ByName:
                formatter.appendText("\u21D2");
                rawType.htmlFormat(formatter);
                break;
            default:
                rawType.htmlFormat(formatter);                
        }
    }
}
