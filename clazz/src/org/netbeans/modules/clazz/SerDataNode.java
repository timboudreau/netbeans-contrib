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

package org.netbeans.modules.clazz;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.beans.BeanInfo;
import java.io.IOException;

import org.openide.ErrorManager;
import org.openide.src.nodes.SourceChildren;

/** Exetends ClassDataNode, adds behaviour specific to serialized objects.
* This class is final only for performance reasons,
* can be happily unfinaled if desired.
*
* @author Jaroslav Tulach, Dafe Simonek
*/
final class SerDataNode extends ClassDataNode {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -2645179282674800246L;

    private static final String SER_BASE =
        "org/netbeans/modules/clazz/resources/ser"; // NOI18N
    private static final String SER_MAIN_BASE =
        "org/netbeans/modules/clazz/resources/serMain"; // NOI18N
    private static final String SER_ERROR_BASE =
        "org/netbeans/modules/clazz/resources/serError"; // NOI18N

    /** Constructs bean data node with asociated data object.
    */
    public SerDataNode(final SerDataObject obj) {
        super(obj, new SerTopChildren(obj, new SourceChildren(ClassDataObject.getExplorerFactory())));
    }
    
    protected SourceChildren getSourceChildren() {
        return ((SerTopChildren)getChildren()).getSourceChildren();
    }

    // ----------------------------------------------------------------------------------
    // methods

    /** Returns initial icon base string for ser node.
    */
    protected String initialIconBase () {
        return SER_BASE;
    }
    
    protected void resolveIcons () {
    }

    protected void requestResolveIcon() {
        try {
            ClassDataObject dataObj = (ClassDataObject)getDataObject();
            dataObj.getBeanClass(); // check exception
            setIconBase(SER_BASE);
        } catch (IOException ex) {
            // log exception only and set error tooltip
            ErrorManager.getDefault().notify(
                ErrorManager.INFORMATIONAL, ex
            );
            setIconBase(SER_ERROR_BASE);
            setErrorToolTip(ex);
        } catch (ClassNotFoundException ex) {
            // log exception only and set error tooltip
            ErrorManager.getDefault().notify(
                ErrorManager.INFORMATIONAL, ex
            );
            setIconBase(SER_ERROR_BASE);
            setErrorToolTip(ex);
        }
        iconResolved = true;
    }
}
