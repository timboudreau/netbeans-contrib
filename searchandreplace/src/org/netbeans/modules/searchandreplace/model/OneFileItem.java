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
package org.netbeans.modules.searchandreplace.model;

import java.awt.EventQueue;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * An Item implementation representing replacement over one file.  More
 * important is the getItems() method, which will return an inner instance
 * of Item which represents a single match within the file represented by
 * this object.
 *
 * @author Tim Boudreau
 */
final class OneFileItem extends Item {
    private final Search search;
    private boolean valid = true;
    private final Point location = new Point();
    private List items = new ArrayList();

    /** Creates a new instance of OneFileItem */
    public OneFileItem(File file, Search search) throws IOException {
        super (file);
        this.search = search;
        checkValid();
        init();
    }

    public Point getLocation() {
        return location;
    }

    public boolean isValid() {
        return valid;
    }

    private void init() throws IOException {
        assert !EventQueue.isDispatchThread();
        StringBuffer content = text();
        Pattern pattern = search.getPattern();
        Matcher matcher = pattern.matcher(content);
        int end = 0;
        int firstStart = -1;
        int firstEnd = -1;
        while (matcher.find(end)) {
            MatchResult res = matcher.toMatchResult();
            end = res.end();
            InFileItem item = new InFileItem (new Point(res.start(),
                end));
            items.add(item);
            if (firstStart == -1) {
                firstStart = res.start();
                firstEnd = end;
            }
        }
    }

    public List getItems() {
        return items == null ? Collections.singletonList(this) :
            Collections.unmodifiableList(items);
    }

    public void replace() throws IOException {
        assert !EventQueue.isDispatchThread();
//        assert search.getRequestProcessor().isRequestProcessorThread();
        if (!isShouldReplace() || isReplaced()) {
            return;
        }
        checkValid();
        StringBuffer content = text();
        Pattern pattern = search.getPattern();
        Matcher matcher = pattern.matcher(content);
        boolean shouldReplaceAll = shouldReplaceAll();
        boolean shouldReplaceNone = !shouldReplaceAll && shouldReplaceNone();
        if (shouldReplaceNone) {
            return;
        }
        if (items == null || shouldReplaceAll) {
            String s = matcher.replaceAll(search.getReplacementText());
            text = new StringBuffer (s);
            if (items != null) {
                for (Iterator i = items.iterator(); i.hasNext();) {
                    Item item = (Item) i.next();
                    if (item instanceof InFileItem) {
                        ((InFileItem) item).setReplaced(true);
                    }
                }
            }
        } else {
            throw new IllegalStateException ("Call replace on my items, not me");
        }
    }

    private boolean shouldReplaceAll() {
        boolean result = isShouldReplace();
        if (result) {
            if (items != null) {
                for (Iterator i = items.iterator(); i.hasNext();) {
                    Item item = (Item) i.next();
                    if (item.isValid()) {
                        result &= item.isShouldReplace();
                    }
                    if (!result) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    private boolean shouldReplaceNone() {
        boolean result = !isShouldReplace();
        if (!result) {
            for (Iterator i = items.iterator(); i.hasNext();) {
                Item item = (Item) i.next();
                if (item.isValid()) {
                    result &= !item.isShouldReplace();
                }
                if (!result) {
                    break;
                }
            }
        }
        return result;
    }

    public void setEntireFileShouldReplace (boolean val) {
        setShouldReplace (val);
    }

    public boolean isEntireFileShouldReplace() {
        return isShouldReplace();
    }

    public void setShouldReplace (boolean val) {
        super.setShouldReplace(val);
        search.observerProxy.fileShouldReplaceChanged(getFile(), val);
    }

    boolean wasCrLf = false;
    private StringBuffer text;
    public String getText() throws IOException {
        StringBuffer txt = text();
        if (txt != null) {
            return txt.toString();
        } else {
            return null;
        }
    }

    private StringBuffer text() throws IOException {
        assert !EventQueue.isDispatchThread();
        checkValid();
        if (text == null) {
            ByteBuffer buf = getByteBuffer();
            if (buf != null) {
                boolean binary = Search.isBinaryFile(getFile());

                //For binary, get us a string of 8-bit characters that will
                //map back to bytes
                Charset charset = binary ? Charset.forName("UTF-8") :
                    Charset.defaultCharset();

                CharBuffer cbuf = charset.decode(buf);
                String terminator = System.getProperty ("line.separator"); //NOI18N
                boolean notNewline = !"\n".equals(terminator); //NOI18N

                if (!binary) {
                    if (notNewline) {
                        Matcher matcher = Pattern.compile(terminator).matcher(cbuf);
                        if (matcher.find() && !Search.isBinaryFile(getFile())) {
                            wasCrLf = true;
                            matcher.reset();
                            text = new StringBuffer(matcher.replaceAll("\n"));
                        } else {
                            text = new StringBuffer(cbuf);
                        }
                    }
                } else {
                    text = new StringBuffer (cbuf);
                }
            }
        }
        return text == null ? new StringBuffer() : text;
    }

    private ByteBuffer getByteBuffer() throws IOException {
        assert !EventQueue.isDispatchThread();
        File file = getFile();
        checkValid();
        //XXX optimize with a single shared bytebuffer if performance
        //problems noted
        FileInputStream str = new FileInputStream (file);

        ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
        FileChannel channel = str.getChannel();
        try {
            channel.read(buffer, 0);
        } catch (ClosedByInterruptException cbie) {
            return null; //this is actually okay
        } finally {
            channel.close();
        }
        buffer.rewind();
        return buffer;
    }

    private void invalid(String reason) throws IOException {
        valid = false;
        search.observerProxy.becameInvalid(getFile(), reason);
        IOException ioe = new IOException (reason);
        ErrorManager.getDefault().annotate (ioe, ErrorManager.USER,
                getFile().getPath(), reason, null, null);
        throw ioe;
    }

    public void checkValid() throws IOException {
        File f = getFile();
        String path = getName();
        if (!f.exists()) {
            invalid(NbBundle.getMessage(OneFileItem.class, "ERR_DELETED", //NOI18N
                    path));
        }
        if (f.isDirectory()) {
            invalid(NbBundle.getMessage(OneFileItem.class, "ERR_BECAME_DIR", //NOI18N
                    path));
        }
        long stamp = f.lastModified();
        if (getTimestamp() != stamp) {
            invalid(NbBundle.getMessage(OneFileItem.class, "ERR_CHANGED", //NOI18N
                    path));
        }
        if (f.length() > Integer.MAX_VALUE) {
            invalid(NbBundle.getMessage(OneFileItem.class, "ERR_TOO_BIG", //NOI18N
                    path));
        }
        if (!f.canRead()) {
            invalid(NbBundle.getMessage(OneFileItem.class, "ERR_CANT_READ", //NOI18N
                    path));
        }
        if (f.length() < search.getSearchText().length()) {
            invalid(NbBundle.getMessage(OneFileItem.class, "ERR_TOO_SHORT", //NOI18N
                    path));
        }
    }

    private void replace(InFileItem item) throws IOException {
        if (isReplaced()) {
            //We've already run the full replace triggered by one of our
            //other items, so just return
            return;
        }
        if (item.isValid() && item.isShouldReplace()) {
            if (shouldReplaceAll()) {
                replace();
                write();
            } else {
                StringBuffer txt = text();
                Point loc = item.getLocation();
                txt.replace(loc.x, loc.y, search.getReplacementText());
                if (item == getFirstReplaceableItem()) {
                    //Committing the 0'th will trigger a write
                    write();
                    setReplaced(true);
                }
            }
        }
    }

    public Item getFirstReplaceableItem() {
        //Get the lowest numbered item in our list of items that is actually set
        //to be replaced - when replace() is called on this one, it should
        //trigger an actual write to disk
        if (items != null) {
            for (Iterator i = items.iterator(); i.hasNext();) {
                Item item = (Item) i.next();
                if (item.isValid() && item.isShouldReplace()) {
                    return item;
                }
            }
        } else {
            return (isValid() && isShouldReplace()) ? this : null;
        }
        return null;
    }

    private void write() throws IOException {
        if (text != null) {
            if (Search.reallyWrite) {
                boolean binary = Search.isBinaryFile(getFile());

                //For binary, get us a string of 8-bit characters that will
                //map back to bytes
                Charset charset = binary ? Charset.forName("UTF-8") :
                    Charset.defaultCharset();

                if (wasCrLf && !binary) {
                    String terminator = System.getProperty ("line.separator"); //NOI18N
                    //XXX use constant - i.e. on mac, only \r, etc.
                    text = new StringBuffer(text.toString().replace("\n", //NOI18N
                           terminator)); //NOI18N
                }
                ByteBuffer buffer = charset.encode(
                        text.toString());

                FileOutputStream fos = new FileOutputStream (getFile());
                FileChannel channel = fos.getChannel();
                channel.write(buffer);
                if (Search.UNIT_TESTING) {
                    channel.force(true);
                }
                channel.close();
                setReplaced(true);
            } else {
                System.err.println("Would write to " + getFile().getPath());
                System.err.println(text);
            }
        } else {
            throw new IllegalStateException ("Buffer is gone");
        }
    }

    public String toString() {
        if (items != null) {
            return "OneFileItem subitems" + items + " in " + getName();
        } else {
            return "OneFileItem @ " + location.x + ":" + location.y
                    + " in " + getName();
        }
    }

    void setReplaced (boolean val) {
        super.setReplaced (val);
        if (items == null) {
            search.observerProxy.replaced(this);
        }
    }

    private class InFileItem extends Item {
        private final Point location;
        InFileItem (Point location) {
            super (null);
            this.location = location;
        }

        long getTimestamp() {
            return OneFileItem.this.getTimestamp();
        }

        public void checkValid() throws IOException {
            OneFileItem.this.checkValid();
        }

        public boolean isValid() {
            return OneFileItem.this.isValid();
        }

        public Point getLocation() {
            return location;
        }

        public File getFile() {
            return OneFileItem.this.getFile();
        }

        public void replace() throws IOException {
            OneFileItem.this.replace(this);
        }

        public String getText() throws IOException {
            return OneFileItem.this.getText();
        }

        public String toString() {
            return "SubItem @ " + location.x + " thru " + location.y +
                    " in " + getName() + " will do crlf conversion "
                    + wasCrLf;
        }

        public void setShouldReplace (boolean val) {
            super.setShouldReplace(val);
            OneFileItem.this.search.observerProxy.shouldReplaceChanged(this,
                    val);
        }

        public boolean isShouldReplace() {
            return OneFileItem.this.isShouldReplace() &&
                    super.isShouldReplace();
        }

        public void setEntireFileShouldReplace (boolean val) {
            OneFileItem.this.setEntireFileShouldReplace(val);
        }

        public boolean isEntireFileShouldReplace() {
            return OneFileItem.this.isShouldReplace();
        }

        void setReplaced (boolean val) {
            if (isReplaced() && !val) {
                throw new IllegalStateException ("Cannot replace twice");
            }
            super.setReplaced(val);
            if (val) {
                OneFileItem.this.search.observerProxy.replaced(this);
            }
        }
    }
}
