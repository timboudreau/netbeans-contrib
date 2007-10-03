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

package org.netbeans.modules.vcscore.registry;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.Serializable;

import org.openide.filesystems.FileSystem;

/**
 * Information of a registered filesystem.
 *
 * @author  Martin Entlicher
 */
public interface FSInfo extends Serializable {

    public static final String PROP_ROOT = "fSRoot"; // NOI18N
    public static final String PROP_TYPE = "displayType"; // NOI18N
    public static final String PROP_ICON = "icon"; // NOI18N
    public static final String PROP_CONTROL="control";//NOI18N
    
    /**
     * Get the root of the filesystem.
     */
    public File getFSRoot();
    
    /**
     * Get the type of the filesystem, that can be displayed as an additional
     * information.
     */
    public String getDisplayType();
    
    /**
     * Get the icon, that can be used to visually present the filesystem.
     */
    public Image getIcon();
    
    /**
     * Get the control state. True when filesystem is under vcs control.
     */
    public boolean isControl();    
    
    /**
     * Determine whether filesystem should be under vcs control or not.
     * @throws IllegalStateException when the filesystem can not be enabled
     *         (e.g. it's module is disabled/not present).
     */
    public void setControl(boolean value) throws IllegalStateException;
   
    /**
     * Get the filesystem instance. This method should create the filesystem
     * if necessary. If the filesystem is still in use, return the same instance.
     * When null is returned, this FS info is discarded.
     * @return The filesystem instance or <code>null</code>, when the filesystem
     *         can not be retrieved or is no longer valid (e.g. it's setting was lost).
     */
    public FileSystem getFileSystem();
    
    /**
     * Get the existing filesystem instance. No instances are created, the existing
     * filesystem instance is retunted, if any, otherwise <code>null</code>.
     */
    public FileSystem getExistingFileSystem();
    
    /**
     * Destroy this filesystem info. This method is called when it's known
     * that the FSInfo is no longer needed and will be discarded.
     * This method should cleanup the filesystem, if necessary.
     */
    public void destroy();
    
    public void addVetoableChangeListener(VetoableChangeListener l);
    
    public void removeVetoableChangeListener(VetoableChangeListener l);
    
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    public void removePropertyChangeListener(PropertyChangeListener l);

}
