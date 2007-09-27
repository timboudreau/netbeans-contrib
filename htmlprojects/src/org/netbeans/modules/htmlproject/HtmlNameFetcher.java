/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.


The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s): */
package org.netbeans.modules.htmlproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.ErrorManager;

/**
 *
 * @author Tim Boudreau
 */
final class HtmlNameFetcher implements Runnable {
    private List pairs = new ArrayList (100);
    private static final ByteBuffer buf = ByteBuffer.allocate(8192);
    private static final CharsetDecoder decoder = 
            Charset.defaultCharset().newDecoder();
    private static final Pattern pat = Pattern.compile(".*<title>(.*?)</title>", //NOI18N
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

//    <META http-equiv="Content-Type" content="text/html; charset=UTF-8">

    private static final String CONTENT_TYPE_PATTERN = 
            ".*<META.*?Content-type.*?charset=(.*?)\">";
    
    private static final Pattern encodingPattern = 
            Pattern.compile(CONTENT_TYPE_PATTERN, 
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    
    HtmlNameFetcher() {
    }
    
    private static final class Pair {
        private final File file;
        private final Reference nodeRef;
        public Pair (File file, HtmlFileNode node) {
            assert node != null;
            assert file != null;
            this.nodeRef = new WeakReference(node);
            this.file = file;
        }
        
        public String toString() {
            return file.getPath();
        }
        
        public HtmlFileNode getNode() {
            return (HtmlFileNode) nodeRef.get();
        }
        
        public File getFile() {
            return file;
        }
    }
    
    private boolean enqueued = false;
    synchronized void add(Kids kids, HtmlFileNode nd, File file) {
        if (!enqueued) {
            kids.post(this);
            enqueued = true;
            cancelled = false;
        }
        pairs.add (new Pair(file, nd));
    }
    
    boolean cancelled = false;
    void cancel() {
        synchronized (this) {
            pairs.clear();
        }
        cancelled = true;
    }

    private boolean running = false;
    public void run() {
        Pair[] pair;
        enqueued = false;
        running = true;
        synchronized (this) {
            pair = (Pair[]) pairs.toArray(new Pair[pairs.size()]);
            pairs.clear();
        }
        for (int i = 0; i < pair.length; i++) {
            handlePair(pair[i]);
            if (Thread.currentThread().isInterrupted() || cancelled) {
                cancel();
            }
        }
    }
    
    private CharSequence decode (ByteBuffer buf) throws CharacterCodingException {
        CharsetDecoder decoder = this.decoder;
        
        CharSequence seq = decoder.decode (buf);
        Matcher matcher = encodingPattern.matcher(seq);
        if (matcher.lookingAt()) {
            String enc = matcher.group(1);
            String encoding = enc.toUpperCase(Locale.ENGLISH);
            String defEncoding = Charset.defaultCharset().name().toUpperCase(
                    Locale.ENGLISH);
            if (!defEncoding.equals(encoding)) {
                try {
                    Charset charset = Charset.forName(encoding);
                    decoder = charset.newDecoder();
                    seq = decoder.decode(buf);
                } catch (Exception e) {
                    ErrorManager.getDefault().notify (
                           ErrorManager.INFORMATIONAL, e);
                }
            }
        }
        return seq;
    }
    
    private void handlePair (Pair pair) {
        File f = pair.getFile();
        String result = null;
        if (f.exists() && f.isFile() && f.length() > 20) {
            FileChannel fc;
            try {
                if (pair.getNode() == null) {
                    return;
                }
                fc = new FileInputStream(f).getChannel();
                buf.clear();
                fc.read(buf);
                fc.close();
                buf.flip();
                //XXX actually detect the encoding in the file header
                CharSequence seq = decode(buf);
                Matcher matcher = pat.matcher(seq);
                if (matcher.lookingAt()) {
                    result = matcher.group(1);
                }
            }  catch (IOException ioe) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL,
                        ioe);
            }
        }
        HtmlFileNode n = pair.getNode();
        if (n != null && result != null) {
            n.setDisplayName(result);
        }
        if (n != null) {
            n.checked();
        }
    }
}
