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
package org.netbeans.modules.latex.model.structural.parser;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.label.LabelStructuralElement;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class MainStructuralElement extends StructuralElement {

    private List<StructuralElement> labels;

    public MainStructuralElement() {
        labels = new ArrayList<StructuralElement>();
    }
    
    public SystemAction[] createActions() {
        return new SystemAction[0];
    }
    
    public int getPriority() {
        return -1;
    }
    
    public synchronized List<? extends StructuralElement> getLabels() {
        //Defense copy (against concurrent modifications):
        return new ArrayList<StructuralElement>(labels);
    }
    
    public synchronized void addLabel(LabelStructuralElement el) {
        labels.add(el);
    }
    
    public synchronized void clearLabels() {
        labels.clear();
    }
    
//    public boolean equals(Object o) {
//        System.err.println("MainStructuralElement.equals, this=" + this + ", o=" + o);
//        if (!getClass().equals(o.getClass()))
//            return false;
//        
//        System.err.println("same class.");
//        
//        MainStructuralElement el = (MainStructuralElement) o;
//        
//        if (getPriority() != el.getPriority())
//            return false;
//        
//        System.err.println("same priority");
//        
////        System.err.println("getSubElements = " + getSubElements() );
////        System.err.println("el.getSubElements = " + el.getSubElements() );
////        
////        if (!getSubElements().equals(el.getSubElements()))
////            return false;
//        
//        System.err.println("same subelements");
//        
//        return true;
//    }
//    
//    public int hashCode() {
//        return 1; //!!!!!
//    }

}
