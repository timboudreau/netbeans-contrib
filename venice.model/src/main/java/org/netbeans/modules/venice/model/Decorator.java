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

package org.netbeans.modules.venice.model;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
/**
 * Provides information about objects in a model.
 *
 * @author Tim Boudreau
 */
public interface Decorator {
    /** Constant for the open action */
    public static final String ACTION_OPEN = "open"; //NOI18N

    /** Property constant for display name changes */
    public static final String PROP_DISPLAY_NAME = "displayName"; //NOI18N
    
    /** Property constant for validity changes */
    public static final String PROP_VALID = "valid"; //NOI18N
    
    /** Property constant for children changes */
    public static final String PROP_CHILDREN = "children"; //NOI18N
    
    /** Children kind for the default children of an object */
    public static final String CHILDREN_DEFAULT = "kids";
    
    /** Get a display name for some object in the model */
    public String getDisplayName(Object o);
    /** Get an ad hoc property of an object.  What keys are passed are a
        contract between the provider of the model and the provider of
        the visualization */
    public Object getProperty (String key, Object o);
    /** Get an action that may be performed against the passed model object.
        The only predefined key is ACTION_OPEN, which should open the 
        model object in the editor, whatever that means.  Other actions
        may be provided;  this is a contract between the provider of the
        model and the provider of the visualization. */
    public Action getAction (String key, Object o);
    /** Determine if a model object is valid - if it is not, all references
        to it should be released and the children of the first valid parent
        recalculated */
    public boolean isValid(Object o);
    /** Add a property change listener to a specific object, for changes in 
        values provided by ObjectDecorator.  All changes should be fired on
        the AWT event thread.
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl, Object o);
    /** Remove a property change listener */
    public void removePropertyChangeListener(PropertyChangeListener pcl, Object o);
    /** Get an object representing the children of a model object.  Equivalent
     * to calling <code>getChildren (o, CHILDREN_DEFAULT, calculate)</code>.
     * @param o The model object for which children are being requested
     * @param calculate If false, return null if the children have not been
     *        calculated already;  if true, return a handle that will 
     *        calculate them on request.
     */
    public ChildrenHandle getChildren (Object o, boolean calculate);
    /** Get an object representing a named set of children of a model object.
     *  Since we may be representing more than one type of child of an 
     *  object, this method allows more than one type of child list to be 
     *  provided.  Only CHILDREN_DEFAULT is predefined as a key to this
     *  method; other keys are a contract between the model and the 
     *  visualization.
     * @param o The model object for which children are being requested.
     * @param calculate If false, return null if the children have not been
     *        calculated already;  if true, return a handle that will 
     *        calculate them on request.
     */
    public ChildrenHandle getChildren (Object o, String childrenType, boolean calculate);
    
    /** Represents the children of a model object (which often will need to
        be calculated asynchronously) */
    public interface ChildrenHandle {
	/** Constant from getState() indicating that the list of child objects
	    has been calculated */
	public static final int STATE_CHILDREN_COLLECTED = 1;
	/** Constant from getState() indicating the list of child objects is
	    still being calculated */
	public static final int STATE_COLLECTING_CHILDREN = 2;
	/** Determine if collection of the children of the model object has
	    completed.  A child handle is used only once for collecting
	    children, and should be discarded after that.
	 */
	public int getState();
	/** Add a change listener for state changes in this model object. All
	 * changes should be fired on the AWT event thread.
	 */
	public void addChangeListener (ChangeListener cl);
	
	/** Remove a channge listener */
	public void removeChangeListener (ChangeListener cl);
	
	/** Get the model object this handle is fetching children for */
	public Object getModelObject();
	
	/** Get a list of child objects of the model object.  If the state
	    is STATE_COLLECTING_CHILDREN, this method will return
	    Collections.EMPTY_LIST.  */
	public List /* <Object> */ getChildren();
	
	/** Return the key passed to getChildren() to produce this 
	    ChildrenHandle */
	public String getKind();
	
	/** Handle for a precalculated list of children */
	public static class Fixed implements ChildrenHandle {
	    private final Object mdlObj;
	    private final List children;
	    private final String kind;
	    public Fixed (Object mdlObj, List children) {
		this (mdlObj, children, CHILDREN_DEFAULT);
	    }
	    
	    public Fixed (Object mdlObj, List children, String kind) {
		this.kind = kind;
		this.mdlObj = mdlObj;
		this.children = children;
	    }
	    
	    /** Create an empty children handle */
	    public Fixed (Object mdlObj, String kind) {
		this (mdlObj, Collections.EMPTY_LIST, kind);
	    }
	    
            public int getState() {
		return STATE_CHILDREN_COLLECTED;
            }

            public void addChangeListener(ChangeListener cl) {
		//do nothing
            }

            public void removeChangeListener(ChangeListener cl) {
		//do nothing
            }

            public Object getModelObject() {
		return mdlObj;
            }

            public List getChildren() {
		return children;
            }
	    
	    public String getKind() {
		return kind;
	    }
	}
	
	/** Empty children handle for default children w/ null model object */
	public static final ChildrenHandle EMPTY = new Fixed (null, 
		CHILDREN_DEFAULT);
    }
}
