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
import java.beans.BeanInfo;


import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.libraries.Library;
import org.openide.filesystems.FileObject;

import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;


import org.openide.util.NbBundle;

import org.openide.util.Utilities;

/** Represents classpath items of various types. Can be used in the model
 * of classpath editing controls.
 *
 * @author  Petr Hrebejk, David Konecny
 */
public class VisualClassPathItem {
            
    // Types of the classpath elements
    public static final int TYPE_JAR = 0;
    public static final int TYPE_LIBRARY = 1;
    public static final int TYPE_ARTIFACT = 2;
    public static final int TYPE_CLASSPATH = 3;

    private static String RESOURCE_ICON_JAR = "org/netbeans/modules/groovy/groovyproject/resources/jar.gif"; //NOI18N
    private static String RESOURCE_ICON_LIBRARY = "org/netbeans/modules/groovy/groovyproject/resources/libraries.gif"; //NOI18N
    private static String RESOURCE_ICON_ARTIFACT = "org/netbeans/modules/groovy/groovyproject/resources/projectDependencies.gif"; //NOI18N
    private static String RESOURCE_ICON_CLASSPATH = "org/netbeans/modules/groovy/groovyproject/resources/referencedClasspath.gif"; //NOI18N
    
    private static Icon ICON_JAR = new ImageIcon( Utilities.loadImage( RESOURCE_ICON_JAR ) );
    private static Icon ICON_FOLDER = null; 
    private static Icon ICON_LIBRARY = new ImageIcon( Utilities.loadImage( RESOURCE_ICON_LIBRARY ) );
    private static Icon ICON_ARTIFACT  = new ImageIcon( Utilities.loadImage( RESOURCE_ICON_ARTIFACT ) );
    private static Icon ICON_CLASSPATH  = new ImageIcon( Utilities.loadImage( RESOURCE_ICON_CLASSPATH ) );
    
    private int type;
    private Object cpElement;
    private String raw;
    private String eval;

    VisualClassPathItem( Object cpElement, int type, String raw, String eval ) {
        this.cpElement = cpElement;
        this.type = type;
        this.raw = raw;
        this.eval = eval;
        
        // check cpElement parameter
        if (cpElement != null) {
            switch ( getType() ) {
                case TYPE_JAR:
                    if (!(cpElement instanceof File)) {
                        throw new IllegalArgumentException("File instance must be " + // NOI18N
                            "passed as object for TYPE_JAR. Was: "+cpElement.getClass()); // NOI18N
                    }
                    break;
                case TYPE_LIBRARY:
                    if (!(cpElement instanceof Library)) {
                        throw new IllegalArgumentException("Library instance must be " + // NOI18N
                            "passed as object for TYPE_LIBRARY. Was: "+cpElement.getClass()); // NOI18N
                    }
                    break;
                case TYPE_ARTIFACT:
                    if (!(cpElement instanceof AntArtifact)) {
                        throw new IllegalArgumentException("AntArtifact instance must be " + // NOI18N
                            "passed as object for TYPE_ARTIFACT. Was: "+cpElement.getClass()); // NOI18N
                    }
                    break;
                case TYPE_CLASSPATH:
                    if (!(cpElement instanceof String)) {
                        throw new IllegalArgumentException("String instance must be " + // NOI18N
                            "passed as object for TYPE_CLASSPATH. Was: "+cpElement.getClass()); // NOI18N
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type " + // NOI18N
                        "passed. Was: "+getType()); // NOI18N
            }
        }
    }

    public Object getObject() {
        return cpElement;
    }
    
    public int getType() {
        return type;
    }

    public String getRaw() {
        return raw;
    }

    public String getEvaluated() {
        return eval == null ? getRaw() : eval;
    }
    
    public boolean canDelete() {
        return getType() != TYPE_CLASSPATH;
    }
    
    public Icon getIcon() {
        if (getObject() == null) {
            // Otherwise get an NPE for a broken project.
            return null;
        }
        
        switch( getType() ) {
            case TYPE_JAR:
                if ( ((File)getObject()).isDirectory() ) {
                    return getFolderIcon();
                }
                else {
                    return ICON_JAR;
                }
            case TYPE_LIBRARY:
                return ICON_LIBRARY;
            case TYPE_ARTIFACT:
                return ICON_ARTIFACT;
            case TYPE_CLASSPATH:
                return ICON_CLASSPATH;
            default:
                return null;
        }
         
    }

    public String toString() {
        switch ( getType() ) {
            case TYPE_JAR:
                if (getObject() != null) {
                    return getEvaluated();
                } else {
                    return NbBundle.getMessage(VisualClassPathItem.class, "LBL_MISSING_FILE", getFileRefName(getEvaluated()));
                }
            case TYPE_LIBRARY:
                if (getObject() != null) {
                    return ((Library)this.getObject()).getDisplayName();
                } else {
                    return NbBundle.getMessage(VisualClassPathItem.class, "LBL_MISSING_LIBRARY", getLibraryName(getRaw()));
                }
            case TYPE_ARTIFACT:
                if (getObject() != null) {
                    return getEvaluated();
                } else {
                    return NbBundle.getMessage(VisualClassPathItem.class, "LBL_MISSING_PROJECT", getProjectName(getEvaluated()));
                }
            case TYPE_CLASSPATH:
                return getEvaluated();
            default:
                assert true : "Unknown item type"; // NOI18N
                return getEvaluated();
        }
    }
    
    private String getProjectName(String ID) {
        // something in the form of "${reference.project-name.id}"
        return ID.substring(12, ID.indexOf(".", 12)); // NOI18N
    }
    
    private String getLibraryName(String ID) {
        // something in the form of "${libs.junit.classpath}"
        return ID.substring(7, ID.indexOf(".classpath")); // NOI18N
    }
    
    private String getFileRefName(String ID) {
        // something in the form of "${file.reference.smth.jar}"
        return ID.substring(17, ID.length()-1);
    }
            
    public int hashCode() {
        
        int hash = getType();
        
        switch ( getType() ) {
            case TYPE_ARTIFACT:
                if (getObject() != null) {
                    AntArtifact aa = (AntArtifact)getObject();

                    hash += aa.getType().hashCode();                
                    hash += aa.getScriptLocation().hashCode();
                    hash += aa.getArtifactLocations()[0].hashCode();
                } else {
                    hash += getRaw().hashCode();
                }
                break;
            default:
                if (getObject() != null) {
                    hash += getObject().hashCode();
                } else {
                    hash += getRaw().hashCode();
                }
                break;
        }
        
        return hash;
    }
    
    public boolean equals( Object object ) {
        
        if ( !( object instanceof VisualClassPathItem ) ) {
            return false;
        }
        VisualClassPathItem vcpi = (VisualClassPathItem)object;
        
        if ( getType() != vcpi.getType() ) {
            return false;
        }
        
        switch ( getType() ) {
            case TYPE_ARTIFACT:
                if (getObject() != null) {
                    AntArtifact aa1 = (AntArtifact)getObject();
                    AntArtifact aa2 = (AntArtifact)vcpi.getObject();

                    if ( aa1.getType() != aa2.getType() ) {
                        return false;
                    }

                    if ( !aa1.getScriptLocation().equals( aa2.getScriptLocation() ) ) {
                        return false;
                    }

                    if ( !aa1.getArtifactLocations()[0].equals( aa2.getArtifactLocations()[0] ) ) {
                        return false;
                    }

                    return true;
                } else {
                    return getRaw().equals(vcpi.getRaw());
                }
            default:
                if (getObject() != null) {
                    return getObject().equals(vcpi.getObject());
                } else {
                    return getRaw().equals(vcpi.getRaw());
                }
        }
        
    }

    public static VisualClassPathItem create (Library library) {
        String libraryName = library.getName();
        return new VisualClassPathItem(library,TYPE_LIBRARY,
            "${libs."+libraryName+".classpath}",libraryName); // NOI18N
    }

    public static VisualClassPathItem create (File archiveFile) {
        return new VisualClassPathItem( archiveFile,
                    VisualClassPathItem.TYPE_JAR,
                    null,
                    archiveFile.getPath());
    }

    public static VisualClassPathItem create (AntArtifact artifact) {
        return new VisualClassPathItem( artifact,
                    VisualClassPathItem.TYPE_ARTIFACT,
                    null,
                    artifact.getArtifactLocations()[0].toString() );
    }
    
    private static Icon getFolderIcon() {
        
        if ( ICON_FOLDER == null ) {
            FileObject root = Repository.getDefault().getDefaultFileSystem().getRoot();
            DataFolder dataFolder = DataFolder.findFolder( root );
            ICON_FOLDER = new ImageIcon( dataFolder.getNodeDelegate().getIcon( BeanInfo.ICON_COLOR_16x16 ) );            
        }
        
        return ICON_FOLDER;
   
    }

}
