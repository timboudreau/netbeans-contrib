/*
 * ClassMetrics.java
 *
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.netbeans.modules.classfile.*;

import org.openide.src.Identifier;
import org.openide.src.ClassElement;
import org.openide.filesystems.*;

import org.netbeans.api.java.classpath.ClassPath;

import java.beans.*;
import java.io.*;
import java.util.*;

/**
 * The collected metrics for a specific Java class file.  
 * The class' information is shared by all
 * metrics and so only need to be retrieved once.
 *
 * @author  tball
 * @version
 */
public class ClassMetrics extends FileChangeAdapter implements NodeHandler {

    Metric[] metrics;
    ClassName className;
    ClassFile classFile;
    ClassMetrics parent;
    FileObject fileObject = null;
    private Set methods;
    private Set dependentClasses = null;        // classes this class references
    private Set clientClasses = new TreeSet();  // classes which reference this class
    private Set childClasses = new TreeSet();   // classes which inherit from this class
    private PropertyChangeSupport changeSupport = null;
    
    private static final boolean debug = false;
    
    private static Map classes = new HashMap();

    public static final String METRICS_PROP = "metrics";
    public static final String LIGHT_PROP = "light";

    private static void putCM(ClassName name, ClassMetrics cm) {
        if (debug)
            System.out.println("adding \"" + name.getSimpleName() + "\"");

        classes.put(name, cm);
    }

    private static ClassMetrics getCM(ClassName name) {
        ClassMetrics cm = (ClassMetrics)classes.get(name);
        if (debug && cm != null)
            System.out.println("retrieved \"" + name.getSimpleName() + "\"");
        return cm;
    }

    private void addDependentClass(ClassName clsName) {
        if (isClass(clsName) && !clsName.equals(className))
            dependentClasses.add(clsName);
    }

    private static boolean isClass(ClassName clsName) {
        // Make sure this isn't a primitive array trying to pass itself off.
        String type = clsName.getType();
        boolean b = (type.startsWith("[") && type.indexOf('L') == -1);
        if (debug && b)
            System.out.println("isClass: ignored " + clsName);
        return !b;
    }

    public static ClassMetrics getClassMetrics(ClassName name) throws IOException {
        return getClassMetrics(name, true);
    }
    
    public static ClassMetrics getClassMetrics(ClassName name, boolean fetch) throws IOException {
        ClassMetrics cm = getCM(name);
        if (cm != null || !fetch)
            return cm;

        cm = new ClassMetrics(name);
        InputStream classBytes = 
            new BufferedInputStream(cm.lookupClass(), 4096);
        cm.loadClass(classBytes);
        putCM(name, cm);
        return cm;
    }
    
    public static ClassMetrics getClassMetrics(final FileObject classFO) throws IOException {
        // We don't know the full name of the class yet, so it can't be
        // looked up until the class is loaded (or reloaded).
        InputStream classBytes = null;
        if (classFO != null)
            classBytes = classFO.getInputStream();
        ClassMetrics cm = new ClassMetrics(null);
        cm.loadClass(classBytes);
        ClassName className = cm.getName();
        ClassMetrics oldCM = getCM(className);
        if (oldCM != null)
            return oldCM;
        cm.setFileObject(classFO);
        putCM(className, cm);
        classFO.addFileChangeListener(cm);
        return cm;
    }

    /**
     * @return the ClassFile associated with this ClassMetrics object.
     */
    public ClassFile getClassFile() {
        return classFile;
    }

    /**
     * Returns the ClassMetrics object for this classfile's superclass.
     * Null is returned if no superclass, or it isn't loadable.
     *
     * @return superclass ClassMetrics object, or null if not found.
     */
    public ClassMetrics getSuperClass() {
	if (parent == null) {
	    ClassName cn = classFile.getSuperClass();
	    if (cn != null) {   // true for java.lang.Object
		try {
		    parent = getClassMetrics(cn, true);
		} catch (IOException e) {
		    // drop through and return a null parent.
		};
	    }
	}
        return parent;
    }
    
    /** Creates new ClassMetrics */
    private ClassMetrics(ClassName name) {
	className = name;
	metrics = MetricsLoader.createMetricsSet(this);
    }

    private InputStream lookupClass() throws IOException {
	InputStream is = null;
	ClassPath classPath = ClassPath.getClassPath(null, ClassPath.EXECUTE);
	String resName = className.getInternalName() + ".class";
	FileObject fo = classPath.findResource(resName);
	is = (fo != null)
	    ? fo.getInputStream() 
	    : getClass().getClassLoader().getResourceAsStream(resName);
        if (is == null)
            throw new FileNotFoundException(resName);
        return is;
    }
    
    private void loadClass(InputStream is) throws IOException {
        classFile = new ClassFile( is );  // will throw IOException if not accessible or invalid format

        // Override any names passed in from NetBeans, as the classfile
        // has the "true names".
        className = classFile.getName();

        ConstantPool pool = classFile.getConstantPool();
        methods = null;
        dependentClasses = null;
    }

    void scanDependencies() {
        if (dependentClasses != null || classFile == null)
            return;
        dependentClasses = new TreeSet();

	getSuperClass();
	if (parent != null) {
            addDependentClass(parent.getName());
            parent.addChild(className);
	}

        Iterator iter = classFile.getInterfaces().iterator();
        while ( iter.hasNext() ) {
            ClassName cls = (ClassName)iter.next();
            addDependentClass(cls);
        }
        
        scanFields(classFile.getVariables().iterator());
        scanFields(classFile.getMethods().iterator());
        scanByteCode();
        setClientStatus();
    }
    
    private void scanFields(Iterator iter) {
        while (iter.hasNext()) {
            Field f = (Field)iter.next();
            Iterator fieldIter = getClassesFromSignature(f.getDescriptor());
            while (fieldIter.hasNext()) {
                addDependentClass((ClassName)fieldIter.next());
            }
        }
    }
    
    // Extract all class names from a VM type signature.
    private static Iterator getClassesFromSignature(String type) {
        Set set = new HashSet();
        int index = 0;
        int n = type.length();
        while (index < n) {
            int i = type.indexOf('L', index);
            if (i < 0)  // not found
                break;
            int i2 = type.indexOf(';', i);
            String cls = type.substring(i + 1, i2);
            set.add(ClassName.getClassName(cls));
            index = i2 + 1;
        }
        return set.iterator();
    }
    
    private void scanByteCode() {
        for (Iterator iter = getMethods().iterator(); iter.hasNext();) {
            MethodMetrics mm = (MethodMetrics)iter.next();
            Code code = mm.getCode();
            if (code != null) {  // true for abstract methods
                Iterator iter2 = mm.getClassReferences();
                while (iter2.hasNext()) {
                    ClassName cn = (ClassName)iter2.next();
                    addDependentClass(cn);
                }
            }
        }
    }
    
    /* Notify other classes that this class is a client. */
    private void setClientStatus() {
        Iterator iter = dependentClasses.iterator();
        while (iter.hasNext()) {
            try {
                ClassName clsName = (ClassName)iter.next();
                if (clsName.equals(className))
                    continue;
                ClassMetrics cm = getClassMetrics(clsName, false);

                // Add this cm to other cm's client list.
                if (cm != null)
                    cm.addClientClass(className);
            } catch (IOException e) {
                if (debug)
                    System.err.println(e.toString());
            }
        }
    }
    
    public Metric[] getMetrics() {
        return metrics;
    }

    Metric getMetric(int index) {
        return metrics[index];
    }

    /**
     * Returns the FileObject associated with this metrics set.
     *
     * @return the associated FileObject, or null if not known.
     */
    public FileObject getFileObject() {
	return fileObject;
    }

    private void setFileObject(FileObject fo) {
        fileObject = fo;
    }

    private void resetMetrics(FileObject fo) throws IOException {
        // classfile has changed, reload it.
        loadClass(fo.getInputStream());
        setFileObject(fo);

        int n = metrics.length;
        for (int i = 0; i < n; i++)
            metrics[i].resetMetric();

        for (Iterator iter = getMethods().iterator(); iter.hasNext();) {
            MethodMetrics mm = (MethodMetrics)iter.next();
	    mm.resetWarningLight();
	}
    }

    ApprovalsFile getApprovalsFile() {
	if (approvalsFile == null) {
	    if (fileObject == null)
		return null;
	    approvalsFile = 
		ApprovalsFile.getApprovalsFile(fileObject.getParent());
	}
	return approvalsFile;
    }
    private ApprovalsFile approvalsFile = null;

    public void addApproval(ApprovalsFile appFile, MetricValue mv) {
	approvalsFile = appFile;
	String simpleName = className.getSimpleName();
	ClassApprovals app = appFile.get(simpleName);
	if (app == null)
	    app = new ClassApprovals(simpleName);
	app.addApproval(mv);
	appFile.add(app); // will overwrite any old entry
    }

    public int getMetricValue(Metric m) {
	return m.getMetricValue().intValue();
    }

    /**
     * Returns the warning level from the approvals file, if any.
     */
    public int getWarningLevel(Metric m) {
        // Check whether an approved warning level has been stored.
        ApprovalsFile apFile = null;
	apFile = getApprovalsFile();
        if (apFile != null) {
            ClassApprovals aps = apFile.get(className.getSimpleName());
            if (aps != null) {
		// There's an approved metric value.  Return whether
		// the current metric has crept past it or not.
		int currentMetric = m.getMetricValue().intValue();
		int approvalLevel = aps.getApprovalLevel(m);
		if (approvalLevel > -1)
		    // if an approval exists...
		    return (currentMetric > approvalLevel) ? 
			Metric.METRIC_FAIL : Metric.METRIC_OKAY;
	    }
        }

        // Use system property warning level
        return m.getWarningLevel();
    }

    public int getWarningLevel() {
        int warnLevel = Metric.METRIC_OKAY;
        int n = metrics.length;
        for (int i = 0; i < n; i++) {
            Metric m = metrics[i];
            if (!m.needsOtherClasses())
                warnLevel = Math.max(warnLevel, getWarningLevel(m));
        }
	return warnLevel;
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
	firePropertyChange(LIGHT_PROP, null, getWarningLight());
    }

    public ClassName getName() {
        return className;
    }

    public String getNodeName() {
	return className.getSimpleName();
    }

    public int getDependencyCount() {
        scanDependencies();
        return dependentClasses.size();
    }
    
    public Set getDependentClasses() {
        scanDependencies();
        return Collections.unmodifiableSet(dependentClasses);
    }
    
    public int getClientCount() {
        scanDependencies();
        return clientClasses.size();
    }
    
    public Set getClientClasses() {
        scanDependencies();
        return Collections.unmodifiableSet(clientClasses);
    }
    
    public boolean hasDependency(ClassName dependentClass) {
        scanDependencies();
        return dependentClasses.contains(dependentClass);
    }
    
    void addClientClass(ClassName clientClass) {
        scanDependencies();
        if (isClass(clientClass))
            clientClasses.add(clientClass);
    }
    
    void addChild(ClassName childName) {
        if (isClass(childName)) {
	    // recurse up hierarchy tree so each superclass includes this class
	    ClassMetrics parent = getSuperClass();
            if (parent != null)
                parent.addChild(childName);

            childClasses.add(childName);
        }
    }
    
    public int numberOfChildClasses() {
        scanDependencies();
        return childClasses.size();
    }
    
    public Set getChildClasses() {
        scanDependencies();
        return Collections.unmodifiableSet(childClasses);
    }
    
    public Set getMethods() {
	buildMethods();
        return Collections.unmodifiableSet(methods);
    }

    private void buildMethods() {
        if (methods == null) {
            methods = new TreeSet();
            for (Iterator iter = classFile.getMethods().iterator(); 
                 iter.hasNext();) {
                Method m = (Method)iter.next();
                if (m.getCode() != null)
                    methods.add(new MethodMetrics(this, m));
            }
        }
    }

    public MethodMetrics getMethod(String name, String signature) {
	buildMethods();
	Iterator i = methods.iterator(); 
	while (i.hasNext()) {
	    MethodMetrics mm = (MethodMetrics)i.next();
	    if (mm.getInternalName().equals(name) && 
		mm.getSignature().equals(signature))
		return mm;
	}
	return null;
    }

    public void fileChanged(FileEvent fe) {
        try {
            resetMetrics(fe.getFile());
            resetWarningLight();
        } catch (IOException e) {
            System.err.println("ClassMetrics reset failed: " + e);
        }
	
        firePropertyChange(METRICS_PROP, null, null);
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
}
