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

import org.openide.util.NbBundle;

public class IORMerge {

    public static String merge (String[] iors) {
	IORData outData = new IORData ();
	String repositoryId = null;
	for (int i=0; i < iors.length; i++) {
	    if (! iors[i].startsWith ("IOR:"))
		throw new org.omg.CORBA.BAD_PARAM (iors[i]);
	    if ((iors[i].length() % 2) != 0)
		throw new org.omg.CORBA.BAD_PARAM (iors[i]);
	    IORData inData = new IORData (iors[i]);
	    if (repositoryId == null) {
		repositoryId = inData.getRepositoryId();
		outData.setLittleEndian (inData.isLittleEndian());
		outData.setRepositoryId (repositoryId);
	    }
	    else {
		if (!repositoryId.equals (inData.getRepositoryId()))
		    throw new org.omg.CORBA.BAD_PARAM (NbBundle.getBundle(IORMerge.class).getString("TXT_BadRepoIds"));
	    }
	    outData.addProfiles (inData.getProfiles());
	}
	IOROutputStream out = new IOROutputStream ();
	outData.write (out);
	return out.toString();
    }
    
}