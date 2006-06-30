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
import java.util.ArrayList;

public class IORData {

    IORInputStream in_;
    boolean littleEndian_;
    String repositoryId_;
    ArrayList profiles_;

    public IORData (String ior) {
	profiles_ = new ArrayList ();
	in_ = new IORInputStream (ior);
	this.parse();
    }

    public IORData () {
	profiles_ = new ArrayList ();
    }

    public boolean isLittleEndian () {
	return littleEndian_;
    }

    public void setLittleEndian (boolean flag) {
	this.littleEndian_ = flag;
    }
    
    public boolean isBigEndian () {
	return ! littleEndian_;
    }
    
    public void setBigEndian (boolean flag) {
	this.littleEndian_ = ! flag;
    }
    
    public String getRepositoryId () {
	return repositoryId_;
    }
    
    public void setRepositoryId (String repositoryId) {
	this.repositoryId_ = repositoryId;
    }
    
    public ArrayList getProfiles () {
	return profiles_;
    }
    
    public void addProfiles (ArrayList profiles) {
	this.profiles_.addAll(profiles);
    }
    
    public void write (IOROutputStream out) {
	out.setLittleEndian (littleEndian_);
	out.writeString (repositoryId_);
	out.writeUnsignedLong (this.profiles_.size());
	for (int i=0; i< this.profiles_.size(); i++) {
	    Object obj = profiles_.get(i);
	    int tag = 0;
	    byte[] data = null;
	    if (obj instanceof IORProfile) {
		tag = ((IORProfile)obj).tag_;
		data = ((IORProfile)obj).in_.buf;
	    }
	    else if (obj instanceof IORTaggedProfile) {
		tag = ((IORTaggedProfile)obj).getTag();
		data = ((IORTaggedProfile)obj).getData();
	    }
	    out.writeUnsignedLong (tag);
	    out.writeOctetArray (data);
	}
    }
        
    public String toString () {
	String res = "LittleEndian "+ littleEndian_ +"\nRepositoryId= " + repositoryId_+"\n";
	for (int i=0; i< profiles_.size(); i++) {
	    res+= profiles_.get(i).toString();
	}
	return res;
    }    
    
    private void parse () {
	littleEndian_ = in_.isLittleEndian();
	in_.skeep (4);
	repositoryId_ = in_.readString ();	
	int noProfiles = in_.readUnsignedLong ();
	for (int i=0; i<noProfiles; i++) {
	    int tag = in_.readUnsignedLong ();
	    byte[] profileData = in_.readOctetArray ();
	    if (tag == 0) {
		IORProfile p = new IORProfile (tag, profileData);
		this.profiles_.add (p);
	    }
	    else {
		this.profiles_.add (new IORTaggedProfile (tag, profileData));
	    }
	}
    }

}
