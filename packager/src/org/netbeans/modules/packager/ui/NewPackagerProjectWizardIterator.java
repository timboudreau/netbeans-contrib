/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.packager.ui;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.packager.PackagerProject;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;


/**
 * Wizard to create a new J2SE project.
 * @author Jesse Glick
 */
public class NewPackagerProjectWizardIterator implements WizardDescriptor.InstantiatingIterator {

    static final int TYPE_APP = 0;
    static final int TYPE_LIB = 1;
    static final int TYPE_EXT = 2;

    private static final String MANIFEST_FILE = "manifest.mf";

    private static final long serialVersionUID = 1L;
    
    private int type;
    
    /** Create a new wizard iterator. */
    public NewPackagerProjectWizardIterator() {
        this(TYPE_APP);
    }
    
    public NewPackagerProjectWizardIterator(int type) {
        this.type = type;
    }
        
    public static NewPackagerProjectWizardIterator library() {
        return new NewPackagerProjectWizardIterator( TYPE_LIB );
    }
    
    public static NewPackagerProjectWizardIterator trada() {
        System.out.println("Trada");
        return new NewPackagerProjectWizardIterator();
    }

    public static NewPackagerProjectWizardIterator existing () {
        return new NewPackagerProjectWizardIterator( TYPE_EXT );
    }

    private WizardDescriptor.Panel[] createPanels () {
        return new WizardDescriptor.Panel[] {
                new CustomizerAsWizardPanel()
            };
    }
    
    private String[] createSteps() {
            return new String[] {
                NbBundle.getMessage(NewPackagerProjectWizardIterator.class,"LAB_ConfigureProject"), //NOI18N
            };
    }
    
    
    public Set/*<FileObject>*/ instantiate () throws IOException {
        Set resultSet = new HashSet ();
        
        File dirF = (File)wiz.getProperty(PackagerCustomizer.KEY_DIR);        //NOI18N
        String name = (String)wiz.getProperty(PackagerCustomizer.KEY_NAME);        //NOI18N
        Project[] projects = (Project[])wiz.getProperty(PackagerCustomizer.KEY_PROJECTS);        //NOI18N
        
        AntProjectHelper helper = PackagerProject.createProject (dirF, name, projects);
        
        FileObject dir = FileUtil.toFileObject(dirF);
        resultSet.add (dir);
        return resultSet;

    }
    
        
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor wiz;
    
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
                JComponent jc = (JComponent)c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i)); // NOI18N
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N
            }
        }
    }
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty(PackagerCustomizer.KEY_DIR,null);           //NOI18N
        this.wiz.putProperty(PackagerCustomizer.KEY_NAME,null);          //NOI18N
        this.wiz.putProperty(PackagerCustomizer.KEY_PROJECTS,null);       //NOI18N
        this.wiz = null;
        panels = null;
    }
    
    public String name() {
        return MessageFormat.format (NbBundle.getMessage(NewPackagerProjectWizardIterator.class,"LAB_IteratorName"),
            new Object[] {new Integer (index + 1), new Integer (panels.length) });                                
    }
    
    public boolean hasNext() {
        return index < panels.length - 1;
    }
    public boolean hasPrevious() {
        return index > 0;
    }
    public void nextPanel() {
        if (!hasNext()) throw new NoSuchElementException();
        index++;
    }
    public void previousPanel() {
        if (!hasPrevious()) throw new NoSuchElementException();
        index--;
    }
    public WizardDescriptor.Panel current () {
        return panels[index];
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {}
    public final void removeChangeListener(ChangeListener l) {}
    
    // helper methods, finds mainclass's FileObject
    private FileObject getMainClassFO (FileObject sourcesRoot, String mainClass) {
        // replace '.' with '/'
        mainClass = mainClass.replace ('.', '/'); // NOI18N
        
        // ignore unvalid mainClass ???
        
        return sourcesRoot.getFileObject (mainClass, "java"); // NOI18N
    }


}

