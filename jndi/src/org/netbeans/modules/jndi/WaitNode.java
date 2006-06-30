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

package org.netbeans.modules.jndi;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 * This class represents na temporary node, that is shown when then
 * folder data are transfered from remote host.
 * @author  tzezula
 * @version
 */


public class WaitNode extends AbstractNode {

    /** Constant to the Icon lookup table*/
    public final static String WAIT_ICON="WAIT_NODE"; // no I18N

    /** Creates new WaitNode */
    public WaitNode() {
        super(Children.LEAF);
        this.setName(JndiRootNode.getLocalizedString("TITLE_WaitNode"));
        this.setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName(WaitNode.WAIT_ICON));
    }

    /** Can not be copied
     *  @return boolean false
     */
    public boolean canCopy(){
        return false;
    }

    /** Can not be cut
     *  @return boolean false
     */
    public boolean canCut(){
        return false;
    }

    /** Can not be deleted
     *  @return boolean false
     */
    public boolean canDelete(){
        return false;
    }

    /** Can not be renamed
     *  @return boolean false
     */
    public boolean canRename(){
        return false;
    }

}