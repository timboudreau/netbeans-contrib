package org.netbeans.modules.themebuilder.wizard;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author winstonp
 */
public final class ThemeBuilderProjectWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] wizardPanels;
    private WizardDescriptor wizard;

    /**
     *
     */
    public ThemeBuilderProjectWizardIterator() {
    }

    /**
     *
     * @return
     */
    public static ThemeBuilderProjectWizardIterator createIterator() {
        return new ThemeBuilderProjectWizardIterator();
    }

    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{new ThemeBuilderProjectWizardPanel()};
    }

    private String[] createSteps() {
        return new String[]{NbBundle.getMessage(ThemeBuilderProjectWizardIterator.class, "LBL_CreateProjectStep")};
    }

    /**
     *
     * @return
     * @throws java.io.IOException
     */
    public Set<FileObject> instantiate() throws IOException {

        File projectRootDir = FileUtil.normalizeFile((File) wizard.getProperty(ThemeBuilderProjectConstants.PROJECT_DIR));
        projectRootDir.mkdirs();

        String themeName = (String) wizard.getProperty(ThemeBuilderProjectConstants.THEME_NAME);
        String projectName = (String) wizard.getProperty(ThemeBuilderProjectConstants.PROJECT_NAME);
        String themePackage = (String) wizard.getProperty(ThemeBuilderProjectConstants.THEME_PACKAGE);

        // Initialize file template replacement parameters with project metadata
        Map<String, String> paramMap = new HashMap<String, String>();

        paramMap.put(ThemeBuilderProjectConstants.PROJECT_NAME, projectName);
        paramMap.put(ThemeBuilderProjectConstants.THEME, themeName);
        paramMap.put(ThemeBuilderProjectConstants.THEME_CSS, "_sun4");
        paramMap.put(ThemeBuilderProjectConstants.THEME_PATH, "/" + themePackage.replace('.', '/'));
        paramMap.put(ThemeBuilderProjectConstants.DATESTAMP, new Date().toString());
        paramMap.put(ThemeBuilderProjectConstants.FULL_VERSION, "DEV_4.1");
        paramMap.put(ThemeBuilderProjectConstants.VERSION, "DEV_4.1");
        paramMap.put(ThemeBuilderProjectConstants.THEME_PACKAGE, themePackage);
        paramMap.put(ThemeBuilderProjectConstants.THEME_SERVICE_CLASS, "ThemeServiceImpl");
        paramMap.put(ThemeBuilderProjectConstants.THEME_SERVICE_PROPERTIES, "ThemeService");

        FileObject template = Templates.getTemplate(wizard);
        unZipFile(template.getInputStream(), projectRootDir, paramMap);

        // Determine which project templates to unzip
        String themeDataPath;
        //Theme.Version themeVersionProperty = (Theme.Version) wizard.getProperty(ThemeBuilderProjectConstants.THEME_VERSION);
        themeDataPath = "org/netbeans/modules/themebuilder/resources/WoodstockThemeTemplate.zip";


        // Unpack project template into project directory
        ClassLoader classLoader = this.getClass().getClassLoader();

        URL themeDataUrl = classLoader.getResource(themeDataPath);

        unZipFile(themeDataUrl.openStream(), projectRootDir, paramMap);

        Set<FileObject> resultSet = new LinkedHashSet<FileObject>();
        FileObject dir = FileUtil.toFileObject(projectRootDir);
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

        File parent = projectRootDir.getParentFile();
        if (parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder(parent);
        }

        return resultSet;
    }

    /**
     *
     *
     * @param wizard
     */
    public void initialize(WizardDescriptor wiz) {
        this.wizard = wiz;
        index = 0;
        wizardPanels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < wizardPanels.length; i++) {
            Component c = wizardPanels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) {
                // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps);
            }
        }
    }

    /**
     *
     *
     * @param wizard
     */
    public void uninitialize(WizardDescriptor wiz) {
        this.wizard.putProperty(ThemeBuilderProjectConstants.PROJECT_DIR, null);
        this.wizard.putProperty(ThemeBuilderProjectConstants.PROJECT_NAME, null);
        this.wizard = null;
        wizardPanels = null;
    }

    /**
     *
     * @return
     */
    public String name() {
        return MessageFormat.format("{0} of {1}", new Object[]{new Integer(index + 1), new Integer(wizardPanels.length)});
    }

    /**
     *
     * @return
     */
    public boolean hasNext() {
        return index < wizardPanels.length - 1;
    }

    /**
     *
     * @return
     */
    public boolean hasPrevious() {
        return index > 0;
    }

    /**
     *
     */
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    /**
     *
     */
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    /**
     *
     * @return
     */
    public WizardDescriptor.Panel current() {
        return wizardPanels[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    /**
     *
     * @param l
     */
    public final void addChangeListener(ChangeListener l) {
    }

    /**
     *
     * @param l
     */
    public final void removeChangeListener(ChangeListener l) {
    }

    private static Pattern sourceFilePattern = Pattern.compile(".*\\.(css|java|js|properties|mf|xml|txt|ThemeService)$", Pattern.CASE_INSENSITIVE);

    private static void unZipFile(InputStream source, File projectRoot, Map<String, String> paramMap) throws IOException {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(source);
            BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    String srcRoot = "src";
                    if (entryName.startsWith(srcRoot) && paramMap != null) {
                        entryName = srcRoot + paramMap.get(ThemeBuilderProjectConstants.THEME_PATH) + entryName.replaceFirst(srcRoot, "");
                    }
                    File newFolder = new File(projectRoot, entryName);
                    newFolder.mkdirs();
                } else {
                    String srcRoot = "src";
                    if (entryName.startsWith(srcRoot) && paramMap != null) {
                        entryName = srcRoot + paramMap.get(ThemeBuilderProjectConstants.THEME_PATH) + entryName.replaceFirst(srcRoot, "");
                    }
                    File file = new File(projectRoot, entryName);
                    FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
                    FileLock lock = channel.lock();
                    OutputStream out = Channels.newOutputStream(channel);
                    String line;
                    StringBuffer sb = new StringBuffer();
                    try {
                        Set<String> keys = paramMap.keySet();
                        while ((line = reader.readLine()) != null) {
                            for (String key : keys) {
                                line = line.replaceAll("@" + key + "@", paramMap.get(key));
                            }
                            sb.append(line + "\n");
                        }
                        out.write(sb.toString().getBytes());
                    } finally {
                        lock.release();
                        out.close();
                    }
                }
            }
        } finally {
            source.close();
        }
    }
}
