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
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

/**
 * Fetches text from an Item off the event thread and passes it to a
 * TextReceiever on the event thread.
 *
 * @author Tim Boudreau
 */
final class TextFetcher implements Runnable {
    private final Item source;
    private final TextReceiver receiver;
    private Point location;
    private String text = null;
    private final RequestProcessor.Task task;
    public TextFetcher(Item source, TextReceiver receiver, RequestProcessor rp) {
        this.source = source;
        this.receiver = receiver;
        this.location = source.getLocation();
        task = rp.post(this, 50);
    }

    private boolean cancelled = false;
    void cancel() {
        cancelled = true;
        task.cancel();
    }

    public void run() {
        if (EventQueue.isDispatchThread()) {
            if (cancelled) {
                return;
            }
            Point p = getLocation();
            FileObject fob = FileUtil.toFileObject(source.getFile());
            String mimeType = fob.getMIMEType();
            //We don't want the swing html editor kit, and even if we 
            //do get it, it will frequently throw a random NPE 
            //in StyleSheet.removeHTMLTags that appears to be a swing bug
            if ("text/html".equals(mimeType)) {
                mimeType = "text/plain";
            }
            receiver.setText(text, fob.getMIMEType(), getLocation());
            done = true;
        }  else {
            if (Thread.interrupted()) {
                return;
            }
            try {
                text = source.getText();
            } catch (ClosedByInterruptException cbie) {
                cancelled = true;
                return;
            } catch (IOException ioe) {
                text = ioe.getLocalizedMessage();
//                cancel();
            }
            if (Thread.interrupted()) {
                return;
            }
            EventQueue.invokeLater(this);
        }
    }
    boolean done = false;

    /**
     * If a new request comes to display the same file, just possibly at a
     * different location, simply change the location we're scheduled to
     * display and return true, else return false (in which case we'll be
     * cancelled and a new request will be scheduled).
     */
    public boolean replaceLocation(Item nue, TextReceiver receiver) {
        if (done || receiver != this.receiver) {
            return false;
        }
        boolean result = source.getFile().equals(nue.getFile());
        if (result) {
            setLocation (nue.getLocation());
            task.schedule(50);
        }
        return result;
    }

    private synchronized void setLocation(Point loc) {
        this.location = loc;
    }

    private synchronized Point getLocation() {
        return new Point(location);
    }
}