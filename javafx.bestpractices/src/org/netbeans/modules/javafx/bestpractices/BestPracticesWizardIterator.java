/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javafx.bestpractices;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.javafx.project.JavaFXProjectGenerator;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.filesystems.URLMapper;
import org.openide.util.NbBundle;

/**
 *
 * @author Michal Skvor
 */
public class BestPracticesWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private WizardDescriptor wizard;
    
    private int index;
    private WizardDescriptor.Panel[] panels;
    
    private FileObject file;

    public BestPracticesWizardIterator( FileObject file ) {
        this.file = file;
    }
    
    public static BestPracticesWizardIterator createIterator( FileObject file ) {
        return new BestPracticesWizardIterator( file );
    }    
    
    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{ new BestPracticesWizardPanel() };
    }
    
    private String[] createSteps() {
        return new String[]{ NbBundle.getMessage( BestPracticesWizardIterator.class, "LBL_CreateProjectStep" )};
    }
        
    public Set instantiate() throws IOException {
        Set<FileObject> resultSet = new LinkedHashSet<FileObject>();
        File dirF = FileUtil.normalizeFile((File) wizard.getProperty( "projdir" ));
       
        FileObject mainFile = Repository.getDefault().getDefaultFileSystem().findResource((String) file.getAttribute( "file" ));
        String path = mainFile.getPath().substring( "demos/".length());
        AntProjectHelper helper = JavaFXProjectGenerator.createProject( dirF, (String)wizard.getProperty( "name" ), null, null );
        EditableProperties props = helper.getProperties( AntProjectHelper.PROJECT_PROPERTIES_PATH );
        
        String srcdir = props.getProperty( "src.dir" );
        FileObject dir = FileUtil.toFileObject( dirF );
        
        FileObject srcDir = dir.getFileObject( srcdir );
        Enumeration<? extends FileObject> subs = mainFile.getFolders( true );
        while( subs.hasMoreElements()) {
            FileObject fo = subs.nextElement();
            FileObject destDirFO = srcDir.getFileObject( fo.getPath().substring( mainFile.getPath().length() + 1 ));
            if( destDirFO == null ) destDirFO = srcDir.createFolder( fo.getPath().substring( mainFile.getPath().length() + 1 ));
            for( FileObject c : fo.getChildren()) {
                FileUtil.copyFile( c, destDirFO, c.getName());
                // If file has mainclass attribute - set it as main 
                if( c.getAttribute( "mainclass" ) != null ) {
                    String fileName = c.getPath().substring( mainFile.getPath().length() + 1 ).replace( "/", "." );
                    fileName = fileName.substring( 0, fileName.length() - 3 );
                            
                    props.setProperty( "main.class", fileName );
                    helper.putProperties( AntProjectHelper.PROJECT_PROPERTIES_PATH, props );        
                }
            }
        }        
        
        // Traverse created directory
        resultSet.add( dir );
        // Look for nested projects to open as well:
        Enumeration<? extends FileObject> e = dir.getFolders( true );
        while( e.hasMoreElements()) {
            FileObject subfolder = e.nextElement();
            if( ProjectManager.getDefault().isProject( subfolder )) {
                resultSet.add( subfolder );
            }
        }
        
        File parent = dirF.getParentFile();
        if( parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder( parent );
        }
        
        return resultSet;
    }

    public void initialize( WizardDescriptor wizard ) {
        this.wizard = wizard;   
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for( int i = 0; i < panels.length; i++ ) {
            Component c = panels[i].getComponent();
            if( steps[i] == null ) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if( c instanceof JComponent ) {
                // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty( "WizardPanel_contentSelectedIndex", new Integer( i ));
                // Step name (actually the whole list for reference).
                jc.putClientProperty( "WizardPanel_contentData", steps );
            }
        }
    }

    public void uninitialize(WizardDescriptor wizard) {
        this.wizard = null;
    }

    public Panel current() {
        return panels[index];
    }

    public String name() {
        return MessageFormat.format( "{0} of {1}", new Object[]{ new Integer( index + 1 ), new Integer( panels.length )});
    }

    public boolean hasNext() {
        return index < panels.length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if( !hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }

}
