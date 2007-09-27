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
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.filesystems.FileChangeListener;

/**
 * The scanning scope are files in the same folder as the currently active editor.
 * 
 * @author S. Aubrecht
 */
public class CurrentFolderScanningScope extends TaskScanningScope 
        implements PropertyChangeListener, Runnable, FileChangeListener {
    
    private FileObject currentFolder = null;
    private Collection<FileObject> editedFiles = null;
    private Callback callback;
    private InstanceContent lookupContent = new InstanceContent();
    private Lookup lookup;
    
    /** Creates a new instance of CurrentFolderScanningScope */
    public CurrentFolderScanningScope( String displayName, String description, Image icon ) {
        super( displayName, description, icon );
    }
    
    public static CurrentFolderScanningScope create() {
        return new CurrentFolderScanningScope(
                NbBundle.getBundle( CurrentFolderScanningScope.class ).getString( "LBL_CurrentFolderScope" ), //NOI18N)
                NbBundle.getBundle( CurrentFolderScanningScope.class ).getString( "HINT_CurrentFolderScope" ), //NOI18N
                Utilities.loadImage( "org/netbeans/modules/tasklist/scanningscopes/current_folder_scope.png" ) //NOI18N
                );
    }
    
    public Iterator<FileObject> iterator() {
        if( null != currentFolder )
            return new FileObjectIterator( currentFolder, editedFiles );
        return new EmptyIterator();
    }
    
    @Override
    public boolean isInScope( FileObject resource ) {
        if( null == resource )
            return false;
        return null != currentFolder && currentFolder.equals( resource.getParent() );
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
            if (null != currentFolder) {
                currentFolder.removeFileChangeListener( this );
                lookupContent.remove(currentFolder);
            }
            currentFolder = null;
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
            || TopComponent.Registry.PROP_OPENED.equals( e.getPropertyName() )
            || TopComponent.Registry.PROP_ACTIVATED.equals( e.getPropertyName() ) ) {
            
            run();
        }
    }
    
    public void run() {
        FileObject newActiveFolder = getCurrentFolder();
        if( (null == currentFolder && null != newActiveFolder)
            || (null != currentFolder && null == newActiveFolder )
            || (null != currentFolder && null != newActiveFolder 
                && !currentFolder.equals(newActiveFolder)) ) {

            if( null != currentFolder ) {
                currentFolder.removeFileChangeListener( this );
                lookupContent.remove( currentFolder );
            }
            if( null != newActiveFolder ) {
                newActiveFolder.addFileChangeListener( this );
                lookupContent.add( newActiveFolder );
            }
            currentFolder = newActiveFolder;
            //notify the TaskManager that user activated other file
            if( null != callback )
                callback.refresh();
        } else {
            currentFolder = newActiveFolder;
        }
        editedFiles = Utils.collectEditedFiles();
    }
    
    private FileObject getCurrentFolder() {
        TopComponent.Registry registry = TopComponent.getRegistry();
        
        TopComponent activeTc = registry.getActivated();
        FileObject newFolder = getFolderFromTopComponent( activeTc );
        
        ArrayList<FileObject> availableFolders = new ArrayList<FileObject>(3);
        if( null == newFolder ) {
            Collection<TopComponent> openedTcs = new ArrayList<TopComponent>( registry.getOpened());
            for( Iterator i=openedTcs.iterator(); i.hasNext(); ) {
                TopComponent tc = (TopComponent)i.next();
                
                FileObject folder = getFolderFromTopComponent( tc );
                if( null != folder ) {
                    availableFolders.add( folder );
                }
            }
            if( null != currentFolder && (availableFolders.contains( currentFolder ) ) )
                newFolder = currentFolder;
            else if( availableFolders.size() > 0 )
                newFolder = availableFolders.get( 0 );
        }
        return newFolder;
    }
    
    private FileObject getFolderFromTopComponent( final TopComponent tc ) {
        if( null == tc || !tc.isShowing() )
            return null;
        if( WindowManager.getDefault().isOpenedEditorTopComponent( tc ) ) {
            DataObject dob = tc.getLookup().lookup( DataObject.class );
            if( null != dob ) {
                FileObject file = dob.getPrimaryFile();
                if( null != file )
                    return file.getParent();
            }
        }
        return null;
    }

    public void fileFolderCreated(FileEvent fe) {
        //ignore
    }

    public void fileDataCreated(FileEvent fe) {
        if( null != callback )
            callback.refresh();
    }

    public void fileChanged(FileEvent fe) {
        //ignore
    }

    public void fileDeleted(FileEvent fe) {
        if( null != callback )
            callback.refresh();
    }

    public void fileRenamed(FileRenameEvent fe) {
        if( null != callback )
            callback.refresh();
    }

    public void fileAttributeChanged(FileAttributeEvent fe) {
        //ignore
    }
}
