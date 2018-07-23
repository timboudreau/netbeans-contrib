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
package org.netbeans.api.bookmarks;

import java.beans.PropertyChangeListener;
import org.openide.util.actions.Presenter;

/**
 * Bookmark is somewhat similar to Action. It can be invoked and must
 * have menu and toolbar representation. Integral part of the contract
 * for objects implementing this interface is following persistence
 * requirement: object implementing this interface that will be used
 * with BookmarkService must support default persistence mechanism of
 * the NetBeans platform. It means it either has to be Serializable
 * or has to provide special convertor (properlyregistered in the system).
 * @author David Strupl
 */
public interface Bookmark extends Presenter.Menu, Presenter.Toolbar {
        
    /** The name identifes the bookmark. It does not have to be unique.
     * The name can be used in the visual representation.
     * @returns name of the bookmark
     */
    public String getName();
    
    /**
     * The name identifes the bookmark. It does not have to be unique.
     * The name can be used in the visual representation. This setter
     * will be called e.g. when the user will try to rename the
     * stored bookmark.
     */
    public void setName(String newName);
    
    /**
     * Main action method called when the user selects the bookmark.
     * This method is called after the user
     * selects the bookmark from the menu or toolbar. So calling this method
     * is contained in the default implementation of the menu and toolbar
     * presenters for the bookmark.
     */
    public void invoke();
    
    /**
     * The bookmark should fire property change when the the internal state
     * (e.g. name) is changed.
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl);
    
    /**
     * The bookmark should fire property change when the the internal state
     * (e.g. name) is changed.
     */
    public void removePropertyChangeListener(PropertyChangeListener pcl);
    
    /**
     * This method is used to fire the PropertyChangeEvents.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue);
}
