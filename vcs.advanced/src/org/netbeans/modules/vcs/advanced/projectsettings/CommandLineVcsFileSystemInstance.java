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
import java.util.HashMap;
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
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.WeakListener;
import org.openide.xml.XMLUtil;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcs.advanced.commands.UserCommandIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIO;

/**
 *
 * @author  Martin Entlicher
 */
public class CommandLineVcsFileSystemInstance extends Object implements InstanceCookie.Of {
    
    public static final String SETTINGS_ROOT_ELEM = "fssettings";               // NOI18N
    public static final String FS_PROPERTIES_ELEM = "fsproperties";               // NOI18N
    public static final String FS_PROPERTY_ELEM = "property";               // NOI18N
    public static final String PROPERTY_NAME_ATTR = "name";               // NOI18N
    public static final String PROPERTY_VALUE_TAG = "value";               // NOI18N

    /** The DTD for a configuration profile. */
    public static final String PUBLIC_ID = "-//NetBeans//DTD VCS Advanced FSSettings 1.0//EN"; // NOI18N
    public static final String SYSTEM_ID = "http://www.netbeans.org/dtds/vcs-advanced-fssettings-1_0.dtd"; // NOI18N

    private static HashMap fsInstances = new HashMap();
    private static PropertyDescriptor[] fsProperties;
    private static HashMap fsPropertiesByName;
    
    private FileObject fo;
    private Document doc;
    private FSPropertyChangeListener fsPropertyChangeListener;

    static {
        try {
            fsProperties = new PropertyDescriptor[] {
                // Stuff from AbstractFileSystem & VcsFileSystem
                new PropertyDescriptor("rootFile", CommandLineVcsFileSystem.class, "getWorkingDirectory", "setRootDirectory"),
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
                new PropertyDescriptor("shortFileStatuses", CommandLineVcsFileSystem.class, "isShortFileStatuses", "setShortFileStatuses")
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
    public CommandLineVcsFileSystemInstance(FileObject fo, Document doc) {
        this.fo = fo;
        this.doc = doc;
    }

    public Object instanceCreate() throws java.io.IOException, ClassNotFoundException {
        CommandLineVcsFileSystem fs = null;
        Reference fsRef = (Reference) fsInstances.get(fo.getPackageNameExt('/', '.'));
        if (fsRef != null) {
            fs = (CommandLineVcsFileSystem) fsRef.get();
            if (fs == null) fsInstances.remove(fo.getPackageNameExt('/', '.'));
        }
        if (fs == null) {
            fs = new CommandLineVcsFileSystem();
            try {
                readFSProperties(fs, doc);
            } catch (DOMException dexc) {
                TopManager.getDefault().notifyException(dexc);
            }
            fsPropertyChangeListener = new FSPropertyChangeListener(fs, fo);
            fs.addPropertyChangeListener(WeakListener.propertyChange(fsPropertyChangeListener, fs));
            fsInstances.put(fo.getPackageNameExt('/', '.'), new WeakReference(fs));
        }
        return fs;
    }
    
    public Class instanceClass() throws java.io.IOException, ClassNotFoundException {
        return CommandLineVcsFileSystem.class;
    }
    
    public boolean instanceOf(Class clazz) {
        return CommandLineVcsFileSystem.class.equals(clazz);
    }
    
    public String instanceName() {
        return org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem.class.getName();
    }
    
    public static void readFSProperties(CommandLineVcsFileSystem fs, Document doc) throws DOMException {
        //System.out.println("readFSProperties()");
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
        //System.out.println("writeFSProperties()");
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

    private static class FSPropertyChangeListener extends Object implements PropertyChangeListener {
        private static int TASK_SCHEDULE_DELAY = 500;
        private Reference fs;
        private FileObject fo;
        private RequestProcessor.Task writeTask = null;
        private volatile FileLock lock = null;
        private volatile boolean reSchedule = false;
        
        public FSPropertyChangeListener(CommandLineVcsFileSystem fs, FileObject fo) {
            this.fs = new WeakReference(fs);
            this.fo = fo;
        }
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            synchronized (this) {
                if (lock == null) {
                    try {
                        lock = fo.lock();
                    } catch (java.io.IOException ioExc) {
                        TopManager.getDefault().getErrorManager().notify(ioExc);
                        return ;
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
                    if (!lock.isValid() || !fo.isValid()) return ;
                    CommandLineVcsFileSystem fs = (CommandLineVcsFileSystem) FSPropertyChangeListener.this.fs.get();
                    if (fs != null) {
                        Document doc = createEmptyFSPropertiesDocument();
                        writeFSProperties(fs, doc);
                        try {
                            XMLUtil.write(doc, fo.getOutputStream(lock), null);
                        } catch (java.io.IOException ioExc) {
                            TopManager.getDefault().getErrorManager().notify(ioExc);
                        }
                    }
                }
            }, TASK_SCHEDULE_DELAY);
            task.addTaskListener(new TaskListener() {
                public void taskFinished(Task task) {
                    synchronized (FSPropertyChangeListener.this) {
                        if (reSchedule) {
                            writeTask.schedule(TASK_SCHEDULE_DELAY);
                            reSchedule = false;
                        } else {
                            lock.releaseLock();
                            lock = null;
                        }
                    }
                }
            });
            return task;
        }
        
    }
    
}
