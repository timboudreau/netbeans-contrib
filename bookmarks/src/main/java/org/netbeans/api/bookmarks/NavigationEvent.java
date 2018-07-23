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

import org.openide.windows.TopComponent;

/**
 * NavigationEvent is used by the NavigationService to
 * capture the state of a TopComponent. The NavigationService
 * is able to navigate backward/forward using a call to
 * restoreState on individual NavigationEvents. The default
 * implementation of the NavigationService does not persist
 * the events so this class does not have to be persistent.
 * WARNING: the persistence requirement on those classes
 * can be changed in future releases.
 * @author David Strupl
 */
public interface NavigationEvent {
    /**
     * When the user invokes backward/forward navigation this
     * method is called to bring the TopComponents to the remembered
     * state.
     * @returns true if the state of the TopComponent was successfully restored
     *   or false if the state cannot be changed
     */
    public boolean restoreState();
    
    /**
     * Each NavigationEvent is bound to one TopComponent. The
     * value returned by this method should not change between
     * successive invocations of getTopComponent (this property
     * of the event should be immutable). 
     * @returns the top component this event belongs to
     */
    public TopComponent getTopComponent();
}
