/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.clazz;

/**
* Exception encapsulating other possible exceptions occurred while working with
* .class file.
*
* @author Jan Jancura
* @version 0.10, Apr 15, 1998
*/
public class ClassException extends java.lang.reflect.InvocationTargetException {
  /** generated Serialized Version UID */
  static final long serialVersionUID = 1159065613681402933L;

  /**
  * Construct ClassException encapsulating some other.
  *
  * @param throwable Exception to be encapsulated.
  */
  public ClassException (Throwable throwable) {
    super (throwable);
  }

  /**
  * Construct ClassException encapsulating some other with special comment.
  *
  * @param throwable Exception to be encapsulated.
  * @param comment Comment.
  */
  public ClassException (Throwable throwable, String comment) {
    super (throwable, comment);
  }
}

/*
 * Log
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 */
