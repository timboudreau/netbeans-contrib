/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.gui;

import java.beans.*;
import java.util.ResourceBundle;

public class StateNodeBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor information will be obtained from introspection.//GEN-FIRST:BeanDescriptor
    private static BeanDescriptor beanDescriptor = null;
    private static BeanDescriptor getBdescriptor(){
//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
         return beanDescriptor;     } //GEN-LAST:BeanDescriptor
    
    
    // Property identifiers //GEN-FIRST:Properties
    private static final int PROPERTY_finalState = 0;
    private static final int PROPERTY_ID = 1;
    private static final int PROPERTY_initialState = 2;
    private static final int PROPERTY_isHidden = 3;
    private static final int PROPERTY_lineStyle = 4;
    private static final int PROPERTY_name = 5;
    private static final int PROPERTY_x = 6;
    private static final int PROPERTY_y = 7;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[8];
    
        try {
            properties[PROPERTY_finalState] = new PropertyDescriptor ( "finalState", StateNode.class, "isFinalState", "setFinalState" );
            properties[PROPERTY_finalState].setDisplayName ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("LBL_finalState") );
            properties[PROPERTY_finalState].setShortDescription ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("SD_finalState") );
            properties[PROPERTY_ID] = new PropertyDescriptor ( "ID", StateNode.class, "getID", "setID" );
            properties[PROPERTY_ID].setDisplayName ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("LBL_ID") );
            properties[PROPERTY_ID].setShortDescription ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("SD_ID") );
            properties[PROPERTY_initialState] = new PropertyDescriptor ( "initialState", StateNode.class, "isInitialState", "setInitialState" );
            properties[PROPERTY_initialState].setDisplayName ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("LBL_initialState") );
            properties[PROPERTY_initialState].setShortDescription ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("SD_initialState") );
            properties[PROPERTY_isHidden] = new PropertyDescriptor ( "isHidden", StateNode.class, "isIsHidden", "setIsHidden" );
            properties[PROPERTY_isHidden].setDisplayName ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("LBL_isHidden") );
            properties[PROPERTY_isHidden].setShortDescription ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("SD_isHidden") );
            properties[PROPERTY_lineStyle] = new PropertyDescriptor ( "lineStyle", StateNode.class, "getLineStyle", "setLineStyle" );
            properties[PROPERTY_lineStyle].setDisplayName ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("LBL_lineStyle") );
            properties[PROPERTY_lineStyle].setShortDescription ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("SD_lineStyle") );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", StateNode.class, "getName", "setName" );
            properties[PROPERTY_name].setDisplayName ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("LBL_name") );
            properties[PROPERTY_name].setShortDescription ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("SD_name") );
            properties[PROPERTY_x] = new PropertyDescriptor ( "x", StateNode.class, "getX", "setX" );
            properties[PROPERTY_x].setDisplayName ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("LBL_x") );
            properties[PROPERTY_x].setShortDescription ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("SD_x") );
            properties[PROPERTY_y] = new PropertyDescriptor ( "y", StateNode.class, "getY", "setY" );
            properties[PROPERTY_y].setDisplayName ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("LBL_y") );
            properties[PROPERTY_y].setShortDescription ( ResourceBundle.getBundle("org.netbeans.modules.latex.gui.Bundle").getString("SD_y") );
        }
        catch( IntrospectionException e) {}//GEN-HEADEREND:Properties
        
        // Here you can add code for customizing the properties array.
        
        return properties;         }//GEN-LAST:Properties
    
    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_propertyChangeListener = 0;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[1];
    
            try {
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( org.netbeans.modules.latex.gui.StateNode.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" );
        }
        catch( IntrospectionException e) {}//GEN-HEADEREND:Events
        
        // Here you can add code for customizing the event sets array.
        
        return eventSets;         }//GEN-LAST:Events
    
    // Method information will be obtained from introspection.//GEN-FIRST:Methods
    private static MethodDescriptor[] methods = null;
    private static MethodDescriptor[] getMdescriptor(){//GEN-HEADEREND:Methods
        
        // Here you can add code for customizing the methods array.
        
        return methods;     } //GEN-LAST:Methods
    
    
    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx
    
    
 //GEN-FIRST:Superclass
    
    // Here you can add code for customizing the Superclass BeanInfo.
    
 //GEN-LAST:Superclass
    
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }
    
    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }
    
    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }
    
    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return  An array of MethodDescriptors describing the methods
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }
    
    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }
    
    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean.
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }
}

