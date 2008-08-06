/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.portalpack.servers.websynergy.portlets.php;

import org.netbeans.modules.portalpack.servers.websynergy.portlets.php.util.PortletProjectUtils;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.AppContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.listeners.PortletXMLChangeEventNotificationHelper;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.InitParamType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletInfoType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletXMLFactory;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.SupportsType;
import org.netbeans.modules.portalpack.servers.websynergy.common.LiferayConstants;
import org.netbeans.modules.portalpack.servers.websynergy.portlets.php.util.PortletDDHelper;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.WizardDescriptor;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public final class PhpPortletWizardIterator implements TemplateWizard.Iterator, PhpPortletConstants/*WizardDescriptor.InstantiatingIterator*/ {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private static Logger logger = Logger.getLogger(LiferayConstants.LR_LOGGER);

//    /**
//     * Initialize panels representing individual wizard's steps and sets
//     * various properties for them influencing wizard appearance.
//     */
//    private WizardDescriptor.Panel[] getPanels() {
//        
//        if (panels == null) {
//            panels = new WizardDescriptor.Panel[]{
//                        new PhpPortletWizardPanel()
//                    };
//            String[] steps = createSteps();
//            for (int i = 0; i < panels.length; i++) {
//                Component c = panels[i].getComponent();
//                if (steps[i] == null) {
//                    // Default step name to component name of panel. Mainly
//                    // useful for getting the name of the target chooser to
//                    // appear in the list of steps.
//                    steps[i] = c.getName();
//                }
//                if (c instanceof JComponent) { // assume Swing components
//
//                    JComponent jc = (JComponent) c;
//                    // Sets step number of a component
//                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
//                    // Sets steps names for a panel
//                    jc.putClientProperty("WizardPanel_contentData", steps);
//                    // Turn on subtitle creation on each step
//                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
//                    // Show steps on the left side with the image on the background
//                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
//                    // Turn on numbering of all steps
//                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
//                }
//            }
//        }
//        return panels;
//    }
    public Set<DataObject> instantiate(TemplateWizard wizard) throws IOException {
        FileObject dir = Templates.getTargetFolder(wizard);
        DataFolder df = DataFolder.findFolder(dir);
        FileObject template = Templates.getTemplate(wizard);
        Project project = Templates.getProject(wizard);
        DataObject dTemplate = DataObject.find(template);
        String targetName = Templates.getTargetName(wizard);
        Set result = Collections.EMPTY_SET;

        DataObject obj = null;
        if (targetName == null) {
            // Default name.
            obj = dTemplate.createFromTemplate(df);
        } else {
            Map<String, String> templateParameters = new HashMap<String, String>();
            templateParameters.put("MSG", "Hello Php Portlet"); //NOI18N

            //templateParameters.put("sourceLevel", JsfProjectUtils.getSourceLevel(project)); //NOI18N

            if ("php".equals(template.getExt())) { // NOI18N

                FileObject webDocbase = PortletProjectUtils.getDocumentRoot(project);
                String folder;
                if (dir == webDocbase) {
                    folder = "";
                } else {
                    folder = FileUtil.getRelativePath(webDocbase, dir);
                    if (folder == null) {
                        folder = "";
                    } else {
                        folder = folder.replace('/', '$') + "$";
                    }
                }
                templateParameters.put("folder", folder); //NOI18N

            }

            obj = dTemplate.createFromTemplate(df, targetName, templateParameters);
        }

        //check if index.php is present
        FileObject webDocbase = PortletProjectUtils.getDocumentRoot(project);
        FileObject indexPhpObj = webDocbase.getFileObject("index.php");
        if (indexPhpObj == null) {
            Map<String, String> templateParameters = new HashMap<String, String>();
            templateParameters.put("MSG", "Index Php Page. Do not delete this page." +
                    " If you delete this page then your portlet may not work."); //NOI18N

            DataObject indexObj = dTemplate.createFromTemplate(df, "index", templateParameters);
        }



        if (result == Collections.EMPTY_SET) {
            result = Collections.singleton(obj);
        } else {
            result.add(obj);
        }

        PortletContext context = (PortletContext) wizard.getProperty("context");
        String phpRelativePath = PortletProjectUtils.getRelativePathForPhpPortlet(webDocbase, dir);
        if(phpRelativePath == null) phpRelativePath = "/";
        String viewPhp = phpRelativePath + targetName + "." + template.getExt();
        addPhpPortletToPortletXML(project, context, viewPhp);
        // Open the new document
        OpenCookie open = (OpenCookie) obj.getCookie(OpenCookie.class);
        if (open != null) {
            open.open();
        }
        return result;

    }

    public void initialize(TemplateWizard wizard) {
        index = 0;
        // obtaining target folder
        Project project = Templates.getProject(wizard);
        SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

        //WizardDescriptor.Panel packagePanel = new PagebeanPackagePanel(project);
        WizardDescriptor.Panel portletDtlPanel = new PortletDetailsPanel(wizard, new ArrayList(), project);
        WizardDescriptor.Panel javaPanel = new SimpleTargetChooserPanel(project, sourceGroups, portletDtlPanel, false);
        String templateType = Templates.getTemplate(wizard).getExt();
        panels = new WizardDescriptor.Panel[]{javaPanel};

        // Creating steps.
        Object prop = wizard.getProperty("WizardPanel_contentData"); // NOI18N

        String[] beforeSteps = null;
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }
        String[] steps = createSteps(beforeSteps, panels);

        for (int i = 0; i < panels.length; i++) {
            JComponent jc = (JComponent) panels[i].getComponent();
            if (steps[i] == null) {
                steps[i] = jc.getName();
            }
            jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i)); // NOI18N

            jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N

        }

        // no support for non-web project
        if (!PortletProjectUtils.isWebProject(project)) {
            return;
        }

        // Always start with the document root or under
        FileObject docRoot = PortletProjectUtils.getDocumentRoot(project);
        FileObject javaDir = PortletProjectUtils.getPageBeanRoot(project);
        FileObject jspDir = Templates.getTargetFolder(wizard);
        String relativePath = (jspDir == null) ? null : FileUtil.getRelativePath(docRoot, jspDir);
        if ((relativePath == null) || (relativePath.indexOf("WEB-INF") != -1)) {
            Templates.setTargetFolder(wizard, docRoot);
            jspDir = docRoot;
        } else if (relativePath.length() > 0) {
            javaDir = javaDir.getFileObject(relativePath);
        }

        // Find a free page name
        String ext = Templates.getTemplate(wizard).getExt();
        String prefix = "php".equals(ext) ? "PhpPortlet" : "PhpPortlet"; // NOI18N

        for (int pageIndex = 1;; pageIndex++) {
            String name = prefix + pageIndex;
            if ((jspDir.getFileObject(name + "." + ext) == null) && ((javaDir == null) || (javaDir.getFileObject(name + ".java") == null))) { // NOI18N

                wizard.setTargetName(name);
                return;
            }
        }
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public boolean hasNext() {
        return index < panels.length - 1;
    }

    public String name() {
        return NbBundle.getMessage(PhpPortletWizardIterator.class, "TITLE_x_of_y", new Integer(index + 1), new Integer(panels.length));
    }

    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {
    }

    public final void removeChangeListener(ChangeListener l) {
    }

    private void addPhpPortletToPortletXML(Project project, PortletContext pc, String viewPhp) {
        WebModule webModule = PortletProjectUtils.getWebModule(project);
        // Create portlet.xml if not exist
        File filePortlet = new File(FileUtil.toFile(webModule.getWebInf()), "portlet.xml"); // NOI18N

        if (!filePortlet.exists()) {
            logger.log(Level.INFO, "No Portlet.xml found ");
            return;
        }

        //create messages.properties if doesn't exis
        FileObject sourceRoot = PortletProjectUtils.getSourceRoot(project);
        if (sourceRoot != null) {
            try {
                FileObject mObj = sourceRoot.getFileObject("messages.properties");
                if (mObj == null) {
                    FileObject data = sourceRoot.createData("messages", "properties");
                    if (data != null) {
                        System.out.println("messages.properties is created");
                    } else {
                        System.out.println("message.properties could not be created");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        PortletApp portletApp = NetbeansUtil.getPortletApp(filePortlet);
        if (portletApp == null) {
            logger.log(Level.WARNING, "Invalid Portlet XML");
            return;
        }

        if (!PortletDDHelper.isPhpPortletEntryPresent(portletApp)) {

            PortletType portletType = portletApp.newPortletType();
            portletType.addDescription(pc.getPortletDescription());
            portletType.setPortletName(pc.getPortletName());
            portletType.addDisplayName(pc.getPortletDisplayName());
            portletType.setPortletClass(PHP_PORTLET_CLASS); //NOI18N

            InitParamType initParam = portletType.newInitParamType();
            initParam.setDescription(new String[]{"Portlet Init View Page"});
            initParam.setName(VIEW_URI); //NOI18N

            initParam.setValue(viewPhp);

            portletType.addInitParam(initParam);
            
            InitParamType initParam1 = portletType.newInitParamType();
            initParam1.setDescription(new String[]{"Add Portlet Params"});
            initParam1.setName("add-portlet-params");
            initParam1.setValue("true");
            portletType.addInitParam(initParam1);
            
            portletType.setExpirationCache(0);

            SupportsType support = portletType.newSupportsType();
            support.setMimeType("text/html"); //NOI18N

            support.addPortletMode("VIEW");   //NOI18N

            portletType.addSupports(support);
            portletType.setSupportedLocale(new String[]{"en"}); //NOI18N

            PortletInfoType portletInfo = portletType.newPortletInfoType();
            portletInfo.setTitle(pc.getPortletTitle());
            portletInfo.setShortTitle(pc.getPortletShortTitle());

            portletType.setPortletInfo(portletInfo);
            portletType.setResourceBundle("messages");//NOI18N

            //add VisualJSFPortlet page as the first portlet entry in portlet.xml
            PortletType[] portletTypes = portletApp.getPortlet();
            if (portletTypes.length == 0) {
                portletApp.addPortlet(portletType);
            } else {

                PortletType firstPortlet = portletApp.getPortlet(0);
                portletApp.setPortlet(0, portletType);
                portletApp.addPortlet(firstPortlet);


            }

            NetbeansUtil.savePortletXML(portletApp, filePortlet);

            //fire add portlet event

            if (webModule.getWebInf() != null) {
                String webInfPath = FileUtil.toFile(webModule.getWebInf()).getAbsolutePath();
                PortletXMLChangeEventNotificationHelper.firePortletAddEvent(pc, new AppContext(), webInfPath);
            }
        }

    }

    private String[] createSteps(String[] before, WizardDescriptor.Panel[] panels) {
        int diff = 0;
        if (before == null) {
            before = new String[0];
        } else if (before.length > 0) {
            diff = ("...".equals(before[before.length - 1])) ? 1 : 0; // NOI18N

        }
        String[] res = new String[(before.length - diff) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (before.length - diff)) {
                res[i] = before[i];
            } else {
                res[i] = panels[i - before.length + diff].getComponent().getName();
            }
        }
        return res;
    }

    public void uninitialize(TemplateWizard wiz) {
        panels = null;
    }
}

