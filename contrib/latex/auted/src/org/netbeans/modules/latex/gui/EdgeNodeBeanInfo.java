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

package org.netbeans.modules.latex.gui;

import java.beans.*;

/**
 * @author Jan Lahoda
 */
public class EdgeNodeBeanInfo extends SimpleBeanInfo {

    // Bean descriptor //GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( EdgeNode.class , null );//GEN-HEADEREND:BeanDescriptor

        // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;         }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers //GEN-FIRST:Properties
    private static final int PROPERTY_border = 0;
    private static final int PROPERTY_ID = 1;
    private static final int PROPERTY_labelPosition = 2;
    private static final int PROPERTY_lineStyle = 3;
    private static final int PROPERTY_name = 4;
    private static final int PROPERTY_orientation = 5;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[6];
    
        try {
            properties[PROPERTY_border] = new PropertyDescriptor ( "border", EdgeNode.class, "isBorder", "setBorder" );
            properties[PROPERTY_ID] = new PropertyDescriptor ( "ID", EdgeNode.class, "getID", "setID" );
            properties[PROPERTY_labelPosition] = new PropertyDescriptor ( "labelPosition", EdgeNode.class, "getLabelPosition", "setLabelPosition" );
            properties[PROPERTY_lineStyle] = new PropertyDescriptor ( "lineStyle", EdgeNode.class, "getLineStyle", "setLineStyle" );
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", EdgeNode.class, "getName", "setName" );
            properties[PROPERTY_orientation] = new PropertyDescriptor ( "orientation", EdgeNode.class, "getOrientation", "setOrientation" );
            properties[PROPERTY_orientation].setPropertyEditorClass ( org.netbeans.modules.latex.gui.EdgeNode.OrientationPropertyEditor.class );
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
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( org.netbeans.modules.latex.gui.EdgeNode.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" );
        }
        catch( IntrospectionException e) {}//GEN-HEADEREND:Events
        
        // Here you can add code for customizing the event sets array.
        
        return eventSets;         }//GEN-LAST:Events
    
    // Method identifiers //GEN-FIRST:Methods
    private static final int METHOD_addPropertyChangeListener0 = 0;
    private static final int METHOD_distance1 = 1;
    private static final int METHOD_draw2 = 2;
    private static final int METHOD_getPoint3 = 3;
    private static final int METHOD_outputVaucansonSource4 = 4;
    private static final int METHOD_remove5 = 5;
    private static final int METHOD_removePropertyChangeListener6 = 6;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[7];
    
        try {
            methods[METHOD_addPropertyChangeListener0] = new MethodDescriptor ( org.netbeans.modules.latex.gui.EdgeNode.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class}));
            methods[METHOD_addPropertyChangeListener0].setDisplayName ( "" );
            methods[METHOD_distance1] = new MethodDescriptor ( org.netbeans.modules.latex.gui.EdgeNode.class.getMethod("distance", new Class[] {java.awt.Point.class}));
            methods[METHOD_distance1].setDisplayName ( "" );
            methods[METHOD_draw2] = new MethodDescriptor ( org.netbeans.modules.latex.gui.EdgeNode.class.getMethod("draw", new Class[] {java.awt.Graphics2D.class}));
            methods[METHOD_draw2].setDisplayName ( "" );
            methods[METHOD_getPoint3] = new MethodDescriptor ( org.netbeans.modules.latex.gui.EdgeNode.class.getMethod("getPoint", new Class[] {Double.TYPE}));
            methods[METHOD_getPoint3].setDisplayName ( "" );
            methods[METHOD_outputVaucansonSource4] = new MethodDescriptor ( org.netbeans.modules.latex.gui.EdgeNode.class.getMethod("outputVaucansonSource", new Class[] {java.io.PrintWriter.class}));
            methods[METHOD_outputVaucansonSource4].setDisplayName ( "" );
            methods[METHOD_remove5] = new MethodDescriptor ( org.netbeans.modules.latex.gui.EdgeNode.class.getMethod("remove", new Class[] {}));
            methods[METHOD_remove5].setDisplayName ( "" );
            methods[METHOD_removePropertyChangeListener6] = new MethodDescriptor ( org.netbeans.modules.latex.gui.EdgeNode.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class}));
            methods[METHOD_removePropertyChangeListener6].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods
        
        // Here you can add code for customizing the methods array.
        
        return methods;         }//GEN-LAST:Methods
    
    
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

