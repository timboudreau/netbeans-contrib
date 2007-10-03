/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcs.advanced;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.netbeans.modules.vcs.advanced.commands.ConditionedCommands;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedCommand;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedPropertiesCommand;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedProperty;
import org.netbeans.modules.vcs.advanced.commands.UserCommandIO;
import org.netbeans.modules.vcs.advanced.conditioned.ConditionedStructuredExec;
import org.netbeans.modules.vcs.advanced.variables.Condition;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;
import org.netbeans.modules.vcs.advanced.variables.VariableIO;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.openide.xml.XMLUtil;

/**
 * Writes the VCS profile in an XML format.
 *
 * @author  Martin Entlicher
 */
public final class ProfileWriter {
    
    private static final byte[] HEADER = ("<?xml version=\"1.0\"?>\n" +
        "<!DOCTYPE configuration PUBLIC '-//NetBeans//DTD VCS Configuration 1.1//EN' " +
        "'http://www.netbeans.org/dtds/vcs-configuration-1_1.dtd'>\n").getBytes();
    private static final byte[] NL = System.getProperty("line.separator").getBytes();
    private static final byte[] OPEN_ELM = "<".getBytes();
    private static final byte[] OPEN_ELM_END = "</".getBytes();
    private static final byte[] CLOSE_ELM = ">".getBytes();
    private static final byte[] CLOSE_ELM_NL = (">"+System.getProperty("line.separator")).getBytes();
    private static final byte[] CLOSE_END_ELM_NL = ("/>"+System.getProperty("line.separator")).getBytes();
    private static final byte[] QUOTE = "\"".getBytes();
    private static final byte[] EQUALS = "=".getBytes();
    private static final byte[] SPACE = " ".getBytes();
    
    private static final int INSET = 4;
    
    /** An empty private constructor, this class is never to be instantiated.
      * It contains just static methods. */
    private ProfileWriter() { }
    
    /**
     * Write the profile into the provided output stream in an XML format.
     * @param out The output stream into which is the XML content written
     * @param profile The profile to be written
     * @throws IOException When an I/O problem occurs
     */
    public static void write(OutputStream out, Profile profile) throws IOException {
        out.write(HEADER);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.CONFIG_ROOT_ELEM.getBytes());
        String type = profile.getType();
        if (type != null) {
            writeAttribute(out, ProfileContentHandler.CONFIG_TYPE_ATTR, type);
        }
        String splitloc = profile.getSplitWhenLocalized();
        if (splitloc != null) {
            writeAttribute(out, VariableIO.CONFIG_SPLITLOC_ATTR, splitloc);
        }
        out.write(CLOSE_ELM_NL);
        
        int inset = INSET;
        
        writeResourceBundles(out, inset, profile.getResourceBundles());
        out.write(NL);
        writeLabel(out, inset, profile.getDisplayName());
        out.write(NL);
        writeOS(out, inset, profile.getCompatibleOSs(), profile.getUncompatibleOSs());
        out.write(NL);
        writeConditions(out, inset, profile.getConditions());
        out.write(NL);
        writeVariables(out, inset, profile.getVariables());
        out.write(NL);
        writeCommands(out, inset, profile.getCommands());
        out.write(NL);
        writeGlobalCommands(out, inset, profile.getGlobalCommands());
        
        out.write(OPEN_ELM_END);
        out.write(ProfileContentHandler.CONFIG_ROOT_ELEM.getBytes());
        out.write(CLOSE_ELM_NL);
    }
    
    /**
     * Writes the inset - the appropriate number of spaces.
     * @param out The output stream
     * @param inset The length of the inset
     */
    private static void writeInset(OutputStream out, int inset) throws IOException {
        for (int i = 0; i < inset; i++) {
            out.write(SPACE);
        }
    }
    
    /**
     * Writes a simple element of the form <code><b>&lt;elementName&gt;</b>content<b>&lt;/elementName&gt;</b></code>
     */
    private static void writeSimpleElement(OutputStream out, int inset,
                                           String elementName, String content) throws IOException {
        writeSimpleElement(out, inset, elementName.getBytes(), XMLUtil.toElementContent(content).getBytes());
    }
    
    /**
     * Writes a simple element of the form <code><b>&lt;elementName&gt;</b>content<b>&lt;/elementName&gt;</b></code>
     */
    private static void writeSimpleElement(OutputStream out, int inset,
                                           byte[] elementName, byte[] content) throws IOException {
        writeInset(out, inset);
        out.write(OPEN_ELM);
        out.write(elementName);
        out.write(CLOSE_ELM);
        out.write(content);
        out.write(OPEN_ELM_END);
        out.write(elementName);
        out.write(CLOSE_ELM_NL);
    }
    
    /**
     * Write an attribute to an element in the form <code>attrName="attrValue"</code>
     * A space is prepended automatically.
     */
    private static void writeAttribute(OutputStream out, String attrName, String attrValue) throws IOException {
        writeAttribute(out, attrName.getBytes(), XMLUtil.toAttributeValue(attrValue).getBytes());
    }
    
    /**
     * Write an attribute to an element in the form <code>attrName="attrValue"</code>
     * A space is prepended automatically.
     */
    private static void writeAttribute(OutputStream out, byte[] attrName, byte[] attrValue) throws IOException {
        out.write(SPACE);
        out.write(attrName);
        out.write(EQUALS);
        out.write(QUOTE);
        out.write(attrValue);
        out.write(QUOTE);
    }
    
    /**
     * Writes the list of resource bundles: <code><b>&lt;resourceBundle&gt;</b>TheResourceBundle<b>&lt;/resourceBundle&gt;</b></code>
     */
    private static void writeResourceBundles(OutputStream out, int inset,
                                             String[] resourceBundles) throws IOException {
        if (resourceBundles != null) {
            for (int i = 0; i < resourceBundles.length; i++) {
                writeSimpleElement(out, inset, ProfileContentHandler.RESOURCE_BUNDLE_TAG,
                                   resourceBundles[i]);
            }
        }
    }
    
    /**
     * Writes the label: <code><b>&lt;label&gt;</b>TheLabel<b>&lt;/label&gt;</b></code>
     */
    private static void writeLabel(OutputStream out, int inset,
                                   String label) throws IOException {
        writeSimpleElement(out, inset, ProfileContentHandler.LABEL_TAG,
                           label);
    }
    
    /**
     * Writes the list of compatible and uncompatible operating systems in a form of:
     * <p>
     * <code><b>&lt;os&gt;</b> <br>
     * &nbsp;&nbsp;<b>&lt;compatible&gt;</b>List&nbsp;of&nbsp;compatible&nbsp;operating&nbsp;systems&nbsp;separated&nbsp;by&nbsp;commas<b>&lt;/compatible&gt;</b> <br>
     * &nbsp;&nbsp;<b>&lt;uncompatible&gt;</b>List&nbsp;of&nbsp;uncompatible&nbsp;operating&nbsp;systems&nbsp;separated&nbsp;by&nbsp;commas<b>&lt;/uncompatible&gt;</b>  <br>
     * <b>&lt;/os&gt;</b></code>
     */
    private static void writeOS(OutputStream out, int inset, Set compatibleOSs,
                                Set uncompatibleOSs) throws IOException {
        if (compatibleOSs.size() > 0 || uncompatibleOSs.size() > 0) {
            writeInset(out, inset);
            out.write(OPEN_ELM);
            out.write(ProfileContentHandler.OS_TAG.getBytes());
            out.write(CLOSE_ELM_NL);
            
            if (compatibleOSs.size() > 0) {
                String oss = VcsUtilities.arrayToQuotedStrings(
                    (String[]) new TreeSet(compatibleOSs).toArray(new String[0]));
                writeSimpleElement(out, inset + INSET,
                                   ProfileContentHandler.OS_COMPATIBLE_TAG,
                                   oss);
            }
            
            if (uncompatibleOSs.size() > 0) {
                String oss = VcsUtilities.arrayToQuotedStrings(
                    (String[]) new TreeSet(uncompatibleOSs).toArray(new String[0]));
                writeSimpleElement(out, inset + INSET,
                                   ProfileContentHandler.OS_UNCOMPATIBLE_TAG,
                                   oss);
            }
            
            writeInset(out, inset);
            out.write(OPEN_ELM_END);
            out.write(ProfileContentHandler.OS_TAG.getBytes());
            out.write(CLOSE_ELM_NL);
        }
    }
    
    /**
     * Write the conditions sorted alphabetically.
     */
    private static void writeConditions(OutputStream out, int inset,
                                        Condition[] conditions) throws IOException {
        if (conditions == null || conditions.length == 0) return ;
        /* We just sort the original array - should not cause any problems.
         * If it does, make a copy:
        Condition[] conditionsCopy = new Condition[conditions.length];
        System.arraycopy(conditions, 0, conditionsCopy, 0, conditions.length);
        conditions = conditionsCopy;
         */
        Arrays.sort(conditions, new ConditionsComparator());
        for (int i = 0; i < conditions.length; i++) {
            writeInset(out, inset);
            out.write(OPEN_ELM);
            out.write(ProfileContentHandler.CONDITION_TAG.getBytes());
            writeAttribute(out, ProfileContentHandler.CONDITION_VAR_ATTR,  conditions[i].getName());
            out.write(CLOSE_ELM_NL);
            
            writeConditionElements(out, inset + INSET, conditions[i], false);
            
            writeInset(out, inset);
            out.write(OPEN_ELM_END);
            out.write(ProfileContentHandler.CONDITION_TAG.getBytes());
            out.write(CLOSE_ELM_NL);
            out.write(NL);
        }
    }
    
    /** Write the inner elements of a condition. */
    private static void writeConditionElements(OutputStream out, int inset,
                                               Condition c, boolean writeAndOperation) throws IOException {
        int lop = c.getLogicalOperation();
        if (lop == Condition.LOGICAL_AND && !writeAndOperation) {
            writeConditionContent(out, inset, c);
        } else {
            byte[] op;
            if (lop == Condition.LOGICAL_OR) {
                op = ProfileContentHandler.OR_TAG.getBytes();
            } else {
                op = ProfileContentHandler.AND_TAG.getBytes();
            }
            writeInset(out, inset);
            out.write(OPEN_ELM);
            out.write(op);
            out.write(CLOSE_ELM_NL);

            writeConditionContent(out, inset + INSET, c);

            writeInset(out, inset);
            out.write(OPEN_ELM_END);
            out.write(op);
            out.write(CLOSE_ELM_NL);
        }
    }
    
    /** Write the inner elements of a condition without enclosing <code>&lt;and&gt;</code> or <code>&lt;or&gt;</code> elements. */
    private static void writeConditionContent(OutputStream out, int inset,
                                              Condition c) throws IOException {
        // Write the variables
        Condition.Var[] vars = c.getVars();
        for (int i = 0; i < vars.length; i++) {
            if (!c.isPositiveTest(vars[i])) {
                writeInset(out, inset);
                out.write(OPEN_ELM);
                out.write(ProfileContentHandler.NOT_TAG.getBytes());
                out.write(CLOSE_ELM_NL);
                
                writeConditionVarElement(out, inset + INSET, vars[i]);
                
                writeInset(out, inset);
                out.write(OPEN_ELM_END);
                out.write(ProfileContentHandler.NOT_TAG.getBytes());
                out.write(CLOSE_ELM_NL);
            } else {
                writeConditionVarElement(out, inset, vars[i]);
            }
        }
        // Write the sub-conditions
        Condition[] subConditions = c.getConditions();
        for (int i = 0; i < subConditions.length; i++) {
            if (!c.isPositiveTest(subConditions[i])) {
                writeInset(out, inset);
                out.write(OPEN_ELM);
                out.write(ProfileContentHandler.NOT_TAG.getBytes());
                out.write(CLOSE_ELM_NL);
                
                writeConditionElements(out, inset + INSET, subConditions[i], true);
                
                writeInset(out, inset);
                out.write(OPEN_ELM_END);
                out.write(ProfileContentHandler.NOT_TAG.getBytes());
                out.write(CLOSE_ELM_NL);
            } else {
                writeConditionElements(out, inset, subConditions[i], true);
            }
        }
    }
    
    /** Write the variable element of a condition. */
    private static void writeConditionVarElement(OutputStream out, int inset,
                                                 Condition.Var var) throws IOException {
        writeInset(out, inset);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.VAR_TAG.getBytes());
        writeAttribute(out, ProfileContentHandler.VAR_NAME_ATTR,  var.getName());
        
        String attrName;
        int compareValue = var.getCompareValue();
        if (Condition.COMPARE_VALUE_EQUALS == compareValue) {
            attrName = ProfileContentHandler.VAR_VALUE_ATTR;
        } else if (Condition.COMPARE_VALUE_EQUALS_IGNORE_CASE == compareValue) {
            attrName = ProfileContentHandler.VAR_VALUE_IGNORE_CASE_ATTR;
        } else if (Condition.COMPARE_VALUE_CONTAINS == compareValue) {
            attrName = ProfileContentHandler.VAR_VALUE_CONTAINS_ATTR;
        } else if (Condition.COMPARE_VALUE_CONTAINS_IGNORE_CASE == compareValue) {
            attrName = ProfileContentHandler.VAR_VALUE_CONTAINS_IGNORE_CASE_ATTR;
        } else {
            attrName = null;
        }
        if (attrName != null) {
            writeAttribute(out, attrName, var.getValue());
        }
        out.write(CLOSE_END_ELM_NL);
    }
    
    /* END of conditions. */
    
    
    private static void writeVariables(OutputStream out, int inset,
                                       ConditionedVariables cvars) throws IOException {
        writeInset(out, inset);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.VARIABLES_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
        
        List varsCollection = new ArrayList(); // The list of all variables. Mixed VcsConfigVariable and String objects
        Collection uncVars = cvars.getUnconditionedVariables(); // Unconditioned variables - VcsConfigVariable objects
        Map conditionsByVariables = cvars.getConditionsByVariables();
        Map varsByConditions = cvars.getVariablesByConditions();
        varsCollection.addAll(uncVars);
        varsCollection.addAll(conditionsByVariables.keySet());
        Collections.sort(varsCollection, new VariablesComparator());
        for (Iterator it = varsCollection.iterator(); it.hasNext(); ) {
            Object varObj = it.next();
            if (varObj instanceof VcsConfigVariable) {
                // An unconditioned variable
                writeVariable(out, inset + INSET, (VcsConfigVariable) varObj, null, null);
            } else {
                // An conditioned variable
                String name = (String) varObj;
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
                writeVariable(out, inset + INSET, var, c, valuesByConditions);
            }
        }
        
        writeInset(out, inset);
        out.write(OPEN_ELM_END);
        out.write(ProfileContentHandler.VARIABLES_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
    }
    
    private static void writeVariable(OutputStream out, int inset, VcsConfigVariable var,
                                      Condition c, Map valuesByConditions) throws IOException {
        writeInset(out, inset);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.VARIABLE_TAG.getBytes());
        
        writeAttribute(out, ProfileContentHandler.VARIABLE_NAME_ATTR, var.getName());
        
        if (var.isBasic()) {
            writeAttribute(out, ProfileContentHandler.VARIABLE_BASIC_ATTR,
                                ProfileContentHandler.BOOLEAN_VARIABLE_TRUE);
            
            writeAttribute(out, ProfileContentHandler.VARIABLE_LABEL_ATTR,
                                var.getLabel());
            
            if (var.getLabelMnemonic() != null) {
                writeAttribute(out, ProfileContentHandler.VARIABLE_LABEL_MNEMONIC_ATTR,
                                    var.getLabelMnemonic().toString());
            }
            if (var.getA11yName() != null) {
                writeAttribute(out, ProfileContentHandler.VARIABLE_A11Y_NAME_ATTR,
                                    var.getA11yName());
            }
            if (var.getA11yDescription() != null) {
                writeAttribute(out, ProfileContentHandler.VARIABLE_A11Y_DESCRIPTION_ATTR,
                                    var.getA11yDescription());
            }
            if (var.isLocalFile()) {
                writeAttribute(out, ProfileContentHandler.VARIABLE_LOCAL_FILE_ATTR,
                                    ProfileContentHandler.BOOLEAN_VARIABLE_TRUE);
            }
            if (var.isLocalDir()) {
                writeAttribute(out, ProfileContentHandler.VARIABLE_LOCAL_DIR_ATTR,
                                    ProfileContentHandler.BOOLEAN_VARIABLE_TRUE);
            }
            if (var.isExecutable()) {
                writeAttribute(out, ProfileContentHandler.VARIABLE_EXECUTABLE_ATTR,
                                    ProfileContentHandler.BOOLEAN_VARIABLE_TRUE);
            }
            if (var.getOrder() != 0) {
                writeAttribute(out, ProfileContentHandler.VARIABLE_ORDER_ATTR,
                                    Integer.toString(var.getOrder()));
            }
            String selector = var.getCustomSelector();
            if (selector != null && selector.length() > 0) {
                writeAttribute(out, ProfileContentHandler.VARIABLE_SELECTOR_ATTR,
                                    selector);
            }
            if (c != null) writeConditionAttributes(out, c);
        }
        
        out.write(CLOSE_ELM_NL);
        
        if (valuesByConditions != null) {
            TreeSet sortedConditions = new TreeSet(new ConditionalsComparator());
            sortedConditions.addAll(valuesByConditions.keySet());
            for (Iterator it = sortedConditions.iterator(); it.hasNext(); ) {
                Condition vc = (Condition) it.next();
                String value = (String) valuesByConditions.get(vc);
                writeValue(out, inset + INSET, value, vc);
            }
        } else {
            writeValue(out, inset + INSET, var.getValue(), null);
        }
        
        writeInset(out, inset);
        out.write(OPEN_ELM_END);
        out.write(ProfileContentHandler.VARIABLE_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
    }
    
    /**
     * Write the conditional atributes <code>if="condition"</code> and <code>unless="condition"</code>.
     * The provided Condition object is expected to have one or two variables,
     * one with the positive test (for unless attribute - equals to an empty String)
     * and one with negative test (for if attribute - does not equal to an empty String).
     */
    private static void writeConditionAttributes(OutputStream out, Condition c) throws IOException {
        Condition.Var[] cvars = c.getVars();
        for (int i = 0; i < cvars.length; i++) {
            if (c.isPositiveTest(cvars[i])) {
                writeAttribute(out, ProfileContentHandler.UNLESS_ATTR,
                               cvars[i].getName());
            } else {
                writeAttribute(out, ProfileContentHandler.IF_ATTR,
                               cvars[i].getName());
            }
        }
    }
    
    /**
     * Write the value element in the form of
     * <code><b>&lt;value&nbsp;xml:space="preserve"&gt;</b>value<b>&lt;/value&gt;</b></code>
     * with an optional condition attributes.
     */
    private static void writeValue(OutputStream out, int inset, String value, Condition c) throws IOException {
        writeInset(out, inset);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.VARIABLE_PROPERTY_VALUE_TAG.getBytes());
        writeAttribute(out, "xml:space", "preserve");
        if (c != null) {
            writeConditionAttributes(out, c);
        }
        out.write(CLOSE_ELM);
        out.write(XMLUtil.toElementContent(value).getBytes());
        out.write(OPEN_ELM_END);
        out.write(ProfileContentHandler.VARIABLE_PROPERTY_VALUE_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
    }
    
    /* END of variables */
    
    
    private static void writeCommands(OutputStream out, int inset, ConditionedCommands commands) throws IOException {
        writeCommands(out, inset, commands, commands.getCommands());
    }
    
    private static void writeCommands(OutputStream out, int inset,
                                      ConditionedCommands commands, CommandsTree tree) throws IOException {
        writeInset(out, inset);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.COMMANDS_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
        
        writeTheCommands(out, inset + INSET, commands, tree);
        
        writeInset(out, inset);
        out.write(OPEN_ELM_END);
        out.write(ProfileContentHandler.COMMANDS_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
    }
        
    private static void writeGlobalCommands(OutputStream out, int inset, ConditionedCommands commands) throws IOException {
        if (commands == null) return ;
        writeInset(out, inset);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.GLOBAL_COMMANDS_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
        
        writeTheCommands(out, inset + INSET, commands, commands.getCommands());
        
        writeInset(out, inset);
        out.write(OPEN_ELM_END);
        out.write(ProfileContentHandler.GLOBAL_COMMANDS_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
    }
    
    private static void writeTheCommands(OutputStream out, int inset,
                                         ConditionedCommands cc, CommandsTree tree) throws IOException {
        CommandsTree[] commandChildren = tree.children();
        for (int i = 0; i < commandChildren.length; i++) {
            CommandsTree commandNode = commandChildren[i];
            CommandSupport supp = commandNode.getCommandSupport();
            //VcsCommand cmd = (VcsCommand) commandNode.getCookie(VcsCommand.class);
            if (supp == null || !(supp instanceof UserCommandSupport)) {
                writeInset(out, inset);
                out.write(OPEN_ELM);
                out.write(ProfileContentHandler.SEPARATOR_TAG.getBytes());
                out.write(CLOSE_END_ELM_NL);
                out.write(NL);
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
                Arrays.sort(conditions, new ConditionalsComparator());
                for (int ic = 0; ic < conditions.length; ic++) {
                    ConditionedPropertiesCommand cpcmd = ccmd.getCommandFor(conditions[ic]);
                    // Test the command so that we find the right one!
                    if (!uSupport.equals(cpcmd.getCommand())) continue;
                    
                    writeInset(out, inset);
                    out.write(OPEN_ELM);
                    out.write(ProfileContentHandler.COMMAND_TAG.getBytes());
                    writeAttribute(out, ProfileContentHandler.COMMAND_NAME_ATTR, cmd.getName());
                    String displayName = cmd.getDisplayName();
                    if (displayName != null) {
                        writeAttribute(out, ProfileContentHandler.COMMAND_DISPLAY_NAME_ATTR, displayName);
                    }
                    if (conditions[ic] != null) {
                        writeConditionAttributes(out, conditions[ic]);
                    }
                    out.write(CLOSE_ELM_NL);
                    writeProperties(out, inset + INSET, cpcmd);
                    if (commandNode.hasChildren()) {
                        writeTheCommands(out, inset + INSET, cc, commandNode);
                    }
                    writeInset(out, inset);
                    out.write(OPEN_ELM_END);
                    out.write(ProfileContentHandler.COMMAND_TAG.getBytes());
                    out.write(CLOSE_ELM_NL);
                    out.write(NL);
                }
            } else {
                writeInset(out, inset);
                out.write(OPEN_ELM);
                out.write(ProfileContentHandler.COMMAND_TAG.getBytes());
                writeAttribute(out, ProfileContentHandler.COMMAND_NAME_ATTR, cmd.getName());
                String displayName = cmd.getDisplayName();
                if (displayName != null) {
                    writeAttribute(out, ProfileContentHandler.COMMAND_DISPLAY_NAME_ATTR, displayName);
                }
                out.write(CLOSE_ELM_NL);
                writeProperties(out, inset + INSET, cmd);
                if (commandNode.hasChildren()) {
                    writeTheCommands(out, inset + INSET, cc, commandNode);
                }
                writeInset(out, inset);
                out.write(OPEN_ELM_END);
                out.write(ProfileContentHandler.COMMAND_TAG.getBytes());
                out.write(CLOSE_ELM_NL);
                out.write(NL);
            }
        }
    }
    
    private static void writeProperties(OutputStream out, int inset,
                                        VcsCommand cmd) throws IOException {
        String[] properties = cmd.getPropertyNames();
        Arrays.sort(properties);
        for (int j = 0; j < properties.length; j++) {
            Object value = cmd.getProperty(properties[j]);
            if (value == null) continue;
            if (VcsCommand.PROPERTY_EXEC_STRUCTURED.equals(properties[j])) {
                writeStructuredExec(out, inset, (StructuredExec) value);
                continue;
            }
            String valueStr = UserCommandIO.getPropertyValueStr(properties[j], value);
            if (valueStr == null) continue;
            writeProperty(out, inset, properties[j], valueStr);
        }
    }
    
    private static void writeProperties(OutputStream out, int inset,
                                        ConditionedPropertiesCommand cpcmd) throws IOException {
        
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
                if (VcsCommand.PROPERTY_EXEC_STRUCTURED.equals(allProperties[i])) {
                    writeStructuredExec(out, inset, c, property.getValuesByConditions());
                } else {
                    writeProperty(out, inset, allProperties[i], c,
                                  property.getValuesByConditions());
                }
            } else {
                Object value = cmd.getProperty(allProperties[i]);
                if (value == null) continue;
                if (VcsCommand.PROPERTY_EXEC_STRUCTURED.equals(allProperties[i])) {
                    writeStructuredExec(out, inset, (StructuredExec) value);
                    continue;
                }
                String valueStr = UserCommandIO.getPropertyValueStr(allProperties[i], value);
                if (valueStr == null) continue;
                writeProperty(out, inset, allProperties[i], valueStr);
            }
        }
    }
    
    private static void writeProperty(OutputStream out, int inset, String name, String value) throws IOException {
        writeInset(out, inset);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.PROPERTY_TAG.getBytes());
        writeAttribute(out, ProfileContentHandler.PROPERTY_NAME_ATTR, name);
        out.write(CLOSE_ELM_NL);

        writeValue(out, inset + INSET, value, null);
        
        writeInset(out, inset);
        out.write(OPEN_ELM_END);
        out.write(ProfileContentHandler.PROPERTY_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
    }
    
    private static void writeProperty(OutputStream out, int inset, String name,
                                      Condition c, Map valuesByConditions) throws IOException {
        writeInset(out, inset);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.PROPERTY_TAG.getBytes());
        writeAttribute(out, ProfileContentHandler.PROPERTY_NAME_ATTR, name);
        if (c != null) {
            writeConditionAttributes(out, c);
        }
        out.write(CLOSE_ELM_NL);
        
        TreeSet sortedConditions = new TreeSet(new ConditionalsComparator());
        sortedConditions.addAll(valuesByConditions.keySet());
        for (Iterator it = sortedConditions.iterator(); it.hasNext(); ) {
            Condition vc = (Condition) it.next();
            Object value = valuesByConditions.get(vc);
            if (value == null) continue;
            String valueStr = UserCommandIO.getPropertyValueStr(name, value);
            Condition subc;
            if (vc != null && !vc.equals(c)) {
                subc = vc;
            } else {
                subc = null;
            }
            writeValue(out, inset + INSET, valueStr, subc);
        }
        
        writeInset(out, inset);
        out.write(OPEN_ELM_END);
        out.write(ProfileContentHandler.PROPERTY_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
    }
    
    private static void writeStructuredExec(OutputStream out, int inset,
                                            StructuredExec exec) throws IOException {
        writeInset(out, inset);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.RUN_TAG.getBytes());
        if (exec.getWorking() != null) {
            writeAttribute(out, ProfileContentHandler.RUN_DIR_ATTR, exec.getWorking().getPath());
        }
        out.write(CLOSE_ELM_NL);
        
        writeInset(out, inset + INSET);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.EXEC_TAG.getBytes());
        writeAttribute(out, ProfileContentHandler.EXEC_VALUE_ATTR, exec.getExecutable()); 
        out.write(CLOSE_END_ELM_NL);
        
        writeExecArguments(out, inset + INSET, exec.getArguments());
        
        writeInset(out, inset);
        out.write(OPEN_ELM_END);
        out.write(ProfileContentHandler.RUN_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
    }
    
    private static void writeStructuredExec(OutputStream out, int inset,
                                            Condition c, Map execsByConditions) throws IOException {
        TreeSet sortedConditions = new TreeSet(new ConditionalsComparator());
        sortedConditions.addAll(execsByConditions.keySet());
        
        writeInset(out, inset);
        out.write(OPEN_ELM);
        out.write(ProfileContentHandler.RUN_TAG.getBytes());
        if (sortedConditions.size() == 0) {
            out.write(CLOSE_END_ELM_NL);
            return ;
        }
        File working = ((StructuredExec) execsByConditions.values().iterator().next()).getWorking();
        if (working != null) {
            String workingPath = working.getPath();
            writeAttribute(out, ProfileContentHandler.RUN_DIR_ATTR, workingPath);
        }
        if (c != null) {
            writeConditionAttributes(out, c);
        }
        out.write(CLOSE_ELM_NL);
        
        StructuredExec oneExec = null;
        for (Iterator it = sortedConditions.iterator(); it.hasNext(); ) {
            Condition ec = (Condition) it.next();
            StructuredExec exec = (StructuredExec) execsByConditions.get(ec);
            oneExec = exec;
            
            writeInset(out, inset + INSET);
            out.write(OPEN_ELM);
            out.write(ProfileContentHandler.EXEC_TAG.getBytes());
            writeAttribute(out, ProfileContentHandler.EXEC_VALUE_ATTR, exec.getExecutable()); 
            if (ec != null && !ec.equals(c)) {
                writeConditionAttributes(out, ec);
            }
            out.write(CLOSE_END_ELM_NL);
        }
        if (oneExec != null) { // The arguments are shared for all conditioned execs
            writeExecArguments(out, inset + INSET, oneExec.getArguments());
        }

        writeInset(out, inset);
        out.write(OPEN_ELM_END);
        out.write(ProfileContentHandler.RUN_TAG.getBytes());
        out.write(CLOSE_ELM_NL);
    }
    
    private static void writeExecArguments(OutputStream out, int inset,
                                           StructuredExec.Argument[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            writeInset(out, inset);
            out.write(OPEN_ELM);
            out.write(ProfileContentHandler.ARG_TAG.getBytes());
            
            if (args[i].isLine()) {
                writeAttribute(out, ProfileContentHandler.ARG_LINE_ATTR, args[i].getArgument());
            } else {
                writeAttribute(out, ProfileContentHandler.ARG_VALUE_ATTR, args[i].getArgument());
            }
            if (args[i] instanceof ConditionedStructuredExec.ConditionedArgument) {
                Condition c = ((ConditionedStructuredExec.ConditionedArgument) args[i]).getCondition();
                writeConditionAttributes(out, c);
            }
            
            out.write(CLOSE_END_ELM_NL);
        }
    }
    
    
    
    /**
     * Compares the Condition objects by their names.
     */
    private static final class ConditionsComparator extends Object implements Comparator {
        
        public int compare(Object o1, Object o2) {
            return ((Condition) o1).getName().compareTo(((Condition) o2).getName());
        }
        
    }
    
    /**
     * Compares the Condition objects that are used to as conditionals to values,
     * properties, commands, etc.
     */
    private static final class ConditionalsComparator extends Object implements Comparator {
        
        public int compare(Object o1, Object o2) {
            Condition c1 = (Condition) o1;
            Condition c2 = (Condition) o2;
            if (c1 == null) return +1; // put unconditioned at the end
            if (c2 == null) return -1;
            Condition.Var[] vars1 = c1.getVars();
            Condition.Var[] vars2 = c2.getVars();
            String[] ifUnless1 = retrieveIfUnless(c1, vars1); // IF and UNLESS var names
            String[] ifUnless2 = retrieveIfUnless(c2, vars2); // IF and UNLESS var names
            if (!ifUnless1[0].equals(ifUnless2[0])) {
                return ifUnless1[0].compareTo(ifUnless2[0]); // Compare the IF attributes first
            } else {
                return ifUnless1[1].compareTo(ifUnless2[1]); // If IF are the same then UNLESS
            }
        }
        
        private static String[] retrieveIfUnless(Condition c, Condition.Var[] vars) {
            String[] ifUnless = new String[] { "", "" };
            for (int i = 0; i < vars.length; i++) {
                if (c.isPositiveTest(vars[i])) {
                    // Unless
                    ifUnless[1] = vars[i].getName();
                } else {
                    // If
                    ifUnless[0] = vars[i].getName();
                }
            }
            return ifUnless;
        }
        
    }
    
    /**
     * A special variables comparator, that is able to compare VcsConfigVariable objects by names
     * with String objects representing the names.
     */
    private static final class VariablesComparator extends Object implements Comparator {
        
        public int compare(Object o1, Object o2) {
            String name1;
            String name2;
            if (o1 instanceof VcsConfigVariable) {
                name1 = ((VcsConfigVariable) o1).getName();
            } else {
                name1 = (String) o1;
            }
            if (o2 instanceof VcsConfigVariable) {
                name2 = ((VcsConfigVariable) o2).getName();
            } else {
                name2 = (String) o2;
            }
            return name1.compareTo(name2);
        }
        
    }
    
}
