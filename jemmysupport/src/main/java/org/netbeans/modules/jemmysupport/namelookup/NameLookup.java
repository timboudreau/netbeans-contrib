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

package org.netbeans.modules.jemmysupport.namelookup;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.*;
import org.netbeans.jemmy.operators.Operator;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExClipboard;

/** Class which installs mouse AWT Event listener and every time mouse is
 * moved over a component, component's name can be obtained from this class.
 * NameLookup works as a bean, so other properties are available like class name
 * or jemmy operator.
 * 
 * @author Jiri.Skrivanek@sun.com 
 */
public class NameLookup extends Object implements java.io.Serializable, AWTEventListener {
    
    private static final String PROP_SAMPLE_PROPERTY = "SampleProperty";
    /** Identification of component property. */
    public static final String PROP_COMPONENT = "component";
    
    private PropertyChangeSupport propertySupport;
    
    /**
     * Holds value of property component.
     */
    private Component component;
    
    /**
     * Holds value of property operator.
     */
    private Operator operator;
    
    /**
     * Holds value of property showFullName.
     */
    private boolean showFullName;
  
    /** Create instance of this class. */
    public NameLookup() {
        propertySupport = new PropertyChangeSupport(this);
    }
    
    /** Add this class as AWT Event listener to default Toolkit. */
    public void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK|AWTEvent.KEY_EVENT_MASK);
    }
    
    /** Remove this class as AWT Event listener from default Toolkit. */
    public void stop() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
    }
    
    /**
     * Calls {@link #stop} to be sure that AWT Event listener was removed
     * from default toolkit.
     * @throws Throwable the exception raised by this method
     */
    public void finalize() throws Throwable {
        super.finalize();
        stop();
    }
    
    /**
     * Add a PropertyChangeListener to the listener list.
     * @param listener the listener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    /**
     * Remove a PropertyChangeListener from the listener list.
     * @param listener the listener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Getter for property component.
     * @return Value of property component.
     */
    public Component getComponent() {
        return this.component;
    }
    
    /**
     * Setter for property component.
     * @param component New value of property component.
     */
    public void setComponent(Component component) {
        Component oldValue = this.component;
        this.component = component;
        propertySupport.firePropertyChange(PROP_COMPONENT, oldValue, component);
    }
    
    /** Returns jemmy operator for component.
     * @return jemmy operator instance
     */
    public Operator getOperator() {
        return Operator.createOperator(component);
    }
    
    /** Returns creation code of jemmy operator for component. If {@link #getName}
     * returns null this method also returns null.
     * @return creation code of jemmy operator for component (e.g. 
     * new JButtonOperator(contOperator, new NameComponentChooser("myName"));)
     * if {@link #getName} is not null, otherwise returns null
     */
    public String getOperatorConstructor() {
        if(getName() != null) {
            String className = getOperator().getClass().getName().replaceAll(".*[.]", "");
            return "new "+className+"(contOperator, new NameComponentChooser(\""+getName()+"\"));";
        } else {
            return null;
        }
    }
    
    /** Returns name of the component obtained by method java.awtComponent.getName().
     * @return name of component from component.getName() method
     */
    public String getName() {
        return component.getName();
    }
    
    /** Returns name of class which is represented by the component.
     * @return name of class for component (component.getClass().getName()).
     * If {@link #showFullName} is true it returns fully qualified clas name,
     * otherwise it returns name without packages.
     */
    public String getClassName() {
        String clazz = component.getClass().getName();
        if(!showFullName) {
            int i = clazz.lastIndexOf('.');
            clazz = clazz.substring(i + 1).replace('$', '.');
        }
        return clazz;
    }
    
    /**
     * If MOUSE_ENTERED event is dispatched it sets component on which this
     * event happens. It also scan CTRL+F10 KEY_RELEASED event and if it occurs
     * it puts results of {@link #getOperatorConstructor} to clipboard.
     * @param event dispatched event
     */
    public void eventDispatched(java.awt.AWTEvent event) {
        if (event.getID() == MouseEvent.MOUSE_ENTERED) {
            MouseEvent ev = (MouseEvent)event;
            setComponent(ev.getComponent());
        } else if ((event instanceof KeyEvent) && (event.getID() == KeyEvent.KEY_RELEASED) &&
                   (((KeyEvent)event).getKeyCode() == KeyEvent.VK_F10) && ((((KeyEvent)event).getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            ExClipboard clp = (ExClipboard)Lookup.getDefault().lookup(ExClipboard.class);
            clp.setContents(new StringSelection(this.getOperatorConstructor()), null);
        }
    }
    
    /**
     * Getter for property showFullName.
     * @return Value of property showFullName.
     */
    public boolean isShowFullName() {
        return this.showFullName;
    }
    
    /**
     * Setter for property showFullName.
     * @param showFullName New value of property showFullName.
     */
    public void setShowFullName(boolean showFullName) {
        this.showFullName = showFullName;
    }
    
}
