package rmitests;
/*
 * RMIExecutor.java
 *
 * Created on December 5, 2001, 1:45 PM
 */

import junit.framework.*;
import org.netbeans.junit.*;
import support.*;
import org.openide.*;
import org.openide.nodes.*;
import org.netbeans.core.*;
import org.netbeans.modules.group.*;
import org.netbeans.modules.rmi.*;
import org.netbeans.modules.rmi.registry.*;
import org.netbeans.modules.rmi.settings.*;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.java.JavaDataObject;
import org.openide.loaders.*;
import org.openide.execution.*;
import org.openide.compiler.*;
import support.RMIRegistrySupport;
import support.PropertySupport;
import java.rmi.registry.*;
import java.rmi.*;
import java.io.*;
/**
 *
 * @author tb115823
 */
public class RMIApiTests extends NbTestCase {
    
    private static final String workpackage = "data/work"; // NOI18N
    private static final String newpackage = "New"; // NOI18N
    public static final java.util.ResourceBundle bundle=java.util.ResourceBundle.getBundle("data/RMITests");
    
    
    private String helloImpl = workpackage + "/HelloWorldImpl.java"; // NOI18N
    private String helloActiv = workpackage + "/HelloWorldActiv.java"; // NOI18N
    private String helloClient = workpackage + "/HelloClient.java"; // NOI18N
    private String hello = workpackage + "/HelloWorld.java"; // NOI18N

    private Support sup;
    
    public RMIApiTests(java.lang.String testName) {
        super(testName);
    }        
        
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    } 
    
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new NbTestSuite();
        suite.addTest(new RMIApiTests("RunLocalRegistry"));
        suite.addTest(new RMIApiTests("testRMIExecutor"));
        suite.addTest(new RMIApiTests("testClientExecutor_RMIExecute"));
        suite.addTest(new RMIApiTests("testRMICompileTest"));
        suite.addTest(new RMIApiTests("testRMIDataLoader"));
        suite.addTest(new RMIApiTests("testRMIModuleEnable"));
        suite.addTest(new RMIApiTests("testRMIHideStubs"));
        suite.addTest(new RMIApiTests("testRMIProjectSettings"));
        suite.addTest(new RMIApiTests("testRMIProjectSettings"));
        suite.addTest(new RMIApiTests("testRMIRegistry"));
        suite.addTest(new RMIApiTests("testRMITemplates"));
        suite.addTest(new RMIApiTests("testRMIUnicastExportNoURLandPort"));
        suite.addTest(new RMIApiTests("testClientExecutor_RMIUnicastExport"));
        suite.addTest(new RMIApiTests("testRMIUnicastExportWithURLandPort"));
        return suite;
    }            
    
    protected void setUp() {
        sup = new Support(null,null);
    }
    
    
    
    
    public void RunLocalRegistry() {
        try {
            //adding local registry item
            RegistryItem regitem = RMIRegistrySupport.addLocalRegistryItem();
            RMIRegistrySupport.setInternalRegistryPort();
            try { Thread.currentThread().sleep(5000);}
            catch(InterruptedException e) {}
            
            //runLocalRegistry();
            
        } catch(Exception ex) {
            System.out.println("unexpected excepton");
            log(ex.getMessage());
            fail(ex.getMessage());
        }    
    }
    
    
    public void testClientExecutor_RMIUnicastExport() {
        clientExecute(workpackage.replace('/','.')+"."+"HelloWorldImpl.java");
    }
    
    public void testClientExecutor_RMIExecute() {
        clientExecute("rmitests.RMIExecutorTest");
    }
    
    
    private void clientExecute(String URL) {
        DataObject obj;
        String source = workpackage + "/HelloClient.java"; // NOI18N
  
        try {
            System.setProperty("netbeans.debug.exceptions","true"); // NOI18N
            
            //Getting local registry 
            log(bundle.getString("Getting_local_registry_item"));
            RegistryItem regitem = RMIRegistrySupport.getLocalRegistryItem();
            
            // Update registry
            log(bundle.getString("Updating_all_registry_items"));
            RMIRegistrySupport.updateAllRegistryItems();
            // Find HelloWorldImpl in registry
            log(bundle.getString("Finding_service_")+URL+bundle.getString("_in_registry"));
            if (RMIRegistrySupport.getService(regitem,URL) == null)
                log(bundle.getString("Service_")+URL+bundle.getString("_not_found_in_registry_before_execution.\nFound_sevices_\n")+RMIRegistrySupport.getAllServiceNames());
            else
                log(bundle.getString("Service_")+URL+bundle.getString("_found_in_registry_before_execution."));

            //Execute HelloWorldImpl
            obj=sup.getDataObject(source);
            sup.setExecutor(obj,sup.RMI_CLIENT_EXECUTOR);

            String logfile= "client1.log"; // NOI18N

            sup.execute(obj,URL+" "+logfile); // NOI18N
            log(obj.getName()+bundle.getString("_executed"));

            // Wait
            try { Thread.currentThread().sleep(5000); }
            catch(InterruptedException e) { e.printStackTrace(getLog());}
            
            
            int i=0;
            do {
                i++;
                // Wait
                try { Thread.currentThread().sleep(5000); }
                catch(InterruptedException e) { log(bundle.getString("Interrupted_during_sleeping"));}

                // Update registry
                log(bundle.getString("Updating_all_registry_items"));
                RMIRegistrySupport.updateAllRegistryItems();
                // Find HelloWorldImpl in registry
                log(bundle.getString("Finding_service_")+URL+bundle.getString("_in_registry_after_execution."));
            } while ((RMIRegistrySupport.getService(regitem,URL) != null)&&(i<20));
            
            if (RMIRegistrySupport.getService(regitem,URL) == null)
                log(bundle.getString("Service_")+URL+bundle.getString("_not_found_in_registry_after_execution."));
            else
                log(bundle.getString("Service_")+URL+bundle.getString("_found_in_registry_after_execution."));

            log(bundle.getString("Finished."));
        } catch (Exception e) {
            e.printStackTrace(getLog());
            fail();
        }
        
    }
    
    
    
    
    public void testRMIUnicastExportNoURLandPort() {
        String URL = workpackage.replace('/','.')+".HelloWorldImpl"; // NOI18N
        UnicastExport(URL,0);
    }
    
    
    public void testRMIUnicastExportWithURLandPort() {
        String URL = workpackage.replace('/','.')+".HelloWorldImpl"; // NOI18N
        UnicastExport("UnicastExport2",7777);
    }
    
    
    private void UnicastExport(String URL, int port) {
    
        DataObject obj;
        String source = workpackage + "/HelloWorldImpl.java"; // NOI18N

        try {
            System.setProperty("netbeans.debug.exceptions","true"); // NOI18N
            
            //Getting local registry 
            log(bundle.getString("Getting_local_registry_item"));
            RegistryItem regitem = RMIRegistrySupport.getLocalRegistryItem();
            
            // Update registry
            log(bundle.getString("Updating_all_registry_items"));
            RMIRegistrySupport.updateAllRegistryItems();
            // Find HelloWorldImpl in registry
            log(bundle.getString("Finding_service_")+URL+bundle.getString("_in_registry"));
            if (RMIRegistrySupport.getService(regitem,URL) == null)
                log(bundle.getString("Service_")+URL+bundle.getString("_not_found_in_registry_before_execution."));
            else
                log(bundle.getString("Service_")+URL+bundle.getString("_found_in_registry_before_execution."));

            // Set another port and Service URL
            obj=sup.getDataObject(source);
            try {
                PropertySupport.setPropertyValue(bundle.getString("RMI_Export/Service_URL"),URL,obj.getNodeDelegate()); // NOI18N
                PropertySupport.setPropertyValue(bundle.getString("RMI_Export/Port"),new Integer(port),obj.getNodeDelegate());
            }
            catch (Exception e) { e.printStackTrace(getLog());
            }

            //Execute HelloWorldImpl
            sup.setExecutor(obj,sup.RMI_UNICAST_EXPORT);
            sup.execute(obj,""); // NOI18N
            log(obj.getName()+bundle.getString("_executed"));

            int i=0;
            do {
                i++;
                // Wait
                try { Thread.currentThread().sleep(5000); }
                catch(InterruptedException e) { sup.exceptionlog(bundle.getString("Interrupted_during_sleeping"), e);}

                // Update registry
                log(bundle.getString("Updating_all_registry_items"));
                RMIRegistrySupport.updateAllRegistryItems();
                // Find HelloWorldImpl in registry
                log(bundle.getString("Finding_service_")+URL+bundle.getString("_in_registry_after_execution."));
            } while ((RMIRegistrySupport.getService(regitem,URL) == null)&&(i<20));

            if (RMIRegistrySupport.getService(regitem,URL) == null)
                log(bundle.getString("Service_")+URL+bundle.getString("_not_found_in_registry_after_execution.\nFound_sevices_\n")+RMIRegistrySupport.getAllServiceNames());
            else
                log(bundle.getString("Service_")+URL+bundle.getString("_found"));

            log(bundle.getString("Finished."));
        } catch (Exception e) {
            e.printStackTrace(getLog());
            fail();
        }
        
    }
    
    
    
    public void testRMITemplates() {
    
        DataFolder newwork=null;
        String folder;
        String template;
        DataObject obj;

        try {
            System.setProperty("netbeans.debug.exceptions","true"); // NOI18N
            try {
                //deleting target directory if exists
                if (sup.getFileObject(workpackage+"/"+newpackage)!=null) { // NOI18N
                    sup.getDataObject(workpackage+"/"+newpackage).delete(); // NOI18N
                    Thread.currentThread().sleep(10000);
                }
            } catch (IOException e) {}; //nic nedelat

            try {
                //creating target directory
                newwork=DataFolder.create((DataFolder)sup.getDataObject(workpackage),newpackage);
                Thread.currentThread().sleep(5000);
            } catch (IOException e) {
                log(bundle.getString("Cannot_create_folder_")+workpackage+"/"+newpackage); // NOI18N
            }

                folder=bundle.getString("RMI.1");
                template=bundle.getString("ActivatableServer");
                log(bundle.getString("Creating_")+folder+"/"+template); // NOI18N
                try {
                    NewFromTemplate(folder,template,newwork,false);
                } catch (IOException e) {
                    log(bundle.getString("Cannot_create_NewFromTemplate_")+folder+"/"+template); // NOI18N
                    fail();
                }

                template=bundle.getString("ClearObject");
                log(bundle.getString("Creating_")+folder+"/"+template); // NOI18N
                try {
                    NewFromTemplate(folder,template,newwork,false);
                } catch (IOException e) {
                    log(bundle.getString("Cannot_create_NewFromTemplate_")+folder+"/"+template); // NOI18N
                    fail();
                }

                template=bundle.getString("IIOPServer");
                log(bundle.getString("Creating_")+folder+"/"+template); // NOI18N
                try {
                    NewFromTemplate(folder,template,newwork,false);
                } catch (IOException e) {
                    log(bundle.getString("Cannot_create_NewFromTemplate_")+folder+"/"+template); // NOI18N
                    fail();
                }

                template=bundle.getString("UnicastRemoteServer");
                log(bundle.getString("Creating_")+folder+"/"+template); // NOI18N
                try {
                    NewFromTemplate(folder,template,newwork,false);
                } catch (IOException e) {
                    log(bundle.getString("Cannot_create_NewFromTemplate_")+folder+"/"+template); // NOI18N
                    fail();
                }

                template=bundle.getString("RMI__Client");
                log(bundle.getString("Creating_")+folder+"/"+template); // NOI18N
                try {
                    NewFromTemplate(folder,template,newwork,false);
                } catch (IOException e) {
                    log(bundle.getString("Cannot_create_NewFromTemplate_")+folder+"/"+template); // NOI18N
                    fail();
                }

            folder=bundle.getString("RMISockets");
                template=bundle.getString("ClientSocket");
                log(bundle.getString("Creating_")+folder+"/"+template); // NOI18N
                try {
                    NewFromTemplate(folder,template,newwork,true);
                } catch (IOException e) {
                    log(bundle.getString("Cannot_create_NewFromTemplate_")+folder+"/"+template); // NOI18N
                    fail();
                }

                template=bundle.getString("CustomSockets");
                log(bundle.getString("Creating_")+folder+"/"+template); // NOI18N
                try {
                    NewFromTemplate(folder,template,newwork,true);
                } catch (IOException e) {
                    log(bundle.getString("Cannot_create_NewFromTemplate_")+folder+"/"+template); // NOI18N
                    fail();
                }

                template=bundle.getString("ServerSocket");
                log(bundle.getString("Creating_")+folder+"/"+template); // NOI18N
                try {
                    NewFromTemplate(folder,template,newwork,true);
                } catch (IOException e) {
                    log(bundle.getString("Cannot_create_NewFromTemplate_")+folder+"/"+template); // NOI18N
                    fail();
                }

            int i=0;
            while ((i<10)&&(sup.getFileObject(workpackage+"/"+newpackage+bundle.getString("/ServerSocketSocket.java"))==null)) { // NOI18N
                log(bundle.getString("Waiting_for_last_object_created."));
                Thread.currentThread().sleep(5000);
                i++;
            }

            try {
                //trying to compile all new objects
                obj=sup.getDataObject(workpackage+"/"+newpackage+bundle.getString("/ActivatableServerImpl.java")); // NOI18N
                sup.compile(obj);
                obj=sup.getDataObject(workpackage+"/"+newpackage+bundle.getString("/ClearObjectImpl.java")); // NOI18N
                sup.compile(obj);
                obj=sup.getDataObject(workpackage+"/"+newpackage+bundle.getString("/IIOPServerImpl.java")); // NOI18N
                sup.compile(obj);
                obj=sup.getDataObject(workpackage+"/"+newpackage+bundle.getString("/UnicastRemoteServerImpl.java")); // NOI18N
                sup.compile(obj);
                obj=sup.getDataObject(workpackage+"/"+newpackage+bundle.getString("/RMI__Client.java")); // NOI18N
                sup.compile(obj);
                obj=sup.getDataObject(workpackage+"/"+newpackage+bundle.getString("/ClientSocketClientSocketFactory.java")); // NOI18N
                sup.compile(obj);
                obj=sup.getDataObject(workpackage+"/"+newpackage+bundle.getString("/CustomSocketsServerSocketFactory.java")); // NOI18N
                sup.compile(obj);
                obj=sup.getDataObject(workpackage+"/"+newpackage+bundle.getString("/ServerSocketServerSocketFactory.java")); // NOI18N
                sup.compile(obj);
                log(bundle.getString("Finished."));
            }catch(Exception e) {
                e.printStackTrace(getLog());
                fail(); 
            }   
            
        } catch (Exception e) {
            e.printStackTrace(getLog());
            fail();
        }
        
    }
    
    /** createn new from template
 * @param templateFolder String
 * @param template String
 * @param directory DataFolder
 * @throws Exception exception during new from template
 * @return new DataObject
 */    
    private DataObject NewFromTemplate(String templateFolder,String template,DataFolder directory, boolean group) throws Exception {
        DataObject[] dobjects=TopManager.getDefault().getPlaces().folders().templates().getChildren();
        int i;
        //looking for template folder
        java.util.StringTokenizer st=new java.util.StringTokenizer(templateFolder,"/");
        while (st.hasMoreTokens()) {
            i=0;
            templateFolder=st.nextToken();
            try {
                while (!templateFolder.equals(dobjects[i].getName())) {
                    i++;
                }
            } catch (Exception e) {
                log(bundle.getString("Folder_not_found_")+templateFolder);
            }
            dobjects=((DataFolder)dobjects[i]).getChildren();
        }
        i=0;
        //looking for template
        try {
            while ((!template.equals(dobjects[i].getName()))  ||((dobjects[i] instanceof org.netbeans.modules.group.GroupShadow)!=group)) {
                i++;
            }
        } catch (Exception e) {
            log(bundle.getString("Template_not_found_")+template);
        }
        if (!dobjects[i].isTemplate()) {
            log(template+bundle.getString("_is_not_template."));
        }
        //creating new from template
        return dobjects[i].createFromTemplate(directory,template);
    }
    
    
    
    
    
    
    
    public void testRMIRegistry() {
        try {
            System.setProperty("netbeans.debug.exceptions","true"); // NOI18N
            
            // Stop registry and remove all items from registry pool
            log(bundle.getString("Stoping_internal_registry"));
            RMIRegistrySupport.stopInternalRegistry();

            log(bundle.getString("Removing_all_registry_items"));
            RMIRegistrySupport.removeAllRegistryItems();
            
            try { Thread.currentThread().sleep(10000);}
            catch(InterruptedException e) {}
            
            try {
                //trying to locate some other registry
                Registry reg=LocateRegistry.getRegistry("localhost",1099); // NOI18N
                String[] s=reg.list();
                log(bundle.getString("External_registry_running_on_localhost_1099_with_services_"));
                for(int i=0;i<s.length;i++) log(s[i]);
            } catch (RemoteException e) {}

            //starting internal registry
            log(bundle.getString("Starting_internal_registry"));
            RMIRegistrySupport.setInternalRegistryPort();
            
            try { Thread.currentThread().sleep(10000);}
            catch(InterruptedException e) {}
            
            //adding local registry item
            log(bundle.getString("Adding_local_registry_item"));
            RegistryItem regitem = RMIRegistrySupport.addLocalRegistryItem();
            
            //updating all registry items
            log(bundle.getString("Updating_all_registry_items"));
            RMIRegistrySupport.updateAllRegistryItems();
            
            log(bundle.getString("Registry_status_\n")+RMIRegistrySupport.getAllServiceNames());

            log(bundle.getString("Finished."));
        } catch (Exception e) {
            log(bundle.getString("Unexpected_exception"));
            fail();
        }
    }
    
    
    
    public void testRMIProjectSettings() {
        
        String compilers[]={"RMIStubCompiler","RMIStubCompilerIIOP"};
        String name;
        int i;
        Node node;
        try {
            for (int c=0;c<2;c++) {
                name=compilers[c];
                System.setProperty("netbeans.debug.exceptions","true"); // NOI18N
                //looking for RMI Stub Compiler node
                node=null;
                i=0;
                while ((i++<20)&&(node==null)) {
                    try {
                        node=TopManager.getDefault().getPlaces().nodes().session();
                        node=sup.findChild(node,"Building");
                        node=sup.findChild(node,"CompilerType");
                        node=sup.findChild(node,name);
                        node.isLeaf();
                    } catch (NullPointerException npe) {
                        sup.sleep(1000);
                    }
                }
                if (node==null) {
                    log(name+bundle.getString("_not_found."));
                } else {
                    name=node.getDisplayName()+": ";
                    Node.PropertySet[] pset=node.getPropertySets();
                    i=0;
                    while ((i++<20)&&(pset.length<2)) {
                        sup.sleep(1000);
                        pset=node.getPropertySets();
                    }
                    Node.Property[] prop;
                    String propname;
                    //listing some properties
                    for (i=0;i<pset.length;i++) {
                        prop=pset[i].getProperties();
                        try {
                            for (int j=0;j<prop.length;j++) {
                                propname=prop[j].getName();
                                if (propname.equals(bundle.getString("externalCompiler"))) {
                                    log(name+bundle.getString("_process__")+((NbProcessDescriptor)(prop[j].getValue())).getProcessName());
                                    log(name+bundle.getString("_arguments__")+((NbProcessDescriptor)(prop[j].getValue())).getArguments());
                                } else if (propname.equals(bundle.getString("errorExpression"))) {
                                    log(name+bundle.getString("_error_expression__")+((ExternalCompiler.ErrorExpression)(prop[j].getValue())).getName());
                                } else if (propname.equals("keepGenerated")) {
                                    log(name+" keep generated: "+prop[j].getValue());
                                } else if (propname.equals("targetPolicy")) {
                                    log(name+" target policy: "+prop[j].getValue());
                                } else if (propname.equals("version")) {
                                    log(name+" version: "+prop[j].getValue());
                                } else if (propname.equals("iiop")) {
                                    log(name+" iiop: "+prop[j].getValue());
                                } else if (propname.equals("targetFileSystem")) {
                                    log(name+" target file system: "+prop[j].getValue());
                                }
                            }
                        } catch (Exception e) {
                            log(bundle.getString("Property_exception"));
                        }
                    }
                }
            }
            name="RMIDebugger";
            //looking for RMI Debugging node
            i=0;
            node=null;
            while ((i++<20)&&(node==null)) {
                try {
                        node=TopManager.getDefault().getPlaces().nodes().session();
                        node=sup.findChild(node,"DebuggingAndExecuting");
                        node=sup.findChild(node,"DebuggerType");
                        node=sup.findChild(node,name);
                        node.isLeaf();
                } catch (NullPointerException npe) {
                    sup.sleep(1000);
                }
            }
            if (node==null) {
                log(name+bundle.getString("_not_found."));
            } else {
                name=node.getDisplayName()+": ";
                Node.PropertySet[] pset=node.getPropertySets();
                i=0;
                while ((i++<20)&&(pset.length<2)) {
                    sup.sleep(1000);
                    pset=node.getPropertySets();
                }
                Node.Property[] prop;
                String propname;
                //listing some properties
                for (i=0;i<pset.length;i++) {
                    prop=pset[i].getProperties();
                    try {
                        for (int j=0;j<prop.length;j++) {
                            propname=prop[j].getName();
                            if (propname.equals(bundle.getString("debuggerProcess"))) {
                                log(name+bundle.getString("_process__")+((NbProcessDescriptor)(prop[j].getValue())).getProcessName());
                                log(name+bundle.getString("_arguments__")+((NbProcessDescriptor)(prop[j].getValue())).getArguments());
                            } else if (propname.equals(bundle.getString("debuggerType"))) {
                                log(name+bundle.getString("_debugger_type__")+(prop[j].getValue()).toString());
                            } else if (propname.equals("classic")) {
                                log(name+" classic: "+prop[j].getValue());
                            }
                        }
                    } catch (Exception e) {
                        log(bundle.getString("Property_exception"));
                    }
                }
            }
            String ExecutorNames[] ={"RMIExecutor","RMIClientExecutor","RMIUnicastExport","RMIExecutorDbg","RMIClientExecutorDbg","RMIExportDbg"};
            //looking for some executors nodes
            for (int k=0;k<ExecutorNames.length;k++) {
                name=ExecutorNames[k];
                //getting executor node
                i=0;
                node=null;
                while ((i++<20)&&(node==null)) {
                    try {
                        node=TopManager.getDefault().getPlaces().nodes().session();
                        node=sup.findChild(node,"DebuggingAndExecuting");
                        node=sup.findChild(node,"Executor");
                        node=sup.findChild(node,name);
                        node.isLeaf();
                    } catch (NullPointerException npe) {
                        sup.sleep(1000);
                    }
                }
                if (node==null) {
                    log(name+bundle.getString("_not_found."));
                } else {
                    name=node.getDisplayName()+": ";
                    Node.PropertySet[] pset=node.getPropertySets();
                    i=0;
                    while ((i++<20)&&(pset.length<2)) {
                        sup.sleep(1000);
                        pset=node.getPropertySets();
                    }
                    Node.Property[] prop;
                    String propname;
                    //listing some properties
                    for (i=0;i<pset.length;i++) {
                        prop=pset[i].getProperties();
                        try {
                            for (int j=0;j<prop.length;j++) {
                                propname=prop[j].getName();
                                if (propname.equals(bundle.getString("externalExecutor"))) {
                                    log(name+bundle.getString("_process__")+((NbProcessDescriptor)(prop[j].getValue())).getProcessName());
                                    log(name+bundle.getString("_arguments__")+((NbProcessDescriptor)(prop[j].getValue())).getArguments());
                                } else if (propname.equals("classic")) {
                                    log(name+" classic: "+prop[j].getValue());
                                }
                            }
                        } catch (Exception e) {
                            log(bundle.getString("Property_exception"));
                        }
                    }
                }
            }

            log(bundle.getString("Finished."));

        } catch (Exception e) {
            log(bundle.getString("Unexpected_exception"));
            fail();
        }
        
    }
    
    
    
    
    public void testRMIHideStubs() {
           try {
            System.setProperty("netbeans.debug.exceptions","true"); // NOI18N
            RMIDataLoader loader=(RMIDataLoader)RMIDataLoader.getLoader(Class.forName("org.netbeans.modules.rmi.RMIDataLoader"));
            FileObject file=TopManager.getDefault().getRepository().find("data.work","HelloWorldImpl","java");
            Node work=DataObject.find(file.getParent()).getNodeDelegate();
            sup.compile(DataObject.find(file));
            RMISettings settings=(RMISettings)RMISettings.findObject(Class.forName("org.netbeans.modules.rmi.settings.RMISettings"));
            if (work.getChildren().findChild("HelloWorldImpl_Stub")==null)
                log("Stubs are now hidden");
            else
                log("Stubs are now visible");
            log("Showing stubs");
            loader.setHideStubs(false);
            DataObject.find(file).setValid(false);
            int i=0;
            do sup.sleep(1000); while ((i++<20)&&(work.getChildren().findChild("HelloWorldImpl_Stub")==null));    
            if (work.getChildren().findChild("HelloWorldImpl_Stub")==null)
                log("Stubs are now hidden");
            else
            log("Stubs are now visible");
            log("Hiding stubs");
            loader.setHideStubs(true);
            DataObject.find(file).setValid(false);
            i=0;
            do sup.sleep(1000); while ((i++<20)&&(work.getChildren().findChild("HelloWorldImpl_Stub")!=null));    
            if (work.getChildren().findChild("HelloWorldImpl_Stub")==null)
                log("Stubs are now hidden");
            else
                log("Stubs are now visible");
            log(bundle.getString("Finished."));

        } catch (Exception e) {
            log(bundle.getString("Unexpected_exception"));
            fail();
        }
    }
    
    
    
    
    public void testRMIModuleEnable() {
        
        try {
            System.setProperty("netbeans.debug.exceptions","true"); // NOI18N
            //getting node of RMI Module enable property
            Node n=null;
            Node.Property props[]=null;
            int i=0;
            while ((i++<30)&&(n==null)) {
                try {
                    n=TopManager.getDefault().getPlaces().nodes().session();
                    n=sup.findChild(n,"IDEConfiguration");
                    n=sup.findChild(n,"System");
                    n=sup.findChild(n,"Modules");
                    n=sup.findChild(n,"Distributed Application Support");
                    n=sup.findChild(n,bundle.getString("org.netbeans.modules.rmi"));
                    props=(Node.Property[])n.getPropertySets()[0].getProperties(); 
                } catch (NullPointerException npe) {
                    sup.sleep(1000);
                }
            }
            Node.Property prop=null;
            for (i=0;i<props.length;i++) 
                if (props[i].getName().equalsIgnoreCase(bundle.getString("enabled"))) prop=props[i];
            if (prop!=null) { 
                if (((Boolean)(prop.getValue())).booleanValue()) log(bundle.getString("Module_RMI_instaled.")); else log(bundle.getString("Module_RMI_not_instaled."));
                log(bundle.getString("Uninstaling_module_RMI"));
                //unistaling module RMI
                prop.setValue(new Boolean(false));
                sup.sleep(5000);
                if (((Boolean)(prop.getValue())).booleanValue()) log(bundle.getString("Module_RMI_not_uninstaled.")); else log(bundle.getString("Module_RMI_uninstaled."));
                log(bundle.getString("Instaling_module_RMI"));
                //istaling module RMI
                prop.setValue(new Boolean(true));
                sup.sleep(5000);
                if (((Boolean)(prop.getValue())).booleanValue()) log(bundle.getString("Module_RMI_instaled.")); else log(bundle.getString("Module_RMI_not_instaled."));
            } else log(bundle.getString("Wrong_property_")+prop.getName());
            log(bundle.getString("Finished."));

        } catch (Exception e) {
            log(bundle.getString("Unexpected_exception"));
            fail();
        }
        
    }
    
    
    
    
    public void testRMIDataLoader() {
        
        try {
            System.setProperty("netbeans.debug.exceptions","true"); // NOI18N
            RMIDataLoader loader=(RMIDataLoader)RMIDataLoader.getLoader(RMIDataLoader.class);
            FileObject file=TopManager.getDefault().getRepository().find("data.work","HelloWorldImpl","java");
            
            log("Disabling Detect Remote and Confirm Convert");
            RMISettings settings=(RMISettings)RMISettings.findObject(RMISettings.class,true);
            settings.setConfirmConvert(false);
            settings.setDetectRemote(false);

            if (JavaDataObject.find(file) instanceof RMIDataObject)
                log("HelloWorldImpl is instance of RMIDataObject");
            else
                log("HelloWorldImpl is not instance of RMIDataObject");
            log("Unmarking as RMI");
            sup.markRMI((JavaDataObject)JavaDataObject.find(file),false);
            if (JavaDataObject.find(file) instanceof RMIDataObject)
                log("HelloWorldImpl is instance of RMIDataObject after unmarking");
            else
                log("HelloWorldImpl is not instance of RMIDataObject after unmarking");
            log("Marking as RMI");
            sup.markRMI((JavaDataObject)JavaDataObject.find(file),true);
            if (JavaDataObject.find(file) instanceof RMIDataObject)
                log("HelloWorldImpl is instance of RMIDataObject after marking");
            else
                log("HelloWorldImpl is not instance of RMIDataObject after marking");
            log("Unmarking as RMI");
            sup.markRMI((JavaDataObject)JavaDataObject.find(file),false);
            if (JavaDataObject.find(file) instanceof RMIDataObject)
                log("HelloWorldImpl is instance of RMIDataObject after unmarking");
            else
                log("HelloWorldImpl is not instance of RMIDataObject after unmarking");
            log("Enabling Detect Remote");
            RMISettings.getInstance().setDetectRemote(true);
            int i=0;
            do {
                JavaDataObject.find(file).setValid(false);
                ((JavaDataObject)JavaDataObject.find(file)).getSource().prepare();
                sup.sleep(1000); 
            } while ((i++<30)&&!(JavaDataObject.find(file) instanceof RMIDataObject));
            if (JavaDataObject.find(file) instanceof RMIDataObject)
                log("HelloWorldImpl is instance of RMIDataObject after automatic detection");
            else {
                log("HelloWorldImpl is not instance of RMIDataObject after automatic detection");
                sup.markRMI((JavaDataObject)JavaDataObject.find(file),true);
            }
            sup.compile(JavaDataObject.find(file));

            log(bundle.getString("Finished."));

        } catch (Exception e) {
            log(bundle.getString("Unexpected_exception"));
            fail();
        }
    }
    
    
    
    
    
    public void testRMIExecutor() {
        DataObject obj;
        String URL = "rmitests.RMIExecutorTest"; // NOI18N
        String source = workpackage + "/HelloWorldImpl.java"; // NOI18N

        try {
            System.setProperty("netbeans.debug.exceptions","true"); // NOI18N
            
            //Getting local registry 
            log(bundle.getString("Getting_local_registry_item"));
            RegistryItem regitem = RMIRegistrySupport.getLocalRegistryItem();
            
            // Update registry
            log(bundle.getString("Updating_all_registry_items"));
            RMIRegistrySupport.updateAllRegistryItems();
            // Find HelloWorldImpl in registry
            log(bundle.getString("Finding_service_")+URL+bundle.getString("_in_registry"));
            if (RMIRegistrySupport.getService(regitem,URL) == null)
                log(bundle.getString("Service_")+URL+bundle.getString("_not_found_in_registry_before_execution."));
            else
                log(bundle.getString("Service_")+URL+bundle.getString("_found_in_registry_before_execution."));

            //Execute HelloWorldImpl
            //String logfile= Globals.get("WORKDIR")+File.separator+Globals.get("user")+"."+Globals.get("osName")+"."+Globals.get("osArch")+File.separator+"RMIExecutorTest"+File.separator+"server3.log"; // NOI18N
  
            obj=sup.getDataObject(source);
            sup.setExecutor(obj,sup.RMI_EXECUTOR);
            sup.execute(obj,URL); // NOI18N
            log(obj.getName()+bundle.getString("_executed"));

            int i=0;
            do {
                i++;
                // Wait
                try { Thread.currentThread().sleep(5000); }
                catch(InterruptedException e) { log(bundle.getString("Interrupted_during_sup.sleeping"));}

                // Update registry
                log(bundle.getString("Updating_all_registry_items"));
                RMIRegistrySupport.updateAllRegistryItems();
                // Find HelloWorldImpl in registry
                log(bundle.getString("Finding_service_")+URL+bundle.getString("_in_registry_after_execution."));
            } while ((RMIRegistrySupport.getService(regitem,URL) == null)&&(i<20));

            if (RMIRegistrySupport.getService(regitem,URL) == null)
                log(bundle.getString("Service_")+URL+bundle.getString("_not_found_in_registry_after_execution.\nFound_sevices_\n")+RMIRegistrySupport.getAllServiceNames());
            else
                log(bundle.getString("Service_")+URL+bundle.getString("_found"));

            log(bundle.getString("Finished."));
        } catch (Exception e) {
            e.printStackTrace(getLog());
            fail();
        }
    }
    
    
    
    public void testRMICompileTest() {
        DataObject obj;
        
        try {
            System.setProperty("netbeans.debug.exceptions","true"); // NOI18N
            // Test compilation on HelloWorld interface
            obj=sup.getDataObject(hello);
            sup.build(obj);
            sup.clean(obj);
            sup.compile(obj);
            // Test compilation on HelloWorldImpl
            obj=sup.getDataObject(helloImpl);
            sup.build(obj);
            sup.clean(obj);
            sup.compile(obj);
            // Test compilation on HelloClient
            obj=sup.getDataObject(helloClient);
            sup.build(obj);
            sup.clean(obj);
            sup.compile(obj);
            // Test compilation on HelloWorldActive
            obj=sup.getDataObject(helloActiv);
            sup.build(obj);
            sup.clean(obj);
            sup.compile(obj);

            log(bundle.getString("Finished."));
        } catch (Exception e) {
            log(bundle.getString("Unexpected_exception"));
            fail();
        }
    }
}