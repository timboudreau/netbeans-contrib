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

package org.netbeans.modules.vcs.advanced.commands;

import java.util.ArrayList;

import org.w3c.dom.*;

import org.openide.nodes.Children;

import org.netbeans.modules.vcscore.commands.VcsCommandNode;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.CommandExecutorSupport;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.variables.VariableIO;

/**
 * This class provides input/output of commands from/to xml file.
 *
 * @author  Martin Entlicher
 */
public class UserCommandIO extends Object {
    
    //public static final String LABEL_TAG = "label";
    public static final String COMMANDS_TAG = "commands";
    public static final String COMMAND_TAG = "command";
    public static final String COMMAND_NAME_ATTR = "name";
    public static final String COMMAND_DISPLAY_NAME_ATTR = "displayName";
    public static final String SEPARATOR_TAG = "separator";
    public static final String PROPERTY_TAG = "property";
    public static final String PROPERTY_NAME_ATTR = "name";
    public static final String PROPERTY_VALUE_TAG = "value";
        
    private static final String ROOT_CMD_NAME = "ROOT_CMD";

    /** Creates new UserCommandIO, since it contains only static methods,
     * the class should never be instantiated. */
    private UserCommandIO() {
    }
    
    private static String convertStringArray2String(String[] array) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            String item;
            if (array[i] != null) {
                item = org.openide.util.Utilities.replaceString(array[i], "/", "//");
            } else {
                item = "";
            }
            buf.append(item);
            if (i < array.length - 1) buf.append("/");
        }
        return buf.toString();
    }
    
    private static String[] convertString2StringArray(String str) {
        ArrayList list = new ArrayList();
        for (int index = 0; index < str.length(); ) {
            int delim;
            while (true) {
                delim = str.indexOf("/", index);
                if (delim < 0) delim = str.length();
                else if (delim < str.length() && str.charAt(delim + 1) == '/') continue;
                break;
            }
            list.add(org.openide.util.Utilities.replaceString(str.substring(index, delim), "//", "/"));
            index = delim + 1;
        }
        if (list.size() == 0) {
            return new String[1];
        } else {
            return (String[]) list.toArray(new String[0]);
        }
    }
    
    private static Object getPropertyValue(String name, String valueStr) {
        Class type = CommandNode.getPropertyClass(name);
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
        } else if (String.class.equals(type)) {
            return VcsUtilities.getBundleString(valueStr);
        } else if (String[].class.equals(type)) {
            return convertString2StringArray(valueStr);
        } else return valueStr;
    }
    
    private static String getPropertyValueStr(String name, Object value) {
        if (name.indexOf(VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY) >= 0) return null;
        Class type = CommandNode.getPropertyClass(name);
        if (String[].class.equals(type)) {
            return convertStringArray2String((String[]) value);
        }
        return value.toString();
    }

    /**
     * Read the commands definitions from the document and create the tree of commands.
     */
    public static org.openide.nodes.Node readCommands(Document doc) throws DOMException {
        Element rootElem = doc.getDocumentElement();
        if (!VariableIO.CONFIG_ROOT_ELEM.equals(rootElem.getNodeName())) return null;
        VcsCommandNode rootCommandNode = null;
        NodeList labelList = rootElem.getElementsByTagName(VariableIO.LABEL_TAG);
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
        UserCommand rootCmd = new UserCommand();
        rootCmd.setName(ROOT_CMD_NAME);
        rootCmd.setDisplayName(label);
        Children children = new Children.Array();
        rootCommandNode = new VcsCommandNode(children, rootCmd);
        NodeList commandsList = rootElem.getElementsByTagName(COMMANDS_TAG);
        if (commandsList.getLength() > 0) {
            Node commands = commandsList.item(0);
            commandsList = commands.getChildNodes();
            getCommands(children, commandsList);
        }
        return rootCommandNode;
    }
    
    private static void getCommands(Children children, NodeList commandsList) throws DOMException {
        int n = commandsList.getLength();
        for (int i = 0; i < n; i++) {
            Node commandNode = commandsList.item(i);
            if (SEPARATOR_TAG.equals(commandNode.getNodeName())) {
                children.add(new org.openide.nodes.Node[] { new VcsCommandNode(Children.LEAF, null) });
                continue;
            }
            if (!COMMAND_TAG.equals(commandNode.getNodeName())) continue; // Ignore nodes that does not contain commands
            NamedNodeMap attrs = commandNode.getAttributes();
            if (attrs.getLength() == 0) continue;
            Node nameNode = attrs.getNamedItem(COMMAND_NAME_ATTR);
            if (nameNode == null) continue;
            UserCommand cmd = new UserCommand();
            cmd.setName(nameNode.getNodeValue());
            //cmd.setName(nameNode.getNodeValue());
            nameNode = attrs.getNamedItem(COMMAND_DISPLAY_NAME_ATTR);
            if (nameNode != null) {
                String displayName = VcsUtilities.getBundleString(nameNode.getNodeValue());
                cmd.setDisplayName(displayName);
            } else cmd.setDisplayName(null);
            NodeList propertiesAndSubCommands = commandNode.getChildNodes();
            int m = propertiesAndSubCommands.getLength();
            boolean subcommandsExist = false;
            for (int j = 0; j < m; j++) {
                Node propertyNode = propertiesAndSubCommands.item(j);
                String name = propertyNode.getNodeName();
                if (PROPERTY_TAG.equals(name)) {
                    String propertyName = null;
                    NamedNodeMap propAttrs = propertyNode.getAttributes();
                    if (propAttrs.getLength() > 0) {
                        Node nameAttr = propAttrs.getNamedItem(PROPERTY_NAME_ATTR);
                        if (nameAttr != null) {
                            propertyName = nameAttr.getNodeValue();
                        }
                    }
                    if (propertyName == null) continue;
                    String propertyValue = "";
                    NodeList valueList = propertyNode.getChildNodes();
                    int vln = valueList.getLength();
                    for (int k = 0; k < vln; k++) {
                        Node valueNode = valueList.item(k);
                        if (PROPERTY_VALUE_TAG.equals(valueNode.getNodeName())) {
                            NodeList textList = valueNode.getChildNodes();
                            //System.out.println("property = "+propertyName+", textList.getLength() = "+textList.getLength());
                            for (int itl = 0; itl < textList.getLength(); itl++) {
                                Node subNode = textList.item(itl);
                                //System.out.println("    subNode = "+subNode);
                                if (subNode instanceof Text) {
                                    Text textNode = (Text) subNode;
                                    propertyValue += textNode.getData();
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
                                            propertyValue += textEntityNode.getData();
                                        }
                                    }
                                }
                                //System.out.println("    propertyValue = "+propertyValue);
                            }
                            /*
                            if (textList.getLength() > 0) {
                                Node subNode = textList.item(0);
                                if (subNode instanceof Text) {
                                    Text textNode = (Text) subNode;
                                    propertyValue = textNode.getData();
                                }
                            }
                             */
                        }
                    }
                    cmd.setProperty(propertyName, getPropertyValue(propertyName, propertyValue));
                } else if (COMMAND_TAG.equals(name)) {
                    subcommandsExist = true;
                }
            }
            if (subcommandsExist) {
                Children subChildren = new Children.Array();
                getCommands(subChildren, propertiesAndSubCommands);
                children.add(new org.openide.nodes.Node[] { new VcsCommandNode(subChildren, cmd) });
            } else {
                children.add(new org.openide.nodes.Node[] { new VcsCommandNode(Children.LEAF, cmd) });
            }
        }
    }

    /**
     * Write the commands definitions to the document from the tree of commands.
     */
    public static boolean writeCommands(Document doc, org.openide.nodes.Node rootCommandNode) throws DOMException {
        Element rootElem = doc.getDocumentElement();
        if (!VariableIO.CONFIG_ROOT_ELEM.equals(rootElem.getNodeName())) return false;
        /*
        VcsCommand cmd = (VcsCommand) rootCommandNode.getCookie(VcsCommand.class);
        if (cmd != null) {
            Element labelElm = doc.createElement(LABEL_TAG);
            labelElm.setNodeValue(cmd.getDisplayName());
            doc.appendChild(labelElm);
        }
         */
        Element commandsElm = doc.createElement(COMMANDS_TAG);
        putCommands(doc, commandsElm, rootCommandNode);
        rootElem.appendChild(commandsElm);
        return true;
    }
    
    private static void putCommands(Document doc, org.w3c.dom.Node commands, org.openide.nodes.Node commandsNode) {
        Children children = commandsNode.getChildren();
        org.openide.nodes.Node[] commandNodes = children.getNodes();
        for (int i = 0; i < commandNodes.length; i++) {
            org.openide.nodes.Node commandNode = commandNodes[i];
            VcsCommand cmd = (VcsCommand) commandNode.getCookie(VcsCommand.class);
            if (cmd == null) {
                Element separatorElem = doc.createElement(SEPARATOR_TAG);
                commands.appendChild(separatorElem);
                continue;
            }
            Element commandElm = doc.createElement(COMMAND_TAG);
            commandElm.setAttribute(COMMAND_NAME_ATTR, cmd.getName());
            String displayName = cmd.getDisplayName();
            if (displayName != null) commandElm.setAttribute(COMMAND_DISPLAY_NAME_ATTR, displayName);
            String[] properties = cmd.getPropertyNames();
            for (int j = 0; j < properties.length; j++) {
                Object value = cmd.getProperty(properties[j]);
                if (value == null) continue;
                String valueStr = getPropertyValueStr(properties[j], value);
                if (valueStr == null) continue;
                Element propertiesElm = doc.createElement(PROPERTY_TAG);
                propertiesElm.setAttribute(PROPERTY_NAME_ATTR, properties[j]);
                Element propValueElem = doc.createElement(PROPERTY_VALUE_TAG);
                Text valueText = doc.createTextNode(valueStr);
                propValueElem.appendChild(valueText);
                propValueElem.setAttribute("xml:space","preserve"); // To preserve new lines (see issue #14163)
                propertiesElm.appendChild(propValueElem);
                commandElm.appendChild(propertiesElm);
            }
            if (!commandNode.isLeaf()) putCommands(doc, commandElm, commandNode);
            commands.appendChild(commandElm);
        }
    }
}
