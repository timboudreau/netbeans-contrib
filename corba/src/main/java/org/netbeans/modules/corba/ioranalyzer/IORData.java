/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
