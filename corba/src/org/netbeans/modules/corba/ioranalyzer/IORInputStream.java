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

public class IORInputStream {

    protected boolean little_endian;
    protected byte[] buf;
    protected int count;
    protected int pos;

    public IORInputStream (String ior) {
        if (ior == null)
            throw new org.omg.CORBA.BAD_PARAM ("Not valid IOR");
	int len = ior.length();
	if (len < 4)
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
	if (little_endian)
	    return this.buf[this.pos++] & 0xff | this.buf[this.pos++]<< 8 & 0xff00 | this.buf[this.pos++]<<16 & 0xff0000 | this.buf[this.pos++]<<24 &0xff000000;
	else
	    return this.buf[this.pos++] << 24 & 0xff000000 | this.buf[this.pos++]<<16 & 0xff0000 | this.buf[this.pos++]<<8 & 0xff00 | this.buf[this.pos++] & 0xff;
    }
    
    public short readUnsignedShort () {
	int i = this.pos % 2;
	if (i!= 0)
	    this.pos += 2 - i;
	if (this.little_endian)
	    return (short) (this.buf[this.pos++] & (short) 0xff | this.buf[this.pos++]<<8 & (short ) 0xff00);
	else
	    return (short) (this.buf[this.pos++]<<8 & (short) 0xff00 | this.buf[this.pos++] & (short)0xff);
    }
    
    public byte readOctet () {
	return this.buf[this.pos++];
    }
    
    public byte[] readOctetArray () {
	int len = this.readUnsignedLong();
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
	return this.buf[this.pos++];
    }
    
    public void skeep (int size) {
	this.pos+=size;
    }
    
    public void seek (int size) {
	this.pos = size;
    }
}