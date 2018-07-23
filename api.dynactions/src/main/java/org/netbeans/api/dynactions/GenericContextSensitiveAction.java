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
package org.netbeans.api.dynactions;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Basically a modern version of CookieAction - an action which can be enabled
 * or disabled depending on the presence or absence of a particular type in
 * a Lookup.
 * <p/>
 * Any subclass needs two public constructors - one no-arg and one taking an
 * arg of Lookup, unless they override createContextAwareInstance().  
 *
 * @author Tim Boudreau
 */
public abstract class GenericContextSensitiveAction <T> implements ContextAwareAction {
    protected final Lookup lookup;
    protected Class <T> targetClass;
    /**
     * Create an action which will be conditionally enabled when an object of
     * a particular type is present in a Lookup.
     * 
     * @param lookup The Lookup
     * @param c The type in the Lookup which this action should be sensitive to
     */
    protected GenericContextSensitiveAction(Lookup lookup, Class <T> c) {
        this.lookup = lookup == null ? Utilities.actionsGlobalContext() :
            lookup;

        if (this.lookup == null) {
            throw new NullPointerException ("Null lookup!"); //NOI18N
        }
        init (c);
        Collection <? extends T> coll = this.lookup.lookupAll(c);
        setEnabled (checkEnabled (coll, c));
    }

    /**
     * Create an action which will be conditionally enabled when an object of
     * a particular type is present in a Lookup.  Instances created using
     * this constructor operate over the global action context returned by
     * Utilities.actionsGlobalContext().
     * 
     * @param c The type to be sensitive to
     */
    protected GenericContextSensitiveAction(Class<T> c) {
        this ((Lookup) null, c);
    }

    /**
     * Create an action which will be conditionally enabled when an object of
     * a particular type is present in a Lookup.
     * @param bundleKey A String key for a display name which will be found in
     * a Bundle.properties file in the same package as this subclass
     * @param The type to be sensitive to
     */
    protected GenericContextSensitiveAction(String bundleKey, Class<T> c) {
        this ((Lookup) null, c);
        if (bundleKey != null) {
            String name;
            try {
                name = NbBundle.getMessage (getClass(), bundleKey);
            } catch (MissingResourceException mre) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                        "Missing string from Bundle.properties in package" + //NOI18N
                        "with " + getClass().getName(), mre); //NOI18N
                name = bundleKey;
            }
            setDisplayName (name);
        }
    }

//    protected GenericContextSensitiveAction (Lookup lkp) {
//        this.lookup = lkp;
//        if (this.lookup == null) {
//            throw new NullPointerException ("Null lookup!");
//        }
//    }
//
    private void init (Class<T> c) {
        if (c == null) {
            throw new NullPointerException ("Passed class is null"); //NOI18N
        }
        this.targetClass = c;
        Sensor.register(lookup, c, n);
    }

    protected final Class<T> getClassesNeededInLookupForEnablement() {
        return targetClass;
    }

    /**
     * Create an instance of this action which will operate against the passed
     * Lookup.  This method is used to create instances in popup menus, which
     * should be sensitive to the Lookup of some specific file or object,
     * regardless of the global selection.
     * <p/>
     * The default implementation tries to find a constructor of this 
     * subclass which takes a Lookup as an argument, and invoke it.  If
     * there is no such constructor, it will throw an exception.
     * 
     * @param lookup The Lookup
     * @return An instance of this action for this particular lookup
     */
    public Action createContextAwareInstance(Lookup lookup) {
        Class clazz = getClass();
        try {
            Constructor c = clazz.getConstructor(Lookup.class);
            GenericContextSensitiveAction result =
                    (GenericContextSensitiveAction) c.newInstance(lookup);
            result.init (targetClass);
            String name = (String) getValue(Action.NAME);
            if (name != null) {
                result.setDisplayName(name);
            }
            Icon icon = (Icon) getValue (Action.SMALL_ICON);
            if (icon != null) {
                result.setIcon(icon);
            }
            return result;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    clazz + " does not have a constructor that takes a Lookup", //NOI18N
                    ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(clazz.getName()).log(Level.SEVERE,
                    clazz + " does not have a constructor that takes a Lookup", //NOI18N
                    ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(clazz.getName()).log(Level.SEVERE,
                    clazz + " does not have a constructor that takes a Lookup", //NOI18N
                    ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(clazz.getName()).log(Level.SEVERE,
                    clazz + " does not have a constructor that takes a Lookup", //NOI18N
                    ex);
        } catch (SecurityException ex) {
            throw new AssertionError (ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(clazz.getName()).log(Level.SEVERE,
                    clazz + " does not have a constructor that takes a Lookup", //NOI18N
                    ex);
        }
        return this;
    }

    //Yes, it's a microoptimization, but why not...
    private Map <String, Object> map;
    private static final int ARR_PROP_COUNT = 6;
    private final String[] keys = new String[ARR_PROP_COUNT];
    private final Object[] vals = new Object[ARR_PROP_COUNT];
    public final Object getValue(String key) {
        Object result = null;
        int last = 0;
        for (last = 0; last < keys.length; last++) {
            if (keys[last] == null) {
                break;
            }
            if (key.equals(keys[last])) {
                result = vals[last];
                break;
            }
        }
        return result != null ? result : map == null ? null : map.get(key);
    }

    public final void putValue(String key, Object value) {
        int last = 0;
        for (last = 0; last < keys.length; last++) {
            if (keys[last] == null) {
                keys[last] = key;
                vals[last] = value;
                firePropertyChange (key, null, value);
                return;
            }
            if (key.equals(keys[last])) {
                Object old = vals[last];
                vals[last] = value;
                if (old != value) {
                    firePropertyChange (key, old, value);
                }
                return;
            }
            if (map == null) {
                map = new HashMap<String, Object>();
            }
            Object old = map.put (key, value);
            if (old != value) {
                firePropertyChange (key, old, value);
            }
        }
    }

    public final void setEnabled(boolean b) {
        boolean was = isEnabled();
        enabled = b;
        if (enabled != was) {
            firePropertyChange ("enabled", Boolean.valueOf(was),
                    Boolean.valueOf (enabled));
        }
    }

    private boolean enabled = true;
    public final boolean isEnabled() {
        return enabled;
    }

    private void firePropertyChange (String s, Object o, Object n) {
        PropertyChangeListener[] ll = (PropertyChangeListener[])
                l.toArray(new PropertyChangeListener[0]);
        if (ll.length != 0) {
            PropertyChangeEvent evt = new PropertyChangeEvent (this, s, o, n);
            for (int i = 0; i < ll.length; i++) {
                ll[i].propertyChange(evt);
            }
        }
    }

    private final List <PropertyChangeListener> l =
            Collections.synchronizedList (new LinkedList <PropertyChangeListener> ());

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        l.add (listener);
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        l.remove (listener);
    }

    private final N n = new N();
    final class N implements Sensor.Notifiable {
        public final void notify(Collection coll, Class clazz) {
            boolean old = enabled;
            enabled = checkEnabled(coll, clazz);
            if (old != enabled) {
                firePropertyChange ("enabled", Boolean.valueOf(old),  //NOI18N
                        Boolean.valueOf(enabled));
            }
        }
    }

    /**
     * Determine if the action should be enabled.  Called on changes in the
     * Lookup this action works against.  By default, the action is enabled
     * if the collection is not empty.  Override to add additional logic.
     * 
     * @param coll The collection of objects of the type passed to the 
     * constructor, which are present in the Lookup
     * @param clazz The type
     * @return whether or not the action should be enabled.
     */
    protected boolean checkEnabled(Collection <? extends T> coll, Class clazz) {
        return !coll.isEmpty();
    }

    protected final void setDisplayName (String name) {
        putValue (Action.NAME, name);
    }

    protected final void setDescription (String desc) {
        putValue (Action.SHORT_DESCRIPTION, desc);
    }

    protected final void setIcon (Icon icon) {
        putValue (Action.SMALL_ICON, icon);
    }

    protected final void setIcon (Image img) {
        Icon icon = new ImageIcon (img);
        setIcon (icon);
    }

    public final void actionPerformed (ActionEvent ae) {
        T t = (T) lookup.lookup(targetClass);
        assert t != null : "No instance of " + targetClass.getName() + 
                " in " + lookup;
        performAction (t);
    }

    /**
     * Actually perform the action
     * @param t The object from the lookup which this action will work against
     */
    protected abstract void performAction(T t);
}
