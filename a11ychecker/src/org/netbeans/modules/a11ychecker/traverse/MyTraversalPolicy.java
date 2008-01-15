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
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/*
 * Class for serializing info about a defined traversal into the value of
 * FocusTraversalPolicy
 *
 * @author Michal Hapala
 * @author Pavel Stehlik
 */
public class MyTraversalPolicy extends FocusTraversalPolicy implements Serializable {

    public MyTraversalPolicy() {
    }
    
    String start;
    String end;
    private Vector<MySavingButton> savedButtons;

    public Vector<MySavingButton> getSavedBtns() {
        return savedButtons;
    }
    
    /**
     * Checks current state of defined tab traversal and returns errorneous 
     * components, in this case those that are unaccessible by traversal. To be 
     * used with A11Y Output Window.
     * @return list of names of unaccessible components
     */
    public List<String> checkTabTraversalState()  {    
        List<String> comps = new ArrayList<String>();
        Vector<MySavingButton> tmpButtons = (Vector<MySavingButton>) savedButtons.clone(); 
        for (MySavingButton mySavingButton : savedButtons) {
            if(mySavingButton.getNextName() != null) {
                tmpButtons.remove(mySavingButton);
                for(MySavingButton button : savedButtons){
                    if(button.getName().compareTo(mySavingButton.getNextName())==0)
                    tmpButtons.remove(button);
                }
            }
        }
        
        for(MySavingButton button : tmpButtons){            
            comps.add(button.getName());
        }
        
        return comps;
    }

    public void setSavedBtns(Vector<MySavingButton> savedButtons) {
        this.savedButtons = savedButtons;
    }

    @Override
    public Component getComponentAfter(Container aContainer, Component aComponent) {
        return null;
    }

    @Override
    public Component getComponentBefore(Container aContainer, Component aComponent) {
        return null;
    }

    @Override
    public Component getDefaultComponent(Container aContainer) {
        return null;
    }

    @Override
    public Component getFirstComponent(Container aContainer) {
        return null;
    }

    @Override
    public Component getLastComponent(Container aContainer) {
        return null;
    }

    @Override
    public String toString() {
        return "customFocusTraversalPolicy";
    }
}