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

package org.netbeans.modules.vcscore;

import java.io.IOException;
import java.util.*;

import org.openide.ErrorManager;

import org.netbeans.modules.vcscore.util.*;
//import org.netbeans.modules.vcscore.*;

/**
 * This class contains static methods for variable expansion.
 * <p>
 * Expand Bash style variables, e.g. <code>${USER}</code>, with some additional gramatic.
 * 
 * @author Michal Fadljevic, Pavel Buzek, Martin Entlicher
 */

//-------------------------------------------
public class Variables {
    private static final Debug E=new Debug("Variables", true); // NOI18N
    private static final Debug D=E;

    /**
     * Automatically set context-specific variables.
     */
    
    /** The name of a variable, whose value is set to the selected file name.
     * When more than one file is selected, the first one is taken.
     */
    public static final String FILE = "FILE";

    /** The name of a variable, whose value is set to the selected file name quoted
     * by the value of the QUOTING variable.
     * When more than one file is selected, the first one is taken.
     */
    public static final String QFILE = "QFILE";

    /** The name of a variable, whose value is set to the selected file names.
     * They are delimeted by the path separator.
     */
    public static final String FILES = "FILES";

    /** The name of a variable, whose value is set to the selected file names each
     * quoted by the value of the QUOTING variable and delimeted by spaces.
     */
    public static final String QFILES = "QFILES";
    
    public static final String DIR = "DIR";
    public static final String PATH = "PATH";
    public static final String QPATH = "QPATH";
    public static final String PATHS = "PATHS";
    public static final String QPATHS = "QPATHS";
    public static final String MPATHS = "MPATHS";
    public static final String QMPATHS = "QMPATHS";

    public static final String MIMETYPE = "MIMETYPE";
    
    public static final String NUM_FILES = "NUM_FILES";
    public static final String NUM_IMPORTANT_FILES = "NUM_IMPORTANT_FILES";
    public static final String FILE_IS_FOLDER = "FILE_IS_FOLDER";
    public static final String FILES_IS_FOLDER = "FILES_IS_FOLDER";
    public static final String MULTIPLE_FILES = "MULTIPLE_FILES";

    /**
     * Variables with special functionality.
     */
    
    /**
     * This variable is defined only when used in the execution string
     * and is filled with the full path to a temporary file, that is created
     * and exists ONLY while the command is running. The file is deleted
     * automatically as soon as the command finish.
     */
    public static final String TEMPORARY_FILE = "TEMPORARY_FILE"; // NOI18N

    /**
     * When non-empty, the directory reader should read also files, which were deleted
     * from the version control system, but their old revisions exist.
     */
    public static final String SHOW_DEAD_FILES = "SHOW_DEAD_FILES";
    
    /**
     * The content of this variable is taken as a message,
     * that is used to ask whether the EDIT command should be executed.
     * This message is used only when the execution is caused by typing
     * to a read-oly file.
     */
    public static final String MSG_PROMPT_FOR_AUTO_EDIT = "MSG_PROMPT_FOR_AUTO_EDIT";
    /**
     * The content of this variable is taken as a message,
     * that is used to ask whether the LOCK command should be executed.
     * This message is used only when the execution is caused by typing
     * to a file, that is not locked by the user.
     */
    public static final String MSG_PROMPT_FOR_AUTO_LOCK = "MSG_PROMPT_FOR_AUTO_LOCK";
    
    /**
     * If this variable is defined, than it's value (should be integer) is supposed
     * to be the maximum length of a command in the system.
     * If the length of the execution string exceeds this value and it's possible
     * to split it (the command takes more than one file as an argument),
     * the execution string is split into two or more execution strings.
     * These are then executed synchronously in the OS.
     */
    public static final String MAX_CMD_LENGTH = "MAX_CMD_LENGTH"; // NOI18N
    
    //private boolean warnUndefVars = true;

    private static final String SUBSTRACT = "-"; // NOI18N
    private static final String REPLACE = "_"; // NOI18N
    
    private static Collection contextVariablesNames = null;

    private Variables() {
    }
    
    public static synchronized Collection getContextVariablesNames() {
        if (contextVariablesNames == null) {
            contextVariablesNames = createContextVariablesNames();
        }
        return contextVariablesNames;
    }
    
    private static Collection createContextVariablesNames() {
        HashSet varNames = new HashSet();
        varNames.addAll(Arrays.asList(new String[] {
            FILE, QFILE, FILES, QFILES, DIR,
            PATH, QPATH, PATHS, QPATHS, MPATHS, QMPATHS,
            MIMETYPE, NUM_FILES, NUM_IMPORTANT_FILES,
            FILE_IS_FOLDER, FILES_IS_FOLDER, MULTIPLE_FILES
        }));
        return Collections.unmodifiableSet(varNames);
    }
    
    /** Expand all occurences of <code>${VARIABLE_NAME}</code> repeatetively.
     * It uses {@link #needFurtherExpansion} function to see 
     * when to stop scanning (via {@link #expandOnce}).
     * @param tab Hashtable holding (String)VARIABLE, (String)VALUE pairs
     * @param cmd Command with <code>${VAR}</code> sequences
     * @return String with all variables expanded
     *
    public static String expand_OLD(Hashtable tab, String cmd, boolean warnUndefVars) {
        D.deb ("expand ("+tab+","+cmd+")"); // NOI18N
        String cmd_cond = ""; // NOI18N
        //this.warnUndefVars = warnUndefVars;
        boolean expanded = false;
        do {
            while (true) {
                cmd_cond = expandConditional (tab, cmd, warnUndefVars);
                if (cmd.equals(cmd_cond)) break;
                cmd = cmd_cond;
            }
            expanded = true;
            //D.deb ("after expandConditional ("+tab+","+cmd+")"); // NOI18N
            while(needFurtherExpansion(cmd) == true) {
                //D.deb("needFurtherExpansion cmd='"+cmd+"'"); // NOI18N
                cmd = expandOnce(tab, cmd, warnUndefVars);
                expanded = false;
            }
        } while(!expanded);
        D.deb ("after expansion ("+tab+","+cmd+")"); // NOI18N
        cmd = org.openide.util.Utilities.replaceString(cmd, "\\${", "${");
        cmd = org.openide.util.Utilities.replaceString(cmd, "\\$[", "$[");
        return cmd;
        //return VcsUtilities.replaceBackslashDollars( cmd );
    }
     */

    /** Expand all occurences of <code>${<variable name>}</code> repeatetively.
     * Expand also conditionals like <code>$[? <variable name>] [<when var is non-empty>]
     * [<when var is empty>]<code>.
     * @param tab Hashtable holding (String)VARIABLE, (String)VALUE pairs
     * @param cmd Command with <code>${VAR}</code> sequences
     * @return String with all variables expanded
     */
    public static String expand(Hashtable tab, String cmd, boolean warnUndefVars) {
        StringBuffer result = new StringBuffer();
        int begin = 0;
        int end;
        do {
            end = cmd.indexOf("$", begin);
            if (end < 0) {
                result.append(cmd.substring(begin, cmd.length()));
                break;
            }
            boolean var = false;
            boolean cond = false;
            boolean escape = false;
            if (end > 0 && (escape = (cmd.charAt(end - 1) == '\\')) ||
                end == (cmd.length() - 1) ||
                (!(var = (cmd.charAt(end + 1) == '{')) &&
                (!(cond = (cmd.charAt(end + 1) == '['))))) {

                if (escape && (end < (cmd.length() - 1)) && ((cmd.charAt(end + 1) == '{') || (cmd.charAt(end + 1) == '['))) {
                    result.append(cmd.substring(begin, end - 1));
                    result.append('$');
                } else {
                    result.append(cmd.substring(begin, end + 1));
                }
                begin = end + 1;
            } else {
                result.append(cmd.substring(begin, end));
                begin = end + 2;
                if (var) {
                    end = VcsUtilities.getPairIndex(cmd, begin, '{', '}'); // cmd.indexOf("}",begin); // NOI18N
                    if (end < 0) {
                        ErrorManager.getDefault().notify(ErrorManager.WARNING,
                            new IOException("Missing closing bracket '}'. Corresponding opening bracket '{' is at pos = "+(begin - 1)));
                        result.append("${");
                    } else {
                        String expansion = expandVariable(tab, cmd.substring(begin, end), warnUndefVars);
                        if (expansion == null) {
                            if (warnUndefVars) {
                                ErrorManager.getDefault().notify(ErrorManager.WARNING,
                                    new IOException("Variable undefined '"+cmd.substring(begin, end)+
                                                    "'. Expanding it to an empty string.")); // NOI18N
                            }
                        } else {
                            result.append(expansion);
                        }
                        begin = end + 1;
                    }
                } else if (cond) {
                    int[] expandEnd = new int[1];
                    String expansion = expandConditional(tab, cmd.substring(begin - 2), warnUndefVars, expandEnd);
                    if (expandEnd[0] < 0) {
                        result.append("$[");
                    } else {
                        result.append(expansion);
                        begin += expandEnd[0] - 1;
                    }
                }
            }
        } while (begin < cmd.length());
        return result.toString();
    }
    
    public static String expandVariable(Hashtable tab, String name, boolean warnUndefVars) {
        String value = (String) tab.get(name);
        char replC1 = (char) -1;
        char replC2 = (char) -1;
        if (value == null) {
            int r = name.lastIndexOf(REPLACE);
            //D.deb("getReplaceVarValue('"+name+"'): r = "+r); // NOI18N
            if (r > 0 && name.length() == (r + 3)) {
                value = (String) tab.get(name.substring(0, r));
                //D.deb("getReplaceVarValue(): value of '"+name.substring(0, r)+"' = '"+value+"'"); // NOI18N
                //D.deb("length = "+name.length()); // NOI18N
                if (value != null) {
                    replC1 = name.charAt(r+1);
                    replC2 = name.charAt(r+2);
                    //D.deb("c1 = "+c1+", c2 = "+c2); // NOI18N
                    //value = value.replace(c1, c2);
                }
            }
        }
        if (value == null) {
            int substr;
            int begin = 0;
            while((substr = name.indexOf(SUBSTRACT, begin)) > 0) {
                String var = name.substring(begin, substr);
                if (var != null) var = var.trim();
                String svalue = expandVariable(tab, var, warnUndefVars);
                if (svalue == null) {
                    //if (warnUndefVars) E.deb("Variable undefined '"+var+"'."); // NOI18N
                    return null;
                }
                if (begin == 0) value = svalue;
                else value = substractVariables(value, svalue);//VcsFileSystem.substractRootDir(value, svalue);
                begin = substr + SUBSTRACT.length();
            }
            if (begin == 0) value = (String) tab.get(name);
            else {
                String svalue = expandVariable(tab, name.substring(begin).trim(), warnUndefVars);
                if (svalue == null) {
                    return null;
                }
                value = substractVariables(value, svalue); //VcsFileSystem.substractRootDir(value, svalue);
            }
        } else {
            value = expand(tab, value, warnUndefVars);
        }
        if (replC1 != ((char) -1)) {
            if (value != null) value = value.replace(replC1, replC2);
        }
        return value;
    }
    
    private static String substractVariables(String value1, String value2) {
        if (value2 == null || value2.length() == 0) return value1;
        int index = value1.lastIndexOf(value2);
        if (index < 0) return value1;
        else return value1.substring(0, index);
    }
    
    private static String expandConditional(Hashtable tab, String cmd, boolean warnUndefVars,
                                           int[] expandEnd) {
        expandEnd[0] = -1;
        if (!cmd.startsWith("$[?")) return null;
        int begin = 3;
        int end = VcsUtilities.getPairIndex(cmd, begin, '[', ']');
        if (end < 0) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING,
                new IOException("Missing closing bracket ']'."+
                                " Corresponding opening bracket '[' is at pos = "+(begin - 1)));
            return null;
        }
        String var = cmd.substring(begin, end).trim();
        String value = expandVariable(tab, var, warnUndefVars);
        if (value == null) {
            if (warnUndefVars) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING,
                    new IOException("Variable undefined '"+var+
                        "'. Expanding it to an empty string.")); // NOI18N
            }
        }

        // find first and second option and choose
        
        // first
        int firstBegin = cmd.indexOf("[", end + 1); // NOI18N
        //D.deb("firstBegin="+firstBegin); // NOI18N
        if (firstBegin < 0) {
            return null;
        }
        
        int firstEnd = VcsUtilities.getPairIndex(cmd, firstBegin + 1, '[', ']');
        if (firstEnd < 0) {
            return null;
        }
        //D.deb("firstEnd="+firstEnd); // NOI18N
        String first = cmd.substring(firstBegin + 1, firstEnd);
        //D.deb ("first="+first); // NOI18N
        
        //index = firstEnd;
        
        // second
        int secondBegin=cmd.indexOf("[", firstEnd); // NOI18N
        //D.deb("secondBegin="+secondBegin); // NOI18N
        if (secondBegin < 0) {
            return null;
        }
        //result.append(cmd.substring(index,secondBegin));
        
        int secondEnd = VcsUtilities.getPairIndex(cmd, secondBegin + 1, '[', ']');
        if (secondEnd < 0) {
            return null;
        }
        //D.deb("secondEnd="+secondEnd); // NOI18N
        String second = cmd.substring(secondBegin+1, secondEnd);
        //D.deb ("second="+second); // NOI18N
        expandEnd[0] = secondEnd;
        
        String result;
        if (value == null || value.length() == 0) {
            result = second;
        } else {
            result = first;
        }
        return expand(tab, result, warnUndefVars);
    }
    
    /** Expand all occurences of <code>${VARIABLE_NAME}</code>.
     * It makes only one expansion cycle, no variables inside other variables are expanded.
     * @param tab Hashtable holding (String)VARIABLE, (String)VALUE pairs
     * @param cmd Command with <code>${VAR}</code> sequences
     * @return String with variables expanded
     */
    public static String expandFast(Hashtable tab, String cmd, boolean warnUndefVars) {
        D.deb ("expandFast ("+cmd+")"); // NOI18N
        String cmd_cond = ""; // NOI18N
        //this.warnUndefVars = warnUndefVars;
        boolean expanded = false;
        cmd_cond = expandConditional (tab, cmd, warnUndefVars);
        cmd = expandOnce(tab, cmd_cond, warnUndefVars);
        D.deb ("after expansion ("+cmd+")"); // NOI18N
        return VcsUtilities.replaceBackslashDollars( cmd );
    }

    public static String expandConditional (Hashtable tab, String cmd, boolean warnUndefVars) {
        //D.deb ("expandConditional (..)"); // NOI18N
        int index = 0;
        int size = cmd.length();
        int begin = 0;
        int end = 0;
        int nextBegin = 0;
        StringBuffer result = new StringBuffer(size+20);
        while(true) {
            begin = cmd.indexOf("$[?", index); // NOI18N
            //D.deb("begin="+begin); // NOI18N
            if (begin < 0) {
                result.append(cmd.substring(index));
                break;
            }
            result.append(cmd.substring(index,begin));
            //D.deb ("pre="+result); // NOI18N

            int fake = cmd.indexOf("\\$[?", index); // NOI18N
            if (fake >= 0 && fake + 1 == begin) {
                index = begin + 1;
                result.append('$');
                continue;
            }
            //end=cmd.indexOf("]",begin); // NOI18N
            end = VcsUtilities.getPairIndex(cmd, begin + 3, '[', ']');
            if (end < 0) {
                index = begin + 1;
                continue;
            }
            //D.deb("end="+end); // NOI18N
            String var = cmd.substring(begin+3, end).trim();
            String value = getVarValue(tab, var); //(String)tab.get(var);
            //D.deb("var="+var+", value="+value); // NOI18N

            if (value == null) {
                if (warnUndefVars) {
                    //E.err("Variable undefined '"+var+"'. Expanding it to an empty string."); // NOI18N
                }
                if (var.indexOf("$[?") >= 0) { // NOI18N
                    E.err("Missing closing bracket ']' ?"); // NOI18N
                }
            }
            index = end + 1;

            // find first and second option and choose

            // first
            int firstBegin = cmd.indexOf("[", index); // NOI18N
            //D.deb("firstBegin="+firstBegin); // NOI18N
            if (firstBegin < 0) {
                result.append(cmd.substring(index));
                break;
            }
            //result.append(cmd.substring(index,firstBegin));

            fake = cmd.indexOf("\\[", index); // NOI18N
            if (fake >= 0 && fake + 1 == firstBegin) {
                index = firstBegin + 1;
                result.append('[');
                continue;
            }
            //int firstEnd=cmd.indexOf("]",firstBegin); // NOI18N
            int firstEnd = VcsUtilities.getPairIndex(cmd, firstBegin + 1, '[', ']');
            if (firstEnd < 0) {
                index = firstBegin+1;
                continue;
            }
            //D.deb("firstEnd="+firstEnd); // NOI18N
            String first = cmd.substring(firstBegin + 1, firstEnd);
            //D.deb ("first="+first); // NOI18N

            index = firstEnd;

            // second
            int secondBegin=cmd.indexOf("[", index); // NOI18N
            //D.deb("secondBegin="+secondBegin); // NOI18N
            if (secondBegin < 0) {
                result.append(cmd.substring(index));
                break;
            }
            //result.append(cmd.substring(index,secondBegin));

            fake = cmd.indexOf("\\[", index); // NOI18N
            if (fake >= 0 && fake + 1 == secondBegin) {
                index = secondBegin + 1;
                result.append('[');
                continue;
            }
            //int secondEnd=cmd.indexOf("]",secondBegin); // NOI18N
            int secondEnd = VcsUtilities.getPairIndex(cmd, secondBegin + 1, '[', ']');
            if (secondEnd < 0) {
                index = secondBegin + 1;
                continue;
            }
            //D.deb("secondEnd="+secondEnd); // NOI18N
            String second = cmd.substring(secondBegin+1, secondEnd);
            //D.deb ("second="+second); // NOI18N
            index = secondEnd + 1;

            if (value == null || value.equals ("")) { // NOI18N
                result.append (/*"["+*/second); // NOI18N
            } else {
                result.append (/*"["+*/first); // NOI18N
            }
        }
        return new String(result);
    }

    //-------------------------------------------
    public static boolean needFurtherExpansion(String cmd) {
        //D.deb ("needFurtherExpansion("+cmd+")"); // NOI18N
        int begin = cmd.indexOf("${"); // NOI18N
        int fake = cmd.indexOf("\\${"); // NOI18N
        if (begin < 0) {
            return false ;
        }
        if (fake >= 0 && fake + 1 == begin) {
            return needFurtherExpansion(cmd.substring(begin + 1));
        }
        return true ;
    }

    //-------------------------------------------
    /** Expand (once) ${VARIABLE} variables in command.
     * It scans 'cmd' string and replaces all occurences of ${VARIABLE}
     * to VALUE=tab.get(VARIABLE). Both VARIABLE and VALUE must be Strings.
     * <p>
     * Note that 'cmd' string is scanned only once.
     * Use {@link #needFurtherExpansion} function to see if it should 
     * be called again.
     * @param tab Hashtable holding (String)VARIABLE=(String)VALUE pairs
     * @param cmd Command in which ${VAR} sequences
     * @return String with all variables expanded
     */
    public static String expandOnce(Hashtable tab, String cmd, boolean warnUndefVars) {
        //D.deb ("expandOnce (..)"); // NOI18N
        int index=0;
        int size=cmd.length();
        int begin=0,end=0,nextBegin=0;
        StringBuffer result=new StringBuffer(size+20);

        while(true){
            begin=cmd.indexOf("${",index); // NOI18N
            //D.deb("begin="+begin); // NOI18N
            if( begin<0 ){
                result.append(cmd.substring(index));
                break;
            }
            result.append(cmd.substring(index,begin));

            int fake = cmd.indexOf("\\${",index); // NOI18N
            if(fake >= 0 && (fake + 1 == begin)) {
                index=begin + 1;
                result.append('$');
                continue;
            }
            end = VcsUtilities.getPairIndex(cmd, begin + 2, '{', '}'); // cmd.indexOf("}",begin); // NOI18N
            if( end<0 ){
                index=begin+1;
                continue;
            }
            //D.deb("end="+end); // NOI18N

            String var=cmd.substring(begin+2,end);
            String value=getVarValue(tab, var);
            //String value=(String)tab.get(var);
            //D.deb("var="+var+", value="+value); // NOI18N

            if( value != null ){
                result.append(value);
            } else {
                if (warnUndefVars) {
                    //E.err("Variable undefined '"+var+"'. Expanding it to an empty string."); // NOI18N
                }
                if( var.indexOf("${")>=0 ){ // NOI18N
                    //E.err("Missing closing bracket '}' ?"); // NOI18N
                    // cvs commit has enclosed variable ${FILE} => do not warn of this case
                }
            }
            index=end+1;
        }
        return new String(result);
    }

    /** Expand (once) ${VARIABLE} variables in command only if they are known.
     * It scans 'cmd' string and replaces all occurences of ${VARIABLE}
     * to VALUE=tab.get(VARIABLE). Both VARIABLE and VALUE must be Strings.
     * <p>
     * Note that 'cmd' string is scanned only once.
     * @param tab Hashtable holding (String)VARIABLE=(String)VALUE pairs
     * @param cmd Command in which ${VAR} sequences
     * @return String with all known variables expanded
     */
    public static String expandKnownOnly(Hashtable tab, String cmd) {
        //D.deb ("expandOnce (..)"); // NOI18N
        int index=0;
        int size=cmd.length();
        int begin=0,end=0,nextBegin=0;
        StringBuffer result=new StringBuffer(size+20);

        while(true){
            begin=cmd.indexOf("${",index); // NOI18N
            //D.deb("begin="+begin); // NOI18N
            if( begin<0 ){
                result.append(cmd.substring(index));
                break;
            }
            result.append(cmd.substring(index,begin));

            int fake=cmd.indexOf("\\${",index); // NOI18N
            if( fake<0 ){
                fake=-5;
            }
            if(fake+1==begin){
                index=begin+1;
                result.append('$');
                continue;
            }
            end = VcsUtilities.getPairIndex(cmd, begin + 2, '{', '}'); // cmd.indexOf("}",begin); // NOI18N
            if( end<0 ){
                index=begin+1;
                continue;
            }
            //D.deb("end="+end); // NOI18N

            String var=cmd.substring(begin+2,end);
            String value=getVarValue(tab, var);
            //String value=(String)tab.get(var);
            //D.deb("var="+var+", value="+value); // NOI18N

            if( value != null ){
                result.append(value);
            } else {
                result.append(cmd.substring(begin, end+1));
            }
            index=end+1;
        }
        return new String(result);
    }

    /**
     * Get the value of a variable. If the variable name is not known,
     * this method search for the last occurence of character '_' and if it
     * is followed by two more chracters it replaces the first by the second
     * in the value of that variable.
     * @param tab The table holding (String)VARIABLE=(String)VALUE pairs
     * @param name The variable name or expression to evaluate
     */
    private static String getReplaceVarValue(Hashtable tab, String name) {
        if (name == null) return null;
        String value = (String) tab.get(name);
        if (value == null) {
            int r = name.lastIndexOf(REPLACE);
            //D.deb("getReplaceVarValue('"+name+"'): r = "+r); // NOI18N
            if (r > 0) {
                value = (String) tab.get(name.substring(0, r));
                //D.deb("getReplaceVarValue(): value of '"+name.substring(0, r)+"' = '"+value+"'"); // NOI18N
                //D.deb("length = "+name.length()); // NOI18N
                if (value != null && name.length() >= r+3) {
                    char c1 = name.charAt(r+1);
                    char c2 = name.charAt(r+2);
                    //D.deb("c1 = "+c1+", c2 = "+c2); // NOI18N
                    value = value.replace(c1, c2);
                }
            }
        }
        return value;
    }

    /**
     * Get the value of a variable or an expression.
     * @param tab The table holding (String)VARIABLE=(String)VALUE pairs
     * @param name The variable name or expression to evaluate
     */
    private static String getVarValue(Hashtable tab, String name) {
        int substr;
        int begin = 0;
        String value = getReplaceVarValue(tab, name);
        if (value == null) {
            while((substr = name.indexOf(SUBSTRACT, begin)) > 0) {
                String var = name.substring(begin, substr);
                if (var != null) var = var.trim();
                String svalue = getReplaceVarValue(tab, var);
                if (svalue == null) {
                    //if (warnUndefVars) E.deb("Variable undefined '"+var+"'."); // NOI18N
                    return null;
                }
                if (begin == 0) value = svalue;
                else value = VcsFileSystem.substractRootDir(value, svalue);
                begin = substr + SUBSTRACT.length();
            }
            if (begin == 0) value = (String) tab.get(name);
            else value = VcsFileSystem.substractRootDir(value, getReplaceVarValue(tab, name.substring(begin).trim()));
        }
        return value;
    }

    /*
    //-------------------------------------------
    public static void main(String[] args){
        Hashtable vars=new Hashtable();
        vars.put("A","a"); // NOI18N
        vars.put("B","${A}b"); // NOI18N
        vars.put("BB","\\${A}b"); // NOI18N
        vars.put("C","${B}c"); // NOI18N
        vars.put("CC","\\${A}\\${B}c"); // NOI18N
        vars.put("DIR","src"); // NOI18N
        vars.put("STCMD","stcmd30"); // NOI18N


        Variables v=new Variables();
        System.out.println("vars="+vars); // NOI18N
        System.out.println("orig='"+args[0]+"'"); // NOI18N
        System.out.println("new ='"+v.expand(vars,args[0], true)+"'"); // NOI18N
    }
     */
}

