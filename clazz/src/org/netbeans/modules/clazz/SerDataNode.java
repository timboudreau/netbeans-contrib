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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
