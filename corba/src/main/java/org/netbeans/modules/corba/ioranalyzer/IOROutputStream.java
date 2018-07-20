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


public class IOROutputStream {

    protected byte[] buf_;
    protected int pos_;
    protected boolean little_endian_;

    public IOROutputStream () {
	buf_ = new byte[1024];
	pos_ = 0;
    }

    public IOROutputStream (int size) {
	buf_ = new byte [size];
	pos_ = 0;
    }

    public void setBigEndian (boolean flag) {
	little_endian_ = ! flag;
    }

    public void setLittleEndian (boolean flag) {
	little_endian_ = flag;
    }

    public void writeUnsignedLong (int value) {
	int i = pos_ % 4;
	if ( i != 0) {
	    i = 4 - i;
	    ensureCapacity (i+4);
	    pos_+= i;
	}
	else {
	    ensureCapacity (4);
	}
	if (little_endian_) {
	    buf_[pos_++] = (byte) value;
	    buf_[pos_++] = (byte) (value>>8);
	    buf_[pos_++] = (byte) (value>>16);
	    buf_[pos_++] = (byte) (value>>24);
	}
	else {
	    buf_[pos_++] = (byte) (value>>24);
	    buf_[pos_++] = (byte) (value>>16);
	    buf_[pos_++] = (byte) (value>>8);
	    buf_[pos_++] = (byte) value;
	}
    }
    
    public void writeUnsignedShort (short value) {
	int i = pos_ % 2;
	ensureCapacity (i + 2);
	pos_ += i;
	if (little_endian_) {
	    buf_[pos_++] = (byte) value;
	    buf_[pos_++] = (byte) (value>>8);
	}
	else {
	    buf_[pos_++] = (byte) (value>>8);
	    buf_[pos_++] = (byte) value;
	}
    }
    
    public void writeOctet (byte octet) {
	ensureCapacity (1);
	buf_[pos_++] = octet;
    }
    
    public void writeString (String str) {
	byte[] b = str.getBytes();
	int len = b.length;
	writeUnsignedLong (len+1);
	ensureCapacity (len);
	System.arraycopy (b,0,buf_,pos_,len);
	pos_+= len;
	writeOctet ((byte)0);
    }
    
    public void writeOctetArray (byte[] a) {
	int len = a.length;
	writeUnsignedLong (len);
	ensureCapacity (len);
	System.arraycopy (a,0,buf_,pos_,len);
	pos_+=len;
    }
    
    public byte[] getBytes () {
	this.trim();
	return (byte[]) buf_.clone();
    }
    
    public void writeRaw (byte[] data) {
	int len = data.length;
	ensureCapacity (len);
	System.arraycopy (data,0,buf_,pos_,len);
	pos_+=len;
    }
    
    private void ensureCapacity (int len) {
	int newLen = len + pos_;
	if (newLen > buf_.length)
	    enlarge (512);
    }
    
    private void resize (int size) {
	byte[] bufTmp = new byte[size];
	System.arraycopy (buf_,0,bufTmp,0,buf_.length);
	buf_ = bufTmp;
    }
    
    private void enlarge (int delta) {
	byte[] bufTmp = new byte [buf_.length + delta];
	System.arraycopy (buf_,0,bufTmp,0,buf_.length);
	buf_ = bufTmp;
    }
    
    
    private void trim () {
	byte[] bufTmp = new byte [pos_];
	System.arraycopy (buf_,0,bufTmp,0,pos_);
	buf_ = bufTmp;
    }
    
    public String toString () {
	String res;
	if (little_endian_)
	    res = "01000000";
	else
	    res = "00000000";
	trim ();
	res = res + Stringifier.stringify (buf_);
	return res;
    }

}