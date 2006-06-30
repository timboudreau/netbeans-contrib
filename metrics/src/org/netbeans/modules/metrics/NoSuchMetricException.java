/*
 * NoSuchMetricException.java
 *
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2002-2001 Sun
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
