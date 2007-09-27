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

package org.netbeans.modules.rmi.activation;

import java.awt.datatransfer.StringSelection;
import java.rmi.activation.*;
import java.text.*;
import java.util.*;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.util.datatransfer.ExClipboard;

/** Action generates setup code for elements of an activation system and sets
 * it to the clipboard.
 *
 * @author  Jan Pokorsky
 */
public class GenerateSetupAction extends CookieAction {
    /** Action is sensitive to ActivationSystemNode and ActivationNode. */
    protected Class[] cookieClasses () {
        return new Class[] { ActivationSystemNode.class, ActivationNode.class };
    }
    /** All nodes must implement cookies. */
    protected int mode () {
        return MODE_ALL;
    }

    protected void performAction (Node[] nodes) {
        ExClipboard ec = (ExClipboard)Lookup.getDefault ().lookup (ExClipboard.class);
        ec.setContents(new StringSelection(generate(shakeNodes(nodes))), null);
    }
    
    /** Returns only selected items of the activation system and gets rid of
     * items duplicities.
     * @param nodes selected nodes
     * @return mapping of objects to groups {ActivationGroupItem, Set{ActivationObjectItem}}
     */
    private Map shakeNodes(Node[] nodes) {
        HashSet selectedSystems = new HashSet();
        HashSet selectedGroups = new HashSet();
        HashSet selectedObjects = new HashSet();
        HashMap generateItems = new HashMap();  // {ActivationGroupItem, Set{ActivationObjectItem}}
        
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof ActivationSystemNode) {
                selectedSystems.add(((ActivationSystemNode) nodes[i]).getActivationSystem());
            } else if (nodes[i] instanceof ActivationGroupNode) {
                selectedGroups.add(((ActivationNode) nodes[i]).getItem());
            } else if (nodes[i] instanceof ActivationObjectNode) {
                selectedObjects.add(((ActivationNode) nodes[i]).getItem());
            }
        }
            
        // add groups for selected objects
        Iterator iter = selectedObjects.iterator();
        while (iter.hasNext()) {
            ActivationObjectItem item = (ActivationObjectItem) iter.next();
            ActivationGroupItem gItem = item.getActivationSystemItem().getActivationGroupItem(item);
            if (!selectedSystems.contains(item.getActivationSystemItem()) &&
                !selectedGroups.contains(gItem))
            {
                HashSet h = (HashSet) generateItems.get(gItem);
                if (h == null) {
                    h = new HashSet();
                    generateItems.put(gItem, h);
                }
                h.add(item);
            }
        }

        // add selected groups
        iter = selectedGroups.iterator();
        while (iter.hasNext()) {
            ActivationGroupItem gItem = (ActivationGroupItem) iter.next();
            if (!selectedSystems.contains(gItem.getActivationSystemItem())) {
                generateItems.put(gItem, gItem.getActivatables());
            }
        }

        // if a. system is selected, all its items will be generated
        Iterator itSystem = selectedSystems.iterator();
        while (itSystem.hasNext()) {
            ActivationSystemItem asItem = (ActivationSystemItem) itSystem.next();
            // add all groups
            iter = asItem.getActivationGroupItems().iterator();
            while (iter.hasNext()) {
                ActivationGroupItem gItem = (ActivationGroupItem) iter.next();
                generateItems.put(gItem, gItem.getActivatables());
            }
        }
        
        return generateItems;
    }

    public String getName () {
        return NbBundle.getMessage (GenerateSetupAction.class, "LBL_GenerateSetupAction");  // NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (GenerateSetupAction.class);
    }

    /** Perform extra initialization of this action's singleton.
     * PLEASE do not use constructors for this purpose!
    protected void initialize () {
	super.initialize ();
	putProperty ("someProp", value);
    }
    */

    private static final char NL = '\n';
    private static final char QUOTATION = '"';
    private static final String INDENT = "    ";  // NOI18N
    private static final String NULL = "null";  // NOI18N
    
    private static MessageFormat msgCmd =
        new MessageFormat("java.rmi.activation.ActivationGroupDesc.CommandEnvironment cmd{0} =\n{1}new java.rmi.activation.ActivationGroupDesc.CommandEnvironment({2}, {3});");  // NOI18N
    private static MessageFormat msgGroupDesc =
        new MessageFormat("java.rmi.activation.ActivationGroupDesc groupDesc{0} =\n{1}new java.rmi.activation.ActivationGroupDesc({2}, {3}, groupData{0}, {4}, {5});");  // NOI18N
    private static MessageFormat msgMarshalled =
        new MessageFormat("java.rmi.MarshalledObject {0}Data{1} = null;");  // NOI18N
    private static MessageFormat msgGroupRegistration =
        new MessageFormat("java.rmi.activation.ActivationGroupID groupID{0} =\n{1}system.registerGroup(groupDesc{0});");  // NOI18N
    private static MessageFormat msgObjDesc =
        new MessageFormat("java.rmi.activation.ActivationDesc desc{0} =\n{1}new java.rmi.activation.ActivationDesc(groupID{2}, {3}, {4}, objData{0}, {5});");  // NOI18N
    private static MessageFormat msgObjRegistration =
        new MessageFormat("system.registerObject(desc{0});");  // NOI18N
    private static MessageFormat msgPutProperty =
        new MessageFormat("overrides{0}.put(\"{1}\", \"{2}\");");  // NOI18N
    private static MessageFormat msgProperties =
        new MessageFormat("java.util.Properties overrides{0} = new java.util.Properties();");  // NOI18N
    
    /** Counter for variables using during generating of groups. */
    private int groupCounter;
    /** Counter for variables using during generating of objects. */
    private int objCounter;
    
    /** Generates setup code for selected part of the activation system.
     * @param generateItems mapping of objects to groups {ActivationGroupItem, Set{ActivationObjectItem}}
     * @return setup code
     */
    public String generate(Map generateItems) {
        if (generateItems.isEmpty())
            return "";  // NOI18N
        
        groupCounter = 0;
        objCounter = 0;
        
        StringBuffer code = new StringBuffer();
        // assign current activation system
        code.append(generateSystem()).append(NL);
        
        // generate groups
        ArrayList list = new ArrayList(generateItems.keySet());
        Collections.sort(list);
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            ActivationGroupItem agi = (ActivationGroupItem) iter.next();
            code.append(generateGroup(agi, (Set) generateItems.get(agi)))
                .append(NL);
        }
        
        return code.toString();
    }
    
    /** Generates code for an activation system. */
    private String generateSystem() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("// activation system lookup")
            .append(NL)
            .append("java.rmi.registry.Registry registry = ")  // NOI18N
            .append(NL).append(INDENT)
            .append("java.rmi.registry.LocateRegistry.getRegistry(java.rmi.activation.ActivationSystem.SYSTEM_PORT);")  // NOI18N
            .append(NL);
        sbuf.append("java.rmi.activation.ActivationSystem system =")  // NOI18N
            .append(NL).append(INDENT)
            .append("(java.rmi.activation.ActivationSystem) registry.lookup(\"java.rmi.activation.ActivationSystem\");")  // NOI18N
            .append(NL);
        return sbuf.toString();
    }
    
    /** Generates code for particular group and its objects.
     * @param item an activation group item
     * @param objectItems set of selected objects from the group.
     * @return generated code.
     */
    private String generateGroup(ActivationGroupItem item, Set objectItems) {
        String groupIndex = String.valueOf(++groupCounter);
        
        ActivationGroupDesc desc = item.getDesc();
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("// new activation group").append(NL);  // NOI18N
        
        // command environment
        String cmdStr, overrideStr;
        ActivationGroupDesc.CommandEnvironment cmd = desc.getCommandEnvironment();
        if (cmd != null) {
            String cmdpath = cmd.getCommandPath();
            if (cmdpath != null) {
                cmdpath = quotation(escape(cmdpath));
            }
            String argv = generateStringArray(cmd.getCommandOptions());
            sbuf.append(msgCmd.format(new String[] {groupIndex, INDENT, cmdpath, argv})).append(NL);
            cmdStr = new StringBuffer("cmd").append(groupIndex).toString();  // NOI18N
        } else {
            cmdStr = NULL;
        }
        
        // properties
        Properties overrides = desc.getPropertyOverrides();
        if (overrides != null) {
            sbuf.append(generateProperties(overrides, groupIndex));
            overrideStr = new StringBuffer("overrides").append(groupIndex).toString();  // NOI18N
        } else {
            overrideStr = NULL;
        }
        
        // Marshalled data
        sbuf.append(msgMarshalled.format(new String[] { "group", groupIndex }))  // NOI18N
            .append(NL);
        
        // activation group descriptor
        sbuf.append(msgGroupDesc.format(new String[] {
            groupIndex,
            INDENT,
            quotation(desc.getClassName()),
            quotation(escape(desc.getLocation())),
            overrideStr,
            cmdStr
            }))
            .append(NL);
        
        // group registration
        sbuf.append(msgGroupRegistration.format(new String[] { groupIndex, INDENT }))
            .append(NL);
        
        // objects registration
        ArrayList list = new ArrayList(objectItems);
        if (!list.isEmpty()) {
            Collections.sort(list);
            sbuf.append(NL).append("// register objects").append(NL);  // NOI18N
            Iterator iter = list.iterator();
            while (iter.hasNext()) {
                sbuf.append(generateObject((ActivationObjectItem) iter.next()))
                    .append(NL);
            }
        }
        return sbuf.toString();
    }
    
    /** Generates code for particular object.
     * @param item an activation object item
     * @return generated code.
     */
    private String generateObject(ActivationObjectItem item) {
        String groupIndex = String.valueOf(groupCounter);
        String objIndex = String.valueOf(++objCounter);
        StringBuffer sbuf = new StringBuffer();
        ActivationDesc desc = item.getDesc();
        
        // Marshalled data
        sbuf.append(msgMarshalled.format(new String[] { "obj", objIndex }))  // NOI18N
            .append(NL);
        
        // activation descriptor
        sbuf.append(msgObjDesc.format(new String[] {
                objIndex,
                INDENT,
                groupIndex,
                quotation(desc.getClassName()),
                quotation(escape(desc.getLocation())),
                String.valueOf(desc.getRestartMode())
            }))
            .append(NL);
            
        // object registration
        sbuf.append(msgObjRegistration.format(new String[] { objIndex }))
            .append(NL);
        
        return sbuf.toString();
    }
    
    /** Generates code for an array of strings. */
    private String generateStringArray(String[] txts) {
        if (txts == null || txts.length == 0)
            return NULL;
        
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("new String[] {");  // NOI18N
        for (int i = 0; i < txts.length; i++) {
            sbuf.append(quotation(escape(txts[i]))).append(", ");  // NOI18N
        }
        sbuf.deleteCharAt(sbuf.length() - 1);
        sbuf.setCharAt(sbuf.length() - 1, '}');
        
        return sbuf.toString();
    }
    
    /** Generates code for group's properties. */
    private String generateProperties(Properties props, String groupIndex) {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(msgProperties.format(new String[] { groupIndex, INDENT }))
            .append(NL);
        
        Enumeration keys = props.keys();
        String[] pair = new String[3];
        pair[0] = groupIndex;
        while (keys.hasMoreElements()) {
            pair[1] = (String) keys.nextElement();
            pair[2] = escape(props.getProperty(pair[1]));
            sbuf.append(msgPutProperty.format(pair)).append(NL);
        }
        
        return sbuf.toString();
    }
    
    /** Wrap text with quotations. */
    private String quotation(String txt) {
        if (txt == null) return NULL;
        return new StringBuffer()
               .append(QUOTATION)
               .append(txt)
               .append(QUOTATION)
               .toString();
    }
    
    /** Translate text. */
    private String escape(String txt) {
        if (txt == null) return NULL;
        
        StringBuffer sbuf = new StringBuffer(txt.length());
        char c;
        for (int i = 0; i < txt.length(); i++) {
            c = txt.charAt(i);
            switch (c) {
                case '\b': sbuf.append("\\b"); break;  // NOI18N
                case '\t': sbuf.append("\\t"); break;  // NOI18N
                case '\n': sbuf.append("\\n"); break;  // NOI18N
                case '\f': sbuf.append("\\f"); break;  // NOI18N
                case '\r': sbuf.append("\\r"); break;  // NOI18N
                case '\"': sbuf.append("\\\""); break;  // NOI18N
                case '\\': sbuf.append("\\\\"); break;  // NOI18N
                default: sbuf.append(c);
            }
        }
        
        return sbuf.toString();
    }
    
}
