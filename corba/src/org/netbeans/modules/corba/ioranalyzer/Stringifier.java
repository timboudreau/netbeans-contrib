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

package org.netbeans.modules.corba.ioranalyzer;


public class Stringifier {

    private final static char[] TRANSLATION_TABLE = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    public static String stringify (byte[] data) {
	String buf="";
	for (int i=0; i< data.length; i++) {
	    buf = buf + stringify (data[i]);
	}
	return buf;
    }

    public static String stringify (byte data) {
	char[] tmp = new char[2];
	tmp[0]= TRANSLATION_TABLE[(data>>4) & 0x0f];
	tmp[1]= TRANSLATION_TABLE[data & 0x0f];
	return new String (tmp);
    }
    
    
}