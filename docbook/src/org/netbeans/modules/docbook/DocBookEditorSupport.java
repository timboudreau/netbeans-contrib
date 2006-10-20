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

package org.netbeans.modules.docbook;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.text.Document;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;

import org.openide.cookies.*;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.CloneableEditorSupport.Pane;
import org.openide.text.DataEditorSupport;
import org.openide.windows.CloneableOpenSupport;
import org.openide.windows.TopComponent;

public class DocBookEditorSupport extends DataEditorSupport implements EditorCookie, OpenCookie, CloseCookie, PrintCookie {

    public DocBookEditorSupport(DocBookDataObject obj) {
        super(obj, new DocBookEnv(obj));
        setMIMEType("text/xml");
    }

    protected boolean notifyModified() {
        if (!super.notifyModified()) {
            return false;
        }
        DocBookDataObject obj = (DocBookDataObject)getDataObject();
        if (obj.getCookie(SaveCookie.class) == null) {
            obj.setModified(true);
            obj.addSaveCookie(new Save());
        }
        return true;
    }

    protected void notifyUnmodified() {
        DocBookDataObject obj = (DocBookDataObject)getDataObject();
        SaveCookie save = (SaveCookie)obj.getCookie(SaveCookie.class);
        if (save != null) {
            obj.removeSaveCookie(save);
            obj.setModified(false);
        }
        super.notifyUnmodified();
    }

    private class Save implements SaveCookie {
        public void save() throws IOException {
            saveDocument();
            getDataObject().setModified(false);
        }
    }

    private static class DocBookEnv extends DataEditorSupport.Env {

        private static final long serialVersionUID = 1L;

        public DocBookEnv(DocBookDataObject obj) {
            super(obj);
        }

        protected FileObject getFile() {
            return getDataObject().getPrimaryFile();
        }

        protected FileLock takeLock() throws IOException {
            return ((DocBookDataObject)getDataObject()).getPrimaryEntry().takeLock();
        }

        public CloneableOpenSupport findCloneableOpenSupport() {
            return (DocBookEditorSupport)getDataObject().getCookie(DocBookEditorSupport.class);
        }

    }

    protected void initializeCloneableEditor(CloneableEditor editor) {
        try {
            super.initializeCloneableEditor(editor);
        } catch (IllegalStateException ise) {
            //Normal during restart if module has created a dataobject, then 
            //immediately been unloaded and reloaded.  The editor toolbar
            //tries to get info from a node whose dataobject was destroyed
            //when the module was unloaded.  Bug is in the Ant Debugger module,
            //which is receiving property changes and not checking validity.
            
            //No worries about initialization not having completed - this 
            //editor is going to be replaced completely anyway, it's being
            //initialized for nothing.
        }
        editor.getEditorPane().setTransferHandler(new TextAndImageTransferHandler(
                editor.getEditorPane()));
    }

}
