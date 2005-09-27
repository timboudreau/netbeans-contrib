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
 */

package org.netbeans.modules.venice.sourcemodel.api;

/**
 * SrcConstants is an interface defining some constants to be used with
 * source models for requesting children, etc.
 *
 * @author Tim Boudreau
 */
public interface SrcConstants {
    public static final String CHILDREN_USAGES = "usages";
    public static final String CHILDREN_CLOSURE = "closure";
    public static final String CHILDREN_METHODS = "methods";
    
    public static final String KEY_KIND = "kind";
    
    public static final String VAL_CLASS = "class";
    public static final String VAL_METHOD = "method";
    public static final String VAL_FIELD = "field";
    public static final String VAL_CONSTRUCTOR = "constructor";
}
