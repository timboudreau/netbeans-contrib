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

package org.netbeans.modules.vcscore.util;

import java.awt.*;
import java.beans.*;
import java.io.*;
import java.text.*;
import java.util.*;

import org.openide.execution.NbClassLoader;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.io.NbObjectInputStream;
import org.openide.util.io.NbObjectOutputStream;

import org.netbeans.modules.vcscore.VcsAttributes;

/** Miscelaneous stuff.
 * 
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public class VcsUtilities {
    private Debug E=new Debug("VcsUtilities", false); // NOI18N
    private Debug D=E;
    
    private static final String GET_BUNDLE = "getBundle(";

    private static final String SYSTEM_ENV_PREFIX = "Env-";

    private static HashMap systemEnvVariables = null;
    private static final Object systemEnvVariablesLock = new Object();
    
    private static ClassLoader sfsClassLoader = null;
    private static boolean sfsCLReset = false;


    //-------------------------------------------
    public static int max3(int v1, int v2, int v3){
        return Math.max(Math.max(v1,v2),v3);
    }

    //-------------------------------------------
    public static int max7(int v1, int v2, int v3, int v4, int v5, int v6, int v7){
        return max3( max3(v1,v2,v3), max3(v4,v5,v6), v7);
    }

    //-------------------------------------------
    public static boolean withinRange(int min, int val, int max){
        return ((min<=val) && (val<=max)) ? true : false ;
    }

    /**
     * Get the pair index of a given character.
     * <p> i.e. getPairIndex("(a-(b+c)+d)", 1, '(', ')') gives 10 -- the position of the last ')' <\p>
     * @param str the String to search
     * @param from the initial position
     * @param p1 the pair character
     * @param p2 the character to search
     * @return the pair position of p2 in the string with respect to occurences of p1
     */
    public static int getPairIndex(String str, int from, char p1, char p2) {
        int len = str.length();
        int cp = 1;
        int i = from;
        for(; i < len; i++) {
            if (str.charAt(i) == p1) cp++;
            if (str.charAt(i) == p2) cp--;
            if (cp == 0) break;
        }
        if (i < len) return i;
        else return -1;
    }
    
    /**
     * Returns the number of characters in the specified string.
     * @param str the string
     * @param c the character
     */
    public static int charCount(String str, char c) {
        int len = str.length();
        int count = 0;
        for (int i = 0; i < len; i++) {
            if (str.charAt(i) == c) count++;
        }
        return count;
    }

    //-------------------------------------------
    public static String arrayToString(String []sa){
        if(sa==null){
            return ""; // NOI18N
        }
        StringBuffer sb=new StringBuffer();
        sb.append("["); // NOI18N
        for(int i=0;i<sa.length;i++){
            if(sa[i] != null) {
                sb.append(sa[i]);
            }
            if(i<sa.length-1){
                sb.append(","); // NOI18N
            }
        }
        sb.append("]"); // NOI18N
        return sb.toString();
    }

    //-----------------------------------
    public static String array2string(String[] sa){
        StringBuffer sb=new StringBuffer(255);
        if (sa != null)
            for(int i=0;i<sa.length;i++){
                sb.append(sa[i]+" "); // NOI18N
            }
        return sb.toString();
    }
    
    /**
     * Converts the array of strings into a string containing the elements separated by new line.
     */
    public static String array2stringNl(String[] sa) {
        StringBuffer sb = new StringBuffer();
        if (sa != null) {
            for (int i = 0; i < sa.length; i++) {
                sb.append(sa[i] + "\n");
                //if (i < sa.length - 1) sb.append("\n");
            }
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    //-------------------------------------------
    public static String arrayToSpaceSeparatedString(String []sa){
        if(sa==null){
            return ""; // NOI18N
        }
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<sa.length;i++){
            if(sa[i]!=null) {
                sb.append(sa[i]);
            }
            if(i<sa.length-1){
                sb.append(" "); // NOI18N
            }
        }
        return new String(sb);
    }

    /*
    public static String arrayToQuotedStrings(String []sa) {
        if (sa == null) {
            return ""; // NOI18N
        }
        StringBuffer sb = new StringBuffer();
        for(int i=0; i < sa.length; i++) {
            if (sa[i] == null) sb.append(""); // NOI18N
            else sb.append("\"" + sa[i] + "\""); // NOI18N
            if (i < sa.length-1) {
                sb.append(" "); // NOI18N
            }
        }
        return new String(sb);
    }
     */

    //MK-------------------------------------------
    public static String arrayToQuotedString(String []sa, boolean unixShell){
        if(sa==null){
            return ""; // NOI18N
        }
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<sa.length;i++){
            if(sa[i]==null) sb.append(""); // NOI18N
            else sb.append(VcsUtilities.msg2CmdlineStr(sa[i], unixShell));
  //MK Test     else sb.append(VcsUtilities.msg2CmdlineStr("\"" + sa[i] + "\"", unixShell));
            if(i<sa.length-1){
                sb.append(" "); // NOI18N
            }
        }
        return new String(sb);
    }
    
    /**
     * Get the comma-separated quoted strings from an array of strings.
     */
    public static String arrayToQuotedStrings(String[] sa) {
        if (sa == null) {
            return ""; // NOI18N
        }
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < sa.length; i++) {
            if (sa[i] == null) sb.append(""); // NOI18N
            else sb.append("\""+sa[i]+"\"");
            if (i < sa.length - 1) {
                sb.append(", "); // NOI18N
            }
        }
        return sb.toString();
    }
    
    //-------------------------------------------
    public static String replaceBackslashDollars(String s){
        int len=s.length();
        StringBuffer sb=new StringBuffer(len);
        for(int i=0;i<len;i++){
            char c=s.charAt(i);
            if( (c=='\\') && (i<len-1) && ('$'==s.charAt(i+1)) ){
                continue;
            }
            sb.append(c);
        }
        return new String(sb);
    }

    //-------------------------------------------
    public static String[] mergeArrays(String[] sa1, String[] sa2){
        if(sa1==null){
            sa1=new String[0];
        }
        if(sa2==null){
            sa2=new String[0];
        }
        int sa1Len=sa1.length;
        int sa2Len=sa2.length;
        Hashtable tab=new Hashtable(sa1Len+sa2Len);
        for(int i=0;i<sa1Len;i++){
            tab.put(sa1[i],sa1[i]);
        }
        for(int i=0;i<sa2Len;i++){
            tab.put(sa2[i],sa2[i]);
        }

        int len=tab.size();
        String[] res=new String[len];
        int i=0;
        for (Enumeration e = tab.keys() ; e.hasMoreElements() ;) {
            String s=(String)e.nextElement();
            res[i++]=s;
        }
        return res;
    }


    //-------------------------------------------
    public static String toSpaceSeparatedString(Vector v){
        StringBuffer sb=new StringBuffer(30);
        for(int i=0;i<v.size();i++){
            Object o=v.elementAt(i);
            sb.append(" "+o); // NOI18N
        }
        return new String(sb);
    }
    
    /**
     * Get the quoted string.
     * @return the string inside of quotation marks or null when no string found.
     */
    private static String getQuotedString(String str, int[] pos) {
        while(pos[0] < str.length() && Character.isWhitespace(str.charAt(pos[0]))) pos[0]++;
        if (pos[0] >= str.length()) return null;
        StringBuffer result = new StringBuffer();
        if (str.charAt(pos[0]) == '"') { // getting quoted string
            pos[0]++;
            while(pos[0] < str.length()) {
                if (str.charAt(pos[0]) != '"') result.append(str.charAt(pos[0]));
                else if (str.charAt(pos[0] - 1) == '\\') result.setCharAt(result.length() - 1, '"'); // replace '\\' with '"' => \" becomes "
                else break;
                pos[0]++;
            }
        } else { // getting not-quoted string
            while(pos[0] < str.length() && str.charAt(pos[0]) != ',') {
                result.append(str.charAt(pos[0]));
                pos[0]++;
            }
        }
        return result.toString();
    }

    /**
     * Converts a String of quoted values delimited by commas to an array of String values.
     * If the values are not quoted, only commas works as delimeters.
     */
    public static String[] getQuotedStrings(String str) {
        LinkedList list = new LinkedList();
        int[] index = new int[] { 0 };
        String element = VcsUtilities.getQuotedString(str, index);
        while(element != null) {
            list.add(element);
            while(index[0] < str.length() && str.charAt(index[0]) != ',') index[0]++;
            index[0]++;
            element = VcsUtilities.getQuotedString(str, index);
        }
        //String element = str.substring(index, end);
        return (String[]) list.toArray(new String[0]);
    }
    
    /**
     * Get the quoted string.
     * @return the string inside of quotation marks or null when no string found.
     */
    private static String getQuotedArgument(String str, int[] pos) {
        while(pos[0] < str.length() && Character.isWhitespace(str.charAt(pos[0]))) pos[0]++;
        if (pos[0] >= str.length()) return null;
        StringBuffer result = new StringBuffer();
        if (str.charAt(pos[0]) == '"') { // getting quoted string
            pos[0]++;
            while(pos[0] < str.length()) {
                if (str.charAt(pos[0]) != '"') result.append(str.charAt(pos[0]));
                else if (str.charAt(pos[0] - 1) == '\\') result.setCharAt(result.length() - 1, '"'); // replace '\\' with '"' => \" becomes "
                else break;
                pos[0]++;
            }
        } else { // getting not-quoted string
            while(pos[0] < str.length() && !Character.isWhitespace(str.charAt(pos[0]))) {
                result.append(str.charAt(pos[0]));
                pos[0]++;
            }
        }
        return result.toString();
    }
    
    /**
     * Converts a String of quoted values delimited by spaces or any other white
     * characters to an array of String values.
     * If the values are not quoted, only white characters works as delimeters.
     */
    public static String[] getQuotedArguments(String str) {
        LinkedList list = new LinkedList();
        int[] index = new int[] { 0 };
        String element = VcsUtilities.getQuotedArgument(str, index);
        while(element != null) {
            list.add(element);
            //while(index[0] < str.length() && Character.isWhitespace(str.charAt(index[0]))) index[0]++;
            index[0]++;
            element = VcsUtilities.getQuotedArgument(str, index);
        }
        //String element = str.substring(index, end);
        return (String[]) list.toArray(new String[0]);
    }
    
    /**
     * Find out, whether some string from the field of quoted strings is contained in a set of strings.
     * @param quotedStr the field of quoted strings, can be <code>null</code>
     * @param set the set of strings
     * @return true if there is a match, false otherwise
     */
    public static boolean matchQuotedStringToSet(String quotedStr, Set set) {
        if (quotedStr != null && set != null) {
            String[] strs = getQuotedStrings(quotedStr);
            for (int i = 0; i < strs.length; i++) {
                if (set.contains(strs[i])) return true;
            }
        }
        return false;
    }

    /**
     * Find out, whether all string from the field of quoted strings are contained in a set of strings.
     * @param quotedStr the field of quoted strings, can be <code>null</code>
     * @param set the set of strings
     * @return true if yes or the quoted string is null, false if there is
     *         a string, that is not contained in the set, or the set is null.
     */
    public static boolean areQuotedStringsContainedInSet(String quotedStr, Set set) {
        if (quotedStr != null && set != null) {
            String[] strs = getQuotedStrings(quotedStr);
	    for (int i = 0; i < strs.length; i++) {
                if (!set.contains(strs[i])) return false;
            }
        } else {
	    if (quotedStr != null) return false;
	    if (set != null) return true;
	}
	return true;
    }

    /**
     * Find out, whether all members of a set of strings are contained in the field of quoted strings.
     * @param quotedStr the field of quoted strings, can be <code>null</code>
     * @param set the set of strings, can be <code>null</code>
     * @return true if yes or the quoted string is null, false if there is
     *         a string, that is not contained in the set, or the set is null.
     */
    public static boolean isSetContainedInQuotedStrings(String quotedStr, Set set) {
	if (quotedStr != null && set != null) {
	    HashSet qsSet = new HashSet(Arrays.asList(getQuotedStrings(quotedStr)));
	    for (Iterator it = set.iterator(); it.hasNext(); ) {
		if (!qsSet.contains(it.next())) return false;
            }
        } else if (quotedStr != null) return true;
	else if (set != null) return false;
	return true;
    }
    
    /**
     * Get the class loader, that loads classes from SystemFileSystem.
     */
    public static synchronized ClassLoader getSFSClassLoader() {
        if (sfsClassLoader == null) {
            sfsCLReset = false;
            FileSystem sfs = Repository.getDefault().getDefaultFileSystem();
            sfsClassLoader = new NbClassLoader(new FileSystem[] { sfs });
            sfs.addFileChangeListener(new FileChangeListener () {
                public void fileFolderCreated(FileEvent fev) {}
                public void fileDataCreated(FileEvent fev) { sfsCLReset = true; }
                public void fileChanged(FileEvent fev) { sfsCLReset = true; }
                public void fileDeleted(FileEvent fev) { sfsCLReset = true; }
                public void fileRenamed(FileRenameEvent frev) { sfsCLReset = true; }
                public void fileAttributeChanged(FileAttributeEvent faev) {}
            });
        }
        if (sfsCLReset) {
            sfsClassLoader = new NbClassLoader(new FileSystem[] { Repository.getDefault().getDefaultFileSystem() });
            sfsCLReset = false;
        }
        return sfsClassLoader;
    }

    /** Get a string from a resource bundle. An arbitrary resource bundle can be
     * specified to get an arbitrary key from.
     * This method resolves all occurrences of
     * "getBundle(<class name or path to the resource bundle>).getString(<key>[, <format param>, ...])"
     * to the value of the key obtained from the resource bundle formatted with the optional parameters.
     * @param str the string
     * @return the resolved string.
     */
    public static String getBundleString(String str) {
        for (int index = str.indexOf(GET_BUNDLE); index >= 0; index = str.indexOf(GET_BUNDLE, index)) {
            index += GET_BUNDLE.length();
            int end = VcsUtilities.getPairIndex(str, index, '(', ')');
            if (end < 0) {
                //System.out.println("BAD key: "+str+" -- end bundle paranthesis missing");
                continue;
            }
            String bundle = str.substring(index, end);
            //String key = str.substring(end);
            int startArg = str.indexOf('(', end);
            if (startArg < 0) {
                //System.out.println("BAD key: "+str+" -- start key paranthesis missing");
                continue;
            }
            startArg++;
            int endArg = VcsUtilities.getPairIndex(str, startArg, '(', ')');
            if (endArg < 0) {
                //System.out.println("BAD key: "+str+" -- end key paranthesis missing");
                continue;
            }
            String key = str.substring(startArg, endArg);
            String replaced = getBundleString(bundle, key);
            str = str.substring(0, index - GET_BUNDLE.length()) + replaced + str.substring(endArg + 1);
            index = index - GET_BUNDLE.length() + replaced.length();
        }
        return str;
    }
    
    private static String getBundleString(String bundle, String key) {
        Class clazz = null;
        if (bundle.endsWith(".class")) {
            try {
            String className = bundle.substring(0, bundle.length() - ".class".length());
            //if (className.endsWith(".class")) className = className.substring(0, className.length() - ".class".length());
            clazz = Class.forName(className, false, org.openide.TopManager.getDefault().systemClassLoader());
            } catch (ClassNotFoundException exc) {
                clazz = null;
                //exc.printStackTrace();
            }
        }
        String[] keyWithArgs = VcsUtilities.getQuotedStrings(key);
        String[] args = null;
        if (keyWithArgs.length > 0) {
            key = keyWithArgs[0];
            args = new String[keyWithArgs.length - 1];
            System.arraycopy(keyWithArgs, 1, args, 0, keyWithArgs.length - 1);
        }
        //System.out.println("clazz = "+clazz);
        String bundleStr = key;
        try {
            if (clazz != null) {
                bundleStr = org.openide.util.NbBundle.getBundle(clazz).getString(key);
            } else {
                bundleStr = org.openide.util.NbBundle.getBundle(bundle, java.util.Locale.getDefault(), getSFSClassLoader()).getString(key);
                //bundleStr = org.openide.util.NbBundle.getBundle(bundle, java.util.Locale.getDefault(), getBundleClassLoader()).getString(key);
            }
            if (args != null) {
                bundleStr = java.text.MessageFormat.format(bundleStr, args);
            }
        } catch (final MissingResourceException missExc) {
            org.openide.TopManager.getDefault().notifyException(new Exception() {
                public String getLocalizedMessage() {
                    return "MissingResourceException:" + missExc.getMessage();
                }
            });
        }
        return bundleStr;
    }
    
    /** @return default class loader which is used, when we don't have
     * any other class loader. (in function getBundle(String), getLocalizedFile(String),
     * and so on...
     *
    private static ClassLoader getBundleClassLoader() {
        ClassLoader c = (ClassLoader) org.openide.util.Lookup.getDefault ().lookup (ClassLoader.class);
        return c != null ? c : ClassLoader.getSystemClassLoader ();
    }
     */


    //-------------------------------------------
    public static String getDirNamePart(String path){
        String dirName=""; // NOI18N
        int sep=path.lastIndexOf('/');
        dirName=(sep<0 ? "" : path.substring(0,sep)); // NOI18N
        return dirName;
    }


    //-------------------------------------------
    public static String getFileNamePart(String path){
        String fileName=""; // NOI18N
        int sep=path.lastIndexOf('/');
        fileName=(sep<0 ? path : path.substring(sep+1));
        return fileName;
    }


    //-------------------------------------------
    public static void centerWindow (Window w) {
        w.setBounds(org.openide.util.Utilities.findCenterBounds(w.getSize()));
    }

    public static boolean deleteRecursive (File dir) {
        boolean result = true;
        File files[] = dir.listFiles ();
        if (files != null) {
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory ()) {
                    result = result && deleteRecursive (files[i]);
                } else {
                    result = result && files[i].delete ();
                }
            }
        }
        result = result && dir.delete ();
        return result;
    }

    public static void removeEnterFromKeymap(javax.swing.JTextField field) {
        javax.swing.KeyStroke enter = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0);
        javax.swing.text.Keymap map = field.getKeymap ();
        map.removeKeyStrokeBinding (enter);
    }

    /**
     * Count the number of occurences of a character in the specified String.
     * @param str the String to count the character in
     * @param c the character to count
     */
    public static int numChars(String str, char c) {
        int n = 0;
        int index = 0;
        while(index < str.length()) {
            index = str.indexOf(c, index + 1);
            if (index < 0) break;
            n++;
        }
        return n;
    }

    /**
     * Transform message to the form that can be used on a command line.
     */
    public static String msg2CmdlineStr(String msg, boolean unixShell) {
        if (msg == null) return "";
        String cmd = org.openide.util.Utilities.replaceString(msg, "\\", "\\\\"); // put \\ instead of \
        if (unixShell) cmd = org.openide.util.Utilities.replaceString(cmd, "\"", "\\\\\\\"\\\\\\\""); // put \\\" instead of "
        else cmd = org.openide.util.Utilities.replaceString(cmd, "\"", "\\\\\\\""); // put \\\" instead of "
        if (org.openide.util.Utilities.isUnix() || unixShell) {
            cmd = org.openide.util.Utilities.replaceString(cmd, "$", "\\$"); // put \$ instead of $
            //cmd = org.openide.util.Utilities.replaceString(cmd, "!", "\\!"); // put \! instead of !
            cmd = org.openide.util.Utilities.replaceString(cmd, "`", "\\`"); // put \` instead of `
        }
        return cmd;
    }
    
    /**
     * Removes all keys from the first Hashtable which are defined in the second one.
     */
    public static void removeKeys(HashMap table, HashMap toRemove) {
        for(Iterator keysIter = toRemove.keySet().iterator(); keysIter.hasNext(); ) {
            table.remove(keysIter.next());
        }
    }
    
    /**
     * Get just the system environment variables.
     */
    public static Map getSystemEnvVars() {
        /*
        if (systemEnvVariables == null) {
            synchronized (systemEnvVariablesLock) {
                if (systemEnvVariables == null) {
         */
                    systemEnvVariables = new HashMap();
                    for (Enumeration enum = System.getProperties().propertyNames(); enum.hasMoreElements(); ) {
                        String key = (String) enum.nextElement();
                        if (key.startsWith(SYSTEM_ENV_PREFIX)) {
                            String value = (String) System.getProperty(key);
                            if (value != null) {
                                systemEnvVariables.put(key.substring(SYSTEM_ENV_PREFIX.length()), value);
                            }
                        }
                    }
                    /*
                }
            }
        }
                     */
        return systemEnvVariables;
    }
    
    /**
     * Add environment variables from the variables table.
     * @param envVars the map of environment variables. The additional variables will be
     * added to it.
     * @param vars the table of variables from which the additional environment variables
     *        will be added to <code>envVars</code>. Only variables with a given prefix
     *        will be added.
     * @param varEnvPrefix the prefix of variables, which are considered as environmental
     * @return the map of all environment variables
     */
    public static Map addEnvVars(Map envVars, Hashtable vars, String varEnvPrefix) {
        for (Enumeration enum = vars.keys(); enum.hasMoreElements(); ) {
            String key = (String) enum.nextElement();
            if (key.startsWith(varEnvPrefix)) {
                String value = (String) vars.get(key);
                if (value != null) {
                    envVars.put(key.substring(varEnvPrefix.length()), value);
                }
            }
        }
        return envVars;
    }
    
    /**
     * Add environment variables from the variables table and remove any variables denoted
     * with a remove prefix from the environment.
     * @param envVars the map of environment variables. The additional variables will be
     * added to it.
     * @param vars the table of variables from which the additional environment variables
     *        will be added to <code>envVars</code>. Only variables with a given prefix
     *        will be added.
     * @param varEnvPrefix the prefix of variables, which are considered as environmental
     * @param varEnvRemovePrefix the prefix of variables, which will be removed from the
              environment.
     * @return the map of all environment variables
     */
    public static Map addEnvVars(Map envVars, Hashtable vars, String varEnvPrefix,
                                 String varEnvRemovePrefix) {
        for (Enumeration enum = vars.keys(); enum.hasMoreElements(); ) {
            String key = (String) enum.nextElement();
            if (key.startsWith(varEnvRemovePrefix)) {
                envVars.remove(key.substring(varEnvRemovePrefix.length()));
            }
        }
        for (Enumeration enum = vars.keys(); enum.hasMoreElements(); ) {
            String key = (String) enum.nextElement();
            if (key.startsWith(varEnvPrefix)) {
                String value = (String) vars.get(key);
                if (value != null) {
                    envVars.put(key.substring(varEnvPrefix.length()), value);
                }
            }
        }
        return envVars;
    }
    
    public static String[] getEnvString(Map envVars) {
        String[] vars = new String[envVars.size()];
        int i = 0;
        for (Iterator it = envVars.entrySet().iterator(); it.hasNext(); i++) {
            Map.Entry entry = (Map.Entry) it.next();
            vars[i] = entry.getKey()+"="+entry.getValue();
        }
        return vars;
    }
    
    /**
     * Creates a temporary directory.
     */
    public static File createTMP() {
        String TMP_ROOT=System.getProperty("netbeans.user")+File.separator+
                 "system"+File.separator+"vcs"+File.separator+"tmp";
        File tmpDir = new File(TMP_ROOT);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        long tmpId;
        do {
            tmpId = 10000 * (1 + Math.round (Math.random () * 8)) + Math.round (Math.random () * 1000);
        } while (new File(TMP_ROOT+File.separator+"tmp"+tmpId).exists()); // NOI18N
        TMP_ROOT = TMP_ROOT+File.separator+"tmp"+tmpId; // NOI18N
        tmpDir = new File(TMP_ROOT);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        return tmpDir;
    }

    /**
     * Create a unique name, that does not occure in the collections of names.
     * @param name the base name
     * @param names the collection of names
     * @return the name if it's not contained in names, name followed by an underscore and a number otherwise
     */
    public static String createUniqueName(String name, Collection names) {
        String unique = name;
        boolean isUnique = false; // do not believe, that it's unique
        int i = 1;
        while (!isUnique) {
            if (names.contains(unique)) {
                unique = name + "_" + i;
                i++;
            } else {
                break;
            }
        }
        return unique;
    }
    
    public static ArrayList createIgnoreList(final org.netbeans.modules.vcscore.cache.CacheDir dir,
                                             final String path,
                                             final org.netbeans.modules.vcscore.VcsFileSystem.IgnoreListSupport ignSupport) {
        org.netbeans.modules.vcscore.cache.CacheDir parent = dir.getParent();
        //System.out.println("createIgnoreList("+dir.getAbsolutePath()+", "+path+"), parent = "+((parent == null) ? "null" : parent.getAbsolutePath()));
        ArrayList ignoreList = null;
        if (parent == null)
            ignoreList = ignSupport.createIgnoreList(path, ignSupport.createInitialIgnoreList());
        else {
            //CacheDir pd = cache.getDir(parent.getPackageNameExt('/','.'));
            ArrayList parentIgnoreList;
            //System.out.println("  parent has ignore list set = "+parent.isIgnoreListSet());
            if (!parent.isIgnoreListSet()) {
                parentIgnoreList = createIgnoreList(parent, VcsUtilities.getDirNamePart(path), ignSupport);
            } else {
                parentIgnoreList = parent.getIgnoreList();
            }
            ignoreList = ignSupport.createIgnoreList(path, parentIgnoreList);
        }
        dir.setIgnoreList(ignoreList);
        return ignoreList;
    }
    
    /**
     * Performs the conversion from the Fileobjects retrieved from nodes to the real
     * underlying versioning filesystem's fileobjects. Should be used in the action's code whenever 
     * the action needs to work with the fileobjects of the versioning fs.
     * That is nessesary when the nodes come from  the MultiFilesystem layer,
     * otherwise we'll get the wrong set of fileobjects and commands will behave strangely.
     */
    public static FileObject[] convertFileObjects(FileObject[] originals) {
        if (originals == null || originals.length == 0) {
            return originals;
        }
        FileObject[] toReturn = new FileObject[originals.length];
        for (int i = 0; i < originals.length; i++) {
            toReturn[i] = originals[i];
            FileObject fo = originals[i];
            FileSystem fs = (FileSystem) fo.getAttribute(VcsAttributes.VCS_NATIVE_FS);
            if (fs != null) {
                try {
                    FileSystem fileSys = fo.getFileSystem();
                    if (!fileSys.equals(fs)) {
                        String nativePath = (String) fo.getAttribute(VcsAttributes.VCS_NATIVE_PACKAGE_NAME_EXT);
                        fo = fs.findResource(nativePath);
                        if (fo != null) {
                            toReturn[i] = fo;
                        }
                    }
                } catch (FileStateInvalidException exc) {
                    continue;
                }
                
            } else {
                continue;
            }
        }
        return toReturn;
    }
    
    /**
     * Encodes Object into String encoded in HEX format
     * @param value Object, which will be encoded
     * @return  serialized Object in String encoded in HEX format
     * @throws IOException
     */                
    public static String encodeValue(Object value) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new NbObjectOutputStream(bos);
            oos.writeObject(value);
            oos.close();
        } catch (SecurityException se) {
            throw (IOException) org.openide.TopManager.getDefault().getErrorManager().annotate(new IOException (), se);
        }
        byte bArray[] = bos.toByteArray();
        StringBuffer strBuff = new StringBuffer(bArray.length*2);
        for(int i = 0; i < bArray.length;i++) {
            if (bArray[i] < 16 && bArray[i] >= 0) strBuff.append("0");// NOI18N
            strBuff.append(Integer.toHexString((bArray[i] < 0) ? (bArray[i]+256) : bArray[i]));
        }
        return strBuff.toString();
    }

    /**
     * Creates serialized object, which was encoded in HEX format
     * @param value Encoded serialized object in HEX format
     * @return Created object from encoded HEX format
     * @throws IOException
     */            
    public static Object decodeValue(String value) throws IOException {
        if ((value == null) || (value.length() == 0)) return null;

        byte[] bytes = new byte[value.length()/2];
        int tempI;
        int count = 0;
        for (int i = 0; i < value.length(); i += 2) {
            try {
                tempI = Integer.parseInt(value.substring(i,i+2),16);
                if (tempI > 127) tempI -=256;
                bytes[count++] = (byte) tempI;
            } catch (NumberFormatException ne) {
                throw (IOException) org.openide.TopManager.getDefault().getErrorManager().annotate(new IOException (), ne);
            }
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes, 0, count);
        try {
            ObjectInputStream ois = new NbObjectInputStream(bis);
            Object ret = ois.readObject();
            return ret;
        } catch (OptionalDataException ode) {
            throw (IOException) org.openide.TopManager.getDefault().getErrorManager().annotate(new IOException (), ode);
        } catch (ClassNotFoundException cnfe) {
            throw (IOException) org.openide.TopManager.getDefault().getErrorManager().annotate(new IOException (), cnfe);
        }
    }

}
