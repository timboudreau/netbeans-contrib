/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore;

import java.util.*;

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

    //private boolean warnUndefVars = true;

    private static final String SUBSTRACT = "-"; // NOI18N
    private static final String REPLACE = "_"; // NOI18N

    private Variables() {
    }
    
    /** Expand all occurences of <code>${VARIABLE_NAME}</code> repeatetively.
     * It uses {@link #needFurtherExpansion} function to see 
     * when to stop scanning (via {@link #expandOnce}).
     * @param tab Hashtable holding (String)VARIABLE, (String)VALUE pairs
     * @param cmd Command with <code>${VAR}</code> sequences
     * @return String with all variables expanded
     */
    public static String expand(Hashtable tab, String cmd, boolean warnUndefVars) {
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
        return VcsUtilities.replaceBackslashDollars( cmd );
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
            if (fake < 0) {
                // why 5 ? no one knows - magical number (<-1)
                fake = -5;
            }
            if(fake + 1 == begin) {
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
            if (fake < 0) {
                // why 5 ? no one knows - magical number (<-1)
                fake = -5;
            }
            if (fake + 1 == firstBegin) {
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
            if (fake < 0) {
                // why 5 ? no one knows - magical number (<-1)
                fake = -5;
            }
            if (fake + 1 == secondBegin) {
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
        if (fake < 0) {
            fake = -5;
        }

        //D.deb("begin="+begin+", fake="+fake); // NOI18N
        if(fake + 1 == begin) {
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
                begin += substr + SUBSTRACT.length();
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

/*
 * Log
 *  15   Jaga      1.13.1.0    3/8/00   Martin Entlicher A simple fast variable 
 *       expansion added.
 *  14   Gandalf   1.13        1/18/00  Martin Entlicher Warning of undefined 
 *       variables on demand.
 *  13   Gandalf   1.12        1/15/00  Ian Formanek    NOI18N
 *  12   Gandalf   1.11        1/6/00   Martin Entlicher 
 *  11   Gandalf   1.10        12/21/99 Martin Entlicher 
 *  10   Gandalf   1.9         12/8/99  Martin Entlicher Added deeper expansion 
 *       of commands.
 *  9    Gandalf   1.8         12/8/99  Martin Entlicher Added substract and 
 *       replace to variables.
 *  8    Gandalf   1.7         10/25/99 Pavel Buzek     
 *  7    Gandalf   1.6         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  6    Gandalf   1.5         10/13/99 Pavel Buzek     
 *  5    Gandalf   1.4         10/13/99 Martin Entlicher Recursive command 
 *       expansion added
 *  4    Gandalf   1.3         10/12/99 Martin Entlicher 
 *  3    Gandalf   1.2         10/12/99 Pavel Buzek     
 *  2    Gandalf   1.1         10/5/99  Pavel Buzek     VCS at least can be 
 *       mounted
 *  1    Gandalf   1.0         9/30/99  Pavel Buzek     
 * $ 
 */
