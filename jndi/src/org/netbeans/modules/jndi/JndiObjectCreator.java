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

package org.netbeans.modules.jndi;

import java.util.Hashtable;
import java.util.Enumeration;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;

/** This class is generator for code that allows accessing of the object
 *  in the Jndi Tree
 *
 *  @author Ales Novak, Tomas Zezula 
 */
final class JndiObjectCreator {
    
    /** Table of hexadecimal digits */
    private static final char[] hexDigitTable = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    /** This method corrects string that contains \ to \\
     *   and also handles special chars and nonvisible chars
     *   escaping
     */
    static String correctValue(String str, boolean inString) {
        StringBuffer sb = new StringBuffer(str);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\\') {
                sb.insert(i, '\\');
                i++;
            }
            else if (sb.charAt(i) == '\'' && inString){
                sb.insert(i,'\\');
                i++;
            }
            else if (sb.charAt(i) =='\"' && inString){
                sb.insert(i,'\\');
                i++;
            }
            else if (sb.charAt(i) =='\t') {
                sb.setCharAt (i,'t');
                sb.insert (i,'\\');
                i++;
            }
            else if (sb.charAt(i) =='\n') {
                sb.setCharAt (i,'n');
                sb.insert (i,'\\');
                i++;
            }
            else if (sb.charAt(i) =='\b') {
                sb.setCharAt (i,'b');
                sb.insert (i,'\\');
                i++;
            }
            else if (sb.charAt(i) =='\r') {
                sb.setCharAt (i,'r');
                sb.insert (i,'\\');
                i++;
            }
            else if (sb.charAt(i) =='\f') {
                sb.setCharAt (i,'f');
                sb.insert (i,'\\');
                i++;
            }
            else if (sb.charAt(i)<0x20 || sb.charAt(i)>0x127) {
                String hexStr = hexStrVal (sb.charAt(i));
                sb.deleteCharAt(i);
                sb.insert (i, hexStr);
                i+=hexStr.length()-1;
            }
        }
        return sb.toString();
    }
    

    /** Returns Java source code for accessing object
     *  @param ctx InitialContext
     *  @param offset offset of object with respect to ctx
     *  @patam className name of class
     *  @return String generated java source code
     *  @exception NamingException on Jndi Error
     */
    static String getLookupCode(Context ctx, String offset, String className) throws NamingException {
        String code = generateProperties(ctx);
        String root = (String) ctx.getEnvironment().get(JndiRootNode.NB_ROOT);
        code = code + generateObjectReference(offset, root, className);
        code = code + generateTail();
        return code;
    }

    /** Creates binding code
     *  @param Context root context
     *  @param ComposteName offset offset of context in which the object should be bind
     *  @param String className name of class for narrowing
     *  @return String generated code
     *  @exception NamingException
     */
    public static String generateBindingCode (Context ctx, String offset, String className) throws NamingException {
        String code = generateProperties(ctx);
        String root = (String) ctx.getEnvironment().get(JndiRootNode.NB_ROOT);
        code = code + generateObjectReference(offset, root, className);
        code+= "    jndiObject.bind(\"<Name>\",<Object>);\n";  // No I18N
        code = code + generateTail();
        return code;
    }
    
    
    public static String stringifyCompositeName (Name name) {
	java.util.Enumeration enum = name.getAll ();
	String strName = null;
	while (enum.hasMoreElements()) {
            if (strName == null)
                strName = enum.nextElement().toString();
            else
                strName = strName+"/"+enum.nextElement().toString(); // No I18N    
        }
        if (strName == null)
            strName = "";	// No I18N
	return strName;
    }


    /** Creates an code for setting environment
     *  @param Context root context
     *  @return String code
     *  @exception NamingException
     */
    private static String generateProperties (Context ctx) throws NamingException{

        Hashtable env = ctx.getEnvironment();
        if (env == null) {
            return null;
        }
        String code = "/** Inserted by Jndi module */\n";  // No I18N
        code = code + "java.util.Properties jndiProperties = new java.util.Properties();\n";  // No I18N
        Enumeration keys = env.keys();
        Enumeration values = env.elements();
        while (keys.hasMoreElements()) {
			Object key = keys.nextElement ();
			Object val = values.nextElement();
			if (!(key instanceof String) || !(val instanceof String))
				continue;
            String name = correctValue((String)key,true);
            String value= correctValue((String)val,true);
            if (name.equals(JndiRootNode.NB_ROOT) ||
                    name.equals(JndiRootNode.NB_LABEL)) {
                continue;
            }
            code = code + "jndiProperties.put(\"" + name + "\",\"" + value + "\");\n";  // No I18N
        }
        return code;
    }

    /** Creates code for getting instance of object
     *  @param String offset of object
     *  @param String className, name of class
     *  @param String root, the root
     *  @return String code
     */
    private static String generateObjectReference(String offset, String root, String className) {
        String code = new String();
        code = code + "try {\n    javax.naming.directory.DirContext jndiCtx = new javax.naming.directory.InitialDirContext(jndiProperties);\n"; // No I18N
        if (root != null && root.length() > 0){
            code = code + "    javax.naming.Context jndiRootCtx = (javax.naming.Context) jndiCtx.lookup(\""+correctValue(root,true)+"\");\n";  // No I18N
            code = code + "    "+className+" jndiObject = ("+className+")jndiRootCtx.lookup(\"" + correctValue(offset,true) + "\");\n"; // No I18N
        }
        else{
            code = code + "    "+className+" jndiObject = ("+className+")jndiCtx.lookup(\"" + correctValue(offset,true) + "\");\n";  //No I18N
        }
        return code;
    }

    /** Generates an tail code
     *  @return String code
     */
    private static String generateTail(){
        return "} catch (javax.naming.NamingException ne) {\n    ne.printStackTrace();\n}\n"; // No I18N
    }
    
    /** Returns string representing the hexadecimal
     *  value of character
     *  @param char c, character to be converted
     *  @result String in the form 0x<hexdigit>+
     */
    private static String hexStrVal (char c) {
        int val = (int) c;
        StringBuffer res = new StringBuffer ("\\u0000");
        int index = 5;
        do {
            int rem = (val & 0xf);
            val = (val >>> 4);
            res.setCharAt (index--, hexDigitTable[rem]);
        }while (val > 0);
        return res.toString();
    }
}
