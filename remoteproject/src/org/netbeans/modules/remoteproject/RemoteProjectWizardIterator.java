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
 * Portions Copyright 1997-2007 Sun Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.remoteproject;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.remoteproject.CheckoutHandler;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class RemoteProjectWizardIterator implements WizardDescriptor.ProgressInstantiatingIterator {

    public static final String PROP_URL = "url";
    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wiz;
    
    private RemoteProjectWizardIterator() {}
    
    public static RemoteProjectWizardIterator createIterator() {
        return new RemoteProjectWizardIterator();
    }
    
    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[] {
            new RemoteProjectWizardPanel(),
        };
    }
    
    private String[] createSteps() {
        return new String[] {
            NbBundle.getMessage(RemoteProjectWizardIterator.class, "LBL_CreateProjectStep")
        };
    }
    public Set <FileObject> instantiate() throws IOException {
        return instantiate(null);
    }
    
    public Set <FileObject> instantiate(ProgressHandle handle) throws IOException {
        Set <FileObject> resultSet = new LinkedHashSet <FileObject>();
        File dirF = FileUtil.normalizeFile((File) wiz.getProperty("projdir"));
        dirF.mkdirs();
        
        final FileObject template = Templates.getTemplate(wiz);
        final FileObject dir = FileUtil.toFileObject(dirF);
        
        Collection <? extends CheckoutHandler> handlers = 
                Lookup.getDefault().lookupAll (CheckoutHandler.class);
        CheckoutHandler handler = null;
        for (CheckoutHandler h : handlers) {
            if (h.canCheckout(template)) {
                handler = h;
                break;
            }
        }
        if (handler == null) {
            throw new IOException ("No handler for " + template.getPath());
        }
        String problem = handler.checkout(template, dir, handle);
        if (problem != null) {
            IOException ioe = new IOException (problem);
            Exceptions.attachLocalizedMessage(ioe, problem);
            Exceptions.printStackTrace(ioe);
            return Collections.<FileObject>emptySet();
        }
        if (dir != null) { //will be null in unit test, no masterfs
            // Always open top dir as a project:
            resultSet.add(dir);
            // Look for nested projects to open as well:
            Enumeration e = dir.getFolders(true);
            while (e.hasMoreElements()) {
                FileObject subfolder = (FileObject) e.nextElement();
                if (ProjectManager.getDefault().isProject(subfolder)) {
                    resultSet.add(subfolder);
                }
            }

            File parent = dirF.getParentFile();
            if (parent != null && parent.exists()) {
                ProjectChooser.setProjectsFolder(parent);
            }
        }
        return resultSet;
    }
    
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps);
            }
        }
    }
    
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir",null);
        this.wiz.putProperty("name",null);
        this.wiz = null;
        panels = null;
    }
    
    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[] {new Integer(index + 1), new Integer(panels.length)});
    }
    
    public boolean hasNext() {
        return index < panels.length - 1;
    }
    
    public boolean hasPrevious() {
        return index > 0;
    }
    
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }
    
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }
    
    public WizardDescriptor.Panel current() {
        return panels[index];
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {}
    public final void removeChangeListener(ChangeListener l) {}
}
