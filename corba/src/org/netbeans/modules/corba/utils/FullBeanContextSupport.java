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

package org.netbeans.modules.corba.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;
import java.beans.beancontext.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Vector;
import java.util.HashMap;
import java.util.Collection;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
/**
 *
 * @author  Tomas Zezula
 * @version  1.0
 * This class does not offers full functionality of BeanContextSupport
 */
public class FullBeanContextSupport extends Vector implements BeanContext, Serializable {
    
    public static final String BEAN_CONTEXT = "beanContext";
    public static final String DESIGN_MODE ="designMode";
    
    protected boolean avoidGui = false;
    protected boolean isDesignTime = false;
    protected int noChildDesignTime = 0;
    protected BeanContext parent = null;
    transient protected ArrayList  contentMembershipListeners;
    transient protected HashMap propertyChangeListeners;
    transient protected HashMap vetoableListeners;
    transient protected PropertyChangeDispatcher pchDispatcher;
    transient protected VetoableChangeListener vchDispatcher;
    
    private class PropertyChangeDispatcher implements PropertyChangeListener {
        public void propertyChange (PropertyChangeEvent event) {
            //racecondition avoidance
            if (!FullBeanContextSupport.this.contains(event.getSource()))
                return;
            String propName = event.getPropertyName();
            if (DESIGN_MODE.equals (propName)) {
                Boolean b = (Boolean) event.getNewValue();
                if (b != null) {
                    if (b.booleanValue())
                        noChildDesignTime++;
                    else
                        noChildDesignTime--;
                }
            }
            synchronized (FullBeanContextSupport.this) {
                ArrayList slot = (ArrayList) FullBeanContextSupport.this.propertyChangeListeners.get (propName);
                if (slot != null && slot.size() > 0)
                    FullBeanContextSupport.this.firePropertyChange (event, slot);
            }
        }
    }
    
    private class VetoableChangeDispatcher implements VetoableChangeListener {
        public void vetoableChange (PropertyChangeEvent event) throws PropertyVetoException {
            //racecondition avoidance
            if (!FullBeanContextSupport.this.contains(event.getSource()))
                return;
            String propName = event.getPropertyName();
            synchronized (FullBeanContextSupport.this) {
                ArrayList slot = (ArrayList) FullBeanContextSupport.this.vetoableListeners.get (propName);
                if (slot != null && slot.size() > 0)
                    FullBeanContextSupport.this.fireVetoableChange (event,slot);
            }
        }
    }

    /** Creates new FullBeanContextSupport */
    public FullBeanContextSupport() {
        this.contentMembershipListeners = new ArrayList();
        this.propertyChangeListeners = new HashMap ();
        this.propertyChangeListeners.put (DESIGN_MODE, new ArrayList());
        this.vetoableListeners = new HashMap ();
        this.pchDispatcher = this.new PropertyChangeDispatcher();
        this.vchDispatcher = this.new VetoableChangeDispatcher();
    }

    
    /** Methods defined in BeanContext interface */
    
    public java.lang.Object instantiateChild(java.lang.String str) throws java.io.IOException, java.lang.ClassNotFoundException {
        return java.beans.Beans.instantiate (this.getClass().getClassLoader(), str);
    }
    
    public synchronized void addBeanContextMembershipListener(java.beans.beancontext.BeanContextMembershipListener beanContextMembershipListener) {
        this.contentMembershipListeners.add (beanContextMembershipListener);
    }

    public synchronized void removeBeanContextMembershipListener(java.beans.beancontext.BeanContextMembershipListener beanContextMembershipListener) {
        this.contentMembershipListeners.remove (beanContextMembershipListener);
    }
    
    public java.net.URL getResource(java.lang.String str, java.beans.beancontext.BeanContextChild beanContextChild) throws java.lang.IllegalArgumentException {
        return beanContextChild.getClass().getClassLoader().getResource (str);
    }
    
    public java.io.InputStream getResourceAsStream(java.lang.String str, java.beans.beancontext.BeanContextChild beanContextChild) throws java.lang.IllegalArgumentException {
        return beanContextChild.getClass().getClassLoader().getResourceAsStream (str);
    }
    
    
    
    /** Mothods defined in BeanContextChildren inteface */
    
    public void setBeanContext(java.beans.beancontext.BeanContext beanContext) throws java.beans.PropertyVetoException {
        Object oldParent = this.parent;
        this.fireVetoableChange (BEAN_CONTEXT, this.parent, beanContext);
        this.parent = beanContext;
        this.firePropertyChange (BEAN_CONTEXT, oldParent, this.parent);
    }
    
    public java.beans.beancontext.BeanContext getBeanContext() {
        return parent;
    }
    
    public synchronized void addVetoableChangeListener(java.lang.String str, java.beans.VetoableChangeListener vetoableChangeListener) {
        ArrayList slot = (ArrayList) this.vetoableListeners.get (str);
        if (slot == null) {
            slot = new ArrayList ();
            this.vetoableListeners.put (str, slot);
            Iterator it = this.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                BeanContextChild bcc = getBeanContextChild (obj);
                if (bcc != null)
                    bcc.addVetoableChangeListener (str, vchDispatcher);
            }
        }
        slot.add (vetoableChangeListener);
    }
    
    public synchronized void removeVetoableChangeListener(java.lang.String str, java.beans.VetoableChangeListener vetoableChangeListener) {
        ArrayList slot = (ArrayList) this.vetoableListeners.get (str);
        if (slot == null)
            return;
        slot.remove (vetoableChangeListener);
        if (slot.size() == 0) {
            this.vetoableListeners.put (str, null);
            Iterator it = this.iterator ();
            while (it.hasNext()) {
                Object obj = it.next();
                BeanContextChild bcc = getBeanContextChild (obj);
                if (bcc != null)
                    bcc.removeVetoableChangeListener (str, vchDispatcher);
            }
        }
    }
    
    public synchronized void addPropertyChangeListener(java.lang.String str, java.beans.PropertyChangeListener propertyChangeListener) {
        ArrayList slot = (ArrayList) this.propertyChangeListeners.get (str);
        if (slot == null) {
            slot = new ArrayList();
            this.propertyChangeListeners.put (str, slot);
            Iterator it = this.iterator ();
            while (it.hasNext()) {
                Object obj = it.next();
                BeanContextChild bcc = getBeanContextChild (obj);
                if (obj != null)
                    bcc.addPropertyChangeListener (str, pchDispatcher);
            }
        }
        slot.add (propertyChangeListener);
    }
    
    public synchronized void removePropertyChangeListener(java.lang.String str, java.beans.PropertyChangeListener propertyChangeListener) {
        ArrayList slot = (ArrayList) this.propertyChangeListeners.get (str);
        if (slot == null)
            return;
        slot.remove (propertyChangeListener);
        if (slot.size() == 0 && ! DESIGN_MODE.equals(str)) {
            this.propertyChangeListeners.remove (str);
            Iterator it = this.iterator ();
            while (it.hasNext()) {
                Object obj = it.next();
                BeanContextChild bcc = getBeanContextChild (obj);
                if (bcc != null)
                    bcc.removePropertyChangeListener (str, pchDispatcher);
            }
        }
    }
    
    
    
    /** Visibility and DesignTime defined methods */
    
    public void okToUseGui() {
        this.avoidGui = false;
    }
   
    public boolean needsGui() {
        return ! this.avoidGui;
    }
    
    public boolean avoidingGui() {
        return this.avoidGui;
    }
    
    public void dontUseGui() {
        this.avoidGui = true;
    }
    
    public boolean isDesignTime() {
        return this.isDesignTime || (this.noChildDesignTime>0);
    }
    
    public void setDesignTime(boolean param) {
        boolean oldValue = this.isDesignTime;
        this.isDesignTime = param;
        firePropertyChange (DESIGN_MODE,oldValue ? Boolean.TRUE : Boolean.FALSE,this.isDesignTime ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public synchronized boolean add (Object element) {
        boolean res = super.add (element);
        if (res) {
            BeanContextChild cld = getBeanContextChild (element);
            if (cld != null) {
                try {
                    cld.setBeanContext (this);
                }catch (PropertyVetoException pve) {}
                if (cld instanceof BeanContext && ((BeanContext)cld).isDesignTime())
                    this.noChildDesignTime++;
                Iterator it = this.propertyChangeListeners.keySet().iterator();
                while (it.hasNext())
                    cld.addPropertyChangeListener ((String)it.next(), this.pchDispatcher);
                it = this.vetoableListeners.keySet().iterator();
                while (it.hasNext())
                    cld.addVetoableChangeListener ((String)it.next(), this.vchDispatcher);
            }
            fireChildrenAdded (new Object[] {element});
        }
        return res;
    }
    
    public synchronized boolean addAll (Collection c) {
        ArrayList subList = new ArrayList ();
        Iterator it = c.iterator ();
        while (it.hasNext()) {         
            Object entry = it.next();
            if (!this.contains (entry)) {
                subList.add (entry);
                super.add (entry);
                BeanContextChild cld = getBeanContextChild (entry);
                if (cld != null) {
                    try {
                        cld.setBeanContext (this);
                    }catch (PropertyVetoException pve){}
                    if (cld instanceof BeanContext && ((BeanContext)cld).isDesignTime())
                        this.noChildDesignTime++;
                    for (Iterator lit = this.propertyChangeListeners.keySet().iterator();it.hasNext();)
                        cld.addPropertyChangeListener ((String)lit.next(), this.pchDispatcher);
                    for (Iterator lit = this.vetoableListeners.keySet().iterator(); it.hasNext();)
                        cld.addVetoableChangeListener ((String)lit.next(),vchDispatcher);
                }
            }
        }
        fireChildrenAdded (subList);
        return subList.size() != 0;
    }
    
    public synchronized void clear () {
        ArrayList entries = new ArrayList();
        while (this.size()>0) {
            Object element = this.remove (0);
            entries.add (element);
            BeanContextChild bcc = getBeanContextChild (element);
            if (bcc != null) {
                try {
                    bcc.setBeanContext (null);
                }catch (PropertyVetoException pve) {}
                for (Iterator lit = this.propertyChangeListeners.keySet().iterator(); lit.hasNext();)
                    bcc.removePropertyChangeListener ((String)lit.next(), this.pchDispatcher);
                for (Iterator lit = this.vetoableListeners.keySet().iterator(); lit.hasNext();)
                    bcc.removeVetoableChangeListener ((String)lit.next(),this.vchDispatcher);
            }
                
        }
        this.noChildDesignTime = 0;
        fireChildrenRemoved (entries);
    }
    
    public synchronized boolean remove (Object entry) {
        boolean res = super.remove (entry);
        if (res) {
            BeanContextChild cld = getBeanContextChild (entry);
            if (cld != null) {
                try {
                    cld.setBeanContext (null);
                }catch (PropertyVetoException pve) {}
                if (cld instanceof BeanContext && ((BeanContext)cld).isDesignTime())
                    this.noChildDesignTime--;
                Iterator it = this.propertyChangeListeners.keySet().iterator();
                while (it.hasNext())
                    cld.removePropertyChangeListener ((String)it.next(),this.pchDispatcher);
                it = this.vetoableListeners.keySet().iterator();
                while (it.hasNext())
                    cld.removeVetoableChangeListener ((String)it.next(), this.vchDispatcher);
            }
            fireChildrenRemoved (new Object[] {entry});
        }
        return res;
    }
    
    public synchronized boolean removeAll (Collection c) {
        ArrayList subList = new ArrayList ();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Object entry = it.next();
            subList.add (entry);
            super.remove (entry);
            BeanContextChild cld = getBeanContextChild (entry);
            if (cld != null) {
                try {
                    cld.setBeanContext (null);
                }catch (PropertyVetoException pve) {}
                if (cld instanceof BeanContext && ((BeanContext)cld).isDesignTime())
                    this.noChildDesignTime--;
                for (Iterator lit = this.propertyChangeListeners.keySet().iterator(); it.hasNext();)
                    cld.addPropertyChangeListener ((String)lit.next(), pchDispatcher);
                for (Iterator lit = this.vetoableListeners.keySet().iterator(); it.hasNext();)
                    cld.addVetoableChangeListener ((String)lit.next(), vchDispatcher);
            }
        }
        fireChildrenRemoved (subList);
        return subList.size()!=0;
    }
    
    
    public synchronized boolean retainAll (Collection c) {
        ArrayList removed = new ArrayList();
        Iterator it = this.iterator();
        while (it.hasNext()) {
            Object element = it.next();
            if (!c.contains (element)) {
                super.remove (element);
                BeanContextChild bcc = getBeanContextChild (element);
                if (bcc != null) {
                    try {
                        bcc.setBeanContext (null);
                    }catch (PropertyVetoException pve) {}
                    if (bcc instanceof BeanContext && ((BeanContext)bcc).isDesignTime())
                        this.noChildDesignTime--;
                    for (Iterator lit = this.propertyChangeListeners.keySet().iterator();lit.hasNext();)
                        bcc.removePropertyChangeListener ((String)lit.next(), this.pchDispatcher);
                    for (Iterator lit = this.vetoableListeners.keySet().iterator(); lit.hasNext();)
                        bcc.removeVetoableChangeListener ((String)lit.next(), this.vchDispatcher);
                }
                removed.add (element);
            }
        }
        fireChildrenRemoved (removed);
        return removed.size()!=0;
    }
    
    /** Listener notification Helpers */
    
    protected void fireChildrenAdded (Object[] change) {
        if (change == null || change.length == 0)
            return;
        BeanContextMembershipEvent event = new BeanContextMembershipEvent (this, change);
        fireChildrenAdded (event);
    }
    
    protected void fireChildrenAdded (Collection change) {
        if (change == null || change.size() == 0)
            return;
        BeanContextMembershipEvent event = new BeanContextMembershipEvent (this, change);
        fireChildrenAdded (event);
    }
    
    protected void fireChildrenAdded (BeanContextMembershipEvent event) {
        Iterator it;
        synchronized (this) {
            it = ((ArrayList)this.contentMembershipListeners.clone()).iterator();
        }
        while (it.hasNext()) {
            ((BeanContextMembershipListener)it.next()).childrenAdded (event);
        }
    }
    
    protected void fireChildrenRemoved (Object[] change) {
        if (change == null || change.length == 0)
            return;
        BeanContextMembershipEvent event = new BeanContextMembershipEvent (this, change);
        fireChildrenRemoved (event);
    }
    
    protected void fireChildrenRemoved (Collection change) {
        if (change == null || change.size() == 0)
            return;
        BeanContextMembershipEvent event = new BeanContextMembershipEvent (this, change);
        fireChildrenRemoved (event);
    }
    
    protected void fireChildrenRemoved  (BeanContextMembershipEvent event) {
       Iterator it;
        synchronized (this) {
            it = ((ArrayList)this.contentMembershipListeners.clone()).iterator();
        }
        while (it.hasNext()) {
            ((BeanContextMembershipListener)it.next()).childrenRemoved (event);
        } 
    }
    
    protected void firePropertyChange (String propName, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent (this, propName, oldValue, newValue);
        ArrayList slot;
        synchronized (this) {
            slot = (ArrayList) this.propertyChangeListeners.get (propName);
            if (slot == null || slot.size() ==0)
                return;
            slot = (ArrayList) slot.clone();
        }
        firePropertyChange (event, slot);
    }
    
    /** Caller of this method has to ensure synchronization !!
     */
    protected void firePropertyChange (PropertyChangeEvent event, ArrayList slot) {
        Iterator it = slot.iterator();
        while (it.hasNext()) {
            ((PropertyChangeListener)it.next()).propertyChange (event);
        }
    }
    
    protected void fireVetoableChange (String propName, Object oldValue, Object newValue) throws PropertyVetoException {
        PropertyChangeEvent event = new PropertyChangeEvent (this, propName, oldValue, newValue);
        ArrayList slot;
        synchronized (this) {
            slot = (ArrayList) this.vetoableListeners.get (propName);
            if (slot == null || slot.size() == 0)
                return;
            slot = (ArrayList) slot.clone();
        }
        fireVetoableChange (event, slot);
    }
    
    /** Caller of this method has to ensure synchronization !!
     */
    protected void fireVetoableChange (PropertyChangeEvent event, ArrayList slot) throws PropertyVetoException {
        Iterator it = slot.iterator();
        while (it.hasNext()) {
            ((VetoableChangeListener)it.next()).vetoableChange (event);
        }
    }
    
    protected BeanContextChild getBeanContextChild (Object obj) {
        if (obj instanceof BeanContextChild)
            return (BeanContextChild) obj;
        if (obj instanceof BeanContextProxy)
            ((BeanContextProxy)obj).getBeanContextProxy();
        return null;
    }
    
    
    private void writeObject (ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    
    private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.contentMembershipListeners = new ArrayList ();
        this.propertyChangeListeners = new HashMap (); 
        this.vetoableListeners = new HashMap ();
        this.pchDispatcher = new PropertyChangeDispatcher ();
        this.vchDispatcher = new VetoableChangeDispatcher ();
    }
    
}
