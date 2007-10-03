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

package org.netbeans.modules.vcscore;

import java.beans.*;
import org.openide.ErrorManager;

import org.openide.util.NbBundle;

/** BeanInfo for VcsVersioningSystem.
 *
 * @author Martin Entlicher
 */

public class VcsVersioningSystemBeanInfo extends SimpleBeanInfo {

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] properties = null;
        PropertyDescriptor showDeadFiles = null;
        PropertyDescriptor showMessages = null;
        PropertyDescriptor messageLength = null;
        //PropertyDescriptor showUnimportantFiles = null;
        //PropertyDescriptor showLocalFiles = null;  -- makes problems, since every file is initially local
        
        try {
            showDeadFiles = new PropertyDescriptor
                          (VcsVersioningSystem.PROP_SHOW_DEAD_FILES, VcsVersioningSystem.class, "isShowDeadFiles", "setShowDeadFiles"); // NOI18N
            showDeadFiles.setDisplayName      (NbBundle.getMessage(VcsVersioningSystem.class, "PROP_showDeadFiles"));
            showDeadFiles.setShortDescription (NbBundle.getMessage(VcsVersioningSystem.class, "HINT_showDeadFiles"));
            showMessages = new PropertyDescriptor
                          (VcsVersioningSystem.PROP_SHOW_MESSAGES, VcsVersioningSystem.class, "isShowMessages", "setShowMessages"); // NOI18N
            showMessages.setDisplayName      (NbBundle.getMessage(VcsVersioningSystem.class, "PROP_showMessages")); //NOI18N
            showMessages.setShortDescription (NbBundle.getMessage(VcsVersioningSystem.class, "HINT_showMessages")); //NOI18N
            messageLength = new PropertyDescriptor
                          (VcsVersioningSystem.PROP_MESSAGE_LENGTH, VcsVersioningSystem.class, "getMessageLength", "setMessageLength"); // NOI18N
            messageLength.setDisplayName      (NbBundle.getMessage(VcsVersioningSystem.class, "PROP_messageLength")); //NOI18N
            messageLength.setShortDescription (NbBundle.getMessage(VcsVersioningSystem.class, "HINT_messageLength")); //NOI18N
            /* makes problems when it's not in synch with "Process All Files" property on VcsFileSystem (issue #32902)
            showUnimportantFiles = new PropertyDescriptor
                          (VcsVersioningSystem.PROP_SHOW_UNIMPORTANT_FILES, VcsVersioningSystem.class, "isShowUnimportantFiles", "setShowUnimportantFiles"); // NOI18N
            showUnimportantFiles.setDisplayName(NbBundle.getMessage(VcsVersioningSystem.class, "PROP_showUnimportantFiles"));
            showUnimportantFiles.setShortDescription(NbBundle.getMessage(VcsVersioningSystem.class, "HINT_showUnimportantFiles"));
            showUnimportantFiles.setExpert(true);
             */
            /*  makes problems, since every file is initially local
            showLocalFiles = new PropertyDescriptor
                          (VcsVersioningSystem.PROP_SHOW_LOCAL_FILES, VcsVersioningSystem.class, "isShowLocalFiles", "setShowLocalFiles"); // NOI18N
            showLocalFiles.setDisplayName     (NbBundle.getMessage(VcsVersioningSystem.class, "PROP_showLocalFiles"));
            showLocalFiles.setShortDescription(NbBundle.getMessage(VcsVersioningSystem.class, "HINT_showLocalFiles"));
            showLocalFiles.setExpert(true);
             */
            
            properties = new PropertyDescriptor[] { showDeadFiles, showMessages, messageLength };
        } catch (IntrospectionException ex) {
           ErrorManager.getDefault().notify(ex);
        }
        return properties;
    }
    
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     * 
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor beanDescriptor = new BeanDescriptor(VcsVersioningSystem.class, null);
        beanDescriptor.setValue(VcsFileSystem.VCS_PROVIDER_ATTRIBUTE, Boolean.TRUE);
	return beanDescriptor;
    }

    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc.   Icon images
     * will typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from
     * this method.
     * <p>
     * There are four possible flavors of icons (16x16 color,
     * 32x32 color, 16x16 mono, 32x32 mono).  If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background
     * so they can be rendered onto an existing background.
     *
     * @param  iconKind  The kind of icon requested.  This should be
     *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32, 
     *    ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return  An image object representing the requested icon.  May
     *    return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int iconKind) {
        return null;
    }

}

