/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * EventFilter.java
 *
 * Created on February 3, 2003, 12:59 PM */

package org.netbeans.modules.uidiagnostics;
import java.awt.Component;
import java.beans.*;
/**
 *
 * @author  Tim Boudreau
 */
public class EventFilter implements PropertyChangeListener {
    public static final String CONDITION_ANY = "any";
    public static final String CONDITION_NULL = "null";
    public static final String CONDITION_CLASSMATCH = "class name matches";
    public static final int TYPE_ANY=0;
    public static final int TYPE_NULL=1;
    public static final int TYPE_MATCHCLASS=2;
    public static final int TYPE_NONNULL=3;
    public static final int TYPE_TOSTRING=4;
    public static final int TYPE_SPECIFIC=5;
    
    public static final int WHICH_BOTH=0;
    public static final int WHICH_OLD=1;
    public static final int WHICH_NEW=2;
    
    /** Holds value of property stackTrace. */
    private boolean stackTrace=false;
    
    /** Utility field used by bound properties. */
    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);
    
    /** Holds value of property type. */
    private int type=TYPE_ANY;
    
    /** Holds value of property classNameFilter. */
    private String classNameFilter="";
    
    /** Holds value of property methodNameFilter. */
    private String methodNameFilter="";
    
    /** Holds value of property which. */
    private int which=WHICH_BOTH;
    
    /** Utility field used by constrained properties. */
    private java.beans.VetoableChangeSupport vetoableChangeSupport =  new java.beans.VetoableChangeSupport(this);
    
    /** Holds value of property propertyNameFilter. */
    private String propertyNameFilter="";
    
    /** Holds value of property useMethodFilter. */
    private boolean useMethodFilter=false;
    
    /** Holds value of property inverted. */
    private boolean inverted = false;
    
    /** Creates a new instance of EventFilter */
    public EventFilter() {
        addPropertyChangeListener (this);
    }
    
    String stringRep=null;
    
    /** Holds value of property stringFilter. */
    private String stringFilter=null;
    
    /** Holds value of property component. */
    private Component component=null;
    
    public String toString () {
        if (stringRep == null) buildString();
        return stringRep;
    }
    

    private void buildString () {
        //XXX use MessageFormat & bundle
        StringBuffer out=new StringBuffer();
        if (isInverted()) out.append ("INVERTED: ");
        out.append ("When ");
        String fil = getPropertyNameFilter();
        if ((fil != null) && (fil.trim().length() > 0)) {
            out.append (" the property name matches \"" + getPropertyNameFilter() + "\" and the ");
        }
        switch (getWhich()) {
            case WHICH_BOTH : out.append("old or new value ");
                              break;
            case WHICH_OLD : out.append("old value ");
                             break;
            case WHICH_NEW : out.append("new value ");
        }
        switch (getType()) {
            case TYPE_ANY : out.append ("changes ");
                            break;
            case TYPE_NULL : out.append ("equals null ");
                            break;
            case TYPE_MATCHCLASS : out.append ("class name matches \"" + getClassNameFilter() + "\"");
                            break;
            case TYPE_TOSTRING : out.append ("string representation contains \"" + (getStringFilter()==null ?  "[no text entered]" : getStringFilter()) + "\"");
                            break;
            case TYPE_SPECIFIC : out.append ("equals a specific component " + (getComponent()==null ?  "[no component chosen]" : getComponent().toString()));
                            break;
            case TYPE_NONNULL : out.append ("is non-null");
        }
        if (isUseMethodFilter()) {
            out.append (" and a method \"" + getMethodNameFilter() + "\" is on the stack");
        }
        if (isStackTrace()) out.append (" print a stack trace");
        if (isShowEvent()) out.append (" and the current awt event");
        
        
        stringRep=out.toString();
    }
    
    public boolean match (PropertyChangeEvent pce) {
        boolean result = doMatch (pce);
        if (isInverted()) result = !result;
        return result;
    }
    
    private boolean doMatch (PropertyChangeEvent pce) {
        boolean result=true;
        result = result && checkPropertyName (pce.getPropertyName());
        if (!result) return result;
        switch (getWhich()) {
            case WHICH_BOTH : result = checkValue (pce.getOldValue()) && checkValue (pce.getNewValue());
                              break;
            case WHICH_OLD : result = checkValue (pce.getOldValue());
                             break;
            case WHICH_NEW : result = checkValue (pce.getNewValue());
        }

        return result;
    }
    
    private boolean checkValue (Object value) {
        boolean result;
        switch (getType()) {
            case TYPE_ANY : result = true;
                            break;
            case TYPE_NULL : result = value==null;
                             break;
            case TYPE_NONNULL : result = value != null;
            default : result = false;
        }
        if (value != null) {
            switch (getType()) {
                case TYPE_MATCHCLASS : result = value.getClass().getName().indexOf (getClassNameFilter()) != -1;
                                       break;
                case TYPE_TOSTRING : result = value.toString().indexOf(getStringFilter()) != -1;
                                       break;
                case TYPE_SPECIFIC : result = getComponent() == value;
            }
        }
        
        if (result && isUseMethodFilter()) {
            result = result && checkMethod (value);
        } 
        return result;
    }
    
    private boolean checkMethod (Object o) {
        Exception e = new Exception();
        try {
            throw e;
        } catch (Exception e1) {
            //make it build the stack trace
        }
        StackTraceElement[] ste = e.getStackTrace();
        String toMatch = getMethodNameFilter();
        for (int i=0; i < ste.length; i++) {
            String s = ste[i].getMethodName();
            if (s.indexOf (toMatch) != -1) return true;
        }
        return false;
    }
    
    private boolean checkPropertyName (String name) {
        return name.indexOf (getPropertyNameFilter()) != -1;
    }
    
    /** Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     *
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }    
    
    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     *
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
    
    /** Getter for property stackTrace.
     * @return Value of property stackTrace.
     *
     */
    public boolean isStackTrace() {
        return this.stackTrace;
    }
    
    /** Setter for property stackTrace.
     * @param stackTrace New value of property stackTrace.
     *
     */
    public void setStackTrace(boolean stackTrace) {
        boolean oldStackTrace = this.stackTrace;
        this.stackTrace = stackTrace;
        propertyChangeSupport.firePropertyChange("stackTrace", new Boolean(oldStackTrace), new Boolean(stackTrace)); //NOI18N
    }
    
    /** Getter for property type.
     * @return Value of property type.
     *
     */
    public int getType() {
        return this.type;
    }
    
    /** Setter for property type.
     * @param type New value of property type.
     *
     */
    public void setType(int type) {
        int oldType = this.type;
        this.type = type;
        propertyChangeSupport.firePropertyChange("type", new Integer(oldType), new Integer(type)); //NOI18N
    }
    
    /** Getter for property classNameFilter.
     * @return Value of property classNameFilter.
     *
     */
    public String getClassNameFilter() {
        return this.classNameFilter;
    }
    
    /** Setter for property classNameFilter.
     * @param classNameFilter New value of property classNameFilter.
     *
     */
    public void setClassNameFilter(String classNameFilter) {
        String oldClassNameFilter = this.classNameFilter;
        this.classNameFilter = classNameFilter;
        propertyChangeSupport.firePropertyChange("classNameFilter", oldClassNameFilter, classNameFilter); //NOI18N
    }
    
    /** Getter for property methodNameFilter.
     * @return Value of property methodNameFilter.
     *
     */
    public String getMethodNameFilter() {
        return this.methodNameFilter;
    }
    
    /** Setter for property methodNameFilter.
     * @param methodNameFilter New value of property methodNameFilter.
     *
     */
    public void setMethodNameFilter(String methodNameFilter) {
        String oldMethodNameFilter = this.methodNameFilter;
        this.methodNameFilter = methodNameFilter;
        propertyChangeSupport.firePropertyChange("methodNameFilter", oldMethodNameFilter, methodNameFilter); //NOI18N
    }
    
    /** Adds a VetoableChangeListener to the listener list.
     * @param l The listener to add.
     *
     */
    public void addVetoableChangeListener(java.beans.VetoableChangeListener l) {
        vetoableChangeSupport.addVetoableChangeListener(l);
    }
    
    /** Removes a VetoableChangeListener from the listener list.
     * @param l The listener to remove.
     *
     */
    public void removeVetoableChangeListener(java.beans.VetoableChangeListener l) {
        vetoableChangeSupport.removeVetoableChangeListener(l);
    }
    
    /** Getter for property which.
     * @return Value of property which.
     *
     */
    public int getWhich() {
        return this.which;
    }
    
    /** Setter for property which.
     * @param which New value of property which.
     *
     * @throws PropertyVetoException
     *
     */
    public void setWhich(int which) throws java.beans.PropertyVetoException {
        int oldWhich = this.which;
        vetoableChangeSupport.fireVetoableChange("which", new Integer(oldWhich), new Integer(which)); //NOI18N
        this.which = which;
        propertyChangeSupport.firePropertyChange("which", new Integer(oldWhich), new Integer(which)); //NOI18N
    }
    
    /** Getter for property propertyNameFilter.
     * @return Value of property propertyNameFilter.
     *
     */
    public String getPropertyNameFilter() {
        return this.propertyNameFilter;
    }
    
    public void setShowEvent(boolean b) {
        showEvent = b;
    }
    
    public boolean isShowEvent() {
        return showEvent;
    }
    
    private boolean showEvent=false;
    
    /** Setter for property propertyNameFilter.
     * @param propertyNameFilter New value of property propertyNameFilter.
     *
     */
    public void setPropertyNameFilter(String propertyNameFilter) {
        String oldPropertyNameFilter = this.propertyNameFilter;
        this.propertyNameFilter = propertyNameFilter;
        propertyChangeSupport.firePropertyChange("propertyNameFilter", oldPropertyNameFilter, propertyNameFilter); //NOI18N
    }
    
    /** Getter for property useMethodFilter.
     * @return Value of property useMethodFilter.
     *
     */
    public boolean isUseMethodFilter() {
        return this.useMethodFilter;
    }
    
    /** Setter for property useMethodFilter.
     * @param useMethodFilter New value of property useMethodFilter.
     *
     */
    public void setUseMethodFilter(boolean useMethodFilter) {
        boolean oldUseMethodFilter = this.useMethodFilter;
        this.useMethodFilter = useMethodFilter;
        propertyChangeSupport.firePropertyChange("useMethodFilter", new Boolean(oldUseMethodFilter), new Boolean(useMethodFilter)); //NOI18N
    }
    
    /** Getter for property inverted.
     * @return Value of property inverted.
     *
     */
    public boolean isInverted() {
        return this.inverted;
    }
    
    /** Setter for property inverted.
     * @param inverted New value of property inverted.
     *
     */
    public void setInverted(boolean inverted) {
        boolean oldInverted = this.inverted;
        this.inverted = inverted;
        propertyChangeSupport.firePropertyChange("inverted", new Boolean(oldInverted), new Boolean(inverted)); //NOI18N
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        stringRep = null;
    }
    
    /** Getter for property stringFilter.
     * @return Value of property stringFilter.
     *
     */
    public String getStringFilter() {
        return this.stringFilter;
    }
    
    /** Setter for property stringFilter.
     * @param stringFilter New value of property stringFilter.
     *
     */
    public void setStringFilter(String stringFilter) {
        String oldStringFilter = this.stringFilter;
        this.stringFilter = stringFilter;
        propertyChangeSupport.firePropertyChange("stringFilter", oldStringFilter, stringFilter); //NOI18N
    }
    
    /** Getter for property component.
     * @return Value of property component.
     *
     */
    public Component getComponent() {
        return this.component;
    }
    
    /** Setter for property component.
     * @param component New value of property component.
     *
     */
    public void setComponent(Component component) {
        Component oldComponent = this.component;
        this.component = component;
        stringRep=null;
        propertyChangeSupport.firePropertyChange("component", oldComponent, component); //NOI18N
    }
    
}
