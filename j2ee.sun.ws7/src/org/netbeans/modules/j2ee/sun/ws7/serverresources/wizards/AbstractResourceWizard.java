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

/*
 * AbstractResourceWizard.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards;

import java.awt.Component;
import java.util.Set;
import javax.swing.JComponent;
import java.io.InputStream;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.util.NbBundle;
import org.openide.WizardDescriptor;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;

import org.netbeans.modules.j2ee.sun.sunresources.beans.Wizard;

/**
 *
 * @author Mukesh Garg
 */
public abstract class AbstractResourceWizard implements WizardDescriptor.InstantiatingIterator{
   
    transient WizardDescriptor.Panel[] panels;
    transient int index;
    /** Creates a new instance of AbstractResourceWizard */
    public AbstractResourceWizard() {
    }
    public abstract void initialize(WizardDescriptor wizard);
        
    public abstract Set instantiate();
    public abstract void uninitialize(WizardDescriptor wizard);

        
     public boolean hasNext(){
        return index < panels.length - 1;
    }
    
    public boolean hasPrevious(){
        return index > 0;
    }
    
    public synchronized void nextPanel(){
        if (index + 1 == panels.length)
            throw new java.util.NoSuchElementException();    
 
        index ++;
    }
    
    public synchronized void previousPanel(){
        if (index == 0)
            throw new java.util.NoSuchElementException();
        
        index--;
    }
    
    public WizardDescriptor.Panel current(){
        return (WizardDescriptor.Panel)panels[index];
    }
    public void addChangeListener(ChangeListener l){
        
    }
    public void removeChangeListener(ChangeListener l){
        
    }
    
    Wizard getWizardInfo(String dataFile){
        Wizard wd = null;
        try{
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(dataFile);
            wd = Wizard.createGraph(in);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return wd;
    }    
}
