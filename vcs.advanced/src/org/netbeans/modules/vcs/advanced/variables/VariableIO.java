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

package org.netbeans.modules.vcs.advanced.variables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.text.MessageFormat;

import org.w3c.dom.*;
import org.xml.sax.*;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.XMLDataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;
import org.openide.xml.XMLUtil;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;

/**
 * This class provides input/output of variables from/to xml file.
 *
 * @author  Martin Entlicher
 */
public class VariableIO extends Object {

    public static final String CONFIG_FILE_EXT = "xml";                          // NOI18N
    public static final String CONFIG_ROOT_ELEM = "configuration";               // NOI18N
    public static final String LABEL_TAG = "label";                              // NOI18N
    public static final String OS_TAG = "os";                                    // NOI18N
    public static final String OS_COMPATIBLE_TAG = "compatible";                 // NOI18N
    public static final String OS_UNCOMPATIBLE_TAG = "uncompatible";             // NOI18N
    public static final String VARIABLES_TAG = "variables";                      // NOI18N
    public static final String VARIABLE_TAG = "variable";                        // NOI18N
    public static final String VARIABLE_SELECTOR_TAG = "selector";               // NOI18N
    public static final String VARIABLE_NAME_ATTR = "name";                      // NOI18N
    public static final String VARIABLE_LABEL_ATTR = "label";                    // NOI18N
    public static final String VARIABLE_LABEL_MNEMONIC_ATTR = "labelMnemonic";   // NOI18N
    public static final String VARIABLE_A11Y_NAME_ATTR = "a11yName";             // NOI18N
    public static final String VARIABLE_A11Y_DESCRIPTION_ATTR = "a11yDescription";// NOI18N
    public static final String VARIABLE_BASIC_ATTR = "basic";                    // NOI18N
    public static final String VARIABLE_LOCAL_FILE_ATTR = "localFile";           // NOI18N
    public static final String VARIABLE_LOCAL_DIR_ATTR = "localDir";             // NOI18N
    public static final String VARIABLE_EXECUTABLE_ATTR = "executable";          // NOI18N
    public static final String VARIABLE_ORDER_ATTR = "order";                    // NOI18N
    public static final String VARIABLE_IF_ATTR = "if";                          // NOI18N
    public static final String VARIABLE_UNLESS_ATTR = "unless";                  // NOI18N
    public static final String VARIABLE_VALUE_TAG = "value";                     // NOI18N
    
    public static final String BOOLEAN_VARIABLE_TRUE = "true";                   // NOI18N
    public static final String BOOLEAN_VARIABLE_FALSE = "false";                 // NOI18N
    
    /** The DTD for a configuration profile. */
    public static final String PUBLIC_ID = "-//NetBeans//DTD VCS Configuration 1.0//EN"; // NOI18N
    public static final String SYSTEM_ID = "http://www.netbeans.org/dtds/vcs-configuration-1_0.dtd"; // NOI18N
    
    /** Whether to validate XML files. Safer, but slows down. */
    private static final boolean VALIDATE_XML = false;
    
    private static LabelContentHandler labelContentHandler = null;

    /** Creates new VariableIO */
    private VariableIO() {
    }

    private static boolean bundleListContains(FileObject[] list, String name) {
        for(int i = 0; i < list.length; i++) {
            String buName = list[i].getName();
            if (buName.equals(name)) return true;
        }
        return false;
    }

    /**
     * Find out whether the locale extension exists as a locale name.
     */
    private static boolean isLocale(String localeExt) {
        String lang;
        int index = localeExt.indexOf('_');
        if (index > 0) lang = localeExt.substring(0, index);
        else lang = localeExt;
        String[] languages = Locale.getISOLanguages();
        List list = Arrays.asList(languages);
        return list.contains(lang);
    }
    
    private static boolean isResourceBundle(FileObject fo) {
        return fo.getExt().equalsIgnoreCase("properties") &&
                (fo.getName().startsWith("Bundle") ||
                 fo.getName().endsWith("Bundle") ||
                 fo.getName().indexOf("Bundle_") > 0);
    }
    
    /** Read list of available confugurations from the directory.
     * All files with extension ".properties" are considered to be configurations.
     * However only properties with current localization are read.
     * @return the available configurations.
     */
    public static ArrayList readConfigurations(FileObject file) {
        //ArrayList res = new ArrayList();
        FileObject[] ch = file.getChildren();
        ArrayList list = new ArrayList(Arrays.asList(ch));
        for(int i = 0; i < list.size(); i++) {
            FileObject fo = (FileObject) list.get(i);
            String ext = fo.getExt();
            if (!ext.equalsIgnoreCase(CONFIG_FILE_EXT)
                && !ext.equalsIgnoreCase(VariableIOCompat.CONFIG_FILE_EXT)
                || CommandLineVcsFileSystem.isTemporaryConfig(fo.getName())
                || isResourceBundle(fo)) {
                list.remove(i);
                i--;
            }
        }
        ch = (FileObject[]) list.toArray(new FileObject[0]);
        ArrayList res = getLocalizedConfigurations(ch);
        hideOlderConfs(res);
        return res;
    }
    
    private static void hideOlderConfs(ArrayList list) {
        for(int i = 0; i < list.size(); i++) {
            String fullName = (String) list.get(i);
            int dotIndex = fullName.lastIndexOf('.');
            String ext = fullName.substring(dotIndex + 1);
            if (VariableIOCompat.CONFIG_FILE_EXT.equals(ext)) {
                String name = fullName.substring(0, dotIndex);
                String newName = name + "." + CONFIG_FILE_EXT;
                if (list.contains(newName)) {
                    list.remove(i);
                    i--;
                }
            }
        }
        
    }
    
    /**
     * Pick the right localized configurations.
     */
    public static final ArrayList getLocalizedConfigurations(FileObject[] ch) {
        ArrayList res = new ArrayList();
        Locale locale = Locale.getDefault();
        for(int i = 0; i < ch.length; i++) {
            String name = ch[i].getName();
            //System.out.println("name = "+name+", locale = "+locale.toString());
            int nameIndex = name.indexOf('_');
            String baseName = name;
            String localeExt = "";
            if (nameIndex > 0) {
                baseName = name.substring(0, nameIndex);
                localeExt = name.substring(nameIndex + 1);
            }
            if (localeExt.equals(locale.toString())) ; // OK
            else if (localeExt.equals(locale.getLanguage()+"_"+locale.getCountry())) {
                if (bundleListContains(ch, baseName+"_"+locale.toString())) continue; // current variant is somewhere
            } else if (localeExt.equals(locale.getLanguage())) {
                if (bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()) ||
                    bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()+"_"+locale.getVariant())) continue;
            } else if (localeExt.length() == 0) {
                if (bundleListContains(ch, baseName+"_"+locale.getLanguage()) ||
                    bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()) ||
                    bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()+"_"+locale.getVariant())) continue;
            } else if (localeExt.length() > 0 && isLocale(localeExt)) continue;
            //System.out.println("adding: "+name+"."+ch[i].getExt());
            res.add(name+"."+ch[i].getExt());
        }
        return res;
    }

    /** Open file and load configurations from it.
     * @param configRoot the directory which contains properties.
     * @param name the name of properties to read.
     */
    public static Document readPredefinedConfigurations(FileObject configRoot, final String name){
        Document doc = null;
        FileObject config = configRoot.getFileObject(name);
        if (config == null) {
            org.openide.ErrorManager.getDefault().notify(new FileNotFoundException("Problems while reading predefined properties.") {
                public String getLocalizedMessage() {
                    return g("EXC_Problems_while_reading_predefined_properties", name);
                }
            });
            //E.err(g("EXC_Problems_while_reading_predefined_properties",name)); // NOI18N
            return doc;
        }
        try {
            DataObject dobj = DataObject.find(config);
            if (dobj != null && dobj instanceof XMLDataObject) {
                doc = ((XMLDataObject) dobj).getDocument();
            }
            //InputStream in = config.getInputStream();
            //props.load(in);
            //in.close();
        } catch(DataObjectNotFoundException e) {
            org.openide.ErrorManager.getDefault().notify(new DataObjectNotFoundException(config) {
                public String getLocalizedMessage() {
                    return g("EXC_Problems_while_reading_predefined_properties", name);
                }
            });
        } catch(IOException e){
            org.openide.ErrorManager.getDefault().notify(new IOException("Problems while reading predefined properties.") {
                public String getLocalizedMessage() {
                    return g("EXC_Problems_while_reading_predefined_properties", name);
                }
            });
        } catch (org.xml.sax.SAXException sexc) {
            org.openide.ErrorManager.getDefault().notify(new org.xml.sax.SAXException("Problems while reading predefined properties.", sexc) {
                public String getLocalizedMessage() {
                    return g("EXC_Problems_while_reading_predefined_properties", name);
                }
            });
        }
        return doc;
    }

    public static String getConfigurationLabel(Document doc) throws DOMException {
        NodeList labelList = doc.getElementsByTagName(LABEL_TAG);
        String label = "";
        if (labelList.getLength() > 0) {
            Node labelNode = labelList.item(0);
            NodeList textList = labelNode.getChildNodes();
            if (textList.getLength() > 0) {
                Node subNode = textList.item(0);
                if (subNode instanceof Text) {
                    Text textNode = (Text) subNode;
                    label = VcsUtilities.getBundleString(textNode.getData());
                }
            }
        }
        return label;
    }
    
    public static synchronized String[] getConfigurationLabelAndOS(FileObject configRoot, final String name) {
        FileObject config = configRoot.getFileObject(name);
        if (config == null) {
            org.openide.util.RequestProcessor.postRequest(new Runnable() {
                public void run() {
                    org.openide.ErrorManager.getDefault().notify(new FileNotFoundException("Problems while reading predefined properties.") {
                        public String getLocalizedMessage() {
                            return g("EXC_Problems_while_reading_predefined_properties", name);
                        }
                    });
                }
            });
            //E.err(g("EXC_Problems_while_reading_predefined_properties",name)); // NOI18N
            return null;
        }
        if (labelContentHandler == null) {
            labelContentHandler = new LabelContentHandler();
        }
        //System.out.println("VariableIO.getConfigurationLabel("+config+")");
        try {
            XMLReader reader = XMLUtil.createXMLReader();
            reader.setContentHandler(labelContentHandler);
            reader.setEntityResolver(labelContentHandler);
            //System.out.println("   parsing...");
            InputSource source = new InputSource(config.getInputStream());
            reader.parse(source);
            //System.out.println("   parsing done.");
        } catch (SAXException exc) {
            //System.out.println("   parsing done. ("+exc.getMessage()+")");
            if (!"End of label.".equals(exc.getMessage())) {
                org.openide.ErrorManager.getDefault().notify(
                    org.openide.ErrorManager.getDefault().annotate(
                        exc, g("EXC_Problems_while_reading_predefined_properties", name)));
            }
        } catch (java.io.FileNotFoundException fnfExc) {
        } catch (java.io.IOException ioExc) {
        }
        //System.out.println("  --> label = "+labelContentHandler.getLabel());
        String[] labelAndOS = new String[3];
        labelAndOS[0] = VcsUtilities.getBundleString(labelContentHandler.getLabel());
        labelAndOS[1] = labelContentHandler.getCompatibleOSs();
        labelAndOS[2] = labelContentHandler.getUncompatibleOSs();
        return labelAndOS;
    }

    /** Read list of VCS variables from the document.
     * If there is only value specified, label is an empty string and basic,
     * localFile and localDir are false.
     */
    public static ConditionedVariables readVariables(Document doc) throws DOMException {
        Element rootElem = doc.getDocumentElement();
        if (!CONFIG_ROOT_ELEM.equals(rootElem.getNodeName())) throw new DOMException((short) 0, "Wrong root element: "+rootElem.getNodeName());
        ConditionedVariables vars;
        NodeList varList = rootElem.getElementsByTagName(VARIABLES_TAG);
        if (varList.getLength() > 0) {
            Node varsNode = varList.item(0);
            vars = getVariables(varsNode.getChildNodes());
        } else {
            vars = new ConditionedVariables(Collections.EMPTY_LIST,
                                            Collections.EMPTY_MAP,
                                            Collections.EMPTY_MAP);
        }
        return vars;
    }
    
    public static ConditionedVariables getVariables(NodeList varList) throws DOMException {
        List vars = new ArrayList();
        Map conditionsByVariables = new TreeMap();
        Map varsByConditions = new HashMap();
        int n = varList.getLength();
        for (int i = 0; i < n; i++) {
            Node varNode = varList.item(i);
            if (VARIABLE_TAG.equals(varNode.getNodeName())) {
                String name = "";
                String if_attr = "";
                String unless_attr = "";
                //boolean isBasic = false;
                String value = "";
                NamedNodeMap varAttrs = varNode.getAttributes();
                Node nameAttr = varAttrs.getNamedItem(VARIABLE_NAME_ATTR);
                if (nameAttr == null) continue;
                name = nameAttr.getNodeValue();
                Node ifAttr = varAttrs.getNamedItem(VARIABLE_IF_ATTR);
                if (ifAttr != null) if_attr = ifAttr.getNodeValue();
                Node unlessAttr = varAttrs.getNamedItem(VARIABLE_UNLESS_ATTR);
                if (unlessAttr != null) unless_attr = unlessAttr.getNodeValue();
                Map valuesByConditions = new HashMap();
                Condition cond = createCondition(name, if_attr, unless_attr);
                
                NodeList valueList = varNode.getChildNodes();
                //System.out.println("valueList.length = "+valueList.getLength());
                int m = valueList.getLength();
                for (int j = 0; j < m; j++) {
                    Node valueNode = valueList.item(j);
                    //System.out.println("value("+j+") name = "+valueNode.getNodeName());
                    if (VARIABLE_VALUE_TAG.equals(valueNode.getNodeName())) {
                        NodeList textList = valueNode.getChildNodes();
                        for (int itl = 0; itl < textList.getLength(); itl++) {
                            Node subNode = textList.item(itl);
                            //System.out.println("    subNode = "+subNode);
                            if (subNode instanceof Text) {
                                Text textNode = (Text) subNode;
                                value += textNode.getData();
                            }
                            if (subNode instanceof EntityReference) {
                                EntityReference entityNode = (EntityReference) subNode;
                                //System.out.println("Have EntityReference = "+entityNode+", value = "+entityNode.getNodeValue());
                                NodeList entityList = entityNode.getChildNodes();
                                for (int iel = 0; iel < entityList.getLength(); iel++) {
                                    Node entitySubNode = entityList.item(iel);
                                    //System.out.println("    entitySubNode = "+entitySubNode);
                                    if (entitySubNode instanceof Text) {
                                        Text textEntityNode = (Text) entitySubNode;
                                        value += textEntityNode.getData();
                                    }
                                }
                            }
                            //System.out.println("    propertyValue = "+propertyValue);
                        }
                        /*
                        if (textList.getLength() > 0) {
                            Node subNode = textList.item(0);
                            //System.out.println("subNode = "+subNode.getNodeName());
                            if (subNode instanceof Text) {
                                Text textNode = (Text) subNode;
                                value = textNode.getData();
                                //System.out.println("value = '"+value+"'");
                            }
                        }
                         */
                        String value_if_attr = "";
                        String value_unless_attr = "";
                        NamedNodeMap valueAttrs = valueNode.getAttributes();
                        if (valueAttrs != null) {
                            Node valueIfAttr = valueAttrs.getNamedItem(VARIABLE_IF_ATTR);
                            if (valueIfAttr != null) value_if_attr = valueIfAttr.getNodeValue();
                            Node valueUnlessAttr = valueAttrs.getNamedItem(VARIABLE_UNLESS_ATTR);
                            if (valueUnlessAttr != null) value_unless_attr = valueUnlessAttr.getNodeValue();
                        }
                        Condition vc = createCondition(name, value_if_attr, value_unless_attr);
                        valuesByConditions.put(vc, value);
                        value = "";
                    }
                }
                Condition[] conditions = new Condition[valuesByConditions.size()];
                int ci = 0;
                for (Iterator it = valuesByConditions.keySet().iterator(); it.hasNext(); ) {
                    Condition vc = (Condition) it.next();
                    if (vc == null) continue;
                    String cvalue = (String) valuesByConditions.get(vc);
                    VcsConfigVariable var = readVariable(name, cvalue, varNode, varAttrs);
                    if (cond != null) vc.addCondition(cond, true);
                    conditions[ci] = vc;
                    varsByConditions.put(vc, var);
                    ci++;
                }
                value = (String) valuesByConditions.get(null);
                if (value != null) {
                    VcsConfigVariable var = readVariable(name, value, varNode, varAttrs);
                    //System.out.println("  var = "+var);
                    if (valuesByConditions.size() > 1) {
                        cond = createComplementaryCondition(name, cond, valuesByConditions.keySet());
                    }
                    if (cond != null) {
                        varsByConditions.put(cond, var);
                        conditions[conditions.length - 1] = cond;
                    } else {
                        vars.add(var);
                        conditions = null;
                    }
                }
                if (conditions != null) {
                    Condition[] oldConditions = (Condition[]) conditionsByVariables.get(name);
                    if (oldConditions != null) {
                        Condition[] newConditions = new Condition[oldConditions.length + conditions.length];
                        System.arraycopy(oldConditions, 0, newConditions, 0, oldConditions.length);
                        System.arraycopy(conditions, 0, newConditions, oldConditions.length, conditions.length);
                        conditions = newConditions;
                    }
                    conditionsByVariables.put(name, conditions);
                }
            }
        }
        return new ConditionedVariables(vars, conditionsByVariables, varsByConditions);
    }
    
    private static VcsConfigVariable readVariable(String name, String value,
                                                  Node varNode, NamedNodeMap varAttrs) {
        VcsConfigVariable var;
        Node basicAttr = varAttrs.getNamedItem(VARIABLE_BASIC_ATTR);
        value = VcsUtilities.getBundleString(value);
        value = translateVariableValue(name, value);
        if (basicAttr != null && BOOLEAN_VARIABLE_TRUE.equalsIgnoreCase(basicAttr.getNodeValue())) {
            var = getBasicVariable(name, value, varNode, varAttrs);
        } else {
            var = new VcsConfigVariable(name, "", value, false, false, false, "");
        }
        return var;
    }
    
    /**
     * Create a condition of given name, that is true when varaible <code>if_attr</code>
     * is non-empty and variable <code>unless_attr</code> is empty.
     * @return The condition or <code>null</code> if no condition is created
     *         (when both if_attr and unless_attr are empty).
     */
    public static Condition createCondition(String name, String if_attr, String unless_attr) {
        Condition c = null;
        if (if_attr.length() > 0) {
            c = new Condition(name);
            c.addVar(if_attr, "", Condition.COMPARE_VALUE_EQUALS, false);
        }
        if (unless_attr.length() > 0) {
            if (c == null) c = new Condition(name);
            c.addVar(unless_attr, "", Condition.COMPARE_VALUE_EQUALS, true);
        }
        return c;
    }
    
    /**
     * Create a condition, that is complementary to a collection of other conditions
     * @param name The name of the created condition
     * @param mainCondition The main condition that is to be satisfied;
     *                      can be <code>null</code>
     * @param subConditions The collection of conditions for which the complementary
     *                      condition is to be created.
     */
    public static Condition createComplementaryCondition(String name,
                                                         Condition mainCondition,
                                                         Collection subConditions) {
        Condition c = new Condition(name);
        if (mainCondition != null) {
            c.addCondition(mainCondition, true);
        }
        for (Iterator it = subConditions.iterator(); it.hasNext(); ) {
            Condition subC = (Condition) it.next();
            if (subC != null) c.addCondition(subC, false);
        }
        return c;
    }
    
    private static boolean getBooleanAttrVariable(String attrName, NamedNodeMap varAttrs) throws DOMException {
        Node attrNode = varAttrs.getNamedItem(attrName);
        if (attrNode != null && BOOLEAN_VARIABLE_TRUE.equalsIgnoreCase(attrNode.getNodeValue())) {
            return true;
        }
        return false;
    }
    
    private static String translateVariableValue(String name, String value) {
        if ("WRAPPER".equals(name)) {
            int classIndex = value.indexOf(".class");
            if (classIndex > 0) {
                int begin;
                for (begin = classIndex; begin >= 0; begin--) {
                    char c = value.charAt(begin);
                    if (!Character.isJavaIdentifierPart(c) && c != '.') break;
                }
                begin++;
                if (begin < classIndex) {
                    String classNameOrig = value.substring(begin, classIndex);
                    String classNameNew =
                        org.netbeans.modules.vcs.advanced.commands.UserCommandIO.translateExecClass(classNameOrig);
                    if (!classNameOrig.equals(classNameNew)) {
                        value = value.substring(0, begin) + classNameNew + value.substring(classIndex);
                    }
                }
            }
        }
        return value;
    }
    
    private static VcsConfigVariable getBasicVariable(String name, String value, Node varNode, NamedNodeMap varAttrs) throws DOMException {
        String label = "";
        String labelMnemonic = null;
        String a11yName = null;
        String a11yDescription = null;
        boolean localFile = false;
        boolean localDir = false;
        boolean executable = false;
        String customSelector = "";
        int order = -1;
        Node attrNode = varAttrs.getNamedItem(VARIABLE_LABEL_ATTR);
        if (attrNode != null) label = VcsUtilities.getBundleString(attrNode.getNodeValue());
        attrNode = varAttrs.getNamedItem(VARIABLE_LABEL_MNEMONIC_ATTR);
        if (attrNode != null) labelMnemonic = VcsUtilities.getBundleString(attrNode.getNodeValue());
        attrNode = varAttrs.getNamedItem(VARIABLE_A11Y_NAME_ATTR);
        if (attrNode != null) a11yName = VcsUtilities.getBundleString(attrNode.getNodeValue());
        attrNode = varAttrs.getNamedItem(VARIABLE_A11Y_DESCRIPTION_ATTR);
        if (attrNode != null) a11yDescription = VcsUtilities.getBundleString(attrNode.getNodeValue());
        localFile = getBooleanAttrVariable(VARIABLE_LOCAL_FILE_ATTR, varAttrs);
        localDir = getBooleanAttrVariable(VARIABLE_LOCAL_DIR_ATTR, varAttrs);
        executable = getBooleanAttrVariable(VARIABLE_EXECUTABLE_ATTR, varAttrs);
        attrNode = varAttrs.getNamedItem(VARIABLE_ORDER_ATTR);
        if (attrNode != null) {
            try {
                order = Integer.parseInt(attrNode.getNodeValue());
            } catch (NumberFormatException exc) {
                order = -1;
            }
        }
        NodeList childList = varNode.getChildNodes();
        int n = childList.getLength();
        for (int i = 0; i < n; i++) {
            Node childNode = childList.item(i);
            if (VARIABLE_SELECTOR_TAG.equals(childNode.getNodeName())) {
                customSelector = childNode.getNodeValue();
                break;
            }
        }
        VcsConfigVariable var = new VcsConfigVariable(name, label, value, true, localFile, localDir, customSelector, order);
        var.setExecutable(executable);
        if (labelMnemonic != null && labelMnemonic.length() > 0) {
            var.setLabelMnemonic(new Character(labelMnemonic.charAt(0)));
        }
        if (a11yName != null) var.setA11yName(a11yName);
        if (a11yDescription != null) var.setA11yDescription(a11yDescription);
        return var;
    }

    /** Write list of VCS variables to the document.
     * If there is only value specified, label is empty string and basic, localFile
     * and localDir are false.
     */
    public static void writeVariables(Document doc, String label, ConditionedVariables vars) throws DOMException {
        writeVariables(doc, label, vars, null, null);
    }
    
    /** Write list of VCS variables to the document.
     * If there is only value specified, label is empty string and basic, localFile
     * and localDir are false.
     */
    public static void writeVariables(Document doc, String label, ConditionedVariables vars,
                                      Set compatibleOSs, Set uncompatibleOSs) throws DOMException {
        Element rootElem = doc.getDocumentElement(); //doc.createElement(CONFIG_ROOT_ELEM);
        //doc.appendChild(rootElem);
        Element labelNode = doc.createElement(LABEL_TAG);
        Text labelText = doc.createTextNode(label);
        labelNode.appendChild(labelText);
        rootElem.appendChild(labelNode);
        if (compatibleOSs != null && uncompatibleOSs != null) {
            if (compatibleOSs.size() > 0 || uncompatibleOSs.size() > 0) {
                Element osNode = doc.createElement(OS_TAG);
                putOSs(doc, osNode, compatibleOSs, uncompatibleOSs);
                rootElem.appendChild(osNode);
            }
        }
        Element varsNode = doc.createElement(VARIABLES_TAG);
        putVariables(doc, varsNode, vars);
        rootElem.appendChild(varsNode);
    }
    
    private static void putOSs(Document doc, Node osNode, Set compatibleOSs, Set uncompatibleOSs) throws DOMException {
        if (compatibleOSs.size() > 0) {
            Element compElem = doc.createElement(OS_COMPATIBLE_TAG);
            String compOS = VcsUtilities.arrayToQuotedStrings((String[]) new TreeSet(compatibleOSs).toArray(new String[0]));
            Text valueElem = doc.createTextNode(compOS);
            compElem.appendChild(valueElem);
            osNode.appendChild(compElem);
        }
        if (uncompatibleOSs.size() > 0) {
            Element uncompElem = doc.createElement(OS_UNCOMPATIBLE_TAG);
            String uncompOS = VcsUtilities.arrayToQuotedStrings((String[]) new TreeSet(uncompatibleOSs).toArray(new String[0]));
            Text valueElem = doc.createTextNode(uncompOS);
            uncompElem.appendChild(valueElem);
            osNode.appendChild(uncompElem);
        }
    }
    
    private static void putVariables(Document doc, Node varsNode, ConditionedVariables cvars) throws DOMException {
        Collection uncVars = cvars.getUnconditionedVariables();
        List vars = new ArrayList(new TreeSet(uncVars));
        int n = vars.size();
        for (int i = 0; i < n; i++) {
            VcsConfigVariable var = (VcsConfigVariable) vars.get(i);
            writeVariable(var, doc, varsNode, null, null);
        }
        Map conditionsByVariables = cvars.getConditionsByVariables();
        Map varsByConditions = cvars.getVariablesByConditions();
        for (Iterator it = conditionsByVariables.keySet().iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            Condition[] conditions = (Condition[]) conditionsByVariables.get(name);
            Condition[] subConditions = conditions[0].getConditions();
            Condition c = null;
            for (int i = 0; i < subConditions.length; i++) {
                if (conditions[0].isPositiveTest(subConditions[i])) {
                    c = subConditions[i];
                    break;
                }
            }
            Map valuesByConditions = new HashMap();
            VcsConfigVariable var = null;
            for (int i = 0; i < conditions.length; i++) {
                var = (VcsConfigVariable) varsByConditions.get(conditions[i]);
                String value = var.getValue();
                if (conditions[i].getVars().length == 0) {
                //if (subConditions.length > 0 && !conditions[i].isPositiveTest(subConditions[subConditions.length - 1])) {
                    // No condition is applied to the <value>
                    valuesByConditions.put(null, value);
                } else {
                    valuesByConditions.put(conditions[i], value);
                }
            }
            if (c == null && valuesByConditions.size() == 1) {
                c = (Condition) valuesByConditions.keySet().iterator().next();
                var.setValue((String) valuesByConditions.get(c));
                valuesByConditions = null;
            }
            writeVariable(var, doc, varsNode, c, valuesByConditions);
        }
    }
    
    private static Element writeVariable(VcsConfigVariable var, Document doc,
                                         Node varsNode, Condition c,
                                         Map valuesByConditions) {
        Element varElem = doc.createElement(VARIABLE_TAG);
        varElem.setAttribute(VARIABLE_NAME_ATTR, var.getName());
        if (valuesByConditions != null) {
            for (Iterator it = valuesByConditions.keySet().iterator(); it.hasNext(); ) {
                Condition vc = (Condition) it.next();
                String value = (String) valuesByConditions.get(vc);
                Element valueElem = addValue(doc, varElem, value);
                if (vc != null) setConditionAttributes(valueElem, vc);
            }
        } else {
            addValue(doc, varElem, var.getValue());
        }
        varElem.setAttribute(VARIABLE_BASIC_ATTR, (var.isBasic()) ? BOOLEAN_VARIABLE_TRUE : BOOLEAN_VARIABLE_FALSE);
        if (var.isBasic()) {
            varElem.setAttribute(VARIABLE_LABEL_ATTR, var.getLabel());
            if (var.getLabelMnemonic() != null) {
                varElem.setAttribute(VARIABLE_LABEL_MNEMONIC_ATTR, var.getLabelMnemonic().toString());
            }
            if (var.getA11yName() != null) {
                varElem.setAttribute(VARIABLE_A11Y_NAME_ATTR, var.getA11yName());
            }
            if (var.getA11yDescription() != null) {
                varElem.setAttribute(VARIABLE_A11Y_NAME_ATTR, var.getA11yDescription());
            }
            varElem.setAttribute(VARIABLE_LOCAL_FILE_ATTR, (var.isLocalFile()) ? BOOLEAN_VARIABLE_TRUE : BOOLEAN_VARIABLE_FALSE);
            varElem.setAttribute(VARIABLE_LOCAL_DIR_ATTR, (var.isLocalDir()) ? BOOLEAN_VARIABLE_TRUE : BOOLEAN_VARIABLE_FALSE);
            varElem.setAttribute(VARIABLE_EXECUTABLE_ATTR, (var.isLocalDir()) ? BOOLEAN_VARIABLE_TRUE : BOOLEAN_VARIABLE_FALSE);
            varElem.setAttribute(VARIABLE_ORDER_ATTR, ""+var.getOrder());
        }
        if (c != null) setConditionAttributes(varElem, c);
        String selector = var.getCustomSelector();
        if (selector != null && selector.length() > 0) {
            Element selectorElem = doc.createElement(VARIABLE_SELECTOR_TAG);
            selectorElem.setNodeValue(selector);
            varElem.appendChild(selectorElem);
        }
        varsNode.appendChild(varElem);
        return varElem;
    }
    
    /**
     * Set the conditional attributes to an element according to a condition.
     */
    public static void setConditionAttributes(Element element, Condition c) {
        Condition.Var[] cvars = c.getVars();
        for (int i = 0; i < cvars.length; i++) {
            if (c.isPositiveTest(cvars[i])) {
                element.setAttribute(VARIABLE_UNLESS_ATTR, cvars[i].getName());
            } else {
                element.setAttribute(VARIABLE_IF_ATTR, cvars[i].getName());
            }
        }
    }
    
    private static Element addValue(Document doc, Element varElem, String value) {
        Element valueElem = doc.createElement(VARIABLE_VALUE_TAG);
        Text valueNode = doc.createTextNode(value);
        valueElem.appendChild(valueNode);
        valueElem.setAttribute("xml:space","preserve"); // To preserve new lines (see issue #14163)
        varElem.appendChild(valueElem);
        //varElem.setNodeValue(var.getValue());
        return valueElem;
    }
    
    private static class LabelContentHandler extends Object implements ContentHandler, EntityResolver {
        
        private String label;
        private String compatibleOSs;
        private String uncompatibleOSs;
        boolean readLabel = false;
        boolean readCompatibleOSs = false;
        boolean readUncompatibleOSs = false;
        
        public void startDocument() throws org.xml.sax.SAXException {
            label = null;
            compatibleOSs = null;
            uncompatibleOSs = null;
        }
        
        public void endDocument() throws org.xml.sax.SAXException {
        }
        
        public void ignorableWhitespace(char[] values, int param, int param2) throws org.xml.sax.SAXException {
        }
        
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws org.xml.sax.SAXException {
            String elementName = ("".equals(localName)) ? qName : localName;
            //System.out.println("      startElement("+elementName+")");
            if (LABEL_TAG.equals(elementName)) {
                label = "";
                readLabel = true;
            } else if (OS_COMPATIBLE_TAG.equals(elementName)) {
                compatibleOSs = "";
                readCompatibleOSs = true;
            } else if (OS_UNCOMPATIBLE_TAG.equals(elementName)) {
                uncompatibleOSs = "";
                readUncompatibleOSs = true;
            } else if (label != null && !OS_TAG.equals(elementName)) {
                throw new org.xml.sax.SAXException("End of label.");
            }
        }
        
        public void endElement(String namespaceURI, String localName, String qName) throws org.xml.sax.SAXException {
            String elementName = ("".equals(localName)) ? qName : localName;
            //System.out.println("      startElement("+elementName+")");
            if (readLabel && LABEL_TAG.equals(elementName)) {
                readLabel = false;
                //throw new org.xml.sax.SAXException("End of label.");
            } else if (readCompatibleOSs && OS_COMPATIBLE_TAG.equals(elementName)) {
                readCompatibleOSs = false;
            } else if (readUncompatibleOSs && OS_UNCOMPATIBLE_TAG.equals(elementName)) {
                readUncompatibleOSs = false;
            } else if (OS_TAG.equals(elementName)) {
                throw new org.xml.sax.SAXException("End of label.");
            }
        }
        
        public void skippedEntity(java.lang.String str) throws org.xml.sax.SAXException {
        }
        
        public void processingInstruction(java.lang.String str, java.lang.String str1) throws org.xml.sax.SAXException {
        }
        
        public void endPrefixMapping(java.lang.String str) throws org.xml.sax.SAXException {
        }
        
        public void startPrefixMapping(java.lang.String str, java.lang.String str1) throws org.xml.sax.SAXException {
        }
        
        public void characters(char[] values, int start, int length) throws org.xml.sax.SAXException {
            //System.out.println("   characters("+new String(values, start, length)+"), readLabel = "+readLabel);
            if (readLabel) {
                label += new String(values, start, length);
            } else if (readCompatibleOSs) {
                compatibleOSs += new String(values, start, length);
            } else if (readUncompatibleOSs) {
                uncompatibleOSs += new String(values, start, length);
            }
        }
        
        public void setDocumentLocator(org.xml.sax.Locator locator) {
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getCompatibleOSs() {
            return compatibleOSs;
        }
        
        public String getUncompatibleOSs() {
            return uncompatibleOSs;
        }
        
        public InputSource resolveEntity(String pubid, String sysid) throws org.xml.sax.SAXException, java.io.IOException {
            if (pubid.equals(PUBLIC_ID)) {
                if (VALIDATE_XML) {
                    // We certainly know where to get this from.
                    return new InputSource("nbres:/vcs/config/configuration-1_0.dtd"); // NOI18N
                } else {
                    // Not validating, don't load any DTD! Significantly faster.
                    return new InputSource(new java.io.ByteArrayInputStream(new byte[0]));
                }
            } else {
                // Otherwise try the standard places.
                return org.openide.xml.EntityCatalog.getDefault().resolveEntity(pubid, sysid);
            }
        }
        
    }
    
    private static String g(String s) {
        return NbBundle.getBundle(VariableIO.class).getString (s);
    }
    private static String g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
}
