/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.DateFormat;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;
import java.util.Properties;
import java.util.HashMap;
import javax.swing.event.*;
import org.openide.actions.AbstractCompileAction;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.filesystems.FileLock;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.cookies.CompilerCookie;
import org.openide.src.ClassElement;
import org.openide.src.Type;
import org.openide.src.Identifier;
import org.openide.*;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.enum.QueueEnumeration;
import org.netbeans.modules.corba.wizard.panels.*;
import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.CORBASupport;
import org.netbeans.modules.corba.IDLNodeCookie;
import org.netbeans.modules.corba.poasupport.POAElement;
import org.netbeans.modules.corba.poasupport.ServantElement;
import org.netbeans.modules.corba.poasupport.POASupport;
import org.netbeans.modules.corba.poasupport.nodes.POANode;
import org.netbeans.modules.corba.poasupport.nodes.POAChildren;
import org.netbeans.modules.corba.poasupport.tools.POAChecker;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBSettingsBundle;
import org.netbeans.modules.corba.settings.ORBSettings;
import org.netbeans.modules.corba.settings.POASettings;
import org.netbeans.modules.corba.settings.ORBBindingDescriptor;
import org.netbeans.modules.corba.settings.WizardSettings;
import org.netbeans.modules.corba.settings.WizardRequirement;
import org.netbeans.modules.corba.wizard.utils.Constants;
import org.netbeans.modules.corba.wizard.panels.util.CosNamingDetails;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ns.ObjectNode;
import org.netbeans.modules.corba.browser.ns.ContextNode;
import org.netbeans.modules.corba.browser.ns.GenerateSupport;
import org.netbeans.modules.corba.utils.Pair;
import java.text.MessageFormat;


/**
 *
 * @author  Tomas Zezula, Dusan Balek
 * @version 1.0
 */
public class CorbaWizard extends Object implements PropertyChangeListener, WizardDescriptor.Iterator {
    
    public static final String PROP_AUTO_WIZARD_STYLE = "WizardPanel_autoWizardStyle"; // NOI18N
    public static final String PROP_HELP_DISPLAYED = "WizardPanel_helpDisplayed"; // NOI18N
    public static final String PROP_CONTENT_DISPLAYED = "WizardPanel_contentDisplayed"; // NOI18N
    public static final String PROP_CONTENT_NUMBERED = "WizardPanel_contentNumbered"; // NOI18N
    public static final String PROP_CONTENT_SELECTED_INDEX = "WizardPanel_contentSelectedIndex"; // NOI18N
    public static final String PROP_CONTENT_DATA = "WizardPanel_contentData"; // NOI18N
    public static final String PROP_CONTENT_BACK_COLOR = "WizardPanel_contentBackColor"; // NOI18N
    public static final String PROP_IMAGE = "WizardPanel_image"; // NOI18N
    public static final String PROP_LEFT_DIMENSION = "WizardPanel_leftDimension"; // NOI18N
    public static final String PROP_HELP_URL = "WizardPanel_helpURL"; // NOI18N
    
    private static final int IMPL_PANELS_COUNT = 6;
    // private static final boolean DEBUG = true;
    private static final boolean DEBUG = false;
    
    private int panelsCount = IMPL_PANELS_COUNT;
    private int index;
    private CorbaWizardData data;
    private Dialog dialog;
    private boolean locked;
    private PackagePanel packagePanel = new PackagePanel ();
    private StartPanel startPanel = new StartPanel ();
    private ORBPanel orbPanel = new ORBPanel ();
    private RootInterface rootInterfacePanel;
    private AbstractWizardPanel bindingMethodDetailsPanel;
    private FinishPanel finishPanel = new FinishPanel ();
    private ArrayList listeners = new ArrayList ();
    
    private class WizardGenerator extends Thread {
        
        private static final String STRING = "string";
        private static final String NAMING = "ns_code";
        private static final String FILE = "file_name";
        
        public WizardGenerator () {
        }
        
        public void run () {
            
            PrintWriter out = null;
            FileLock lock = null;
            FileObject destination = null;
            
            CorbaWizard.this.dialog.setVisible (false);
            CorbaWizard.this.dialog.dispose();
            try {
                DataFolder pkg = CorbaWizard.this.data.getDestinationPackage ();
                IDLDataObject idlDataObject = (IDLDataObject) CorbaWizard.this.data.getIdlSource();
                String name = CorbaWizard.this.data.getName();
                int mode = CorbaWizard.this.data.getGenerate ();
                ORBSettings activeSettings = CorbaWizard.this.data.getSettings().getActiveSetting();
                String rootInterface = CorbaWizard.this.data.getRootInterface();
                String rootInterfaceJavaName = null;
                String callBackInterface = CorbaWizard.this.data.getCallBackInterface();
                String callBackInterfaceJavaName = null;
                String implClassName = null;
                String implClassTIECtorParam = null;
                String cbImplClassName = null;
                String cbImplClassTIECtorParam = null;
                Task generationImplTask = null;
                POASettings poaSettings = null;
                
                idlDataObject.setOrbForCompilation (CorbaWizard.this.data.getCORBAImpl());
                
                //Set Tie
                CorbaWizard.this.data.setDefaultSkeletons(activeSettings.getSkeletons());
                if (CorbaWizard.this.data.getTie())
                    activeSettings.setSkeletons (ORBSettingsBundle.TIE);
                else
                    activeSettings.setSkeletons (ORBSettingsBundle.INHER);
                
                // Compile IDL file
                QueueEnumeration q = new QueueEnumeration ();
                Object cookie = idlDataObject.getCookie (CompilerCookie.Compile.class);
                if (cookie != null) q.put (cookie);
                AbstractCompileAction.compile (q, "");
                
                // Create Impl files
                if ((mode & CorbaWizardData.IMPL) == CorbaWizardData.IMPL) {
                    TopManager.getDefault().setStatusText (CorbaWizardAction.getLocalizedString("MSG_CreatingImpl"));
                    idlDataObject.generateImplementation();
                    generationImplTask = idlDataObject.getGenerationTask();
                }
                
                if (((mode & CorbaWizardData.CLIENT) == CorbaWizardData.CLIENT) || ((mode & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT) || ((mode & CorbaWizardData.SERVER) == CorbaWizardData.SERVER)) {
                    CorbaWizard.this.data.setDefaultJavaTemplateCodePatchTable(activeSettings.getJavaTemplateCodePatchTable());
                    rootInterfaceJavaName = idlScopedName2JavaName(rootInterface, pkg);
                    // Substitution of TEMPLATE tags by data
                    activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_INTERFACE_NAME*/", rootInterfaceJavaName); // NOI18N
                }
                if (((mode & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT) || ((mode & CorbaWizardData.SERVER) == CorbaWizardData.SERVER)) {
                    poaSettings = activeSettings.getPOASettings();
                    if (poaSettings != null) {
                        activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_OA_VAR_NAME*/", poaSettings.getDefaultPOAVarName()); // NOI18N
                        activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_SERVANT_VAR_NAME*/", poaSettings.getDefaultServantVarName()); // NOI18N
                    }
                    else {
                        activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_SERVANT_VAR_NAME*/", "servant"); // NOI18N
                    }
                }
                if (((mode & CorbaWizardData.CLIENT) == CorbaWizardData.CLIENT) || ((mode & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT)) {
                    Iterator bindings = activeSettings.getClientBindings ().iterator();
                    ORBBindingDescriptor bd = null;
                    HashMap bindingDetail = (HashMap)CorbaWizard.this.data.getBindingDetails();
                    while (bindings.hasNext()) {
                        bd = (ORBBindingDescriptor)bindings.next();
                        if (bd.getName().equals(CorbaWizard.this.data.getClientBindMethod())) {
                            WizardSettings ws = bd.getWizardSettings();
                            if (ws != null && ws.isSupported()) {
                                Iterator wri = ws.getRequirements().iterator();
                                while (wri.hasNext()) {
                                    WizardRequirement wr = (WizardRequirement)wri.next();
                                    if (wr.getType().equals(STRING))
                                        activeSettings.addJavaTemplateCodePatchPair(wr.getValue(), (String)bindingDetail.get(wr.getValue())); // NOI18N
                                    else if (wr.getType().equals(FILE))
                                        activeSettings.addJavaTemplateCodePatchPair(wr.getValue(), GenerateSupport.correctCode((String)bindingDetail.get(wr.getValue()))); // NOI18N
                                    else if (wr.getType().equals(NAMING)) {
                                        CosNamingDetails dtls = (CosNamingDetails)bindingDetail.get(wr.getValue());
                                        Vector names = new Vector ();
                                        Node tmp_node = dtls.node;
                                        while (tmp_node.getParentNode () != null) {
                                            ContextNode cn = (ContextNode)tmp_node.getCookie (ContextNode.class);
                                            tmp_node = tmp_node.getParentNode ();
                                            names.add (cn.getName ());
                                            names.add (cn.getKind ());
                                        }
                                        String paste = new String ("String[] client_name_hierarchy = new String [] {"); // NOI18N
                                        for (int i=names.size () - 6; i>=0; i=i-2) {
                                            paste = paste + "\"" + GenerateSupport.correctCode((String)names.elementAt (i)) + "\"" + ", "; // NOI18N
                                            paste = paste + "\"" + GenerateSupport.correctCode((String)names.elementAt (i+1)) + "\"" + ", "; // NOI18N
                                        }
                                        if (dtls.name != null) {
                                            paste = paste + "\"" + GenerateSupport.correctCode(dtls.name) + "\", "; // NOI18N
                                            paste = paste + "\"" + GenerateSupport.correctCode(dtls.kind) + "\", "; // NOI18N
                                        }
                                        if (paste.substring (paste.length () - 2, paste.length ()).equals (", ")) // NOI18N
                                            paste = paste.substring (0, paste.length () - 2);
                                        paste = paste + "};"; // NOI18N
                                        activeSettings.addJavaTemplateCodePatchPair(wr.getValue(), paste);
                                    }
                                }
                            }
                        }
                    }
                    DataFolder templates = TopManager.getDefault().getPlaces().folders().templates();
                    if ((mode & CorbaWizardData.CLIENT) == CorbaWizardData.CLIENT) {
                        // Generate Client
                        activeSettings.setJavaTemplateTable();
                        TopManager.getDefault().setStatusText (CorbaWizardAction.getLocalizedString("MSG_CreatingClient"));
                        DataObject template = findDataObject (templates,"CORBA/ClientMain");    // No I18N
                        String clientName = name+"Client"; // NOI18N
                        DataObject client = template.createFromTemplate (pkg, clientName);
                        OpenCookie openCookie = (OpenCookie) client.getCookie (OpenCookie.class);
                        if (openCookie != null)
                            openCookie.open();
                    }
                    if ((mode & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT) {
                        Iterator implNames = idlDataObject.getImplementationNames().iterator();
                        int idx = callBackInterface.lastIndexOf("::");
                        String ifaceName = (idx == -1) ? callBackInterface : callBackInterface.substring(idx+2);
                        if (generationImplTask != null)
                            generationImplTask.waitFinished();
                        while (implNames.hasNext()) {
                            Pair pair = (Pair)implNames.next();
                            if (((String)pair.second).indexOf(ifaceName) != -1) {
                                String implName = pair.first.toString() + pair.second.toString();
                                if (pkg != null) {
                                    String pkgName = pkg.getPrimaryFile().getPackageName('.');
                                    if (pkgName != null && pkgName.length() > 0) 
                                        implName = pkg.getPrimaryFile().getPackageName('.') + "." + implName;
                                }
                                ClassElement cle = ClassElement.forName( implName );
                                if (cle != null) {
                                    Identifier sid = cle.getSuperclass();
                                    if (sid != null) {
                                        String baseName = sid.getName();
                                        if (baseName != null && baseName.startsWith(activeSettings.getExtClassPrefix()) && baseName.endsWith(activeSettings.getExtClassPostfix())) {
                                            cbImplClassName = implName;
                                            activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_SERVANT_CLASS_NAME*/", cbImplClassName); // NOI18N
                                        }
                                    }
                                    if (cbImplClassName == null) {
                                        Identifier[] iids = cle.getInterfaces();
                                        for (int j = 0; j < iids.length; j++) {
                                            String baseName = iids[j].getName();
                                            if (baseName != null && baseName.startsWith(activeSettings.getImplIntPrefix()) && baseName.endsWith(activeSettings.getImplIntPostfix())) {
                                                cbImplClassName = baseName.substring(activeSettings.getImplIntPrefix().length(), baseName.length() - activeSettings.getImplIntPostfix().length());
                                                String qual = iids[j].getQualifier();
                                                if (qual != null && qual.length() > 0)
                                                    cbImplClassName = qual + "." + activeSettings.getTieClassPrefix() + cbImplClassName + activeSettings.getTieClassPostfix();
                                                else
                                                    cbImplClassName = activeSettings.getTieClassPrefix() + cbImplClassName + activeSettings.getTieClassPostfix();
                                                activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_SERVANT_CLASS_NAME*/", cbImplClassName); // NOI18N
                                                cbImplClassTIECtorParam = "new " + implName + "()"; // NOI18N
                                                activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_TIE_SERVANT_CTOR_PARAM*/", cbImplClassTIECtorParam); // NOI18N
                                                activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_TIE_SERVANT_CTOR_PARAMS*/", cbImplClassTIECtorParam + ", "); // NOI18N
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        // Generate Call-back Client
                        activeSettings.setJavaTemplateTable();
                        TopManager.getDefault().setStatusText (CorbaWizardAction.getLocalizedString("MSG_CreatingCallBackClient"));
                        DataObject template = findDataObject (templates,"CORBA/CallBackClientMain");    // No I18N
                        String clientName = name+"CallBackClient"; // NOI18N
                        DataObject client = template.createFromTemplate (pkg, clientName);
                        Node rootPOANode = null;
                        try {
                            rootPOANode = NodeOp.findPath(client.getNodeDelegate(), new String [] {clientName, POASupport.getString("LBL_RootPOA_node")});
                        }
                        catch (Exception e) {
                        }
                        if (rootPOANode != null) {
                            POAElement poa = ((POANode)rootPOANode).getPOAElement();
                            POAElement newPOA = new POAElement(poa, poa.getRootPOA(), poa.isWriteable());
                            newPOA.setPOAName(poaSettings.getDefaultPOAName());
                            newPOA.setVarName(poaSettings.getDefaultPOAVarName());
                            newPOA.setManager(poa.getVarName());
                            poa.addChildPOA(newPOA);
                            ServantElement newServant = new ServantElement(newPOA, newPOA.isWriteable());
                            newServant.setVarName(poaSettings.getDefaultServantVarName());
                            newServant.setObjID(poaSettings.getDefaultServantId());
                            if (cbImplClassName != null) {
                                newServant.setTypeName(cbImplClassName);
                                if (cbImplClassTIECtorParam != null && cbImplClassTIECtorParam.length() > 0)
                                    newServant.setConstructor(cbImplClassName + "(" + cbImplClassTIECtorParam + ")"); // NOI18N
                                else
                                    newServant.setConstructor(cbImplClassName + "()"); // NOI18N
                            }
                            newPOA.addServant(newServant);
                        }
                        SaveCookie saveCookie = (SaveCookie) client.getCookie (SaveCookie.class);
                        if (saveCookie != null)
                            saveCookie.save();
                        OpenCookie openCookie = (OpenCookie) client.getCookie (OpenCookie.class);
                        if (openCookie != null)
                            openCookie.open();
                    }
                }
                
                if ((mode & CorbaWizardData.SERVER) == CorbaWizardData.SERVER) {
                    Iterator implNames = idlDataObject.getImplementationNames().iterator();
                    int idx = rootInterface.lastIndexOf("::");
                    String ifaceName = (idx == -1) ? rootInterface : rootInterface.substring(idx+2);
                    if (generationImplTask != null)
                        generationImplTask.waitFinished();
                    while (implNames.hasNext()) {
                        Pair pair = (Pair)implNames.next();
                        if (((String)pair.second).indexOf(ifaceName) != -1) {
                            String implName = pair.first.toString() + pair.second.toString();
                            if (pkg != null) {
                                String pkgName = pkg.getPrimaryFile().getPackageName('.');
                                if (pkgName != null && pkgName.length() > 0)
                                    implName =  pkgName + "." + implName;
                            }
                            ClassElement cle = ClassElement.forName( implName );
                            if (cle != null) {
                                Identifier sid = cle.getSuperclass();
                                if (sid != null) {
                                    String baseName = sid.getName();
                                    if (baseName != null && baseName.startsWith(activeSettings.getExtClassPrefix()) && baseName.endsWith(activeSettings.getExtClassPostfix())) {
                                        implClassName = implName;
                                        activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_SERVANT_CLASS_NAME*/", implClassName); // NOI18N
                                    }
                                }
                                if (implClassName == null) {
                                    Identifier[] iids = cle.getInterfaces();
                                    for (int j = 0; j < iids.length; j++) {
                                        String baseName = iids[j].getName();
                                        if (baseName != null && baseName.startsWith(activeSettings.getImplIntPrefix()) && baseName.endsWith(activeSettings.getImplIntPostfix())) {
                                            implClassName = baseName.substring(activeSettings.getImplIntPrefix().length(), baseName.length() - activeSettings.getImplIntPostfix().length());
                                            String qual = iids[j].getQualifier();
                                            if (qual != null && qual.length() > 0)
                                                implClassName =  qual + "." + activeSettings.getTieClassPrefix() + implClassName + activeSettings.getTieClassPostfix();
                                            else
                                                implClassName =  activeSettings.getTieClassPrefix() + implClassName + activeSettings.getTieClassPostfix();
                                            activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_SERVANT_CLASS_NAME*/", implClassName); // NOI18N
                                            implClassTIECtorParam = "new " + implName + "()"; // NOI18N
                                            activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_TIE_SERVANT_CTOR_PARAM*/", implClassTIECtorParam); // NOI18N
                                            activeSettings.addJavaTemplateCodePatchPair("/*FFJ_CORBA_TODO_TIE_SERVANT_CTOR_PARAMS*/", implClassTIECtorParam + ", "); // NOI18N
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                    Iterator bindings = activeSettings.getServerBindings ().iterator();
                    ORBBindingDescriptor bd = null;
                    HashMap bindingDetail = (HashMap)CorbaWizard.this.data.getBindingDetails();
                    while (bindings.hasNext()) {
                        bd = (ORBBindingDescriptor)bindings.next();
                        if (bd.getName().equals(CorbaWizard.this.data.getBindMethod())) {
                            WizardSettings ws = bd.getWizardSettings();
                            if (ws != null && ws.isSupported()) {
                                Iterator wri = ws.getRequirements().iterator();
                                while (wri.hasNext()) {
                                    WizardRequirement wr = (WizardRequirement)wri.next();
                                    if (wr.getType().equals(STRING))
                                        activeSettings.addJavaTemplateCodePatchPair(wr.getValue(), (String)bindingDetail.get(wr.getValue())); // NOI18N
                                    else if (wr.getType().equals(FILE))
                                        activeSettings.addJavaTemplateCodePatchPair(wr.getValue(), GenerateSupport.correctCode((String)bindingDetail.get(wr.getValue()))); // NOI18N
                                    else if (wr.getType().equals(NAMING)) {
                                        CosNamingDetails dtls = (CosNamingDetails)bindingDetail.get(wr.getValue());
                                        Vector names = new Vector ();
                                        Node tmp_node = dtls.node;
                                        while (tmp_node.getParentNode () != null) {
                                            ContextNode cn = (ContextNode)tmp_node.getCookie (ContextNode.class);
                                            tmp_node = tmp_node.getParentNode ();
                                            names.add (cn.getName ());
                                            names.add (cn.getKind ());
                                        }
                                        String paste = new String ("String[] hierarchy_of_contexts = new String [] {"); // NOI18N
                                        for (int i=names.size () - 6; i>=0; i=i-2) {
                                            paste = paste + "\"" + GenerateSupport.correctCode((String)names.elementAt (i)) + "\"" + ", "; // NOI18N
                                            paste = paste + "\"" + GenerateSupport.correctCode((String)names.elementAt (i+1)) + "\"" + ", "; // NOI18N
                                        }
                                        if (paste.substring (paste.length () - 2, paste.length ()).equals (", ")) // NOI18N
                                            paste = paste.substring (0, paste.length () - 2);
                                        paste = paste + "};\n"; // NOI18N
                                        paste = paste + "String[] name_of_server = new String [] {\"" + GenerateSupport.correctCode(dtls.name) + "\", "; // NOI18N
                                        paste = paste + "\"" + GenerateSupport.correctCode(dtls.kind) + "\"};"; // NOI18N
                                        activeSettings.addJavaTemplateCodePatchPair(wr.getValue(), paste);
                                    }
                                }
                            }
                        }
                    }
                    activeSettings.setJavaTemplateTable();
                    DataFolder templates = TopManager.getDefault().getPlaces().folders().templates();
                    // Generate Server
                    TopManager.getDefault().setStatusText (CorbaWizardAction.getLocalizedString("MSG_CreatingServer"));
                    DataObject template = findDataObject (templates,"CORBA/ServerMain");    // No I18N
                    String serverName = name + "Server"; // NOI18N
                    DataObject server = template.createFromTemplate (pkg, serverName);
                    Node rootPOANode = null;
                    try {
                        rootPOANode = NodeOp.findPath(server.getNodeDelegate(), new String [] {serverName, POASupport.getString("LBL_RootPOA_node")});
                    }
                    catch (Exception e) {
                    }
                    if (rootPOANode != null) {
                        POAElement poa = ((POANode)rootPOANode).getPOAElement();
                        POAElement newPOA = new POAElement(poa, poa.getRootPOA(), poa.isWriteable());
                        newPOA.setPOAName(poaSettings.getDefaultPOAName());
                        newPOA.setVarName(poaSettings.getDefaultPOAVarName());
                        newPOA.setManager(poa.getVarName());
                        Properties policies = newPOA.getPolicies();
                        if (POAChecker.checkPOAPoliciesChange(newPOA, policies, "Lifespan", "PERSISTENT", false) &&
                        POAChecker.checkPOAPoliciesChange(newPOA, policies, "Id Assignment", "USER_ID", false)) // NOI18N
                            newPOA.setPolicies(policies);
                        poa.addChildPOA(newPOA);
                        ServantElement newServant = new ServantElement(newPOA, newPOA.isWriteable());
                        newServant.setVarName(poaSettings.getDefaultServantVarName());
                        newServant.setObjID(poaSettings.getDefaultServantId());
                        if (implClassName != null) {
                            newServant.setTypeName(implClassName);
                            if (implClassTIECtorParam != null && implClassTIECtorParam.length() > 0)
                                newServant.setConstructor(implClassName + "(" + implClassTIECtorParam + ")"); // NOI18N
                            else
                                newServant.setConstructor(implClassName + "()"); // NOI18N
                        }
                        newPOA.addServant(newServant);
                    }
                    SaveCookie saveCookie = (SaveCookie) server.getCookie (SaveCookie.class);
                    if (saveCookie != null)
                        saveCookie.save();
                    OpenCookie openCookie = (OpenCookie) server.getCookie (OpenCookie.class);
                    if (openCookie != null)
                        openCookie.open();
                }
            }
            catch (IOException ioe) {
                // Handle Error Here
                TopManager.getDefault().notifyException (ioe);
            }
            finally {
                CorbaWizard.this.rollBack();
            }
        }
        
        /** Finds the DataObject given by name in the hierarchy
         *  @param DataFolder folder, the root folder
         *  @param String name, name of DataObject, '/' as separator
         *  @return DataObject if exists, null otherwise
         */
        private DataObject findDataObject (DataFolder folder, String name) {
            if (name.length() == 0)
                return null;
            return findDataObject (folder, new StringTokenizer (name, "/")); // NOI18N
        }
        
        private DataObject findDataObject (DataFolder folder, StringTokenizer tk) {
            String nameComponent = tk.nextToken();
            DataObject[] list = folder.getChildren();
            for (int i=0; i< list.length; i++) {
                if (list[i].getName().equals(nameComponent)){
                    if (!tk.hasMoreTokens())
                        return list[i];
                    else if (list[i] instanceof DataFolder)
                        return findDataObject ((DataFolder)list[i], tk);
                    else
                        return null;
                }
            }
            return null;
        }
        
        private String idlScopedName2JavaName (String name, DataFolder pkg) {
            String javaName = Utilities.replaceString(name, "::", ".");
            if (pkg != null) {
                String pkgName = pkg.getPrimaryFile().getPackageName('.');
                if (pkgName != null && pkgName.length() > 0)
                    javaName = pkgName + "." + javaName;
            }
            return javaName;
        }
    }
    
    /** Creates new CorbaWizard
     */
    public CorbaWizard () {
        this (null);
    }
    
    /** Creates new CorbaWizard */
    public CorbaWizard (IDLDataObject dobj) {
        this.index = 0;
        this.data = new CorbaWizardData ();
        this.data.setIdlSource (dobj);
    }
    
    
    /** Returns current panel
     *  @return WizardDescriptor.Panel
     */
    public WizardDescriptor.Panel current() {
        switch (this.index) {
            case 0:
                return packagePanel;
            case 1:
                return startPanel;
            case 2:
                return orbPanel;
            case 3:
                return getRootInterfacePanel();
            case 4:
                return getBindingDetailsPanel();
            case 5:
                return finishPanel;
                default:
                    return null;
        }
    }
    
    /** Can the iterater return next panel
     *  @return boolean
     */
    public boolean hasNext() {
        return (index < (panelsCount -1));
    }
    
    /** Can the iterator return previous panel
     *  @return boolean
     */
    public boolean hasPrevious () {
        return index > 0;
    }
    
    
    /** Return index of current panel
     *  @return int current index
     */
    private int currentIndex () {
        return index;
    }
    
    /** Returns the name of Wizard
     *  @return String wizard name
     */
    public String name () {
        return NbBundle.getBundle(CorbaWizard.class).getString(MessageFormat.format ("TXT_P{0}",new Object[]{new Integer (this.index)}));
    }
    
    /** Returns the next panel
     *  @return WizardDescriptor.Panel
     */
    public synchronized void nextPanel () {
        if (index < panelsCount)
            this.index++;
        int mask = this.data.getGenerate();
        if (index == 3 && !(((mask & CorbaWizardData.CLIENT) == CorbaWizardData.CLIENT) || ((mask & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT) || ((mask & CorbaWizardData.SERVER) == CorbaWizardData.SERVER)))
            this.index+=2;
    }
    
    /** Returns the previous panel
     *  @return WizardDescriptor.Panel
     */
    public synchronized void  previousPanel () {
        if (index > 0)
            this.index--;
        int mask = this.data.getGenerate();
        if (index == 4 && !(((mask & CorbaWizardData.CLIENT) == CorbaWizardData.CLIENT) || ((mask & CorbaWizardData.SERVER) == CorbaWizardData.SERVER)))
            this.index-=2;
    }
    
    /** Starts the wizard
     *  @see CorbaWizardAction
     */
    public void run () {
        if (DEBUG)
            System.out.println("Starting CORBA Wizard...");
        WizardDescriptor descriptor = new WizardDescriptor (CorbaWizard.this, data);
        descriptor.setClosingOptions (new Object[] {DialogDescriptor.CANCEL_OPTION});
        descriptor.setTitleFormat(new java.text.MessageFormat ("{0}"));
        descriptor.setTitle(CorbaWizardAction.getLocalizedString("TITLE_CorbaWizard"));
        descriptor.addPropertyChangeListener (CorbaWizard.this);
        dialog = TopManager.getDefault().createDialog (descriptor);
        dialog.show();
    }
    
    /** Adds ChangeListener
     *  @param ChangeListener listener
     */
    public synchronized void addChangeListener (ChangeListener listener){
        if (DEBUG)
            System.out.println("addChangeListener added");
        this.listeners.add (listener);
    }
    
    /** Removes ChangeListener
     * @param ChangeListener listener
     */
    public synchronized void removeChangeListener (ChangeListener listener){
        this.listeners.remove (listener);
    }
    
    /** Callback for CorbaWizardDescriptor
     *  @param PropertyChangeListener event
     */
    public void propertyChange(final PropertyChangeEvent event) {
        if (DialogDescriptor.PROP_VALUE.equals(event.getPropertyName())){
            Object option = event.getNewValue();
            if (option == WizardDescriptor.FINISH_OPTION) {
                WizardGenerator wg = this.new WizardGenerator ();
                wg.start ();
            }
            else if (option == WizardDescriptor.CANCEL_OPTION) {
                this.rollBack();
                dialog.setVisible(false);
                dialog.dispose ();
            }
        }
    }
    
    
    protected void fireEvent () {
        ArrayList list;
        synchronized (this) {
            list = (ArrayList) this.listeners.clone();
        }
        ChangeEvent event = new ChangeEvent (this);
        for (int i=0; i<list.size(); i++)
            ((ChangeListener)list.get (i)).stateChanged (event);
    }
    
    
    private void rollBack () {
        CORBASupportSettings css = this.data.getSettings();
        if (css != null) {
            if (this.data.getDefaultJavaTemplateCodePatchTable() != null)
                css.getActiveSetting().setJavaTemplateCodePatchTable (this.data.getDefaultJavaTemplateCodePatchTable());
            if (this.data.getDefaultServerBindingValue() != null)
                css.getActiveSetting().setServerBindingFromString(this.data.getDefaultServerBindingValue());
            if (this.data.getDefaultClientBindingValue() != null)
                css.getActiveSetting().setClientBindingFromString (this.data.getDefaultClientBindingValue());
            if (this.data.getDefaultSkeletons() != null)
                css.getActiveSetting().setSkeletons (this.data.getDefaultSkeletons());
            if (this.data.getDefaultOrbValue() != null)
                css.setOrb(this.data.getDefaultOrbValue());
        }
    }
    
    
    private WizardDescriptor.Panel getBindingDetailsPanel () {
        if (this.bindingMethodDetailsPanel == null)
            this.bindingMethodDetailsPanel = new BindingDetailsPanel();
        return  this.bindingMethodDetailsPanel;
    }
    
    
    private WizardDescriptor.Panel getRootInterfacePanel() {
        if (this.rootInterfacePanel == null) {
            this.rootInterfacePanel = new RootInterface();
        }
        return this.rootInterfacePanel;
    }
    
}
