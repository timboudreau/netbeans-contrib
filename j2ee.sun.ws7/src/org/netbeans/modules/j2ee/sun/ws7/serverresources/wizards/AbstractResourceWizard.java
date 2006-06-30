/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
