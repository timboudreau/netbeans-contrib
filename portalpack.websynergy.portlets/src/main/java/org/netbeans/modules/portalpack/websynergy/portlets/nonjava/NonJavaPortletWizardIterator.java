/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.portalpack.websynergy.portlets.nonjava;

//import org.netbeans.modules.portalpack.portlets.ruby.util.PortletProjectUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.websynergy.portlets.nonjava.api.NonJavaPortletBuilder;
import org.openide.WizardDescriptor;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

public abstract class NonJavaPortletWizardIterator implements TemplateWizard.Iterator/*WizardDescriptor.InstantiatingIterator*/ {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);

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
    public final Set<DataObject> instantiate(TemplateWizard wizard) throws IOException {
        FileObject dir = Templates.getTargetFolder(wizard);
        DataFolder df = DataFolder.findFolder(dir);
        ///FileObject template = Templates.getTemplate(wizard);
        Project project = Templates.getProject(wizard);
        //// DataObject dTemplate = DataObject.find(template);
        String targetName = Templates.getTargetName(wizard);
        String packageName = (String) wizard.getProperty("PACKAGE_NAME");//NOI18N

        Set result = Collections.EMPTY_SET;

        NonJavaPortletBuilder builder = (NonJavaPortletBuilder) wizard.getProperty(NonJavaPortletConstants.PORTLET_BUILDER);

        DataObject obj = null;
        if (targetName == null) {
            // Default name.
            //// obj = dTemplate.createFromTemplate(df);
        } else {
            Map<String, String> templateParameters = new HashMap<String, String>();



            if (builder == null) {
                return result;
            }
        /*  if (builder.getExtension().equals(template.getExt())) { // NOI18N
        
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
        
        }*/

        //obj = dTemplate.createFromTemplate(df, targetName, templateParameters);


        }

        //crate action.rb 
     /*   String actionRbName = "action";//obj.getName() + "_" + "action";
        FileObject actionTemplate = dir.getFileObject(actionRbName);
        if(actionTemplate == null)
        RubyTemplateUtil.createFileFromTemplate("Action.template", dir, 
        actionRbName, "rb");
        
        //Create global ruby files if required
        WebModule wm = PortletProjectUtils.getWebModule(project);
        String[] globalfiles = RubyPortletProjectUtil.createRubyFiles(wm,dir);
        
         */

        boolean isNewPortlet = (Boolean) wizard.getProperty("is_new_portlet");

        result = builder.handleCreate(wizard, isNewPortlet);

        /* if (result == Collections.EMPTY_SET) {
        result = Collections.singleton(obj);
        } else {
        result.add(obj);
        }*/

        /*      String rubyFolderRelativePath = FileUtil.getRelativePath(wm.getDocumentBase(), dir);
        if(rubyFolderRelativePath != null)
        {
        if(!rubyFolderRelativePath.startsWith("/") && !rubyFolderRelativePath.startsWith("\\"))
        rubyFolderRelativePath = "/" + rubyFolderRelativePath;
        if(!rubyFolderRelativePath.endsWith("/") && !rubyFolderRelativePath.endsWith("\\"))
        rubyFolderRelativePath += "/";
        rubyFolderRelativePath = rubyFolderRelativePath.replace("\\", "/");
        }
        
        PortletContext context = (PortletContext) wizard.getProperty("context");
        String viewPhp = targetName + "." + template.getExt();
        addRubyPortlet(project, packageName, "RubyPortlet", context, rubyFolderRelativePath + viewPhp,rubyFolderRelativePath + actionRbName + ".rb",globalfiles);
        // Open the new document
        OpenCookie open = (OpenCookie) obj.getCookie(OpenCookie.class);
        if (open != null) {
        open.open();
        }*/
        return result;

    }

    public final void initialize(TemplateWizard wizard) {
        index = 0;
        // obtaining target folder
        Project project = Templates.getProject(wizard);
        SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

        wizard.putProperty(NonJavaPortletConstants.PORTLET_BUILDER, getNonJavaPortletBuilder());

        //// List<NonJavaPortletBuilder> builders = NonJavaLayerXMLHelper.getNonJavaPortletBuilders();
        //WizardDescriptor.Panel packagePanel = new PagebeanPackagePanel(project);
        WizardDescriptor.Panel portletDtlPanel = new PortletDetailsPanel(wizard, new ArrayList(), project);
        WizardDescriptor.Panel javaPanel = new SimpleTargetChooserPanel(project, sourceGroups, portletDtlPanel, false);

        ///// WizardDescriptor.Panel typePanel = new PortletTypesPanel(builders);
        /// String templateType = Templates.getTemplate(wizard).getExt();
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
        FileObject jspDir = Templates.getTargetFolder(wizard);
        String relativePath = (jspDir == null) ? null : FileUtil.getRelativePath(docRoot, jspDir);
        if ((relativePath == null) || (relativePath.indexOf("WEB-INF") != -1)) {
            Templates.setTargetFolder(wizard, docRoot);
            jspDir = docRoot;
        }

    // Find a free page name
       /* String ext = Templates.getTemplate(wizard).getExt();
    String prefix = "php".equals(ext) ? "PhpPortlet" : "PhpPortlet"; // NOI18N
    
    for (int pageIndex = 1;; pageIndex++) {
    String name = prefix + pageIndex;
    if ((jspDir.getFileObject(name + "." + ext) == null) && ((javaDir == null) || (javaDir.getFileObject(name + ".java") == null))) { // NOI18N
    
    wizard.setTargetName(name);
    return;
    }
    }*/
    }

   public abstract NonJavaPortletBuilder getNonJavaPortletBuilder();

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
        return NbBundle.getMessage(NonJavaPortletWizardIterator.class, "TITLE_x_of_y", new Integer(index + 1), new Integer(panels.length));
    }

    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {
    }

    public final void removeChangeListener(ChangeListener l) {
    }

    /*private void addRubyPortlet(Project project, String packageName, String className, PortletContext pc, String viewPhp,String actionRb,String[] globalfiles) {
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
    
    if (!RubyPortletDDHelper.isRubyPortletEntryPresent(portletApp)) {
    //Create Ruby Portlet Class
    RubyPortletProjectUtil.createRubyPortletClass(webModule, packageName, className);
    PortletType portletType = portletApp.newPortletType();
    portletType.addDescription(pc.getPortletDescription());
    portletType.setPortletName(pc.getPortletName());
    portletType.addDisplayName(pc.getPortletDisplayName());
    
    if(packageName.endsWith("."))
    portletType.setPortletClass(packageName + className); //NOI18N
    else
    portletType.setPortletClass(packageName + "." + className); //NOI18N
    
    InitParamType initParam = portletType.newInitParamType();
    initParam.setDescription(new String[]{"Portlet Init View Page"});
    initParam.setName(VIEW_URI); //NOI18N
    
    initParam.setValue(viewPhp);
    
    portletType.addInitParam(initParam);
    
    
    InitParamType initParam1 = portletType.newInitParamType();
    initParam1.setDescription(new String[]{"Portlet Ruby Action Page"});
    initParam1.setName(ACTION_URI);
    initParam1.setValue(actionRb);
    
    portletType.addInitParam(initParam1);
    
    String gfiles = "";
    for(int i=0;i<globalfiles.length;i++)
    {
    gfiles += globalfiles[i];
    if(i != globalfiles.length -1)
    gfiles += ",";
    }
    
    if(gfiles.length() > 0)
    {
    InitParamType initParam2 = portletType.newInitParamType();
    initParam2.setDescription(new String[]{"Global ruby files"});
    initParam2.setName(GLOBAL_FILES);
    initParam2.setValue(gfiles);
    portletType.addInitParam(initParam2);
    }
    
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
    
    }*/
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

