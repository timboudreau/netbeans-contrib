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

package org.netbeans.modules.groovy.groovyproject.ui.customizer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;

import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.modules.groovy.groovyproject.ui.FoldersListSettings;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/** Handles adding, removing, editing and reordering of classpath.
 *
 * @author Petr Hrebejk
 */
final class VisualClasspathSupport {
    
    final Project master;
    final JList classpathList;
    final JButton addJarButton;
    final JButton addLibraryButton;
    final JButton addArtifactButton;
    final JButton editButton;
    final JButton removeButton;
    final JButton upButton;
    final JButton downButton;
    
    private final DefaultListModel classpathModel;
    
    private final ArrayList actionListeners = new ArrayList();
    
    public VisualClasspathSupport(  Project master,
                                    JList classpathList,
                                    JButton addJarButton,
                                    JButton addLibraryButton,
                                    JButton addArtifactButton,
                                    JButton editButton,
                                    JButton removeButton,
                                    JButton upButton,
                                    JButton downButton ) {
        // Remember all buttons                               
        this.classpathList = classpathList;
        this.classpathModel = new DefaultListModel();
        this.classpathList.setModel( classpathModel );
        this.classpathList.setCellRenderer( new ClassPathCellRenderer() );
        
        this.addJarButton = addJarButton;
        this.addLibraryButton = addLibraryButton;
        this.addArtifactButton = addArtifactButton;
        this.editButton = editButton;
        this.removeButton = removeButton;
        this.upButton = upButton;
        this.downButton = downButton;
        
        this.master = master;
        
        // Register the listeners
        ClasspathSupportListener csl = new ClasspathSupportListener();
        
        // On all buttons
        addJarButton.addActionListener( csl ); 
        addLibraryButton.addActionListener( csl );
        addArtifactButton.addActionListener( csl );
        editButton.addActionListener( csl );
        removeButton.addActionListener( csl );
        upButton.addActionListener( csl );
        downButton.addActionListener( csl );
        // On list selection
        classpathList.getSelectionModel().addListSelectionListener( csl );
        
        // Set the initial state of the buttons
        csl.valueChanged( null );
        
    } 
    
    public void setVisualClassPathItems( List items ) {
        
        classpathModel.clear();        
        for( Iterator it = items.iterator(); it.hasNext(); ) {
            VisualClassPathItem cpItem = (VisualClassPathItem)it.next();
            classpathModel.addElement( cpItem );
        }
    }
    
    public List getVisualClassPathItems() {
        
        ArrayList items = new ArrayList();
        for( Enumeration e = classpathModel.elements(); e.hasMoreElements(); ) {
            VisualClassPathItem cpItem = (VisualClassPathItem)e.nextElement();
            items.add( cpItem );
        }
        
        return items;
    } 
    
    /** Action listeners will be informed when the value of the
     * list changes.
     */
    public void addActionListener( ActionListener listener ) {
        actionListeners.add( listener );
    }
    
    public void removeActionListener( ActionListener listener ) {
        actionListeners.remove( listener );
    }
    
    private void fireActionPerformed() {
           
        ArrayList listeners;
        
        synchronized ( this ) {
             listeners = new ArrayList( actionListeners );
        }
        
        ActionEvent ae = new ActionEvent( this, 0, null );
        
        for( Iterator it = listeners.iterator(); it.hasNext(); ) {
            ActionListener al = (ActionListener)it.next();
            al.actionPerformed( ae );
        }
        
    }
        
    // Private methods ---------------------------------------------------------

    private void addLibraries (Library[] libraries, Set/*<Library>*/ alreadyIncludedLibs) {
        int[] si = classpathList.getSelectedIndices();
        int lastIndex = si == null || si.length == 0 ? -1 : si[si.length - 1];
        for (int i = 0, j=1; i < libraries.length; i++) {
            if (!alreadyIncludedLibs.contains(libraries[i])) {
                classpathModel.add(lastIndex + j++, VisualClassPathItem.create(libraries[i]));
            }
        }
        Set addedLibs = new HashSet (Arrays.asList(libraries));
        int[] indexes = new int[libraries.length];
        for (int i=0, j=0; i<classpathModel.getSize(); i++) {
            VisualClassPathItem vcpi = (VisualClassPathItem) classpathModel.get (i);
            if (vcpi.getType() == VisualClassPathItem.TYPE_LIBRARY) {
                if (addedLibs.contains(vcpi.getObject())) {
                    indexes[j++] =i;
                }
            }
        }
        this.classpathList.setSelectedIndices(indexes);
        fireActionPerformed();
    }

    private void addJarFiles( File files[] ) {
        
        int[] si = classpathList.getSelectedIndices();
        
        int lastIndex = si == null || si.length == 0 ? -1 : si[si.length - 1];
        int[] indexes = new int[files.length];
        for( int i = 0; i < files.length; i++ ) {
            int current = lastIndex + 1 + i;
            classpathModel.add( current, VisualClassPathItem.create(files[i]));
            indexes[i] = current;
        }
        this.classpathList.setSelectedIndices(indexes);
        fireActionPerformed();
    }
    
    private void addArtifacts( AntArtifact artifacts[] ) {
        
        int[] si = classpathList.getSelectedIndices();
        
        int lastIndex = si == null || si.length == 0 ? -1 : si[si.length - 1];
        int[] indexes = new int[artifacts.length];
        for( int i = 0; i < artifacts.length; i++ ) {
            int current = lastIndex + 1 + i;
            classpathModel.add(current,VisualClassPathItem.create(artifacts[i]));
            indexes[i] = current;
        }
        this.classpathList.setSelectedIndices(indexes);
        fireActionPerformed();

    }
    
    private void removeElements() {
        
        int[] si = classpathList.getSelectedIndices();
        
        if(  si == null || si.length == 0 ) {
            assert false : "Remove button should be disabled"; // NOI18N
        }
        
        // Remove the items
        for( int i = si.length - 1 ; i >= 0 ; i-- ) {
            classpathModel.remove( si[i] );
        }
        
        
        if ( !classpathModel.isEmpty() ) {
            // Select reasonable item
            int selectedIndex = si[si.length - 1] - si.length  + 1; 
            if ( selectedIndex > classpathModel.size() - 1) {
                selectedIndex = classpathModel.size() - 1;
            }
            classpathList.setSelectedIndex( selectedIndex );
        }
        
        fireActionPerformed();

    }
    
    private void moveUp() {
        
        int[] si = classpathList.getSelectedIndices();
        
        if(  si == null || si.length == 0 ) {
            assert false : "MoveUp button should be disabled"; // NOI18N
        }
        
        // Move the items up
        for( int i = 0; i < si.length; i++ ) {
            Object item = classpathModel.get( si[i] );
            classpathModel.remove( si[i] );
            classpathModel.add( si[i] - 1, item ); 
        }
        
        // Keep the selection a before
        for( int i = 0; i < si.length; i++ ) {
            si[i] -= 1;
        }        
        classpathList.setSelectedIndices( si );
        
        fireActionPerformed();
    } 
    
    private void moveDown() {
        
        int[] si = classpathList.getSelectedIndices();
        
        if(  si == null || si.length == 0 ) {
            assert false : "MoveDown button should be disabled"; // NOI18N
        }
        
        // Move the items up
        for( int i = si.length -1 ; i >= 0 ; i-- ) {
            Object item = classpathModel.get( si[i] );
            classpathModel.remove( si[i] );
            classpathModel.add( si[i] + 1, item ); 
        }
        
        // Keep the selection a before
        for( int i = 0; i < si.length; i++ ) {
            si[i] += 1;
        }
        classpathList.setSelectedIndices( si );
        
        
        fireActionPerformed();
    }
    
    
    // Private innerclasses ----------------------------------------------------
    
    private class ClasspathSupportListener implements ActionListener, ListSelectionListener {
     
        // Implementation of ActionListener ------------------------------------
        
        /** Handles button events
         */        
        public void actionPerformed( ActionEvent e ) {
            
            Object source = e.getSource();
            
            if ( source == addJarButton ) { 
                
                // Let user search for the Jar file
                JFileChooser chooser = new JFileChooser();
                FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
                chooser.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
                chooser.setMultiSelectionEnabled( true );
                chooser.setDialogTitle( NbBundle.getMessage( CustomizerCompile.class, "LBL_AddJar_DialogTitle" ) ); // NOI18N
                chooser.setFileFilter( new SimpleFileFilter( 
                    NbBundle.getMessage( VisualClasspathSupport.class, "LBL_ZipJarFolderFilter" ),                  // NOI18N
                    new String[] {"ZIP","JAR"} ) );                                                                 // NOI18N 
                chooser.setAcceptAllFileFilterUsed( false );
                File curDir = FoldersListSettings.getDefault().getLastUsedClassPathFolder(); 
                chooser.setCurrentDirectory (curDir);
                int option = chooser.showOpenDialog( SwingUtilities.getWindowAncestor( addJarButton ) ); // Sow the chooser
                
                if ( option == JFileChooser.APPROVE_OPTION ) {
                    
                    File files[] = chooser.getSelectedFiles();
                    addJarFiles( files );
                    curDir = FileUtil.normalizeFile(chooser.getCurrentDirectory());
                    FoldersListSettings.getDefault().setLastUsedClassPathFolder(curDir);
                }
                
            }
            else if ( source == addLibraryButton ) {
                Set/*<Library>*/includedLibraries = new HashSet ();
                for (int i=0; i< classpathModel.getSize(); i++) {
                    VisualClassPathItem vcpi = (VisualClassPathItem) classpathModel.get(i);
                    if (vcpi.getType() == VisualClassPathItem.TYPE_LIBRARY) {
                        includedLibraries.add (vcpi.getObject());
                    }
                }
                Object[] options = new Object[] {
                    new JButton (NbBundle.getMessage (VisualClasspathSupport.class,"LBL_AddLibrary")),
                    DialogDescriptor.CANCEL_OPTION
                };
                ((JButton)options[0]).setEnabled(false);
                ((JButton)options[0]).getAccessibleContext().setAccessibleDescription (NbBundle.getMessage (VisualClasspathSupport.class,"AD_AddLibrary"));
                LibrariesChooser panel = new LibrariesChooser ((JButton)options[0], includedLibraries);
                DialogDescriptor desc = new DialogDescriptor(panel,NbBundle.getMessage( VisualClasspathSupport.class, "LBL_CustomizeCompile_Classpath_AddLibrary" ),
                    true, options, options[0], DialogDescriptor.DEFAULT_ALIGN,null,null);
                Dialog dlg = DialogDisplayer.getDefault().createDialog(desc);
                dlg.setVisible(true);
                if (desc.getValue() == options[0]) {
                   addLibraries (panel.getSelectedLibraries(), includedLibraries);
                }
                dlg.dispose();
            }
            else if ( source == addArtifactButton ) { 
                AntArtifact artifacts[] = AntArtifactChooser.showDialog(JavaProjectConstants.ARTIFACT_TYPE_JAR, master);
                if ( artifacts != null ) {
                    addArtifacts( artifacts );
                }
            }
            else if ( source == removeButton ) { 
                removeElements();
            }
            else if ( source == upButton ) {
                moveUp();
            }
            else if ( source == downButton ) {
                moveDown();
            }
        }
        
        // ListSelectionModel --------------------------------------------------
        
        /** Handles changes in the selection
         */        
        public void valueChanged( ListSelectionEvent e ) {
            
            int[] si = classpathList.getSelectedIndices();
            
            // addJar allways enabled
            
            // addLibrary allways enabled
            
            // addArtifact allways enabled
            
            // edit enabled only if selection is not empty
            boolean edit = si != null && si.length > 0;            

            // remove enabled only if selection is not empty
            boolean remove = si != null && si.length > 0;
            // and when the selection does not contain unremovable item
            if ( remove ) {
                for ( int i = 0; i < si.length; i++ ) {
                    assert si[i] < classpathModel.getSize () : "The selected indices " + Arrays.asList (Utilities.toObjectArray (si)) + // NOI18N
                                                                " at " + i +  // NOI18N
                                                                " must fit into size of classpathModel" + classpathModel.getSize (); // NOI18N
                    VisualClassPathItem vcpi = (VisualClassPathItem)classpathModel.get( si[i] );
                    if ( !vcpi.canDelete() ) {
                        remove = false;
                        break;
                    }
                }
            }
                        
            // up button enabled if selection is not empty
            // and the first selected index is not the first row
            boolean up = si != null && si.length > 0 && si[0] != 0;
            
            // up button enabled if selection is not empty
            // and the laset selected index is not the last row
            boolean down = si != null && si.length > 0 && si[si.length-1] != classpathModel.size() - 1;            
            
            editButton.setEnabled( edit );
            removeButton.setEnabled( remove );
            upButton.setEnabled( up );
            downButton.setEnabled( down );       
            
            
            //System.out.println("Selection changed " + edit + ", " + remove + ", " +  + ", " + + ", ");
            
        }
        
    }
    
    
    private static class ClassPathCellRenderer extends DefaultListCellRenderer {
        
        
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );            
            setIcon( ((VisualClassPathItem)value).getIcon() );
            setToolTipText( value.toString() );
            
            return this;
        }
        
    }
    
    
    private static class SimpleFileFilter extends FileFilter {

        private String description;
        private Collection extensions;


        public SimpleFileFilter (String description, String[] extensions) {
            this.description = description;
            this.extensions = Arrays.asList(extensions);
        }

        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            String name = f.getName();
            int index = name.lastIndexOf('.');   //NOI18N
            if (index <= 0 || index==name.length()-1)
                return false;
            String extension = name.substring (index+1).toUpperCase();
            return this.extensions.contains(extension);
        }

        public String getDescription() {
            return this.description;
        }
    }

}
