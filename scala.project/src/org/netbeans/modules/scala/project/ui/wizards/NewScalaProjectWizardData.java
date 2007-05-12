package org.netbeans.modules.scala.project.ui.wizards;

import java.io.File;
import java.text.MessageFormat;
import org.netbeans.modules.scala.project.Util;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 * @author Martin Krauskopf
 */
final class NewScalaProjectWizardData {
    
    private static final String PREF_PROJECT_LOCATION = "projectLocation";
    private static final String NEW_APPLICATION_COUNT = "newApplicationCount";
    
    private final WizardDescriptor settings;
    
    /** Actual project folder. */
    private String projectFolder;
    
    /** Parent of project folder. */
    private String projectLocation;
    
    private String projectName;
    private int baseCount;
    private boolean setAsMainProject;
    private boolean createMainClass;
    private String mainClass;
    
    NewScalaProjectWizardData(final WizardDescriptor settings) {
        this.settings = settings;
    }
    
    /** Actual project folder. */
    void setProjectFolder(final String projectFolder) {
        this.projectFolder = projectFolder;
    }
    
    /** Actual project folder. */
    String getProjectFolder() {
        return projectFolder;
    }
    
    File getProjectFolderFile() {
        return new File(getProjectFolder());
    }
    
    /** Parent of project folder. */
    void setProjectLocation(final String projectLocation) {
        this.projectLocation = projectLocation;
    }
    
    /** Parent of project folder. */
    String getProjectLocation() {
        if (projectLocation == null) {
            projectLocation = Util.getPreferences().get(PREF_PROJECT_LOCATION,
                    ProjectChooser.getProjectsFolder().getAbsolutePath());
        }
        return projectLocation;
    }
    
    void setProjectName(final String projectName) {
        this.projectName = projectName;
    }
    
    String getProjectName() {
        baseCount = Util.getPreferences().getInt(NEW_APPLICATION_COUNT, 0) + 1;
        String formatter = NbBundle.getMessage(NewScalaProjectWizardData.class, "TXT_ScalaApplication");
        File projectLocationF = new File(getProjectLocation());
        while ((projectName = validFreeProjectName(projectLocationF, formatter, baseCount)) == null) {
            baseCount++;
        }
        return projectName;
    }
    
    void setSetAsMainProject(final boolean setAsMainProject) {
        this.setAsMainProject = setAsMainProject;
    }
    
    boolean isSetAsMainProject() {
        return setAsMainProject;
    }
    
    void setCreateMainClass(final boolean createMainClass) {
        this.createMainClass = createMainClass;
    }
    
    public boolean isCreateMainClass() {
        return createMainClass;
    }
    
    void setMainClass(final String mainClass) {
        this.mainClass = mainClass;
    }
    
    public String getMainClass() {
        return mainClass;
    }
    
    WizardDescriptor getSettings() {
        return settings;
    }
    
    private static String validFreeProjectName(
            final File parentFolder, final String formater, final int index) {
        String name = MessageFormat.format(formater, index);
        File file = new File(parentFolder, name);
        return file.exists() ? null : name;
    }
    
    void storeToPreferences() {
        Util.getPreferences().put(PREF_PROJECT_LOCATION, projectLocation);
        Util.getPreferences().putInt(NEW_APPLICATION_COUNT, baseCount);
    }
    
}
