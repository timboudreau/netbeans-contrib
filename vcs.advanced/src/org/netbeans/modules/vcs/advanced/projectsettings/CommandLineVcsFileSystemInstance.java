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

package org.netbeans.modules.vcs.advanced.projectsettings;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.DOMException;

import org.openide.TopManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileChangeListener;
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

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcs.advanced.commands.UserCommandIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIO;

/**
 *
 * @author  Martin Entlicher
 */
public class CommandLineVcsFileSystemInstance extends Object implements InstanceCookie.Of, FileChangeListener {
    
    public static final String SETTINGS_ROOT_ELEM = "fssettings";               // NOI18N
    public static final String FS_PROPERTIES_ELEM = "fsproperties";               // NOI18N
    public static final String FS_PROPERTY_ELEM = "property";               // NOI18N
    public static final String PROPERTY_NAME_ATTR = "name";               // NOI18N
    public static final String PROPERTY_VALUE_TAG = "value";               // NOI18N

    /** The DTD for a configuration profile. */
    public static final String PUBLIC_ID = "-//NetBeans//DTD VCS Advanced FSSettings 1.0//EN"; // NOI18N
    public static final String SYSTEM_ID = "http://www.netbeans.org/dtds/vcs-advanced-fssettings-1_0.dtd"; // NOI18N

    /** The last FS instance. */
    private WeakReference weakFsInstance = new WeakReference(null);
    private static PropertyDescriptor[] fsProperties;
    private static HashMap fsPropertiesByName;
    
    private FileObject fo;
    /** The number of how many changes to the settings file should be ignored.
     *  Increased by save task, decreesed by the file change listener */
    private int numIgnoredFileChanges = 0;
    private Document doc;
    private InstanceContent ic;
    private FSPropertyChangeListener fsPropertyChangeListener;
    private long timeIgnoreFileChange = 0L;
    private static final int FILE_MODIFICATION_TIME_RANGE = 5000;

    static {
        try {
            fsProperties = new PropertyDescriptor[] {
                // Stuff from AbstractFileSystem & VcsFileSystem
                new PropertyDescriptor("rootFile", CommandLineVcsFileSystem.class, "getWorkingDirectory", "setRootDirectory"),
                new PropertyDescriptor("systemName", CommandLineVcsFileSystem.class, "getSystemName", "setAdjustedSystemName"),
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
                new PropertyDescriptor("cacheId", CommandLineVcsFileSystem.class, "getCacheId", "setCacheId"),
                new PropertyDescriptor("shortFileStatuses", CommandLineVcsFileSystem.class, "isShortFileStatuses", "setShortFileStatuses"), //NOI18N
                new PropertyDescriptor("VFSMessageLength", CommandLineVcsFileSystem.class, "getVFSMessageLength", "setVFSMessageLength"), //NOI18N
                new PropertyDescriptor("VFSShowMessage", CommandLineVcsFileSystem.class, "getVFSShowMessage", "setVFSShowMessage"), //NOI18N
                new PropertyDescriptor("VFSShowLocalFiles", CommandLineVcsFileSystem.class, "getVFSShowLocalFiles", "setVFSShowLocalFiles"), //NOI18N
                new PropertyDescriptor("VFSShowUnimportantFiles", CommandLineVcsFileSystem.class, "getVFSShowUnimportantFiles", "setVFSShowUnimportantFiles"), //NOI18N
                new PropertyDescriptor("VFSShowGarbageFiles", CommandLineVcsFileSystem.class, "getVFSShowGarbageFiles", "setVFSShowGarbageFiles") //NOI18N
                
            };
        } catch (java.beans.IntrospectionException iexc) {
            TopManager.getDefault().notifyException(iexc);
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
        this.fo = fo;
        this.doc = doc;
        this.ic = ic;
        fo.addFileChangeListener(WeakListener.fileChange(this, fo));
    }

    public Object instanceCreate() throws java.io.IOException, ClassNotFoundException {
        //if (!isModuleEnabled()) return new BrokenSettings(instanceName());
        //System.out.println("instanceCreate(), fo = "+fo);
        CommandLineVcsFileSystem fs;
        boolean needToReadFSProperties = false;
        synchronized (this) {
            fs = (CommandLineVcsFileSystem) weakFsInstance.get();
            //System.out.println("  fs = "+((fs == null) ? "null" : fs.getSystemName()));
            if (fs == null) {
                fs = new CommandLineVcsFileSystem();
                needToReadFSProperties = true;
                weakFsInstance = new WeakReference(fs);
            }
        }
        if (needToReadFSProperties) {
            try {
                if (doc == null) {
                    try {
                        org.openide.loaders.XMLDataObject dobj = (org.openide.loaders.XMLDataObject) org.openide.loaders.DataObject.find(fo);
                        try {
                            doc = dobj.getDocument();
                        } catch (org.xml.sax.SAXException sexc) {
                            throw (java.io.IOException) TopManager.getDefault().getErrorManager().annotate(new java.io.IOException(), sexc);
                        }
                    } catch (org.openide.loaders.DataObjectNotFoundException donfexc) {
                        throw (java.io.IOException) TopManager.getDefault().getErrorManager().annotate(new java.io.IOException(), donfexc);
                    }
                    if (doc == null) return null;
                }
                readFSProperties(fs, doc);
            } catch (DOMException dexc) {
                TopManager.getDefault().notifyException(dexc);
            }
            fsPropertyChangeListener = new FSPropertyChangeListener(fo);
            fs.addPropertyChangeListener(fsPropertyChangeListener); //WeakListener.propertyChange(fsPropertyChangeListener, fs));
            //System.out.println("  PROPERTIES READ: fs = "+fs.getSystemName());
        }
        return fs;
    }
    
    public Class instanceClass() throws java.io.IOException, ClassNotFoundException {
        //if (!isModuleEnabled()) return BrokenSettings.class;
        return CommandLineVcsFileSystem.class;
    }
    
    public boolean instanceOf(Class clazz) {
        //if (!isModuleEnabled()) return BrokenSettings.class.isAssignableFrom(clazz);
        return (clazz.isAssignableFrom(CommandLineVcsFileSystem.class));
    }
    
    public String instanceName() {
        return org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem.class.getName();
    }
    
    public void setInstance(CommandLineVcsFileSystem fs) {
        CommandLineVcsFileSystem oldFs = (CommandLineVcsFileSystem) weakFsInstance.get();
        if (oldFs == fs) return ;
        numIgnoredFileChanges = 0;
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
    
    /** Ignore FileObject changes done right after this time.
     */
    public void setIgnoreSubsequentFileChange(long time) {
        this.timeIgnoreFileChange = time + FILE_MODIFICATION_TIME_RANGE;
    }
    
    private final Object MODULE_LST_LOCK = new Object();
    /** due to asynchronous firing of PROP_ENABLED from ModuleInfo implementation in core. */
    private boolean wasEnabled;
    /** listen on ModuleInfo if module is enabled/disabled. */
    private PropertyChangeListener moduleListener;

    private boolean isModuleEnabled() {
        final ModuleInfo m = getModuleInfo(CommandLineVcsFileSystem.class);
        if (m == null) return false;
        /*
        synchronized (MODULE_LST_LOCK) {
            if (moduleListener == null) {
                wasEnabled = m.isEnabled();
                moduleListener = new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
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
                                ic.add((InstanceCookie) new CommandLineVcsFileSystemInstance(fo, doc, ic));
                            }
                        }
                    }
                };
                m.addPropertyChangeListener(
                    WeakListener.propertyChange(moduleListener, m));
            }
        }
        */
        if (!m.isEnabled()) return false;
        // is release number ok?
        //if (recog.getCodeNameRelease() > m.getCodeNameRelease()) return false;
        // is specification ok?
        //return recog.getSpecificationVersion() == null ||
        //    recog.getSpecificationVersion().compareTo(m.getSpecificationVersion()) <= 0;
        return true;
    }
    
    /** all modules <code bas name, ModuleInfo> */
    private static HashMap modules = null;
    /** lookup query to find out all modules */
    private static Lookup.Result modulesResult = null;
    private static final Object MODULES_LOCK = new Object();
    
    /** find module info.
     * @param codeBaseName module code base name (without revision)
     * @return module info or null
     */
    private static ModuleInfo getModule(String codeBaseName) {
        Collection l = null;
        if (modules == null) {
            l = getModulesResult().allInstances();
        }
        synchronized (MODULES_LOCK) {
            if (modules == null) fillModules(l);
            return (ModuleInfo) modules.get(codeBaseName);
        }
    }
    
    private static Lookup.Result getModulesResult() {
        synchronized (MODULES_LOCK) {
            if (modulesResult == null) {
                modulesResult = Lookup.getDefault().
                lookup(new Lookup.Template(ModuleInfo.class));
                modulesResult.addLookupListener(new LookupListener() {
                    public void resultChanged(LookupEvent ev) {
                        Collection l = getModulesResult().allInstances();
                        synchronized (MODULES_LOCK) {
                            fillModules(l);
                        }
                    }
                });
            }
            return modulesResult;
        }
    }
    
    /** recompute accessible modules. */
    private static void fillModules(Collection l) {
        HashMap m = new HashMap((l.size() << 2) / 3 + 1);
        Iterator it = l.iterator();
        while (it.hasNext()) {
            ModuleInfo mi = (ModuleInfo) it.next();
            m.put(mi.getCodeNameBase(), mi);
        }
        modules = m;
    }
    
    private static ModuleInfo getModuleInfo(Class clazz) {
        Iterator it = getModulesResult().allInstances().iterator();
        while (it.hasNext()) {
            ModuleInfo mi = (ModuleInfo) it.next();
            if (mi.owns(clazz)) return mi;
        }
        return null;
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
                fs.setVariables(VariableIO.getVariables(varsNode.getChildNodes()));
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
            fs.setCommands(UserCommandIO.readCommands(labelNode, commandsList));
        }
        readAdditionalFSProperties(fs, doc);
    }
    
    public static void writeFSProperties(CommandLineVcsFileSystem fs, Document doc) throws DOMException {
        //System.out.println("writeFSProperties("+fs.getSystemName()+")");
        Document vcDoc = XMLUtil.createDocument(VariableIO.CONFIG_ROOT_ELEM, null, VariableIO.PUBLIC_ID, VariableIO.SYSTEM_ID);
        VariableIO.writeVariables(vcDoc, fs.getConfig(), fs.getVariables());
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
                        TopManager.getDefault().notifyException(iaexc);
                    } catch (IllegalArgumentException iarexc) {
                        TopManager.getDefault().notifyException(iarexc);
                    } catch (InvocationTargetException itexc) {
                        TopManager.getDefault().notifyException(itexc);
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
                    TopManager.getDefault().notifyException(iaexc);
                } catch (IllegalArgumentException iarexc) {
                    TopManager.getDefault().notifyException(iarexc);
                } catch (InvocationTargetException itexc) {
                    TopManager.getDefault().notifyException(itexc);
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
        if (numIgnoredFileChanges > 0) {
            numIgnoredFileChanges--;
            //System.out.println("  IGNORED.");
            return ;
        }
        if (timeIgnoreFileChange > 0) {
            if (fileEvent.getFile().lastModified().getTime() <= timeIgnoreFileChange) {
                //System.out.println("  IGNORED - time");
                return ;
            } else {
                //System.out.println("  Time Expired.");
                timeIgnoreFileChange = 0L;
            }
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
                    } catch (java.io.IOException ioExc) {
                        TopManager.getDefault().getErrorManager().notify(ioExc);
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
                        }
                    }
                }
            });
            return task;
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
