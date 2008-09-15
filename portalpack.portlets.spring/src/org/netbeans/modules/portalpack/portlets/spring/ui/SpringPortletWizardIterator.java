/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.portalpack.portlets.spring.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.netbeans.modules.portalpack.portlets.spring.api.ControllerType;
import org.netbeans.modules.portalpack.portlets.spring.api.ControllerTypeFactory;
import org.netbeans.modules.portalpack.portlets.spring.api.ControllerTypeHandler;
import org.netbeans.modules.portalpack.portlets.spring.util.JspBuilderUtil;
import org.netbeans.modules.portalpack.portlets.spring.util.SpringPortletConstants;
import org.netbeans.modules.portalpack.portlets.spring.util.SpringProjectHelper;
import org.netbeans.modules.portalpack.portlets.spring.util.TemplateUtil;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.openide.WizardDescriptor;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.TemplateWizard;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public class SpringPortletWizardIterator implements TemplateWizard.Iterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private static Logger logger = Logger.getLogger(SpringPortletConstants.LOGGER);

    public final Set<DataObject> instantiate(TemplateWizard wizard) throws IOException {
        FileObject dir = Templates.getTargetFolder(wizard);
        DataFolder df = DataFolder.findFolder(dir);
        ///FileObject template = Templates.getTemplate(wizard);
        Project project = Templates.getProject(wizard);
        //// DataObject dTemplate = DataObject.find(template);
        String targetName = Templates.getTargetName(wizard);
        String packageName = PortletProjectUtils.getPackage(dir);//(String) wizard.getProperty("PACKAGE_NAME");//NOI18N

        PortletContext pc = (PortletContext) wizard.getProperty("context");
        String configFolder = (String) wizard.getProperty("config-folder");
        String jspFolder = (String) wizard.getProperty("jsp-folder");
        String contextFile = (String) wizard.getProperty("context-file");
        ControllerType controllerType = (ControllerType) wizard.getProperty("controller-type");
        Set result = new HashSet();

        WebModule webModule = PortletProjectUtils.getWebModule(project);
        SpringProjectHelper util = new SpringProjectHelper();
        util.addSpringPortletConfig(webModule);

        ControllerTypeHandler handler = ControllerTypeFactory.getControllerTypeHandler(controllerType);
        //Create Spring Controller class
        TemplateUtil templateUtil = new TemplateUtil(SpringPortletConstants.TEMPLATE_FOLDER);
        Map values = new HashMap();
        try {
            ///   FileObject controllerTemplate = templateUtil.getTemplateFile("portletcontroller.java");

            values.put("pc", pc);
            values.put("PACKAGE", packageName);
            values.put("VIEW_PAGE", getJspName(pc.getViewJsp()));
            values.put("EDIT_PAGE", getJspName(pc.getEditJsp()));
            values.put("HELP_PAGE", getJspName(pc.getHelpJsp()));
            values.put("CONTROLLER_CLASS", targetName);


            handler.createControllerClass(dir, targetName, values, wizard,result);
        ///   templateUtil.mergeTemplateToFile(controllerTemplate, dir, targetName, values);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        //get portlet spec version
        FileObject webInf = webModule.getWebInf();
        if (webInf != null) {
            FileObject portletXmlObj = webInf.getFileObject("portlet", "xml");
            PortletXMLDataObject portletXmlDataObject = null;

            if (portletXmlObj != null) {
                try {
                    portletXmlDataObject = (PortletXMLDataObject) DataObject.find(portletXmlObj);
                } catch (DataObjectNotFoundException ex) {
                    logger.log(Level.SEVERE, "Portlet XML DataObject Not found.", ex);
                }
            }

            if (portletXmlDataObject != null) {
                pc.setPortletVersion(portletXmlDataObject.getPortletSpecVersion());
            } else {
                pc.setPortletVersion(NetbeanConstants.PORTLET_1_0);
            }
        }

        //Create JSPs
        Map jspValues = handler.getTemplateValues(pc, values, wizard);

        JspBuilderUtil.createJSPs(handler.getJSPTemplateName(), webModule.getWebInf(), pc, jspValues);  
        
        handler.createAdditionalJsps(webModule, pc, values, wizard,result);

        util.addSpringPortletContext(handler, webModule, packageName + "." + targetName, configFolder, contextFile, pc, values, wizard);
        util.addPortletToPortletXML(project, pc);

        return result;

    }

    public final void initialize(TemplateWizard wizard) {
        index = 0;
        // obtaining target folder
        Project project = Templates.getProject(wizard);
        SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

        //// List<NonJavaPortletBuilder> builders = NonJavaLayerXMLHelper.getNonJavaPortletBuilders();
        //WizardDescriptor.Panel packagePanel = new PagebeanPackagePanel(project);
        WizardDescriptor.Panel portletDtlPanel = new PortletDetailsPanel(wizard, new ArrayList(), project);
        WizardDescriptor.Panel javaPanel = new SimpleTargetChooserPanel(project, sourceGroups, portletDtlPanel, false);

        WizardDescriptor.Panel packageChooserPanel = JavaTemplates.createPackageChooser(project, sourceGroups, portletDtlPanel);
        SpringDetailsPanel springDtlPanel = new SpringDetailsPanel(project);
        ///// WizardDescriptor.Panel typePanel = new PortletTypesPanel(builders);
        /// String templateType = Templates.getTemplate(wizard).getExt();
        panels = new WizardDescriptor.Panel[]{packageChooserPanel, springDtlPanel, new ControllerDetailsPanel()};

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
     /*   FileObject docRoot = PortletProjectUtils.getDocumentRoot(project);
    FileObject jspDir = Templates.getTargetFolder(wizard);
    String relativePath = (jspDir == null) ? null : FileUtil.getRelativePath(docRoot, jspDir);
    if ((relativePath == null) || (relativePath.indexOf("WEB-INF") != -1)) {
    Templates.setTargetFolder(wizard, docRoot);
    jspDir = docRoot;
    }*/
    }

    private static String getJspName(String jspWithExt) {
        String jspName = null;
        int index = jspWithExt.lastIndexOf(".");
        if (index == -1) {
            jspName = jspWithExt;
        } else {
            jspName = jspWithExt.substring(0, index);
        }
        return jspName;
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
        return NbBundle.getMessage(SpringPortletWizardIterator.class, "TITLE_x_of_y", new Integer(index + 1), new Integer(panels.length));
    }

    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {
    }

    public final void removeChangeListener(ChangeListener l) {
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

