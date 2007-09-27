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

