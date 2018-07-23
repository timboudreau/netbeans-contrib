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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.visitors;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates sufficient information which the user entered in the UI to be
 * able to determine if the requested parameter changes are possible.
 * 
 * Provides the necessary information about modified parameters to be able to
 * test existing parameters in overridden methods and figure out if the
 * existing parameter names match the names of the parameters in an overridden
 * method, and figure out if any of the new parameter names match existing 
 * variable names inside those methods.  Immuntable.
 * 
 * See ChangeSignaturePlugin.preCheck() and VariableNameScanner.
 *
 * @author Tim Boudreau
 */
public final class RequestedParameterChanges {
    private final Collection <String> newParameterNames;
    private final Collection <String> newOrChangedParameterNames;
    private final List <String> origParamNamesInOrder;
    private final ParameterRenamePolicy policy;
    public RequestedParameterChanges (Collection<String> newParameterNames, 
        Collection<String> newOrChangedParamNames, List <String> origParamNamesInOrder,
        ParameterRenamePolicy policy) {
        
        this.origParamNamesInOrder = Collections.<String>unmodifiableList(origParamNamesInOrder);
        this.newParameterNames = Collections.<String>unmodifiableCollection(newParameterNames);
        this.newOrChangedParameterNames = Collections.<String>unmodifiableCollection(newOrChangedParamNames);
        this.policy = policy;
    }

    /** Determine if there are no new parameters or changed parameter names.
     * If this is the case, then no parameter name conflict check needs to be
     * done */
    public boolean isEmpty() {
        return newOrChangedParameterNames.isEmpty();
    }

    /**
     * Determine if the passed string matches a newly defined parameter name.
     */ 
    public boolean isNewParameterName (String s) {
        return newParameterNames.contains (s);
    }

    /**
     * Determine if the passed string matches a parameter name entered by the
     * user for either a new parameter or a renamed one.
     */ 
    public boolean isNewOrChangedParameterName (String s) {
        return newOrChangedParameterNames.contains(s);
    }
    
    /**
     * Get the parameter rename policy the user specified.
     */ 
    public ParameterRenamePolicy getPolicy() {
        return policy;
    }

    /**
     * Determine if a string matches the original parameter name at the same
     * position in the list of parameters of an overriding method.
     */ 
    public boolean matchesOriginalParameterName(int idx, String paramName) {
        return origParamNamesInOrder.get(idx).equals(paramName);
    }
    
    public Set <String> getNewParameterNames() {
        return Collections.<String>unmodifiableSet(new HashSet<String>(newParameterNames));
    }
}
