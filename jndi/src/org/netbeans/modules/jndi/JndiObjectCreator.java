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

package com.netbeans.enterprise.modules.jndi;

import java.util.Hashtable;
import java.util.Enumeration;
import javax.naming.CompositeName;
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
  static String getCode(Context ctx, CompositeName offset, String className) throws NamingException {

    Hashtable env = ctx.getEnvironment();
    if (env == null) {
      return null;
    }
    String code = "/** Inserted by Jndi module */\n";
    code = code + "java.util.Properties jndiProperties = new java.util.Properties();\n";
    Enumeration keys = env.keys();
    Enumeration values = env.elements();
    while (keys.hasMoreElements()) {
      String name = correctValue((String)keys.nextElement());
      String value= correctValue((String)values.nextElement());
      if (name.equals(JndiRootNode.NB_ROOT) ||
          name.equals(JndiRootNode.NB_LABEL)) {
        continue;
      }
      code = code + "jndiProperties.put(\"" + name + "\",\"" + value + "\");\n";
    }
    code = code + "try {\n  javax.naming.directory.DirContext jndiCtx = new javax.naming.directory.InitialDirContext(jndiProperties);\n";
    code = code + "  "+className+" jndiObject = ("+className+")jndiCtx.lookup(\"" + offset.toString() + "\");\n";
    code= code + "} catch (javax.naming.NamingException ne) {\n  ne.printStackTrace();\n}\n";
    return code;
  }
}

/*
 * <<Log>>
 *  6    Gandalf   1.5         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  5    Gandalf   1.4         7/9/99   Ales Novak      localization + code 
 *       requirements followed
 *  4    Gandalf   1.3         6/10/99  Ales Novak      beautified code
 *  3    Gandalf   1.2         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  2    Gandalf   1.1         6/8/99   Ales Novak      sources beautified + 
 *       subcontext creation
 *  1    Gandalf   1.0         6/4/99   Ales Novak      
 * $
 */
