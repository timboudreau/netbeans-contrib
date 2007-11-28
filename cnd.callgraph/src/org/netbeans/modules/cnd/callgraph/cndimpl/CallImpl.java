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
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.callgraph.api.Call;
import org.netbeans.modules.cnd.modelutil.CsmImageLoader;

/**
 *
 * @author Alexander Simon
 */
public class CallImpl implements Call {

    private CsmReference reference;
    private CsmFunction function;
    private String name;
    
    public CallImpl(CsmReference reference){
        this.reference = reference;
    }

    public CallImpl(CsmReference reference, CsmFunction function){
        this.function = function;
        this.reference = reference;
    }

    public Object getUserObject() {
        return reference;
    }

    public int compareTo(Call o) {
        return getName().compareTo(o.getName());
    }

    public String getName() {
        if (name == null) {
            setName();
        }
        return name;
    }

    public String getDescription() {
        return getName();
    }
    
    public Image getIcon(int param) {
        try {
            CsmObject csmObj = getCsmObject();
            if (csmObj != null) {
                return CsmImageLoader.getImage(csmObj);
            }
        } catch (AssertionError ex){
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void setName() {
        CsmFunction f = getCsmObject();
        if (f != null){
            if (CsmKindUtilities.isFunctionDefinition(f)) {
                CsmFunction decl = ((CsmFunctionDefinition)f).getDeclaration();
                if (decl != null){
                    f = decl;
                }
            }
            if (f instanceof CsmMember) {
                CsmMember m = (CsmMember) f;
                name = f.getName()+" in "+m.getContainingClass().getName();
            } else {
                name = f.getName();
            }
        } else {
            name = "Unknown";
        }
    }

    public CsmFunction getCsmObject() {
        if (function != null) {
            return function;
        }
            
        CsmObject o = reference.getReferencedObject();
        if (o instanceof CsmFunction) {
            return (CsmFunction)o;
        }
        return null;
    }

}
