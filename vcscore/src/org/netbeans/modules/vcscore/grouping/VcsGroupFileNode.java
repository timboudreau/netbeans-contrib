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

package org.netbeans.modules.vcscore.grouping;

import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;
import java.io.*;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.openide.util.actions.*;
import org.openide.util.SharedClassObject;
import org.netbeans.modules.vcscore.actions.*;

/** A customized filter node.
 *
 * @author Milos Kleint
 */
public class VcsGroupFileNode extends FilterNode {

    private DataShadow shadowObject;
    private MainVcsGroupNode main;
    private Node originalNode;
    
    public VcsGroupFileNode(DataShadow shadow, Node originalNode) {
        super (originalNode, new FilterNode.Children(originalNode));
        // Or if you just want to copy all the children as plain FilterNode's
        // (or acc. to Node.cloneNode) you can use:
        // super (original);
        // If you wish to operate on the filter node, instead of / in addition to the original,
        // for basic node operations, you should use e.g.:
        this.originalNode = originalNode;
        shadowObject = shadow;
        disableDelegation (DELEGATE_DESTROY | DELEGATE_SET_DISPLAY_NAME | DELEGATE_GET_DISPLAY_NAME 
           | DELEGATE_GET_ACTIONS // | DELEGATE_GET_CONTEXT_ACTIONS 
           | DELEGATE_GET_SHORT_DESCRIPTION | DELEGATE_SET_SHORT_DESCRIPTION);
         
//        Node originalNode = shadow.getOriginal().getNodeDelegate().cloneNode();
        setShortDescription(originalNode.getShortDescription());
        // Then you can customize some parts, e.g.:
        // super.setDisplayName (NbBundle.getMessage (VcsGroupFileNodeNode.class, "LBL_FilterNode_display_name_format", getDisplayName ());
        // To change the icon, you must use getIcon -- there is no icon resource, you
        // must load it yourself.
        
    }
    
    public void checkShowLinks() {
        fireDisplayNameChange("", getDisplayName());//NOI18N
    }
    
    public String getDisplayName() {
       VcsGroupSettings settings = (VcsGroupSettings)SharedClassObject.findObject(VcsGroupSettings.class, true);
       
//        Node origNode = shadowObject.getOriginal().getNodeDelegate();
        if (settings.isShowLinks()) {
            // XXX this is probably wrong:
            return originalNode.getDisplayName() + " -> " + shadowObject.getOriginal().getPrimaryFile().getPath(); //NOI18N
        } else {
            return originalNode.getDisplayName();
        }
    }

    public Node cloneNode () {
        // Usually you will want to override this if you are subclassing.
        // Otherwise a filter of a filter is created, which works but is not ideal.
        return new VcsGroupFileNode(shadowObject, originalNode);
    }

    // Often you will want to override this if you are subclassing.
    // Otherwise the filter node cannot be persisted, so for example
    // Explorer windows rooted at it will not be correctly restored.
/*    public Node.Handle getHandle () {
	Node.Handle origHandle = getOriginal ().getHandle ();
	// Simplest behavior: just store the original node and try to recreate
	// a filter based on that.
	if (origHandle != null)
            return new VcsGroupFileNodeHandle (origHandle);
	else
	    return null; // cannot persist original, do not persist filter
    }
 */
    
    public org.openide.util.actions.SystemAction[] getActions() {
        SystemAction[] actions = originalNode.getActions();
        List actionsList = Arrays.asList(actions);
        if (!actionsList.contains(SystemAction.get(DeleteAction.class))) {
            actions = addDelete(actionsList);
        }
        return actions;
    }
    
    private SystemAction[] addDelete(List actionsList) {
        actionsList = new ArrayList(actionsList);
        int i = actionsList.size() - 1;
        if (i >= 0 && SystemAction.get(PropertiesAction.class).equals(actionsList.get(i))) i--;
        if (i >= 0 && SystemAction.get(ToolsAction.class).equals(actionsList.get(i))) i--;
        i++;
        actionsList.add(i, SystemAction.get(DeleteAction.class));
        return (SystemAction[]) actionsList.toArray(new SystemAction[0]);
    }
    
    
/*    // Note that this class should be static and is serializable:
    private static class VcsGroupFileNodeHandle extends Node.Handle {
	private Node.Handle origHandle; // the only serializable state
        public VcsGroupFileNodeHandle (Node.Handle origHandle) {
	    this.origHandle = origHandle;
	}
	public Node getNode () throws IOException {
	    // Simplest behavior. If you have more configuration present in the filter,
	    // for example additional options to the constructor, you will probably need
	    // to manually store them or otherwise know how to recreate them.
            return new VcsGroupFileNodeNode (origHandle);
	}
    }
*/
    // To override handling of e.g. node destruction, you may:
    public boolean canDestroy () {
	return true;
    }
    public void destroy () throws IOException {
        shadowObject.delete();
	super.destroy (); // if you have disabled DELEGATE_DESTROY, this destroys this node
	// getOriginal ().destroy (); // you might wish to destroy both
	// Can perform other actions here to your liking.
    }

    // To add cookies:
    public Node.Cookie getCookie (Class clazz) {
        if (clazz.equals(DataShadow.class)) {
            return shadowObject;
        }
       return shadowObject.getOriginal().getCookie (clazz);
    }

    public boolean canRename() {
        return false;
    }
    

}
