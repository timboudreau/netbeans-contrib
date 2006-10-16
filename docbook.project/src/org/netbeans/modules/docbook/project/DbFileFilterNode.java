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
/*
 * DocbookFileNode.java
 *
 * Created on October 14, 2006, 2:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.docbook.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.ErrorManager;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

/**
 *
 * @author Tim Boudreau
 */
public class DbFileFilterNode extends FilterNode implements FileChangeListener, Runnable {
    private final DbProject project;
    private RequestProcessor.Task task;
    /** Creates a new instance of DocbookFileNode */
    public DbFileFilterNode(Node orig, DbProject project, FileObject parentFolder) throws DataObjectNotFoundException {
        super (orig, new ImageChildren (parentFolder));
        this.project = project;
        DataObject ob = (DataObject) orig.getLookup().lookup (DataObject.class);
        FileObject fob = ob.getPrimaryFile();
        fob.addFileChangeListener(WeakListeners.create(
                FileChangeListener.class, this, fob));
        setName (ob.getName());
    }

    private String cachedName = null;
    private final Object lock = new Object();
    public String getDisplayName() {
        String nm = super.getDisplayName();
        if (cachedName == null) {
            synchronized (lock) {
                if (task == null) {
                    task = project.rp.post (this);
                }
            }
            return super.getDisplayName();
        } else {
            String result = NO_NAME.equals(cachedName) ?
                nm : cachedName;
            FileObject ob = ((DataNode) getOriginal()).getDataObject().getPrimaryFile();
            try {
                String nue = ob.getFileSystem().getStatus().annotateName(nm,
                        Collections.singleton(ob));
                result = nue.replace(nm, result);
            } catch (FileStateInvalidException ex) {
                ErrorManager.getDefault().notify(ex);
            }
            return result;
        }
    }

    public String getHtmlDisplayName() {
        FileObject ob = ((DataNode) getOriginal()).getDataObject().getPrimaryFile();
        String origHtml = getOriginal().getHtmlDisplayName();
        String result = null;
        boolean main = ob.equals(project.getMainFile());
        if (main || origHtml != null) {
            result = getDisplayName(); //NOI18N
        }
        if (result != null) {
            try {
                 FileSystem.Status stat =
                     ob.getFileSystem().getStatus();
                 if (stat instanceof FileSystem.HtmlStatus) {
                     FileSystem.HtmlStatus hstat = (FileSystem.HtmlStatus) stat;

                     result = hstat.annotateNameHtml (
                         result, Collections.singleton(ob));

                     if (main) result = "<b>" + result;

                     //Make sure the super string was really modified
                     if (!super.getDisplayName().equals(result)) {
                         return result;
                     }
                 }
            } catch (FileStateInvalidException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return result;
    }

    void cancel() {
        synchronized (lock) {
            if (task != null) {
                task.cancel();
                task = null;
            }
        }
    }

    void updateName() {
        synchronized (lock) {
            if (task == null) {
                task = project.rp.post (this);
            }
        }
    }

    public void fileFolderCreated(FileEvent fe) {
    }

    public void fileDataCreated(FileEvent fe) {
    }

    public void fileChanged(FileEvent fe) {
        updateName();
    }

    public void fileDeleted(FileEvent fe) {
        try {
            destroy();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify (ex);
        }
    }

    public void fileRenamed(FileRenameEvent fe) {
    }

    public void fileAttributeChanged(FileAttributeEvent fe) {
    }

    public String getShortDescription() {
        return super.getDisplayName();
    }

    private static final CharsetDecoder decoder =
            Charset.defaultCharset().newDecoder();

    private static final Pattern TITLE_PATTERN =
            Pattern.compile("<title>\\s*(.*?)\\s*</title>"); //NOI18N

    //<?xml version="1.0" encoding="UTF-8"?>
    private static final String CONTENT_TYPE_PATTERN =
            ".*<\\?xml.*?\\s*encoding=\\s*\\\"(.*?)\\\".*?>"; //NOI18N

    private static final Pattern encodingPattern =
            Pattern.compile(CONTENT_TYPE_PATTERN,
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public void run() {
        synchronized (lock) {
            task = null;
        }
        try {
            DataObject dob = getLookup().lookup (DataObject.class);
            if (dob != null && dob.isValid()) {
                FileObject fob = dob.getPrimaryFile();
                File f = FileUtil.toFile (fob);
                if (!f.exists() || f.length() > Integer.MAX_VALUE) {
                    return;
                }
                if (Thread.interrupted()) {
                    return;
                }
                FileInputStream in = new FileInputStream (f);
                FileChannel channel = in.getChannel();
                try {
                    ByteBuffer buf = ByteBuffer.allocate (
                            (int) f.length());

                    channel.read(buf);
                    buf.flip();
                    if (Thread.interrupted()) {
                        return;
                    }
//                    CharSequence contents = decode(buf);
                    CharSequence contents = decoder.decode(buf);
                    Matcher matcher = TITLE_PATTERN.matcher(contents);
                    String old = getDisplayName();
                    if (matcher.find()) {
                        String nm = matcher.group(1).trim();
                        if (nm.length() > 0) {
                            cachedName = nm;
                        } else {
                            cachedName = NO_NAME;
                        }
                        fireDisplayNameChange(old, getDisplayName());
                    } else {
                        cachedName = NO_NAME;
                    }
                } finally {
                    channel.close();
                }
            }
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify (ErrorManager.INFORMATIONAL, ioe);
        }
    }

    private static final String NO_NAME = "Unknown"; //NOI18N

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
}
