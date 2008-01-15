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

package org.netbeans.modules.a11ychecker.traverse;

import java.awt.Component;
import java.io.StringWriter;
import java.util.Vector;
import org.netbeans.modules.form.FormModel;
import org.netbeans.modules.form.RADComponent;
import org.netbeans.modules.form.VisualReplicator;

/*
 * focusTraversalPolicy string generator 
 * @author Michal Hapala, Pavel Stehlik
 */
public class FocusTraversalPolicyGenerator {
    FormModel myModel;
    VisualReplicator myRepl;
    
    public FocusTraversalPolicyGenerator(FormModel m, VisualReplicator r) {
        myModel = m;
        myRepl = r;
    }
    
    public RADComponent getMetaComponent(Component comp)
    {
        return myModel.getMetaComponent( myRepl.getClonedComponentId(comp) );
    }
    
    /**
     * Generates string with the proper definition of focusTraversalPolicy class
     * @param first designated first button in traversal
     * @param last designated last button in traversal
     * @param vecButtons all buttons in traversal with their assigned components
     * @return
     */
    public String generate(OverflowLbl first,OverflowLbl last, Vector<OverflowLbl> vecButtons) {
        StringWriter myWriter = new StringWriter();
        OverflowLbl rFirst = first;
        OverflowLbl rLast = last;
        myWriter.append("new java.awt.FocusTraversalPolicy() {\n");
        
        myWriter.append("public java.awt.Component getDefaultComponent(java.awt.Container focusCycleRoot){\n");
        
        myWriter.append("return "+ getMetaComponent(rFirst.mycomp).getName() +";\n");
        myWriter.append("}//end getDefaultComponent\n\n");
        
        myWriter.append("public java.awt.Component getFirstComponent(java.awt.Container focusCycleRoot){\n");
        myWriter.append("return "+ getMetaComponent(rFirst.mycomp).getName() +";\n");
        myWriter.append("}//end getFirstComponent\n\n");
        
        myWriter.append("public java.awt.Component getLastComponent(java.awt.Container focusCycleRoot){\n");
        myWriter.append("return "+ getMetaComponent(rLast.mycomp).getName() +";\n");
        myWriter.append("}//end getLastComponent\n\n");
        
        myWriter.append("public java.awt.Component getComponentAfter(java.awt.Container focusCycleRoot, java.awt.Component aComponent){\n");
        for (int i = 0; i < vecButtons.size(); i++) {
            OverflowLbl r = ((OverflowLbl)vecButtons.get(i));
            if(r.nextcomp == null) continue;
            
            RADComponent metanext = getMetaComponent( r.nextcomp );
            RADComponent metamy = getMetaComponent( r.mycomp );
            if(metanext == null || metamy == null) continue;
            
            myWriter.append("if(aComponent ==  "+ metamy.getName() +"){\n");
            myWriter.append("return "+ metanext.getName() +";\n");
            myWriter.append("}\n");
        }
        myWriter.append("return "+ getMetaComponent(rFirst.mycomp).getName() +";//end getComponentAfter\n");
        myWriter.append("}\n");
        
        myWriter.append("public java.awt.Component getComponentBefore(java.awt.Container focusCycleRoot, java.awt.Component aComponent){\n");
        for (int i = 0; i < vecButtons.size(); i++) {
            OverflowLbl r = ((OverflowLbl)vecButtons.get(i));
            if(r.nextcomp == null) continue;
            
            RADComponent metanext = getMetaComponent( r.nextcomp );
            RADComponent metamy = getMetaComponent( r.mycomp );
            if(metanext == null || metamy == null) continue;
            
            myWriter.append("if(aComponent ==  "+ metanext.getName() +"){\n");
            myWriter.append("return "+ metamy.getName() +";\n");
            myWriter.append("}\n");
        }
        myWriter.append("return "+ getMetaComponent(rLast.mycomp).getName() +";//end getComponentBefore\n\n");
        myWriter.append("}");
        
        myWriter.append("}\n");
        return myWriter.toString();
    }
}
