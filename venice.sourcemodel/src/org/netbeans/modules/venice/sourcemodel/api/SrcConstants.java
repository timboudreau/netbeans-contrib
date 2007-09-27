/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.venice.sourcemodel.api;

/**
 * SrcConstants is an interface defining some constants to be used with
 * source models for requesting children, etc.
 *
 * @author Tim Boudreau
 */
public interface SrcConstants {
    /** Key to pass to getChildren() */
    public static final String CHILDREN_USAGES = "usages"; //NOI18N
    /** Key to pass to getChildren() */
    public static final String CHILDREN_CLOSURE = "closure"; //NOI18N
    /** Key to pass to getChildren() */
    public static final String CHILDREN_MEMBERS = "members"; //NOI18N
    /** Key to pass to getChildren() */
    public static final String CHILDREN_PARENTCLASS = "parentClass"; //NOI18N

    /** Key to pass to getValue() to determine the kind of object being
        represented */
    public static final String KEY_KIND = "kind"; //NOI18N
    
    /** return value for getValue(KEY_KIND) */
    public static final String VAL_CLASS = "class"; //NOI18N
    /** return value for getValue(KEY_KIND) */
    public static final String VAL_METHOD = "method"; //NOI18N
    /** return value for getValue(KEY_KIND) */
    public static final String VAL_FIELD = "field"; //NOI18N
    /** return value for getValue(KEY_KIND) */
    public static final String VAL_CONSTRUCTOR = "constructor"; //NOI18N
    
    /** Pass to getValue() to get a URL to a source file.
     * Value will be java.net.URL */
    public static final String KEY_URL = "url";
    
    /** Pass to getValue() to get the offsets of the element in its source file.
     * Value type will be int[2] { start, end } */
    public static final String KEY_OFFSET = "offsets";
    
    /** Model kind returned by Model.getKind() for a java source model */
    public static final String MODEL_KIND_JAVA_SOURCE = "javaSource"; //NOI18N
}
