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

import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.XMLDataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.HelpCtx;
import org.openide.util.Utilities;
import org.openide.util.WeakListener;

/**
 * The node, that represents the CommandLineVcsFileSystem instance.
 * Inspired by InstanceNode.
 *
 * @author  Martin Entlicher
 */
public class CommandLineVcsFileSystemNode extends AbstractNode {

    private XMLDataObject obj;
    private InstanceCookie ic;
    private PropertyChangeListener xmlPropertyListener;
    private PropertyChangeListener instancePropertyListener;
    
    /** Creates a new instance of CommandLineVcsFileSystemNode */
    public CommandLineVcsFileSystemNode(XMLDataObject obj, InstanceCookie ic) {
        super(Children.LEAF);
        this.obj = obj;
        this.ic = ic;
        xmlPropertyListener = new XMLPropertyListener();
        obj.addPropertyChangeListener(WeakListener.propertyChange(xmlPropertyListener, obj));
        setIconBase("org/netbeans/modules/vcs/advanced/vcsGeneric"); // NOI18N
        getCookieSet().add(ic);
        updateState();
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(CommandLineVcsFileSystemNode.class);
    }
    
    private void updateState() {
        boolean wasSetDisplayName = false;
        boolean wasSetSystemName = false;
        try {
            Document doc = obj.getDocument();
            Element rootElem = doc.getDocumentElement();
            NodeList propertiesesList = rootElem.getElementsByTagName(CommandLineVcsFileSystemInstance.FS_PROPERTIES_ELEM);
            if (propertiesesList.getLength() > 0) {
                Element propertiesNode = (Element) propertiesesList.item(0);
                NodeList propertiesList = propertiesNode.getElementsByTagName(CommandLineVcsFileSystemInstance.FS_PROPERTY_ELEM);
                for (int i = 0; i < propertiesList.getLength(); i++) {
                    Node property = propertiesList.item(i);
                    NamedNodeMap propertyAttrs = property.getAttributes();
                    Node nameAttr = propertyAttrs.getNamedItem(CommandLineVcsFileSystemInstance.PROPERTY_NAME_ATTR);
                    if (nameAttr == null) continue;
                    String name = nameAttr.getNodeValue();
                    if ("displayName".equals(name)) { // NOI18N
                        String value = getPropertyValue(property);
                        setDisplayName(value);
                        wasSetDisplayName = true;
                        if (wasSetDisplayName && wasSetSystemName) break;
                    }
                    if ("systemName".equals(name)) { // NOI18N
                        String value = getPropertyValue(property);
                        setName(value);
                        wasSetSystemName = true;
                        if (wasSetDisplayName && wasSetSystemName) break;
                    }
                }
            }
        } catch (java.io.IOException ioex) {
        } catch (SAXException sex) {}
        if (!wasSetDisplayName) {
            try {
                FileSystem instFS = (FileSystem) ic.instanceCreate();
                setDisplayName(instFS.getDisplayName());
            } catch (java.io.IOException ioex) {
            } catch (ClassNotFoundException cnfex) {}
        }
        if (!wasSetSystemName) {
            try {
                FileSystem instFS = (FileSystem) ic.instanceCreate();
                setName(instFS.getSystemName());
            } catch (java.io.IOException ioex) {
            } catch (ClassNotFoundException cnfex) {}
        }
    }

    private String getPropertyValue(Node property) {
        String value = ""; // NOI18N
        NodeList valueList = property.getChildNodes();
        int m = valueList.getLength();
        for (int j = 0; j < m; j++) {
            Node valueNode = valueList.item(j);
            if (CommandLineVcsFileSystemInstance.PROPERTY_VALUE_TAG.equals(valueNode.getNodeName())) {
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
        return value;
    }
    
    /** try to register PropertyChangeListener to instance to fire its changes.*/
    private void initPropListener() {
        try {
            BeanInfo info = Utilities.getBeanInfo(ic.instanceClass());
            java.beans.EventSetDescriptor[] descs = info.getEventSetDescriptors();
            java.lang.reflect.Method setter = null;
            for (int i = 0; descs != null && i < descs.length; i++) {
                setter = descs[i].getAddListenerMethod();
                if (setter != null && setter.getName().equals("addPropertyChangeListener")) { // NOI18N
                    Object bean = ic.instanceCreate();
                    instancePropertyListener = new InstancePropertyListener();
                    setter.invoke(bean, new Object[] { WeakListener.propertyChange(instancePropertyListener, bean) });
                }
            }
        } catch (java.io.IOException ioex) {
        } catch (ClassNotFoundException cnfex) {
        } catch (java.beans.IntrospectionException iex) {
        } catch (IllegalAccessException iaex) {
        } catch (java.lang.reflect.InvocationTargetException itex) {}
    }
    
    protected Sheet createSheet() {
        //Sheet orig = super.createSheet();
        //changeSheet(orig);
        //return orig;
        Sheet sheet = new Sheet();
        try {
            // properties
            BeanInfo beanInfo = Utilities.getBeanInfo (ic.instanceClass ());
            BeanNode.Descriptor descr = BeanNode.computeProperties (ic.instanceCreate (), beanInfo);
            initPropListener();

            // ok, introspection succeeded, we will have properties tab
            Sheet.Set props = Sheet.createPropertiesSet();
            if (descr.property != null) {
                convertProps (props, descr.property, ic);
            }
            sheet.put (props);

            if (descr.expert != null && descr.expert.length != 0) {
                Sheet.Set exp = Sheet.createExpertSet();
                convertProps (exp, descr.expert, ic);
                sheet.put (exp);
            }
        } catch (ClassNotFoundException ex) {
            //TopManager.getDefault ().getErrorManager ().notify (ErrorManager.INFORMATIONAL, ex);
        } catch (java.io.IOException ex) {
            //TopManager.getDefault ().getErrorManager ().notify (ErrorManager.INFORMATIONAL, ex);
        } catch (java.beans.IntrospectionException ex) {
            //TopManager.getDefault ().getErrorManager ().notify (ErrorManager.INFORMATIONAL, ex);
        }
        return sheet;
    }
    
    /** Method that converts properties of an object.
     * @param set set to add properties to
     * @param arr array of Node.Property and Node.IndexedProperty
     * @param ido IDO providing task to invoke when a property changes
     */
    private static final void convertProps(Sheet.Set set, org.openide.nodes.Node.Property[] arr, InstanceCookie ic) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] instanceof org.openide.nodes.Node.IndexedProperty) {
                set.put (new I ((org.openide.nodes.Node.IndexedProperty)arr[i], ic));
            } else {
                set.put (new P (arr[i], ic));
            }
        }
    }        

    /** A property that delegates every call to original property
     * but when modified, also starts a saving task.
     */
    private static final class P extends org.openide.nodes.Node.Property {
        /** delegate */
        private org.openide.nodes.Node.Property del;
        /** task to executed */
        private InstanceCookie ic;

        public P (org.openide.nodes.Node.Property del, InstanceCookie ic) {
            super (del.getValueType ());
            this.del = del;
            this.ic = ic;
        }

        public void setName(java.lang.String str) {
            del.setName(str);
        }

        public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
            del.restoreDefaultValue();
        }

        public void setValue(java.lang.String str, java.lang.Object obj) {
            del.setValue(str, obj);
        }

        public boolean supportsDefaultValue() {
            return del.supportsDefaultValue();
        }

        public boolean canRead() {
            return del.canRead ();
        }

        public PropertyEditor getPropertyEditor() {
            return del.getPropertyEditor();
        }

        public boolean isHidden() {
            return del.isHidden();
        }

        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return del.getValue ();
        }

        public void setExpert(boolean param) {
            del.setExpert(param);
        }

        /** Delegates the set value and also saves the bean.
         */
        public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            del.setValue (val);
            //FileSystem fs = (FileSystem) ic.instanceCreate();
            //fs.
            //t.scheduleSave();
        }

        public void setShortDescription(java.lang.String str) {
            del.setShortDescription(str);
        }

        public boolean isExpert() {
            return del.isExpert();
        }

        public boolean canWrite() {
            return del.canWrite ();
        }

        public Class getValueType() {
            return del.getValueType();
        }

        public java.lang.String getDisplayName() {
            return del.getDisplayName();
        }

        public java.util.Enumeration attributeNames() {
            return del.attributeNames();
        }

        public java.lang.String getShortDescription() {
            return del.getShortDescription();
        }

        public java.lang.String getName() {
            return del.getName();
        }

        public void setHidden(boolean param) {
            del.setHidden(param);
        }

        public void setDisplayName(java.lang.String str) {
            del.setDisplayName(str);
        }

        public boolean isPreferred() {
            return del.isPreferred();
        }

        public java.lang.Object getValue(java.lang.String str) {
            return del.getValue(str);
        }

        public void setPreferred(boolean param) {
            del.setPreferred(param);
        }

    } // end of P

    /** A property that delegates every call to original property
     * but when modified, also starts a saving task.
     */
    private static final class I extends org.openide.nodes.Node.IndexedProperty {
        /** delegate */
        private org.openide.nodes.Node.IndexedProperty del;
        /** task to executed */
        private InstanceCookie ic;

        public I (org.openide.nodes.Node.IndexedProperty del, InstanceCookie ic) {
            super (del.getValueType (), del.getElementType ());
            this.del = del;
            this.ic = ic;
        }

        public void setName(java.lang.String str) {
            del.setName(str);
        }

        public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
            del.restoreDefaultValue();
        }

        public void setValue(java.lang.String str, java.lang.Object obj) {
            del.setValue(str, obj);
        }

        public boolean supportsDefaultValue() {
            return del.supportsDefaultValue();
        }

        public boolean canRead() {
            return del.canRead ();
        }

        public PropertyEditor getPropertyEditor() {
            return del.getPropertyEditor();
        }

        public boolean isHidden() {
            return del.isHidden();
        }

        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return del.getValue ();
        }

        public void setExpert(boolean param) {
            del.setExpert(param);
        }

        /** Delegates the set value and also saves the bean.
         */
        public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            del.setValue (val);
            //t.scheduleSave();
        }

        public void setShortDescription(java.lang.String str) {
            del.setShortDescription(str);
        }

        public boolean isExpert() {
            return del.isExpert();
        }

        public boolean canWrite() {
            return del.canWrite ();
        }

        public Class getValueType() {
            return del.getValueType();
        }

        public java.lang.String getDisplayName() {
            return del.getDisplayName();
        }

        public java.util.Enumeration attributeNames() {
            return del.attributeNames();
        }

        public java.lang.String getShortDescription() {
            return del.getShortDescription();
        }

        public java.lang.String getName() {
            return del.getName();
        }

        public void setHidden(boolean param) {
            del.setHidden(param);
        }

        public void setDisplayName(java.lang.String str) {
            del.setDisplayName(str);
        }

        public boolean isPreferred() {
            return del.isPreferred();
        }

        public java.lang.Object getValue(java.lang.String str) {
            return del.getValue(str);
        }

        public void setPreferred(boolean param) {
            del.setPreferred(param);
        }

        public boolean canIndexedRead () {
            return del.canIndexedRead ();
        }

        public Class getElementType () {
            return del.getElementType ();
        }

        public Object getIndexedValue (int index) throws
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            return del.getIndexedValue (index);
        }

        public boolean canIndexedWrite () {
            return del.canIndexedWrite ();
        }

        public void setIndexedValue (int indx, Object val) throws
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            del.setIndexedValue (indx, val);
            //t.scheduleSave();
        }

        public PropertyEditor getIndexedPropertyEditor () {
            return del.getIndexedPropertyEditor ();
        }
    } // end of I
    
    private final class XMLPropertyListener extends Object implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent ev) {
            if (XMLDataObject.PROP_DOCUMENT.equals(ev.getPropertyName())) {
                updateState();
            }
        }
        
    }
    
    private final class InstancePropertyListener extends Object implements PropertyChangeListener {
        
        private boolean doNotListen = false;
        public void propertyChange(PropertyChangeEvent e) {
            if (doNotListen) return;
            firePropertyChange(e.getPropertyName(), e.getOldValue(), e.getNewValue());
        }
        
        public void destroy() {
            doNotListen = true;
        }
        
    }
}
