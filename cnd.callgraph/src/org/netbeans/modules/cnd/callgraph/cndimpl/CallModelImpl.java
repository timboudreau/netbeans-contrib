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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.api.model.CsmProject;
import org.netbeans.modules.cnd.api.model.CsmScope;
import org.netbeans.modules.cnd.api.model.deep.CsmCondition;
import org.netbeans.modules.cnd.api.model.deep.CsmExpression;
import org.netbeans.modules.cnd.api.model.deep.CsmStatement;
import org.netbeans.modules.cnd.api.model.services.CsmFileReferences;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.api.model.xref.CsmReferenceRepository;
import org.netbeans.modules.cnd.callgraph.api.Call;
import org.netbeans.modules.cnd.callgraph.api.CallModel;

/**
 *
 * @author Alexander Simon
 */
public class CallModelImpl implements CallModel {
    private CsmReferenceRepository repository;
    private CsmFileReferences references;
    private CsmProject project;
    
    public CallModelImpl(CsmProject project){
        repository = CsmReferenceRepository.getDefault();
        references = CsmFileReferences.getDefault();
        this.project = project;
    }

    public List<Call> getCallers(Call function) {
        CsmReference ref = (CsmReference) function.getUserObject();
        CsmObject owner = getOwner(ref);
        if (!CsmKindUtilities.isFunction(owner)) {
            owner = ref.getOwner();
        }
        if (CsmKindUtilities.isFunction(owner)) {
            HashMap<CsmFunction,CsmReference> set = new HashMap<CsmFunction,CsmReference>();
            for(CsmReference r : repository.getReferences(owner, project, true)){
                CsmFunction o = getOwner(r);
                if (o != null) {
                    if (!set.containsKey(o)) {
                        set.put(o, r);
                    }
                }
            }
            List<Call> res = new ArrayList<Call>();
            for(Map.Entry<CsmFunction,CsmReference> r : set.entrySet()){
                res.add(new CallImpl(r.getValue(),r.getKey()));
            }
            return res;
        } else {
            return Collections.<Call>emptyList();
        }
    }

    private CsmFunction getOwner(CsmReference ref){
        CsmObject o = ref.getOwner();
        if (CsmKindUtilities.isExpression(o)){
            o = ((CsmExpression)o).getScope();
        } else if (o instanceof CsmCondition){
            o = ((CsmCondition)o).getScope();
        }
        if (CsmKindUtilities.isStatement(o)){
            CsmScope scope = ((CsmStatement)o).getScope();
            while(scope != null){
                if (CsmKindUtilities.isFunction(scope)){
                    return (CsmFunction) scope;
                } else if (CsmKindUtilities.isStatement(scope)){
                    scope = ((CsmStatement)scope).getScope();
                } else {
                    return null;
                }
            }
        }
        return null;
    }
    
    public List<Call> getCalls(Call definition) {
        CsmReference ref = (CsmReference) definition.getUserObject();
        CsmObject owner = ref.getReferencedObject();
        if (CsmKindUtilities.isFunctionDeclaration(owner)){
            owner = ((CsmFunction)owner).getDefinition();
        }
        if (CsmKindUtilities.isFunctionDefinition(owner)) {
            //final CsmFunctionDefinition analyzedDefinition = (CsmFunctionDefinition)owner;
            //final CsmFunction analyzedDeclaration = analyzedDefinition.getDeclaration();
            final HashMap<CsmObject,CsmReference> set = new HashMap<CsmObject,CsmReference>();
            references.accept((CsmScope)owner, new CsmFileReferences.Visitor() {
                public void visit(CsmReference r) {
                    CsmObject o = r.getReferencedObject();
                    if (CsmKindUtilities.isFunction(o) &&
                        !CsmKindUtilities.isFunction(r.getOwner())){//!analyzedDefinition.equals(o) && !analyzedDeclaration.equals(o)) {
                        if (!set.containsKey(o)) {
                            set.put(o, r);
                        }
                    }
                }
            });
            List<Call> res = new ArrayList<Call>();
            for(CsmReference r : set.values()){
                res.add(new CallImpl(r));
            }
            return res;
        } else {
            return Collections.<Call>emptyList();
        }
    }
}
