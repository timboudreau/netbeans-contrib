/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
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
                        poaElement = sourceMaker.scanPOAHierarchy();
                        createKeys();
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
                                poaElement = sourceMaker.scanPOAHierarchy();
                                createKeys();
                                ((POANode)getNode()).setActions();
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
