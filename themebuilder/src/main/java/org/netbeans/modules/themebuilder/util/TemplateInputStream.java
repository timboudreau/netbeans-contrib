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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
