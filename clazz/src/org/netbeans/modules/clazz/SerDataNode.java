/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
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
