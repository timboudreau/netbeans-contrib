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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.callgraph.cndimpl;

import java.awt.Image;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmFunctionDefinition;
import org.netbeans.modules.cnd.api.model.CsmMember;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.callgraph.api.Call;
import org.netbeans.modules.cnd.modelutil.CsmImageLoader;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexander Simon
 */
public class CallImpl implements Call {

    private CsmFunction owner;
    private CsmReference reference;
    private CsmFunction function;
    
    public CallImpl(CsmFunction owner, CsmReference reference, CsmFunction function){
        this.owner = owner;
        this.function = function;
        this.reference = reference;
    }

    public Object getReferencedCall() {
        return reference;
    }

    public Object getFunctionDeclaration() {
        return function;
    }

    public Object getCallOwner() {
        return owner;
    }

    public String getFunctionName() {
        return function.getName();
    }

    public String getOwnerName() {
        return owner.getName();
    }

    public String getFunctionDescription() {
        return getDescription(function);
    }

    public String getOwnerDescription() {
        return getDescription(owner);
    }

    public Image getOwnerIcon() {
        return getIcon(owner);
    }
    
    public Image getFunctionIcon() {
        return getIcon(function);
    }

    public int compareTo(Call o) {
        return getFunctionName().compareTo(o.getFunctionName());
    }

    private Image getIcon(CsmFunction f) {
        try {
            return CsmImageLoader.getImage(f);
        } catch (AssertionError ex){
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getDescription(CsmFunction f) {
        f = getFunction(f);
        if (CsmKindUtilities.isFunctionDefinition(f)) {
            CsmFunction decl = ((CsmFunctionDefinition) f).getDeclaration();
            if (decl != null) {
                f = decl;
            }
        }
        String n;
        if (f instanceof CsmMember) {
            CsmMember m = (CsmMember) f;
            n = f.getName() + " in " + m.getContainingClass().getName();
        } else {
            n = f.getName();
        }
        return n;
    }

    private CsmFunction getFunction(CsmFunction f) {
       if (CsmKindUtilities.isFunctionDefinition(f)) {
           CsmFunction decl = ((CsmFunctionDefinition)f).getDeclaration();
           if (decl != null){
               f = decl;
           }
       }
       return f;
    }

    private String getString(String key) {
        return NbBundle.getMessage(getClass(), key);
    }
}
