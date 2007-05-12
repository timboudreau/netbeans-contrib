package org.netbeans.modules.scala.project.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.scala.project.ScalaProjectGenerator;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

/**
 * @author Martin Krauskopf
 */
public final class NewScalaProjectWizardIterator implements WizardDescriptor.InstantiatingIterator {
    
    private NewScalaProjectWizardData data;
    
    private Panel[] panels;
    private int position;
    
    public Set<FileObject> instantiate() throws IOException {
        data.storeToPreferences();
        return testableInstantiate(data);
    }
    
    static Set<FileObject> testableInstantiate(final NewScalaProjectWizardData data) throws IOException {
        AntProjectHelper aph = ScalaProjectGenerator.createProject(
                data.getProjectFolderFile(), data.getProjectName());
        
        // XXX this constant should be defined somewhere!
        data.getSettings().putProperty("setAsMain", Boolean.valueOf(data.isSetAsMainProject())); // NOI18N
        
        Set<FileObject> toOpen = new HashSet<FileObject>();
        if (data.isCreateMainClass()) {
            FileObject classFO = createClass(aph, data.getMainClass());
            if (classFO != null) {
                toOpen.add(classFO);
            }
        }
        toOpen.add(aph.getProjectDirectory());
        return toOpen;
    }
    
    private static FileObject createClass(final AntProjectHelper aph, final String classFQN) throws IOException {
        FileObject result = null;
        if (classFQN.length() > 2) {
            FileObject srcDir = aph.getProjectDirectory().getFileObject("src");
            File classFilePath = new File(FileUtil.toFile(srcDir), classFQN.replace('.', '/'));
            FileObject pkgDir = FileUtil.createFolder(srcDir.getFileSystem().getRoot(), classFilePath.getParent());
            DataFolder pkgFolder = DataFolder.findFolder(pkgDir);
            FileObject templateFO = Repository.getDefault().getDefaultFileSystem().
                    findResource("Templates/Scala/Class.scala"); // NOI18N
            assert templateFO != null : "scala template was not found";
            DataObject templateDO = DataObject.find(templateFO);
            DataObject classDO = templateDO.createFromTemplate(pkgFolder, classFilePath.getName());
            result = classDO.getPrimaryFile();
        }
        return result;
    }
    
    public void initialize(final WizardDescriptor wd) {
        data = new NewScalaProjectWizardData(wd);
        panels = new WizardDescriptor.Panel[] {
            new BasicInfoWizardPanel(data),
        };
    }
    
    public void uninitialize(WizardDescriptor wd) {
        data = null;
        panels = null;
    }
    
    public String name() {
        // TemplateWizard internally does not use the value returned by this
        // method so we may return whatever (e.g. null) in the meantime. But it
        // would be resolved as "null" string by MessageFormat. So probably the
        // safest is to return empty string.
        return "";
    }
    
    public boolean hasNext() {
        return position < (panels.length - 1);
    }
    
    public boolean hasPrevious() {
        return position > 0;
    }
    
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        position++;
    }
    
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        position--;
    }
    
    public WizardDescriptor.Panel current() {
        return panels[position];
    }
    
    public void addChangeListener(final ChangeListener l) {
        // do not need now
    }
    
    public void removeChangeListener(final ChangeListener l) {
        // do not need now
    }
    
}
