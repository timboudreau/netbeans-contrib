/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.modules.loaders.clazz;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.lang.ref.SoftReference;

import org.openide.util.Task;
import org.openide.src.*;

/** The implementation of source element for class objects.
* This class is final only for performance reasons,
* can be happily unfinaled if desired.
*
* @author Dafe Simonek, Jan Jancura
*/
public final class SourceElementImpl extends MemberElementImpl
                                     implements SourceElement.Impl {

  /** Empty array of imports - constant to return fro getImports() */
  static final Import[] EMPTY_IMPORTS = new Import[0];

  /* Soft reference to the class element */
  private SoftReference topClass;

  /** Soft ref to the map holding all inners */
  private SoftReference allClasses;

  /** The identifier of the package of the class data */
  private Identifier packg;

  /** Creates object with asociated class.
  */
  public SourceElementImpl (final Class data) {
    super(data);
  }

  /** Not supported. Throws SourceException.
  */
  public void setPackage (Identifier id) throws SourceException {
    throw new SourceException();
  }

  /** @return The package of class which we are representing.
  */
  public Identifier getPackage () {
    if (packg == null)
      packg = Identifier.create(((Class)data).getPackage().getName());
    return packg;
  }

  /** @return always returns empty array
  */
  public Import[] getImports () {
    return EMPTY_IMPORTS;
  }

  /** Not supported. Throws SourceException.
  */
  public void changeImports (Import[] elems, int action) throws SourceException {
    throw new SourceException();
  }

  /** Not supported. Throws SourceException.
  */
  public void changeClasses (ClassElement[] elems, int action) throws SourceException {
    throw new SourceException();
  }

  /** Always returns only one class element which belongs to the
  * class data we were given in constructor.
  */
  public ClassElement[] getClasses () {
    return new ClassElement[] { getClassElement() };
  }

  /** Finds an inner class with given name.
  * @param name the name to look for
  * @return the element or null if such class does not exist
  */
  public ClassElement getClass (Identifier name) {
    return (ClassElement)getAllClassesMap ().get(name);
  }

  /** @return Top level class which we are asociated with
  * and all its innerclasses and innerinterfaces.
  */
  public ClassElement[] getAllClasses () {
    return (ClassElement[])getAllClassesMap ().values().toArray (new ClassElement[0]);
  }

  /** @return Always returns STATUS_OK, 'cause we always have the class...
  */
  public int getStatus () {
    return SourceElement.STATUS_OK;
  }

  /** Returns empty task, because we don't need any preparation.
  */
  public Task prepare () {
    return Task.EMPTY;
  }

  /************* utility methods *********/

  /** Returns class element for asociated class data.
  * Care must be taken, 'cause we are playing with soft reference.
  */
  private ClassElement getClassElement () {
    ClassElement result =
      (topClass == null) ? null : (ClassElement)topClass.get();
    if (result == null) {
      result = new ClassElement(
                 new ClassElementImpl((Class)data), (SourceElement)element);
      topClass = new SoftReference(result);
    }
    return result;
  }

  /** Returns map with all innerclasses.
  * @return map with all innerclasses.
  */
  private Map getAllClassesMap () {
    Map allClassesMap = (allClasses == null) ? null : (Map)allClasses.get();
    if (allClassesMap == null) {
      // soft ref null, we must recreate
      allClassesMap = createClassesMap();
      // remember it, please ...
      allClasses = new SoftReference(allClassesMap);
    }
    return allClassesMap;
  }

  /** Recursively creates the map of all classes.
  * The entries in the map are built from
  * identifier - class element pairs.
  */
  private Map createClassesMap () {
    Map result = new HashMap(15);
    addClassElement(result, getClassElement());
    return result;
  }

  /** Adds given class element to the output map and
  * recurses on its inner classes and interfaces.
  */
  private void addClassElement (Map map, final ClassElement outer) {
    map.put(outer.getName(), outer);
    // recurse on inners
    ClassElement[] inners = null;
    try {
      inners = outer.getClasses();
    } catch (Throwable exc) {
      // rethrow only ThreadDeath, ignore otherwise
      if (exc instanceof ThreadDeath)
        throw (ThreadDeath)exc;
      return;
    }
    for (int i = 0; i < inners.length; i++) {
      addClassElement(map, inners[i]);
    }
  }

  /** Lock the underlaing document to have exclusive access to it and could make changes
  * on this SourceElement.
  *
  * @param run the action to run
  */
  public void runAtomic (Runnable run) {
    run.run();
  }

  /** Executes given runnable in "user mode" does not allowing any modifications
  * to parts of text marked as guarded. The actions should be run as "atomic" so
  * either happen all at once or none at all (if a guarded block should be modified).
  *
  * @param run the action to run
  */
  public void runAtomicAsUser (Runnable run) {
    run.run();
  }
  
  public Object readResolve() {
    return new SourceElement(this);
  }
}

/*
* Log
*  7    src-jtulach1.6         7/8/99   Petr Hamernik   runAtomic simple 
*       implementation
*  6    src-jtulach1.5         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  5    src-jtulach1.4         4/1/99   Jan Jancura     Object browser support
*  4    src-jtulach1.3         3/26/99  David Simonek   properties, actions 
*       completed, more robust now
*  3    src-jtulach1.2         2/17/99  Petr Hamernik   serialization changed.
*  2    src-jtulach1.1         2/11/99  David Simonek   
*  1    src-jtulach1.0         1/29/99  David Simonek   
* $
*/
