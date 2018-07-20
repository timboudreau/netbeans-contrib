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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.vcscore.annotation;

import java.beans.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.util.Lookup;

import org.openide.util.NbBundle;

/** Property editor for annotation pattern properties
*
* @author Milos Kleint

*/
public abstract class AnnotationPatternPropertyEditor extends PropertyEditorSupport {


    public void setValue(Object value) {
        super.setValue(value);
    }

    public java.lang.String getAsText() {
        java.lang.String retValue;
        
        retValue = super.getAsText();
        return retValue;
    }
    
    public void setAsText(java.lang.String str) throws java.lang.IllegalArgumentException {
        super.setAsText(str);
    }
    
    public java.lang.Object getValue() {
        java.lang.Object retValue;
        
        retValue = super.getValue();
        return retValue;
    }
    
    public abstract String[] getPatterns();
    
    public abstract String[] getPatternDisplaNames();

    public abstract String getDefaultAnnotationPattern();
    
    public java.awt.Component getCustomEditor() {
        AnnotPatternCustomEditor editor = new AnnotPatternCustomEditor();
        editor.setCallingPropertyEditor(this);
        return editor;
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }
    
}

