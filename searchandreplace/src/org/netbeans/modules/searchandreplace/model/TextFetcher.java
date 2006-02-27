/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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