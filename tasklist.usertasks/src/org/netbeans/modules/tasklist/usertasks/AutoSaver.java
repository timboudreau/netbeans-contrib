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

package org.netbeans.modules.tasklist.usertasks;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.logging.Level;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import net.fortuna.ical4j.model.ValidationException;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.translators.ICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 * Automatic saving for .ics files.
 *
 * @author Tor Norbye
 * @author tl
 */
public class AutoSaver {
    private UserTaskList utl;
    private EventListenerList listeners = new EventListenerList();

    /** 
     * Has the options set changed such that we need to save 
     */
    private boolean modified = false;
        
    /** File being shown in this tasklist */
    private FileObject file = null;
    
    /** 
     * Timer which keeps track of outstanding save requests - that way
     * deleting multiple items for example will not cause multiple saves. 
     */
    private Timer runTimer = null;
    
    private boolean enabled = false;
    private DataObject do_;

    /** 
     * Creates a new instance of AutoSaver.
     *
     * @param utl a task list
     * @param file target file
     */
    public AutoSaver(UserTaskList utl, FileObject file) {
        try {
            this.do_ = DataObject.find(file);
        } catch (DataObjectNotFoundException e) {
            UTUtils.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        this.utl = utl;
        this.file = file;
        this.utl.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
                do_.setModified(true);
                if (modified != true) {
                    UTUtils.LOGGER.fine("modified = true"); // NOI18N
                    modified = true;
                    fireChange();
                }
                if (enabled)
                    scheduleWrite();
            }
        });
    }
    
    /**
     * Is automatic saving enabled?
     *
     * @return true = yes
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Enable/disable automatic saving.
     *
     * @param enabled true = save automatically
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /** 
     * Location of the tasklist 
     */
    public FileObject getFile() {
        return file;
    }
    
    /** 
     * Schedule a document save 
     */
    private void scheduleWrite() {
        // Stop our current timer; the previous node has not
        // yet been scanned; too brief an interval
	if (runTimer != null) {
	    runTimer.stop();
	    runTimer = null;
	}
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                runTimer = null;
                try {
                    save();
                } catch (IOException ioe) {
                    DialogDisplayer.getDefault().notify(new Message(ioe,
                                                                    NotifyDescriptor.ERROR_MESSAGE));
                }
            }
        };
        // 0.3 second delay
	runTimer = new Timer(300, al);
	runTimer.setRepeats(false);
	runTimer.setCoalesce(true);
	runTimer.start();
    }

    /**
     * Was the task list modified since last saving?
     *
     * @return true = modified
     */
    public boolean isModified() {
        return modified;
    }
    
    /** 
     * Write the list to iCal.
     */
    public void save() throws IOException {
        ICalExportFormat io = new ICalExportFormat();
        
        FileLock lock = this.file.lock();
        try {
            Writer w = new OutputStreamWriter(new BufferedOutputStream(
                    file.getOutputStream(lock)), "UTF-8");;
            try {
                io.writeList(utl, w, false);
            } catch (ParseException e) {
                throw new IOException(e.getMessage());
            } catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            } catch (ValidationException e) {
                throw new IOException(e.getMessage());
            } finally {
                try {
                    w.close();
                } catch (IOException e) {
                    UTUtils.LOGGER.log(Level.WARNING, 
                            "failed closing file", e);
                }
            }
        } finally {
            lock.releaseLock();
        }

        // Remove permissions for others on the file when on Unix
        // varieties
        if (new File("/bin/chmod").exists()) { // NOI18N
            try {
                Runtime.getRuntime().exec(
                     new String[] {"/bin/chmod", "go-rwx",  // NOI18N
                         FileUtil.toFile(this.file).getAbsolutePath()});
            } catch (Exception e) {
                // Silently accept
                UTUtils.LOGGER.log(Level.INFO, 
                        "chmod call failed", e); // NOI18N
            }
        }

        if (modified != false) {
            modified = false;
            do_.setModified(false);
            UTUtils.LOGGER.fine("modified = true"); // NOI18N
            fireChange();
        }
    }
    
    /**
     * Returns the task list associated with this AutoSaver.
     *
     * @return task list
     */
    public UserTaskList getUserTaskList() {
        return utl;
    }
    
    
    /**
     * Adds a change listener. The listener will be notified whenever the
     * modified flag changes.
     *
     * @param l a listener
     */
    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a change listener.
     *
     * @param l a listener.
     */
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }
    
    /**
     * Fires a ChangeEvent
     */
    void fireChange() {
        // Guaranteed to return a non-null array
        Object[] list = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        ChangeEvent changeEvent = null;
        for (int i = list.length - 2; i >= 0; i -= 2) {
            if (list[i] == ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener) list[i+1]).stateChanged(changeEvent);
            }
        }
    }
}
