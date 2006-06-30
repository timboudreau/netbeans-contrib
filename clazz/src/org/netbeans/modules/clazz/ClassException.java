/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
