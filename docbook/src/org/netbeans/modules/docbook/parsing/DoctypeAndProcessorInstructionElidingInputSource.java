/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.docbook.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.util.Exceptions;
import org.xml.sax.InputSource;

/**
 *
 * @author Tim Boudreau
 */
public final class DoctypeAndProcessorInstructionElidingInputSource extends InputSource {
    private final File file;
    private final String charsetName;
    static final Pattern XML_DEF_PATTERN_WITH_ENCODING = Pattern.compile("^<\\?xml.*?encoding=\"(.*?)\"\\?>", Pattern.DOTALL | Pattern.MULTILINE); //NOI18N
    static final Pattern XML_DEF_PATTERN = Pattern.compile("^<\\?xml.*?\\?>", Pattern.DOTALL | Pattern.MULTILINE); //NOI18N
    static final Pattern DOCTYPE_PATTERN = Pattern.compile("<!DOCTYPE.*?(?:\\[.*?\\])*+>", Pattern.DOTALL | Pattern.MULTILINE); //NOI18N
    public DoctypeAndProcessorInstructionElidingInputSource(File file, String charsetName) {
        this.file = file;
        this.charsetName = charsetName;
    }

    @Override
    public Reader getCharacterStream() {
        String encoding = charsetName;
        ByteBuffer buf = ByteBuffer.allocate ((int) file.length());
        try {
            FileInputStream in = new FileInputStream(file);
            FileChannel channel = in.getChannel();
            try {
                int bytes = channel.read(buf);
                assert bytes == file.length();
                buf.rewind();
                Charset charset = Charset.forName(encoding);
                CharBuffer cb = charset.decode(buf);
                int docBegin = 0;
                Matcher m = XML_DEF_PATTERN_WITH_ENCODING.matcher(cb);
                if (m.find()) {
                    encoding = m.group(1);
                    if (!"UTF-8".equals(encoding)) { //NOI18N
                        charset = Charset.forName(encoding);
                        cb = charset.decode(buf);
                        m = XML_DEF_PATTERN_WITH_ENCODING.matcher(cb);
                        assert m.find();
                        docBegin = m.end();
                    } else {
                        docBegin = m.end();
                    }
                } else {
                    m = XML_DEF_PATTERN.matcher(cb);
                    if (m.find()) {
                        docBegin = m.end();
                    }
                }
                m = DOCTYPE_PATTERN.matcher(cb);
                if (m.find(docBegin)) {
                    docBegin = m.end();
                }
                int docBeginBytes = 0;
                for (int i=0; i <= docBegin; i++) {
                    char c = cb.charAt(i);
                    docBeginBytes += new String( new char[] { c }).getBytes(charset).length;
                }
                FileInputStream realIn = new FileInputStream(file);
                if (docBeginBytes > 0) {
                    realIn.skip(docBeginBytes -1);
                }
                BufferedReader result = new BufferedReader(new InputStreamReader(realIn));
                return result;
            } finally {
                channel.close();
                in.close();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
