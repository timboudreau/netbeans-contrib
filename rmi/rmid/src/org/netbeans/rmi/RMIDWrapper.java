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

