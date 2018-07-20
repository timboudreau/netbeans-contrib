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

public class IORInputStream {

    protected boolean little_endian;
    protected byte[] buf;
    protected int count;
    protected int pos;

    public IORInputStream (String ior) {
        if (ior == null)
            throw new org.omg.CORBA.BAD_PARAM ("Not valid IOR");
	int len = ior.length();
	if (len < 12)
	    throw new org.omg.CORBA.BAD_PARAM ("Not valid IOR");
	if (!ior.startsWith("IOR:"))
	    throw new org.omg.CORBA.BAD_PARAM ("Not valid IOR");
	len  -=  4;
	if (( len & 0x1) != 0)
	    throw new org.omg.CORBA.BAD_PARAM ("Not valid IOR");
	len /= 2;
	this.buf = new byte [len];
	this.count = len;
	int j = 0;
	for (int i=4; j< len; i+=2,j++) {	    	    
	    this.buf[j] = (byte) ((byte)(hexDigit(ior.charAt(i))<<4 & (byte) 0xf0) | (byte) (hexDigit(ior.charAt(i+1)) & (byte) 0xf));
	}
	this.little_endian = (this.buf[0]!=0);
    }
    
    public IORInputStream (byte[] data) {
	this.buf = data;
	this.pos = 1;
	this.count = data.length;
	this.little_endian = (data[0]!=0);
    }

    public boolean isLittleEndian () {
	return this.little_endian;
    }
    
    public boolean isBigEndian () {
	return ! this.little_endian;
    }
    
    public String readString () {
	int i = this.readUnsignedLong () - 1;
        if ((this.pos+i) > this.count || i < 0)
            throw new IllegalStateException ();
	String res = new String (this.buf,this.pos,i);
	this.pos+=i;
	if (this.read() != 0)
	    throw new org.omg.CORBA.BAD_PARAM ("Bad IOR format");
	return res;
    }
    
    public int readUnsignedLong () {
	int i = this.pos % 4;
	if (i!= 0)
	    this.pos += 4 - i;
        if (this.pos+4 > this.count)
            throw new IllegalStateException ();
	if (little_endian)
	    return this.buf[this.pos++] & 0xff | this.buf[this.pos++]<< 8 & 0xff00 | this.buf[this.pos++]<<16 & 0xff0000 | this.buf[this.pos++]<<24 &0xff000000;
	else
	    return this.buf[this.pos++] << 24 & 0xff000000 | this.buf[this.pos++]<<16 & 0xff0000 | this.buf[this.pos++]<<8 & 0xff00 | this.buf[this.pos++] & 0xff;
    }
    
    public short readUnsignedShort () {
	int i = this.pos % 2;
	if (i!= 0)
	    this.pos += 2 - i;
        if ((this.pos+2) > this.count)
            throw new IllegalStateException ();
	if (this.little_endian)
	    return (short) (this.buf[this.pos++] & (short) 0xff | this.buf[this.pos++]<<8 & (short ) 0xff00);
	else
	    return (short) (this.buf[this.pos++]<<8 & (short) 0xff00 | this.buf[this.pos++] & (short)0xff);
    }
    
    public byte readOctet () {
        if (this.pos+1 > this.count)
            throw new IllegalStateException ();
	return this.buf[this.pos++];
    }
    
    public byte[] readOctetArray () {
	int len = this.readUnsignedLong();
        if (this.pos+len > this.count)
            throw new IllegalStateException ();
	byte[] res = new byte[len];
	System.arraycopy (this.buf,this.pos,res,0,len);
	this.pos+= len;
	return res;
    }
    
    private byte hexDigit (char a) {
	if ( a >= '0' && a<='9')
	    return (byte) (a - '0');
	else if (a>='a' && a<='f')
	    return (byte) (10 + a - 'a');
	else if (a>='A' && a<='F')
	    return (byte) (10 + a - 'A');
	else
	    throw new org.omg.CORBA.BAD_PARAM ("Invalid IOR format");
    }
    
    public String toString () {
	StringBuffer bfr = new StringBuffer ();
	for (int i=0; i <buf.length; i++)
	    bfr.append((char)buf[i]);
	return bfr.toString();
    }
    
    public int read () {
        if (this.pos + 1 > this.count)
            throw new IllegalStateException ();
	return this.buf[this.pos++];
    }
    
    public void skeep (int size) {
        if (this.pos + size > this.count)
            throw new IllegalArgumentException ();
	this.pos+=size;
    }
    
    public void seek (int size) {
        if (size > this.count ||
            size < 0)
            throw new IllegalArgumentException ();
	this.pos = size;
    }
}
