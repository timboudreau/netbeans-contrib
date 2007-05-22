package com.sun.jbi.sapbc.sapwsdlgenerator.wizard;

import java.awt.Component;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * Wizard for the SAP BC WSDL generator.
 *
 * @author Noel Ang (noel.ang@sun.com)
 */
public final class bapiwizardWizardIterator
        implements WizardDescriptor.InstantiatingIterator {
    
    private int index;
    
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel[] panels;
    
    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                new bapiwizardWizardPanel1(),
                new bapiwizardWizardPanel2()
            };
            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.FALSE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }
    
    public Set instantiate() throws IOException {
        final Set<FileObject> instantiations = new HashSet<FileObject>();
        
        if (wizard != null) {
            // Find the physical location of the project files.
            // Find a suitable place to deposit the data to be instantiated.
            Project project = Templates.getProject(wizard);
            FileObject directory = Templates.getTargetFolder(wizard);
            if (directory == null) {
                if (project != null) {
                    directory = findSourceDirectory(project);
                }
            }
            
            if (directory != null) {
                // Instantiate the WSDL file
                // Hardcoded for demo
                FileObject file = directory.createData("ZFlight", "wsdl");
                FileLock lock = file.lock();
                OutputStream ostream = file.getOutputStream(lock);
                
                String content = NbBundle.getMessage(getClass(), "wsdl.for_demo_use_only");
                ostream.write(content.getBytes("UTF-8"));
                
                ostream.flush();
                ostream.close();
                lock.releaseLock();
                
                instantiations.add(file);
            }
        }
        
        return instantiations;
    }
    
    private FileObject findSourceDirectory(Project project) {
        
        // TODO: Redo this hack
        // Need to implement target selection in a wizard panel,
        // to give users a way to explicitly specify where generated
        // artifacts are deposited.
        
        FileObject projectDir = project.getProjectDirectory();
        Enumeration subFolders = projectDir.getFolders(false);
        FileObject target = null;
        
        final Set<String> sourceDirNames = new HashSet<String>(
                Arrays.asList(new String[] {
                    "src",
                    "source",
                    "sources",
                }
        ));
        
        while (subFolders.hasMoreElements()) {
            FileObject folder = (FileObject) subFolders.nextElement();
            if (sourceDirNames.contains(folder.getName())) {
                target = folder;
                break;
            }
        }
        
        if (target == null) {
            target = projectDir;
        }
        
        return target;
    }
    
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }
    
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }
    
    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }
    
    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }
    
    public boolean hasNext() {
        return index < getPanels().length - 1;
    }
    
    public boolean hasPrevious() {
        return index > 0;
    }
    
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        if (panels[index] instanceof VetoableStep) {
            VetoableStep transition = (VetoableStep) panels[index];
            if (transition.transpire()) {
                index++;
            }
        } else {
            index++;
        }
    }
    
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    public void addChangeListener(ChangeListener l) {}
    public void removeChangeListener(ChangeListener l) {}
    
    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] beforeSteps = null;
        Object prop = wizard.getProperty("WizardPanel_contentData");
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }
        
        if (beforeSteps == null) {
            beforeSteps = new String[0];
        }
        
        String[] res = new String[(beforeSteps.length - 1) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels[i - beforeSteps.length + 1].getComponent().getName();
            }
        }
        return res;
    }
}
