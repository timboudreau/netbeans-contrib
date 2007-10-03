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

package org.netbeans.modules.vcs.advanced;

import java.awt.Color;
import java.awt.Image;
import org.netbeans.api.vcs.FileStatusInfo;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;

/**
 * The implementation of FileStatusInfo for command-line VCS filesystem.
 *
 * @author  Martin Entlicher
 */
public class CommandLineFileStatusInfo extends FileStatusInfo implements javax.swing.colorchooser.ColorSelectionModel {

    private String displayName;
    private String shortDisplayName;
    private Image icon;
    private FileStatusInfo repInfo;
    private Color color;
    
    /**
     * Creates a new instance of CommandLineFileStatusInfo.
     * @param name The name of this status.
     * @param displayName The display name of this status.
     * @param shortDisplayName The short variant of the display name of this status.
     *                         It can be <code>null</code> when no short variant
     *                         exists.
     */
    public CommandLineFileStatusInfo(String name, String displayName,
                                     String shortDisplayName) {
        super(name);
        this.displayName = displayName;
        if (shortDisplayName == null) {
            this.shortDisplayName = displayName;
        } else {
            this.shortDisplayName = shortDisplayName;
        }
    }
    
    /**
     * Get the localized string representation of this status info. Used for
     * displaying purposes.
     * @return The localized status representation.
     */
    public String getDisplayName() {

        boolean shortForm = false;
        GeneralVcsSettings settings = (GeneralVcsSettings) GeneralVcsSettings.findObject(GeneralVcsSettings.class);
        if (settings != null) {
            shortForm = settings.getFileAnnotation() == GeneralVcsSettings.FILE_ANNOTATION_SHORT;
        }

        if (shortForm) {
            return shortDisplayName;
        } else {
            return displayName;
        }
    }
    
    /**
     * Get the icon for this status info.
     * @return The icon representing this status info or
     *         <code>null</code> when there is no icon.
     */
    public Image getIcon() {
        return icon;
    }
    
    /**
     * Set the icon for this status info.
     * @param icon The icon representing this status info or
     *             <code>null</code> when there is no icon.
     */
    public void setIcon(Image icon) {
        this.icon = icon;
    }
    
    /**
     * Set the status info, that represents this status info.
     * @param repInfo The representing file status info. Can be <code>null</code>
     *                if no other status info is represented.
     */
    public void setRepresentingStatus(FileStatusInfo repInfo) {
        this.repInfo = repInfo;
    }
    
    /**
     * Tell, whether this file status information represents just the same kind
     * of status as another one. This method can be used by version control systems,
     * that have more status information types, then the pre-defined constants.
     * Use this to find out e.g. whether this status info represents one of the
     * pre-defined status info.
     * @return Whether this file status information represents just the same kind
     * of status as another one.
     */
    public boolean represents(FileStatusInfo info) {
        return equals(info) || (repInfo != null && repInfo.equals(info));
    }
    
    public Color getSelectedColor() {
        return color;
    }
    
    public void setSelectedColor(Color color) {
        this.color = color;
    }
    
    public void addChangeListener(javax.swing.event.ChangeListener listener) {
        // Not used currently
    }
    
    public void removeChangeListener(javax.swing.event.ChangeListener listener) {
        // Not used currently
    }
    
}
