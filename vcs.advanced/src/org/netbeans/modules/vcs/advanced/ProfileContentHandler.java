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

package org.netbeans.modules.vcs.advanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.netbeans.modules.vcs.advanced.commands.ConditionedCommands;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder;
import org.netbeans.modules.vcs.advanced.commands.UserCommandIO;
import org.netbeans.modules.vcs.advanced.conditioned.ConditionedStructuredExec;
import org.netbeans.modules.vcs.advanced.variables.Condition;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;
import org.netbeans.modules.vcs.advanced.variables.VariableIO;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * The Profile content handler. Using SAX parser this object parse the VCS
 * Profile file or it's parts.
 *
 * @author  Martin Entlicher
 */
public class ProfileContentHandler extends Object implements ContentHandler, EntityResolver {
    
    public static final String CONFIG_ROOT_ELEM = "configuration";               // NOI18N
    public static final String LABEL_TAG = "label";                              // NOI18N
    public static final String OS_TAG = "os";                                    // NOI18N
    public static final String OS_COMPATIBLE_TAG = "compatible";                 // NOI18N
    public static final String OS_UNCOMPATIBLE_TAG = "uncompatible";             // NOI18N
    
    public static final String CONDITION_TAG = "condition";                      // NOI18N
    public static final String CONDITION_VAR_ATTR = "var";                       // NOI18N
    public static final String VAR_TAG = "var";                                  // NOI18N
    public static final String VAR_NAME_ATTR = "name";                           // NOI18N
    public static final String VAR_VALUE_ATTR = "value";                         // NOI18N
    public static final String VAR_VALUE_IGNORE_CASE_ATTR = "valueIgnoreCase";   // NOI18N
    public static final String VAR_VALUE_CONTAINS_ATTR = "valueContains";        // NOI18N
    public static final String VAR_VALUE_CONTAINS_IGNORE_CASE_ATTR = "valueContainsIgnoreCase"; // NOI18N
    public static final String AND_TAG = "and";                                  // NOI18N
    public static final String OR_TAG = "or";                                    // NOI18N
    public static final String NOT_TAG = "not";                                  // NOI18N

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
    public static final String VARIABLE_PROPERTY_VALUE_TAG = "value";            // NOI18N
    
    public static final String COMMANDS_TAG = "commands";                        // NOI18N
    public static final String GLOBAL_COMMANDS_TAG = "globalCommands";           // NOI18N
    public static final String COMMAND_TAG = "command";                          // NOI18N
    public static final String COMMAND_NAME_ATTR = "name";                       // NOI18N
    public static final String COMMAND_DISPLAY_NAME_ATTR = "displayName";        // NOI18N
    public static final String SEPARATOR_TAG = "separator";                      // NOI18N
    public static final String PROPERTY_TAG = "property";                        // NOI18N
    public static final String PROPERTY_NAME_ATTR = "name";                      // NOI18N
    //public static final String PROPERTY_VALUE_TAG = "value";                     // NOI18N
    public static final String RUN_TAG = "run";                                  // NOI18N
    public static final String RUN_DIR_ATTR = "dir";                             // NOI18N
    public static final String EXEC_TAG = "executable";                          // NOI18N
    public static final String EXEC_VALUE_ATTR = "value";                        // NOI18N
    public static final String ARG_TAG = "arg";                                  // NOI18N
    public static final String ARG_VALUE_ATTR = "value";                         // NOI18N
    public static final String ARG_LINE_ATTR = "line";                           // NOI18N
    public static final String IF_ATTR = "if";                                   // NOI18N
    public static final String UNLESS_ATTR = "unless";                           // NOI18N
        
    private static final String ROOT_CMD_NAME = "ROOT_CMD";                      // NOI18N

    public static final String BOOLEAN_VARIABLE_TRUE = "true";                   // NOI18N
    public static final String BOOLEAN_VARIABLE_FALSE = "false";                 // NOI18N
    
    /** SAXException with this message is thrown when the parsing is done. */
    public static final String END_OF_PARSING = "End of Profile Parsing";        // NOI18N
    
    /** The DTD for a configuration profile. */
    public static final String PUBLIC_ID_1_0 = "-//NetBeans//DTD VCS Configuration 1.0//EN";             // NOI18N
    public static final String PUBLIC_ID_1_1 = "-//NetBeans//DTD VCS Configuration 1.1//EN";             // NOI18N
    public static final String SYSTEM_ID_1_0 = "http://www.netbeans.org/dtds/vcs-configuration-1_0.dtd"; // NOI18N
    public static final String SYSTEM_ID_1_1 = "http://www.netbeans.org/dtds/vcs-configuration-1_1.dtd"; // NOI18N     
    
    /** Whether to validate XML files. Safer, but slows down. */
    private static final boolean VALIDATE_XML = false;
    
    private boolean getOS, getConditions, getVariables, getCommands, getGlobalCommands;
    private boolean haveOS, haveConditions, haveSomeConditions, haveVariables, haveCommands, haveGlobalCommands;
    private boolean readingLabel, readingOSCompat, readingOSUncompat, readingCondition, /*readingVariables,*/ readingCommands, readingGlobalCommands;
    private boolean skipping;
    
    private Condition lastCondition;        // The last condition object being parsed
    private Condition lastReadingCondition; // The currently reading sub-condition
    private boolean negativeCondition;      // Whether we're inside <not> tag.
    
    private VcsConfigVariable lastReadingVariable;  // The last variable being read
    private Condition lastReadingVariableCondition; // The condition of that variable
    private Map lastValuesByConditions;             // The values by conditions of the last variable or property.
    private String lastValue;                       // The last value being read. This is reused for property values as well.
    private Condition lastValueCondition;           // The condition of that last value
    
    private Stack readingCommandTrees;              // The stack of command trees that are currently being read.
    private UserCommand readingCommand;             // The command that is currently being read.
    private Condition lastCommandCondition;         // The condition of the current command
    private List conditionedProperties;             // The list of conditioned properties of the command that is surrently being read.
    private String lastReadingProperty;             // The name of the property that is being read
    private Condition lastPropertyCondition;        // The condition of the current property or structured exec
    
    private String lastStructuredExecWork;          // The working of the structured executor that was recently read. Can be null.
    private Map lastStructuredExecExecsByConditions;// The working of the structured executor that was recently read
    
    private String label;
    private String compatibleOSs;
    private String uncompatibleOSs;
    private List conditions;
    private List variables;
    private Map conditionsByVariables;
    private Map varsByConditions;
    private CommandExecutionContext execContext;
    private CommandsTree commandsTree;
    private UserCommand rootCmd;
    private ConditionedCommandsBuilder ccb;
    private ConditionedCommands ccContext;
    private ConditionedCommands ccGlobal;
    //private CommandsTree globalCommandsTree;
    
    /** Creates a new instance of ProfileContentHandler */
    public ProfileContentHandler(boolean os, boolean conditions, boolean variables,
                                 boolean commands, boolean globalCommands,
                                 CommandExecutionContext execContext) {
        this.getOS = os;
        this.getConditions = conditions;
        this.getVariables = variables;
        this.getCommands = commands;
        this.getGlobalCommands = globalCommands;
        this.execContext = execContext;
        if (getConditions) {
            this.conditions = new ArrayList();
        }
        if (getVariables) {
            this.variables = new ArrayList();
            conditionsByVariables = new TreeMap();
            varsByConditions = new HashMap();
        }
        if (getCommands || getGlobalCommands) {
            rootCmd = new UserCommand();
            rootCmd.setName(ROOT_CMD_NAME);
            //rootCmd.setDisplayName(label);
            commandsTree = new CommandsTree(new UserCommandSupport(rootCmd, execContext));
            ccb = new ConditionedCommandsBuilder(commandsTree);
        }
    }
    
    public void startDocument() throws SAXException {
    }
    
    public void endDocument() throws SAXException {
    }
    
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (skipping) return ;
        String elementName = ("".equals(localName)) ? qName : localName;
        //System.out.println("      startElement("+elementName+"), atts = "+atts);
        if (CONDITION_TAG.equals(elementName)) {
            if (!getConditions) {
                skipping = true;
                return ;
            }
            readingCondition = true;
            String varName = atts.getValue(CONDITION_VAR_ATTR);
            lastCondition = new Condition(varName);
            lastReadingCondition = lastCondition;
        } else {
            if (haveSomeConditions && !readingCondition) haveConditions = true;
        }
        if (LABEL_TAG.equals(elementName)) {
            label = "";
            readingLabel = true;
        } else if (OS_TAG.equals(elementName)) {
            if (!getOS) skipping = true;
        } else if (OS_COMPATIBLE_TAG.equals(elementName)) {
            compatibleOSs = "";
            readingOSCompat = true;
        } else if (OS_UNCOMPATIBLE_TAG.equals(elementName)) {
            uncompatibleOSs = "";
            readingOSUncompat = true;
        //} else if (label != null && !OS_TAG.equals(elementName)) {
        //    throw new org.xml.sax.SAXException("End of label.");
        } else if (VAR_TAG.equals(elementName)) {
            if (lastReadingCondition != null) {
                addConditionVar(lastReadingCondition, atts, !negativeCondition);
            }
        } else if (NOT_TAG.equals(elementName)) {
            if (!readingCondition) {
                throw new SAXException("<not> element outside of condition.");
            }
            negativeCondition = true;
        } else if (AND_TAG.equals(elementName)) {
            if (!readingCondition) {
                throw new SAXException("<and> element outside of condition.");
            }
            Condition inner = new Condition("Anonymous inner");
            lastReadingCondition.addCondition(inner, !negativeCondition);
            inner.setLogicalOperation(Condition.LOGICAL_AND);
            lastReadingCondition = inner;
        } else if (OR_TAG.equals(elementName)) {
            if (!readingCondition) {
                throw new SAXException("<or> element outside of condition.");
            }
            Condition inner = new Condition("Anonymous inner");
            lastReadingCondition.addCondition(inner, !negativeCondition);
            inner.setLogicalOperation(Condition.LOGICAL_OR);
            lastReadingCondition = inner;
        } else if (VARIABLES_TAG.equals(elementName)) {
            if (!getVariables) {
                skipping = true;
                return ;
            }
            //readingVariables = true;
        } else if (VARIABLE_TAG.equals(elementName)) {
            lastReadingVariable = readVariable(atts);
        } else if (VARIABLE_PROPERTY_VALUE_TAG.equals(elementName)) {
            if (lastReadingVariable != null) {
                lastValue = "";
                lastValueCondition = createCondition(lastReadingVariable.getName(), atts);
            } else if (lastReadingProperty != null) {
                lastValue = "";
                lastValueCondition = createCondition(lastReadingProperty, atts);
            } else {
                throw new SAXException("<value> tag without enclosing variable or command property.");
            }
        } else if (COMMANDS_TAG.equals(elementName)) {
            if (!getCommands) {
                skipping = true;
                return ;
            }
            readingCommands = true;
            readingCommandTrees = new Stack();
            readingCommandTrees.push(commandsTree);
        } else if (COMMAND_TAG.equals(elementName)) {
            if (readingCommand != null) {
                addReadCommand(true); // A sub-command of read command will follow
            }
            readCommand(atts);
        } else if (SEPARATOR_TAG.equals(elementName)) {
            ((CommandsTree) readingCommandTrees.peek()).add(CommandsTree.EMPTY);
        } else if (PROPERTY_TAG.equals(elementName)) {
            if (readingCommand == null) {
                throw new SAXException("Property element without an enclosing command.");
            }
            String name = atts.getValue(PROPERTY_NAME_ATTR);
            if (name == null) {
                throw new SAXException("\"name\" attribute is required for property element.");
            }
            lastReadingProperty = name;
            lastPropertyCondition = createCondition(readingCommand.getName() + "/" + name, atts);
            lastValuesByConditions = new IdentityHashMap();
        } else if (RUN_TAG.equals(elementName)) {
            if (readingCommand == null) {
                throw new SAXException("Run element without an enclosing command.");
            }
            lastPropertyCondition = createCondition(readingCommand.getName() + "/" + VcsCommand.PROPERTY_EXEC_STRUCTURED, atts);
            lastStructuredExecWork = atts.getValue(RUN_DIR_ATTR); // can be null
            lastStructuredExecExecsByConditions = new IdentityHashMap();
        } else if (EXEC_TAG.equals(elementName)) {
            Condition c = createCondition(readingCommand.getName() + "/" + VcsCommand.PROPERTY_EXEC_STRUCTURED + "/executor", atts);
            String executable = atts.getValue(EXEC_VALUE_ATTR);
            if (executable == null) {
                throw new SAXException("Executable element without value attribute.");
            }
            StructuredExec exec = new StructuredExec((lastStructuredExecWork != null) ? new java.io.File(lastStructuredExecWork) : null, executable, new StructuredExec.Argument[0]);
            lastStructuredExecExecsByConditions.put(c, exec);
        } else if (ARG_TAG.equals(elementName)) {
            addStructuredArgument(atts);
        } else if (GLOBAL_COMMANDS_TAG.equals(elementName)) {
            if (!getGlobalCommands) {
                skipping = true;
                return ;
            }
            readingCommands = true;
            readingCommandTrees = new Stack();
            readingCommandTrees.push(commandsTree);
        }
    }
    
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        String elementName = ("".equals(localName)) ? qName : localName;
        //if (!skipping) System.out.println("      endElement("+elementName+")");
        if (readingLabel && LABEL_TAG.equals(elementName)) {
            readingLabel = false;
            label = VcsUtilities.getBundleString(label);
            if (rootCmd != null) {
                rootCmd.setDisplayName(label);
            }
        } else if (readingOSCompat && OS_COMPATIBLE_TAG.equals(elementName)) {
            readingOSCompat = false;
        } else if (readingOSUncompat && OS_UNCOMPATIBLE_TAG.equals(elementName)) {
            readingOSUncompat = false;
        } else if (OS_TAG.equals(elementName)) {
            if (skipping) {
                skipping = false;
            } else {
                haveOS = true;
            }
        } else if (CONDITION_TAG.equals(elementName)) {
            if (skipping) {
                skipping = false;
            } else {
                conditions.add(lastCondition);
                lastCondition = lastReadingCondition = null;
                readingCondition = false;
                haveSomeConditions = true;
            }
        } else if (NOT_TAG.equals(elementName)) {
            negativeCondition = false;
        } else if (VARIABLES_TAG.equals(elementName)) {
            if (skipping) {
                skipping = false;
            } else {
                //readingVariables = false;
                haveVariables = true;
            }
        } else if (VARIABLE_TAG.equals(elementName)) {
            if (skipping) return ;
            addLastReadConditionedVariable();
            lastReadingVariable = null;
        } else if (VARIABLE_PROPERTY_VALUE_TAG.equals(elementName)) {
            if (skipping) return ;
            if (lastReadingVariable != null) {
                lastValue = VcsUtilities.getBundleString(lastValue);
                lastValue = VariableIO.translateVariableValue(lastReadingVariable.getName(), lastValue);
                lastValuesByConditions.put(lastValueCondition, lastValue);
            } else if (lastReadingProperty != null) {
                lastValue = UserCommandIO.translateCommandProperty(lastReadingProperty, lastValue);
                lastValuesByConditions.put(lastValueCondition, UserCommandIO.getPropertyValue(lastReadingProperty, lastValue));
            }
            lastValueCondition = null;
            lastValue = null;
        } else if (PROPERTY_TAG.equals(elementName)) {
            if (skipping) return ;
            addLastProperty();
        } else if (COMMAND_TAG.equals(elementName)) {
            if (skipping) return ;
            if (readingCommand != null) {
                addReadCommand(false); // No sub-commands will follow
            } else { // We've closed the read command before, now we're on it's parent.
                readingCommandTrees.pop();
            }
        } else if (COMMANDS_TAG.equals(elementName)) {
            if (skipping) {
                skipping = false;
            } else {
                ccContext = ccb.getConditionedCommands();
                haveCommands = true;
                if (getGlobalCommands && !haveGlobalCommands) {
                    rootCmd = new UserCommand();
                    rootCmd.setName(ROOT_CMD_NAME);
                    rootCmd.setDisplayName(label);
                    commandsTree = new CommandsTree(new UserCommandSupport(rootCmd, execContext));
                    ccb = new ConditionedCommandsBuilder(commandsTree);
                }
            }
        } else if (RUN_TAG.equals(elementName)) {
            if (skipping) return ;
            addStructuredExec();
        } else if (GLOBAL_COMMANDS_TAG.equals(elementName)) {
            if (skipping) {
                skipping = false;
            } else {
                ccGlobal = ccb.getConditionedCommands();
                haveGlobalCommands = true;
                if (getCommands && !haveCommands) {
                    rootCmd = new UserCommand();
                    rootCmd.setName(ROOT_CMD_NAME);
                    rootCmd.setDisplayName(label);
                    commandsTree = new CommandsTree(new UserCommandSupport(rootCmd, execContext));
                    ccb = new ConditionedCommandsBuilder(commandsTree);
                }
            }
        } else if (CONFIG_ROOT_ELEM.equals(elementName)) {
            if (getGlobalCommands && !haveGlobalCommands) { // If there were no global commands defined.
                ccGlobal = ccb.getConditionedCommands();
                haveGlobalCommands = true;
            }
        }
        checkParseFinished();
    }
    
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (skipping) return ;
        if (readingLabel) {
            label += new String(ch, start, length);
        } else if (readingOSCompat) {
            compatibleOSs += new String(ch, start, length);
        } else if (readingOSUncompat) {
            uncompatibleOSs += new String(ch, start, length);
        } else if (lastValue != null) {
            lastValue += new String(ch, start, length);
        }
    }
    
    public void endPrefixMapping(String prefix) throws SAXException {
    }
    
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }
    
    public void processingInstruction(String target, String data) throws SAXException {
    }
    
    public void setDocumentLocator(Locator locator) {
    }
    
    public void skippedEntity(String name) throws SAXException {
    }
    
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }
    
    private void checkParseFinished() throws SAXException {
        if (getOS == haveOS && getConditions == haveConditions && getVariables == haveVariables &&
            getCommands == haveCommands && getGlobalCommands == haveGlobalCommands) {
            
                
            throw new SAXException(END_OF_PARSING);
        }
    }
    
    public InputSource resolveEntity(String pubid, String sysid) throws SAXException, java.io.IOException {
        if (pubid.equals(PUBLIC_ID_1_0)) {
            if (VALIDATE_XML) {
                // We certainly know where to get this from.
                return new InputSource("nbres:/vcs/config/configuration-1_0.dtd"); // NOI18N
            } else {
                // Not validating, don't load any DTD! Significantly faster.
                return new InputSource(new java.io.ByteArrayInputStream(new byte[0]));
            }
        } if (pubid.equals(PUBLIC_ID_1_1)) {
            if (VALIDATE_XML) {
                // We certainly know where to get this from.
                return new InputSource("nbres:/vcs/config/configuration-1_1.dtd"); // NOI18N
            } else {
                // Not validating, don't load any DTD! Significantly faster.
                return new InputSource(new java.io.ByteArrayInputStream(new byte[0]));
            }
        } else {
            // Otherwise try the standard places.
            return org.openide.xml.EntityCatalog.getDefault().resolveEntity(pubid, sysid);
        }
     }
        
    
    /**
     * Get the Profile label.
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Get the list of compatible operating systems.
     * @return The compatible operating systems, or <code>null</code> if the operating systems were not parsed.
     */
    public String getCompatibleOSs() {
        return compatibleOSs;
    }
    
    /**
     * Get the list of incompatible operating systems.
     * @return The incompatible operating systems, or <code>null</code> if the operating systems were not parsed.
     */
    public String getUncompatibleOSs() {
        return uncompatibleOSs;
    }
    
    /**
     * Get the profile conditions.
     * @return The array of conditions, or <code>null</code> if the conditions were not parsed.
     */
    public Condition[] getConditions() {
        if (conditions == null) return null;
        return (Condition[]) conditions.toArray(new Condition[0]);
    }
    
    /**
     * Get the conditioned variables that were read from the profile.
     * @return The conditioned variables, or <code>null</code> if the variables were not parsed.
     */
    public ConditionedVariables getVariables() {
        if (getVariables) {
            return new ConditionedVariables(variables, conditionsByVariables, varsByConditions);
        } else {
            return null;
        }
    }
    
    /**
     * Get the conditioned contextual commands that were read from the profile.
     * @return The conditioned commands, or <code>null</code> if the commands were not parsed.
     */
    public ConditionedCommands getCommands() {
        return ccContext;
    }
    
    /**
     * Get the conditioned global commands that were read from the profile.
     * @return The global commands, or <code>null</code> if the commands were not parsed.
     */
    public ConditionedCommands getGlobalCommands() {
        return ccGlobal;
    }
    
    
    private void addConditionVar(Condition c, Attributes atts, boolean positive) {
        String name = atts.getValue(VAR_NAME_ATTR);
        int compareValue = 0;
        String value = atts.getValue(VAR_VALUE_ATTR);
        if (value != null) {
            compareValue = Condition.COMPARE_VALUE_EQUALS;
        } else {
            value = atts.getValue(VAR_VALUE_IGNORE_CASE_ATTR);
            if (value != null) {
                compareValue = Condition.COMPARE_VALUE_EQUALS_IGNORE_CASE;
            } else {
                value = atts.getValue(VAR_VALUE_CONTAINS_ATTR);
                if (value != null) {
                    compareValue = Condition.COMPARE_VALUE_CONTAINS;
                } else {
                    value = atts.getValue(VAR_VALUE_CONTAINS_IGNORE_CASE_ATTR);
                    if (value != null) {
                        compareValue = Condition.COMPARE_VALUE_CONTAINS_IGNORE_CASE;
                    }
                }
            }
        }
        if (name != null && value != null) {
            c.addVar(name, value, compareValue, positive);
        }
    }
    
    private Condition createCondition(String name, Attributes atts) {
        String if_attr = atts.getValue(IF_ATTR);
        if (if_attr == null) if_attr = ""; // NOI18N
        String unless_attr = atts.getValue(UNLESS_ATTR);
        if (unless_attr == null) unless_attr = ""; // NOI18N
        return VariableIO.createCondition(name, if_attr, unless_attr);
    }
    
    private VcsConfigVariable readVariable(Attributes atts) {
        String name = atts.getValue(VARIABLE_NAME_ATTR);
        if (name == null) return null; // The name is required
        //boolean isBasic = false;
        //String value = "";
        VcsConfigVariable var;
        String basicAttr = atts.getValue(VARIABLE_BASIC_ATTR);
        boolean basic = BOOLEAN_VARIABLE_TRUE.equalsIgnoreCase(basicAttr);
        if (basic) {
            var = getBasicVariable(name, "", atts);
        } else {
            var = new VcsConfigVariable(name, "", "", false, false, false, "");
        }
        lastValuesByConditions = new IdentityHashMap();
        lastReadingVariableCondition = createCondition(name, atts);
        return var;
    }
    
    private VcsConfigVariable getBasicVariable(String name, String value, Attributes atts) {
        String label = atts.getValue(VARIABLE_LABEL_ATTR);
        if (label != null) {
            label = VcsUtilities.getBundleString(label);
        } else {
            label = "";
        }
        String labelMnemonic = atts.getValue(VARIABLE_LABEL_MNEMONIC_ATTR);
        if (labelMnemonic != null) {
            labelMnemonic = VcsUtilities.getBundleString(labelMnemonic);
        }
        String a11yName = atts.getValue(VARIABLE_A11Y_NAME_ATTR);
        if (a11yName != null) {
            a11yName = VcsUtilities.getBundleString(a11yName);
        }
        String a11yDescription = atts.getValue(VARIABLE_A11Y_DESCRIPTION_ATTR);
        if (a11yDescription != null) {
            a11yDescription = VcsUtilities.getBundleString(a11yDescription);
        }
        boolean localFile = BOOLEAN_VARIABLE_TRUE.equalsIgnoreCase(atts.getValue(VARIABLE_LOCAL_FILE_ATTR));
        boolean localDir = BOOLEAN_VARIABLE_TRUE.equalsIgnoreCase(atts.getValue(VARIABLE_LOCAL_DIR_ATTR));
        boolean executable = BOOLEAN_VARIABLE_TRUE.equalsIgnoreCase(atts.getValue(VARIABLE_EXECUTABLE_ATTR));
        String orderStr = atts.getValue(VARIABLE_ORDER_ATTR);
        int order = -1;
        if (orderStr != null) {
            try {
                order = Integer.parseInt(orderStr);
            } catch (NumberFormatException exc) {
                order = -1;
            }
        }
        String customSelector = atts.getValue(VARIABLE_SELECTOR_TAG);
        if (customSelector == null) customSelector = ""; //NOI18N
        VcsConfigVariable var = new VcsConfigVariable(name, label, value, true, localFile, localDir, customSelector, order);
        var.setExecutable(executable);
        if (labelMnemonic != null && labelMnemonic.length() > 0) {
            var.setLabelMnemonic(new Character(labelMnemonic.charAt(0)));
        }
        if (a11yName != null) var.setA11yName(a11yName);
        if (a11yDescription != null) var.setA11yDescription(a11yDescription);
        return var;
    }
    
    private void addLastReadConditionedVariable() {
        Condition[] conditions = new Condition[lastValuesByConditions.size()];
        int ci = 0;
        String name = lastReadingVariable.getName();
        for (Iterator it = lastValuesByConditions.keySet().iterator(); it.hasNext(); ) {
            Condition vc = (Condition) it.next();
            if (vc == null) continue;
            String cvalue = (String) lastValuesByConditions.get(vc);
            VcsConfigVariable var = (VcsConfigVariable) lastReadingVariable.clone();//readVariable(name, cvalue, varNode, varAttrs);
            var.setValue(cvalue);
            if (lastReadingVariableCondition != null) vc.addCondition(lastReadingVariableCondition, true);
            conditions[ci] = vc;
            varsByConditions.put(vc, var);
            ci++;
        }
        String value = (String) lastValuesByConditions.get(null);
        if (value != null) {
            VcsConfigVariable var = (VcsConfigVariable) lastReadingVariable.clone();//readVariable(name, value, varNode, varAttrs);
            var.setValue(value);
            //System.out.println("  var = "+var);
            Condition cond = lastReadingVariableCondition;
            if (lastValuesByConditions.size() > 1) {
                cond = VariableIO.createComplementaryCondition(name, cond, lastValuesByConditions.keySet());
            }
            if (cond != null) {
                varsByConditions.put(cond, var);
                conditions[conditions.length - 1] = cond;
            } else {
                variables.add(var);
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
        lastValuesByConditions = null;
    }
    
    private void readCommand(Attributes atts) {
        String name = atts.getValue(COMMAND_NAME_ATTR);
        if (name == null) return ; // Name is required
        UserCommand cmd = new UserCommand();
        cmd.setName(name);
        String displayName = atts.getValue(COMMAND_DISPLAY_NAME_ATTR);
        if (displayName != null) {
            displayName = VcsUtilities.getBundleString(displayName);
        }
        cmd.setDisplayName(displayName);
        lastCommandCondition = createCondition(name, atts);
        readingCommand = cmd;
        conditionedProperties = new ArrayList();
    }
    
    private void addReadCommand(boolean subcommandsExist) {
        UserCommandSupport cmdSupp = new UserCommandSupport(readingCommand, execContext);
        CommandsTree cmdTree = (CommandsTree) readingCommandTrees.peek();
        if (subcommandsExist) {
            CommandsTree subTree = new CommandsTree(cmdSupp);
            cmdTree.add(subTree);
            readingCommandTrees.push(subTree);
        } else {
            cmdTree.add(new CommandsTree(cmdSupp));
        }
        if (lastCommandCondition != null) ccb.addConditionedCommand(cmdSupp, lastCommandCondition);
        if (conditionedProperties.size() > 0) {
            for (Iterator it = conditionedProperties.iterator(); it.hasNext(); ) {
                ConditionedCommandsBuilder.ConditionedProperty property =
                    (ConditionedCommandsBuilder.ConditionedProperty) it.next();
                ccb.addPropertyToCommand(readingCommand.getName(), lastCommandCondition, property);
            }
        }
        readingCommand = null;
        lastCommandCondition = null;
        conditionedProperties = null;
    }
    
    private void addLastProperty() {
        Object value = lastValuesByConditions.get(null);
        if (value != null) {
            Condition cond = lastPropertyCondition;
            if (lastValuesByConditions.size() > 1) {
                cond = VariableIO.createComplementaryCondition(readingCommand.getName() + "/" + lastReadingProperty, lastPropertyCondition, lastValuesByConditions.keySet());
            }
            if (cond != null) {
                lastValuesByConditions.put(cond, value);
                lastValuesByConditions.remove(null);
            } else {
                lastValuesByConditions = null;
            }
        }
        if (lastValuesByConditions == null) {
            readingCommand.setProperty(lastReadingProperty, value);
        } else {
            if (lastPropertyCondition != null) {
                for (Iterator it = lastValuesByConditions.keySet().iterator(); it.hasNext(); ) {
                    Condition vc = (Condition) it.next();
                    if (!vc.equals(lastPropertyCondition)) {
                        vc.addCondition(lastPropertyCondition, true);
                    }
                }
            }
            conditionedProperties.add(
                new ConditionedCommandsBuilder.ConditionedProperty(lastReadingProperty,
                                                                   lastPropertyCondition,
                                                                   lastValuesByConditions));
        }
        lastReadingProperty = null;
        lastPropertyCondition = null;
        lastValuesByConditions = null;
    }
    
    private void addStructuredArgument(Attributes atts) throws SAXException {
        if (lastStructuredExecExecsByConditions == null) {
            throw new SAXException("Argument element without an enclosing <run> element.");
        }
        if (lastStructuredExecExecsByConditions.size() == 0) {
            throw new SAXException("Argument element without a precedent <executable> element.");
        }
        String argStr = atts.getValue(ARG_VALUE_ATTR);
        boolean line = false;
        if (argStr == null) {
            argStr = atts.getValue(ARG_LINE_ATTR);
            line = true;
        }
        if (argStr == null) {
            throw new SAXException("Argument element without value or line attributes.");
        }
        Condition ac = createCondition(readingCommand.getName() + "/" + VcsCommand.PROPERTY_EXEC_STRUCTURED + "/arg",
                                       atts);
        StructuredExec.Argument arg;
        if (ac == null) {
            arg = new StructuredExec.Argument(argStr, line);
        } else {
            arg = new ConditionedStructuredExec.ConditionedArgument(ac, argStr, line);
        }
        //for (Iterator it = execsByConditions.values().iterator(); it.hasNext(); ) {
        for (Iterator it = lastStructuredExecExecsByConditions.keySet().iterator(); it.hasNext(); ) {
            Condition cc = (Condition) it.next();

            StructuredExec exec = (StructuredExec) lastStructuredExecExecsByConditions.get(cc);
            if (arg instanceof ConditionedStructuredExec.ConditionedArgument &&
                !(exec instanceof ConditionedStructuredExec)) {

                exec = new ConditionedStructuredExec(exec.getWorking(), exec.getExecutable(), exec.getArguments());
                lastStructuredExecExecsByConditions.put(cc, exec);
            }
            exec.addArgument(arg);
        }
    }
    
    private void addStructuredExec() {
        if (lastStructuredExecExecsByConditions.size() == 1 && lastStructuredExecExecsByConditions.get(null) != null) {
            readingCommand.setProperty(VcsCommand.PROPERTY_EXEC_STRUCTURED, lastStructuredExecExecsByConditions.get(null));
        } else {
            if (lastPropertyCondition != null) {
                for (Iterator it = lastStructuredExecExecsByConditions.keySet().iterator(); it.hasNext(); ) {
                    Condition vc = (Condition) it.next();
                    if (!vc.equals(lastPropertyCondition)) {
                        vc.addCondition(lastPropertyCondition, true);
                    }
                }
            }
            conditionedProperties.add(
                new ConditionedCommandsBuilder.ConditionedProperty(VcsCommand.PROPERTY_EXEC_STRUCTURED,
                                                                   lastPropertyCondition,
                                                                   lastStructuredExecExecsByConditions));
        }
        lastStructuredExecExecsByConditions = null;
        lastPropertyCondition = null;
        lastStructuredExecWork = null;
    }
    
}
