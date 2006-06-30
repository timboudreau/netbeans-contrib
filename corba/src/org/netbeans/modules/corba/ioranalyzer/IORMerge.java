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