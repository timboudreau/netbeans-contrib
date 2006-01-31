/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
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
