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
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
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
package org.netbeans.api.eview;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Data object representing data edited by the user in a panel
 * created by <code>ExtensibleView.createExtensiblePanel(String)</code>.
 * You can obtain an instance of this object by calling
 * <code>ExtensibleView.getPanelData(JPanel)</code>.
 *
 * @author David Strupl
 */
public interface PanelData {

    /**
     * Map containing current values edited by the user. Calling this method
     * can take rather long time since all of the GUI controls arequeried
     * for the values. 
     */
    public Map/*<String, Object>*/ getValues();
    
    /**
     * Set a value in the control. The value gets propagated to the GUI
     * controls.
     */
    public void setValue(String key, Object value);
    
    /**
     * Returns true if the user has modified the data somehow.
     */
    public boolean isModified();
    
    /** Clears the modification flag. If nothing is changed by the user
     * between calling this method and isModified - isModified will return
     * false.
     */
    public void clearModified();
    
    /** Allows listening on changes in the data edited by the user. */
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    /** Allows listening on changes in the data edited by the user. */
    public void removePropertyChangeListener(PropertyChangeListener l);
}
