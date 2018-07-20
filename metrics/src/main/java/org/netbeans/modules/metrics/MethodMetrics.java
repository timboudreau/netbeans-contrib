/*
 * MethodMetrics.java
 *
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
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.netbeans.modules.classfile.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author  tball
 * @version
 */
public class MethodMetrics implements Comparable, NodeHandler {
    private static final boolean debug = false;

    private ClassMetrics cm;
    private Method method;
    private Parameter[] tramps;
    private ReferenceScanner refScanner;
    private String name;
    private String internalName;
    private String signature;
    private PropertyChangeSupport changeSupport = null;

    private static final Parameter[] noTramps = new Parameter[0];


    /** Creates new MethodMetrics */
    public MethodMetrics(ClassMetrics cm, Method method) {
	this.cm = cm;
        this.method = method;
        refScanner = new ReferenceScanner(method);
        tramps = findTramps();
    }
    
    public Code getCode() {
        return method.getCode();
    }
    
    public Iterator getClassReferences() {
        return refScanner.getClassReferences();
    }
    
    public Iterator getMethodReferences() {
        return refScanner.getMethodReferences();
    }

    public int getMethodReferencesCount() {
	return refScanner.getMethodReferencesCount();
    }
    
    public int getCodePathCount() {
        return refScanner.getCodePathCount();
    }
    
    public int getMessageSendCount() {
        return refScanner.getMessageSendCount();
    }

    /**
     * Returns the name of this method, minus any parameter
     * or return type information.  MethodMetrics associated with
     * other overloaded methods will return the same value.  Use
     * getFullName() to differentiate between them.
     *
     * @return the name of the method associated with this instance.
     */
    public String getName() {
        if (name == null) 
            name = expandName(getInternalName());
        return name;
    }

    public String getInternalName() {
        if (internalName == null) 
            internalName = method.getName();
        return internalName;
    }

    public String getSignature() {
        if (signature == null)
            signature = method.getDescriptor();
        return signature;
    }

    /**
     * Returns a unique description of this method, consisting of
     * the method name and its parameters (but no return type).
     * Every method in a class will return unique "full" names.
     *
     * @return the full name of the method associated with this instance.
     */
    public String getFullName() {
        if (fullName == null) {
            String s = method.getDeclaration();
            // strip off return type (doesn't make full name any more unique)
            int i = s.indexOf(' ');
            fullName = expandName((i == -1) ? s : s.substring(i + 1));
        }
        return fullName;
    }
    private String fullName;

    public String getNodeName() {
	return cm.getName().getSimpleName() + ':' + getFullName();
    }

    /*
     * Replace <init> in constructors' names with the method's classname,
     * and expand <clinit> a little.  Hopefully this will increase
     * comprehension by engineers who don't know classfile internals.
     */
    private String expandName(String s) {
        if (s.indexOf("<init>") == 0) { //NOI18N
            // Substitute classname for <init> in constructors
            String cls = method.getClassFile().getName().getSimpleName();
            s = cls + s.substring(6);  // strlen("<init>")
        } else if (s.indexOf("<clinit>") == 0) //NOI18N
            // Explain what clinit means
            s = "<class_initialization>" + s.substring(8);
        return s;
    }

    public boolean isPrivate() {
        return method.isPrivate();
    }

    public boolean isSynthetic() {
        return method.isSynthetic();
    }

    public boolean hasTramps() {
        return (tramps.length > 0);
    }

    public Iterator getTramps() {
        return Arrays.asList(tramps).iterator();
    }

    public int getTrampCount() {
        return tramps.length;
    }

    /**
     * For method name sorting:  compares the names of each 
     * method.  If the names are equal, it then compares their 
     * signatures before returning.
     *
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     * 
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this Object.
     */
    public int compareTo(Object o) {
        MethodMetrics mm = (MethodMetrics)o; // may throw ClassCastException
        int c = getName().compareTo(mm.getName());
        if (c == 0)
            // compare with signatures
            c = getFullName().compareTo(mm.getFullName());
        return c;
    }

    private Parameter[] findTramps() {
        if (implementsInterface() || overridesSuperclass())
            return noTramps;

        String sig = getSignature();
        if (sig.charAt(0) != '(')
            throw new InternalError("assert failure");

        int idx = 1;
        int offset = 0;
        int nParams = 0;
        Vector v = new Vector();
        char ch;
        while ((ch = sig.charAt(idx)) != ')') {
            switch (ch) {
              case 'B': case 'C': case 'F': 
              case 'I': case 'S': case 'Z':
                  v.add(new Parameter(String.valueOf(ch), nParams++, offset));
                  offset++;
                  break;

              case 'D': case 'J':
                  v.add(new Parameter(String.valueOf(ch), nParams++, offset));
                  offset += 2;
                  break;

              case 'L': {
                  int n = idx;
                  while (sig.charAt(idx) != ';')
                      idx++;
                  String clsName = sig.substring(n, idx);
                  v.add(new Parameter(clsName, nParams++, offset));
                  offset++;
                  break;
              }

              case '[': {
                  int n = idx;
                  while (sig.charAt(idx) == '[')
                      idx++;
                  if (sig.charAt(idx) == 'L')
                      while (sig.charAt(idx) != ';')
                          idx++;
                  String arrName = sig.substring(n, idx);
                  v.add(new Parameter(arrName, nParams++, offset));
                  offset++;
                  break;
              }

              case ')':
                  break;

              default:
                  throw new InternalError("assert failure: ch=" + ch);
            }
            idx++;
        }

        int n = v.size();
        if (n == 0)
            return noTramps;

        Parameter[] params = new Parameter[n];
        for (int i = 0; i < n; i++)
            params[i] = (Parameter)v.get(i);
        ParameterScanner ps = 
            new ParameterScanner(method);
        return ps.scan(params);
    }

    // Does this method implement an interface definition?
    private boolean implementsInterface() {
        try {
            ClassFile cf = method.getClassFile();
            Iterator iter = cf.getInterfaces().iterator();
            while (iter.hasNext()) {
                ClassName cn = (ClassName)iter.next();
                ClassMetrics cm = 
                    ClassMetrics.getClassMetrics(cn);
                cf = cm.getClassFile();
                if (cf.getMethod(getInternalName(), getSignature()) != null)
                    return true;
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("MethodMetrics.implementsInterfaces: " + e);
            }
        }
        return false;
    }

    /* Does this method override a superclass method?
     * It uses ClassMetrics objects instead of directly using ClassFiles
     * to use this module's class lookup facility.
     */
    private boolean overridesSuperclass() {
        try {
            ClassFile cf = method.getClassFile();
            ClassMetrics cm = 
                ClassMetrics.getClassMetrics(cf.getName());
            ClassMetrics superCM = cm.getSuperClass();
            while (superCM != null) {
                cm = superCM;
                cf = cm.getClassFile();
                if (cf.getMethod(getInternalName(), getSignature()) != null)
                    return true;
                superCM = cm.getSuperClass();
            }
        } catch (Exception e) {
            if (debug)
                System.err.println("MethodMetrics.overridesSuperclass: " + e);
        }
        return false;
    }

    public Metric[] getMetrics() {
        return cm.getMetrics();
    }

    public int getMetricValue(Metric m) {
	try {
	    return m.getMetricValue(this).intValue();
	} catch (NoSuchMetricException e) {
	    return 0;
	}
    }

    public int getWarningLevel() {
        int warnLevel = Metric.METRIC_OKAY;
	MethodApprovals ma = getMethodApprovals();
	Metric[] metrics = cm.getMetrics();
        int n = metrics.length;
        for (int i = 0; i < n; i++) {
            Metric m = metrics[i];
            if (m.isMethodMetric())
		warnLevel = Math.max(warnLevel, getWarningLevel(m, ma));
        }
	return warnLevel;
    }

    /**
     * Returns the warning level from the approvals file, if any.
     */
    public int getWarningLevel(Metric m) {
	return getWarningLevel(m, getMethodApprovals());
    }

    private int getWarningLevel(Metric m, MethodApprovals ma) {
	if (ma != null) {
	    try {
		int currentMetric = m.getMetricValue(this).intValue();
		int approvalLevel = ma.getApprovalLevel(m);
		if (approvalLevel > -1)
		    // if an approval exists...
		    return (currentMetric > approvalLevel) ? 
			Metric.METRIC_FAIL : Metric.METRIC_OKAY;
	    } catch (NoSuchMetricException e) {
		// just fall through...
	    }
	}

        // Use system property warning level
	try {
	    return m.getWarningLevel(this);
	} catch (NoSuchMetricException e) {
	}
	return Metric.METRIC_OKAY;
    }

    private MethodApprovals getMethodApprovals() {
        // Check whether an approved warning level has been stored.
        MethodApprovals ma = null;

        ApprovalsFile apFile = null;
        if (cm.getFileObject() != null) // should always be true.
            apFile = cm.getApprovalsFile();
        if (apFile != null) {
            ClassApprovals aps = 
		apFile.get(cm.getName().getSimpleName());
            if (aps != null) {
		ma = aps.getMethodApprovals(getFullName());
	    }
        }
	return ma;
    }

    public void addApproval(ApprovalsFile appFile, MetricValue mv) {
	String clsName = cm.getName().getSimpleName();
	String methodName = getFullName();
	ClassApprovals app = appFile.get(clsName);
	if (app == null)
	    app = new ClassApprovals(clsName);
	MethodApprovals mapp = app.getMethodApprovals(methodName);
	if (mapp == null) {
	    mapp = new MethodApprovals(methodName);
	    app.addMethodApprovals(mapp);
	}
	mapp.addApproval(mv);
	appFile.add(app); // will overwrite any old entry
    }

    TrafficNode.Light getWarningLight() {
        int warnLevel = getWarningLevel();
        if (warnLevel == Metric.METRIC_FAIL)
            return TrafficNode.Light.RED;
        else if (warnLevel == Metric.METRIC_WARN)
            return TrafficNode.Light.YELLOW;
        else
            return TrafficNode.Light.NONE;
    }

    /** Metrics have changed, reset threshold icon. */
    public void resetWarningLight() {
	firePropertyChange(ClassMetrics.LIGHT_PROP, null, getWarningLight());
    }

    private void firePropertyChange(String propertyName, 
				    Object oldValue, Object newValue) {
	if (changeSupport != null)
	    // if there are any listeners...
	    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
	if (listener == null) {
	    return;
	}
	if (changeSupport == null) {
	    changeSupport = new PropertyChangeSupport(this);
	}
	changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	if (listener == null || changeSupport == null) {
	    return;
	}
	changeSupport.removePropertyChangeListener(listener);
    }

    class Parameter {
        String type;
        int index;
        int stackIndex;
        boolean referenced;

        Parameter(String type, int index, int stackIndex) {
            this.type = type;
            this.index = index;
            this.stackIndex = stackIndex;
            referenced = false;
        }

        int getIndex() {
            return index;
        }

        int getStackIndex() {
            return stackIndex;
        }

        void setReferenced(boolean b) {
            referenced = b;
        }
    }
}
