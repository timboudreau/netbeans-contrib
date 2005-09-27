/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.venice.sourcemodel;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.Constructor;
import org.netbeans.jmi.javamodel.Field;
import org.netbeans.jmi.javamodel.Method;
import org.netbeans.jmi.javamodel.NamedElement;
import org.netbeans.modules.venice.model.Decorator;
import org.netbeans.modules.venice.model.Model;
import org.netbeans.modules.venice.sourcemodel.api.SrcConstants;
/**
 * SrcModel is a
 *
 * @author Tim Boudreau
 */
public class SrcModel implements Model, Decorator {
    private JMIObjectWrapper root;
    
    public SrcModel(NamedElement root) {
	this.root = new JMIObjectWrapper(root);
    }

    public Object getRoot() {
	return root;
    }

    public Decorator getDecorator(Object o) {
	return this;
    }

    public String getDisplayName(Object o) {
	return toWrapper(o).getDisplayName();
    }

    public Object getProperty(String key, Object o) {
	return toWrapper(o).getProperty(key);
    }

    public Action getAction(String key, Object o) {
	return toWrapper(o).getAction(key);
    }

    public boolean isValid(Object o) {
	return toWrapper(o).isValid();
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl, Object o) {
	Set s = (Set) listenersToObjects.get(pcl);
	if (s == null) {
	    s = new HashSet();
	    listenersToObjects.put (pcl, s);
	}
	s.add (o);
	if (!listeningTo.containsKey(o)) {
	    Integer in = new Integer (1);
	    listeningTo.put (o, in);
	    startListeningTo (o);
	} else {
	    Integer in = (Integer) listeningTo.get(o);
	    in = new Integer (in.intValue() + 1);
	    listeningTo.put (o, in);
	}
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl, Object o) {
	Set s = (Set) listenersToObjects.get(pcl);
	if (s == null) {
	    return;
	}
	s.remove(o);
	Integer in = (Integer) listeningTo.get(o);
	in = new Integer (in.intValue() - 1);
	if (in.intValue() == 0) {
	    stopListeningTo(o);
	    listenersToObjects.remove(pcl);
	    listeningTo.remove(o);
	}
    }

    public ChildrenHandle getChildren(Object o, boolean calculate) {
	return getChildren (o, CHILDREN_DEFAULT, calculate);
    }

    public ChildrenHandle getChildren(Object o, String childrenType, boolean calculate) {
	return toWrapper(o).getChildren(childrenType, calculate);
    }
    
    private void startListeningTo (Object o) {
	
    }
    
    private void stopListeningTo (Object o) {
	
    }
    
    private JMIObjectWrapper toWrapper (Object o) {
	return (JMIObjectWrapper) o;
    }
    
    private Map listenersToObjects = new HashMap();
    private Map listeningTo = new HashMap();
    
    private class JMIObjectWrapper implements SrcConstants {
	private final NamedElement obj;
	private final String displayName;
	private Map kids = null;
	public JMIObjectWrapper (NamedElement obj) {
	    this.obj = obj;
	    if (obj instanceof Constructor) {
		//Constructors return nothing for their name, so handle
		//specially
		displayName = ((Constructor) obj).getType().getName();
	    } else {
		displayName = obj.getName();
	    }
	}

        public String getDisplayName() {
	    return displayName;
        }

        public Object getProperty(String key) {
	    if (KEY_KIND.equals(key)) {
		if (obj instanceof Constructor) {
		    return VAL_CONSTRUCTOR;
		} else if (obj instanceof Method) {
		    return VAL_METHOD;
		} else if (obj instanceof Field) {
		    return VAL_FIELD;
		} else if (obj instanceof ClassDefinition) {
		    return VAL_CLASS;
		} else {
		    throw new IllegalStateException ("What is this? " + obj);
		}
	    } else {
		return null;
	    }
        }

        public Action getAction(String key) {
	    return null;
        }

        public boolean isValid() {
	    return obj.isValid();
        }

        public ChildrenHandle getChildren(String childrenType, boolean calculate) {
	    List l = kids == null ? null : (List) kids.get(childrenType);
	    if (!isValid()) {
		return new Decorator.ChildrenHandle.Fixed (obj, childrenType);
	    } else if (children != null) {
		return new Decorator.ChildrenHandle.Fixed (obj, children, childrenType);
	    } else {
		//do something
	    }
        }
    }
}
