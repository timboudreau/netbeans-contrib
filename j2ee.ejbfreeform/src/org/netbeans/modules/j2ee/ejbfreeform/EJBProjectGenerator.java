/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbfreeform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.ant.freeform.FreeformProjectType;
import org.netbeans.modules.ant.freeform.spi.support.Util;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.ErrorManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Reads/writes project.xml.
 *
 * @author  Jesse Glick, David Konecny, Pavel Buzek, Martin Adamek
 */
public class EJBProjectGenerator {
    
    public static final String NS_GENERAL = "http://www.netbeans.org/ns/freeform-project/1"; // NOI18N
    
    //    /** Keep root elements in the order specified by project's XML schema. */
    private static final String[] rootElementsOrder = new String[]{"name", "properties", "folders", "ide-actions", "export", "view", "subprojects"}; // NOI18N
    private static final String[] viewElementsOrder = new String[]{"items", "context-menu"}; // NOI18N
    
    //    // this order is not required by schema, but follow it to minimize randomness a bit
    private static final String[] folderElementsOrder = new String[]{"source-folder", "build-folder"}; // NOI18N
    private static final String[] viewItemElementsOrder = new String[]{"source-folder", "source-file"}; // NOI18N
    
    private EJBProjectGenerator() {}
    
    /**
     * @param soruces list of pairs[relative path, display name]
     */
    public static void putEJBSourceFolder(AntProjectHelper helper, List/*<String>*/ sources) {
        ArrayList list = new ArrayList();
        Element data = helper.getPrimaryConfigurationData(true);
        Document doc = data.getOwnerDocument();
        Element foldersEl = Util.findElement(data, "folders", NS_GENERAL); // NOI18N
        if (foldersEl == null) {
            foldersEl = doc.createElementNS(NS_GENERAL, "folders"); // NOI18N
            Util.appendChildElement(data, foldersEl, rootElementsOrder);
        }
        Element viewEl = Util.findElement(data, "view", NS_GENERAL); // NOI18N
        if (viewEl == null) {
            viewEl = doc.createElementNS(NS_GENERAL, "view"); // NOI18N
            Util.appendChildElement(data, viewEl, rootElementsOrder);
        }
        Element itemsEl = Util.findElement(viewEl, "items", NS_GENERAL); // NOI18N
        if (itemsEl == null) {
            itemsEl = doc.createElementNS(NS_GENERAL, "items"); // NOI18N
            Util.appendChildElement(viewEl, itemsEl, viewElementsOrder);
        }
        Iterator it1 = sources.iterator();
        while (it1.hasNext()) {
            String path = (String)it1.next();
            assert it1.hasNext();
            String dispname = (String)it1.next();
            Element sourceFolderEl = doc.createElementNS(NS_GENERAL, "source-folder"); // NOI18N
            Element el = doc.createElementNS(NS_GENERAL, "label"); // NOI18N
            el.appendChild(doc.createTextNode(dispname));
            sourceFolderEl.appendChild(el);
            el = doc.createElementNS(NS_GENERAL, "type"); // NOI18N
            el.appendChild(doc.createTextNode("configFilesRoot"));
            sourceFolderEl.appendChild(el);
            el = doc.createElementNS(NS_GENERAL, "location"); // NOI18N
            el.appendChild(doc.createTextNode(path));
            sourceFolderEl.appendChild(el);
            Util.appendChildElement(foldersEl, sourceFolderEl, folderElementsOrder);
            
            sourceFolderEl = doc.createElementNS(NS_GENERAL, "source-folder"); // NOI18N
            sourceFolderEl.setAttribute("style", EJBProjectNature.STYLE_CONFIG_FILES); // NOI18N
            el = doc.createElementNS(NS_GENERAL, "label"); // NOI18N
            el.appendChild(doc.createTextNode(dispname));
            sourceFolderEl.appendChild(el);
            el = doc.createElementNS(NS_GENERAL, "location"); // NOI18N
            el.appendChild(doc.createTextNode(path)); // NOI18N
            sourceFolderEl.appendChild(el);
            Util.appendChildElement(itemsEl, sourceFolderEl, viewItemElementsOrder);
        }
        
        
        List sourceRootNames = getSourceFolders(doc, foldersEl, "java");
        // TODO: ma154696: add support for multiple source roots?
        //  now I get only first one, have to check impact of more roots 
        // to Enterprise beans node (which one to put into view items
        putProperty(doc, data, "src.dir", (String) sourceRootNames.get(0));
        addSourceFolderViewItem(doc, itemsEl, EJBProjectNature.STYLE_EJBS, "XXX Enterprise Beans", (String) sourceRootNames.get(0));
        
        helper.putPrimaryConfigurationData(data, true);
    }
    
    /**
     * Adds source-folder element in view items in project.xml
     * @param doc document to write to
     * @param itemsEl items element in project.xml
     * @param style a view style; will be one of {@link ProjectNature#getSourceFolderViewStyles}
     * @param label name for the node in logical view (mostly ignored)
     * @param path path to source folder defined by displayed node
     */
    private static void addSourceFolderViewItem(Document doc, Element itemsEl, String style, String label, String path) {
        // creates source-folder element for
        Element sourceFolderEl = doc.createElementNS(NS_GENERAL, "source-folder"); // NOI18N
        sourceFolderEl.setAttribute("style", style); // NOI18N
        Element el = doc.createElementNS(NS_GENERAL, "label"); // NOI18N
        el.appendChild(doc.createTextNode(label));
        sourceFolderEl.appendChild(el);
        el = doc.createElementNS(NS_GENERAL, "location"); // NOI18N
        el.appendChild(doc.createTextNode(path));
        sourceFolderEl.appendChild(el);
        Util.appendChildElement(itemsEl, sourceFolderEl, viewItemElementsOrder);
    }
    
    /**
     * Creates property entry in project.xml.
     * @param doc document to write to
     * @param parent parent element
     * @param key property name
     * @param value property value
     */
    private static void putProperty(Document doc, Element parent, String key, String value) {
        // TODO: ma154696: check NodeList length
        Element props = Util.findElement(parent, "properties", EJBProjectGenerator.NS_GENERAL); // NOI18N
        Element property = doc.createElementNS(FreeformProjectType.NS_GENERAL, "property"); // NOI18N
        property.setAttribute("name", key); // NOI18N
        property.appendChild(doc.createTextNode(value));
        props.appendChild(property);
    }
    
    private static List/*<String>*/ getSourceFolders(Document doc, Element parent, String type) {
        // TODO: ma154696: add check if parent element's name is folder (or better refactor method interface)
        List result = new ArrayList();
        List sourceFolderElements = Util.findSubElements(parent);
        for (int i = 0; i < sourceFolderElements.size(); i++) {
            Element subElement = (Element) sourceFolderElements.get(i);
            Element locationEl = Util.findElement(subElement, "location", EJBProjectGenerator.NS_GENERAL);
            Element typeEl = Util.findElement(subElement, "type", EJBProjectGenerator.NS_GENERAL);
            if (typeEl != null) {
                if (typeEl.getChildNodes().item(0).getNodeValue().equals(type)) {
                    result.add(locationEl.getChildNodes().item(0).getNodeValue());
                }
            }
        }
        return result;
    }
    
    /**
     * Read EJB modules from the project.
     * @param helper AntProjectHelper instance
     * @param aux AuxiliaryConfiguration instance
     * @return list of EJBModule instances
     */
    public static List/*<EJBModule>*/ getEJBmodules(
            AntProjectHelper helper, AuxiliaryConfiguration aux) {
        //assert ProjectManager.mutex().isReadAccess() || ProjectManager.mutex().isWriteAccess();
        ArrayList list = new ArrayList();
        Element data = aux.getConfigurationFragment("ejb-data", EJBProjectNature.NS_EJB, true); // NOI18N
        List/*<Element>*/ wms = Util.findSubElements(data);
        Iterator it = wms.iterator();
        while (it.hasNext()) {
            Element wmEl = (Element)it.next();
            EJBModule wm = new EJBModule();
            Iterator it2 = Util.findSubElements(wmEl).iterator();
            while (it2.hasNext()) {
                Element el = (Element)it2.next();
                if (el.getLocalName().equals("config-files")) { // NOI18N
                    wm.configFiles = Util.findText(el);
                    continue;
                }
                if (el.getLocalName().equals("classpath")) { // NOI18N
                    wm.classpath = Util.findText(el);
                    continue;
                }
                //                if (el.getLocalName().equals("context-path")) { // NOI18N
                //                    wm.contextPath = Util.findText(el);
                //                    continue;
                //                }
                if (el.getLocalName().equals("j2ee-spec-level")) { // NOI18N
                    wm.j2eeSpecLevel = Util.findText(el);
                }
            }
            list.add(wm);
        }
        return list;
    }
    
    /**
     * Update EJB modules of the project. Project is left modified
     * and you must save it explicitely.
     * @param helper AntProjectHelper instance
     * @param aux AuxiliaryConfiguration instance
     * @param ejbModules list of EJBModule instances
     */
    public static void putEJBModules(AntProjectHelper helper,
            AuxiliaryConfiguration aux, List/*<EJBModule>*/ ejbModules) {
        //assert ProjectManager.mutex().isWriteAccess();
        ArrayList list = new ArrayList();
        Element data = aux.getConfigurationFragment("ejb-data", EJBProjectNature.NS_EJB, true); // NOI18N
        if (data == null) {
            data = helper.getPrimaryConfigurationData(true).getOwnerDocument().
                    createElementNS(EJBProjectNature.NS_EJB, "ejb-data"); // NOI18N
        }
        Document doc = data.getOwnerDocument();
        List wms = Util.findSubElements(data);
        Iterator it = wms.iterator();
        while (it.hasNext()) {
            Element wmEl = (Element)it.next();
            data.removeChild(wmEl);
        }
        Iterator it2 = ejbModules.iterator();
        while (it2.hasNext()) {
            Element wmEl = doc.createElementNS(EJBProjectNature.NS_EJB, "ejb-module"); // NOI18N
            data.appendChild(wmEl);
            EJBModule wm = (EJBModule)it2.next();
            Element el;
            if (wm.configFiles != null) {
                el = doc.createElementNS(EJBProjectNature.NS_EJB, "config-files"); // NOI18N
                el.appendChild(doc.createTextNode(wm.configFiles));
                wmEl.appendChild(el);
            }
            if (wm.classpath != null) {
                el = doc.createElementNS(EJBProjectNature.NS_EJB, "classpath"); // NOI18N
                el.appendChild(doc.createTextNode(wm.classpath));
                wmEl.appendChild(el);
            }
            //            if (wm.contextPath != null) {
            //                el = doc.createElementNS(EJBProjectNature.NS_EJB, "context-path"); // NOI18N
            //                el.appendChild(doc.createTextNode(wm.contextPath));
            //                wmEl.appendChild(el);
            //            }
            if (wm.j2eeSpecLevel != null) {
                el = doc.createElementNS(EJBProjectNature.NS_EJB, "j2ee-spec-level"); // NOI18N
                el.appendChild(doc.createTextNode(wm.j2eeSpecLevel));
                wmEl.appendChild(el);
            }
        }
        aux.putConfigurationFragment(data, true);
    }
    
    /**
     * Structure describing EJB module.
     * Data in the struct are in the same format as they are stored in XML.
     */
    public static final class EJBModule {
        public String configFiles;
        public String classpath;
        //        public String contextPath;
        public String j2eeSpecLevel;
    }
    
}
