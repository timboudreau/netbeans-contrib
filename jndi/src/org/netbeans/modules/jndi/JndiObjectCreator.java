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

    /** This method corrects string that contains \ to \\
     */
    static String correctValue(String str) {
        StringBuffer sb = new StringBuffer(str);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\\') {
                sb.insert(i, '\\');
                i++;
            }
            else if (sb.charAt(i) == '\''){
                sb.insert(i,'\\');
                i++;
            }
            else if (sb.charAt(i) =='\"'){
                sb.insert(i,'\\');
                i++;
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
    
    
    public static String stringifyCompositeName (Name name, Context ctx) {
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
            String name = correctValue((String)keys.nextElement());
            String value= correctValue((String)values.nextElement());
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
            code = code + "    javax.naming.Context jndiRootCtx = (javax.naming.Context) jndiCtx.lookup(\""+correctValue(root)+"\");\n";  // No I18N
            code = code + "    "+className+" jndiObject = ("+className+")jndiRootCtx.lookup(\"" + correctValue(offset) + "\");\n"; // No I18N
        }
        else{
            code = code + "    "+className+" jndiObject = ("+className+")jndiCtx.lookup(\"" + correctValue(offset) + "\");\n";  //No I18N
        }
        return code;
    }

    /** Generates an tail code
     *  @return String code
     */
    private static String generateTail(){
        return "} catch (javax.naming.NamingException ne) {\n    ne.printStackTrace();\n}\n"; // No I18N
    }
}