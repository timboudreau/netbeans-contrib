/*
 * NoSuchMetricException.java
 *
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 2002-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

/**
 * Thrown if a method-level metric is requested from a metric that
 * doesn't support method-level metrics.
 */
public class NoSuchMetricException extends Exception {
    /**
     * Constructs an NoSuchMetricException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public NoSuchMetricException() {
	super();
    }

    /**
     * Constructs an NoSuchMetricException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     * @param s the String that contains a detailed message
     */
    public NoSuchMetricException(String s) {
	super(s);
    }
}
