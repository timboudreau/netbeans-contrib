/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.projectsettings;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.DOMException;

import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileChangeListener;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.WeakListener;
import org.openide.util.lookup.InstanceContent;
import org.openide.xml.XMLUtil;

import org.netbeans.modules.vcscore.VcsConfigVariable;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommands;
import org.netbeans.modules.vcs.advanced.commands.UserCommandIO;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;
import org.netbeans.modules.vcs.advanced.variables.VariableIO;

/**
 *
 * @author  Martin Entlicher
 */
public class CommandLineVcsFileSystemInstance extends Object implements InstanceCookie.Of, FileChangeListener {
    
    /**
     * The FS Settings file extension.
     */
    public static final String SETTINGS_EXT = "xml"; // NOI18N
    
    public static final String SETTINGS_ROOT_ELEM = "fssettings";               // NOI18N
    public static final String FS_PROPERTIES_ELEM = "fsproperties";               // NOI18N
    public static final String FS_PROPERTY_ELEM = "property";               // NOI18N
    public static final String PROPERTY_NAME_ATTR = "name";               // NOI18N
    public static final String PROPERTY_VALUE_TAG = "value";               // NOI18N

    /** The DTD for a configuration profile. */
    public static final String PUBLIC_ID = "-//NetBeans//DTD VCS Advanced FSSettings 1.0//EN"; // NOI18N
    public static final String SYSTEM_ID = "http://www.netbeans.org/dtds/vcs-advanced-fssettings-1_0.dtd"; // NOI18N
    
    /** This variable is expected to contain the profile module information.
     * When the required module is not installed, the FS settings file is ignored. */
    public static final String MODULE_INFO_CODE_NAME_BASE_VAR = "MODULE_INFORMATION_CODE_NAME_BASE"; // NOI18N

    /** The last FS instance. */
    private WeakReference weakFsInstance = new WeakReference(null);
    private static PropertyDescriptor[] fsProperties;
    private static HashMap fsPropertiesByName;
    
    private FileObject fo;
    /** The number of how many changes to the settings file should be ignored.
     *  Increased by save task, decreesed by the file change listener */
    //private int numIgnoredFileChanges = 0;
    private Document doc;
    private InstanceContent ic;
    private FSPropertyChangeListener fsPropertyChangeListener;
    /** The profile's module information */
    private String moduleCodeNameBase = null;
    //private long timeIgnoreFileChange = 0L;
    //private static final int FILE_MODIFICATION_TIME_RANGE = 500;

    static {
        try {
            fsProperties = new PropertyDescriptor[] {
                // Stuff from AbstractFileSystem & VcsFileSystem
                new PropertyDescriptor("rootFile", CommandLineVcsFileSystem.class, "getWorkingDirectory", "setRootDirectory"),
                new PropertyDescriptor("systemName", CommandLineVcsFileSystem.class, "getSystemName", "setAdjustedSystemName"),
                new PropertyDescriptor("displayName", CommandLineVcsFileSystem.class, "getDisplayName", null),
                new PropertyDescriptor("preferredSystemName", CommandLineVcsFileSystem.class, "getPreferredSystemName", "setPreferredSystemName"),
                new PropertyDescriptor("refreshTime", CommandLineVcsFileSystem.class, "getRefreshTimeStored", "setRefreshTimeStored"),
                new PropertyDescriptor("hidden", CommandLineVcsFileSystem.class, "isHidden", "setHidden"),
                new PropertyDescriptor("readOnly", CommandLineVcsFileSystem.class, "isReadOnly", "setReadOnly"),
                new PropertyDescriptor("debug", CommandLineVcsFileSystem.class, "getDebug", "setDebug"),
                new PropertyDescriptor("password", CommandLineVcsFileSystem.class, "getPasswordStored", "setPassword"),
                new PropertyDescriptor("rememberPassword", CommandLineVcsFileSystem.class, "isRememberPassword", "setRememberPassword"),
                new PropertyDescriptor("annotationPattern", CommandLineVcsFileSystem.class, "getAnnotationPattern", "setAnnotationPattern"),
                new PropertyDescriptor("multiFilesAnnotationTypes", CommandLineVcsFileSystem.class, "getMultiFileAnnotationTypes", "setMultiFileAnnotationTypes"),
                new PropertyDescriptor("processUnimportantFiles", CommandLineVcsFileSystem.class, "isProcessUnimportantFiles", "setProcessUnimportantFiles"),
                new PropertyDescriptor("promptForVarsForEachFile", CommandLineVcsFileSystem.class, "isPromptForVarsForEachFile", "setPromptForVarsForEachFile"),
                new PropertyDescriptor("expertMode", CommandLineVcsFileSystem.class, "isExpertMode", "setExpertMode"),
                new PropertyDescriptor("userParams", CommandLineVcsFileSystem.class, "getUserParams", "setUserParams"),
                new PropertyDescriptor("numberOfFinishedCmdsToCollect", CommandLineVcsFileSystem.class, "getNumberOfFinishedCmdsToCollect", "setNumberOfFinishedCmdsToCollect"),
                new PropertyDescriptor("offLine", CommandLineVcsFileSystem.class, "isOffLine", "setOffLine"),
                new PropertyDescriptor("autoRefresh", CommandLineVcsFileSystem.class, "getAutoRefresh", "setAutoRefresh"),
                new PropertyDescriptor("hideShadowFiles", CommandLineVcsFileSystem.class, "isHideShadowFiles", "setHideShadowFiles"),
                new PropertyDescriptor("showDeadFiles", CommandLineVcsFileSystem.class, "isShowDeadFiles", "setShowDeadFiles"),
                new PropertyDescriptor("commandNotification", CommandLineVcsFileSystem.class, "isCommandNotification", "setCommandNotification"),
                new PropertyDescriptor("ignoredGarbageFiles", CommandLineVcsFileSystem.class, "getIgnoredGarbageFiles", "setIgnoredGarbageFiles"),
                new PropertyDescriptor("createBackupFiles", CommandLineVcsFileSystem.class, "isCreateBackupFiles", "setCreateBackupFiles"),
                new PropertyDescriptor("filterBackupFiles", CommandLineVcsFileSystem.class, "isFilterBackupFiles", "setFilterBackupFiles"),
                // Stuff from CommandLineVcsFileSystem
                new PropertyDescriptor("config", CommandLineVcsFileSystem.class, "getConfig", "setConfig"),
                new PropertyDescriptor("configFileName", CommandLineVcsFileSystem.class, "getConfigFileName", "setConfigFileName"),
                new PropertyDescriptor("configFileModificationTimeStr", CommandLineVcsFileSystem.class, "getConfigFileModificationTimeStr", null),
                new PropertyDescriptor("profilesOriginalCommands", CommandLineVcsFileSystem.class, "isProfilesOriginalCommands", "setProfilesOriginalCommands"),
                new PropertyDescriptor("cacheId", CommandLineVcsFileSystem.class, "getCacheId", "setCacheId"),
                new PropertyDescriptor("shortFileStatuses", CommandLineVcsFileSystem.class, "isShortFileStatuses", "setShortFileStatuses"), //NOI18N
                new PropertyDescriptor("VFSMessageLength", CommandLineVcsFileSystem.class, "getVFSMessageLength", "setVFSMessageLength"), //NOI18N
                new PropertyDescriptor("VFSShowMessage", CommandLineVcsFileSystem.class, "getVFSShowMessage", "setVFSShowMessage"), //NOI18N
                new PropertyDescriptor("VFSShowLocalFiles", CommandLineVcsFileSystem.class, "getVFSShowLocalFiles", "setVFSShowLocalFiles"), //NOI18N
                new PropertyDescriptor("VFSShowUnimportantFiles", CommandLineVcsFileSystem.class, "getVFSShowUnimportantFiles", "setVFSShowUnimportantFiles"), //NOI18N
                new PropertyDescriptor("VFSShowGarbageFiles", CommandLineVcsFileSystem.class, "getVFSShowGarbageFiles", "setVFSShowGarbageFiles"), //NOI18N
                new PropertyDescriptor("VFSShowDeadFiles", CommandLineVcsFileSystem.class, "getVFSShowDeadFiles", "setVFSShowDeadFiles"), // NOI18N
                new PropertyDescriptor("CapableCompile", CommandLineVcsFileSystem.class, "getCapableCompile", "setCapableCompile"), // NOI18N
                new PropertyDescriptor("CapableDebug", CommandLineVcsFileSystem.class, "getCapableDebug", "setCapableDebug"), // NOI18N
                new PropertyDescriptor("CapableDoc", CommandLineVcsFileSystem.class, "getCapableDoc", "setCapableDoc"), // NOI18N
                new PropertyDescriptor("CapableExecute", CommandLineVcsFileSystem.class, "getCapableExecute", "setCapableExecute") // NOI18N
                
            };
        } catch (java.beans.IntrospectionException iexc) {
            ErrorManager.getDefault().notify(iexc);
            if (fsProperties == null) {
                fsProperties = new PropertyDescriptor[0];
            }
        }
        fsPropertiesByName = new HashMap(fsProperties.length);
        for (int i = 0; i < fsProperties.length; i++) {
            fsPropertiesByName.put(fsProperties[i].getName(), fsProperties[i]);
        }
    }
    
    /** Creates new CommandLineVcsFileSystemInstance */
    public CommandLineVcsFileSystemInstance(FileObject fo, Document doc, InstanceContent ic) {
        this(fo, doc, ic, true);
    }

    /** Creates new CommandLineVcsFileSystemInstance */
    private CommandLineVcsFileSystemInstance(FileObject fo, Document doc, InstanceContent ic, boolean readModuleInfo) {
        this.fo = fo;
        this.doc = doc;
        this.ic = ic;
        fo.addFileChangeListener(WeakListener.fileChange(this, fo));
        if (readModuleInfo) {
            try {
                readModuleInfo(doc);
            } catch (DOMException dex) {}
        }
    }

    public Object instanceCreate() throws java.io.IOException, ClassNotFoundException {
        //if (!isModuleEnabled()) return new BrokenSettings(instanceName());
        //System.out.println("instanceCreate(), fo = "+fo);
        CommandLineVcsFileSystem fs;
        boolean needToReadFSProperties = false;
        synchronized (this) {
            fs = (CommandLineVcsFileSystem) weakFsInstance.get();
            //System.out.println("  fs = "+((fs == null) ? "null" : fs.getSystemName()));
            if (fs == null && fo != null) {
                fs = new CommandLineVcsFileSystem(true);
                needToReadFSProperties = true;
                weakFsInstance = new WeakReference(fs);
            }
        }
        FileObject fo = this.fo;
        if (fo == null) return null;
        if (needToReadFSProperties) {
            try {
                if (doc == null) {
                    try {
                        org.openide.loaders.XMLDataObject dobj = (org.openide.loaders.XMLDataObject) org.openide.loaders.DataObject.find(fo);
                        try {
                            doc = dobj.getDocument();
                        } catch (org.xml.sax.SAXException sexc) {
                            throw (java.io.IOException) ErrorManager.getDefault().annotate(new java.io.IOException(), sexc);
                        }
                    } catch (org.openide.loaders.DataObjectNotFoundException donfexc) {
                        throw (java.io.IOException) ErrorManager.getDefault().annotate(new java.io.IOException(), donfexc);
                    }
                    if (doc == null) return null;
                }
                readFSProperties(fs, doc);
            } catch (DOMException dexc) {
                ErrorManager.getDefault().notify(dexc);
            }
            fsPropertyChangeListener = new FSPropertyChangeListener(fo);
            fs.addPropertyChangeListener(fsPropertyChangeListener); //WeakListener.propertyChange(fsPropertyChangeListener, fs));
            //System.out.println("  PROPERTIES READ: fs = "+fs.getSystemName());
        }
        return fs;
    }
    
    public Class instanceClass() throws java.io.IOException, ClassNotFoundException {
        if (!isModuleEnabled()) return BrokenSettings.class;
        return CommandLineVcsFileSystem.class;
    }
    
    public boolean instanceOf(Class clazz) {
        if (!isModuleEnabled()) return BrokenSettings.class.isAssignableFrom(clazz);
        return (clazz.isAssignableFrom(CommandLineVcsFileSystem.class));
    }
    
    public String instanceName() {
        return org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem.class.getName();
    }
    
    public void setInstance(CommandLineVcsFileSystem fs) {
        CommandLineVcsFileSystem oldFs = (CommandLineVcsFileSystem) weakFsInstance.get();
        if (oldFs == fs) return ;
        //numIgnoredFileChanges = 0;
        weakFsInstance = new WeakReference(fs);
        if (fs != null) {
            fsPropertyChangeListener = new FSPropertyChangeListener(fo);
            fs.addPropertyChangeListener(fsPropertyChangeListener); //WeakListener.propertyChange(fsPropertyChangeListener, fs));
        } else {
            if (oldFs != null) {
                oldFs.removePropertyChangeListener(fsPropertyChangeListener);
            }
            fsPropertyChangeListener = null;
        }
    }
    
    public static DataObject createVcsInstanceDataObject(
        FileObject folder, CommandLineVcsFileSystem fs, String settingsName) throws IOException {

        return Creator.createVcsInstanceDataObject(folder, fs, settingsName);
    }
        
    private static DataObject storeVcsSettings(
        FileObject folder, CommandLineVcsFileSystem fs, String settingsName) throws IOException {

            FileObject fo;
            fo = folder.createData(settingsName, SETTINGS_EXT);
            Document doc = createEmptyFSPropertiesDocument();
            try {
                writeFSProperties(fs, doc);
            } catch (org.w3c.dom.DOMException dExc) {
                ErrorManager.getDefault().notify(dExc);
            }
            FileLock lock = fo.lock();
            OutputStream out = fo.getOutputStream(lock);
            try {
                org.openide.xml.XMLUtil.write(doc, out, null);
            } finally {
                out.close();
                lock.releaseLock();
            }
            try {
                DataObject myXMLDataObject = DataObject.find(fo);
                //((CommandLineVcsFileSystemInstance) myXMLDataObject.getCookie(org.openide.cookies.InstanceCookie.Of.class)).setInstance(this);
                org.openide.util.Lookup instanceLookup = org.openide.loaders.Environment.find(myXMLDataObject);
                CommandLineVcsFileSystemInstance myInstance =
                    (CommandLineVcsFileSystemInstance) instanceLookup.lookup(org.openide.cookies.InstanceCookie.class);
                //myInstance.setIgnoreSubsequentFileChange(System.currentTimeMillis());
                myInstance.setInstance(fs);
                //firePropertyChange("writeAllProperties", null, null);
                //System.out.println("createInstanceDataObject() = "+myXMLDataObject);
                return myXMLDataObject;
            } catch (DataObjectNotFoundException donfExc) {
                throw new IOException(donfExc.getLocalizedMessage());
            }
    }
    
    /** Ignore FileObject changes done right after this time.
     *
    public void setIgnoreSubsequentFileChange(long time) {
        this.timeIgnoreFileChange = time + FILE_MODIFICATION_TIME_RANGE;
        System.out.println("timeIgnoreFileChange = "+timeIgnoreFileChange);
    }
     */
    
    private final Object MODULE_LST_LOCK = new Object();
    /** due to asynchronous firing of PROP_ENABLED from ModuleInfo implementation in core. */
    private boolean wasEnabled;
    /** listen on ModuleInfo if module is enabled/disabled. */
    private PropertyChangeListener moduleListener;

    private boolean isModuleEnabled() {
        if (moduleCodeNameBase == null) return true;
        final ModuleInfo m = getModule(moduleCodeNameBase);
        if (m == null) return false;
        synchronized (MODULE_LST_LOCK) {
            if (moduleListener == null) {
                wasEnabled = m.isEnabled();
                moduleListener = new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        InstanceContent ic = CommandLineVcsFileSystemInstance.this.ic;
                        if (ic == null) {
                            synchronized (MODULE_LST_LOCK) {
                                moduleListener = null; // I will be detached by GC.
                            }
                            return ;
                        }
                        if (ModuleInfo.PROP_ENABLED.equals(evt.getPropertyName()) &&
                            Boolean.FALSE.equals(evt.getNewValue())) {
                            // a module has been disabled, use full checks
                            //aModuleHasBeenChanged = true;
                        }
                        
                        if (wasEnabled != m.isEnabled() && evt.getPropertyName().equals(ModuleInfo.PROP_ENABLED)) {
                            wasEnabled = m.isEnabled();
                            setInstance(null);
                            if (!wasEnabled) {
                                ic.remove((InstanceCookie) CommandLineVcsFileSystemInstance.this);
                            } else {
                                CommandLineVcsFileSystemInstance newFSInstance = new CommandLineVcsFileSystemInstance(fo, doc, ic, false);
                                newFSInstance.moduleCodeNameBase = CommandLineVcsFileSystemInstance.this.moduleCodeNameBase;
                                ic.add(newFSInstance);
                            }
                        }
                    }
                };
                m.addPropertyChangeListener(
                    WeakListener.propertyChange(moduleListener, m));
            }
        }
        if (!m.isEnabled()) return false;
        // is release number ok?
        //if (recog.getCodeNameRelease() > m.getCodeNameRelease()) return false;
        // is specification ok?
        //return recog.getSpecificationVersion() == null ||
        //    recog.getSpecificationVersion().compareTo(m.getSpecificationVersion()) <= 0;
        return true;
    }
    
    /** all modules <code bas name, ModuleInfo> */
    private HashMap modules = null;
    private static final Object MODULES_LOCK = new Object();
    
    /** find module info.
     * @param codeBaseName module code base name (without revision)
     * @return module info or null
     */
    private ModuleInfo getModule(final String codeBaseName) {
        synchronized (MODULES_LOCK) {
            if (modules == null) {
                Lookup.Result modulesResult =
                    Lookup.getDefault().lookup(new Lookup.Template(ModuleInfo.class));
                modulesResult.addLookupListener(new LookupListener() {
                    public void resultChanged(LookupEvent ev) {
                        InstanceContent ic = CommandLineVcsFileSystemInstance.this.ic;
                        if (ic == null) {
                            synchronized (MODULES_LOCK) {
                                ((Lookup.Result) ev.getSource()).removeLookupListener(this);
                                modules = null;
                            }
                            return ;
                        }
                        Collection l = ((Lookup.Result) ev.getSource()).allInstances();
                        boolean wasEnabled = isModuleEnabled();
                        synchronized (MODULES_LOCK) {
                            modules = fillModules(l);
                        }
                        if (wasEnabled != isModuleEnabled()) {
                            setInstance(null);
                            if (wasEnabled) {
                                ic.remove((InstanceCookie) CommandLineVcsFileSystemInstance.this);
                            } else {
                                CommandLineVcsFileSystemInstance newFSInstance = new CommandLineVcsFileSystemInstance(fo, doc, ic, false);
                                newFSInstance.moduleCodeNameBase = CommandLineVcsFileSystemInstance.this.moduleCodeNameBase;
                                ic.add(newFSInstance);
                            }
                        }
                    }
                });
                modules = fillModules(modulesResult.allInstances());
            }
            return (ModuleInfo) modules.get(codeBaseName);
        }
    }
    
    /** recompute accessible modules. */
    private static HashMap fillModules(Collection l) {
        HashMap m = new HashMap((l.size() << 2) / 3 + 1);
        Iterator it = l.iterator();
        while (it.hasNext()) {
            ModuleInfo mi = (ModuleInfo) it.next();
            m.put(mi.getCodeNameBase(), mi);
        }
        return m;
    }
    
    private void readModuleInfo(Document doc) throws DOMException {
        Element rootElem = doc.getDocumentElement();
        NodeList configList = rootElem.getElementsByTagName(VariableIO.CONFIG_ROOT_ELEM);
        Collection variables = null;
        if (configList.getLength() > 0) {
            Element configNode = (Element) configList.item(0);
            NodeList varList = configNode.getElementsByTagName(VariableIO.VARIABLES_TAG);
            if (varList.getLength() > 0) {
                Node varsNode = varList.item(0);
                variables = VariableIO.getVariables(varsNode.getChildNodes()).getUnconditionedVariables();
            }
        }
        if (variables != null) {
            for (Iterator it = variables.iterator(); it.hasNext(); ) {
                VcsConfigVariable var = (VcsConfigVariable) it.next();
                if (MODULE_INFO_CODE_NAME_BASE_VAR.equals(var.getName())) {
                    moduleCodeNameBase = var.getValue();
                }
            }
        }
    }
    
    private static void readFSProperties(CommandLineVcsFileSystem fs, Document doc) throws DOMException {
        //System.out.println("readFSProperties("+fs.getSystemName()+")");
        //Document vcDoc = XMLUtil.createDocument(VariableIO.CONFIG_ROOT_ELEM, null, VariableIO.PUBLIC_ID, VariableIO.SYSTEM_ID);
        Element rootElem = doc.getDocumentElement();
        NodeList configList = rootElem.getElementsByTagName(VariableIO.CONFIG_ROOT_ELEM);
        if (configList.getLength() > 0) {
            Element configNode = (Element) configList.item(0);
            /* THERE ARE PROBLEMS WITH IMPORT OF NODES (THE NODE ATTRIBUTES ARE NOT IMPORTED)
             * ANYBODY KNOWS WHY ??
            NodeList configChildren = configNode.getChildNodes();
            for (int i = 0; i < configChildren.getLength(); i++) {
                Node importedNode = vcDoc.importNode(configChildren.item(i), true);
                System.out.println("  imported Node = "+importedNode);
                vcDoc.getDocumentElement().appendChild(importedNode);
            }
            try {
                XMLUtil.write(doc, new java.io.FileOutputStream("/home/me97925/testOrig.xml"), null);
                XMLUtil.write(vcDoc, new java.io.FileOutputStream("/home/me97925/testVC.xml"), null);
            } catch (java.io.IOException exc) {
                TopManager.getDefault().notifyException(exc);
            }
            fs.setVariables(VariableIO.readVariables(vcDoc));
            fs.setCommands(UserCommandIO.readCommands(vcDoc));
             */
            NodeList varList = configNode.getElementsByTagName(VariableIO.VARIABLES_TAG);
            if (varList.getLength() > 0) {
                Node varsNode = varList.item(0);
                fs.setVariables(new Vector(VariableIO.getVariables(varsNode.getChildNodes()).getUnconditionedVariables()));
            }
            NodeList labelList = configNode.getElementsByTagName(VariableIO.LABEL_TAG);
            Node labelNode = null;
            if (labelList.getLength() > 0) {
                labelNode = labelList.item(0);
            }
            NodeList commandsList = configNode.getElementsByTagName(UserCommandIO.COMMANDS_TAG);
            if (commandsList.getLength() > 0) {
                Node commands = commandsList.item(0);
                commandsList = commands.getChildNodes();
            } else commandsList = null;
            fs.setCommands(UserCommandIO.readCommands(labelNode, commandsList, fs).getCommands());
        }
        readAdditionalFSProperties(fs, doc);
    }
    
    public static void writeFSProperties(CommandLineVcsFileSystem fs, Document doc) throws DOMException {
        //System.out.println("writeFSProperties("+fs.getSystemName()+")");
        Document vcDoc = XMLUtil.createDocument(VariableIO.CONFIG_ROOT_ELEM, null, VariableIO.PUBLIC_ID, VariableIO.SYSTEM_ID);
        VariableIO.writeVariables(vcDoc, fs.getConfig(),
                                  new ConditionedVariables(fs.getVariables(),
                                                           java.util.Collections.EMPTY_MAP,
                                                           java.util.Collections.EMPTY_MAP));
        UserCommandIO.writeCommands(vcDoc, fs.getCommands());
        Node importedNode = doc.importNode(vcDoc.getDocumentElement(), true);
        doc.getDocumentElement().appendChild(importedNode);
        writeAdditionalFSProperties(fs, doc);
    }
    
    public static Document createEmptyFSPropertiesDocument() {
        return XMLUtil.createDocument(SETTINGS_ROOT_ELEM, null, PUBLIC_ID, SYSTEM_ID);
    }
    
    private static void readAdditionalFSProperties(CommandLineVcsFileSystem fs, Document doc) throws DOMException {
        Element rootElem = doc.getDocumentElement();
        NodeList propertiesesList = rootElem.getElementsByTagName(FS_PROPERTIES_ELEM);
        if (propertiesesList.getLength() > 0) {
            Element propertiesNode = (Element) propertiesesList.item(0);
            NodeList propertiesList = propertiesNode.getElementsByTagName(FS_PROPERTY_ELEM);
            for (int i = 0; i < propertiesList.getLength(); i++) {
                Node property = propertiesList.item(i);
                NamedNodeMap propertyAttrs = property.getAttributes();
                Node nameAttr = propertyAttrs.getNamedItem(PROPERTY_NAME_ATTR);
                if (nameAttr == null) continue;
                String name = nameAttr.getNodeValue();
                PropertyDescriptor propertyDesc = (PropertyDescriptor) fsPropertiesByName.get(name);
                if (propertyDesc == null) continue;
                String value = "";
                
                NodeList valueList = property.getChildNodes();
                int m = valueList.getLength();
                for (int j = 0; j < m; j++) {
                    Node valueNode = valueList.item(j);
                    if (PROPERTY_VALUE_TAG.equals(valueNode.getNodeName())) {
                        NodeList textList = valueNode.getChildNodes();
                        for (int itl = 0; itl < textList.getLength(); itl++) {
                            Node subNode = textList.item(itl);
                            if (subNode instanceof Text) {
                                Text textNode = (Text) subNode;
                                value += textNode.getData();
                            }
                            if (subNode instanceof EntityReference) {
                                EntityReference entityNode = (EntityReference) subNode;
                                NodeList entityList = entityNode.getChildNodes();
                                for (int iel = 0; iel < entityList.getLength(); iel++) {
                                    Node entitySubNode = entityList.item(iel);
                                    if (entitySubNode instanceof Text) {
                                        Text textEntityNode = (Text) entitySubNode;
                                        value += textEntityNode.getData();
                                    }
                                }
                            }
                        }
                    }
                }

                Object realValue = getFSPropertyValue(value, propertyDesc.getPropertyType());
                Method write = propertyDesc.getWriteMethod();
                if (write != null) {
                    try {
                        //System.out.println("propertyClass = "+propertyDesc.getPropertyType());
                        //System.out.println("realValue = "+realValue+", class = "+realValue.getClass());
                        write.invoke(fs, new Object[] { realValue });
                    } catch (IllegalAccessException iaexc) {
                        ErrorManager.getDefault().notify(iaexc);
                    } catch (IllegalArgumentException iarexc) {
                        ErrorManager.getDefault().notify(iarexc);
                    } catch (InvocationTargetException itexc) {
                        ErrorManager.getDefault().notify(itexc);
                    }
                }
            }
        }
    }
    
    private static void writeAdditionalFSProperties(CommandLineVcsFileSystem fs, Document doc) throws DOMException {
        Element rootElem = doc.getDocumentElement(); //doc.createElement(CONFIG_ROOT_ELEM);
        //doc.appendChild(rootElem);
        Element fsPropsNode = doc.createElement(FS_PROPERTIES_ELEM);
        for (int i = 0; i < fsProperties.length; i++) {
            String name = fsProperties[i].getName();
            Method read = fsProperties[i].getReadMethod();
            if (read != null) {
                Object value = null;
                try {
                    value = read.invoke(fs, new Object[0]);
                } catch (IllegalAccessException iaexc) {
                    ErrorManager.getDefault().notify(iaexc);
                } catch (IllegalArgumentException iarexc) {
                    ErrorManager.getDefault().notify(iarexc);
                } catch (InvocationTargetException itexc) {
                    ErrorManager.getDefault().notify(itexc);
                }
                if (value != null) {
                    String valueStr = getFSPropertyValueStr(value);
                    Element fsProperty = doc.createElement(FS_PROPERTY_ELEM);
                    fsProperty.setAttribute(PROPERTY_NAME_ATTR, name);
                    Element valueElem = doc.createElement(PROPERTY_VALUE_TAG);
                    Text valueNode = doc.createTextNode(valueStr);
                    valueElem.appendChild(valueNode);
                    fsProperty.appendChild(valueElem);
                    fsPropsNode.appendChild(fsProperty);
                }
            }
        }
        rootElem.appendChild(fsPropsNode);
    }
    
    private static String getFSPropertyValueStr(Object value) {
        Class type = value.getClass();
        if (java.io.File.class.equals(type)) {
            return ((java.io.File) value).getAbsolutePath();
        } else if (int[].class.equals(type)) {
            int[] nums = (int[]) value;
            StringBuffer buff = new StringBuffer();
            for (int i = 0; i < nums.length; i++) {
                buff.append(Integer.toString(nums[i]) + ((i < nums.length - 1) ? "/" : ""));
            }
            return buff.toString();
        } else if (String[].class.equals(type)) {
            return UserCommandIO.convertStringArray2String((String[]) value);
        } else {
            return value.toString();
        }
    }

    private static Object getFSPropertyValue(String valueStr, Class type) {
        if (Boolean.TYPE.equals(type)) {
            return Boolean.valueOf(valueStr);
        } else if (Integer.TYPE.equals(type)) {
            Integer intObject;
            try {
                int intValue = Integer.parseInt(valueStr);
                intObject = new Integer(intValue);
            } catch (NumberFormatException exc) {
                intObject = null;
            }
            return intObject;
        } else if (Long.TYPE.equals(type)) {
            Long longObject;
            try {
                long longValue = Long.parseLong(valueStr);
                longObject = new Long(longValue);
            } catch (NumberFormatException exc) {
                longObject = null;
            }
            return longObject;
        } else if (int[].class.equals(type)) {
            StringTokenizer numbers = new StringTokenizer("/");
            int[] nums = new int[numbers.countTokens()];
            for (int i = 0; numbers.hasMoreTokens(); i++) {
                try {
                    nums[i] = Integer.parseInt(numbers.nextToken());
                } catch (NumberFormatException exc) {}
            }
            return nums;
        } else if (String[].class.equals(type)) {
            return UserCommandIO.convertString2StringArray(valueStr);
        } else if (java.io.File.class.equals(type)) {
            return new java.io.File(valueStr);
        } else {
            return valueStr;
        }
    }

    public void fileDeleted(org.openide.filesystems.FileEvent fileEvent) {
        //FileObject fo = fileEvent.getFile();
        //fsInstances.remove(fo.getPackageNameExt('/', '.'));
        //fo.removeFileChangeListener(this);
        synchronized (this) {
            CommandLineVcsFileSystem fs = (CommandLineVcsFileSystem) weakFsInstance.get();
            if (fs != null) {
                fs.removePropertyChangeListener(fsPropertyChangeListener);
                weakFsInstance = new WeakReference(null);
                fsPropertyChangeListener = null;
            }
        }
        fo = null;
        doc = null;
        ic = null;
    }
    
    public void fileFolderCreated(org.openide.filesystems.FileEvent fileEvent) {
    }
    
    public void fileDataCreated(org.openide.filesystems.FileEvent fileEvent) {
    }
    
    public void fileAttributeChanged(org.openide.filesystems.FileAttributeEvent fileAttributeEvent) {
    }
    
    public void fileRenamed(org.openide.filesystems.FileRenameEvent fileRenameEvent) {
    }
    
    public void fileChanged(org.openide.filesystems.FileEvent fileEvent) {
        //System.out.println("fileChanged("+fileEvent.getFile()+"), numIgnoredFileChanges = "+numIgnoredFileChanges);
        /*
        if (numIgnoredFileChanges > 0) {
            numIgnoredFileChanges--;
            //System.out.println("  IGNORED.");
            return ;
        }
        if (timeIgnoreFileChange > 0) {
            if (fileEvent.getFile().lastModified().getTime() <= timeIgnoreFileChange) {
                System.out.println("  IGNORED - time: "+fileEvent.getFile().lastModified().getTime()+" <= "+timeIgnoreFileChange);
                return ;
            } else {
                System.out.println("  Time Expired.");
                timeIgnoreFileChange = 0L;
            }
        }
         */
        if (Creator.isFiredFromMe (fileEvent) ||  getSaver().isFiredFromMe (fileEvent)) {
            //System.out.println("  IGNORED.");
            return;
        }
        //System.out.println("  NOT IGNORED.");
        setInstance(null);
        if (fsPropertyChangeListener != null) {
            RequestProcessor.Task task = fsPropertyChangeListener.getWriteTask();
            // We do not want to overwrite the changed file
            if (task != null) task.cancel();
        }
        doc = null;
        ic.remove(this);
        ic.add(this);
    }
    
    public void waitToFinishSaveTasks() throws InterruptedException {
        if (fsPropertyChangeListener != null) fsPropertyChangeListener.waitToFinishSaveTasks();
    }

    private static int TASK_SCHEDULE_DELAY = 1000;

    private class FSPropertyChangeListener extends Object implements PropertyChangeListener {
        //private Reference fs;
        private FileObject fo;
        private FileLock lock;
        private RequestProcessor.Task writeTask = null;
        private volatile boolean reSchedule = false;
        
        public FSPropertyChangeListener(FileObject fo) {
            this.fo = fo;
        }
        
        public RequestProcessor.Task getWriteTask() {
            return writeTask;
        }
        
        public void waitToFinishSaveTasks() throws InterruptedException {
            synchronized (this) {
                if (writeTask == null) return ;
                while (!writeTask.isFinished()) {
                    wait();
                }
            }
        }
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            // Ignore valid property changes
            if (CommandLineVcsFileSystem.PROP_VALID.equals(propertyChangeEvent.getPropertyName())) return ;
            //System.out.println("Property '"+propertyChangeEvent.getPropertyName()+"' changed.");
            synchronized (this) {
                if (lock == null) {
                    try {
                        lock = fo.lock();
                    } catch (IOException ioex) {
                        ErrorManager.getDefault().notify(ioex);
                    }
                }
                if (writeTask == null) {
                    writeTask = createWriteTask();
                }
                if (writeTask.isFinished()) {
                    writeTask.schedule(TASK_SCHEDULE_DELAY);
                } else {
                    reSchedule = true;
                }
            }
        }
        
        private RequestProcessor.Task createWriteTask() {
            RequestProcessor.Task task = RequestProcessor.postRequest(new Runnable() {
                public void run() {
                    try {
                        if (fo == null || lock == null) return ;
                        getSaver().save(fo, lock);
                        /*
                        fo.getFileSystem().runAtomicAction(new org.openide.filesystems.FileSystem.AtomicAction() {
                            public void run() throws java.io.IOException {
                                CommandLineVcsFileSystem fs = (CommandLineVcsFileSystem) weakFsInstance.get();
                                if (fs != null) {
                                    Document doc = createEmptyFSPropertiesDocument();
                                    writeFSProperties(fs, doc);
                                    FileLock lock = null;
                                    java.io.OutputStream out = null;
                                    try {
                                        lock = fo.lock();
                                        out = fo.getOutputStream(lock);
                                        XMLUtil.write(doc, out, null);
                                        //System.out.println("  written to "+fo+", File = "+org.openide.filesystems.FileUtil.toFile(fo));
                                    } finally {
                                        try {
                                            if (out != null) {
                                                numIgnoredFileChanges++;
                                                //System.out.println("  numIgnoredFileChanges = "+numIgnoredFileChanges);
                                                out.close();
                                            }
                                        } catch (java.io.IOException ioexc) {}
                                        if (lock != null) lock.releaseLock();
                                    }
                                }
                            }
                        });
                         */
                    } catch (java.io.IOException ioExc) {
                        ErrorManager.getDefault().notify(ioExc);
                    }
                }
            }, TASK_SCHEDULE_DELAY);
            task.addTaskListener(new TaskListener() {
                public void taskFinished(Task task2) {
                    synchronized (FSPropertyChangeListener.this) {
                        if (reSchedule) {
                            writeTask.schedule(TASK_SCHEDULE_DELAY);
                            reSchedule = false;
                        } else {
                            FSPropertyChangeListener.this.notifyAll();
                            if (lock != null) lock.releaseLock();
                            lock = null;
                        }
                    }
                }
            });
            return task;
        }
        
    }
    
    private static class Creator implements org.openide.filesystems.FileSystem.AtomicAction {
        
        private static final Creator me = new Creator();
        
        private FileObject folder = null;
        private CommandLineVcsFileSystem fs = null;
        private String settingsName = null;
        private DataObject result = null;

        private Creator() {
        }
        
        /** Executed when it is guaranteed that no events about changes
         * in filesystems will be notified.
         *
         * @exception IOException if there is an error during execution
         */
        public void run() throws IOException {
            result = storeVcsSettings(folder, fs, settingsName);
        }
        
        public static DataObject createVcsInstanceDataObject(
            FileObject folder, CommandLineVcsFileSystem fs, String settingsName) throws IOException {

            synchronized (me) {
                me.folder = folder;
                me.fs = fs;            
                me.settingsName = settingsName;
                
                folder.getFileSystem().runAtomicAction(me);
                
                me.folder = null;
                me.fs = null;            
                me.settingsName = null;
                DataObject result = me.result;
                me.result = null;
                return result;
            }
        }
        
        /** is file event originated by this Creator? */
        public static boolean isFiredFromMe (org.openide.filesystems.FileEvent fe)  {
            return fe.firedFrom(me);
        }        

    }
    
    private final Saver saver = new Saver (this);
    /** get the Saver support */
    private Saver getSaver() {
        return saver;
    }
    
    /** Support for storing instances allowing identify the origin of file events 
     * fired as a consequence of this storing.
     */
    private static class Saver implements org.openide.filesystems.FileSystem.AtomicAction {
        
        private CommandLineVcsFileSystemInstance vfsInstance;
        private FileObject fo = null;
        private FileLock lock = null;
        
        private Saver(CommandLineVcsFileSystemInstance vfsInstance) {
            this.vfsInstance = vfsInstance;
        }
        
        /** Executed when it is guaranteed that no events about changes
         * in filesystems will be notified.
         *
         * @exception IOException if there is an error during execution
         */
        public void run() throws IOException {
            CommandLineVcsFileSystem fs = (CommandLineVcsFileSystem) vfsInstance.weakFsInstance.get();
            if (fs != null) {
                Document doc = createEmptyFSPropertiesDocument();
                writeFSProperties(fs, doc);
                java.io.OutputStream out = null;
                try {
                    out = fo.getOutputStream(lock);
                    XMLUtil.write(doc, out, null);
                    //System.out.println("  written to "+fo+", File = "+org.openide.filesystems.FileUtil.toFile(fo));
                } finally {
                    try {
                        if (out != null) {
                            //numIgnoredFileChanges++;
                            //System.out.println("  numIgnoredFileChanges = "+numIgnoredFileChanges);
                            out.close();
                        }
                    } catch (java.io.IOException ioexc) {}
                }
            }
        }
        
        /** write down buffer to file
         */
        public void save (FileObject fo, FileLock lock) throws IOException {
            synchronized (this) {
                this.fo = fo;
                this.lock = lock;
                fo.getFileSystem().runAtomicAction(this);
                this.fo = null;
            }
        }
        /** is file event originated by this Saver? */
        public boolean isFiredFromMe (org.openide.filesystems.FileEvent fe)  {
            return fe.firedFrom(this);
        }        
        
    }
    
    /** Indicates settings from uninstalled module. */
    final static class BrokenSettings {
        String name;
        public BrokenSettings(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
    
}
