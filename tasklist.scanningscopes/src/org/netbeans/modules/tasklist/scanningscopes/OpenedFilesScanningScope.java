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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.tasklist.scanningscopes;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import org.netbeans.spi.tasklist.TaskScanningScope;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * The scanning scope are all opened files.
 * 
 * @author S. Aubrecht
 */
public class OpenedFilesScanningScope extends TaskScanningScope 
        implements PropertyChangeListener, Runnable {
    
    private Collection<FileObject> editedFiles;
    private Callback callback;
    private InstanceContent lookupContent = new InstanceContent();
    private Lookup lookup;
    
    /** Creates a new instance of OpenedFilesScanningScope */
    public OpenedFilesScanningScope( String displayName, String description, Image icon ) {
        super( displayName, description, icon );
    }
    
    public static OpenedFilesScanningScope create() {
        return new OpenedFilesScanningScope(
                NbBundle.getBundle( OpenedFilesScanningScope.class ).getString( "LBL_OpenedFilesScope" ), //NOI18N)
                NbBundle.getBundle( OpenedFilesScanningScope.class ).getString( "HINT_OpenedFilesScope" ), //NOI18N
                Utilities.loadImage( "org/netbeans/modules/tasklist/scanningscopes/opened_files_scope.png" ) //NOI18N
                );
    }
    
    public Iterator<FileObject> iterator() {
        return null == editedFiles 
                ? new ArrayList<FileObject>(0).iterator() 
                : editedFiles.iterator();
    }
    
    @Override
    public boolean isInScope( FileObject resource ) {
        if( null == resource )
            return false;
        return null != editedFiles && editedFiles.contains( resource );
    }
    
    public Lookup getLookup() {
        if( null == lookup ) {
            lookup = new AbstractLookup( lookupContent );
        }
        return lookup;
    }
    
    public void attach( Callback newCallback ) {
        if( null != newCallback && null == callback ) {
            WindowManager.getDefault().getRegistry().addPropertyChangeListener( this );
        } else if( null == newCallback && null != callback ) {
            WindowManager.getDefault().getRegistry().removePropertyChangeListener( this );
            if (null != editedFiles) {
                for( FileObject fo : editedFiles )
                    lookupContent.remove(fo);
            }
            editedFiles = null;
        }
        if( null != newCallback && newCallback != this.callback ) {
            this.callback = newCallback;
            if( SwingUtilities.isEventDispatchThread() ) {
                run();
            } else {
                SwingUtilities.invokeLater( this );
            }
        }
        this.callback = newCallback;
    }
    
    public void propertyChange( PropertyChangeEvent e ) {
        if( TopComponent.Registry.PROP_ACTIVATED_NODES.equals( e.getPropertyName() )
            || TopComponent.Registry.PROP_OPENED.equals( e.getPropertyName() ) ) {
            
            run();
        }
    }
    
    public void run() {
        Collection<FileObject> currentFiles = Utils.collectEditedFiles();
        if( (null == currentFiles && null != editedFiles)
            || (null != currentFiles && null == editedFiles )
            || (null != currentFiles && null != editedFiles 
                && !equalCollections( currentFiles, editedFiles)) ) {

            if( null != editedFiles ) {
                for( FileObject fo : editedFiles )
                    lookupContent.remove( fo );
            }
            if( null != currentFiles ) {
                for( FileObject fo : currentFiles )
                    lookupContent.add( fo );
            }
            editedFiles = currentFiles;
            //notify the TaskManager that user activated other file
            if( null != callback )
                callback.refresh();
        } else {
            editedFiles = currentFiles;
        }
    }
    
    private boolean equalCollections( Collection<FileObject> c1, Collection<FileObject> c2 ) {
        if( c1.size() != c2.size() )
            return false;
        for( FileObject fo : c1 ) {
            if( !c2.contains( fo ) ) 
                return false;
        }
        return true;
    }
}
