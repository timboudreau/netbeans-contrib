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

package org.netbeans.modules.vcs.advanced.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.*;

import org.openide.ErrorManager;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandExecutorSupport;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedCommand;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedPropertiesCommand;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedProperty;
import org.netbeans.modules.vcs.advanced.conditioned.ConditionedStructuredExec;
import org.netbeans.modules.vcs.advanced.variables.Condition;
import org.netbeans.modules.vcs.advanced.variables.VariableIO;

/**
 * This class provides input/output of commands from/to xml file.
 *
 * @author  Martin Entlicher
 */
public class UserCommandIO extends Object {
    
    //public static final String LABEL_TAG = "label";
    public static final String COMMANDS_TAG = "commands";                   // NOI18N
    public static final String GLOBAL_COMMANDS_TAG = "globalCommands";      // NOI18N
    public static final String COMMAND_TAG = "command";                     // NOI18N
    public static final String COMMAND_NAME_ATTR = "name";                  // NOI18N
    public static final String COMMAND_DISPLAY_NAME_ATTR = "displayName";   // NOI18N
    public static final String SEPARATOR_TAG = "separator";                 // NOI18N
    public static final String PROPERTY_TAG = "property";                   // NOI18N
    public static final String PROPERTY_NAME_ATTR = "name";                 // NOI18N
    public static final String PROPERTY_VALUE_TAG = "value";                // NOI18N
    public static final String RUN_TAG = "run";                             // NOI18N
    public static final String RUN_DIR_ATTR = "dir";                        // NOI18N
    public static final String EXEC_TAG = "executable";                     // NOI18N
    public static final String EXEC_VALUE_ATTR = "value";                   // NOI18N
    public static final String ARG_TAG = "arg";                             // NOI18N
    public static final String ARG_VALUE_ATTR = "value";                    // NOI18N
    public static final String ARG_LINE_ATTR = "line";                      // NOI18N
    public static final String IF_ATTR = "if";                              // NOI18N
    public static final String UNLESS_ATTR = "unless";                      // NOI18N
        
    private static final String ROOT_CMD_NAME = "ROOT_CMD";                 // NOI18N

    /** Creates new UserCommandIO, since it contains only static methods,
     * the class should never be instantiated. */
    private UserCommandIO() {
    }
    
    public static String convertStringArray2String(String[] array) {
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
    
    public static String[] convertString2StringArray(String str) {
        ArrayList list = new ArrayList();
        for (int index = 0; index < str.length(); ) {
            int delim;
            int pos = index;
            while (true) {
                delim = str.indexOf("/", pos);
                if (delim < 0) delim = str.length();
                else if (delim < str.length() && str.charAt(delim + 1) == '/') {
                    pos = delim + 2;
                    continue;
                }
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
    
    public static Object getPropertyValue(String name, String valueStr) {
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
    public static ConditionedCommands readCommands(Document doc, CommandExecutionContext fileSystem) throws DOMException {
        Element rootElem = doc.getDocumentElement();
        if (!VariableIO.CONFIG_ROOT_ELEM.equals(rootElem.getNodeName())) return null;
        NodeList labelList = rootElem.getElementsByTagName(VariableIO.LABEL_TAG);
        Node labelNode = null;
        if (labelList.getLength() > 0) {
            labelNode = labelList.item(0);
        }
        NodeList commandsList = rootElem.getElementsByTagName(COMMANDS_TAG);
        if (commandsList.getLength() > 0) {
            Node commands = commandsList.item(0);
            commandsList = commands.getChildNodes();
            //getCommands(children, commandsList);
        } else commandsList = null;
        return readCommands(labelNode, commandsList, fileSystem);
    }
    
    /**
     * Read the global commands definitions from the document and create the tree of commands.
     */
    public static ConditionedCommands readGlobalCommands(Document doc) throws DOMException {
        Element rootElem = doc.getDocumentElement();
        if (!VariableIO.CONFIG_ROOT_ELEM.equals(rootElem.getNodeName())) return null;
        NodeList labelList = rootElem.getElementsByTagName(VariableIO.LABEL_TAG);
        Node labelNode = null;
        if (labelList.getLength() > 0) {
            labelNode = labelList.item(0);
        }
        NodeList commandsList = rootElem.getElementsByTagName(GLOBAL_COMMANDS_TAG);
        if (commandsList.getLength() > 0) {
            Node commands = commandsList.item(0);
            commandsList = commands.getChildNodes();
            //getCommands(children, commandsList);
        } else commandsList = null;
        return readCommands(labelNode, commandsList, null);
    }
    
    public static ConditionedCommands readCommands(Node labelNode, NodeList commandsList,
                                                   CommandExecutionContext execContext) throws DOMException {
        CommandsTree rootCommandNode = null;
        String label = "";
        if (labelNode != null) {
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
        rootCommandNode = new CommandsTree(new UserCommandSupport(rootCmd, execContext));
        ConditionedCommandsBuilder ccb = new ConditionedCommandsBuilder(rootCommandNode);
        if (commandsList != null) getCommands(rootCommandNode, ccb, commandsList,
                                              execContext);
        return ccb.getConditionedCommands();
    }
    
    private static void getCommands(CommandsTree cmdTree, ConditionedCommandsBuilder ccb,
                                    NodeList commandsList, CommandExecutionContext execContext) throws DOMException {
        int n = commandsList.getLength();
        for (int i = 0; i < n; i++) {
            Node commandNode = commandsList.item(i);
            if (SEPARATOR_TAG.equals(commandNode.getNodeName())) {
                cmdTree.add(CommandsTree.EMPTY);
                //children.add(new org.openide.nodes.Node[] { new VcsCommandNode(Children.LEAF, null) });
                continue;
            }
            if (!COMMAND_TAG.equals(commandNode.getNodeName())) continue; // Ignore nodes that does not contain commands
            NamedNodeMap attrs = commandNode.getAttributes();
            if (attrs.getLength() == 0) continue;
            Node nameNode = attrs.getNamedItem(COMMAND_NAME_ATTR);
            if (nameNode == null) continue;
            UserCommand cmd = new UserCommand();
            String name = nameNode.getNodeValue();
            cmd.setName(name);
            //cmd.setName(nameNode.getNodeValue());
            nameNode = attrs.getNamedItem(COMMAND_DISPLAY_NAME_ATTR);
            if (nameNode != null) {
                String displayName = VcsUtilities.getBundleString(nameNode.getNodeValue());
                cmd.setDisplayName(displayName);
            } else cmd.setDisplayName(null);
            String ifAttr = "";
            String unlessAttr = "";
            Node ifNode = attrs.getNamedItem(IF_ATTR);
            if (ifNode != null) ifAttr = ifNode.getNodeValue();
            Node unlessNode = attrs.getNamedItem(UNLESS_ATTR);
            if (unlessNode != null) unlessAttr = unlessNode.getNodeValue();
            Condition c = VariableIO.createCondition(name, ifAttr, unlessAttr);
            
            NodeList propertiesAndSubCommands = commandNode.getChildNodes();
            List conditionedProperties = new ArrayList();
            boolean subcommandsExist = getProperties(cmd, propertiesAndSubCommands,
                                                     conditionedProperties);
            UserCommandSupport cmdSupp = new UserCommandSupport(cmd, execContext);
            if (subcommandsExist) {
                CommandsTree subTree = new CommandsTree(cmdSupp);
                cmdTree.add(subTree);
                getCommands(subTree, ccb,
                            propertiesAndSubCommands, execContext);
            } else {
                cmdTree.add(new CommandsTree(cmdSupp));
            }
            if (c != null) ccb.addConditionedCommand(cmdSupp, c);
            if (conditionedProperties.size() > 0) {
                for (Iterator it = conditionedProperties.iterator(); it.hasNext(); ) {
                    ConditionedCommandsBuilder.ConditionedProperty property =
                        (ConditionedCommandsBuilder.ConditionedProperty) it.next();
                    ccb.addPropertyToCommand(name, c, property);
                }
            }
        }
    }
    
    private static boolean getProperties(UserCommand cmd, NodeList propertiesAndSubCommands,
                                         Collection conditionedProperties) {
        int m = propertiesAndSubCommands.getLength();
        boolean subcommandsExist = false;
        for (int j = 0; j < m; j++) {
            Node propertyNode = propertiesAndSubCommands.item(j);
            String name = propertyNode.getNodeName();
            if (PROPERTY_TAG.equals(name)) {
                readProperty(propertyNode, cmd, conditionedProperties);
            } else if (RUN_TAG.equals(name)) {
                readStructuredExec(propertyNode, cmd, conditionedProperties);
            } else if (COMMAND_TAG.equals(name)) {
                subcommandsExist = true;
            }
        }
        return subcommandsExist;
    }

    private static void readProperty(Node propertyNode, UserCommand cmd, Collection conditionedProperties) throws DOMException {
        String propertyName = null;
        NamedNodeMap propAttrs = propertyNode.getAttributes();
        if (propAttrs.getLength() > 0) {
            Node nameAttr = propAttrs.getNamedItem(PROPERTY_NAME_ATTR);
            if (nameAttr != null) {
                propertyName = nameAttr.getNodeValue();
            }
        }
        if (propertyName == null) return ;
        String ifAttr = "";
        String unlessAttr = "";
        Node ifNode = propAttrs.getNamedItem(IF_ATTR);
        if (ifNode != null) ifAttr = ifNode.getNodeValue();
        Node unlessNode = propAttrs.getNamedItem(UNLESS_ATTR);
        if (unlessNode != null) unlessAttr = unlessNode.getNodeValue();
        Map valuesByConditions = new IdentityHashMap();
        Condition c = VariableIO.createCondition(cmd.getName() + "/" + propertyName, ifAttr, unlessAttr);

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
                String valueIfAttr = "";
                String valueUnlessAttr = "";
                NamedNodeMap valueAttrs = valueNode.getAttributes();
                if (valueAttrs != null) {
                    Node valueIfNode = valueAttrs.getNamedItem(IF_ATTR);
                    if (valueIfNode != null) valueIfAttr = valueIfNode.getNodeValue();
                    Node valueUnlessNode = valueAttrs.getNamedItem(UNLESS_ATTR);
                    if (valueUnlessNode != null) valueUnlessAttr = valueUnlessNode.getNodeValue();
                }
                Condition vc = VariableIO.createCondition(cmd.getName() + "/" + propertyName, valueIfAttr, valueUnlessAttr);
                propertyValue = translateCommandProperty(propertyName, propertyValue);
                valuesByConditions.put(vc, getPropertyValue(propertyName, propertyValue));
                propertyValue = "";
            }
        }
        Object value = valuesByConditions.get(null);
        if (value != null) {
            Condition cond = c;
            if (valuesByConditions.size() > 1) {
                cond = VariableIO.createComplementaryCondition(cmd.getName() + "/" + propertyName, c, valuesByConditions.keySet());
            }
            if (cond != null) {
                valuesByConditions.put(cond, value);
                valuesByConditions.remove(null);
            } else {
                valuesByConditions = null;
            }
        }
        if (valuesByConditions == null) {
            cmd.setProperty(propertyName, value);
        } else {
            if (c != null) {
                for (Iterator it = valuesByConditions.keySet().iterator(); it.hasNext(); ) {
                    Condition vc = (Condition) it.next();
                    if (!vc.equals(c)) {
                        vc.addCondition(c, true);
                    }
                }
            }
            conditionedProperties.add(
                new ConditionedCommandsBuilder.ConditionedProperty(propertyName,
                                                                   c,
                                                                   valuesByConditions));
        }
    }
    
    private static void readStructuredExec(Node runNode, UserCommand cmd, Collection conditionedProperties) throws DOMException {
        String dir = null;
        NamedNodeMap dirAttrs = runNode.getAttributes();
        if (dirAttrs.getLength() > 0) {
            Node dirAttr = dirAttrs.getNamedItem(RUN_DIR_ATTR);
            if (dirAttr != null) {
                dir = dirAttr.getNodeValue();
            }
        }
        String ifAttr = "";
        String unlessAttr = "";
        Node ifNode = dirAttrs.getNamedItem(IF_ATTR);
        if (ifNode != null) ifAttr = ifNode.getNodeValue();
        Node unlessNode = dirAttrs.getNamedItem(UNLESS_ATTR);
        if (unlessNode != null) unlessAttr = unlessNode.getNodeValue();
        Condition c = VariableIO.createCondition(cmd.getName() + "/" + VcsCommand.PROPERTY_EXEC_STRUCTURED, ifAttr, unlessAttr);
        
        Map execsByConditions = new IdentityHashMap();
        //StructuredExec exec = new StructuredExec((dir != null) ? new java.io.File(dir) : null, "", new StructuredExec.Argument[0]);
        NodeList execArgsList = runNode.getChildNodes();
        int ean = execArgsList.getLength();
        for (int k = 0; k < ean; k++) {
            Node eaNode = execArgsList.item(k);
            if (EXEC_TAG.equals(eaNode.getNodeName())) {
                NamedNodeMap execAttributes = eaNode.getAttributes();
                if (execAttributes == null) continue;
                Node execValue = execAttributes.getNamedItem(EXEC_VALUE_ATTR);
                if (execValue == null) continue;
                String execStr = execValue.getNodeValue();
                StructuredExec exec = new StructuredExec((dir != null) ? new java.io.File(dir) : null, execStr, new StructuredExec.Argument[0]);
                String execIfAttr = "";
                String execUnlessAttr = "";
                Node execIfNode = execAttributes.getNamedItem(IF_ATTR);
                if (execIfNode != null) execIfAttr = execIfNode.getNodeValue();
                Node execUnlessNode = execAttributes.getNamedItem(UNLESS_ATTR);
                if (execUnlessNode != null) execUnlessAttr = execUnlessNode.getNodeValue();
                Condition ec = VariableIO.createCondition(cmd.getName() + "/" + VcsCommand.PROPERTY_EXEC_STRUCTURED + "/executor",
                                                          execIfAttr, execUnlessAttr);
                execsByConditions.put(ec, exec);
            } else if (ARG_TAG.equals(eaNode.getNodeName())) {
                NamedNodeMap argAttributes = eaNode.getAttributes();
                if (argAttributes == null) continue;
                Node argValue = argAttributes.getNamedItem(ARG_VALUE_ATTR);
                Node argLine = argAttributes.getNamedItem(ARG_LINE_ATTR);
                //String argValueStr = null;
                String argStr = null;
                boolean line = false;
                if (argValue != null) {
                    argStr = argValue.getNodeValue();
                } else if (argLine != null) {
                    argStr = argLine.getNodeValue();
                    line = true;
                }
                //if (argValueStr == null && argLineStr == null) continue;
                if (argStr == null) continue;
                
                String argIfAttr = "";
                String argUnlessAttr = "";
                Node argIfNode = argAttributes.getNamedItem(IF_ATTR);
                if (argIfNode != null) argIfAttr = argIfNode.getNodeValue();
                Node argUnlessNode = argAttributes.getNamedItem(UNLESS_ATTR);
                if (argUnlessNode != null) argUnlessAttr = argUnlessNode.getNodeValue();
                Condition ac = VariableIO.createCondition(cmd.getName() + "/" + VcsCommand.PROPERTY_EXEC_STRUCTURED + "/arg",
                                                          argIfAttr, argUnlessAttr);
                StructuredExec.Argument arg;
                if (ac == null) {
                    arg = new StructuredExec.Argument(argStr, line);
                } else {
                    arg = new ConditionedStructuredExec.ConditionedArgument(ac, argStr, line);
                }
                //for (Iterator it = execsByConditions.values().iterator(); it.hasNext(); ) {
                for (Iterator it = execsByConditions.keySet().iterator(); it.hasNext(); ) {
                    Condition cc = (Condition) it.next();
                    
                    StructuredExec exec = (StructuredExec) execsByConditions.get(cc);
                    if (arg instanceof ConditionedStructuredExec.ConditionedArgument &&
                        !(exec instanceof ConditionedStructuredExec)) {
                        
                        exec = new ConditionedStructuredExec(exec.getWorking(), exec.getExecutable(), exec.getArguments());
                        execsByConditions.put(cc, exec);
                    }
                    exec.addArgument(arg);
                }
            }
        }
        if (execsByConditions.size() == 1 && execsByConditions.get(null) != null) {
            cmd.setProperty(VcsCommand.PROPERTY_EXEC_STRUCTURED, execsByConditions.get(null));
        } else {
            if (c != null) {
                for (Iterator it = execsByConditions.keySet().iterator(); it.hasNext(); ) {
                    Condition vc = (Condition) it.next();
                    if (!vc.equals(c)) {
                        vc.addCondition(c, true);
                    }
                }
            }
            conditionedProperties.add(
                new ConditionedCommandsBuilder.ConditionedProperty(VcsCommand.PROPERTY_EXEC_STRUCTURED,
                                                                   c,
                                                                   execsByConditions));
        }
    }
    
    /**
     * Write the commands definitions to the document from the tree of commands.
     */
    public static boolean writeCommands(Document doc, CommandsTree commands) throws DOMException {
        return writeCommands(doc, null, commands);
    }
    
    /**
     * Write the commands definitions to the document from the tree of commands.
     */
    public static boolean writeCommands(Document doc, ConditionedCommands commands) throws DOMException {
        return writeCommands(doc, commands, commands.getCommands());
    }
    
    private static boolean writeCommands(Document doc, ConditionedCommands commands,
                                         CommandsTree tree) throws DOMException {
        Element rootElem = doc.getDocumentElement();
        if (!VariableIO.CONFIG_ROOT_ELEM.equals(rootElem.getNodeName())) return false;
        Element commandsElm = doc.createElement(COMMANDS_TAG);
        putCommands(doc, commandsElm, commands, tree, null);
        rootElem.appendChild(commandsElm);
        return true;
    }
    
    /**
     * Write the global commands definitions to the document from the tree of commands.
     */
    public static boolean writeGlobalCommands(Document doc, ConditionedCommands commands) throws DOMException {
        Element rootElem = doc.getDocumentElement();
        if (!VariableIO.CONFIG_ROOT_ELEM.equals(rootElem.getNodeName())) return false;
        Element commandsElm = doc.createElement(GLOBAL_COMMANDS_TAG);
        putCommands(doc, commandsElm, commands,
                    commands.getCommands(), null);
        rootElem.appendChild(commandsElm);
        return true;
    }
    
    private static void putCommands(Document doc, org.w3c.dom.Node commands,
                                    ConditionedCommands cc, CommandsTree commandsNode,
                                    Collection notFoundConditions) {
        if (notFoundConditions == null) notFoundConditions = new HashSet();
        CommandsTree[] commandChildren = commandsNode.children();
        for (int i = 0; i < commandChildren.length; i++) {
            CommandsTree commandNode = commandChildren[i];
            CommandSupport supp = commandNode.getCommandSupport();
            //VcsCommand cmd = (VcsCommand) commandNode.getCookie(VcsCommand.class);
            if (supp == null || !(supp instanceof UserCommandSupport)) {
                Element separatorElem = doc.createElement(SEPARATOR_TAG);
                commands.appendChild(separatorElem);
                continue;
            }
            UserCommandSupport uSupport = (UserCommandSupport) supp;
            VcsCommand cmd = uSupport.getVcsCommand();
            ConditionedCommand ccmd = null;
            if (cc != null) {
                ccmd = cc.getConditionedCommand(cmd.getName());
            }
            if (ccmd != null) {
                Condition[] conditions = ccmd.getConditions();
                for (int ic = 0; ic < conditions.length; ic++) {
                    ConditionedPropertiesCommand cpcmd = ccmd.getCommandFor(conditions[ic]);
                    // Test the command so that we find the right one!
                    if (!uSupport.equals(cpcmd.getCommand())) continue;
                    
                    Element commandElm = doc.createElement(COMMAND_TAG);
                    commandElm.setAttribute(COMMAND_NAME_ATTR, cmd.getName());
                    String displayName = cmd.getDisplayName();
                    if (displayName != null) commandElm.setAttribute(COMMAND_DISPLAY_NAME_ATTR, displayName);
                    if (conditions[ic] != null) VariableIO.setConditionAttributes(commandElm, conditions[ic]);
                    putProperties(doc, commandElm, cpcmd);
                    if (commandNode.hasChildren()) putCommands(doc, commandElm, cc, commandNode, notFoundConditions);
                    commands.appendChild(commandElm);
                }
            } else {
                Element commandElm = doc.createElement(COMMAND_TAG);
                commandElm.setAttribute(COMMAND_NAME_ATTR, cmd.getName());
                String displayName = cmd.getDisplayName();
                if (displayName != null) commandElm.setAttribute(COMMAND_DISPLAY_NAME_ATTR, displayName);
                putProperties(doc, commandElm, cmd);
                if (commandNode.hasChildren()) putCommands(doc, commandElm, cc, commandNode, notFoundConditions);
                commands.appendChild(commandElm);
            }
        }
    }
    
    private static void putProperties(Document doc, Element commandElm, VcsCommand cmd) throws DOMException {
        String[] properties = cmd.getPropertyNames();
        Arrays.sort(properties);
        for (int j = 0; j < properties.length; j++) {
            Object value = cmd.getProperty(properties[j]);
            if (value == null) continue;
            if (VcsCommand.PROPERTY_EXEC_STRUCTURED.equals(properties[j])) {
                putStructuredExec(doc, commandElm, (StructuredExec) value);
                continue;
            }
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
    }
    
    private static void putProperties(Document doc, Element commandElm,
                                      ConditionedPropertiesCommand cpcmd) throws DOMException {
        VcsCommand cmd = cpcmd.getCommand().getVcsCommand();
        String[] allProperties;
        ConditionedProperty[] cproperties = cpcmd.getConditionedProperties();
        Map conditionedPropertiesByNames;
        if (cproperties != null) {
            List properties = new ArrayList(Arrays.asList(cmd.getPropertyNames()));
            conditionedPropertiesByNames = new HashMap();
            for (int i = 0; i < cproperties.length; i++) {
                String name = cproperties[i].getName();
                if (!properties.contains(name)) properties.add(name);
                conditionedPropertiesByNames.put(name, cproperties[i]);
            }
            allProperties = (String[]) properties.toArray(new String[properties.size()]);
        } else {
            conditionedPropertiesByNames = Collections.EMPTY_MAP;
            allProperties = cmd.getPropertyNames();
        }
        Arrays.sort(allProperties);
        for (int i = 0; i < allProperties.length; i++) {
            ConditionedProperty property =
                (ConditionedProperty) conditionedPropertiesByNames.get(allProperties[i]);
            if (property != null) {
                Condition c = property.getCondition();
                Element propertyElm;
                if (VcsCommand.PROPERTY_EXEC_STRUCTURED.equals(allProperties[i])) {
                    propertyElm = putStructuredExec(doc, commandElm, c,
                                                    property.getValuesByConditions());
                } else {
                    propertyElm = putProperty(doc, commandElm, allProperties[i], c,
                                              property.getValuesByConditions());
                }
                if (c != null) {
                    VariableIO.setConditionAttributes(propertyElm, c);
                }
            } else {
                Object value = cmd.getProperty(allProperties[i]);
                if (value == null) continue;
                if (VcsCommand.PROPERTY_EXEC_STRUCTURED.equals(allProperties[i])) {
                    putStructuredExec(doc, commandElm, (StructuredExec) value);
                    continue;
                }
                String valueStr = getPropertyValueStr(allProperties[i], value);
                if (valueStr == null) continue;
                putProperty(doc, commandElm, allProperties[i], valueStr);
            }
        }
    }
    
    private static Element putProperty(Document doc, Element commandElm,
                                       String name, String value) throws DOMException {
        Element propertyElm = doc.createElement(PROPERTY_TAG);
        propertyElm.setAttribute(PROPERTY_NAME_ATTR, name);
        addPropertyValue(doc, propertyElm, value);
        commandElm.appendChild(propertyElm);
        return propertyElm;
    }
    
    private static Element putProperty(Document doc, Element commandElm, String name,
                                       Condition c, Map valuesByConditions) throws DOMException {
        Element propertyElm = doc.createElement(PROPERTY_TAG);
        propertyElm.setAttribute(PROPERTY_NAME_ATTR, name);
        for (Iterator it = valuesByConditions.keySet().iterator(); it.hasNext(); ) {
            Condition vc = (Condition) it.next();
            Object value = valuesByConditions.get(vc);
            String valueStr = getPropertyValueStr(name, value);
            if (valueStr == null) continue;
            Element valueElem = addPropertyValue(doc, propertyElm, valueStr);
            if (vc != null && !vc.equals(c)) VariableIO.setConditionAttributes(valueElem, vc);
        }
        commandElm.appendChild(propertyElm);
        return propertyElm;
    }
    
    private static Element addPropertyValue(Document doc, Element propertyElm, String value) {
        Element propValueElem = doc.createElement(PROPERTY_VALUE_TAG);
        Text valueNode = doc.createTextNode(value);
        propValueElem.appendChild(valueNode);
        propValueElem.setAttribute("xml:space","preserve"); // To preserve new lines (see issue #14163)
        propertyElm.appendChild(propValueElem);
        return propValueElem;
    }
    
    private static Element putStructuredExec(Document doc, Element commandElm, StructuredExec exec) throws DOMException {
        Element runElm = doc.createElement(RUN_TAG);
        if (exec.getWorking() != null) {
            runElm.setAttribute(RUN_DIR_ATTR, exec.getWorking().getPath());
        }
        Element execElm = doc.createElement(EXEC_TAG);
        execElm.setAttribute(EXEC_VALUE_ATTR,  exec.getExecutable());
        runElm.appendChild(execElm);
        addArguments(doc, runElm, exec.getArguments());
        commandElm.appendChild(runElm);
        return runElm;
    }
    private static Element putStructuredExec(Document doc, Element commandElm,
                                             Condition c, Map execsByConditions) throws DOMException {
        Element runElm = doc.createElement(RUN_TAG);
        if (execsByConditions.size() == 0) return runElm;
        String working = ((StructuredExec) execsByConditions.values().iterator().next()).getWorking().getPath();
        runElm.setAttribute(RUN_DIR_ATTR, working);
        for (Iterator it = execsByConditions.keySet().iterator(); it.hasNext(); ) {
            Condition ec = (Condition) it.next();
            StructuredExec exec = (StructuredExec) execsByConditions.get(ec);
            Element execElm = doc.createElement(EXEC_TAG);
            execElm.setAttribute(EXEC_VALUE_ATTR,  exec.getExecutable());
            runElm.appendChild(execElm);
            if (ec != null && !ec.equals(c)) VariableIO.setConditionAttributes(execElm, ec);
            addArguments(doc, runElm, exec.getArguments());
            commandElm.appendChild(runElm);
        }
        return runElm;
    }
    
    private static void addArguments(Document doc, Element runElm, StructuredExec.Argument[] args) throws DOMException {
        for (int i = 0; i < args.length; i++) {
            Element argElm = doc.createElement(ARG_TAG);
            if (args[i].isLine()) {
                argElm.setAttribute(ARG_LINE_ATTR, args[i].getArgument());
            } else {
                argElm.setAttribute(ARG_VALUE_ATTR, args[i].getArgument());
            }
            if (args[i] instanceof ConditionedStructuredExec.ConditionedArgument) {
                Condition c = ((ConditionedStructuredExec.ConditionedArgument) args[i]).getCondition();
                VariableIO.setConditionAttributes(argElm, c);
            }
            runElm.appendChild(argElm);
        }
    }
    
    /**
     * Perform a translation of command's property value. Currently just class names
     * are translated according to a translation table.
     */
    public static String translateCommandProperty(String propertyName, String propertyValue) {
        if (UserCommand.PROPERTY_EXEC.equals(propertyName)) {
            int classIndex = propertyValue.indexOf(".class");
            if (classIndex > 0) {
                int begin;
                for (begin = classIndex; begin >= 0; begin--) {
                    char c = propertyValue.charAt(begin);
                    if (!Character.isJavaIdentifierPart(c) && c != '.') break;
                }
                begin++;
                if (begin < classIndex) {
                    String classNameOrig = propertyValue.substring(begin, classIndex);
                    String classNameNew = translateExecClass(classNameOrig);
                    if (!classNameOrig.equals(classNameNew)) {
                        propertyValue = propertyValue.substring(0, begin) + classNameNew + propertyValue.substring(classIndex);
                    }
                }
            }
        }
        return propertyValue;
    }
    
    private static Reference translateMapRef = new SoftReference(null);
    private static final Object MAP_LOCK = new Object();
    
    /**
     * Translates an old class name (starting with "vcs.*") to the new class name
     * ("org.netbeans.modules.vcs.profiles.*") according to the translation table
     * at org/netbeans/modules/vcs/advanced/commands/cmdPackageTranslations
     */
    public static String translateExecClass(String className) {
        String[][] map;
        synchronized (MAP_LOCK) {
            map = (String[][]) translateMapRef.get();
            if (map == null) {
                try {
                    map = loadTranslateClassMap();
                } catch (IOException ioex) {
                    if (ErrorManager.getDefault() != null) {
                        ErrorManager.getDefault().notify (ErrorManager.INFORMATIONAL, ioex);
                    } else {
                        ioex.printStackTrace();
                    }
                }
                translateMapRef = new SoftReference(map);
            }
        }
        // search for "the" name
        final int mapsize = map.length;
        for (int i = 0; i < mapsize; i++) {
            if (className.startsWith(map[i][0])) {
                String newClassName;
                //if (arrayPrefix < 0) {
                    newClassName = map[i][1] + className.substring(map[i][0].length());
                //} else {
                //    newClassName = className.substring(0, arrayPrefix) + map[i][1] + name.substring(map[i][0].length());
                //}
                className = newClassName;
            }
        }/* catch (IOException e) {
            if (ErrorManager.getDefault() != null) {
                ErrorManager.getDefault().notify (ErrorManager.INFORMATIONAL, e);
            } else {
                e.printStackTrace();
            }
        } */
        // default
        return className;
        
    }
    
    private static String[][] loadTranslateClassMap() throws IOException {
        URL table = UserCommandIO.class.getResource("cmdPackageTranslations.txt"); //NOI18N
        ArrayList chunks = new ArrayList();
        loadTranslationFile(table, chunks);
        // post process
        Collections.sort(chunks, new StringArrayComparator());
        final int pairslen = chunks.size();
        String[][] mapping = new String[pairslen][2];
        for (int i = 0; i < pairslen; i++) {
            String[] chunk = (String[]) chunks.get(i);
            mapping[i][0] = chunk[0];
            mapping[i][1] = chunk[1];
        }
        return mapping;
    }
    
    /**
     * Load single translation file.
     * @param resource URL identifiing transaction table
     * @param chunks output parameter accepting loaded data
     */
    private static void loadTranslationFile(URL resource, List chunks) throws IOException {
        BufferedReader reader =
            new BufferedReader(new InputStreamReader( resource.openStream(), "UTF8"));  // use explicit encoding  //NOI18N

        for (;;) {
            String line = reader.readLine();
            String[] pair = parseLine(line);
            if (pair == null) { // EOF
                break;
            }
            chunks.add(pair);
        }

    }

    private static String[] parseLine(final String line) {
        if (line == null) {
            return null;
        }
        final int slen = line.length();
        int space = line.indexOf(' ');
        if (space <= 0 || (space == slen - 1)) {
            return null;
        }
        String[] chunk = new String[] {line.substring(0, space), null};

        space++;
        int c;
        while ((space < slen) && (line.charAt(space++) == ' '));
        if (space == slen) {
            return null;
        }
        String token = line.substring(--space);
        token = token.trim();
        chunk[1] = token;
        return chunk;
    }

    /** Compares to object by length of String returned by toString(). */
    private static final class StringArrayComparator implements Comparator {
        
        public boolean equals(Object o) {
            return super.equals(o);
        }

        public int compare(Object o1, Object o2) {
            String[] s1 = (String[]) o1;
            String[] s2 = (String[]) o2;
            return (s2[0].length() - s1[0].length());
        }
    }

}
