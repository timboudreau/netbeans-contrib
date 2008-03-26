/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.spellchecker.hunspell;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;

/**
 *
 * @author Jan Lahoda
 */
public class DictionaryImpl implements Dictionary {
    
    private static final Logger LOG = Logger.getLogger("org.netbeans.modules.spellchecker.hunspell");
    
    private Pointer hunspell;
    private Charset encoding;

    private DictionaryImpl(Pointer hunspell, Charset encoding) {
        this.hunspell = hunspell;
        this.encoding = encoding;
    }
    
    public synchronized ValidityType validateWord(CharSequence word) {
        byte[] nullFinishedBytes = computeBytes(word);

        if (Hunspell.INSTANCE.Hunspell_spell(hunspell, nullFinishedBytes) == 0) {
            return ValidityType.INVALID;
        } else {
            return ValidityType.VALID;
        }
    }

    public List<String> findValidWordsForPrefix(CharSequence word) {
        return Collections.emptyList();
    }

    public synchronized List<String> findProposals(CharSequence word) {
        byte[] nullFinishedBytes = computeBytes(word);
        PointerByReference result = new PointerByReference();
        int num = Hunspell.INSTANCE.Hunspell_suggest(hunspell, result, nullFinishedBytes);
        Pointer[] proposals = result.getValue().getPointerArray(0, num);
        List<String> proposalsStrings = new LinkedList<String>();
        
        for (Pointer p : proposals) {
            proposalsStrings.add(readString(p));
            
            C.INSTANCE.free(p);
        }
        
        C.INSTANCE.free(result.getValue());
        
        return proposalsStrings;
    }
    
    private byte[] byteBuffer = new byte[20];
    
    private synchronized String readString(Pointer p) {
        int cntr = 0;
        byte read;
        
        while ((read = p.getByte(cntr)) != 0) {
            if (byteBuffer == null || byteBuffer.length <= cntr) {
                byte[] newBuffer = new byte[2 * byteBuffer.length];
                
                System.arraycopy(byteBuffer, 0, newBuffer, 0, byteBuffer.length);
                byteBuffer = newBuffer;
            }
            byteBuffer[cntr++] = read;
        }
        
        return encoding.decode(ByteBuffer.wrap(byteBuffer, 0, cntr)).toString();
    }

    @Override
    protected void finalize() throws Throwable {
        Hunspell.INSTANCE.Hunspell_destroy(hunspell);
        
        super.finalize();
    }

    private byte[] computeBytes(CharSequence word) {
        byte[] bytes = encoding.encode(CharBuffer.wrap(word)).array();
        byte[] nullFinishedBytes = new byte[bytes.length + 1];

        System.arraycopy(bytes, 0, nullFinishedBytes, 0, bytes.length);
        nullFinishedBytes[bytes.length] = 0;

        return nullFinishedBytes;
    }
    
    static DictionaryImpl create(File aff, File dict) {
        Pointer hunspell = Hunspell.INSTANCE.Hunspell_create(aff.getAbsolutePath(), dict.getAbsolutePath());

        Pointer enc = Hunspell.INSTANCE.Hunspell_get_dic_encoding(hunspell);
        String encodingName = enc.getString(0);

        try {
            if (!Charset.isSupported(encodingName)) {
                LOG.log(Level.FINE, "Unsupported charset: {0}", encodingName);
                Hunspell.INSTANCE.Hunspell_destroy(hunspell);
                return null;
            }
        } catch (IllegalCharsetNameException ex) {
            LOG.log(Level.FINE, null, ex);
            Hunspell.INSTANCE.Hunspell_destroy(hunspell);
            return null;
        }
        
        return new DictionaryImpl(hunspell, Charset.forName(encodingName));
    }

}
