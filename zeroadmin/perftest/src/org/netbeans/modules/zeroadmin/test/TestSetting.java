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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.zeroadmin.test;

import java.beans.*;

/**
 * Test setting containing configurable number of properties.
 * @author David Strupl
 */
public class TestSetting {

    private final static String PROP_TEST = "test"; //NOI18N

    private PropertyChangeSupport pcSupport;

    private String test = "value";

    /** Number of properties */
    private int size = 1;

    /** Creates a new instance of TestSetting */
    public TestSetting() {
    }

    //  property change event support
    public void addPropertyChangeListener(PropertyChangeListener l) {
        getPCSupport().addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        getPCSupport().removePropertyChangeListener(l);
    }
    
    // getters/setters
    public String getTest() {
        return test;
    }
    
    public void setTest(String test) {
        String old = this.test;
        this.test = test;
        getPCSupport().firePropertyChange(PROP_TEST, old, test);
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    private PropertyChangeSupport getPCSupport() {
        if (pcSupport == null) {
            pcSupport = new PropertyChangeSupport(this);
        }
        return pcSupport;
    }
    
    // readProperties/writeProperties called by XMLPropertiesConvertor
    private void readProperties(java.util.Properties p) {
        test = p.getProperty(PROP_TEST);
        // the rest of properties is ignored for now
    }
    
    private void writeProperties(java.util.Properties p) {
        p.setProperty(PROP_TEST, test);
        for (int i = 0; i < this.size; i++) {
            p.setProperty(PROP_TEST+i, test);
        }
    }
}
