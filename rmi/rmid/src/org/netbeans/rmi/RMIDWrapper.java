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

package org.netbeans.rmi;

/**
 * The RMIDWrapper interface adds functionality to the activation system.
 * @author  Jan Pokorsky
 * @version
 */
public interface RMIDWrapper extends java.rmi.activation.ActivationSystem {

    /** Returns an array of activation identifiers.
     * @return the array of activation ids
     * @throws RemoteException
     * @throws UnknownRMIDException if a unknown rmid implementation is used.
     */
    public java.rmi.activation.ActivationID[] getActivationIDs()
    throws java.rmi.RemoteException, UnknownRMIDException;
    
    /** Returns an array of activation group identifiers.
     * @return the array of activation group ids
     * @throws RemoteException
     * @throws UnknownRMIDException if a unknown rmid implementation is used.
     */  
    public java.rmi.activation.ActivationGroupID[] getActivationGroupIDs()
    throws java.rmi.RemoteException, UnknownRMIDException;
    
    /** Returns the remote reference to the activatable object.
     * @param id activation id
     * @return a remote reference.
     * @throws RemoteException - if remote call fails
     * @throws ActivationException - if activation system is not running or for general failure
     * @throws UnknownObjectException - if id is not registered
     * @throws StubNotFoundException
     */
    public java.rmi.Remote getStub(java.rmi.activation.ActivationID id)
    throws java.rmi.RemoteException,
           java.rmi.activation.ActivationException,
           java.rmi.activation.UnknownObjectException,
           java.rmi.StubNotFoundException;
    
    /** Registers the remote reference of the activatable object to 
     * a local registry.
     * @param id activation id
     * @param name name for the remote object
     * @param port port on which the registry accepts requests
     * @throws RemoteException - if remote call fails
     * @throws AccessException  if Registry.rebind operation is not permitted.
     * @throws MalformedURLException - if the name is not an appropriately formatted URL
     * @throws ActivationException - if activation system is not running or for general failure
     * @throws UnknownObjectException - if id is not registered
     * @throws StubNotFoundException
     */
    public void rebind(java.rmi.activation.ActivationID id,
                       String name,
                       int port)
    throws java.rmi.RemoteException,
           java.rmi.AccessException,
           java.net.MalformedURLException,
           java.rmi.activation.ActivationException,
           java.rmi.activation.UnknownObjectException,
           java.rmi.StubNotFoundException;
    
    /** Inactivate activatable object.
     * @param id activation id
     * @throws RemoteException - if remote call fails
     * @throws ServerException - detail = RemoteException - if call informing monitor fails
     * @throws UnknownObjectException - if id is not registered
     * @throws UnknownRMIDException - if a unknown rmid implementation is used.
     */
    public void inactivateObject(java.rmi.activation.ActivationID id)
    throws java.rmi.RemoteException,
           java.rmi.activation.UnknownObjectException,
           UnknownRMIDException;
    
    /** Inactivate group.
     * @param gid activation group id
     * @throws RemoteException - if remote call fails
     * @throws ServerException - detail = RemoteException - if call informing monitor fails
     * @throws UnknownGroupException - unknown group id
     * @throws UnknownRMIDException - if a unknown rmid implementation is used.
     */
    public void inactivateGroup(java.rmi.activation.ActivationGroupID gid)
    throws java.rmi.RemoteException,
           java.rmi.activation.UnknownGroupException,
           UnknownRMIDException;
    
}

