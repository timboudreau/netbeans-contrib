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
