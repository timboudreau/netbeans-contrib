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
    
    public static Object getPropertyValue(String[] resourceBundles, String name, String valueStr) {
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
            if (VcsCommand.PROPERTY_INPUT_DESCRIPTOR.equals(name)) {
                return valueStr;
            } else {
                return VcsUtilities.getBundleString(resourceBundles, valueStr);
            }
        } else if (String[].class.equals(type)) {
            return convertString2StringArray(valueStr);
        } else return valueStr;
    }
    
    public static String getPropertyValueStr(String name, Object value) {
        if (name.indexOf(VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY) >= 0) return null;
        Class type = CommandNode.getPropertyClass(name);
        if (String[].class.equals(type)) {
            return convertStringArray2String((String[]) value);
        }
        return value.toString();
    }

    /**
     * Perform a translation of command's property value. Currently just class names
     * are translated according to a translation table.
     */
    public static String translateCommandProperty(String propertyName, String propertyValue) {
        if (UserCommand.PROPERTY_EXEC.equals(propertyName)) {
            int classIndex = 0;
            while ((classIndex = propertyValue.indexOf(".class", classIndex)) > 0) {
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
                    classIndex = begin + classNameNew.length() + ".class".length();
                } else {
                    classIndex += ".class".length();
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
        if (table == null) return new String[0][0];
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
