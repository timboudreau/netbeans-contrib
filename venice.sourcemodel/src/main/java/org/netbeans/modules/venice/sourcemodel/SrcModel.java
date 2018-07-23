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

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.venice.sourcemodel;
import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jmi.model.Feature;
import javax.jmi.reflect.InvalidObjectException;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.ClassMember;
import org.netbeans.jmi.javamodel.Constructor;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.ElementReference;
import org.netbeans.jmi.javamodel.Field;
import org.netbeans.jmi.javamodel.Import;
import org.netbeans.jmi.javamodel.Method;
import org.netbeans.jmi.javamodel.NamedElement;
import org.netbeans.modules.javacore.jmiimpl.javamodel.ResourceImpl;
import org.netbeans.modules.venice.model.Decorator;
import org.netbeans.modules.venice.model.Decorator.ChildrenHandle;
import org.netbeans.modules.venice.model.Model;
import org.netbeans.modules.venice.sourcemodel.api.SrcConstants;
import org.openide.ErrorManager;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;/**
 * SrcModel is a
 *
 * @author Tim Boudreau
 */
public class SrcModel implements Model, Decorator {
    private JMIObjectWrapper root;
    
    public SrcModel(NamedElement root) {
	this.root = new JMIObjectWrapper(root);
    }
    
    public String getModelKind() {
	return SrcConstants.MODEL_KIND_JAVA_SOURCE;
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
	//do nothing for now
    }
    
    private void stopListeningTo (Object o) {
	//do nothing for now
    }
    
    private JMIObjectWrapper toWrapper (Object o) {
	return (JMIObjectWrapper) o;
    }
    
    private Map listenersToObjects = new HashMap();
    private Map listeningTo = new HashMap();
    
    private static class JMIObjectWrapper implements SrcConstants {
	private final NamedElement obj;
	private String displayName;
	private Map kids = new HashMap(); //XXX make lazy
	public JMIObjectWrapper (NamedElement obj) {
	    this.obj = obj;
	    try {
		if (obj instanceof Constructor) {
		    //Constructors return nothing for their name, so handle
		    //specially
		    displayName = ((Constructor) obj).getType().getName();
		} else {
		    displayName = obj.getName();
		}
	    } catch (InvalidObjectException e) {
		ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
		displayName = "Invalid object";
	    }
	}
	
	NamedElement getElement() {
	    return obj;
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
	    } else if (KEY_URL.equals(key)) {
		try {
		    ResourceImpl r = (ResourceImpl) obj.getResource();
		    return r.getFileObject().getURL();
		} catch (Exception e) {
		    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, 
			    e);
		    return null;
		}
	    } else if (KEY_OFFSET.equals(key)) {
		int[] result = new int[2];
		if (obj.isValid()) {
		    try {
			result[0] = obj.getStartOffset();
			result[1] = obj.getEndOffset();
		    } catch (Exception e) {
			ErrorManager.getDefault().notify (ErrorManager.INFORMATIONAL, e);
		    }
		}
		return result;
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

	private String[] kidTypes = new String[] { CHILDREN_CLOSURE, CHILDREN_MEMBERS, CHILDREN_USAGES, CHILDREN_PARENTCLASS };
        public ChildrenHandle getChildren(String childrenType, boolean calculate) {
	    if (!isValid()) {
		return new Decorator.ChildrenHandle.Fixed (obj, childrenType);
	    }
	    
	    Object o;
	    synchronized (kids) {
		o = kids.get (childrenType);
	    }
	    
	    if (o instanceof Decorator.ChildrenHandle) {
		return (Decorator.ChildrenHandle) o;
	    } else if (o instanceof List) {
		return new Decorator.ChildrenHandle.Fixed (obj, (List) o, childrenType);
	    } else if (calculate) {
		int ix = Arrays.asList(kidTypes).indexOf(childrenType);
		ChildrenHandle result = null;
		switch (ix) {
		    case 0 :
			result = new MemberClosureChildrenHandle (this, CHILDREN_CLOSURE, true);
			break;
		    case 2 :
			result = new UsagesChildrenHandle (this, CHILDREN_USAGES);
			break;
		    case 3 :
			result = new ParentClassChildrenHandle (this, CHILDREN_PARENTCLASS);
			break;
		    case 1 :
		    default :
			if (obj instanceof ClassDefinition) {
			    result = new MembersChildrenHandle (this, CHILDREN_MEMBERS);
			} else {
			    result = new Decorator.ChildrenHandle.Fixed (obj, childrenType);
			}
			break;
		}
		synchronized (kids) {
		    //Another thread could have entered between the 
		    //synch blocks;  rare, but double check it
		    Object o2 = kids.get(childrenType);
		    if (o2 instanceof Decorator.ChildrenHandle) {
			result = (Decorator.ChildrenHandle) o2;
		    } else if (o2 instanceof List) {
			result = new Decorator.ChildrenHandle.Fixed (obj, (List) o2, childrenType);
		    } else {
			kids.put (childrenType, result);
		    }
		}
		return result;
	    } else {
		return null;
	    }
        }
    }
    
    private abstract static class AsynchChildren implements Decorator.ChildrenHandle, Runnable {
	protected final JMIObjectWrapper w;
	private final String type;
	protected volatile boolean done = false;
	protected AsynchChildren (JMIObjectWrapper w, String type) {
	    this.w = w;
	    this.type = type;
	}
	
	public void run() {
	    if (!EventQueue.isDispatchThread()) {
		List l = getList();
		System.err.println(this + " found " + l.size() + " items");
		synchronized (w.kids) {
		    w.kids.put (type, toWrappers(l));
		}
		done = true;
		EventQueue.invokeLater(this);
		synchronized (this) {
		    notifyAll();
		}
	    } else {
		fire();
	    }
	}
	
	void fire() {
	    if (clis != null) {
		System.err.println(this + " firing");
		clis.stateChanged(new ChangeEvent(this));
	    }
	}
	
	public String toString() {
	    return super.getClass() + " " + type + " done=" + done;
	}

        public int getState() {
	    return !done ? STATE_COLLECTING_CHILDREN : STATE_CHILDREN_COLLECTED;
        }

	private ChangeListener clis = null;
	private Task task = null;
        public void addChangeListener(ChangeListener cl) {
	    System.err.println("Add change listener " + cl);
	    if (this.clis != cl && this.clis != null) {
		throw new IllegalStateException (clis + " is already listening");
	    }
	    this.clis = cl;
	    System.err.println("Post read task for " + this);
	    synchronized (this) {
		task = rp.post(this);
	    }
        }

        public void removeChangeListener(ChangeListener cl) {
	    if (clis != cl) {
		throw new IllegalStateException("Removed a listener not present:" + cl);
	    }
	    this.clis = null;
	    Task task = null;
	    synchronized (this) {
		task = this.task;
	    }
	    if (task != null) {
		task.cancel();
		task = null;
		synchronized(this) {
		    notifyAll();
		}
	    }
        }

        public Object getModelObject() {
	    return w;
        }

        public List getChildren() {
	    if (!done) {
		return Collections.EMPTY_LIST;
	    } else {
		Object o;
		synchronized (w.kids) {
		    o = w.kids.get(type);
		}
		assert o == null || o instanceof List || o == this;
		if (o instanceof List) {
		    return (List) o;
		}
		System.err.println(" got no children from map");
		return Collections.EMPTY_LIST;
	    }
        }

        public String getKind() {
	    return type;
        }
	
	protected abstract List getList();
    }
    
    private static List toWrappers (Collection c) {
	List result = new ArrayList (c.size());
	for (Iterator i=c.iterator(); i.hasNext();) {
	    NamedElement ne = (NamedElement) i.next();
	    result.add (new JMIObjectWrapper(ne));
	}
	return result;
    }
    
    private static class MemberClosureChildrenHandle extends AsynchChildren {
	private final boolean returnClassMembers;
	protected MemberClosureChildrenHandle (JMIObjectWrapper w, String type, boolean useMethods) {
	    super (w, type);
	    this.returnClassMembers = useMethods;
	}
	
        protected List getList() {
	    return childrenOf (w.getElement());
        }
	
	List childrenOf (Element e) {
	    List l = new ArrayList();
	    childrenOf (e, l);
	    List result = new ArrayList();
	    for (Iterator i=l.iterator(); i.hasNext();) {
		Element curr = (Element) i.next();
		if (curr instanceof ElementReference) {
		    ElementReference me = (ElementReference) curr;
		    Element el = me.getElement();
		    if (el instanceof ClassMember) {
			ClassMember meth = (ClassMember) me.getElement();
			ClassDefinition clazz = meth instanceof Method ? ((Method) meth).getDeclaringClass() :
			    meth instanceof Field ? ((Field) meth).getDeclaringClass() : null;
			if (returnClassMembers || clazz != null) {
			    result.add (returnClassMembers ? (NamedElement) meth : (NamedElement) clazz);
			}
		    } else {
			System.err.println("Got a mystery thing " + el.getClass() + " - " + el);
		    }
		}
	    }
	    
	    return result;
	}
	
	void childrenOf (Element e, Collection c) {
	    Collection kids;
	    try {
		kids = e.getChildren();
	    } catch (NullPointerException npe) { //JavaCore bug for some kinds of elements
		return;
	    }
	    c.addAll (kids);
	    for (Iterator i=kids.iterator(); i.hasNext();) {
		Object o = i.next();
		if (o instanceof Element) {
		    childrenOf ((Element) o, c);
		}
	    }
	}
    }
    
    private static class ClassClosureChildrenHandle extends AsynchChildren {
	protected ClassClosureChildrenHandle (JMIObjectWrapper w, String type) {
	    super (w, type);
	}
	
        protected List getList() {
	    List /* <Import> */ imports = w.getElement().getResource().getImports();
	    List result = new ArrayList();
	    //XXX this gets it for the whole file, not just the class in question
	    for (Iterator i=imports.iterator(); i.hasNext();) {
		//XXX handle wildcard imports
		result.addAll(((Import) i.next()).getImportedElements());
	    }
	    
	    return result.isEmpty() ? Collections.EMPTY_LIST : result;
        }
    }    
    
    private static class MembersChildrenHandle extends AsynchChildren {
	protected MembersChildrenHandle (JMIObjectWrapper w, String type) {
	    super (w, type);
	}
	
        protected List getList() {
	    ArrayList /* <NamedElement> */ result = new ArrayList();
	    if (w.getElement() instanceof ClassDefinition) {
		ClassDefinition cd = (ClassDefinition) w.getElement();
		for (Iterator i=cd.getFeatures().iterator(); i.hasNext();) {
		    Object o = i.next();
		    if (o instanceof Method || o instanceof Field || o instanceof ClassDefinition) {
			result.add (o);
		    }
		}
	    }
	    return result.isEmpty() ? Collections.EMPTY_LIST : result;
        }
    }
    
    private static class UsagesChildrenHandle extends AsynchChildren {
	protected UsagesChildrenHandle (JMIObjectWrapper w, String type) {
	    super (w, type);
	}
	
        protected List getList() {
	    Set results = new HashSet();
	    Collection c = w.getElement().getReferences();
	    for (Iterator i=c.iterator(); i.hasNext();) {
		ClassMember f = findEnclosingFeature((Element) i.next());
		if (f != null) {
		    results.add(f);
		}
	    }
	    return new ArrayList(results);
        }
	
	protected ClassMember findEnclosingFeature (Element e) {
	    while (!(e instanceof ClassMember) && e != null) {
		e = (Element) e.refImmediateComposite();
	    }
	    return (ClassMember) e;
	}
    }
    
    private static class ParentClassChildrenHandle extends AsynchChildren {
	protected ParentClassChildrenHandle (JMIObjectWrapper w, String type) {
	    super (w, type);
	}

        protected List getList() {
	    NamedElement el  = w.getElement();
	    if (el instanceof ClassDefinition) {
		Collections.singletonList( ((ClassDefinition) el).getSuperClass() );
	    } else if (el instanceof ClassMember) {
		return Collections.singletonList( ((ClassMember) el).getDeclaringClass() );
	    }
	    //???
	    return Collections.EMPTY_LIST;
        }
    }
    
    
    private static RequestProcessor rp = new RequestProcessor();
}
