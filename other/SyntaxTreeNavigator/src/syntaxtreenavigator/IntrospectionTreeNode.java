/*DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/*Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/*The contents of this file are subject to the terms of either the GNU
/*General Public License Version 2 only ("GPL") or the Common
/*Development and Distribution License("CDDL") (collectively, the
/*"License"). You may not use this file except in compliance with the
/*License. You can obtain a copy of the License at
/*http://www.netbeans.org/cddl-gplv2.html
/*or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/*specific language governing permissions and limitations under the
/*License.  When distributing the software, include this License Header
/*Notice in each file and include the License file at
/*nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/*particular file as subject to the "Classpath" exception as provided
/*by Sun in the GPL Version 2 section of the License file that
/*accompanied this code. If applicable, add the following below the
/*License Header, with the fields enclosed by brackets [] replaced by
/*your own identifying information:
/*"Portions Copyrighted [year] [name of copyright owner]"
/*
/*Contributor(s):  */
package syntaxtreenavigator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Tim Boudreau
 */
public class IntrospectionTreeNode extends DefaultMutableTreeNode {
    private List <Method> methods;
    private Object obj;
    private Method method;
    private Object owner;
    static final Object NULL = new StringBuffer("[null]");
    static final Object ERROR = new StringBuffer ("[error]");
    private TreeNode parent;
    public IntrospectionTreeNode(Object o) {
        this.obj = o;
        assert o != null;
    }
    
    public IntrospectionTreeNode (Object owner, Method m) {
        assert m.getReturnType() != null;
        assert m.getParameterTypes().length == 0;
        this.method = m;
        this.owner = owner;
    }
    
    public Object getObject() {
        if (obj == null) {
            assert method != null;
            try {
                obj = method.invoke(owner, (Object[]) null);
                if (obj == null) {
                    obj = NULL;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                obj = ERROR;
            }
        }
        return obj;
    }
    
    private boolean isArray() {
        return getObject().getClass().isArray();
    }
    
    private boolean isPrimitiveArray() {
        Object o = getObject();
        return isArray() && (o instanceof int[] || o instanceof long[] || o instanceof short[]
                || o instanceof float[] || o instanceof double[] ||
                o instanceof byte[] || o instanceof char[] || 
                o instanceof boolean[]);
    }
    
    private int getArraySize() {
        if (isArray()) {
            if (isPrimitiveArray()) {
                return toObjectArray (getObject()).length;
            } else {
                return ((Object[]) getObject()).length;
            }
        }
        return 0;
    }
    
    boolean isArrayOrCollection() {
        return isArray() || getObject() instanceof Collection;
    }
    
    List elements;
    private List getElements() {
        if (elements == null) {
            elements = createElements();
        }
        return elements;
    }
    
    private List createElements() {
        if (isInvalid()) {
            return Collections.EMPTY_LIST;
        }
        Object o = getObject();
        List c = null;
        if (o instanceof Collection) {
            if (o instanceof List) {
                c = (List) o;
            } else {
                Collection col = (Collection) o;
                c = new ArrayList(col);
            }
        } else if (isArray()) {
            if (isPrimitiveArray()) {
                c = Arrays.asList (toObjectArray(o));
            } else {
                c = Arrays.asList((Object[])o);
            }
        }
        if (c != null) {
            return c;
        } else {
            throw new IllegalStateException("Not an array or collection: " + o);
        }
    }
    
    private IntrospectionTreeNode createNodeForCollectionElement (Object o) {
        IntrospectionTreeNode result = new IntrospectionTreeNode (o);
        ce2n.put (o, result);
        return result;
    }
    
    private final Map <Object, IntrospectionTreeNode> ce2n = new HashMap();
    private IntrospectionTreeNode nodeForCollectionElement (Object o) {
        IntrospectionTreeNode result = ce2n.get(o);
        if (result == null) {
            result = createNodeForCollectionElement (o);
        }
        return result;
    }
    
    private boolean accept (Method m) {
        boolean result = m.getParameterTypes().length == 0 && 
                m.getReturnType() != Void.TYPE;
        String nm = m.getName();
        result &= !"toString".equals(nm) &&
                !"hashCode".equals(nm) && !"clone".equals(nm)
                && !"getClass".equals(nm);
        return result;
    }
    
    private List <Method> getMethods() {
        if (methods == null) {
            methods = new ArrayList <Method>();
            Object obj = getObject();
            if (!NULL.equals(obj) && !ERROR.equals(obj)) {
                Method[] m = obj.getClass().getMethods();
                for (int i = 0; i < m.length; i++) {
                    if (accept(m[i])) {
                        methods.add (m[i]);
                    }
                }
            }
        }
        return methods;
    }

    Map <Method, IntrospectionTreeNode> m2n = new HashMap<Method, IntrospectionTreeNode>();
    
    private IntrospectionTreeNode createNode(Method m) {
        IntrospectionTreeNode result = new IntrospectionTreeNode (getObject(), m);
        m2n.put(m, result);
        return result;
    }
    
    private IntrospectionTreeNode getNode (Method m) {
        IntrospectionTreeNode result = (IntrospectionTreeNode) m2n.get(m);
        if (result == null) {
            result = createNode (m);
        }
        return result;
    }
    
    IntrospectionTreeNode singleChild;
    private IntrospectionTreeNode getSingleChild() {
        if (singleChild == null) {
            singleChild = new IntrospectionTreeNode (getObject());
        }
        return singleChild;
    }
    
    public TreeNode getChildAt(int childIndex) {
        if (isInvalid()) {
            throw new IndexOutOfBoundsException ("No children; " + childIndex);
        }
        if (isArrayOrCollection()) {
            return nodeForCollectionElement (getElements().get(childIndex));
        }
        if (method == null) {
            Method m = getMethods().get(childIndex);
            return getNode (m);
        } else {
            if (childIndex != 0) {
                throw new IndexOutOfBoundsException ("" + childIndex);
            }
            return getSingleChild();
        }
    }

    public int getChildCount() {
        return isInvalid() ? 0 : method == null ? getMethods().size() : isArrayOrCollection() ?
            getElements().size() : 1;
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        if (singleChild == node) {
            return 0;
        }
        if (isArrayOrCollection()) {
            return node instanceof IntrospectionTreeNode ? getElements().indexOf (
                    ((IntrospectionTreeNode)node).getUserObject()) : -1;
        }
        IntrospectionTreeNode nd = 
                (IntrospectionTreeNode) node;
        List <Method> methods = getMethods();
        return methods.indexOf(nd.getMethod());
    }
    
    public Method getMethod() {
        return method;
    }

    public boolean getAllowsChildren() {
        if (isInvalid()) {
            return false;
        } else if (method == null) {
            return true;
        } else {
            return getMethods().size() > 0;
        }
    }

    public boolean isLeaf() {
        return isInvalid() ? true : method == null ? isArrayOrCollection() ?
            getElements().size() > 0 : getMethods().size() == 0 : 
            false;
    }
    
    boolean isInvalid() {
        Object o = getObject();
        return NULL.equals(o) || ERROR.equals (o);
    }

    public Enumeration children() {
        if (isArrayOrCollection()) {
            List l = getElements();
            Vector v = new Vector (l.size());
            for (Iterator i=l.iterator(); i.hasNext();) {
                Object o = i.next();
                v.add(nodeForCollectionElement(o));
            }
            return v.elements();
        }
        List <Method> m = getMethods();
        Vector <IntrospectionTreeNode> result = new Vector <IntrospectionTreeNode> (m.size());
        if (!isInvalid()) {
            if (method == null) {
                result.add (getSingleChild());
            } else {
                for (Method method : m) {
                    result.add (getNode(method));
                }
            }
        }
        return result.elements();
    }
    
    public String toString() {
        return getMethod() == null ? getObject().getClass().getName() : 
            "<html><b>" + getMethod().getName() + 
            "()</b> : <font color=#AAAAAA>" + 
            getMethod().getReturnType().getName();
    }
    
    public String getMethodString() {
        return getMethod() != null ? getMethod().toGenericString() :
            " ";
    }
    public void setUserObject(Object object) {
        throw new UnsupportedOperationException();
    }
    
    public Object getUserObject() {
        return getObject();
    }
    
    public String getString() {
        List l = null;
        if (isArray()) {
            if (isPrimitiveArray()) {
                l = Arrays.asList(toObjectArray(getObject()));
            } else {
                l = Arrays.asList((Object[]) getObject());
            }
        }
        StringBuffer sb = new StringBuffer();
        if (l != null) {
            int ix=0;
            for (Iterator i=l.iterator(); i.hasNext();) {
                sb.append (ix);
                sb.append (':');
                sb.append (i.next());
                sb.append ("\n");
                ix++;
            }
        } else {
            sb.append (getObject().toString());
        }
        return sb.toString();
    }
    
    public static Object[] toObjectArray(Object array) {
        if (array instanceof Object[]) {
            return (Object[]) array;
        }

        if (array instanceof int[]) {
            int i;
            int k = ((int[]) array).length;
            Integer[] r = new Integer[k];

            for (i = 0; i < k; i++)
                r[i] = new Integer(((int[]) array)[i]);

            return r;
        }

        if (array instanceof boolean[]) {
            int i;
            int k = ((boolean[]) array).length;
            Boolean[] r = new Boolean[k];

            for (i = 0; i < k; i++)
                r[i] = ((boolean[]) array)[i] ? Boolean.TRUE : Boolean.FALSE;

            return r;
        }

        if (array instanceof byte[]) {
            int i;
            int k = ((byte[]) array).length;
            Byte[] r = new Byte[k];

            for (i = 0; i < k; i++)
                r[i] = new Byte(((byte[]) array)[i]);

            return r;
        }

        if (array instanceof char[]) {
            int i;
            int k = ((char[]) array).length;
            Character[] r = new Character[k];

            for (i = 0; i < k; i++)
                r[i] = new Character(((char[]) array)[i]);

            return r;
        }

        if (array instanceof double[]) {
            int i;
            int k = ((double[]) array).length;
            Double[] r = new Double[k];

            for (i = 0; i < k; i++)
                r[i] = new Double(((double[]) array)[i]);

            return r;
        }

        if (array instanceof float[]) {
            int i;
            int k = ((float[]) array).length;
            Float[] r = new Float[k];

            for (i = 0; i < k; i++)
                r[i] = new Float(((float[]) array)[i]);

            return r;
        }

        if (array instanceof long[]) {
            int i;
            int k = ((long[]) array).length;
            Long[] r = new Long[k];

            for (i = 0; i < k; i++)
                r[i] = new Long(((long[]) array)[i]);

            return r;
        }

        if (array instanceof short[]) {
            int i;
            int k = ((short[]) array).length;
            Short[] r = new Short[k];

            for (i = 0; i < k; i++)
                r[i] = new Short(((short[]) array)[i]);

            return r;
        }

        throw new IllegalArgumentException();
    }    
}
