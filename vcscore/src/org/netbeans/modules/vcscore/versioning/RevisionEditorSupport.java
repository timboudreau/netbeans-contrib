/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.versioning;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.datatransfer.Transferable;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.text.MessageFormat;
import java.awt.print.PrinterJob;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterIOException;
import java.awt.print.PrinterAbortException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Component;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.text.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.undo.CannotUndoException;

import org.openide.awt.UndoRedo;
import org.openide.actions.*;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.SaveCookie;
import org.openide.cookies.PrintCookie;
import org.openide.filesystems.*;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeListener;
import org.openide.loaders.*;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.windows.*;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.WeakListener;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** Support for associating an editor and a Swing {@link Document} to a revision object.
 * This is a modification of org.openide.text.DataEditorSupport
 *
 * @author Jaroslav Tulach, Martin Entlicher
 */
public class RevisionEditorSupport extends CloneableEditorSupport implements EditorCookie.Observable, OpenCookie, PrintCookie, CloseCookie {
    private final RevisionList list;
    private final RevisionItem revisionItem;
    /** Which file object we are associated with */
    private final FileObject obj;
    /** Which revision we are associated with */
    private final String revision;
    /** listener to asociated node's events */
    private NodeListener nodeL;
    
    /** Editor support for a given data object. The file is taken from the
     * data object and is updated if the object moves or renames itself.
     * @param obj object to work with
     * @param env environment to pass to 
     */
    public RevisionEditorSupport (RevisionList list, RevisionItem revisionItem/*, CloneableEditorSupport.Env env*/) {
        super (new RevisionEditorSupport.Env(list, revisionItem));
        this.list = list;
        this.obj = list.getFileObject();
        this.revisionItem = revisionItem;
        this.revision = revisionItem.getRevisionVCS();
    }
    
    /** Getter of the file object that this support is associated with.
     * @return file object passed in constructor
     */
    public final FileObject getFileObject () {
        return obj;
    }

    /** Getter of the revision that this support is associated with.
     * @return revision passed in constructor
     */
    public final String getRevision () {
        return revision;
    }

    /** Message to display when an object is being opened.
     * @return the message or null if nothing should be displayed
     */
    protected String messageOpening () {
        return NbBundle.getMessage (RevisionEditorSupport.class , "CTL_ObjectOpen", // NOI18N
            obj.getNameExt(),
            obj.getPackageNameExt('/', '.'),
            revision
        );
    }
    

    /** Message to display when an object has been opened.
    * @return the message or null if nothing should be displayed
    */
    protected String messageOpened () {
        return NbBundle.getMessage (RevisionEditorSupport.class, "CTL_ObjectOpened", // NOI18N
            obj.getNameExt(),
            obj.getPackageNameExt('/', '.'),
            revision
        );
    }

    /** Constructs message that should be displayed when the data object
    * is modified and is being closed.
    *
    * @return text to show to the user
    */
    protected String messageSave () {
        return "";/*NbBundle.getMessage (
            DataEditorSupport.class,
            "MSG_SaveFile", // NOI18N
            obj.getName()
        );*/
    }
    
    /** Constructs message that should be used to name the editor component.
    *
    * @return name of the editor
    */
    protected String messageName () {
        return NbBundle.getMessage(RevisionEditorSupport.class, "LAB_RevisionEditorName", //NOI18N
            obj.getNameExt(),
            revision
        );
        /*
        String name = obj.getNodeDelegate().getDisplayName();
        
        if (obj.getPrimaryFile ().isReadOnly ()) {
            name += NbBundle.getBundle (
                DataEditorSupport.class).getString ("LAB_EditorName_ReadOnly");
        }
        
        if (isModified ()) {
            return NbBundle.getMessage (
                DataEditorSupport.class,
                "LAB_EditorName_Modified",
                name
            );
        } else {
            return NbBundle.getMessage (
                DataEditorSupport.class,
                "LAB_EditorName_Uptodate",
                name
            );
        }
         */
    }
    
    /** Text to use as tooltip for component.
    *
    * @return text to show to the user
    */
    protected String messageToolTip () {
        // update tooltip
        return NbBundle.getMessage (RevisionEditorSupport.class, "LAB_RevisionEditorToolTip", new Object[] {
            obj.getNameExt(),
            obj.getPackageNameExt('/', '.'),
            revision
        });
        /*
        FileObject fo = obj.getPrimaryFile ();
        
        try {
            return NbBundle.getMessage (DataEditorSupport.class, "LAB_EditorToolTip_Valid", new Object[] {
                fo.getPackageName ('.'),
                fo.getName (),
                fo.getExt (),
                fo.getFileSystem ().getDisplayName ()
            });
        } catch (FileStateInvalidException fsie) {
            return NbBundle.getMessage (DataEditorSupport.class, "LAB_EditorToolTip_Invalid", new Object[] {
                fo.getPackageName ('.'),
                fo.getName (),
                fo.getExt ()
            });
        }
         */
    }
    
    /** Annotates the editor with icon from the data object and also sets 
     * appropriate selected node.
     * This implementation also listen to display name and icon chamges of the
     * node and keeps editor top component up-to-date. If you override this
     * method and not call super, please note that you will have to keep things
     * synchronized yourself. 
     *
     * @param editor the editor that has been created and should be annotated
     */
    protected void initializeCloneableEditor (CloneableEditor editor) {
        Node ourNode = list.getNodeDelegate(revisionItem, null);
        editor.setActivatedNodes (new Node[] { ourNode });
        editor.setIcon(ourNode.getIcon (java.beans.BeanInfo.ICON_COLOR_16x16));
        //nodeL = new DataNodeListener(editor);
        //ourNode.addNodeListener(WeakListener.node(nodeL, ourNode));
    }
    
    /** Let's the super method create the document and also annotates it
    * with Title and StreamDescription properities.
    *
    * @param kit kit to user to create the document
    * @return the document annotated by the properties
    */
    protected StyledDocument createStyledDocument (EditorKit kit) {
        StyledDocument doc = super.createStyledDocument (kit);
            
        // set document name property
        doc.putProperty(javax.swing.text.Document.TitleProperty,
            obj.getPackageNameExt('/', '.')
        );
        /* set dataobject to stream desc property
        doc.putProperty(javax.swing.text.Document.StreamDescriptionProperty,
            obj
        );
         */
        return doc;
    }
    
    /** Getter for data object associated with this 
    * data object.
    *
    final DataObject getDataObjectHack () {
        return obj;
    }
     */
    
    /** Environment that connects the data object and the CloneableEditorSupport.
    */
    public static class Env extends Object implements CloneableOpenSupport.Env, CloneableEditorSupport.Env, java.io.Serializable
                                                      /*PropertyChangeListener, VetoableChangeListener*/ {
        /** generated Serialized Version UID */
        static final long serialVersionUID = -2945098431098324441L;

        private transient RevisionList list;
        private transient RevisionItem revisionItem;
        /** The file object this environment is associated to.
        * This file object can be changed by a call to refresh file.
        */
        private transient FileObject fileObject;
        
        private transient String revision;

        /** Lock acquired after the first modification and used in save.
        * Transient => is not serialized.
        */
        //private transient FileLock fileLock;
        
        /** Constructor.
        * @param obj this support should be associated with
        */
        public Env (RevisionList list, RevisionItem revisionItem) {
            this.list = list;
            this.revisionItem = revisionItem;
            this.fileObject = list.getFileObject();
            this.revision = revisionItem.getRevisionVCS();
        }
        
        /** Locks the file.
        * @return the lock on the file getFile ()
        * @exception IOException if the file cannot be locked
        */
        //protected abstract FileLock takeLock () throws IOException;
                
        /** Obtains the input stream.
        * @exception IOException if an I/O error occures
        */
        public InputStream inputStream() throws IOException {
            InputStream is = ((VersioningFileSystem) fileObject.getFileSystem()).
                getVersions().inputStream(fileObject.getPackageNameExt('/', '.'), revision);
            return is;
        }
        
        /** Obtains the output stream.
        * @exception IOException if an I/O error occures
        */
        public OutputStream outputStream() throws IOException {
            throw new IOException("No output to a file revision supported.");
            //return getFileImpl ().getOutputStream (fileLock);
        }
        
        /** Mime type of the document.
        * @return the mime type to use for the document
        */
        public String getMimeType() {
            return fileObject.getMIMEType ();
        }
        
        /** First of all tries to lock the primary file and
        * if it succeeds it marks the data object modified.
        *
        * @exception IOException if the environment cannot be marked modified
        *   (for example when the file is readonly), when such exception
        *   is the support should discard all previous changes
        */
        public void markModified() throws java.io.IOException {
            throw new IOException("The file revision can not be modified.");
            /*
            if (fileLock == null || !fileLock.isValid()) {
                fileLock = takeLock ();
            }

            this.getDataObject ().setModified (true);
             */
        }
        
        /** Reverse method that can be called to make the environment 
        * unmodified.
        */
        public void unmarkModified() {
            //throw new IOException("The file revision can not be unmodified.");
            /*
            if (fileLock != null && fileLock.isValid()) {
                fileLock.releaseLock();
            }
            
            this.getDataObject ().setModified (false);
             */
        }
        
        /** Called from the EnvListener
        * @param expected is the change expected
        * @param time of the change
        *
        final void fileChanged (boolean expected, long time) {
            if (expected) {
                // newValue = null means do not ask user whether to reload
                firePropertyChange (PROP_TIME, null, null);
            } else {
                firePropertyChange (PROP_TIME, null, new Date (time));
            }
        }
         */
        
        public void removePropertyChangeListener(java.beans.PropertyChangeListener propertyChangeListener) {
        }
        
        public boolean isModified() {
            return false;
        }
        
        public java.util.Date getTime() {
            return new java.util.Date(System.currentTimeMillis());
        }
        
        public void removeVetoableChangeListener(java.beans.VetoableChangeListener vetoableChangeListener) {
        }
        
        public boolean isValid() {
            return true;
        }
        
        public void addVetoableChangeListener(java.beans.VetoableChangeListener vetoableChangeListener) {
        }
        
        public void addPropertyChangeListener(java.beans.PropertyChangeListener propertyChangeListener) {
        }
        
        public CloneableOpenSupport findCloneableOpenSupport() {
            return (CloneableOpenSupport) list.getNodeDelegate(revisionItem, null).getCookie(CloneableOpenSupport.class);
        }
        
    } // end of Env
    
    /** Listener on file object that notifies the Env object
    * that a file has been modified.
    *
    private static final class EnvListener extends FileChangeAdapter {
        /** Reference (Env) *
        private Reference env;
        
        /** @param env environement to use
        *
        public EnvListener (Env env) {
            this.env = new java.lang.ref.WeakReference (env);
        }

        /** Fired when a file is changed.
        * @param fe the event describing context where action has taken place
        *
        public void fileChanged(FileEvent fe) {
            Env env = (Env)this.env.get ();
            if (env == null || env.getFileImpl () != fe.getFile ()) {
                // the Env change its file and we are not used
                // listener anymore => remove itself from the list of listeners
                fe.getFile ().removeFileChangeListener (this);
                return;
            }

            env.fileChanged (fe.isExpected (), fe.getTime ());
        }
                
    }
    
    /** Listener on node representing asociated data object, listens to the
     * property changes of the node and updates state properly
     *
    private final class DataNodeListener extends NodeAdapter {
        /** Asociated editor *
        CloneableEditor editor;
        
        DataNodeListener (CloneableEditor editor) {
            this.editor = editor;
        }
        
        public void propertyChange (java.beans.PropertyChangeEvent ev) {
            
            if (Node.PROP_DISPLAY_NAME.equals(ev.getPropertyName())) {
                updateTitles();
            }
            if (Node.PROP_ICON.equals(ev.getPropertyName())) {
                editor.setIcon(
                    getDataObject().getNodeDelegate().getIcon (java.beans.BeanInfo.ICON_COLOR_16x16)
                );
            }
        }
        
    } // end of DataNodeListener
     */
    
}
