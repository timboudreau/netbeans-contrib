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

package org.netbeans.modules.corba.poasupport.nodes;

import java.util.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.openide.util.RequestProcessor;
import org.netbeans.modules.corba.poasupport.*;
import org.netbeans.modules.corba.poasupport.tools.*;
import org.openide.nodes.*;

/** List of children of a containing node.
 * Remember to document what your permitted keys are!
 *
 * @author Dusan Balek
 */
public class POAChildren extends Children.Keys {

    private POAElement poaElement = null;
    private POASourceMaker sourceMaker = null;
    private boolean isZombie = false;
    private Children parentChildren;
    
    public POAChildren(POAElement _poaElement) {
        super();
        poaElement = _poaElement;
    }
    
    public POAChildren(POASourceMaker _maker) {
        super();
        sourceMaker = _maker;
        sourceMaker.setChangeListener(new MyListener());
        poaElement = sourceMaker.scanPOAHierarchy();
    }
    
    protected void addNotify () {
        createKeys ();
    }
    
    protected POAElement getPOAElement () {
        return poaElement;
    }
    
    public void createKeys () {
        if (poaElement != null) {
            Collection c = (Collection)poaElement.getChildPOAs().clone();
            if (poaElement.getPOAActivator()!=null)
                c.add(poaElement.getPOAActivator());
            c.addAll(poaElement.getServants());
            if (poaElement.getDefaultServant()!=null)
                c.add(poaElement.getDefaultServant());
            if (poaElement.getServantManager()!=null)
                c.add(poaElement.getServantManager());
            setKeys (c);
        }
    }
    
    protected Node[] createNodes (Object key) {
        if (key instanceof POAElement)
            return new Node[] { new POANode(new POAChildren((POAElement)key))};
            if (key instanceof POAActivatorElement)
                return new Node[] { new POAActivatorNode((POAActivatorElement)key) };
                if (key instanceof DefaultServantElement)
                    return new Node[] { new DefaultServantNode((DefaultServantElement)key) };
                    if (key instanceof ServantManagerElement)
                        return new Node[] { new ServantManagerNode((ServantManagerElement)key) };
                        if (key instanceof ServantElement)
                            return new Node[] { new ServantNode((ServantElement)key) };
                            return new Node[0];
    }
    
    /** The listener of method changes temporary used in PatternAnalyser to
     * track changes in
     */
    
    final class MyListener implements ChangeListener {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            if (sourceMaker != null)
                if (evt.getSource() == sourceMaker) {
                    if (sourceMaker.checkForPOA()) {
                        poaElement.removePropertyChangeListener((POANode)getNode());
                        poaElement = sourceMaker.scanPOAHierarchy();
                        poaElement.addPropertyChangeListener((POANode)getNode());
                        createKeys();
                        ((POANode)getNode()).propertyChange(new java.beans.PropertyChangeEvent(poaElement, null, null, null));
                        ((POANode)getNode()).setActions();
                    }
                    else {
                        try {
                            parentChildren = getNode().getParentNode().getChildren();
                            parentChildren.remove(new Node[] {
                                getNode()
                            });
                            isZombie = true;
                        }
                        catch (Exception e) {
                        }
                    }
                }
                else {
                    RequestProcessor.postRequest (new Runnable () {
                        public void run () {
                            if (sourceMaker.isSourceModified()) {
                                poaElement.removePropertyChangeListener((POANode)getNode());
                                poaElement = sourceMaker.scanPOAHierarchy();
                                poaElement.addPropertyChangeListener((POANode)getNode());
                                createKeys();
                                ((POANode)getNode()).setActions();
                                ((POANode)getNode()).propertyChange(new java.beans.PropertyChangeEvent(poaElement, null, null, null));
                                if (isZombie) {
                                    parentChildren.add(new Node[] {
                                        getNode()
                                    });
                                    isZombie = false;
                                }
                            }
                        }
                    });
                }
        }
    }
    
    /** Optional accessor method for the keys, for use by the container node or maybe subclasses. */
    /*
    protected addKey (Object newKey) {
        // Make sure some keys already exist:
        addNotify ();
        myKeys.add (newKey);
        // Ensure that the node(s) is displayed:
        refreshKey (newKey);
    }
     */
    
    /** Optional accessor method for keys, for use by the container node or maybe subclasses. */
    /*
    protected void setKeys (Collection keys) {
        myKeys = new LinkedList ();
        myKeys.addAll (keys);
        super.setKeys (keys);
    }
     */
    
    // Could also write e.g. removeKey to be used by the nodes in this children.
    // Or, could listen to changes in their status (NodeAdapter.nodeDestroyed)
    // and automatically remove them from the keys list here. Etc.
    
}
