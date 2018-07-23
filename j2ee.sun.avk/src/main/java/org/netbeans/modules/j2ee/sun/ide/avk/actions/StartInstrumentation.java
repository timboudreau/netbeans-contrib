package org.netbeans.modules.j2ee.sun.ide.avk.actions;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.application.Application;
import org.netbeans.modules.j2ee.dd.api.application.Module;
import org.netbeans.modules.j2ee.dd.api.application.Web;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeAppProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.sun.api.SunDeploymentManagerInterface;
import org.netbeans.modules.j2ee.sun.ide.avk.report.model.ReportModel;
import org.netbeans.modules.j2ee.sun.ide.avk.report.view.ReportAction;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * @author bshankar@sun.com
 *
 */
public final class StartInstrumentation extends CommonNodeAction {
    
    private boolean isRunning = false;
    private static StartInstrumentation instance = null;
    
    public static synchronized StartInstrumentation getInstance() {
        if (instance == null) {
            instance = new StartInstrumentation();
        }
        return instance;
    }
    
    public String getName() {
        return NbBundle.getMessage(StartInstrumentation.class, "CTL_StartInstrumentation");
    }
    
    // Custom code starts here.
    
    public void performAction(Node[] nodes) {
        //start(nodes[0]);
    }
    
    public void start(final Project project) {
        
        new Thread() {
            
            public synchronized void run() {
                
                SunDeploymentManagerInterface sdm = getDeploymentManager(project);
                final ActionProvider ap = getActionProvider(project);
                final Lookup projectLookup = project.getLookup();
                AdminTasks adminTasks = new AdminTasks(sdm);
                
                /**
                 * If the AVK session is already running, then request the MBean to dump the previous sessions results.
                 * Else, do the verifier setup, run deploy and start instrumentation
                 */
                if(isRunning && sdm.isRunning()) { //
                    /**
                     * Step1. If AVK is already running then store the prev session results.
                     */
                    AdminTasks.debug("AVK is already running....storing the prev session resuls....");
                    //adminTasks.storeInstrumentationResults(curResultsDir, true);
                    adminTasks.generateReport(curResultsDir);
                    
                    /**
                     * Step2. Run project's 'deploy' target.
                     */
                    AdminTasks.debug("Running redeploy build target...");
                    
                    deployProject(project);
                    
                    AdminTasks.debug("Redeploy completed...");
                    
                    AdminTasks.debug("Deploy Dir = " + getDeployDir(project));
                    
                    if(getDeployDir(project) == null) {
                        showError("CTL_DeploymentFailed");
                        return;
                    }
                } else {
                    boolean isVerifierSetup = adminTasks.isSetup();
                    AdminTasks.debug("isVerifierSetup - " + isVerifierSetup);
                    /**
                     * Step1. If AVK is not setup in the target server then do the setup.
                     */
                    if(!isVerifierSetup) {
                        AdminTasks.debug("Doing AVK setup...copying jar...");
                        showProgress("CTL_PerformingSetup");
                        adminTasks.copyJar();
                        hideProgress();
                    }
                    /**
                     * Step2. Run project's 'deploy' target.
                     */
                    AdminTasks.debug("Running redeploy build target...");
                    
                    deployProject(project);
                    
                    AdminTasks.debug("Redeploy completed...");
                    
                    AdminTasks.debug("Deploy Dir = " + getDeployDir(project));
                    
                    if(getDeployDir(project) == null) {
                        showError("CTL_DeploymentFailed");
                        return;
                    }
                    
                    /**
                     * Step3. If AVK is not setup then create AVK MBean and Rule.
                     */
                    if(!isVerifierSetup) {
                        AdminTasks.debug("Doing AVK setup...creating AVK Mbean and Rule...");
                        adminTasks.createVerifierMBeanAndRule();
                        
                    }
                    /**
                     * Step4. Start Instrumentation.
                     */
                    AdminTasks.debug("Starting instrumentation...");
                    adminTasks.startInstrumentation();
                }
                
                /**
                 * Do introspection.
                 */
                String deployDir = getDeployDir(project);
                curResultsDir = getNewResultsDir(project);
                
                AdminTasks.debug("deployDir = " + deployDir);
                AdminTasks.debug("resultDir = " + curResultsDir);
                
                AdminTasks.debug("Doing introspection...");
                adminTasks.storeIntrospectionResults(deployDir, curResultsDir);
                
                /**
                 * Run the project.
                 */
                AdminTasks.debug("Running run build target...");
                runProject(project);
                
                //ap.invokeAction("run", projectLookup);
                
                startReportUI(project);
                
                isRunning = true;
            }
        }.start();
    }
    
    private String getNewResultsDir(Project project) {
        String domainDir = getDomainDir(project);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm");
        String newDirName = domainDir + File.separator +
                "logs" + File.separator +
                "reporttool" + File.separator +
                df.format(new Date());
        File newDir = new File(newDirName);
        newDir.mkdirs();
        return newDirName;
    }
    
    private void runProject(Project project) {
        try {
            Lookup projectLookup = project.getLookup();
            FileObject buildXML = project.getProjectDirectory().getFileObject(GeneratedFilesHelper.BUILD_XML_PATH);
            /**
             * Run the application in browser.
             */
            SunDeploymentManagerInterface sdm = getDeploymentManager(project);
            J2eeModuleProvider moduleProvider = (J2eeModuleProvider)projectLookup.lookup(J2eeModuleProvider.class);
            
            launchApp(sdm, getContextRoot(moduleProvider));
            
            /**
             * Run application client.
             */
            //ActionUtils.runTarget(buildXML, new String[]{"run-ac"}, new Properties());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String getContextRoot(J2eeModuleProvider modProvider){
        AdminTasks.debug("Modprovider = " + modProvider);
        String url = null;
        if(modProvider != null){
            if(modProvider instanceof J2eeAppProvider){
                //Get contextRoot of first web module in application.xml
                Application appXml = (Application)modProvider.getJ2eeModule().getDeploymentDescriptor(J2eeModule.APP_XML);
                AdminTasks.debug("appXml = "  + appXml);
                Module[] mods = appXml.getModule();
                AdminTasks.debug("mods.length = "  + mods.length);
                for(int i=0; i<mods.length; i++){
                    Web webMod = mods[i].getWeb();
                    AdminTasks.debug("webMod = "  + webMod);
                    if(webMod != null){
                        url = webMod.getContextRoot();
                        AdminTasks.debug("url = "  + url);
                        break;
                    }
                }
            }else{
                url = modProvider.getConfigSupport().getWebContextRoot();
            }
        }
        try {
            AdminTasks.debug("Config support url - " + modProvider.getConfigSupport().getWebContextRoot());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return url;
    }
    
    private void launchApp(SunDeploymentManagerInterface sdm, String contextRoot){
        AdminTasks.debug("LaunchApp : contextRoot = " + contextRoot);
        try {
            if(contextRoot != null){
                String httpport = sdm.getNonAdminPortNumber() == null ? "8080" : sdm.getNonAdminPortNumber();
                String start = "http://" + sdm.getHost() + ":" + httpport; //NOI18N
                if (contextRoot.startsWith("/")) //NOI18N
                    contextRoot = start + contextRoot;
                else
                    contextRoot = start + "/" + contextRoot; //NOI18N
                AdminTasks.debug("URL = " + contextRoot);
                URLDisplayer.getDefault().showURL(new URL(contextRoot));
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void startReportUI(Project project) {
        
        AdminTasks.debug("Starting UI with curResultDir = " + curResultsDir);
        
        final ReportAction reportAction = new ReportAction();
        SunDeploymentManagerInterface sdm = getDeploymentManager(project);
        final AdminTasks adminTasks = new AdminTasks(sdm);
        AdminTasks.debug("Current result dir = " + curResultsDir);
        adminTasks.startAutoWriteInstrumentationResults(curResultsDir);
        //adminTasks.storeInstrumentationResults(curResultsDir, false);
        adminTasks.generateReport(curResultsDir);
        
        final ReportModel avkReport = ReportModel.getInstance();
        avkReport.init(curResultsDir + File.separator + "results" + File.separator + "result.xml");
        reportAction.actionPerformed(null);
        
        final File monitoredFile = new File(curResultsDir + File.separator + "instrumentation" + File.separator + "result.xml");
        if(!monitoredFile.exists()) {
            return;
        }
        /**
         * Update the UI whenever a new instrumentation data is available.
         */
        new Thread() {
            public void run() {
                long lastModified = 0L;
                while(true) {
                    if(lastModified != monitoredFile.lastModified()) {
                        lastModified = monitoredFile.lastModified();
                        adminTasks.generateReport(curResultsDir);
                        if(reportAction.isUIClosed())
                            break;
                        AdminTasks.debug("File modified....New report generated...refreshing UI...");
                        avkReport.init(curResultsDir + File.separator + "results" + File.separator + "result.xml");
                        reportAction.repaint();
                        AdminTasks.debug("Refresing UI done...");
                        try {
                            sleep(10000);
                        } catch(Exception ex) {
                            AdminTasks.debug("File monitor thread can not sleep. " + ex.getMessage());
                        }
                    }
                }
            }
        }.start();
    }
}
