/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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