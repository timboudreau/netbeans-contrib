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

package org.netbeans.modules.corba.poasupport;

import java.util.*;
import java.net.*;
import java.io.*;

import org.openide.src.ClassElement;
import org.openide.cookies.OpenCookie;
import org.netbeans.modules.corba.poasupport.tools.POAChecker;

/*
 * @author Dusan Balek
 */

public class POAElement {
    
    public static String PROP_POA_NAME = "POAName"; // NOI18N
    public static String PROP_VAR_NAME = "VarName"; // NOI18N
    public static String PROP_MANAGER = "Manager"; // NOI18N
    public static String PROP_POLICIES = "Policies"; // NOI18N
    
    private String poaName = null;
    private String varName = null;
    private String manager = null;
    private Properties policies = new Properties();
    private Vector childPOAs = new Vector();
    private Vector servants = new Vector();
    private POAActivatorElement poaActivator = null;
    private DefaultServantElement defaultServant = null;
    private ServantManagerElement servantManager = null;
    private POAElement parentPOA;
    protected RootPOAElement rootPOA;
    
    protected boolean writeable;
    
    /** Utility field holding list of PropertyChangeListeners. */
    private transient java.util.ArrayList propertyChangeListenerList;
    
    public POAElement(POAElement _parentPOA, RootPOAElement _rootPOA, boolean _writeable) {
        parentPOA = _parentPOA;
        rootPOA = _rootPOA;
        writeable = _writeable;
        poaName = getDefaultPOAName();
        varName = getDefaultVarName();
    }
    
    public POAElement(boolean _writeable) {
        parentPOA = null;
        rootPOA = null;
        writeable = _writeable;
    }
    
    public boolean isWriteable() {
        return writeable;
    }

    public boolean canUseAsNewVarName(String name) {
        return rootPOA.canUseAsNewVarNameFor(name, this);
    }
    
    public String getDefaultVarName() {
        int counter = 1;
        while (!canUseAsNewVarName(POASupport.getPOASettings().getDefaultPOAVarName() + String.valueOf(counter)))
            counter++;
        return POASupport.getPOASettings().getDefaultPOAVarName() + String.valueOf(counter);
    }
    
    public boolean canUseAsPOAName(String name) {
        return getParentPOA().canUseIDForChildPOA(name, this);
    }
    
    public String getDefaultPOAName() {
        if (isRootPOA())
            return POASupport.getString("LBL_RootPOA_node");
        int counter = 1;
        while (!canUseAsPOAName(POASupport.getPOASettings().getDefaultPOAName() + String.valueOf(counter)))
            counter++;
        return POASupport.getPOASettings().getDefaultPOAName() + String.valueOf(counter);
    }
    
    public ClassElement getDeclaringClass() {
        return rootPOA.maker.getClassElement();
    }
    
    public OpenCookie getOpenCookie() {
        return rootPOA.maker.getOpenCookie();
    }

    public void setLinePosition() {
        rootPOA.maker.setLinePosition(this);
    }
    
    public String getPOAName () {
        return poaName;
    }
    
    public void setPOAName (String _poaName) {
        if (!_poaName.equals(poaName)) {
            String oldName = poaName;
            poaName = _poaName;
            firePropertyChange(new java.beans.PropertyChangeEvent(this, PROP_POA_NAME, oldName, poaName));
        }
    }
    
    public String getVarName () {
        return varName;
    }
    
    public void setVarName (String _varName) {
        if (!_varName.equals(varName)) {
            String oldName = varName;
            varName = _varName;
            firePropertyChange(new java.beans.PropertyChangeEvent(this, PROP_VAR_NAME, oldName, varName));
        }
    }
    
    public String getManager() {
        return manager;
    }
    
    public void setManager(String _manager) {
        if (((_manager != null)&&(!_manager.equals(manager))) || ((_manager == null)&&(manager != null))) {
            String oldManager = manager;
            manager = _manager;
            firePropertyChange(new java.beans.PropertyChangeEvent(this, PROP_MANAGER, oldManager, manager));
        }
    }
    
    public Properties getPolicies() {
        return (Properties)policies.clone();
    }
    
    public void setPolicies(Properties _policies) {
        if (!_policies.equals(policies)) {
            Properties oldPolicies = policies;
            policies = _policies;
            if ((getServants().size() > 0) && (POAChecker.checkDisabledServantActivation(policies).equals(POASupport.getPOASettings().ALL_SERVANTS)))
                removeAllServants();
            if ((getServantManager() != null) && (!POAChecker.isServantManagerEnabled(policies)))
                removeServantManager();
            if ((getDefaultServant() != null) && (!POAChecker.isDefaultServantEnabled(policies)))
                removeDefaultServant();
            firePropertyChange(new java.beans.PropertyChangeEvent(this, PROP_POLICIES, oldPolicies, policies));
        }
    }
    
    public Vector getChildPOAs () {
        return (Vector)childPOAs.clone();
    }
    
    public void addChildPOA (POAElement element) {
        childPOAs.addElement (element);
        rootPOA.addPOAToList (element);
    }
    
    public void removeChildPOA (POAElement element) {
        int index = childPOAs.indexOf(element);
        if (index != -1) {
            childPOAs.remove (index);
            rootPOA.removePOAFromList (element);
        }
    }
    
    public Vector getServants () {
        return (Vector)servants.clone();
    }
    
    public void addServant (ServantElement element) {
        servants.addElement (element);
        rootPOA.addServantToList(element);
    }
    
    public void removeServant (ServantElement element) {
        int index = servants.indexOf(element);
        if (index != -1) {
            servants.remove (index);
            rootPOA.removeServantFromList (element);
        }
    }
    
    public void removeAllServants () {
        ListIterator iterator = servants.listIterator();
        while (iterator.hasNext())
            rootPOA.removeServantFromList((ServantElement)iterator.next());
        servants.removeAllElements();
    }
    
    public DefaultServantElement getDefaultServant () {
        return defaultServant;
    }
    
    public void setDefaultServant (DefaultServantElement element) {
        defaultServant = element;
        rootPOA.addDefaultServantToList(defaultServant);
    }
    
    public void removeDefaultServant() {
        rootPOA.removeDefaultServantFromList(defaultServant);
        defaultServant = null;
    }
    
    public ServantManagerElement getServantManager () {
        return servantManager;
    }
    
    public void setServantManager (ServantManagerElement element) {
        servantManager = element;
        rootPOA.addServantManagerToList(servantManager);
    }
    
    public void removeServantManager() {
        rootPOA.removeServantManagerFromList(servantManager);
        servantManager = null;
    }
    
    public POAActivatorElement getPOAActivator () {
        return poaActivator;
    }
    
    public void setPOAActivator (POAActivatorElement element) {
        poaActivator = element;
        rootPOA.addPOAActivatorToList(poaActivator);
    }
    
    public void removePOAActivator() {
        rootPOA.removePOAActivatorFromList(poaActivator);
        poaActivator = null;
    }
    
    public POAElement getParentPOA() {
        return parentPOA;
    }
    
    public RootPOAElement getRootPOA() {
        return rootPOA;
    }
    
    public boolean isRootPOA() {
        return false;
    }
    
    public Vector getAvailablePOAManagers() {
        return rootPOA.getAvailablePOAManagers(this);
    }
    
    /** Registers PropertyChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        if (propertyChangeListenerList == null ) {
            propertyChangeListenerList = new java.util.ArrayList ();
        }
        propertyChangeListenerList.add (listener);
    }
    
    /** Removes PropertyChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        if (propertyChangeListenerList != null ) {
            propertyChangeListenerList.remove (listener);
        }
    }
    
    /** Notifies all registered listeners about the event.
     *
     * @param e The event to be fired
     */
    protected void firePropertyChange(java.beans.PropertyChangeEvent event) {
        java.util.ArrayList list;
        synchronized (this) {
            if (propertyChangeListenerList == null) return;
            list = (java.util.ArrayList)propertyChangeListenerList.clone ();
        }
        for (int i = 0; i < list.size (); i++) {
            ((java.beans.PropertyChangeListener)list.get (i)).propertyChange (event);
        }
    }
    
    protected boolean canUseIDForServant(String id, ServantElement servant) {
        if (id == null)
            return true;
        for (int i = 0; i < servants.size(); i++) {
            ServantElement se = (ServantElement)servants.get(i);
            if ( id.equals(se.getObjID()) && !se.equals(servant) )
                return false;
        }
        return true;
    }
    
    protected boolean canUseIDForChildPOA(String id, POAElement poa) {
        if (id == null)
            return true;
        for (int i = 0; i < childPOAs.size(); i++) {
            POAElement pe = (POAElement)childPOAs.get(i);
            if ( id.equals(pe.getPOAName()) && !pe.equals(poa) )
                return false;
        }
        return true;
    }
    
    protected boolean canUseAsNewVarNameFor(String name, Object element) {
        return rootPOA.canUseAsNewVarNameFor(name, element);
    }
    
    protected boolean canUseAsVarNameFor(String name, Object element) {
        return rootPOA.canUseAsVarNameFor(name, element);
    }
    
}
