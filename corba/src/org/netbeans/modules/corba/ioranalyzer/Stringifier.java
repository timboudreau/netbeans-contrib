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