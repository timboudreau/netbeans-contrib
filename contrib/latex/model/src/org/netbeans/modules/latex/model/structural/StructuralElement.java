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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.structural;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openide.util.HelpCtx;

/**
 *
 * @author Jan Lahoda
 */
public abstract class StructuralElement implements HelpCtx.Provider {
    
    public static final String SUB_ELEMENTS = "subElements";
    
    protected static final int NO_PRIORITY = (-1);
    
    private List<StructuralElement> subElements;
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    /** Creates a new instance of StructuralNode */
    public StructuralElement() {
        subElements = new ArrayList<StructuralElement>();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    public List<? extends StructuralElement> getSubElements() {
        return Collections.unmodifiableList(subElements);
    }
    
    public void addSubElement(StructuralElement el) {
        assert getPriority() == NO_PRIORITY || el.getPriority() == NO_PRIORITY || getPriority() < el.getPriority() : "Priority inversion parent priority=" + getPriority() + ", child priority=" + el.getPriority();
        
        subElements.add(el);
        
//        pcs.firePropertyChange(SUB_ELEMENTS, null, null);
    }
    
    //!!!!only authorized personel should do this (in particular, this should be done only when the element is updated)!
    public void clearSubElements() {
        subElements.clear();
//        pcs.firePropertyChange(SUB_ELEMENTS, null, null);
    }
    
    public void fireSubElementsChange() {
        pcs.firePropertyChange(SUB_ELEMENTS, null, null);
    }
    
    public abstract int getPriority();
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
        
//    public SourcePosition getStartingPosition() {
//        return null;
//    }
//    
//    public SourcePosition getEndingPosition() {
//        return null;
//    }
}
