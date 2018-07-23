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
package org.netbeans.modules.a11ychecker.traverse;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import org.netbeans.modules.form.CreationFactory;
import org.netbeans.modules.form.ViewConverter;
import org.netbeans.modules.form.ViewConverter.Convert;

/**
 * Convertor for VisualReplicator
 * @author Tomas Pavek
 */
public class MyConverter implements ViewConverter { 
        public Convert convert(Object component, boolean root, boolean designRestrictions) { 
            Class compClass = component.getClass(); 
            Class convClass = null; 
            if ((RootPaneContainer.class.isAssignableFrom(compClass) 
                        && Window.class.isAssignableFrom(compClass)) 
                    || Frame.class.isAssignableFrom(compClass)) { 
                convClass = JRootPane.class; 
            } else if (Window.class.isAssignableFrom(compClass) 
                       || java.applet.Applet.class.isAssignableFrom(compClass)) { 
                convClass = Panel.class; 
            } 
            if (convClass == null) { 
                return null; // no conversion needed 
            } 
 
            try { 
                Component converted = (Component) CreationFactory.createDefaultInstance(convClass); 
                Component enclosed = null; 
 
                if (converted instanceof JRootPane) { // RootPaneContainer or Frame converted to JRootPane 
                    Container contentCont = (Container) CreationFactory.createDefaultInstance( 
                            RootPaneContainer.class.isAssignableFrom(compClass) ? JPanel.class : Panel.class); 
                    ((JRootPane)converted).setContentPane(contentCont); 
                } 
 
                return new ConvertResult(converted, enclosed); 
            } catch (Exception ex) { // some instance creation failed, very unlikely to happen 
                Logger.getLogger(MyConverter.class.getName()).log(Level.INFO, null, ex); 
                return null; 
            } 
        } 
 
        public boolean canVisualize(Class componentClass) { 
            return false; // not able to visualize non-visual components 
              // AWT menus are converted, but never used as the root in the design view 
        } 
    } 
 
    class ConvertResult implements ViewConverter.Convert { 
        private Object converted; 
        private Object enclosed; 
        ConvertResult(Object converted, Object enclosed) { 
            this.converted = converted; 
            this.enclosed = enclosed; 
        } 
        public Object getConverted() { 
            return converted; 
        } 
        public Object getEnclosed() { 
            return enclosed; 
        } 
    } 

