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

public class IORProfile {

    IORInputStream in_;
    int tag_;
    short major_;
    short minor_;
    String hostname_;
    short port_;
    byte[] objectKey_;
    TaggedComponent[] components_;
    byte[] objectId_;
    byte[] adapterId_;
    String poaHierarchy_;
    String timeStamp_;
    boolean persistent_;

    public IORProfile (int tag, byte[] profile) {
	in_ = new IORInputStream (profile);
	tag_ = tag;
	this.parse ();
    }

    public int getTag () {
	return tag_;
    }

    public short getMajor () {
	return this.major_;
    }
    
    public short getMinor () {
	return this.minor_;
    }
    
    public String getHostname () {
	return this.hostname_;
    }
    
    public short getPort () {
	return this.port_;
    }
    
    public byte[] getObjectKey () {
	return this.objectKey_;
    }
    
    public byte[] getObjectId () {
	return objectId_;
    }
    
    public byte[] getAdapterId () {
	return adapterId_;
    }
    
    public String getPOAHierarchy () {
	return poaHierarchy_;
    }
    
    public boolean isPersistent () {
	return persistent_;
    }
    
    // Valid only for transient object
    public String getCreationTime () {
	return timeStamp_;
    }
    
    public int componentCount() {
	return this.components_.length;
    }
    
    public TaggedComponent[] getComponents () {
	return this.components_;
    }
    
    public String toString () {
	String res = "Tag= "+tag_ + "\nMajor= " +major_ + "\nMinor= "+minor_ + "\nHostname= "+hostname_ +"\nPort= "+port_+"\n";
	String dta = "";
	for (int i=0; i< objectKey_.length; i++) {
	    dta = dta + new Character ((char) objectKey_[i]).toString();
	}
	res = res + "Object Key= "+dta+"\n";
	for (int i=0; i<components_.length; i++) {
	    res +="Component Tag= "+components_[i].tag+"\n";
	    dta ="";
	    for (int j=0; j<components_[i].component_data.length; j++) {
		dta = dta + new Character ((char)components_[i].component_data[j]).toString();
	    }
	    res +="Component Data= "+dta+"\n";
	} 
	return res;
    }
    
    private void parse () {
	major_ = (short) in_.readOctet ();
	minor_ = (short) in_.readOctet ();
	hostname_ = in_.readString ();
	port_ = in_.readUnsignedShort ();
	objectKey_ = in_.readOctetArray ();
	if (major_ >=1  && minor_ >=1) {
	    int componentCount = in_.readUnsignedLong ();
	    components_ = new TaggedComponent [componentCount];
	    for (int i=0; i< componentCount; i++) {
		components_[i] = new TaggedComponent ();
		components_[i].tag = in_.readUnsignedLong();
		components_[i].component_data = in_.readOctetArray ();
	    }
	}
	else
	    components_ = new TaggedComponent[0];
	if (objectKey_.length > 4 && objectKey_[0]==(byte)0xab && objectKey_[1]==(byte)0xac && objectKey_[2]==(byte)0xab) {
	    int index = 3;
	    if (objectKey_[index] == '0'){
		persistent_ = true;
		index++;
	    }
	    else if (objectKey_[index] == '1'){
		persistent_ = false;
		index++;
		int start = index;
		while (index < objectKey_.length && objectKey_[index]!='\0')
		    index++;
		if (index >= objectKey_.length)
		    return;  // Malformed
		timeStamp_ = new String (objectKey_, start, index, index - start);
		index++;
	    }
	    else
		return;  // Unknown type
	    poaHierarchy_ = ""; 
	    while (index < objectKey_.length) {
	        int start = index;
		while (index < objectKey_.length && objectKey_[index] != 0)
		    index ++;
		if (index >= objectKey_.length)
		    return;
		String id = new String (objectKey_, 0,start, index - start);
		poaHierarchy_ = poaHierarchy_ + "/" + id;
		index++;
		if (index >= objectKey_.length)
		    return;
		if (objectKey_[index] == 0) {
		    adapterId_ = new byte [index+1];
		    System.arraycopy (objectKey_,0,adapterId_,0,index+1);
		    index++;
		    start = index;
		    while (index < objectKey_.length)
			index ++;
		    objectId_ = new byte [index - start];
		    System.arraycopy (objectKey_,start,objectId_,0,index-start); 
		    index++;
		}
	    }
	}	
    }
    
    public boolean equals (Object other) {
        if (!(other instanceof IORProfile))
            return false;
        IORProfile o = (IORProfile) other;
        if (tag_ != o.tag_)
            return false;
        if (major_ != o.major_)
            return false;
        if (minor_ != o.minor_)
            return false;
        if (!hostname_.equals(o.hostname_))
            return false;
        if (port_ != o.port_)
            return false;
        if (objectKey_.length != o.objectKey_.length)
            return false;
        for (int i=0; i<objectKey_.length; i++) {
            if (objectKey_[i] != o.objectKey_[i])
                return false;
        }
        return true;
    }
    
    
}
