/*
 * TemplateInputStream.java
 *
 * Created on December 07, 2006, 4:42 PM
 */

package org.netbeans.modules.themebuilder.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

//TODO - Not a Good way. Use Token matchin replacing  using regular expression 
public final class TemplateInputStream extends FilterInputStream {
    
    private static final int MAX_BYTES = 250;
    private byte[] readBuffer = new byte[MAX_BYTES];
    private int startIndex = -1;
    private int stopIndex = -1;
    
    private Map<String,String> parameterMap;
    
    public TemplateInputStream(InputStream in, Map<String,String> parameterMap) {
        super(in);
        this.parameterMap = parameterMap;
    }
    
    @Override
    public int read() throws IOException {
        int c;
        if (startIndex < stopIndex) {
            c = readBuffer[startIndex++];
        } else {
            c = super.read();
        }
        if (c == '@') {
            startIndex = 0;
            stopIndex = 0;
            readBuffer[stopIndex++] = '@';
            c = super.read();
            readBuffer[stopIndex++] = (byte) c;
            
            while ((c != -1) && (c != '@'))  {
                c = super.read();
                readBuffer[stopIndex++] = (byte) c;
                // This is a hack in case there are string like @param 
                if ((stopIndex > MAX_BYTES - 2)){
                    startIndex = 0;
                    stopIndex = 0;
                    return '@';
                }
            }
            if (c == '@') {
                String key = new String(readBuffer, 1, stopIndex - 2);
                if (this.parameterMap.containsKey(key)) {
                    String value = this.parameterMap.get(key);
                    if (value != null) {
                        stopIndex = 0;
                        byte[] bytes = value.getBytes();
                        while (stopIndex < bytes.length) {
                            readBuffer[stopIndex] = bytes[stopIndex];
                            stopIndex++;
                        }
                    }
                }
            }
            c = readBuffer[startIndex++];
        }
        return c;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public int read(byte[] bytes, int offset, int length) throws IOException {
        if (bytes == null) {
            throw new NullPointerException();
        } else if ((offset < 0) || (offset > bytes.length) || (length < 0) ||
                ((offset + length) > bytes.length) || ((offset + length) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (length == 0) {
            return 0;
        }
        
        int c = this.read();
        if (c == -1) {
            return -1;
        }
        bytes[offset] = (byte) c;
        
        int i = 1;
        for (; i < length ; i++) {
            c = read();
            if (c == -1) {
                break;
            }
            if (bytes != null) {
                bytes[offset + i] = (byte)c;
            }
        }
        return i;
    }
    
    @Override
    public int read(byte[] bytes) throws IOException {
        return this.read(bytes, 0, bytes.length);
    }
    
    
}
